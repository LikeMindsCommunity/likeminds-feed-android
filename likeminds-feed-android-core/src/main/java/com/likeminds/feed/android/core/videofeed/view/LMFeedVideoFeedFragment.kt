package com.likeminds.feed.android.core.videofeed.view

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.util.containsKey
import androidx.core.view.get
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.cache.CacheWriter
import com.likeminds.feed.android.core.LMFeedCoreApplication
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.databinding.LmFeedFragmentVideoFeedBinding
import com.likeminds.feed.android.core.databinding.LmFeedItemPostVideoFeedBinding
import com.likeminds.feed.android.core.delete.model.DELETE_TYPE_POST
import com.likeminds.feed.android.core.delete.model.LMFeedDeleteExtras
import com.likeminds.feed.android.core.delete.view.LMFeedAdminDeleteDialogFragment
import com.likeminds.feed.android.core.delete.view.LMFeedSelfDeleteDialogFragment
import com.likeminds.feed.android.core.post.viewmodel.LMFeedHelperViewModel
import com.likeminds.feed.android.core.post.viewmodel.LMFeedPostViewModel.ErrorMessageEvent.*
import com.likeminds.feed.android.core.postmenu.model.*
import com.likeminds.feed.android.core.postmenu.view.LMFeedPostMenuBottomSheetFragment
import com.likeminds.feed.android.core.postmenu.view.LMFeedPostMenuBottomSheetListener
import com.likeminds.feed.android.core.report.model.LMFeedReportExtras
import com.likeminds.feed.android.core.report.model.REPORT_TYPE_POST
import com.likeminds.feed.android.core.report.view.LMFeedReportActivity
import com.likeminds.feed.android.core.report.view.LMFeedReportFragment.Companion.LM_FEED_REPORT_RESULT
import com.likeminds.feed.android.core.report.view.LMFeedReportSuccessDialogFragment
import com.likeminds.feed.android.core.socialfeed.adapter.LMFeedPostAdapterListener
import com.likeminds.feed.android.core.socialfeed.model.LMFeedPostViewData
import com.likeminds.feed.android.core.socialfeed.util.LMFeedPostBinderUtils
import com.likeminds.feed.android.core.ui.base.styles.LMFeedIconStyle
import com.likeminds.feed.android.core.ui.base.styles.LMFeedTextStyle
import com.likeminds.feed.android.core.ui.theme.LMFeedAppearance
import com.likeminds.feed.android.core.ui.widgets.post.postmedia.view.LMFeedPostVerticalVideoMediaView
import com.likeminds.feed.android.core.utils.*
import com.likeminds.feed.android.core.utils.LMFeedValueUtils.pluralizeOrCapitalize
import com.likeminds.feed.android.core.utils.analytics.LMFeedAnalytics
import com.likeminds.feed.android.core.utils.base.LMFeedDataBoundViewHolder
import com.likeminds.feed.android.core.utils.coroutine.observeInLifecycle
import com.likeminds.feed.android.core.utils.feed.*
import com.likeminds.feed.android.core.utils.feed.LMFeedType.PERSONALISED_FEED
import com.likeminds.feed.android.core.utils.feed.LMFeedType.UNIVERSAL_FEED
import com.likeminds.feed.android.core.utils.pluralize.model.LMFeedWordAction
import com.likeminds.feed.android.core.utils.user.LMFeedUserMetaData
import com.likeminds.feed.android.core.utils.user.LMFeedUserPreferences
import com.likeminds.feed.android.core.utils.video.*
import com.likeminds.feed.android.core.videofeed.adapter.LMFeedVideoFeedAdapter
import com.likeminds.feed.android.core.videofeed.model.*
import com.likeminds.feed.android.core.videofeed.viewmodel.LMFeedVideoFeedViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.onEach

open class LMFeedVideoFeedFragment(
    private val feedType: LMFeedType,
    private val config: LMFeedVideoFeedConfig?,
    private val props: LMFeedVideoFeedProps?
) : LMFeedBaseThemeFragment(),
    LMFeedPostAdapterListener,
    LMFeedPostMenuBottomSheetListener,
    LMFeedVideoPlayerListener {

    lateinit var binding: LmFeedFragmentVideoFeedBinding
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout

    lateinit var videoFeedAdapter: LMFeedVideoFeedAdapter

    private val videoFeedViewModel: LMFeedVideoFeedViewModel by viewModels()

    private val postVideoPreviewAutoPlayHelper by lazy {
        LMFeedPostVideoPreviewAutoPlayHelper.getInstance()
    }

    private val userPreferences by lazy {
        LMFeedUserPreferences(requireContext())
    }

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    companion object {
        private const val VIDEO_PRELOAD_THRESHOLD = 5
        private const val CACHE_SIZE_EACH_VIDEO = 50 * 1024 * 1024L // 50 MB
        private const val PRECACHE_VIDEO_COUNT = 2 //we will precache 2 videos from current position

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        private const val POST_NOTIFICATIONS = Manifest.permission.POST_NOTIFICATIONS

        @JvmStatic
        fun getInstance(
            feedType: LMFeedType = UNIVERSAL_FEED,
            config: LMFeedVideoFeedConfig? = null,
            props: LMFeedVideoFeedProps? = null
        ): LMFeedVideoFeedFragment {
            return LMFeedVideoFeedFragment(feedType, config, props)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LmFeedFragmentVideoFeedBinding.inflate(layoutInflater)

        setVerticalVideoPostViewStyle()
        setupVideoFeed()
        binding.apply {
            customizeVideoFeedListView(binding.vp2VideoFeed, videoFeedAdapter)
        }
        setViewPagerAdapter()

        return binding.root
    }

    private fun setVerticalVideoPostViewStyle() {
        val postViewStyle = LMFeedStyleTransformer.postViewStyle
        val postHeaderViewStyle = postViewStyle.postHeaderViewStyle
        val postActionViewStyle = postViewStyle.postActionViewStyle

        LMFeedStyleTransformer.postViewStyle = postViewStyle.toBuilder()
            .postHeaderViewStyle(
                postHeaderViewStyle.toBuilder()
                    .authorNameViewStyle(
                        postHeaderViewStyle.authorNameViewStyle.toBuilder()
                            .textColor(R.color.lm_feed_white)
                            .build()
                    )
                    .postEditedTextStyle(
                        postHeaderViewStyle.postEditedTextStyle?.toBuilder()
                            ?.textColor(R.color.lm_feed_white)
                            ?.build()
                    )
                    .timestampTextStyle(
                        postHeaderViewStyle.timestampTextStyle?.toBuilder()
                            ?.textColor(R.color.lm_feed_white)
                            ?.build()
                    )
                    .backgroundColor(android.R.color.transparent)
                    .menuIconStyle(null)
                    .pinIconStyle(null)
                    .build()
            )
            .postContentTextStyle(
                postViewStyle.postContentTextStyle.toBuilder()
                    .postTextViewStyle(
                        LMFeedTextStyle.Builder()
                            .textColor(R.color.lm_feed_white)
                            .maxLines(1)
                            .expandableCTAText("...")
                            .expandableCTAColor(R.color.lm_feed_white)
                            .build()
                    ).build()
            )
            .postActionViewStyle(
                postActionViewStyle.toBuilder()
                    .commentTextStyle(null)
                    .shareIconStyle(
                        postActionViewStyle.shareIconStyle?.toBuilder()
                            ?.inActiveSrc(R.drawable.lm_feed_ic_share_white)
                            ?.build()
                    )
                    .likeIconStyle(
                        postActionViewStyle.likeIconStyle.toBuilder()
                            .inActiveSrc(R.drawable.lm_feed_ic_like_white)
                            .build()
                    )
                    .likeTextStyle(
                        postActionViewStyle.likeTextStyle?.toBuilder()
                            ?.textColor(R.color.lm_feed_white)
                            ?.build()
                    )
                    .menuIconStyle(
                        LMFeedIconStyle.Builder()
                            .inActiveSrc(R.drawable.lm_feed_ic_overflow_menu_white)
                            .build()
                    )
                    .build()
            )
            .build()
    }

    private fun setupVideoFeed() {
        binding.vp2VideoFeed.apply {
            for (i in 0 until childCount) {
                if (getChildAt(i) is RecyclerView) {
                    val recyclerView = getChildAt(i) as RecyclerView
                    val itemAnimator = recyclerView.itemAnimator
                    if (itemAnimator != null && itemAnimator is SimpleItemAnimator) {
                        itemAnimator.supportsChangeAnimations = false
                    }
                }
            }

            registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    //if the current item is the last item then show Caught up layout
                    if (position == videoFeedAdapter.itemCount - 1
                        && (videoFeedAdapter.items().lastOrNull() !is LMFeedCaughtUpViewData)
                    ) {
                        videoFeedAdapter.add(LMFeedCaughtUpViewData.Builder().build())
                    }

                    //for sending no more reels view event
                    val item = videoFeedAdapter.items()[position]
                    if (item is LMFeedCaughtUpViewData) {
                        sendNoMoreReelsShownEvent()
                    }

                    if (feedType == PERSONALISED_FEED) {
                        // add post in static post seen
                        if (item is LMFeedPostViewData) {
                            LMFeedPostSeenUtil.insertSeenPost(item, System.currentTimeMillis())
                        }
                    }

                    //plays the video in the view pager
                    playVideoInViewPager(position)
                }

                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)

                    val size: Int = videoFeedAdapter.itemCount

                    // Call API if the [VIDEO_PRELOAD_THRESHOLD] is reached
                    if (currentItem > 0 && currentItem >= size - VIDEO_PRELOAD_THRESHOLD) {
                        if (size > videoFeedViewModel.previousTotal && !videoFeedViewModel.postsFinished) {
                            videoFeedViewModel.previousTotal = size
                            fetchData()
                        }
                    }
                }
            })

            videoFeedAdapter = LMFeedVideoFeedAdapter(this@LMFeedVideoFeedFragment)
        }
    }

    protected open fun customizeVideoFeedListView(
        vp2VideoFeed: ViewPager2,
        videoFeedAdapter: LMFeedVideoFeedAdapter
    ) {
        //customize video feed view here
    }

    //sets adapter to the view pager
    private fun setViewPagerAdapter() {
        binding.vp2VideoFeed.adapter = videoFeedAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (videoFeedViewModel.adapterItems.isEmpty()) {
            fetchData()
        } else {
            videoFeedAdapter.replace(videoFeedViewModel.adapterItems.toList())
            binding.vp2VideoFeed.apply {
                post {
                    setCurrentItem(videoFeedViewModel.adapterPosition, true)
                }
            }
        }

        sendExploreReelsOpenedEvent()
        initSwipeRefreshLayout()
        observeResponses()
        checkForNotificationPermission()
    }

    //check for notification permission
    private fun checkForNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!NotificationManagerCompat.from(requireContext()).areNotificationsEnabled()) {
                if (activity?.checkSelfPermission(POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    notificationPermissionLauncher.launch(POST_NOTIFICATIONS)
                }
            }
        }
    }

    //calls the getFeed() function to fetch feed videos
    private fun fetchData() {
        videoFeedViewModel.apply {
            pageToCall++
            when (feedType) {
                PERSONALISED_FEED -> {
                    if (pageToCall == 1) {
                        postViewModel.getPersonalisedFeed(
                            page = pageToCall,
                            shouldReorder = true,
                            shouldRecompute = true,
                            startFeedWithPostIds = props?.startFeedWithPostIds
                        )
                        helperViewModel.postSeen()
                    } else {
                        postViewModel.getPersonalisedFeed(page = pageToCall)
                    }
                }

                UNIVERSAL_FEED -> {
                    postViewModel.getUniversalFeed(
                        page = pageToCall,
                        startFeedWithPostIds = props?.startFeedWithPostIds
                    )
                }
            }
        }
    }

    //initializes the refresh layout
    private fun initSwipeRefreshLayout() {
        mSwipeRefreshLayout = binding.swipeRefreshLayout
        mSwipeRefreshLayout.apply {
            setColorSchemeColors(
                ContextCompat.getColor(
                    requireContext(),
                    LMFeedAppearance.getButtonColor()
                )
            )

            setOnRefreshListener {
                onFeedRefreshed()
            }
        }
    }

    //processes the feed refreshed event
    protected open fun onFeedRefreshed() {
        videoFeedViewModel.apply {
            mSwipeRefreshLayout.isRefreshing = true
            pageToCall = 1
            when (feedType) {
                PERSONALISED_FEED -> {
                    postViewModel.getPersonalisedFeed(
                        page = pageToCall,
                        shouldReorder = true,
                        shouldRecompute = true,
                        startFeedWithPostIds = props?.startFeedWithPostIds
                    )
                    helperViewModel.postSeen()
                }

                UNIVERSAL_FEED -> {
                    postViewModel.getUniversalFeed(
                        page = pageToCall,
                        startFeedWithPostIds = props?.startFeedWithPostIds
                    )
                }
            }
        }
    }

    //observes live data responses
    private fun observeResponses() {
        videoFeedViewModel.postViewModel.universalFeedResponse.observe(viewLifecycleOwner) { response ->
            val page = response.first
            val posts = response.second

            // update the variable that no new posts are available now
            if (posts.isEmpty()) {
                videoFeedViewModel.postsFinished = true
            }

            //add only those posts which are supported by the adapter
            val finalPosts = posts.filter {
                (videoFeedAdapter.supportedViewBinderResolverMap.containsKey(it.viewType))
            }

            if (mSwipeRefreshLayout.isRefreshing) {
                checkPostsAndReplace(finalPosts)
                mSwipeRefreshLayout.isRefreshing = false
                return@observe
            }

            if (page == 1) {
                checkPostsAndReplace(finalPosts)
            } else {
                videoFeedAdapter.addAll(finalPosts)
            }
        }

        videoFeedViewModel.postViewModel.personalisedFeedResponse.observe(viewLifecycleOwner) { response ->
            val page = response.first
            val posts = response.second

            // update the variable that no new posts are available now
            if (posts.isEmpty()) {
                videoFeedViewModel.postsFinished = true
            }

            //add only those posts which are supported by the adapter
            val finalPosts = posts.filter {
                (videoFeedAdapter.supportedViewBinderResolverMap.containsKey(it.viewType))
            }

            if (mSwipeRefreshLayout.isRefreshing) {
                checkPostsAndReplace(finalPosts)
                mSwipeRefreshLayout.isRefreshing = false
                return@observe
            }

            if (page == 1) {
                checkPostsAndReplace(finalPosts)
            } else {
                videoFeedAdapter.addAll(finalPosts)
            }
        }

        videoFeedViewModel.postViewModel.errorMessageEventFlow.onEach { response ->
            when (response) {
                is UniversalFeed -> {
                    videoFeedViewModel.pageToCall--
                    val errorMessage = response.errorMessage
                    mSwipeRefreshLayout.isRefreshing = false
                    LMFeedViewUtils.showErrorMessageToast(requireContext(), errorMessage)
                }

                is PersonalisedFeed -> {
                    videoFeedViewModel.pageToCall--
                    val errorMessage = response.errorMessage
                    mSwipeRefreshLayout.isRefreshing = false
                    LMFeedViewUtils.showErrorMessageToast(requireContext(), errorMessage)
                }

                is PostDeletedInFeed -> {
                    val errorMessage = response.errorMessage
                    mSwipeRefreshLayout.isRefreshing = false
                    LMFeedViewUtils.showErrorMessageToast(requireContext(), errorMessage)
                }

                else -> {}
            }
        }.observeInLifecycle(viewLifecycleOwner)

        videoFeedViewModel.helperViewModel.errorMessageEventFlow.onEach { response ->
            when (response) {
                is LMFeedHelperViewModel.ErrorMessageEvent.LikePost -> {
                    val postId = response.postId

                    //get post and index
                    val pair = getIndexAndPostFromAdapter(postId) ?: return@onEach
                    val post = pair.second
                    val index = pair.first

                    val postActionData = post.actionViewData

                    val newLikesCount = if (postActionData.isLiked) {
                        postActionData.likesCount - 1
                    } else {
                        postActionData.likesCount + 1
                    }

                    val updatedIsLiked = !postActionData.isLiked

                    val updatedActionViewData = postActionData.toBuilder()
                        .isLiked(updatedIsLiked)
                        .likesCount(newLikesCount)
                        .build()

                    val updatedPostData = post.toBuilder()
                        .actionViewData(updatedActionViewData)
                        .fromPostLiked(true)
                        .build()

                    videoFeedAdapter.update(index, updatedPostData)

                    //show error message
                    LMFeedViewUtils.showSomethingWentWrongToast(requireContext())
                }

                else -> {}
            }
        }.observeInLifecycle(viewLifecycleOwner)
    }

    //checks whether posts are empty or not and replaces the posts accordingly
    private fun checkPostsAndReplace(posts: List<LMFeedPostViewData>) {
        if (posts.isEmpty()) {
            videoFeedAdapter.add(LMFeedCaughtUpViewData.Builder().build())
        } else {
            //cache starting 2 videos
            preCache(0)
            videoFeedAdapter.replace(posts)
        }
    }

    //replaces the video view in the video feed list view
    protected open fun replaceVideoView(position: Int): LMFeedPostVerticalVideoMediaView? {
        //get the video feed binding to play the view in [postVideoView]
        val videoFeedBinding =
            ((binding.vp2VideoFeed[0] as? RecyclerView)?.findViewHolderForAdapterPosition(position) as? LMFeedDataBoundViewHolder<*>)
                ?.binding as? LmFeedItemPostVideoFeedBinding ?: return null

        return videoFeedBinding.postVideoView
    }

    //plays the video at specified position if present in view pager
    fun playVideoInViewPager(position: Int) {
        if (position >= 0 && videoFeedAdapter.items()[position] != null) {
            val data = videoFeedAdapter.items()[position]
            if (data !is LMFeedPostViewData) {
                postVideoPreviewAutoPlayHelper.removePlayer(triggerSwipeOrScrollEvent = true, this)
                return
            }

            val videoView = replaceVideoView(position) ?: return

            val url = data.mediaViewData.attachments.firstOrNull()?.attachmentMeta?.url ?: ""

            //plays the video in the [postVideoView]
            postVideoPreviewAutoPlayHelper.playVideoInView(
                videoPost = videoView,
                url = url,
                config = config ?: LMFeedVideoFeedConfig.Builder().build(),
                videoPlayerListener = this
            )

            //cache videos from next position till [PRECACHE_VIDEO_COUNT]
            preCache(position + 1)
        } else {
            postVideoPreviewAutoPlayHelper.removePlayer()
        }
    }

    override fun onDurationThresholdReached(duration: Long, totalDuration: Long) {
        val currentPosition = binding.vp2VideoFeed.currentItem
        val currentReel = videoFeedAdapter.items()[currentPosition]
        if (currentReel is LMFeedPostViewData) {
            val reelId = currentReel.id
            sendReelViewedEvent(reelId, duration, totalDuration)
        }
    }

    override fun onVideoSwipedOrScrolled(duration: Long, totalDuration: Long) {
        //for reel swiped event
        val currentPosition = binding.vp2VideoFeed.currentItem
        val previousItem = videoFeedAdapter.items().getOrNull(currentPosition - 1)
        if (previousItem != null && previousItem is LMFeedPostViewData) {
            sendReelSwipedEvent(
                previousItem.id,
                duration,
                totalDuration
            )
        }
    }

    override fun onIdleSwipeReached() {
        if (feedType == PERSONALISED_FEED) {
            videoFeedViewModel.helperViewModel.apply {
                setPostSeenInLocalDb()
                postSeen()
            }
        }
    }

    //precache the videos from specified position till [PRECACHE_VIDEO_COUNT]
    private fun preCache(position: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                for (i in position..position + PRECACHE_VIDEO_COUNT) {
                    if (videoFeedAdapter.itemCount <= i) {
                        return@launch
                    }

                    val data = (videoFeedAdapter.items()[i])
                    if (data !is LMFeedPostViewData) {
                        return@launch
                    }

                    val url =
                        data.mediaViewData.attachments.first().attachmentMeta.url

                    CacheWriter(
                        LMFeedVideoCache.getCacheDataSourceFactory(requireContext().applicationContext)
                            .createDataSource(),
                        DataSpec(
                            Uri.parse(url),
                            0,
                            CACHE_SIZE_EACH_VIDEO
                        ),
                        null
                    ) { _, _, _ -> }.cache()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val currentItem = binding.vp2VideoFeed.currentItem

        if (currentItem >= 0
            && videoFeedAdapter.itemCount > currentItem
            && videoFeedAdapter.items()[currentItem] != null
        ) {
            playVideoInViewPager(currentItem)
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (hidden) {
            // remove the player when fragment is hidden
            postVideoPreviewAutoPlayHelper.removePlayer()
        } else {
            // play the video when fragment is not hidden
            val currentItem = binding.vp2VideoFeed.currentItem

            if (currentItem >= 0
                && videoFeedAdapter.itemCount > currentItem
                && videoFeedAdapter.items()[currentItem] != null
            ) {
                playVideoInViewPager(currentItem)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        postVideoPreviewAutoPlayHelper.removePlayer()
    }

    override fun onStop() {
        if (feedType == PERSONALISED_FEED) {
            videoFeedViewModel.helperViewModel.setPostSeenInLocalDb()
        }
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (this::videoFeedAdapter.isInitialized && videoFeedAdapter.items().isNotEmpty()) {
            val postItems = videoFeedAdapter.items().filterIsInstance<LMFeedPostViewData>()
            videoFeedViewModel.setViewPagerState(binding.vp2VideoFeed.currentItem, postItems)
        }
        postVideoPreviewAutoPlayHelper.removePlayer()
    }

    //callback when the user clicks on the post like button
    override fun onPostLikeClicked(position: Int, postViewData: LMFeedPostViewData) {
        val userPreferences = LMFeedUserPreferences(requireContext())
        val loggedInUUID = userPreferences.getUUID()

        //call api
        videoFeedViewModel.helperViewModel.likePost(
            postViewData.id,
            postViewData.actionViewData.isLiked,
            loggedInUUID
        )

        val adapterPosition = getIndexAndPostFromAdapter(postViewData.id)?.first ?: return

        //update view pager item
        videoFeedAdapter.update(adapterPosition, postViewData)
    }

    //callback when the user clicks on the post share button
    override fun onPostShareClicked(position: Int, postViewData: LMFeedPostViewData) {
        val userMeta = LMFeedUserMetaData.getInstance()
        LMFeedShareUtils.sharePost(
            requireContext(),
            postViewData.id,
            userMeta.domain ?: "",
            LMFeedCommunityUtil.getPostVariable()
        )

        val loggedInUUID = userPreferences.getUUID()

        LMFeedAnalytics.sendReelSharedEvent(loggedInUUID, postViewData)
    }

    //updates the fromPostLiked/fromPostSaved variables and updates the rv list
    override fun updateFromLikedSaved(position: Int, postViewData: LMFeedPostViewData) {
        val updatedPostData = postViewData.toBuilder()
            .fromPostLiked(false)
            .fromPostSaved(false)
            .build()

        //update view pager item without notifying
        videoFeedAdapter.updateWithoutNotifyingRV(position, updatedPostData)
    }

    //updates [alreadySeenFullContent] for the post
    override fun onPostContentSeeMoreClicked(position: Int, postViewData: LMFeedPostViewData) {
        val adapterPosition = getIndexAndPostFromAdapter(postViewData.id)?.first ?: return

        //update view pager item
        videoFeedAdapter.update(adapterPosition, postViewData)
    }

    //callback when the user clicks on the link in the post content
    override fun onPostContentLinkClicked(url: String) {
        // creates a route and returns an intent to handle the link
        val intent = LMFeedRoute.handleDeepLink(requireContext(), url)
        if (intent != null) {
            try {
                // starts activity with the intent
                ActivityCompat.startActivity(requireContext(), intent, null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //callback when the user clicks on the video feed caught up layout
    override fun onPostVideoFeedCaughtUpClicked() {
        super.onPostVideoFeedCaughtUpClicked()

        binding.vp2VideoFeed.apply {
            onFeedRefreshed()
        }
    }

    //callback when the user clicks on the post menu icon in the post action view
    override fun onPostActionMenuClicked(position: Int, postViewData: LMFeedPostViewData) {
        super.onPostActionMenuClicked(position, postViewData)

        val postMenuExtras = LMFeedPostMenuExtras.Builder()
            .postId(postViewData.id)
            .menuItems(postViewData.headerViewData.menuItems)
            .build()

        LMFeedPostMenuBottomSheetFragment.newInstance(childFragmentManager, postMenuExtras)
    }

    //callback when the user clicks on the post author header
    override fun onPostAuthorHeaderClicked(position: Int, postViewData: LMFeedPostViewData) {
        super.onPostAuthorHeaderClicked(position, postViewData)

        val coreCallback = LMFeedCoreApplication.getLMFeedCoreCallback()
        coreCallback?.openProfile(postViewData.headerViewData.user)
    }

    //callback when the tag of the user is clicked
    override fun onPostTaggedMemberClicked(position: Int, uuid: String) {
        super.onPostTaggedMemberClicked(position, uuid)

        val coreCallback = LMFeedCoreApplication.getLMFeedCoreCallback()
        coreCallback?.openProfileWithUUID(uuid)
    }

    //callback when the user clicks on post menu item in the menu bottom sheet
    override fun onPostMenuItemClicked(postId: String, menuItem: LMFeedPostMenuItemViewData) {
        val postWithIndex = getIndexAndPostFromAdapter(postId) ?: return
        val position = postWithIndex.first
        val postViewData = postWithIndex.second

        when (val menuId = menuItem.id) {
            EDIT_POST_MENU_ITEM_ID -> {
                onEditPostMenuClicked(
                    position,
                    menuId,
                    postViewData
                )
            }

            DELETE_POST_MENU_ITEM_ID -> {
                onDeletePostMenuClicked(
                    position,
                    menuId,
                    postViewData
                )
            }

            REPORT_POST_MENU_ITEM_ID -> {
                onReportPostMenuClicked(
                    position,
                    menuId,
                    postViewData
                )
            }

            PIN_POST_MENU_ITEM_ID -> {
                val updatedPostViewData =
                    LMFeedPostBinderUtils.updatePostForPin(requireContext(), postViewData)

                updatedPostViewData?.let {
//                    onPinPostMenuClicked(
//                        position,
//                        menuId,
//                        it
//                    )
                }
            }

            UNPIN_POST_MENU_ITEM_ID -> {
                val updatedPost =
                    LMFeedPostBinderUtils.updatePostForUnpin(requireContext(), postViewData)

                updatedPost?.let {
//                    onUnpinPostMenuClicked(
//                        position,
//                        menuId,
//                        it
//                    )
                }
            }
        }
    }

    //processes the edit post menu click
    protected open fun onEditPostMenuClicked(
        position: Int,
        menuId: Int,
        post: LMFeedPostViewData
    ) {
        //to be implemented
    }

    //processes the delete post menu click
    protected open fun onDeletePostMenuClicked(
        position: Int,
        menuId: Int,
        post: LMFeedPostViewData
    ) {
        val deleteExtras = LMFeedDeleteExtras.Builder()
            .postId(post.id)
            .entityType(DELETE_TYPE_POST)
            .build()

        val postCreatorUUID = post.headerViewData.user.sdkClientInfoViewData.uuid

        val userPreferences = LMFeedUserPreferences(requireContext())
        val loggedInUUID = userPreferences.getUUID()

        if (postCreatorUUID == loggedInUUID) {
            // if the post was created by current user
            LMFeedSelfDeleteDialogFragment.showDialog(
                childFragmentManager,
                deleteExtras
            )
        } else {
            // if the post was not created by current user and they are admin
            LMFeedAdminDeleteDialogFragment.showDialog(
                childFragmentManager,
                deleteExtras
            )
        }
    }

    // launcher to start [ReportActivity] and show success dialog for result
    private val reportPostLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data?.getStringExtra(LM_FEED_REPORT_RESULT)
                val entityType = if (data == "Post") {
                    LMFeedCommunityUtil.getPostVariable()
                        .pluralizeOrCapitalize(LMFeedWordAction.FIRST_LETTER_CAPITAL_SINGULAR)
                } else {
                    data
                }
                LMFeedReportSuccessDialogFragment(entityType ?: "").show(
                    childFragmentManager,
                    LMFeedReportSuccessDialogFragment.TAG
                )
            }
        }

    //processes the report post menu click
    protected open fun onReportPostMenuClicked(
        position: Int,
        menuId: Int,
        post: LMFeedPostViewData
    ) {
        //create extras for [ReportActivity]
        val reportExtras = LMFeedReportExtras.Builder()
            .entityId(post.id)
            .uuid(post.headerViewData.user.sdkClientInfoViewData.uuid)
            .entityType(REPORT_TYPE_POST)
            .postViewType(post.viewType)
            .build()

        //get Intent for [ReportActivity]
        val intent = LMFeedReportActivity.getIntent(requireContext(), reportExtras)

        //start [ReportActivity] and check for result
        reportPostLauncher.launch(intent)
    }

    /**
     * Adapter Util Block
     **/

    //get index and post from the adapter using postId
    private fun getIndexAndPostFromAdapter(postId: String): Pair<Int, LMFeedPostViewData>? {
        val index = videoFeedAdapter.items().indexOfFirst {
            (it is LMFeedPostViewData) && (it.id == postId)
        }

        if (index == -1) {
            return null
        }

        val post = videoFeedAdapter.items()[index] as? LMFeedPostViewData ?: return null

        return Pair(index, post)
    }

    //send analytics events for explore reels opened
    private fun sendExploreReelsOpenedEvent() {
        val loggedInUUID = userPreferences.getUUID()
        LMFeedAnalytics.sendExploreReelsOpenedEvent(loggedInUUID)
    }

    //send analytics events for no more reels shown
    private fun sendNoMoreReelsShownEvent() {
        val loggedInUUID = userPreferences.getUUID()
        LMFeedAnalytics.sendNoMoreReelsShownEvent(loggedInUUID)
    }

    //send analytics events for reel viewed
    private fun sendReelViewedEvent(reelId: String, watchDuration: Long, totalDuration: Long) {
        val loggedInUUID = userPreferences.getUUID()

        val watchDurationInInt = (watchDuration / 1000).toInt()
        val totalDurationInFloat = totalDuration / 1000f

        LMFeedAnalytics.sendReelsViewedEvent(
            loggedInUUID,
            reelId,
            watchDurationInInt,
            totalDurationInFloat
        )
    }

    //send analytics events for reel swiped
    private fun sendReelSwipedEvent(
        previousReelId: String,
        previousReelWatchDuration: Long,
        previousReelTotalDuration: Long
    ) {
        val loggedInUUID = userPreferences.getUUID()

        val previousWatchDurationInFloat = previousReelWatchDuration / 1000f
        val previousReelTotalDurationInFloat = previousReelTotalDuration / 1000f
        LMFeedAnalytics.sendReelSwipedEvent(
            loggedInUUID,
            previousReelId,
            previousWatchDurationInFloat,
            previousReelTotalDurationInFloat
        )
    }
}