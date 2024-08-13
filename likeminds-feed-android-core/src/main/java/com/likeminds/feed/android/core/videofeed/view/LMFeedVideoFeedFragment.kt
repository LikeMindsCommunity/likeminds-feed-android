package com.likeminds.feed.android.core.videofeed.view

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.util.containsKey
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
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
import com.likeminds.feed.android.core.post.edit.model.LMFeedEditPostExtras
import com.likeminds.feed.android.core.post.edit.view.LMFeedEditPostActivity
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
import com.likeminds.feed.android.core.ui.widgets.post.postmedia.view.LMFeedPostVerticalVideoMediaView
import com.likeminds.feed.android.core.utils.*
import com.likeminds.feed.android.core.utils.LMFeedValueUtils.pluralizeOrCapitalize
import com.likeminds.feed.android.core.utils.base.LMFeedDataBoundViewHolder
import com.likeminds.feed.android.core.utils.coroutine.observeInLifecycle
import com.likeminds.feed.android.core.utils.pluralize.model.LMFeedWordAction
import com.likeminds.feed.android.core.utils.user.LMFeedUserPreferences
import com.likeminds.feed.android.core.utils.video.LMFeedPostVideoPreviewAutoPlayHelper
import com.likeminds.feed.android.core.utils.video.LMFeedVideoCache
import com.likeminds.feed.android.core.videofeed.adapter.LMFeedVideoFeedAdapter
import com.likeminds.feed.android.core.videofeed.model.LMFeedCaughtUpViewData
import com.likeminds.feed.android.core.videofeed.viewmodel.LMFeedVideoFeedViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.onEach

open class LMFeedVideoFeedFragment :
    Fragment(),
    LMFeedPostAdapterListener,
    LMFeedPostMenuBottomSheetListener {

    lateinit var binding: LmFeedFragmentVideoFeedBinding

    lateinit var videoFeedAdapter: LMFeedVideoFeedAdapter

    private val videoFeedViewModel: LMFeedVideoFeedViewModel by viewModels()

    private val postVideoPreviewAutoPlayHelper by lazy {
        LMFeedPostVideoPreviewAutoPlayHelper.getInstance()
    }

    private var pageToCall: Int = 0

    private var previousTotal: Int = 0

    companion object {
        private const val VIDEO_PRELOAD_THRESHOLD = 5
        private const val CACHE_SIZE_EACH_VIDEO = 50 * 1024 * 1024L // 50 MB
        private const val PRECACHE_VIDEO_COUNT = 2 //we will precache 2 videos from current position
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LmFeedFragmentVideoFeedBinding.inflate(layoutInflater)

        setupVideoFeed()
        binding.apply {
            customizeVideoFeedListView(binding.vp2VideoFeed, videoFeedAdapter)
        }

        return binding.root
    }

    //sets up the video feed
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
        }
        videoFeedAdapter = LMFeedVideoFeedAdapter(this)
        setVerticalVideoPostViewStyle()
    }

    //sets view style to vertical video post
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
                    .textColor(R.color.lm_feed_white)
                    .maxLines(1)
                    .expandableCTAText("...")
                    .expandableCTAColor(R.color.lm_feed_white)
                    .build()
            )
            .postActionViewStyle(
                postActionViewStyle.toBuilder()
                    .commentTextStyle(null)
                    .shareIconStyle(null)
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

    protected open fun customizeVideoFeedListView(
        vp2VideoFeed: ViewPager2,
        videoFeedAdapter: LMFeedVideoFeedAdapter
    ) {
        //customize video feed view here
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchData()
        initViewPager()
        observeResponses()
    }

    //calls the getFeed() function to fetch feed videos
    private fun fetchData() {
        pageToCall++
        videoFeedViewModel.getFeed(pageToCall)
    }

    //initializes the view pager
    private fun initViewPager() {
        binding.vp2VideoFeed.apply {
            registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    //if the current item is the last item then show Caught up layout
                    if (position == videoFeedAdapter.itemCount - 1
                        && (videoFeedAdapter.items().lastOrNull() !is LMFeedCaughtUpViewData)
                    ) {
                        videoFeedAdapter.add(LMFeedCaughtUpViewData.Builder().build())
                    }

                    //plays the video in the view pager
                    playVideoInViewPager(position)
                }

                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)

                    val size: Int = videoFeedAdapter.itemCount

                    // Call API if the [VIDEO_PRELOAD_THRESHOLD] is reached
                    if (currentItem > 0 && currentItem >= size - VIDEO_PRELOAD_THRESHOLD) {
                        if (videoFeedAdapter.itemCount > previousTotal) {
                            previousTotal = videoFeedAdapter.itemCount
                            fetchData()
                        }
                    }
                }
            })

            adapter = videoFeedAdapter
        }
    }

    //observes live data responses
    private fun observeResponses() {
        videoFeedViewModel.videoFeedResponse.observe(viewLifecycleOwner) { response ->
            val page = response.first
            val posts = response.second

            //add only those posts which are supported by the adapter
            val finalPosts = posts.filter {
                (videoFeedAdapter.supportedViewBinderResolverMap.containsKey(it.viewType))
            }

            if (page == 1) {
                checkPostsAndReplace(finalPosts)
            } else {
                videoFeedAdapter.addAll(finalPosts)
            }
        }

        videoFeedViewModel.errorMessageEventFlow.onEach { response ->
            when (response) {
                is LMFeedVideoFeedViewModel.ErrorMessageEvent.LikePost -> {
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

                is LMFeedVideoFeedViewModel.ErrorMessageEvent.VideoFeed -> {
                    val errorMessage = response.errorMessage
                    LMFeedViewUtils.showErrorMessageToast(requireContext(), errorMessage)
                }
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
        val videoView = replaceVideoView(position) ?: return
        if (position >= 0 && videoFeedAdapter.items()[position] != null) {
            val data = videoFeedAdapter.items()[position]
            if (data !is LMFeedPostViewData) {
                postVideoPreviewAutoPlayHelper.removePlayer()
                return
            }
            val url = data.mediaViewData.attachments.firstOrNull()?.attachmentMeta?.url ?: ""

            //plays the video in the [postVideoView]
            postVideoPreviewAutoPlayHelper.playVideoInView(videoView, url)

            //cache videos from next position till [PRECACHE_VIDEO_COUNT]
            preCache(position + 1)
        } else {
            postVideoPreviewAutoPlayHelper.removePlayer()
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

    override fun onPause() {
        super.onPause()
        postVideoPreviewAutoPlayHelper.removePlayer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        postVideoPreviewAutoPlayHelper.removePlayer()
    }

    //callback when the user clicks on the post like button
    override fun onPostLikeClicked(position: Int, postViewData: LMFeedPostViewData) {
        val userPreferences = LMFeedUserPreferences(requireContext())
        val loggedInUUID = userPreferences.getUUID()

        //call api
        videoFeedViewModel.likePost(
            postViewData.id,
            postViewData.actionViewData.isLiked,
            loggedInUUID
        )

        val adapterPosition = getIndexAndPostFromAdapter(postViewData.id)?.first ?: return

        //update view pager item
        videoFeedAdapter.update(adapterPosition, postViewData)
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

        binding.vp2VideoFeed.setCurrentItem(0, true)
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
}