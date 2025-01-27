package com.likeminds.feed.android.core.socialfeed.view

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.likeminds.feed.android.core.LMFeedCoreApplication
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.activityfeed.view.LMFeedActivityFeedActivity
import com.likeminds.feed.android.core.databinding.LmFeedFragmentSocialFeedBinding
import com.likeminds.feed.android.core.delete.model.DELETE_TYPE_POST
import com.likeminds.feed.android.core.delete.model.LMFeedDeleteExtras
import com.likeminds.feed.android.core.delete.view.*
import com.likeminds.feed.android.core.likes.model.LMFeedLikesScreenExtras
import com.likeminds.feed.android.core.likes.model.POST
import com.likeminds.feed.android.core.likes.view.LMFeedLikesActivity
import com.likeminds.feed.android.core.poll.result.model.*
import com.likeminds.feed.android.core.poll.result.view.LMFeedPollResultsActivity
import com.likeminds.feed.android.core.post.create.model.LMFeedCreatePostExtras
import com.likeminds.feed.android.core.post.create.view.LMFeedCreatePostActivity
import com.likeminds.feed.android.core.post.detail.model.LMFeedPostDetailExtras
import com.likeminds.feed.android.core.post.detail.view.LMFeedPostDetailActivity
import com.likeminds.feed.android.core.post.edit.model.LMFeedEditPostExtras
import com.likeminds.feed.android.core.post.edit.view.LMFeedEditPostActivity
import com.likeminds.feed.android.core.post.model.LMFeedAttachmentViewData
import com.likeminds.feed.android.core.post.util.LMFeedPostEvent
import com.likeminds.feed.android.core.post.util.LMFeedPostObserver
import com.likeminds.feed.android.core.post.viewmodel.LMFeedHelperViewModel
import com.likeminds.feed.android.core.post.viewmodel.LMFeedPostViewModel
import com.likeminds.feed.android.core.post.viewmodel.LMFeedPostViewModel.ErrorMessageEvent.*
import com.likeminds.feed.android.core.postmenu.model.*
import com.likeminds.feed.android.core.report.model.LMFeedReportExtras
import com.likeminds.feed.android.core.report.model.REPORT_TYPE_POST
import com.likeminds.feed.android.core.report.view.LMFeedReportActivity
import com.likeminds.feed.android.core.report.view.LMFeedReportFragment.Companion.LM_FEED_REPORT_RESULT
import com.likeminds.feed.android.core.report.view.LMFeedReportSuccessDialogFragment
import com.likeminds.feed.android.core.search.model.LMFeedSearchExtras
import com.likeminds.feed.android.core.search.view.LMFeedSearchActivity
import com.likeminds.feed.android.core.socialfeed.adapter.LMFeedPostAdapterListener
import com.likeminds.feed.android.core.socialfeed.adapter.LMFeedSelectedTopicAdapterListener
import com.likeminds.feed.android.core.socialfeed.model.LMFeedPostViewData
import com.likeminds.feed.android.core.socialfeed.util.LMFeedPostBinderUtils
import com.likeminds.feed.android.core.socialfeed.viewmodel.LMFeedSocialFeedViewModel
import com.likeminds.feed.android.core.topics.model.LMFeedTopicViewData
import com.likeminds.feed.android.core.topicselection.model.LMFeedTopicSelectionExtras
import com.likeminds.feed.android.core.topicselection.model.LMFeedTopicSelectionResultExtras
import com.likeminds.feed.android.core.topicselection.view.LMFeedTopicSelectionActivity
import com.likeminds.feed.android.core.topicselection.view.LMFeedTopicSelectionActivity.Companion.LM_FEED_TOPIC_SELECTION_RESULT_EXTRAS
import com.likeminds.feed.android.core.ui.base.styles.setStyle
import com.likeminds.feed.android.core.ui.base.views.LMFeedFAB
import com.likeminds.feed.android.core.ui.theme.LMFeedAppearance
import com.likeminds.feed.android.core.ui.widgets.headerview.view.LMFeedHeaderView
import com.likeminds.feed.android.core.ui.widgets.noentitylayout.view.LMFeedNoEntityLayoutView
import com.likeminds.feed.android.core.ui.widgets.overflowmenu.view.LMFeedOverflowMenu
import com.likeminds.feed.android.core.ui.widgets.poll.model.LMFeedAddPollOptionExtras
import com.likeminds.feed.android.core.ui.widgets.poll.view.*
import com.likeminds.feed.android.core.utils.*
import com.likeminds.feed.android.core.utils.LMFeedValueUtils.pluralizeOrCapitalize
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.hide
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.show
import com.likeminds.feed.android.core.utils.analytics.LMFeedAnalytics
import com.likeminds.feed.android.core.utils.analytics.LMFeedAnalytics.LMFeedScreenNames
import com.likeminds.feed.android.core.utils.base.LMFeedBaseViewType
import com.likeminds.feed.android.core.utils.coroutine.observeInLifecycle
import com.likeminds.feed.android.core.utils.feed.*
import com.likeminds.feed.android.core.utils.feed.LMFeedType.PERSONALISED_FEED
import com.likeminds.feed.android.core.utils.feed.LMFeedType.UNIVERSAL_FEED
import com.likeminds.feed.android.core.utils.mediauploader.LMFeedMediaUploadWorker
import com.likeminds.feed.android.core.utils.pluralize.model.LMFeedWordAction
import com.likeminds.feed.android.core.utils.user.*
import com.likeminds.likemindsfeed.post.model.PollMultiSelectState
import com.likeminds.likemindsfeed.search.model.SearchType
import kotlinx.coroutines.flow.onEach
import java.util.UUID

open class LMFeedSocialFeedFragment(private val feedType: LMFeedType) :
    LMFeedBaseThemeFragment(),
    LMFeedPostAdapterListener,
    LMFeedAdminDeleteDialogListener,
    LMFeedSelfDeleteDialogListener,
    LMFeedSelectedTopicAdapterListener,
    LMFeedAddPollOptionBottomSheetListener,
    LMFeedPostObserver {

    private lateinit var binding: LmFeedFragmentSocialFeedBinding
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout

    private val socialFeedViewModel: LMFeedSocialFeedViewModel by viewModels()

    // variable to check if there is a post already uploading
    private var alreadyPosting: Boolean = false
    private val workersMap by lazy { ArrayList<UUID>() }

    private val postPublisher by lazy {
        LMFeedPostEvent.getPublisher()
    }

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    companion object {

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        private const val POST_NOTIFICATIONS = Manifest.permission.POST_NOTIFICATIONS

        @JvmStatic
        fun getInstance(
            feedType: LMFeedType = UNIVERSAL_FEED
        ): LMFeedSocialFeedFragment {
            return LMFeedSocialFeedFragment(feedType)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LmFeedFragmentSocialFeedBinding.inflate(layoutInflater)

        binding.apply {
            rvSocial.initAdapterAndSetListener(this@LMFeedSocialFeedFragment)

            customizeCreateNewPostButton(fabNewPost)
            customizeSocialFeedHeaderView(headerViewSocial)
            customizeNoPostLayout(layoutNoPost)
            customizePostingLayout(layoutPosting)
            customizeTopicSelectorBar(topicSelectorBar)
            customizeSocialFeedListView(rvSocial)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
        fetchData()
        initListeners()
        observeResponses()
        checkForNotificationPermission()
    }

    override fun onStart() {
        super.onStart()
        postPublisher.subscribe(this)
    }

    override fun onResume() {
        super.onResume()

        // sends feed opened event
        LMFeedAnalytics.sendFeedOpenedEvent()

        socialFeedViewModel.postViewModel.fetchPendingPostFromDB()
        binding.rvSocial.initiateVideoAutoPlayer()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (hidden) {
            // destroy the video player when fragment is not hidden
            binding.rvSocial.destroyVideoAutoPlayer()
        } else {
            // initiate the video player when fragment is not hidden
            binding.rvSocial.initiateVideoAutoPlayer()
        }
    }

    private fun fetchData() {
        socialFeedViewModel.apply {
            helperViewModel.getLoggedInUser()
            postViewModel.getCreatePostRights()
            helperViewModel.getUnreadNotificationCount()

            when (feedType) {
                PERSONALISED_FEED -> {
                    postViewModel.getPersonalisedFeed(
                        page = 1,
                        shouldReorder = true,
                        shouldRecompute = true
                    )
                    helperViewModel.postSeen()
                }

                UNIVERSAL_FEED -> {
                    postViewModel.getUniversalFeed(1, null)
                }
            }
        }
    }

    private fun initUI() {
        initSocialFeedRecyclerView()
        initSwipeRefreshLayout()

        if (feedType == UNIVERSAL_FEED) {
            initSelectedTopicRecyclerView()
        } else {
            binding.topicSelectorBar.hide()
        }
    }

    override fun onPause() {
        super.onPause()
        binding.rvSocial.destroyVideoAutoPlayer()
    }

    override fun onStop() {
        if (feedType == PERSONALISED_FEED) {
            socialFeedViewModel.helperViewModel.setPostSeenInLocalDb()
        }
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        // unsubscribes itself from the [PostPublisher]
        postPublisher.unsubscribe(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvSocial.destroyVideoAutoPlayer()
    }

    private fun initListeners() {
        binding.apply {
            fabNewPost.setOnClickListener {
                onCreateNewPostClick(true)
            }

            headerViewSocial.setNotificationIconClickListener {
                onNotificationIconClicked()
            }

            headerViewSocial.setSearchIconClickListener {
                onSearchIconClicked()
            }

            layoutNoPost.setActionFABClickListener {
                onCreateNewPostClick(true)
            }

            topicSelectorBar.setAllTopicsClickListener {
                onAllTopicsClicked()
            }
        }
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

    private fun observeResponses() {
        observePosting()

        //observes user response LiveData
        socialFeedViewModel.helperViewModel.userResponse.observe(viewLifecycleOwner) {
            binding.headerViewSocial.apply {
                setUserProfileClickListener {
                    onUserProfileClicked(it)
                }
                setUserProfileImage(it)
            }
        }

        // observes hasCreatePostRights LiveData
        socialFeedViewModel.postViewModel.hasCreatePostRights.observe(viewLifecycleOwner) {
            socialFeedViewModel.helperViewModel.getLoggedInUser()
            initNewPostClick(it)
        }

        socialFeedViewModel.postViewModel.personalisedFeedResponse.observe(viewLifecycleOwner) { response ->
            LMFeedProgressBarHelper.hideProgress(binding.progressBar)
            val page = response.first
            val posts = response.second

            if (mSwipeRefreshLayout.isRefreshing) {
                checkPostsAndReplace(posts)
                mSwipeRefreshLayout.isRefreshing = false
                return@observe
            }

            if (page == 1) {
                checkPostsAndReplace(posts)
            } else {
                binding.rvSocial.addPosts(posts)
                binding.rvSocial.refreshVideoAutoPlayer()
            }
        }

        socialFeedViewModel.postViewModel.universalFeedResponse.observe(viewLifecycleOwner) { response ->
            LMFeedProgressBarHelper.hideProgress(binding.progressBar)
            val page = response.first
            val posts = response.second

            if (mSwipeRefreshLayout.isRefreshing) {
                checkPostsAndReplace(posts)
                mSwipeRefreshLayout.isRefreshing = false
                return@observe
            }

            if (page == 1) {
                checkPostsAndReplace(posts)
            } else {
                binding.rvSocial.addPosts(posts)
                binding.rvSocial.refreshVideoAutoPlayer()
            }
        }

        // observes deletePostResponse LiveData
        socialFeedViewModel.helperViewModel.deletePostResponse.observe(viewLifecycleOwner) { postId ->
            binding.rvSocial.apply {
                val indexToRemove = getIndexAndPostFromAdapter(postId)?.first ?: return@observe
                removePostAtIndex(indexToRemove)
                checkForNoPost(allPosts())
                refreshVideoAutoPlayer()
                LMFeedViewUtils.showShortToast(
                    requireContext(),
                    getString(
                        R.string.lm_feed_s_deleted,
                        LMFeedCommunityUtil.getPostVariable()
                            .pluralizeOrCapitalize(LMFeedWordAction.FIRST_LETTER_CAPITAL_SINGULAR)
                    )
                )
            }
        }

        socialFeedViewModel.helperViewModel.showTopicFilter.observe(viewLifecycleOwner) { showTopicFilter ->
            if (feedType == PERSONALISED_FEED) return@observe
            binding.topicSelectorBar.apply {
                isVisible = showTopicFilter
                setAllTopicsTextVisibility(showTopicFilter)
                setSelectedTopicFilterVisibility(false)
            }
        }

        socialFeedViewModel.helperViewModel.unreadNotificationCount.observe(viewLifecycleOwner) { unreadNotificationCount ->
            binding.headerViewSocial.setNotificationCountText(unreadNotificationCount)
        }

        socialFeedViewModel.helperViewModel.postSavedResponse.observe(viewLifecycleOwner) { postViewData ->
            //create toast message
            val toastMessage = if (postViewData.actionViewData.isSaved) {
                getString(
                    R.string.lm_feed_s_saved,
                    LMFeedCommunityUtil.getPostVariable()
                        .pluralizeOrCapitalize(LMFeedWordAction.FIRST_LETTER_CAPITAL_SINGULAR)
                )
            } else {
                getString(
                    R.string.lm_feed_s_unsaved,
                    LMFeedCommunityUtil.getPostVariable()
                        .pluralizeOrCapitalize(LMFeedWordAction.FIRST_LETTER_CAPITAL_SINGULAR)
                )
            }
            LMFeedViewUtils.showShortToast(requireContext(), toastMessage)
        }

        socialFeedViewModel.helperViewModel.postPinnedResponse.observe(viewLifecycleOwner) { postViewData ->
            //show toast message
            val toastMessage = if (postViewData.headerViewData.isPinned) {
                getString(
                    R.string.lm_feed_s_pinned_to_top,
                    LMFeedCommunityUtil.getPostVariable()
                        .pluralizeOrCapitalize(LMFeedWordAction.FIRST_LETTER_CAPITAL_SINGULAR)
                )
            } else {
                getString(
                    R.string.lm_feed_s_unpinned,
                    LMFeedCommunityUtil.getPostVariable()
                        .pluralizeOrCapitalize(LMFeedWordAction.FIRST_LETTER_CAPITAL_SINGULAR)
                )
            }
            LMFeedViewUtils.showShortToast(requireContext(), toastMessage)
        }

        //observers get post response
        socialFeedViewModel.postViewModel.postResponse.observe(viewLifecycleOwner) { postViewData ->
            binding.rvSocial.apply {
                val index = getIndexAndPostFromAdapter(postViewData.id)?.first ?: return@observe
                updatePostItem(index, postViewData)
            }
        }

        socialFeedViewModel.postViewModel.errorMessageEventFlow.onEach { response ->
            when (response) {
                is UniversalFeed -> {
                    val errorMessage = response.errorMessage
                    mSwipeRefreshLayout.isRefreshing = false
                    LMFeedProgressBarHelper.hideProgress(binding.progressBar)
                    LMFeedViewUtils.showErrorMessageToast(requireContext(), errorMessage)
                }

                is AddPost -> {
                    LMFeedViewUtils.showErrorMessageToast(requireContext(), response.errorMessage)
                    removePostingView()
                }

                is SubmitVote -> {
                    LMFeedViewUtils.showErrorMessageToast(requireContext(), response.errorMessage)
                }

                is AddPollOption -> {
                    LMFeedViewUtils.showErrorMessageToast(requireContext(), response.errorMessage)
                }

                is GetPost -> {
                    LMFeedViewUtils.showErrorMessageToast(requireContext(), response.errorMessage)
                }

                is PersonalisedFeed -> {
                    val errorMessage = response.errorMessage
                    mSwipeRefreshLayout.isRefreshing = false
                    LMFeedProgressBarHelper.hideProgress(binding.progressBar)
                    LMFeedViewUtils.showErrorMessageToast(requireContext(), errorMessage)
                }
            }
        }.observeInLifecycle(viewLifecycleOwner)

        socialFeedViewModel.helperViewModel.errorMessageEventFlow.onEach { response ->
            when (response) {
                is LMFeedHelperViewModel.ErrorMessageEvent.LikePost -> {
                    val postId = response.postId

                    //get post and index
                    val pair =
                        binding.rvSocial.getIndexAndPostFromAdapter(postId) ?: return@onEach
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

                    binding.rvSocial.updatePostItem(index, updatedPostData)

                    //show error message
                    LMFeedViewUtils.showSomethingWentWrongToast(requireContext())
                }

                is LMFeedHelperViewModel.ErrorMessageEvent.DeletePost -> {
                    val errorMessage = response.errorMessage
                    LMFeedViewUtils.showErrorMessageToast(requireContext(), errorMessage)
                }

                is LMFeedHelperViewModel.ErrorMessageEvent.PinPost -> {
                    binding.rvSocial.apply {
                        val postId = response.postId

                        //get post and index
                        val pair = getIndexAndPostFromAdapter(postId) ?: return@onEach
                        val post = pair.second
                        val index = pair.first

                        //update header view data
                        val updatedHeaderView = post.headerViewData.toBuilder()
                            .isPinned(!post.headerViewData.isPinned)
                            .build()

                        //update post view data
                        val updatedPostViewData = post.toBuilder()
                            .headerViewData(updatedHeaderView)
                            .build()

                        //update recycler view
                        updatePostItem(index, updatedPostViewData)

                        //show error message
                        LMFeedViewUtils.showSomethingWentWrongToast(requireContext())
                    }
                }

                is LMFeedHelperViewModel.ErrorMessageEvent.SavePost -> {
                    binding.rvSocial.apply {
                        val postId = response.postId

                        //get post and index
                        val pair = getIndexAndPostFromAdapter(postId) ?: return@onEach
                        val post = pair.second
                        val index = pair.first

                        //update action view data
                        val updatedActionView = post.actionViewData.toBuilder()
                            .isSaved(!post.actionViewData.isSaved)
                            .build()

                        //update post view data
                        val updatedPostViewData = post.toBuilder()
                            .actionViewData(updatedActionView)
                            .fromPostSaved(true)
                            .build()

                        //update recycler view
                        updatePostItem(index, updatedPostViewData)

                        //show error message
                        LMFeedViewUtils.showSomethingWentWrongToast(requireContext())
                    }
                }

                is LMFeedHelperViewModel.ErrorMessageEvent.GetTopic -> {
                    LMFeedViewUtils.showSomethingWentWrongToast(requireContext())
                }

                is LMFeedHelperViewModel.ErrorMessageEvent.GetUnreadNotificationCount -> {
                    binding.headerViewSocial.setNotificationIconVisibility(false)
                    LMFeedViewUtils.showErrorMessageToast(requireContext(), response.errorMessage)
                }
            }
        }.observeInLifecycle(viewLifecycleOwner)
    }

    // observes post live data
    private fun observePosting() {
        socialFeedViewModel.postViewModel.postDataEventFlow.onEach { response ->
            when (response) {
                // when the post data comes from local db
                is LMFeedPostViewModel.PostDataEvent.PostDbData -> {
                    val post = response.post
                    if (post.isPosted) {
                        removePostingView()
                        return@onEach
                    }
                    if (!alreadyPosting) {
                        alreadyPosting = true
                        binding.layoutPosting.apply {
                            show()
                            val postThumbnail =
                                post.mediaViewData.attachments.firstOrNull()?.attachmentMeta?.thumbnail

                            if (postThumbnail.isNullOrEmpty()) {
                                setAttachmentThumbnail(null)
                            } else {
                                setAttachmentThumbnail(Uri.parse(postThumbnail))
                            }
                            setProgress(0)
                            setProgressVisibility(true)

                            setPostSuccessfulVisibility(false)
                            setRetryVisibility(false)
                            observeMediaUpload(post)
                        }
                    }
                }

                // when the post data comes from api response
                is LMFeedPostViewModel.PostDataEvent.PostResponseData -> {
                    binding.apply {
                        LMFeedViewUtils.showShortToast(
                            requireContext(),
                            getString(
                                R.string.lm_feed_s_created,
                                LMFeedCommunityUtil.getPostVariable()
                                    .pluralizeOrCapitalize(
                                        LMFeedWordAction.FIRST_LETTER_CAPITAL_SINGULAR
                                    )
                            )
                        )
                        onFeedRefreshed()
                        removePostingView()
                    }
                }
            }
        }.observeInLifecycle(viewLifecycleOwner)
    }

    // removes the posting view and shows create post button
    private fun removePostingView() {
        binding.apply {
            alreadyPosting = false
            layoutPosting.hide()
        }
    }

    // finds the upload worker by UUID and observes the worker
    private fun observeMediaUpload(postingData: LMFeedPostViewData) {
        if (postingData.mediaViewData.workerUUID.isEmpty()) {
            return
        }
        val uuid = UUID.fromString(postingData.mediaViewData.workerUUID)
        if (!workersMap.contains(uuid)) {
            workersMap.add(uuid)
            WorkManager.getInstance(requireContext()).getWorkInfoByIdLiveData(uuid)
                .observe(viewLifecycleOwner) { workInfo ->
                    observeMediaWorker(workInfo, postingData)
                }
        }
    }

    // observes the media worker through various worker lifecycle
    private fun observeMediaWorker(
        workInfo: WorkInfo,
        postingData: LMFeedPostViewData
    ) {
        when (workInfo.state) {
            WorkInfo.State.SUCCEEDED -> {
                // uploading completed, call the add post api
                binding.layoutPosting.apply {
                    setProgressVisibility(false)
                    setRetryVisibility(false)
                    setPostSuccessfulVisibility(true)
                }
                socialFeedViewModel.postViewModel.addPost(postingData)
            }

            WorkInfo.State.FAILED -> {
                // uploading failed, initiate retry mechanism
                val indexList = workInfo.outputData.getIntArray(
                    LMFeedMediaUploadWorker.ARG_MEDIA_INDEX_LIST
                ) ?: return
                initRetryAction(
                    postingData.mediaViewData.temporaryId,
                    indexList.size
                )
            }

            else -> {
                // uploading in progress, map the progress to progress bar
                val progress = LMFeedMediaUploadWorker.getProgress(workInfo) ?: return
                binding.layoutPosting.apply {
                    val percentage = (((1.0 * progress.first) / progress.second) * 100)
                    val progressValue = percentage.toInt()
                    setProgress(progressValue)
                }
            }
        }
    }

    // initializes retry mechanism for attachments uploading
    private fun initRetryAction(temporaryId: Long?, attachmentCount: Int) {
        binding.layoutPosting.apply {
            setPostSuccessfulVisibility(false)
            setProgressVisibility(false)
            setRetryVisibility(true)
            setRetryCTAClickListener {
                onRetryUploadClicked(temporaryId, attachmentCount)
            }
        }
    }

    // initializes new post fab
    private fun initNewPostClick(hasCreatePostRights: Boolean) {
        binding.apply {

            if (hasCreatePostRights) {
                val fabButtonColor =
                    LMFeedStyleTransformer.socialFeedFragmentViewStyle.createNewPostButtonViewStyle.backgroundColor

                //sets color of fab button as per user rights
                layoutNoPost.setActionFABColor(fabButtonColor)
                fabNewPost.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), fabButtonColor))
            } else {
                //sets color of fab button as per user rights
                layoutNoPost.setActionFABColor(R.color.lm_feed_grey)
                fabNewPost.backgroundTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.lm_feed_grey
                        )
                    )
            }

            layoutNoPost.setActionFABClickListener {
                onCreateNewPostClick(hasCreatePostRights)
            }

            fabNewPost.setOnClickListener {
                onCreateNewPostClick(hasCreatePostRights)
            }
        }
    }

    private fun checkPostsAndReplace(posts: List<LMFeedPostViewData>) {
        binding.rvSocial.apply {
            checkForNoPost(posts)
            replacePosts(posts)
            scrollToPosition(0)
            refreshVideoAutoPlayer()
        }
    }

    // initializes the recycler view of the social feed
    private fun initSocialFeedRecyclerView() {
        LMFeedProgressBarHelper.showProgress(binding.progressBar)
        binding.rvSocial.apply {
            setAdapter()

            val paginationScrollListener =
                object : LMFeedEndlessRecyclerViewScrollListener(linearLayoutManager) {
                    override fun onLoadMore(currentPage: Int) {
                        if (currentPage > 0) {
                            when (feedType) {
                                PERSONALISED_FEED -> {
                                    socialFeedViewModel.postViewModel.getPersonalisedFeed(
                                        currentPage
                                    )
                                }

                                UNIVERSAL_FEED -> {
                                    socialFeedViewModel.postViewModel.getUniversalFeed(
                                        currentPage,
                                        socialFeedViewModel.helperViewModel.getTopicIdsFromAdapterList(
                                            binding.topicSelectorBar.getAllSelectedTopics()
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            setPaginationScrollListener(paginationScrollListener)

            val scrollStateListener =
                object : LMFeedRecyclerViewScrollStateListener(linearLayoutManager) {
                    override fun onItemVisibleMoreThan40Percent(position: Int) {
                        val postViewData = getPostFromAdapter(position)
                        postViewData?.let {
                            LMFeedPostSeenUtil.insertSeenPost(
                                postViewData,
                                System.currentTimeMillis()
                            )
                        }
                    }

                    override fun onScrollStateIdleReached() {
                        socialFeedViewModel.helperViewModel.apply {
                            setPostSeenInLocalDb()
                            postSeen()
                        }
                    }
                }

            if (feedType == PERSONALISED_FEED) {
                setScrollStateListener(scrollStateListener)
            }
        }
    }

    // initializes the swipe to refresh layout
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

    //init selected topic recycler view
    private fun initSelectedTopicRecyclerView() {
        binding.topicSelectorBar.apply {
            socialFeedViewModel.helperViewModel.getAllTopics(false)
            setSelectedTopicAdapter(this@LMFeedSocialFeedFragment)

            setClearSelectedTopicsClickListener {
                clearSelectedTopics()
            }
        }
    }

    //clear all selected topics and reset data
    private fun clearSelectedTopics() {
        binding.apply {
            //call api
            topicSelectorBar.clearSelectedTopicsAndNotify()
            rvSocial.resetPaginationScrollListenerData()
            LMFeedProgressBarHelper.showProgress(progressBar, true)
            socialFeedViewModel.postViewModel.getUniversalFeed(1, null)

            //show layout accordingly
            topicSelectorBar.setSelectedTopicFilterVisibility(false)
            topicSelectorBar.setAllTopicsTextVisibility(true)
        }
    }

    private fun checkForNoPost(feed: List<LMFeedBaseViewType>) {
        binding.apply {
            if (feed.isNotEmpty()) {
                layoutNoPost.hide()
                fabNewPost.show()
                rvSocial.show()
            } else {
                binding.apply {
                    layoutNoPost.show()
                    fabNewPost.hide()
                    rvSocial.hide()
                }
            }
        }
    }

    // callback when the user clicks on the post content
    override fun onPostContentClicked(position: Int, postViewData: LMFeedPostViewData) {
        // sends comment list open event
        LMFeedAnalytics.sendCommentListOpenEvent()

        val postDetailExtras = LMFeedPostDetailExtras.Builder()
            .postId(postViewData.id)
            .isEditTextFocused(false)
            .build()
        LMFeedPostDetailActivity.start(requireContext(), postDetailExtras)
    }

    //callback when the user clicks on the post like button
    override fun onPostLikeClicked(position: Int, postViewData: LMFeedPostViewData) {
        val userPreferences = LMFeedUserPreferences(requireContext())
        val loggedInUUID = userPreferences.getUUID()

        //call api
        socialFeedViewModel.helperViewModel.likePost(
            postViewData.id,
            postViewData.actionViewData.isLiked,
            loggedInUUID
        )

        binding.rvSocial.apply {
            val adapterPosition = getIndexAndPostFromAdapter(postViewData.id)?.first ?: return

            //update recycler
            updatePostItem(adapterPosition, postViewData)
        }
    }

    //callback when the user clicks on the post likes count
    override fun onPostLikesCountClicked(position: Int, postViewData: LMFeedPostViewData) {
        //show the likes screen
        val likesScreenExtras = LMFeedLikesScreenExtras.Builder()
            .postId(postViewData.id)
            .entityType(POST)
            .build()
        LMFeedLikesActivity.start(requireContext(), likesScreenExtras)
    }

    //callback when the user clicks on the post comments count
    override fun onPostCommentsCountClicked(position: Int, postViewData: LMFeedPostViewData) {
        // sends comment list open event
        LMFeedAnalytics.sendCommentListOpenEvent()

        val postDetailExtras = LMFeedPostDetailExtras.Builder()
            .postId(postViewData.id)
            .isEditTextFocused(true)
            .build()
        LMFeedPostDetailActivity.start(requireContext(), postDetailExtras)
    }

    //callback when the user clicks on the save post button
    override fun onPostSaveClicked(position: Int, postViewData: LMFeedPostViewData) {
        //call api
        socialFeedViewModel.helperViewModel.savePost(postViewData)

        binding.rvSocial.apply {
            val adapterPosition = getIndexAndPostFromAdapter(postViewData.id)?.first ?: return

            //update recycler
            updatePostItem(adapterPosition, postViewData)
        }
    }

    //callback when the user clicks on the share post button
    override fun onPostShareClicked(position: Int, postViewData: LMFeedPostViewData) {
        val userMeta = LMFeedUserMetaData.getInstance()
        LMFeedShareUtils.sharePost(
            requireContext(),
            postViewData.id,
            userMeta.domain ?: "",
            LMFeedCommunityUtil.getPostVariable()
        )

        LMFeedAnalytics.sendPostShared(postViewData)
    }

    //updates the fromPostLiked/fromPostSaved variables and updates the rv list
    override fun updateFromLikedSaved(position: Int, postViewData: LMFeedPostViewData) {
        val updatedPostData = postViewData.toBuilder()
            .fromPostLiked(false)
            .fromPostSaved(false)
            .build()

        binding.rvSocial.updatePostWithoutNotifying(position, updatedPostData)
    }

    //updates [alreadySeenFullContent] for the post
    override fun onPostContentSeeMoreClicked(position: Int, postViewData: LMFeedPostViewData) {
        binding.rvSocial.apply {
            val adapterPosition = getIndexAndPostFromAdapter(postViewData.id)?.first ?: return

            //update recycler
            updatePostItem(adapterPosition, postViewData)
        }
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

    //callback when the user clicks on the post menu icon
    override fun onPostMenuIconClicked(
        position: Int,
        anchorView: View,
        postViewData: LMFeedPostViewData
    ) {
        val popupMenu = LMFeedOverflowMenu(requireContext(), anchorView)
        val menuItems = postViewData.headerViewData.menuItems
        popupMenu.addMenuItems(menuItems)

        popupMenu.setMenuItemClickListener { menuId ->
            onPostMenuItemClicked(position, menuId, postViewData)
        }

        popupMenu.show()
    }

    //callback when the user clicks on the post image media
    override fun onPostImageMediaClicked(position: Int, postViewData: LMFeedPostViewData) {
        // sends comment list open event
        LMFeedAnalytics.sendCommentListOpenEvent()

        val postDetailExtras = LMFeedPostDetailExtras.Builder()
            .postId(postViewData.id)
            .isEditTextFocused(false)
            .build()
        LMFeedPostDetailActivity.start(requireContext(), postDetailExtras)
    }

    //callback when the user clicks on the post video media
    override fun onPostVideoMediaClicked(position: Int, postViewData: LMFeedPostViewData) {
        // sends comment list open event
        LMFeedAnalytics.sendCommentListOpenEvent()

        val postDetailExtras = LMFeedPostDetailExtras.Builder()
            .postId(postViewData.id)
            .isEditTextFocused(false)
            .build()
        LMFeedPostDetailActivity.start(requireContext(), postDetailExtras)
    }

    //callback when the user clicks on the post link media
    override fun onPostLinkMediaClicked(position: Int, postViewData: LMFeedPostViewData) {
        // creates a route and returns an intent to handle the link
        val intent = LMFeedRoute.handleDeepLink(
            requireContext(),
            postViewData.mediaViewData.attachments.firstOrNull()?.attachmentMeta?.ogTags?.url
        )

        if (intent != null) {
            try {
                // starts activity with the intent
                ActivityCompat.startActivity(requireContext(), intent, null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //callback when the user clicks on the post documents media
    override fun onPostDocumentMediaClicked(
        position: Int,
        parentPosition: Int,
        attachmentViewData: LMFeedAttachmentViewData
    ) {
        //open the pdf using Android's document view
        val documentUrl = attachmentViewData.attachmentMeta.url ?: ""
        val pdfUri = Uri.parse(documentUrl)
        LMFeedAndroidUtils.startDocumentViewer(requireContext(), pdfUri)
    }

    //callback when the user clicks on the post image inside multiple media
    override fun onPostMultipleMediaImageClicked(
        position: Int,
        parentPosition: Int,
        attachmentViewData: LMFeedAttachmentViewData
    ) {
        // sends comment list open event
        LMFeedAnalytics.sendCommentListOpenEvent()

        val postDetailExtras = LMFeedPostDetailExtras.Builder()
            .postId(attachmentViewData.postId)
            .isEditTextFocused(false)
            .build()
        LMFeedPostDetailActivity.start(requireContext(), postDetailExtras)
    }

    //callback when the user clicks on the post video inside multiple media
    override fun onPostMultipleMediaVideoClicked(
        position: Int,
        parentPosition: Int,
        attachmentViewData: LMFeedAttachmentViewData
    ) {
        // sends comment list open event
        LMFeedAnalytics.sendCommentListOpenEvent()

        val postDetailExtras = LMFeedPostDetailExtras.Builder()
            .postId(attachmentViewData.postId)
            .isEditTextFocused(false)
            .build()
        LMFeedPostDetailActivity.start(requireContext(), postDetailExtras)
    }

    //called when the page in the multiple media post is changed
    override fun onPostMultipleMediaPageChangeCallback(position: Int, parentPosition: Int) {
        //processes the current video whenever view pager's page is changed
        binding.rvSocial.refreshVideoAutoPlayer()
    }

    //called when show more is clicked in the documents type post
    override fun onPostMultipleDocumentsExpanded(position: Int, postViewData: LMFeedPostViewData) {
        binding.rvSocial.apply {
            if (position == itemCount - 1) {
                scrollToPositionWithOffset(position)
            }

            val updatedMediaViewData = postViewData.mediaViewData
                .toBuilder()
                .isExpanded(true)
                .build()

            val updatedPostViewData = postViewData.toBuilder()
                .mediaViewData(updatedMediaViewData)
                .fromPostSaved(false)
                .fromPostLiked(false)
                .build()

            binding.rvSocial.apply {
                val adapterPosition =
                    getIndexAndPostFromAdapter(updatedPostViewData.id)?.first ?: return

                //updates the [isExpanded] for the document item to true
                updatePostItem(adapterPosition, updatedPostViewData)
            }
        }
    }

    //callback when the user clicks on the post author header
    override fun onPostAuthorHeaderClicked(position: Int, postViewData: LMFeedPostViewData) {
        super.onPostAuthorHeaderClicked(position, postViewData)

        val coreCallback = LMFeedCoreApplication.getLMFeedCoreCallback()
        coreCallback?.openProfile(postViewData.headerViewData.user)
    }

    override fun onEntityDeletedByAdmin(deleteExtras: LMFeedDeleteExtras, reason: String) {
        val post =
            binding.rvSocial.getIndexAndPostFromAdapter(deleteExtras.postId)?.second ?: return
        socialFeedViewModel.helperViewModel.deletePost(post, reason)
    }

    override fun onEntityDeletedByAuthor(deleteExtras: LMFeedDeleteExtras) {
        val post =
            binding.rvSocial.getIndexAndPostFromAdapter(deleteExtras.postId)?.second ?: return
        socialFeedViewModel.helperViewModel.deletePost(post)
    }

    override fun onTopicRemoved(position: Int, topicViewData: LMFeedTopicViewData) {
        super.onTopicRemoved(position, topicViewData)

        binding.apply {
            val selectedTopics = topicSelectorBar.getAllSelectedTopics()
            if (selectedTopics.size == 1) {
                clearSelectedTopics()
            } else {
                //remove from adapter
                topicSelectorBar.removeTopicAndNotify(position)

                //call apis
                rvSocial.resetPaginationScrollListenerData()
                rvSocial.clearPostsAndNotify()
                LMFeedProgressBarHelper.showProgress(binding.progressBar, true)
                socialFeedViewModel.postViewModel.getUniversalFeed(
                    1,
                    socialFeedViewModel.helperViewModel.getTopicIdsFromAdapterList(selectedTopics)
                )
            }
        }
    }

    //callback when publisher publishes any updated postData
    override fun update(postData: Pair<String, LMFeedPostViewData?>) {
        val postId = postData.first
        // fetches post from adapter
        binding.rvSocial.apply {
            val postIndex = getIndexAndPostFromAdapter(postId)?.first ?: return

            val updatedPost = postData.second

            // updates the item in adapter
            if (updatedPost == null) {
                // Post was deleted!
                removePostAtIndex(postIndex)
            } else {
                // Post was updated
                updatePostItem(postIndex, updatedPost)
            }
        }
    }

    //callback when the tag of the user is clicked
    override fun onPostTaggedMemberClicked(position: Int, uuid: String) {
        super.onPostTaggedMemberClicked(position, uuid)

        val coreCallback = LMFeedCoreApplication.getLMFeedCoreCallback()
        coreCallback?.openProfileWithUUID(uuid)
    }

    //callback when the post title is clicked
    override fun onPostPollTitleClicked(position: Int, postViewData: LMFeedPostViewData) {
        super.onPostPollTitleClicked(position, postViewData)

        // sends comment list open event
        LMFeedAnalytics.sendCommentListOpenEvent()

        val postDetailExtras = LMFeedPostDetailExtras.Builder()
            .postId(postViewData.id)
            .isEditTextFocused(false)
            .build()
        LMFeedPostDetailActivity.start(requireContext(), postDetailExtras)
    }

    //callback when add poll option is clicked
    override fun onPostAddPollOptionClicked(position: Int, postViewData: LMFeedPostViewData) {
        super.onPostAddPollOptionClicked(position, postViewData)

        val pollAttachment = postViewData.mediaViewData.attachments.firstOrNull() ?: return
        val pollId = pollAttachment.attachmentMeta.poll?.id ?: return

        val addPollOptionExtras = LMFeedAddPollOptionExtras.Builder()
            .postId(postViewData.id)
            .pollId(pollId)
            .build()

        LMFeedAddPollOptionBottomSheetFragment.newInstance(
            childFragmentManager,
            addPollOptionExtras
        )
    }

    //callback when the poll member voted count is clicked
    override fun onPostMemberVotedCountClicked(position: Int, postViewData: LMFeedPostViewData) {
        super.onPostMemberVotedCountClicked(position, postViewData)

        val pollAttachment = postViewData.mediaViewData.attachments.firstOrNull() ?: return
        val pollViewData = pollAttachment.attachmentMeta.poll ?: return

        if (pollViewData.isAnonymous) {
            LMFeedAnonymousPollDialogFragment.showDialog(childFragmentManager)
            return
        }

        if (pollViewData.toShowResults || pollViewData.hasPollEnded()) {
            val pollResultsExtras = LMFeedPollResultsExtras.Builder()
                .pollId(pollViewData.id)
                .pollOptions(pollViewData.options)
                .build()

            LMFeedPollResultsActivity.start(requireContext(), pollResultsExtras)
        } else {
            LMFeedViewUtils.showShortToast(
                requireContext(),
                getString(R.string.lm_feed_the_results_will_be_visible_after_the_poll_has_ended)
            )
        }
    }

    //callback when the submit poll vote button is clicked
    override fun onPostSubmitPollVoteClicked(position: Int, postViewData: LMFeedPostViewData) {
        super.onPostSubmitPollVoteClicked(position, postViewData)

        val pollAttachment = postViewData.mediaViewData.attachments.firstOrNull() ?: return
        val pollViewData = pollAttachment.attachmentMeta.poll ?: return

        val selectedOptions = pollViewData.options.filter { it.isSelected }
        val selectedOptionIds = selectedOptions.map { it.id }

        validateSelectedPollOptions(pollViewData, selectedOptions.size) {
            socialFeedViewModel.postViewModel.submitPollVote(
                requireContext(),
                postViewData.id,
                pollViewData.id,
                selectedOptionIds
            )
        }
    }

    //validates user submitted poll votes for multiple choice poll
    private fun validateSelectedPollOptions(
        pollViewData: LMFeedPollViewData,
        selectedOptionsCount: Int,
        onValidationSuccess: () -> Unit
    ) {
        val multipleSelectNumber = pollViewData.multipleSelectNumber
        val multipleSelectState = pollViewData.multipleSelectState

        when (multipleSelectState) {
            PollMultiSelectState.EXACTLY -> {
                if (selectedOptionsCount != multipleSelectNumber) {
                    LMFeedViewUtils.showShortSnack(
                        binding.root,
                        resources.getQuantityString(
                            R.plurals.lm_feed_please_select_exactly_d_options,
                            multipleSelectNumber,
                            multipleSelectNumber
                        )
                    )
                    return
                }
            }

            PollMultiSelectState.AT_MAX -> {
                if (selectedOptionsCount > multipleSelectNumber) {
                    LMFeedViewUtils.showShortSnack(
                        binding.root,
                        resources.getQuantityString(
                            R.plurals.lm_feed_please_select_at_most_d_options,
                            multipleSelectNumber,
                            multipleSelectNumber
                        )
                    )
                    return
                }
            }

            PollMultiSelectState.AT_LEAST -> {
                if (selectedOptionsCount < multipleSelectNumber) {
                    LMFeedViewUtils.showShortSnack(
                        binding.root,
                        resources.getQuantityString(
                            R.plurals.lm_feed_please_select_at_least_d_options,
                            multipleSelectNumber,
                            multipleSelectNumber
                        )
                    )
                    return
                }
            }
        }
        onValidationSuccess()
    }

    //callback when the poll edit vote button is clicked
    override fun onPostEditPollVoteClicked(position: Int, postViewData: LMFeedPostViewData) {
        super.onPostEditPollVoteClicked(position, postViewData)

        binding.rvSocial.apply {
            val postIndex = getIndexAndPostFromAdapter(postViewData.id)?.first ?: return

            val attachment = postViewData.mediaViewData.attachments.firstOrNull() ?: return
            val pollViewData = attachment.attachmentMeta.poll ?: return


            //update the poll view data
            val updatedPollViewData = pollViewData.toBuilder()
                .isPollSubmitted(false)
                .build()

            //update the attachments with the updated poll view data
            val updatedAttachments = listOf(
                attachment.toBuilder()
                    .attachmentMeta(
                        attachment.attachmentMeta.toBuilder()
                            .poll(updatedPollViewData)
                            .build()
                    )
                    .build()
            )

            //update the post view data
            val updatedPostViewData = postViewData.toBuilder()
                .mediaViewData(
                    postViewData.mediaViewData.toBuilder()
                        .attachments(updatedAttachments)
                        .build()
                )
                .build()

            updatePostItem(postIndex, updatedPostViewData)
        }
    }

    //callback when the poll option is clicked
    override fun onPollOptionClicked(
        pollPosition: Int,
        pollOptionPosition: Int,
        pollOptionViewData: LMFeedPollOptionViewData
    ) {
        super.onPollOptionClicked(
            pollPosition,
            pollOptionPosition,
            pollOptionViewData
        )

        val postViewData = binding.rvSocial.allPosts()[pollPosition] as LMFeedPostViewData
        val attachment = postViewData.mediaViewData.attachments.firstOrNull() ?: return
        val pollViewData = attachment.attachmentMeta.poll ?: return

        when {
            pollViewData.hasPollEnded() -> {
                LMFeedViewUtils.showShortToast(
                    requireContext(),
                    getString(R.string.lm_feed_poll_ended_vote_cannot_be_submitted_now)
                )
                return
            }

            (pollViewData.isPollSubmitted && pollViewData.isInstantPoll()) -> {
                return
            }

            !pollViewData.isMultiChoicePoll() -> {
                if (pollViewData.isPollSubmitted) {
                    return
                }

                //call api to submit vote
                socialFeedViewModel.postViewModel.submitPollVote(
                    requireContext(),
                    postViewData.id,
                    pollViewData.id,
                    listOf(pollOptionViewData.id)
                )
            }

            else -> {
                if (pollViewData.isPollSubmitted) {
                    return
                }

                //update the clicked poll option view data
                val updatedPollOptionViewData = if (pollOptionViewData.isSelected) {
                    pollOptionViewData.toBuilder()
                        .isSelected(false)
                        .build()
                } else {
                    pollOptionViewData.toBuilder()
                        .isSelected(true)
                        .build()
                }

                //update the poll view data
                val updatedPollViewData = pollViewData.toBuilder()
                    .options(
                        pollViewData.options.map { pollOption ->
                            if (pollOption.id == pollOptionViewData.id) {
                                updatedPollOptionViewData
                            } else {
                                pollOption
                            }
                        }
                    )
                    .build()

                //update the attachments with the updated poll view data
                val updatedAttachments = listOf(
                    attachment.toBuilder()
                        .attachmentMeta(
                            attachment.attachmentMeta.toBuilder()
                                .poll(updatedPollViewData)
                                .build()
                        )
                        .build()
                )

                //update the post view data
                val updatedPostViewData = postViewData.toBuilder()
                    .mediaViewData(
                        postViewData.mediaViewData.toBuilder()
                            .attachments(updatedAttachments)
                            .build()
                    )
                    .build()

                //update the recycler view
                binding.rvSocial.updatePostItem(pollPosition, updatedPostViewData)
            }
        }

        if (pollViewData.hasPollEnded()) {
            LMFeedViewUtils.showShortToast(
                requireContext(),
                getString(R.string.lm_feed_poll_ended_vote_cannot_be_submitted_now)
            )
            return
        }
    }

    //callback when the polls option vote count is clicked
    override fun onPollOptionVoteCountClicked(
        pollPosition: Int,
        pollOptionPosition: Int,
        pollOptionViewData: LMFeedPollOptionViewData
    ) {
        super.onPollOptionVoteCountClicked(
            pollPosition,
            pollOptionPosition,
            pollOptionViewData
        )

        val postViewData = binding.rvSocial.allPosts()[pollPosition] as LMFeedPostViewData
        val attachment = postViewData.mediaViewData.attachments.firstOrNull() ?: return
        val pollViewData = attachment.attachmentMeta.poll ?: return

        if (pollViewData.isAnonymous) {
            LMFeedAnonymousPollDialogFragment.showDialog(childFragmentManager)
            return
        }

        if (pollOptionViewData.toShowResults || pollOptionViewData.hasPollEnded) {
            val pollResultsExtras = LMFeedPollResultsExtras.Builder()
                .pollId(pollViewData.id)
                .pollOptions(pollViewData.options)
                .selectedPollOptionId(pollOptionViewData.id)
                .build()

            LMFeedPollResultsActivity.start(requireContext(), pollResultsExtras)
        } else {
            LMFeedViewUtils.showShortToast(
                requireContext(),
                getString(R.string.lm_feed_the_results_will_be_visible_after_the_poll_has_ended)
            )
        }
    }

    //callback when an option is submitted
    override fun onAddOptionSubmitted(
        postId: String,
        pollId: String,
        option: String
    ) {
        val post = binding.rvSocial.getIndexAndPostFromAdapter(postId)?.second ?: return

        socialFeedViewModel.postViewModel.addPollOption(
            post,
            option
        )
    }

    //customizes the create new post fab
    protected open fun customizeCreateNewPostButton(fabNewPost: LMFeedFAB) {
        fabNewPost.apply {
            setStyle(LMFeedStyleTransformer.socialFeedFragmentViewStyle.createNewPostButtonViewStyle)

            text = getString(
                R.string.lm_feed_new_s,
                LMFeedCommunityUtil.getPostVariable()
                    .pluralizeOrCapitalize(LMFeedWordAction.ALL_CAPITAL_SINGULAR)
            )
        }
    }

    private val createPostLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    // post of type text/link has been created and posted
                    onFeedRefreshed()
                }

                LMFeedCreatePostActivity.RESULT_UPLOAD_POST -> {
                    // post with attachments created, now upload and post it from db
                    socialFeedViewModel.postViewModel.fetchPendingPostFromDB()
                }
            }
        }

    /**
     * Processes the new post fab click
     *
     * @param hasCreatePostRights: whether the user has the rights to create a post or not
     */
    protected open fun onCreateNewPostClick(hasCreatePostRights: Boolean) {
        binding.apply {
            if (hasCreatePostRights) {
                if (alreadyPosting) {
                    LMFeedViewUtils.showShortToast(
                        requireContext(),
                        getString(
                            R.string.lm_feed_a_s_is_already_uploading,
                            LMFeedCommunityUtil.getPostVariable()
                                .pluralizeOrCapitalize(LMFeedWordAction.ALL_SMALL_SINGULAR)
                        )
                    )
                } else {
                    // sends post creation started event
                    LMFeedAnalytics.sendPostCreationStartedEvent(LMFeedScreenNames.UNIVERSAL_FEED)

                    val createPostExtras = LMFeedCreatePostExtras.Builder()
                        .source(LMFeedAnalytics.LMFeedSource.SOCIAL_FEED)
                        .build()

                    val intent = LMFeedCreatePostActivity.getIntent(
                        requireContext(),
                        createPostExtras
                    )
                    createPostLauncher.launch(intent)
                }
            } else {
                LMFeedViewUtils.showShortSnack(
                    root,
                    getString(
                        R.string.lm_feed_you_do_not_have_permission_to_create_a_s,
                        LMFeedCommunityUtil.getPostVariable()
                            .pluralizeOrCapitalize(LMFeedWordAction.ALL_SMALL_SINGULAR)
                    )
                )
            }
        }
    }

    //customizes the header view
    protected open fun customizeSocialFeedHeaderView(headerViewSocial: LMFeedHeaderView) {
        headerViewSocial.apply {
            setStyle(LMFeedStyleTransformer.socialFeedFragmentViewStyle.headerViewStyle)

            setTitleText(getString(R.string.lm_feed_feed))
        }
    }

    //customizes the universal feed list view
    protected open fun customizeSocialFeedListView(rvUniversal: LMFeedSocialFeedListView) {
    }

    //processes the user profile clicked
    protected open fun onUserProfileClicked(userViewData: LMFeedUserViewData) {
        val coreCallback = LMFeedCoreApplication.getLMFeedCoreCallback()
        coreCallback?.openProfile(userViewData)
    }

    //processes the notification icon clicked
    protected open fun onNotificationIconClicked() {
        LMFeedAnalytics.sendNotificationPageOpenedEvent()
        LMFeedActivityFeedActivity.start(requireContext())
    }

    //processes the search icon clicked
    protected open fun onSearchIconClicked() {
        val searchExtras = LMFeedSearchExtras.Builder()
            .searchType(SearchType.TEXT)
            .build()

        LMFeedSearchActivity.start(requireContext(), searchExtras)
    }

    //customizes the no post layout
    protected open fun customizeNoPostLayout(layoutNoPost: LMFeedNoEntityLayoutView) {
        layoutNoPost.apply {
            val postAsVariable = LMFeedCommunityUtil.getPostVariable()

            setStyle(LMFeedStyleTransformer.socialFeedFragmentViewStyle.noPostLayoutViewStyle)

            setTitleText(
                getString(
                    R.string.lm_feed_no_s_to_show,
                    postAsVariable.pluralizeOrCapitalize(LMFeedWordAction.FIRST_LETTER_CAPITAL_SINGULAR)
                )
            )
            setSubtitleText(
                getString(
                    R.string.lm_feed_be_the_first_one_to_s_here,
                    postAsVariable.pluralizeOrCapitalize(LMFeedWordAction.FIRST_LETTER_CAPITAL_SINGULAR)
                )
            )
            setActionCTAText(
                getString(
                    R.string.lm_feed_new_s,
                    postAsVariable.pluralizeOrCapitalize(LMFeedWordAction.ALL_CAPITAL_SINGULAR)
                )
            )
        }
    }

    //customizes the posting layout
    protected open fun customizePostingLayout(layoutPosting: LMFeedPostingView) {
        layoutPosting.apply {
            setStyle(LMFeedStyleTransformer.socialFeedFragmentViewStyle.postingViewStyle)

            setPostingText(
                getString(
                    R.string.lm_feed_creating_s,
                    LMFeedCommunityUtil.getPostVariable()
                        .pluralizeOrCapitalize(LMFeedWordAction.FIRST_LETTER_CAPITAL_SINGULAR)
                )
            )
            setRetryCTAText(getString(R.string.lm_feed_retry))
        }
    }

    //customizes the topic selector bar
    protected open fun customizeTopicSelectorBar(topicSelectorBar: LMFeedTopicSelectorBarView) {
        topicSelectorBar.apply {
            setStyle(LMFeedStyleTransformer.socialFeedFragmentViewStyle.topicSelectorBarStyle)

            setAllTopicsText(getString(R.string.lm_feed_all_topics))
            setClearTopicsText(getString(R.string.lm_feed_clear))
        }
    }

    protected open fun onRetryUploadClicked(temporaryId: Long?, attachmentCount: Int) {
        socialFeedViewModel.postViewModel.createRetryPostMediaWorker(
            requireContext(),
            temporaryId,
            attachmentCount
        )
    }

    private val topicSelectionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val bundle = result.data?.extras
                val resultExtras = LMFeedExtrasUtil.getParcelable(
                    bundle,
                    LM_FEED_TOPIC_SELECTION_RESULT_EXTRAS,
                    LMFeedTopicSelectionResultExtras::class.java
                ) ?: return@registerForActivityResult

                handleTopicSelectionResult(resultExtras)
            }
        }

    //handles result after selecting filters and show recyclers views
    private fun handleTopicSelectionResult(resultExtras: LMFeedTopicSelectionResultExtras) {
        binding.apply {
            rvSocial.resetPaginationScrollListenerData()
            rvSocial.clearPostsAndNotify()

            if (resultExtras.isAllTopicSelected) {
                //show layouts accordingly
                topicSelectorBar.setAllTopicsTextVisibility(true)
                topicSelectorBar.setSelectedTopicFilterVisibility(false)

                //call api
                LMFeedProgressBarHelper.showProgress(progressBar, true)
                socialFeedViewModel.postViewModel.getUniversalFeed(1, null)
            } else {
                //show layouts accordingly
                topicSelectorBar.setAllTopicsTextVisibility(false)
                topicSelectorBar.setSelectedTopicFilterVisibility(true)

                //set selected topics to filter
                val selectedTopics = resultExtras.selectedTopics
                topicSelectorBar.replaceSelectedTopics(selectedTopics)

                //call api
                LMFeedProgressBarHelper.showProgress(progressBar, true)
                socialFeedViewModel.postViewModel.getUniversalFeed(
                    1,
                    socialFeedViewModel.helperViewModel.getTopicIdsFromAdapterList(selectedTopics)
                )
            }
        }
    }

    //processes the all topics view click
    protected open fun onAllTopicsClicked() {
        //show topics selecting screen with All topic filter
        val intent = LMFeedTopicSelectionActivity.getIntent(
            requireContext(),
            LMFeedTopicSelectionExtras.Builder()
                .showAllTopicFilter(true)
                .showEnabledTopicOnly(false)
                .build()
        )

        topicSelectionLauncher.launch(intent)
    }

    //processes the feed refreshed event
    protected open fun onFeedRefreshed() {
        binding.apply {
            mSwipeRefreshLayout.isRefreshing = true
            rvSocial.resetPaginationScrollListenerData()
            socialFeedViewModel.helperViewModel.getUnreadNotificationCount()
            when (feedType) {
                PERSONALISED_FEED -> {
                    rvSocial.resetScrollStateListenerData()
                    socialFeedViewModel.postViewModel.getPersonalisedFeed(
                        page = 1,
                        shouldReorder = true,
                        shouldRecompute = true
                    )
                    socialFeedViewModel.helperViewModel.postSeen()
                }

                UNIVERSAL_FEED -> {
                    socialFeedViewModel.postViewModel.getUniversalFeed(
                        1,
                        socialFeedViewModel.helperViewModel.getTopicIdsFromAdapterList(
                            topicSelectorBar.getAllSelectedTopics()
                        )
                    )
                }
            }
        }
    }

    //callback when post menu items are clicked
    protected open fun onPostMenuItemClicked(
        position: Int,
        menuId: Int,
        postViewData: LMFeedPostViewData
    ) {
        when (menuId) {
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
                    onPinPostMenuClicked(
                        position,
                        menuId,
                        it
                    )
                }
            }

            UNPIN_POST_MENU_ITEM_ID -> {
                val updatedPost =
                    LMFeedPostBinderUtils.updatePostForUnpin(requireContext(), postViewData)

                updatedPost?.let {
                    onUnpinPostMenuClicked(
                        position,
                        menuId,
                        it
                    )
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
        val editPostExtras = LMFeedEditPostExtras.Builder()
            .postId(post.id)
            .build()
        val intent = LMFeedEditPostActivity.getIntent(requireContext(), editPostExtras)
        startActivity(intent)
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

    //processes the pin post menu click
    protected open fun onPinPostMenuClicked(
        position: Int,
        menuId: Int,
        post: LMFeedPostViewData
    ) {
        //call api
        socialFeedViewModel.helperViewModel.pinPost(post)

        binding.rvSocial.apply {
            val adapterPosition = getIndexAndPostFromAdapter(post.id)?.first ?: return

            //update recycler
            updatePostItem(adapterPosition, post)
        }
    }

    //processes the unpin post menu click
    protected open fun onUnpinPostMenuClicked(
        position: Int,
        menuId: Int,
        post: LMFeedPostViewData
    ) {
        //call api
        socialFeedViewModel.helperViewModel.pinPost(post)

        binding.rvSocial.apply {
            val adapterPosition = getIndexAndPostFromAdapter(post.id)?.first ?: return

            //update recycler
            updatePostItem(adapterPosition, post)
        }
    }
}
