package com.likeminds.feed.android.core.post.detail.view

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.likeminds.feed.android.core.*
import com.likeminds.feed.android.core.databinding.LmFeedFragmentPostDetailBinding
import com.likeminds.feed.android.core.delete.model.*
import com.likeminds.feed.android.core.delete.view.*
import com.likeminds.feed.android.core.likes.model.*
import com.likeminds.feed.android.core.likes.view.LMFeedLikesActivity
import com.likeminds.feed.android.core.poll.result.model.*
import com.likeminds.feed.android.core.poll.result.view.LMFeedPollResultsActivity
import com.likeminds.feed.android.core.post.detail.adapter.LMFeedPostDetailAdapterListener
import com.likeminds.feed.android.core.post.detail.adapter.LMFeedReplyAdapterListener
import com.likeminds.feed.android.core.post.detail.model.*
import com.likeminds.feed.android.core.post.detail.view.LMFeedPostDetailActivity.Companion.LM_FEED_POST_DETAIL_EXTRAS
import com.likeminds.feed.android.core.post.detail.viewmodel.LMFeedPostDetailViewModel
import com.likeminds.feed.android.core.post.edit.model.LMFeedEditPostExtras
import com.likeminds.feed.android.core.post.edit.view.LMFeedEditPostActivity
import com.likeminds.feed.android.core.post.model.LMFeedAttachmentViewData
import com.likeminds.feed.android.core.post.util.LMFeedPostEvent
import com.likeminds.feed.android.core.postmenu.model.*
import com.likeminds.feed.android.core.report.model.*
import com.likeminds.feed.android.core.report.view.*
import com.likeminds.feed.android.core.socialfeed.adapter.LMFeedPostAdapterListener
import com.likeminds.feed.android.core.socialfeed.model.LMFeedPostViewData
import com.likeminds.feed.android.core.socialfeed.util.LMFeedPostBinderUtils
import com.likeminds.feed.android.core.ui.theme.LMFeedAppearance
import com.likeminds.feed.android.core.ui.widgets.comment.commentcomposer.view.LMFeedCommentComposerView
import com.likeminds.feed.android.core.ui.widgets.headerview.view.LMFeedHeaderView
import com.likeminds.feed.android.core.ui.widgets.overflowmenu.view.LMFeedOverflowMenu
import com.likeminds.feed.android.core.ui.widgets.poll.model.LMFeedAddPollOptionExtras
import com.likeminds.feed.android.core.ui.widgets.poll.view.*
import com.likeminds.feed.android.core.utils.*
import com.likeminds.feed.android.core.utils.LMFeedValueUtils.pluralizeOrCapitalize
import com.likeminds.feed.android.core.utils.analytics.LMFeedAnalytics
import com.likeminds.feed.android.core.utils.base.LMFeedBaseViewType
import com.likeminds.feed.android.core.utils.coroutine.observeInLifecycle
import com.likeminds.feed.android.core.utils.membertagging.MemberTaggingUtil
import com.likeminds.feed.android.core.utils.pluralize.model.LMFeedWordAction
import com.likeminds.feed.android.core.utils.user.LMFeedUserMetaData
import com.likeminds.feed.android.core.utils.user.LMFeedUserPreferences
import com.likeminds.likemindsfeed.post.model.PollMultiSelectState
import com.likeminds.usertagging.UserTagging
import com.likeminds.usertagging.model.UserTaggingConfig
import com.likeminds.usertagging.util.UserTaggingDecoder
import com.likeminds.usertagging.util.UserTaggingViewListener
import com.likeminds.usertagging.view.UserTaggingSuggestionListView
import kotlinx.coroutines.flow.onEach

open class LMFeedPostDetailFragment :
    Fragment(),
    LMFeedPostAdapterListener,
    LMFeedPostDetailAdapterListener,
    LMFeedReplyAdapterListener,
    LMFeedAdminDeleteDialogListener,
    LMFeedAddPollOptionBottomSheetListener,
    LMFeedSelfDeleteDialogListener {

    private lateinit var binding: LmFeedFragmentPostDetailBinding
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var memberTagging: UserTaggingSuggestionListView

    private lateinit var postDetailExtras: LMFeedPostDetailExtras

    private val postDetailViewModel: LMFeedPostDetailViewModel by viewModels()

    private var parentCommentIdToReply: String? = null
    private var toFindComment: Boolean = false

    // fixed position of viewTypes in adapter
    private val postDataPosition = 0
    private val commentsCountPosition = 1
    private val commentsStartPosition = 2

    // variables to handle comment/reply edit action
    private var editCommentId: String? = null
    private var parentId: String? = null

    // [postPublisher] to publish changes in the post
    private val postEvent by lazy {
        LMFeedPostEvent.getPublisher()
    }

    companion object {
        const val TAG = "LMFeedPostDetailFragment"

        fun getInstance(postDetailExtras: LMFeedPostDetailExtras): LMFeedPostDetailFragment {
            val postDetailFragment = LMFeedNavigationFragments.getInstance().getPostDetailFragment()
            val bundle = Bundle()
            bundle.putParcelable(LM_FEED_POST_DETAIL_EXTRAS, postDetailExtras)
            postDetailFragment.arguments = bundle
            return postDetailFragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LmFeedFragmentPostDetailBinding.inflate(layoutInflater)
        binding.rvPostDetails.initAdapterAndSetListeners(this, this, this)

        customizePostDetailHeaderView(binding.headerViewPostDetail)
        customizeCommentComposer(binding.commentComposer)
        customizePostDetailListView(binding.rvPostDetails)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        receiveExtras()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
        fetchData()
        initCommentEditText()
        initListeners()
        observeData()
    }

    override fun onResume() {
        super.onResume()
        binding.rvPostDetails.initiateVideoAutoPlayer()
    }

    override fun onPause() {
        super.onPause()
        binding.rvPostDetails.destroyVideoAutoPlayer()
    }

    //customize post detail header view
    protected open fun customizePostDetailHeaderView(headerViewPostDetail: LMFeedHeaderView) {
        headerViewPostDetail.apply {
            setStyle(LMFeedStyleTransformer.postDetailFragmentViewStyle.headerViewStyle)
            LMFeedViewUtils.setStatusBarColor(
                requireActivity(),
                LMFeedStyleTransformer.postDetailFragmentViewStyle.headerViewStyle.backgroundColor
            )

            setTitleText(
                getString(
                    R.string.lm_feed_s_post,
                    LMFeedCommunityUtil.getPostVariable()
                        .pluralizeOrCapitalize(LMFeedWordAction.FIRST_LETTER_CAPITAL_SINGULAR)
                )
            )
        }
    }

    //customize comment composer
    protected open fun customizeCommentComposer(commentComposer: LMFeedCommentComposerView) {
        commentComposer.apply {
            setCommentInputBoxHint(
                getString(
                    R.string.lm_feed_write_s_comment,
                    LMFeedCommunityUtil.getCommentVariable()
                        .pluralizeOrCapitalize(LMFeedWordAction.ALL_SMALL_SINGULAR)
                )
            )
            setStyle(LMFeedStyleTransformer.postDetailFragmentViewStyle.commentComposerStyle)
        }
    }

    //customize post detail list view
    protected open fun customizePostDetailListView(rvPostDetailListView: LMFeedPostDetailListView) {

    }

    private fun receiveExtras() {
        postDetailExtras = LMFeedExtrasUtil.getParcelable(
            arguments,
            LM_FEED_POST_DETAIL_EXTRAS,
            LMFeedPostDetailExtras::class.java
        ) ?: throw emptyExtrasException(TAG)
    }

    private fun initUI() {
        initPostDetailRecyclerView()
        initMemberTaggingView()
        initSwipeRefreshLayout()
    }

    private fun initPostDetailRecyclerView() {
        fetchPostData()
        binding.rvPostDetails.apply {
            setAdapter()

            //set scroll listener
            val paginationScrollListener =
                object : LMFeedEndlessRecyclerViewScrollListener(linearLayoutManager) {
                    override fun onLoadMore(currentPage: Int) {
                        if (currentPage > 0) {
                            postDetailViewModel.getPost(postDetailExtras.postId, currentPage)
                        }
                    }
                }
            setPaginationScrollListener(paginationScrollListener)
        }
    }

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
                refreshPostData()
            }
        }
    }

    //initializes member tagging view
    private fun initMemberTaggingView() {
        memberTagging = binding.userTaggingView

        val listener = object : UserTaggingViewListener {
            override fun callApi(page: Int, searchName: String) {
                postDetailViewModel.getMembersForTagging(page, searchName)
            }
        }

        val config = UserTaggingConfig.Builder()
            .editText(binding.commentComposer.etComment)
            .maxHeightInPercentage(0.4f)
            .color(LMFeedAppearance.getTextLinkColor())
            .hasAtRateSymbol(true)
            .build()

        UserTagging.initialize(memberTagging, config, listener)
    }

    // refreshes the whole post detail screen
    private fun refreshPostData() {
        mSwipeRefreshLayout.isRefreshing = true
        binding.rvPostDetails.resetScrollListenerData()
        fetchPostData(true)
    }

    private fun fetchData() {
        fetchPostData()
        checkCommentsRight()
    }

    //fetches post data to set initial data
    private fun fetchPostData(fromRefresh: Boolean = false) {
        if (!fromRefresh) {
            //show progress bar
            LMFeedProgressBarHelper.showProgress(binding.progressBar)
        }

        //if source is notification/deep link, then call initiate first and then other apis
        if (postDetailExtras.source == LMFeedAnalytics.LMFeedSource.NOTIFICATION ||
            postDetailExtras.source == LMFeedAnalytics.LMFeedSource.DEEP_LINK
        ) {
            LMFeedCore.showFeed(
                context = requireContext(),
                accessToken = null,
                refreshToken = null,
                success = {
                    postDetailViewModel.getPost(postDetailExtras.postId, 1)
                },
                error = {
                    LMFeedProgressBarHelper.hideProgress(binding.progressBar)
                    LMFeedViewUtils.showErrorMessageToast(requireContext(), it)
                }
            )
        } else {
            postDetailViewModel.getPost(postDetailExtras.postId, 1)
        }
    }

    //check if user has comment rights or not
    private fun checkCommentsRight() {
        postDetailViewModel.checkCommentRights()
    }

    // initializes comment edittext with TextWatcher and focuses the keyboard
    private fun initCommentEditText() {
        binding.commentComposer.apply {
            if (postDetailExtras.isEditTextFocused) {
                etComment.focusAndShowKeyboard()
            }

            etComment.doAfterTextChanged {
                if (it?.trim().isNullOrEmpty()) {
                    setCommentSendButton(false)
                } else {
                    setCommentSendButton(true)
                }
            }
        }
    }

    // initializes text-watcher and click listeners
    private fun initListeners() {
        binding.apply {
            headerViewPostDetail.setNavigationIconClickListener {
                onNavigationIconClick()
            }

            commentComposer.setCommentSendClickListener {
                val text = commentComposer.etComment.text
                val updatedText = memberTagging.replaceSelectedMembers(text).trim()
                val postId = postDetailExtras.postId
                when {
                    parentCommentIdToReply != null -> {
                        addReply(updatedText)
                    }

                    editCommentId != null -> {
                        editCommentLocally(updatedText)
                    }

                    else -> {
                        //input text is a comment
                        addComment(postId, updatedText)
                    }
                }
                LMFeedViewUtils.hideKeyboard(commentComposer)
                commentComposer.etComment.text = null
            }
            commentComposer.setCommentSendButton(false)

            commentComposer.setRemoveReplyingToClickListener {
                hideReplyingToView()
            }
        }
    }

    //processes the navigation icon click
    protected open fun onNavigationIconClick() {
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    // adds the comment locally and calls api
    private fun addComment(postId: String, updatedText: String) {
        binding.rvPostDetails.apply {
            val createdAt = System.currentTimeMillis()
            val tempId = "-${createdAt}"

            // calls api
            postDetailViewModel.addComment(postId, tempId, updatedText)

            val userPreferences = LMFeedUserPreferences(requireContext())
            val creatorUserName = userPreferences.getUserName()

            // adds comment locally
            val commentViewData = postDetailViewModel.getCommentViewDataForLocalHandling(
                postId,
                creatorUserName,
                createdAt,
                tempId,
                updatedText,
                null
            )

            // remove NoCommentsViewData if visible
            if (getItem(commentsCountPosition) is LMFeedNoCommentsViewData) {
                removeItem(commentsCountPosition)
            }
            // gets old [CommentsCountViewData] from adapter
            if (getItem(commentsCountPosition) != null) {
                val oldCommentsCountViewData =
                    (getItem(commentsCountPosition) as LMFeedCommentsCountViewData)

                // updates old [CommentsCountViewData] by adding to [commentsCount]
                val updatedCommentsCountViewData = oldCommentsCountViewData.toBuilder()
                    .commentsCount(oldCommentsCountViewData.commentsCount + 1)
                    .build()

                //updates [CommentsCountViewData]
                updateItem(commentsCountPosition, updatedCommentsCountViewData)
            } else {
                // creates new [CommentsCountViewData] when the added comment is first
                val newCommentsCountViewData = LMFeedCommentsCountViewData.Builder()
                    .commentsCount(1)
                    .build()
                addItem(commentsCountPosition, newCommentsCountViewData)
            }

            // gets post from adapter
            var post = getItem(postDataPosition) as LMFeedPostViewData

            //update the action view data
            val updatedActionViewData = post.actionViewData.toBuilder()
                .commentsCount(post.actionViewData.commentsCount + 1)
                .build()

            //updated the post
            post = post.toBuilder()
                .actionViewData(updatedActionViewData)
                .build()

            // notifies the subscribers about the change in post data
            postEvent.notify(Pair(post.id, post))

            // updates comments count on header
            updateCommentsCount(post.actionViewData.commentsCount)

            //adds new comment to adapter
            addItem(commentsStartPosition, commentViewData)

            //scroll to comment's position
            scrollToPositionWithOffset(commentsStartPosition, 75)

            //updates comment data in post
            updateItem(postDataPosition, post)
        }
    }

    // adds the reply locally and calls api
    private fun addReply(updatedText: String) {
        binding.rvPostDetails.apply {
            val createdAt = System.currentTimeMillis()
            val tempId = "-${createdAt}"
            val postId = postDetailExtras.postId

            // input text is reply to a comment
            val parentCommentId = parentCommentIdToReply ?: return
            val parentComment = getIndexAndCommentFromAdapter(parentCommentId)?.second
                ?: return
            val parentCommentCreatorUUID = parentComment.user.sdkClientInfoViewData.uuid
            postDetailViewModel.replyComment(
                parentCommentCreatorUUID,
                postId,
                parentCommentId,
                updatedText,
                tempId
            )
            hideReplyingToView()

            val userPreferences = LMFeedUserPreferences(requireContext())
            val creatorUserName = userPreferences.getUserName()

            // view data of comment with level-1
            val replyViewData = postDetailViewModel.getCommentViewDataForLocalHandling(
                postId,
                creatorUserName,
                createdAt,
                tempId,
                updatedText,
                parentCommentId,
                level = 1
            )

            //adds reply to the adapter
            addReplyToAdapter(parentCommentId, replyViewData)
        }
    }

    // edits the comment locally and calls api
    private fun editCommentLocally(updatedText: String) {
        // when an existing comment is edited
        val commentId = editCommentId ?: return

        // calls api
        postDetailViewModel.editComment(
            postDetailExtras.postId,
            commentId,
            updatedText
        )

        binding.rvPostDetails.apply {

            if (parentId == null) {
                // edited comment is of level-0

                val pair = getIndexAndCommentFromAdapter(commentId) ?: return
                val commentPosition = pair.first
                val comment = pair.second

                //update comment view data
                val updatedComment = comment.toBuilder()
                    .fromCommentLiked(false)
                    .fromCommentEdited(true)
                    .isEdited(true)
                    .text(updatedText)
                    .build()

                updateItem(commentPosition, updatedComment)
            } else {
                // edited comment is of level-1 (reply)

                val pair = getIndexAndCommentFromAdapter(parentId ?: "") ?: return
                val parentIndex = pair.first
                val parentCommentInAdapter = pair.second

                // finds index of the reply inside the comment
                val replyPair =
                    getIndexAndReplyFromComment(parentCommentInAdapter, commentId) ?: return

                val index = replyPair.first
                val reply = replyPair.second.toBuilder()
                    .isEdited(true)
                    .text(updatedText)
                    .fromCommentEdited(true)
                    .build()

                if (index == -1) return

                parentCommentInAdapter.replies[index] = reply

                val newViewData = parentCommentInAdapter.toBuilder()
                    .fromCommentLiked(false)
                    .fromCommentEdited(false)
                    .build()

                //updates the parentComment with edited reply
                updateItem(parentIndex, newViewData)
            }
        }
        editCommentId = null
        parentId = null
    }

    // adds the reply to its parentComment
    private fun addReplyToAdapter(parentCommentId: String, reply: LMFeedCommentViewData) {
        binding.rvPostDetails.apply {
            // gets the parentComment from adapter
            val parentComment = getIndexAndCommentFromAdapter(parentCommentId) ?: return
            val parentIndex = parentComment.first
            val parentCommentViewData = parentComment.second

            // adds the reply at first
            parentCommentViewData.replies.add(0, reply)

            val newCommentViewData = parentCommentViewData.toBuilder()
                .fromCommentLiked(false)
                .fromCommentEdited(false)
                .repliesCount(parentCommentViewData.repliesCount + 1)
                .build()

            //updates the parentComment with added reply
            updateItem(parentIndex, newCommentViewData)

            // scroll to comment's position
            scrollToPositionWithOffset(parentIndex, 75)
        }
    }

    // hides the replying to view
    private fun hideReplyingToView() {
        binding.commentComposer.apply {
            parentCommentIdToReply = null
            setReplyingView("")
        }
    }

    // updates the comments count on toolbar
    private fun updateCommentsCount(commentsCount: Int) {
        val commentString = if (commentsCount == 1) {
            LMFeedCommunityUtil.getCommentVariable()
                .pluralizeOrCapitalize(LMFeedWordAction.ALL_SMALL_SINGULAR)
        } else {
            LMFeedCommunityUtil.getCommentVariable()
                .pluralizeOrCapitalize(LMFeedWordAction.ALL_SMALL_PLURAL)
        }

        binding.headerViewPostDetail.setSubTitleText(
            resources.getQuantityString(
                R.plurals.lm_feed_s_comments_small,
                commentsCount,
                commentsCount,
                commentString
            )
        )
    }

    private fun observeData() {
        observePostData()
        observeCommentData()
        observeCommentsRightData()
        observeMembersTaggingList()
        observeErrors()
    }

    //observes live data related to post
    private fun observePostData() {
        // observes postResponse live data
        postDetailViewModel.postResponse.observe(viewLifecycleOwner) { pair ->
            //hide progress bar
            LMFeedProgressBarHelper.hideProgress(binding.progressBar)
            //page in sent in api
            val page = pair.first

            // post data
            val post = pair.second

            // notifies the subscribers about the change in post data
            postEvent.notify(Pair(post.id, post))

            // update the comments count
            updateCommentsCount(post.actionViewData.commentsCount)

            //if pull to refresh is called
            if (mSwipeRefreshLayout.isRefreshing) {
                setPostDataAndScrollToTop(post)
                mSwipeRefreshLayout.isRefreshing = false
                return@observe
            }

            //normal adding
            if (page == 1) {
                setPostDataAndScrollToTop(post)
            } else {
                updatePostAndAddComments(post)
                binding.rvPostDetails.refreshVideoAutoPlayer()
            }
        }

        postDetailViewModel.getPostResponse.observe(viewLifecycleOwner) { postViewData ->
            binding.rvPostDetails.updateItem(postDataPosition, postViewData)

            //notifies the subscribers about the change in post data
            postEvent.notify(Pair(postViewData.id, postViewData))
        }

        // observes deletePostResponse LiveData
        postDetailViewModel.deletePostResponse.observe(viewLifecycleOwner) {
            // notifies the subscribers about the deletion of post
            postEvent.notify(Pair(postDetailExtras.postId, null))

            LMFeedViewUtils.showShortToast(
                requireContext(),
                getString(
                    R.string.lm_feed_s_deleted,
                    LMFeedCommunityUtil.getPostVariable()
                        .pluralizeOrCapitalize(LMFeedWordAction.FIRST_LETTER_CAPITAL_SINGULAR)
                )
            )
            requireActivity().finish()
        }

        //observes postSavedResponse LiveData
        postDetailViewModel.postSavedResponse.observe(viewLifecycleOwner) { postViewData ->
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

        //observes pinPostResponse LiveData
        postDetailViewModel.postPinnedResponse.observe(viewLifecycleOwner) { postViewData ->
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
    }

    // sets page-1 data of post and scrolls to top
    private fun setPostDataAndScrollToTop(post: LMFeedPostViewData) {
        binding.rvPostDetails.apply {
            // [ArrayList] to add all the items to adapter
            val postDetailList = ArrayList<LMFeedBaseViewType>()
            // adds the post data at [postDataPosition]
            postDetailList.add(postDataPosition, post)

            if (post.actionViewData.commentsCount == 0) {
                // adds no comments view data
                val noCommentViewData = LMFeedNoCommentsViewData.Builder().build()
                postDetailList.add(noCommentViewData)
            } else {
                // adds commentsCountViewData if comments are present
                postDetailList.add(
                    commentsCountPosition,
                    LMFeedViewDataConvertor.convertCommentsCount(post.actionViewData.commentsCount)
                )
            }

            val comments = post.actionViewData.replies.toList()
            // adds all the comments to the [postDetailList]
            postDetailList.addAll(comments)
            replaceItems(postDetailList)

            if (toFindComment) {
                //find the comments already present in adapter
                postDetailExtras.commentId?.let { commentId ->
                    val index = getIndexAndCommentFromAdapter(commentId)?.first ?: -1

                    //comment not present -> get it from api
                    if (index == -1) {
                        postDetailViewModel.getComment(post.id, postDetailExtras.commentId ?: "", 1)
                    } else {
                        //scroll to that comment
                        scrollToPositionWithOffset(index, 0)
                    }
                }
            } else {
                scrollToPositionWithOffset(postDataPosition, 0)
            }
            refreshVideoAutoPlayer()
        }
    }

    // updates the post and add comments to adapter
    private fun updatePostAndAddComments(post: LMFeedPostViewData) {
        // notifies the subscribers about the change in post data
        postEvent.notify(Pair(post.id, post))

        binding.rvPostDetails.apply {
            // updates the post
            updateItem(postDataPosition, post)
            // adds the paginated comments
            addItems(post.actionViewData.replies.toList())
        }
    }

    private fun observeCommentData() {
        binding.rvPostDetails.apply {

            // observes addCommentResponse LiveData
            postDetailViewModel.addCommentResponse.observe(viewLifecycleOwner) { comment ->
                val index =
                    getIndexAndCommentFromAdapterUsingTempId(comment.tempId)?.first
                        ?: return@observe

                if (getItem(index) is LMFeedCommentViewData) {
                    updateItem(index, comment)
                }
            }

            // observes editCommentResponse LiveData
            postDetailViewModel.editCommentResponse.observe(viewLifecycleOwner) { comment ->
                editCommentInAdapter(comment)
            }

            // observes addReplyResponse LiveData
            postDetailViewModel.addReplyResponse.observe(viewLifecycleOwner) { pair ->
                // [parentCommentId] for the reply
                val parentCommentId = pair.first

                // view data of comment with level-1
                val replyViewData = pair.second

                // gets the parentComment from adapter
                val parentComment = getIndexAndCommentFromAdapter(parentCommentId) ?: return@observe
                val parentIndex = parentComment.first
                val parentCommentViewData = parentComment.second

                val replyIndex = parentCommentViewData.replies.indexOfFirst {
                    it.tempId == replyViewData.tempId
                }

                if (replyIndex != -1) {
                    parentCommentViewData.replies[replyIndex] = replyViewData
                    val newCommentViewData = parentCommentViewData.toBuilder()
                        .fromCommentLiked(false)
                        .fromCommentEdited(false)
                        .build()
                    updateItem(parentIndex, newCommentViewData)
                }
            }
        }

        // observes deleteCommentResponse LiveData
        postDetailViewModel.deleteCommentResponse.observe(viewLifecycleOwner) { pair ->
            val commentId = pair.first
            val parentCommentId = pair.second

            // level-0 comment
            if (parentCommentId == null) {
                removeCommentFromAdapter(commentId)
            } else {
                // level-1 comment
                removeReplyFromAdapter(parentCommentId, commentId)
            }
        }

        postDetailViewModel.getCommentResponse.observe(viewLifecycleOwner) { pair ->
            //page in api send
            val page = pair.first

            //comment data
            val comment = pair.second

            // adds paginated replies to adapter
            addReplies(comment, page)
        }
    }

    // finds and edits comment/reply in the adapter
    private fun editCommentInAdapter(comment: LMFeedCommentViewData) {
        binding.rvPostDetails.apply {
            val parentComment = comment.parentComment
            if (parentComment == null) {
                // edited comment is of level-0

                val commentPosition = getIndexAndCommentFromAdapter(comment.id)?.first ?: return

                //update comment view data
                val updatedComment = comment.toBuilder()
                    .fromCommentLiked(false)
                    .fromCommentEdited(true)
                    .build()

                updateItem(commentPosition, updatedComment)
            } else {
                // edited comment is of level-1 (reply)

                val parentCommentId = parentComment.id
                val pair = getIndexAndCommentFromAdapter(parentCommentId) ?: return
                val parentIndex = pair.first
                val parentCommentInAdapter = pair.second

                // finds index of the reply inside the comment
                val index =
                    getIndexAndReplyFromComment(parentCommentInAdapter, comment.id)?.first ?: return

                if (index == -1) return

                parentCommentInAdapter.replies[index] = comment

                val newViewData = parentCommentInAdapter.toBuilder()
                    .fromCommentLiked(false)
                    .fromCommentEdited(false)
                    .build()

                // updates the parentComment with edited reply
                updateItem(parentIndex, newViewData)
            }
        }
    }

    //removes a comment from the adapter and shows no comment view if required
    private fun removeCommentFromAdapter(commentId: String, isLocal: Boolean = false) {
        binding.rvPostDetails.apply {
            // gets old [CommentsCountViewData] from adapter
            val oldCommentsCountViewData =
                (getItem(commentsCountPosition) as LMFeedCommentsCountViewData)

            // creates new [CommentsCountViewData] by adding to [commentsCount]
            val newCommentsCountViewData = oldCommentsCountViewData.toBuilder()
                .commentsCount(oldCommentsCountViewData.commentsCount - 1)
                .build()

            // updates [CommentsCountViewData]
            updateItem(commentsCountPosition, newCommentsCountViewData)

            var post = getItem(postDataPosition) as LMFeedPostViewData

            //update the action view data
            val updatedActionViewData = post.actionViewData.toBuilder()
                .commentsCount(newCommentsCountViewData.commentsCount)
                .build()

            //updated the post
            post = post.toBuilder()
                .actionViewData(updatedActionViewData)
                .build()

            // notifies the subscribers about the change in post data
            postEvent.notify(Pair(post.id, post))

            //update the comments count in the header
            updateCommentsCount(newCommentsCountViewData.commentsCount)

            //updates comment data in post
            updateItem(postDataPosition, post)

            // get the deleted comment from the adapter
            val indexToRemove =
                getIndexAndCommentFromAdapter(commentId)?.first ?: return

            // removes the deleted comment from the adapter
            removeItem(indexToRemove)

            if (!isLocal) {
                LMFeedViewUtils.showShortToast(
                    requireContext(),
                    getString(
                        R.string.lm_feed_s_comment_deleted,
                        LMFeedCommunityUtil.getCommentVariable()
                            .pluralizeOrCapitalize(LMFeedWordAction.FIRST_LETTER_CAPITAL_SINGULAR)
                    )
                )
            }

            if (newCommentsCountViewData.commentsCount == 0) {
                removeItem(commentsCountPosition)
                // adds no comments view data to adapter
                val noCommentViewData = LMFeedNoCommentsViewData.Builder().build()
                addItem(noCommentViewData)
            }
        }
    }

    // removes the reply from its parentComment
    private fun removeReplyFromAdapter(parentCommentId: String, replyId: String) {
        binding.rvPostDetails.apply {
            // gets the parentComment from adapter
            val parentComment = getIndexAndCommentFromAdapter(parentCommentId) ?: return
            val parentIndex = parentComment.first
            val parentCommentViewData = parentComment.second

            // removes the reply with specified replyId
            parentCommentViewData.replies.removeIf {
                it.id == replyId
            }

            val newCommentViewData = parentCommentViewData.toBuilder()
                .fromCommentLiked(false)
                .fromCommentEdited(false)
                .repliesCount(parentCommentViewData.repliesCount - 1)
                .build()

            // updates the parentComment with removed reply
            updateItem(parentIndex, newCommentViewData)
        }
    }

    // adds paginated replies to comment
    private fun addReplies(comment: LMFeedCommentViewData, page: Int) {
        binding.rvPostDetails.apply {
            // gets comment from adapter
            val indexAndComment = getIndexAndCommentFromAdapter(comment.id)

            //if comment is not present in adapter
            if (indexAndComment == null) {
                //set to false because comment is added
                toFindComment = false
                //add comment to adapter
                addItem(commentsStartPosition, comment)
                //scroll to the comment
                scrollToPositionWithOffset(commentsStartPosition, 0)
            } else {
                val index = indexAndComment.first
                val adapterComment = indexAndComment.second
                if (page == 1) {
                    // updates the comment with page-1 replies
                    updateItem(index, comment)
                    scrollToPositionWithOffset(index, 75)
                } else {
                    // adds replies in adapter and fetched replies
                    comment.replies.addAll(
                        0,
                        adapterComment.replies
                    )
                    updateItem(index, comment)
                    scrollToPositionWithOffset(index + 1, 150)
                }
            }
        }
    }

    //observes hasCommentRights live data
    private fun observeCommentsRightData() {
        postDetailViewModel.hasCommentRights.observe(viewLifecycleOwner) {
            //if source is notification/deep link, don't update comments right from here
            if (postDetailExtras.source != LMFeedAnalytics.LMFeedSource.NOTIFICATION &&
                postDetailExtras.source != LMFeedAnalytics.LMFeedSource.DEEP_LINK
            ) {
                binding.commentComposer.setCommentRights(it)
            }
        }
    }

    private fun observeMembersTaggingList() {
        postDetailViewModel.taggingData.observe(viewLifecycleOwner) { result ->
            MemberTaggingUtil.setMembersInView(memberTagging, result)
        }
    }

    //observes error events
    private fun observeErrors() {
        binding.rvPostDetails.apply {
            postDetailViewModel.errorMessageEventFlow.onEach { response ->
                when (response) {
                    is LMFeedPostDetailViewModel.ErrorMessageEvent.GetPost -> {
                        mSwipeRefreshLayout.isRefreshing = false
                        LMFeedProgressBarHelper.hideProgress(binding.progressBar)
                        LMFeedViewUtils.showErrorMessageToast(
                            requireContext(),
                            response.errorMessage
                        )
                        requireActivity().finish()
                    }

                    is LMFeedPostDetailViewModel.ErrorMessageEvent.LikeComment -> {
                        val commentId = response.commentId

                        //get comment and index
                        val pair = getIndexAndCommentFromAdapter(commentId) ?: return@onEach
                        val comment = pair.second
                        val index = pair.first

                        //update comment view data
                        val updatedComment = comment.toBuilder()
                            .isLiked(false)
                            .fromCommentLiked(true)
                            .fromCommentEdited(false)
                            .likesCount(comment.likesCount - 1)
                            .build()

                        //update recycler view
                        updateItem(index, updatedComment)

                        //show error message
                        LMFeedViewUtils.showErrorMessageToast(
                            requireContext(),
                            response.errorMessage
                        )
                    }

                    is LMFeedPostDetailViewModel.ErrorMessageEvent.AddComment -> {
                        removeCommentFromAdapter(response.tempId, isLocal = true)
                        LMFeedViewUtils.showErrorMessageToast(
                            requireContext(),
                            response.errorMessage
                        )
                    }

                    is LMFeedPostDetailViewModel.ErrorMessageEvent.ReplyComment -> {
                        removeReplyFromAdapter(response.parentCommentId, response.tempId)
                        LMFeedViewUtils.showErrorMessageToast(
                            requireContext(),
                            response.errorMessage
                        )
                    }

                    is LMFeedPostDetailViewModel.ErrorMessageEvent.DeleteComment -> {
                        LMFeedViewUtils.showErrorMessageToast(
                            requireContext(),
                            response.errorMessage
                        )
                    }

                    is LMFeedPostDetailViewModel.ErrorMessageEvent.GetComment -> {
                        LMFeedViewUtils.showErrorMessageToast(
                            requireContext(),
                            response.errorMessage
                        )
                    }

                    is LMFeedPostDetailViewModel.ErrorMessageEvent.EditComment -> {
                        LMFeedViewUtils.showErrorMessageToast(
                            requireContext(),
                            response.errorMessage
                        )
                    }

                    is LMFeedPostDetailViewModel.ErrorMessageEvent.LikePost -> {
                        //get post
                        val post = getItem(postDataPosition) as LMFeedPostViewData

                        //update footer view data
                        val updatedFooterView = post.actionViewData.toBuilder()
                            .isLiked(false)
                            .likesCount(post.actionViewData.likesCount - 1)
                            .build()

                        //update post view data
                        val updatedPost = post.toBuilder()
                            .actionViewData(updatedFooterView)
                            .fromPostLiked(true)
                            .build()

                        postEvent.notify(Pair(updatedPost.id, updatedPost))

                        //update recycler view
                        updateItem(postDataPosition, updatedPost)

                        //show error message
                        val errorMessage = response.errorMessage
                        LMFeedViewUtils.showErrorMessageToast(requireContext(), errorMessage)
                    }

                    is LMFeedPostDetailViewModel.ErrorMessageEvent.SavePost -> {
                        //get post
                        val post = getItem(postDataPosition) as LMFeedPostViewData

                        //update footer view data
                        val updatedFooter = post.actionViewData.toBuilder()
                            .isSaved(false)
                            .build()

                        //update post view data
                        val updatedPost = post.toBuilder()
                            .actionViewData(updatedFooter)
                            .fromPostSaved(true)
                            .build()

                        postEvent.notify(Pair(updatedPost.id, updatedPost))

                        //update recycler view
                        updateItem(postDataPosition, updatedPost)

                        //show error message
                        val errorMessage = response.errorMessage
                        LMFeedViewUtils.showErrorMessageToast(requireContext(), errorMessage)
                    }

                    is LMFeedPostDetailViewModel.ErrorMessageEvent.DeletePost -> {
                        val errorMessage = response.errorMessage
                        LMFeedViewUtils.showErrorMessageToast(requireContext(), errorMessage)
                    }

                    is LMFeedPostDetailViewModel.ErrorMessageEvent.PinPost -> {
                        //get post
                        val post = getItem(postDataPosition) as LMFeedPostViewData

                        val updatedHeader = post.headerViewData.toBuilder()
                            .isPinned(!post.headerViewData.isPinned)
                            .build()

                        //update post view data
                        val updatedPost = post.toBuilder()
                            .headerViewData(updatedHeader)
                            .build()

                        //update recycler view
                        updateItem(postDataPosition, updatedPost)

                        //show error message
                        val errorMessage = response.errorMessage
                        LMFeedViewUtils.showErrorMessageToast(requireContext(), errorMessage)
                    }

                    is LMFeedPostDetailViewModel.ErrorMessageEvent.TaggingList -> {
                        LMFeedViewUtils.showErrorMessageToast(
                            requireContext(),
                            response.errorMessage
                        )
                    }

                    is LMFeedPostDetailViewModel.ErrorMessageEvent.AddPollOption -> {
                        LMFeedViewUtils.showErrorMessageToast(
                            requireContext(),
                            response.errorMessage
                        )
                    }

                    is LMFeedPostDetailViewModel.ErrorMessageEvent.SubmitVote -> {
                        LMFeedViewUtils.showErrorMessageToast(
                            requireContext(),
                            response.errorMessage
                        )
                    }
                }
            }.observeInLifecycle(viewLifecycleOwner)
        }
    }

    //processes edit comment/reply request
    private fun editCommentEntity(comment: LMFeedCommentViewData) {
        binding.apply {
            val parentCommentId = comment.parentId
            // gets text of the comment/reply
            val commentText =
                if (parentCommentId == null) {
                    comment.text
                } else {
                    val parentComment =
                        rvPostDetails.getIndexAndCommentFromAdapter(parentCommentId)?.second
                            ?: return

                    val reply = rvPostDetails.getIndexAndReplyFromComment(parentComment, comment.id)
                        ?: return

                    reply.second.text
                }

            // updates the edittext with the comment to be edited
            editCommentId = comment.id
            parentId = parentCommentId
            // decodes the comment text and sets to the edit text
            UserTaggingDecoder.decode(
                commentComposer.etComment,
                commentText,
                ContextCompat.getColor(requireContext(), LMFeedAppearance.getTextLinkColor())
            )
            commentComposer.etComment.setSelection(commentComposer.etComment.length())
            commentComposer.etComment.setSelection(commentComposer.etComment.length())
            commentComposer.etComment.focusAndShowKeyboard()
        }
    }

    private fun deleteCommentEntity(comment: LMFeedCommentViewData) {
        val deleteExtras = LMFeedDeleteExtras.Builder()
            .postId(comment.postId)
            .commentId(comment.id)
            .entityType(DELETE_TYPE_COMMENT)
            .parentCommentId(comment.parentId)
            .build()

        val creatorUUID = comment.user.sdkClientInfoViewData.uuid
        showDeleteDialog(creatorUUID, deleteExtras)
    }

    private fun showDeleteDialog(creatorUUID: String, deleteExtras: LMFeedDeleteExtras) {
        val userPreferences = LMFeedUserPreferences(requireContext())
        val loggedInUUID = userPreferences.getUUID()

        if (creatorUUID == loggedInUUID) {
            // when user deletes their own entity
            LMFeedSelfDeleteDialogFragment.showDialog(
                childFragmentManager,
                deleteExtras
            )
        } else {
            // when CM deletes other user's entity
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
                val data = result.data?.getStringExtra(LMFeedReportFragment.LM_FEED_REPORT_RESULT)

                val entityType = if (data == "Post") {
                    LMFeedCommunityUtil.getPostVariable()
                        .pluralizeOrCapitalize(LMFeedWordAction.FIRST_LETTER_CAPITAL_SINGULAR)
                } else {
                    data
                }

                LMFeedReportSuccessDialogFragment.showDialog(childFragmentManager, (entityType ?: ""))
            }
        }

    private fun reportPostEntity(post: LMFeedPostViewData) {
        val creatorUUID = post.headerViewData.user.sdkClientInfoViewData.uuid

        //create extras for [ReportActivity]
        val reportExtras = LMFeedReportExtras.Builder()
            .entityId(post.id)
            .uuid(creatorUUID)
            .entityType(REPORT_TYPE_POST)
            .postId(post.id)
            .postViewType(post.viewType)
            .build()

        //get Intent for [ReportActivity]
        val intent = LMFeedReportActivity.getIntent(requireContext(), reportExtras)

        //start [ReportActivity] and check for result
        reportPostLauncher.launch(intent)
    }

    private fun reportCommentEntity(
        @LMFeedReportType
        reportType: Int,
        comment: LMFeedCommentViewData
    ) {
        val creatorUUID = comment.user.sdkClientInfoViewData.uuid

        //create extras for [ReportActivity]
        val reportExtras = LMFeedReportExtras.Builder()
            .entityId(comment.id)
            .uuid(creatorUUID)
            .entityType(reportType)
            .postId(comment.postId)
            .parentCommentId(comment.parentId)
            .build()

        //get Intent for [ReportActivity]
        val intent = LMFeedReportActivity.getIntent(requireContext(), reportExtras)

        //start [ReportActivity] and check for result
        reportPostLauncher.launch(intent)
    }

    override fun onCommentContentLinkClicked(url: String) {
        super.onCommentContentLinkClicked(url)

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

    override fun onPostCommentsCountClicked(position: Int, postViewData: LMFeedPostViewData) {
        super.onPostCommentsCountClicked(position, postViewData)

        binding.commentComposer.etComment.focusAndShowKeyboard()
    }

    override fun onPostLikeClicked(position: Int, postViewData: LMFeedPostViewData) {
        super.onPostLikeClicked(position, postViewData)

        postEvent.notify(Pair(postViewData.id, postViewData))

        val userPreferences = LMFeedUserPreferences(requireContext())
        val loggedInUUID = userPreferences.getUUID()

        //call api
        postDetailViewModel.likePost(
            postViewData.id,
            postViewData.actionViewData.isLiked,
            loggedInUUID
        )

        //update recycler
        binding.rvPostDetails.updateItem(position, postViewData)
    }

    override fun onPostLikesCountClicked(position: Int, postViewData: LMFeedPostViewData) {
        //show the likes screen
        val likesScreenExtras = LMFeedLikesScreenExtras.Builder()
            .postId(postViewData.id)
            .entityType(POST)
            .build()
        LMFeedLikesActivity.start(requireContext(), likesScreenExtras)
    }

    override fun onPostSaveClicked(position: Int, postViewData: LMFeedPostViewData) {
        super.onPostSaveClicked(position, postViewData)

        // notifies the subscribers about the change
        postEvent.notify(Pair(postViewData.id, postViewData))

        //call api
        postDetailViewModel.savePost(postViewData)

        //update recycler
        binding.rvPostDetails.updateItem(postDataPosition, postViewData)
    }

    override fun onPostContentSeeMoreClicked(position: Int, postViewData: LMFeedPostViewData) {
        super.onPostContentSeeMoreClicked(position, postViewData)

        binding.rvPostDetails.updateItem(postDataPosition, postViewData)
    }

    //callback when the tag of the user is clicked
    override fun onPostTaggedMemberClicked(position: Int, uuid: String) {
        super.onPostTaggedMemberClicked(position, uuid)

        val coreCallback = LMFeedCoreApplication.getLMFeedCoreCallback()
        coreCallback?.openProfileWithUUID(uuid)
    }

    override fun onCommentContentSeeMoreClicked(position: Int, comment: LMFeedCommentViewData) {
        super.onCommentContentSeeMoreClicked(position, comment)

        binding.rvPostDetails.updateItem(position, comment)
    }

    override fun onCommentLiked(position: Int, comment: LMFeedCommentViewData) {
        super.onCommentLiked(position, comment)

        val userPreferences = LMFeedUserPreferences(requireContext())
        val loggedInUUID = userPreferences.getUUID()

        val indexAndComment =
            binding.rvPostDetails.getIndexAndCommentFromAdapter(comment.id) ?: return

        val adapterPosition = indexAndComment.first

        //call api
        postDetailViewModel.likeComment(
            comment.postId,
            comment.id,
            !comment.isLiked,
            loggedInUUID
        )

        //update recycler
        binding.rvPostDetails.updateItem(adapterPosition, comment)
    }

    override fun onCommentLikesCountClicked(position: Int, comment: LMFeedCommentViewData) {
        super.onCommentLikesCountClicked(position, comment)

        //shows likes screen for comment
        val likesScreenExtras = LMFeedLikesScreenExtras.Builder()
            .postId(comment.postId)
            .commentId(comment.id)
            .entityType(COMMENT)
            .build()
        LMFeedLikesActivity.start(requireContext(), likesScreenExtras)
    }

    override fun onReplyLiked(position: Int, replyViewData: LMFeedCommentViewData) {
        super.onReplyLiked(position, replyViewData)

        binding.rvPostDetails.apply {
            // gets parentComment from adapter
            val parentCommentId = replyViewData.parentId ?: return
            val parentIndexAndComment = getIndexAndCommentFromAdapter(parentCommentId) ?: return
            val parentPosition = parentIndexAndComment.first
            val parentComment = parentIndexAndComment.second

            parentComment.replies[position] = replyViewData

            //update comment view data
            val newViewData = parentComment.toBuilder()
                .fromCommentLiked(false)
                .fromCommentEdited(false)
                .replies(parentComment.replies)
                .build()

            val userPreferences = LMFeedUserPreferences(requireContext())
            val loggedInUUID = userPreferences.getUUID()

            //call api
            postDetailViewModel.likeComment(
                newViewData.postId,
                replyViewData.id,
                !replyViewData.isLiked,
                loggedInUUID
            )

            //update recycler
            updateItem(parentPosition, newViewData)
        }
    }

    override fun onCommentReplyClicked(position: Int, comment: LMFeedCommentViewData) {
        super.onCommentReplyClicked(position, comment)

        parentCommentIdToReply = comment.id
        binding.apply {
            val replyingText = getString(
                R.string.lm_feed_replying_to_s,
                comment.user.name
            )
            commentComposer.setReplyingView(replyingText)
            commentComposer.etComment.focusAndShowKeyboard()

            rvPostDetails.smoothScrollToPosition(position)
        }
    }

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

    override fun onPostMenuIconClicked(
        position: Int,
        anchorView: View,
        postViewData: LMFeedPostViewData
    ) {
        super.onPostMenuIconClicked(position, anchorView, postViewData)

        val popupMenu = LMFeedOverflowMenu(requireContext(), anchorView)
        val menuItems = postViewData.headerViewData.menuItems
        popupMenu.addMenuItems(menuItems)

        popupMenu.setMenuItemClickListener { menuId ->
            onPostMenuItemClicked(position, menuId, postViewData)
        }

        popupMenu.show()
    }

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

    // callback when other's post is deleted by CM
    override fun onEntityDeletedByAdmin(deleteExtras: LMFeedDeleteExtras, reason: String) {
        binding.rvPostDetails.apply {
            when (deleteExtras.entityType) {
                DELETE_TYPE_POST -> {
                    val post = getItem(postDataPosition) as LMFeedPostViewData
                    postDetailViewModel.deletePost(post, reason)
                }

                DELETE_TYPE_COMMENT -> {
                    val commentId = deleteExtras.commentId ?: return
                    postDetailViewModel.deleteComment(
                        deleteExtras.postId,
                        commentId,
                        parentCommentId = deleteExtras.parentCommentId,
                        reason = reason
                    )
                }
            }
        }
    }

    // callback when self post is deleted by user
    override fun onEntityDeletedByAuthor(deleteExtras: LMFeedDeleteExtras) {
        binding.rvPostDetails.apply {
            when (deleteExtras.entityType) {
                DELETE_TYPE_POST -> {
                    val post = getItem(postDataPosition) as LMFeedPostViewData
                    postDetailViewModel.deletePost(post)
                }

                DELETE_TYPE_COMMENT -> {
                    val commentId = deleteExtras.commentId ?: return
                    postDetailViewModel.deleteComment(
                        deleteExtras.postId,
                        commentId,
                        parentCommentId = deleteExtras.parentCommentId
                    )
                }
            }
        }
    }

    // callback when the user clicks on the post answer prompt
    override fun onPostAnswerPromptClicked(position: Int, postViewData: LMFeedPostViewData) {
        super.onPostAnswerPromptClicked(position, postViewData)

        binding.commentComposer.etComment.focusAndShowKeyboard()
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

    // launcher for [EditPostActivity]
    private val editPostLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    refreshPostData()
                }
            }
        }

    protected open fun onEditPostMenuClicked(
        position: Int,
        menuId: Int,
        post: LMFeedPostViewData
    ) {
        val editPostExtras = LMFeedEditPostExtras.Builder()
            .postId(post.id)
            .build()
        val intent = LMFeedEditPostActivity.getIntent(requireContext(), editPostExtras)
        editPostLauncher.launch(intent)
    }

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

    protected open fun onReportPostMenuClicked(
        position: Int,
        menuId: Int,
        post: LMFeedPostViewData
    ) {
        reportPostEntity(post)
    }

    protected open fun onPinPostMenuClicked(
        position: Int,
        menuId: Int,
        post: LMFeedPostViewData
    ) {
        //call api
        postDetailViewModel.pinPost(post)

        //update recycler
        binding.rvPostDetails.updateItem(postDataPosition, post)
    }

    protected open fun onUnpinPostMenuClicked(
        position: Int,
        menuId: Int,
        post: LMFeedPostViewData
    ) {
        //call api
        postDetailViewModel.pinPost(post)

        //update recycler
        binding.rvPostDetails.updateItem(postDataPosition, post)
    }

    override fun onCommentReplyCountClicked(
        position: Int,
        comment: LMFeedCommentViewData,
        areRepliesVisible: Boolean
    ) {
        super.onCommentReplyCountClicked(position, comment, areRepliesVisible)

        if (areRepliesVisible) {
            // gets page-1 replies
            postDetailViewModel.getComment(
                comment.postId,
                comment.id,
                1
            )
        }
    }

    override fun onCommentMenuIconClicked(
        position: Int,
        anchorView: View,
        comment: LMFeedCommentViewData
    ) {
        super.onCommentMenuIconClicked(position, anchorView, comment)

        val popupMenu = LMFeedOverflowMenu(requireContext(), anchorView)
        val menuItems = comment.menuItems
        popupMenu.addMenuItems(menuItems)

        popupMenu.setMenuItemClickListener { menuId ->
            onCommentMenuItemClicked(
                position,
                menuId,
                comment
            )
        }

        popupMenu.show()
    }

    override fun onViewMoreRepliesClicked(
        position: Int,
        viewMoreReplyViewData: LMFeedViewMoreReplyViewData
    ) {
        super.onViewMoreRepliesClicked(position, viewMoreReplyViewData)

        binding.rvPostDetails.apply {
            val comment =
                getIndexAndCommentFromAdapter(viewMoreReplyViewData.parentCommentId)?.second
                    ?: return
            postDetailViewModel.getComment(
                comment.postId,
                viewMoreReplyViewData.parentCommentId,
                viewMoreReplyViewData.page
            )
        }
    }

    override fun onReplyLikesCountClicked(position: Int, replyViewData: LMFeedCommentViewData) {
        super.onReplyLikesCountClicked(position, replyViewData)

        //shows likes screen for comment
        val likesScreenExtras = LMFeedLikesScreenExtras.Builder()
            .postId(replyViewData.postId)
            .commentId(replyViewData.id)
            .entityType(COMMENT)
            .build()
        LMFeedLikesActivity.start(requireContext(), likesScreenExtras)
    }

    override fun onReplyMenuIconClicked(
        position: Int,
        anchorView: View,
        replyViewData: LMFeedCommentViewData
    ) {
        super.onReplyMenuIconClicked(position, anchorView, replyViewData)

        val popupMenu = LMFeedOverflowMenu(requireContext(), anchorView)
        val menuItems = replyViewData.menuItems
        popupMenu.addMenuItems(menuItems)

        popupMenu.setMenuItemClickListener { menuId ->
            onReplyMenuItemClicked(
                position,
                menuId,
                replyViewData
            )
        }

        popupMenu.show()
    }

    //processes comment menu item clicked
    private fun onCommentMenuItemClicked(
        position: Int,
        menuId: Int,
        comment: LMFeedCommentViewData
    ) {
        when (menuId) {
            EDIT_COMMENT_MENU_ITEM_ID -> {
                onEditCommentMenuClicked(
                    position,
                    menuId,
                    comment
                )
            }

            DELETE_COMMENT_MENU_ITEM_ID -> {
                onDeleteCommentMenuClicked(
                    position,
                    menuId,
                    comment
                )
            }

            REPORT_COMMENT_MENU_ITEM_ID -> {
                onReportCommentMenuClicked(
                    position,
                    menuId,
                    comment
                )
            }
        }
    }

    //processes edit comment request
    protected open fun onEditCommentMenuClicked(
        position: Int,
        menuId: Int,
        comment: LMFeedCommentViewData
    ) {
        editCommentEntity(comment)
    }

    //processes delete comment menu item clicked
    protected open fun onDeleteCommentMenuClicked(
        position: Int,
        menuId: Int,
        comment: LMFeedCommentViewData
    ) {
        deleteCommentEntity(comment)
    }

    //processes report comment menu item clicked
    protected open fun onReportCommentMenuClicked(
        position: Int,
        menuId: Int,
        comment: LMFeedCommentViewData
    ) {
        reportCommentEntity(REPORT_TYPE_COMMENT, comment)
    }

    //processes reply menu item clicked
    protected open fun onReplyMenuItemClicked(
        position: Int,
        menuId: Int,
        reply: LMFeedCommentViewData
    ) {
        when (menuId) {
            EDIT_COMMENT_MENU_ITEM_ID -> {
                onEditReplyMenuClicked(
                    position,
                    menuId,
                    reply
                )
            }

            DELETE_COMMENT_MENU_ITEM_ID -> {
                onDeleteReplyMenuClicked(
                    position,
                    menuId,
                    reply
                )
            }

            REPORT_COMMENT_MENU_ITEM_ID -> {
                onReportReplyMenuClicked(
                    position,
                    menuId,
                    reply
                )
            }
        }
    }

    //processes edit reply menu item click
    protected open fun onEditReplyMenuClicked(
        position: Int,
        menuId: Int,
        reply: LMFeedCommentViewData
    ) {
        editCommentEntity(reply)
    }

    //processes delete reply menu item click
    protected open fun onDeleteReplyMenuClicked(
        position: Int,
        menuId: Int,
        reply: LMFeedCommentViewData
    ) {
        deleteCommentEntity(reply)
    }

    //processes report reply menu item click
    protected open fun onReportReplyMenuClicked(
        position: Int,
        menuId: Int,
        reply: LMFeedCommentViewData
    ) {
        reportCommentEntity(REPORT_TYPE_REPLY, reply)
    }

    //called when the page in the multiple media post is changed
    override fun onPostMultipleMediaPageChangeCallback(position: Int, parentPosition: Int) {
        //processes the current video whenever view pager's page is changed
        binding.rvPostDetails.refreshVideoAutoPlayer()
    }

    //callback when the user expands the documents list
    override fun onPostMultipleDocumentsExpanded(position: Int, postViewData: LMFeedPostViewData) {
        super.onPostMultipleDocumentsExpanded(position, postViewData)

        binding.rvPostDetails.apply {
            if (position == items().size - 1) {
                scrollToPositionWithOffset(position, 75)
            }

            updateItem(position, postViewData)
        }
    }

    //callback to update [fromPostLiked] variable
    override fun updateFromLikedSaved(position: Int, postViewData: LMFeedPostViewData) {
        super.updateFromLikedSaved(position, postViewData)

        val updatedPostData = postViewData.toBuilder()
            .fromPostLiked(false)
            .fromPostSaved(false)
            .build()
        binding.rvPostDetails.updateItemWithoutNotifying(position, updatedPostData)
    }

    //callback when the post share button is clicked
    override fun onPostShareClicked(position: Int, postViewData: LMFeedPostViewData) {
        super.onPostShareClicked(position, postViewData)
        val userMeta = LMFeedUserMetaData.getInstance()
        LMFeedShareUtils.sharePost(
            requireContext(),
            postViewData.id,
            userMeta.domain ?: "",
            LMFeedCommunityUtil.getPostVariable()
        )

        LMFeedAnalytics.sendPostShared(postViewData)
    }

    //callback when comment creator header view is clicked
    override fun onCommenterHeaderClicked(position: Int, commentViewData: LMFeedCommentViewData) {
        super.onCommenterHeaderClicked(position, commentViewData)

        //trigger profile callback
        val coreCallback = LMFeedCoreApplication.getLMFeedCoreCallback()
        coreCallback?.openProfile(commentViewData.user)
    }

    //callback when the tag of a member in comment is clicked
    override fun onCommentTaggedMemberClicked(position: Int, uuid: String) {
        super.onCommentTaggedMemberClicked(position, uuid)

        //trigger profile callback
        val coreCallback = LMFeedCoreApplication.getLMFeedCoreCallback()
        coreCallback?.openProfileWithUUID(uuid)
    }

    //callback when replier header view is clicked
    override fun onReplierHeaderClicked(position: Int, replyViewData: LMFeedCommentViewData) {
        super.onReplierHeaderClicked(position, replyViewData)

        //trigger profile callback
        val coreCallback = LMFeedCoreApplication.getLMFeedCoreCallback()
        coreCallback?.openProfile(replyViewData.user)
    }

    //callback when the tag of a member in reply is clicked
    override fun onReplyTaggedMemberClicked(position: Int, uuid: String) {
        super.onReplyTaggedMemberClicked(position, uuid)

        //trigger profile callback
        val coreCallback = LMFeedCoreApplication.getLMFeedCoreCallback()
        coreCallback?.openProfileWithUUID(uuid)
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
            postDetailViewModel.submitPollVote(
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

        binding.rvPostDetails.apply {
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

            updateItem(postDataPosition, updatedPostViewData)
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

        val postViewData = binding.rvPostDetails.getItem(pollPosition) as LMFeedPostViewData
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
                postDetailViewModel.submitPollVote(
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
                binding.rvPostDetails.updateItem(pollPosition, updatedPostViewData)
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

        val postViewData = binding.rvPostDetails.getItem(pollPosition) as LMFeedPostViewData
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
        val post = binding.rvPostDetails.getItem(postDataPosition) as LMFeedPostViewData

        postDetailViewModel.addPollOption(post, option)
    }

    // callback when the see more button is clicked on the post heading
    override fun onPostHeadingSeeMoreClicked(position: Int, postViewData: LMFeedPostViewData) {
        super.onPostHeadingSeeMoreClicked(position, postViewData)

        //update recycler
        binding.rvPostDetails.updateItem(postDataPosition, postViewData)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvPostDetails.destroyVideoAutoPlayer()
    }
}