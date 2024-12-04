package com.likeminds.feed.android.core.search.view

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.likeminds.feed.android.core.LMFeedCoreApplication
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.databinding.LmFeedSearchFragmentBinding
import com.likeminds.feed.android.core.delete.model.DELETE_TYPE_POST
import com.likeminds.feed.android.core.delete.model.LMFeedDeleteExtras
import com.likeminds.feed.android.core.delete.view.*
import com.likeminds.feed.android.core.likes.model.LMFeedLikesScreenExtras
import com.likeminds.feed.android.core.likes.model.POST
import com.likeminds.feed.android.core.likes.view.LMFeedLikesActivity
import com.likeminds.feed.android.core.poll.result.model.*
import com.likeminds.feed.android.core.poll.result.view.LMFeedPollResultsActivity
import com.likeminds.feed.android.core.post.detail.model.LMFeedPostDetailExtras
import com.likeminds.feed.android.core.post.detail.view.LMFeedPostDetailActivity
import com.likeminds.feed.android.core.post.edit.model.LMFeedEditPostExtras
import com.likeminds.feed.android.core.post.edit.view.LMFeedEditPostActivity
import com.likeminds.feed.android.core.post.model.LMFeedAttachmentViewData
import com.likeminds.feed.android.core.post.util.LMFeedPostEvent
import com.likeminds.feed.android.core.post.util.LMFeedPostObserver
import com.likeminds.feed.android.core.post.viewmodel.LMFeedHelperViewModel
import com.likeminds.feed.android.core.post.viewmodel.LMFeedPostViewModel
import com.likeminds.feed.android.core.postmenu.model.*
import com.likeminds.feed.android.core.report.model.LMFeedReportExtras
import com.likeminds.feed.android.core.report.model.REPORT_TYPE_POST
import com.likeminds.feed.android.core.report.view.*
import com.likeminds.feed.android.core.search.model.LMFeedSearchExtras
import com.likeminds.feed.android.core.search.view.LMFeedSearchActivity.Companion.LM_FEED_SEARCH_EXTRAS
import com.likeminds.feed.android.core.search.viewmodel.LMFeedSearchViewModel
import com.likeminds.feed.android.core.socialfeed.adapter.LMFeedPostAdapterListener
import com.likeminds.feed.android.core.socialfeed.model.LMFeedPostViewData
import com.likeminds.feed.android.core.socialfeed.util.LMFeedPostBinderUtils
import com.likeminds.feed.android.core.ui.widgets.noentitylayout.view.LMFeedNoEntityLayoutView
import com.likeminds.feed.android.core.ui.widgets.overflowmenu.view.LMFeedOverflowMenu
import com.likeminds.feed.android.core.ui.widgets.poll.model.LMFeedAddPollOptionExtras
import com.likeminds.feed.android.core.ui.widgets.poll.view.*
import com.likeminds.feed.android.core.ui.widgets.searchbar.view.LMFeedSearchBarListener
import com.likeminds.feed.android.core.ui.widgets.searchbar.view.LMFeedSearchBarView
import com.likeminds.feed.android.core.utils.*
import com.likeminds.feed.android.core.utils.LMFeedValueUtils.pluralizeOrCapitalize
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.hide
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.show
import com.likeminds.feed.android.core.utils.analytics.LMFeedAnalytics
import com.likeminds.feed.android.core.utils.base.LMFeedBaseViewType
import com.likeminds.feed.android.core.utils.coroutine.observeInLifecycle
import com.likeminds.feed.android.core.utils.pluralize.model.LMFeedWordAction
import com.likeminds.feed.android.core.utils.user.LMFeedUserMetaData
import com.likeminds.feed.android.core.utils.user.LMFeedUserPreferences
import com.likeminds.likemindsfeed.post.model.PollMultiSelectState
import kotlinx.coroutines.flow.onEach

open class LMFeedSearchFragment : Fragment(),
    LMFeedPostAdapterListener,
    LMFeedAdminDeleteDialogListener,
    LMFeedSelfDeleteDialogListener,
    LMFeedAddPollOptionBottomSheetListener,
    LMFeedPostObserver {

    private lateinit var binding: LmFeedSearchFragmentBinding
    private lateinit var lmFeedSearchFragmentExtras: LMFeedSearchExtras

    private val searchViewModel: LMFeedSearchViewModel by viewModels()

    // keyword entered in the search bar
    private var searchKeyword: String? = null

    companion object {
        const val TAG = "LMFeedSearchFragment"

        fun getInstance(searchFragmentExtras: LMFeedSearchExtras): LMFeedSearchFragment {
            val searchFragment = LMFeedSearchFragment()
            val bundle = Bundle()
            bundle.putParcelable(LM_FEED_SEARCH_EXTRAS, searchFragmentExtras)
            searchFragment.arguments = bundle
            return searchFragment
        }
    }

    private val postEvent by lazy {
        LMFeedPostEvent.getPublisher()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //receive extras
        receiveExtras()
    }

    private fun receiveExtras() {
        if (arguments == null || arguments?.containsKey(LM_FEED_SEARCH_EXTRAS) == false) {
            requireActivity().supportFragmentManager.popBackStack()
            return
        }

        lmFeedSearchFragmentExtras = LMFeedExtrasUtil.getParcelable(
            arguments,
            LM_FEED_SEARCH_EXTRAS,
            LMFeedSearchExtras::class.java
        ) ?: throw emptyExtrasException(TAG)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LmFeedSearchFragmentBinding.inflate(layoutInflater)

        binding.apply {
            rvSearch.initAdapterAndSetListener(this@LMFeedSearchFragment)

            customizeFeedSearchBarView(feedSearchBarView)
            customizeNoSearchResultLayout(layoutNoResultFound)
            customizeSearchListView(rvSearch)
        }

        return binding.root
    }

    //customizes the search bar view
    protected open fun customizeFeedSearchBarView(searchBarView: LMFeedSearchBarView) {
        searchBarView.apply {
            val searchBarStyle =
                LMFeedStyleTransformer.searchFeedFragmentViewStyle.feedSearchBarViewStyle
            setStyle(searchBarStyle)
        }
    }

    //customizes the no search result layout
    protected open fun customizeNoSearchResultLayout(layoutNoResult: LMFeedNoEntityLayoutView) {
        layoutNoResult.apply {
            val noSearchResultLayoutStyle =
                LMFeedStyleTransformer.searchFeedFragmentViewStyle.noSearchResultLayoutViewStyle

            setStyle(noSearchResultLayoutStyle)
            setTitleText(getString(R.string.lm_feed_search_no_results))
        }
    }

    //customizes the search list view
    protected open fun customizeSearchListView(rvSearchListView: LMFeedSearchListView) {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        observeResponses()
    }

    override fun onStart() {
        super.onStart()
        postEvent.subscribe(this)
    }

    override fun onResume() {
        super.onResume()
        binding.rvSearch.initiateVideoAutoPlayer()
    }

    override fun onPause() {
        super.onPause()
        binding.rvSearch.destroyVideoAutoPlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        // unsubscribes itself from the [PostPublisher]
        postEvent.unsubscribe(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvSearch.destroyVideoAutoPlayer()
    }

    // initializes the UI by setting up the recycler view and search bar view
    private fun initUI() {
        initSearchView()
        initRecyclerView()
    }

    // handles the back press of search bar for this fragment
    protected open fun onSearchViewClosed() {
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    // handles the cross press of search bar for this fragment
    protected open fun onSearchCrossed() {
        binding.rvSearch.hide()
    }

    // initializes the search bar view
    private fun initSearchView() {
        binding.feedSearchBarView.apply {
            initialize(lifecycleScope)
            //open search bar with animation
            post {
                openSearch()
            }

            //set search listener
            val searchListener = object : LMFeedSearchBarListener {
                override fun onSearchViewOpened() {
                    super.onSearchViewOpened()
                    binding.layoutNoResultFound.hide()
                }

                override fun onSearchViewClosed() {
                    super.onSearchViewClosed()
                    this@LMFeedSearchFragment.onSearchViewClosed()
                }

                override fun onSearchCrossed() {
                    super.onSearchCrossed()
                    this@LMFeedSearchFragment.onSearchCrossed()
                    binding.apply {
                        layoutNoResultFound.hide()
                        rvSearch.clearPostsAndNotify()
                    }
                }

                override fun onKeywordEntered(keyword: String) {
                    super.onKeywordEntered(keyword)
                    if (keyword.isNotEmpty()) {
                        updateSearchedPosts(keyword)
                    } else {
                        binding.apply {
                            layoutNoResultFound.hide()
                            rvSearch.clearPostsAndNotify()
                        }
                    }
                }

                override fun onEmptyKeywordEntered() {
                    super.onEmptyKeywordEntered()
                    binding.apply {
                        layoutNoResultFound.hide()
                        rvSearch.clearPostsAndNotify()
                    }
                }
            }

            setSearchViewListener(searchListener)
            observeSearchView(true)
        }
    }

    // initializes the recycler view
    private fun initRecyclerView() {
        LMFeedProgressBarHelper.showProgress(binding.progressBar)
        binding.rvSearch.apply {
            setAdapter()
            //set scroll listener
            val paginationScrollListener =
                object : LMFeedEndlessRecyclerViewScrollListener(linearLayoutManager) {
                    override fun onLoadMore(currentPage: Int) {
                        if (currentPage > 0) {
                            // calls api for paginated data
                            searchKeyword?.let { searchString ->
                                searchViewModel.searchPosts(
                                    currentPage,
                                    searchString,
                                    lmFeedSearchFragmentExtras.searchType
                                )
                            }
                        }
                    }
                }
            setPaginationScrollListener(paginationScrollListener)
        }
    }

    // replaces the adapter with the new posts
    private fun checkPostsAndReplace(posts: List<LMFeedPostViewData>) {
        binding.rvSearch.apply {
            checkForNoPost(posts)
            replacePosts(posts)
            scrollToPosition(0)
            refreshVideoAutoPlayer()
        }
    }

    // checks for no posts in response and shows the no result layout
    private fun checkForNoPost(feed: List<LMFeedBaseViewType>) {
        binding.apply {
            if (feed.isNotEmpty()) {
                layoutNoResultFound.hide()
                rvSearch.show()
            } else {
                binding.apply {
                    layoutNoResultFound.show()
                    rvSearch.clearPostsAndNotify()
                }
            }
        }
    }

    // updates the searched posts
    private fun updateSearchedPosts(keyword: String?) {
        binding.rvSearch.apply {
            resetScrollListenerData()
            clearPostsAndNotify()
        }

        //trims the keyword for leading and trailing spaces before api call
        searchKeyword = keyword?.trim()

        //call apis
        searchKeyword?.let { searchString ->
            searchViewModel.searchPosts(
                1,
                searchString,
                lmFeedSearchFragmentExtras.searchType
            )
        }
    }

    private fun observeResponses() {
        LMFeedProgressBarHelper.showProgress(binding.progressBar)
        //observers post response
        searchViewModel.postViewModel.postResponse.observe(viewLifecycleOwner) { postViewData ->
            binding.rvSearch.apply {
                val index = getIndexAndPostFromAdapter(postViewData.id)?.first ?: return@observe
                updatePostItem(index, postViewData)

                //notifies the subscribers about the change in post data
                postEvent.notify(Pair(postViewData.id, postViewData))
            }
        }

        // observe search post response
        searchViewModel.searchFeedResponse.observe(viewLifecycleOwner) { response ->
            val page = response.first
            val posts = response.second
            binding.apply {
                if (page == 1) {
                    checkPostsAndReplace(posts)
                } else {
                    rvSearch.addPosts(posts)
                    rvSearch.refreshVideoAutoPlayer()
                }
            }
            LMFeedProgressBarHelper.hideProgress(binding.progressBar)
        }

        // observes deletePostResponse LiveData
        searchViewModel.helperViewModel.deletePostResponse.observe(viewLifecycleOwner) { postId ->
            postEvent.notify(Pair(postId, null))

            binding.rvSearch.apply {
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

        searchViewModel.helperViewModel.postSavedResponse.observe(viewLifecycleOwner) { postViewData ->
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

        searchViewModel.helperViewModel.postPinnedResponse.observe(viewLifecycleOwner) { postViewData ->
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
        searchViewModel.postViewModel.postResponse.observe(viewLifecycleOwner) { postViewData ->
            binding.rvSearch.apply {
                val index = getIndexAndPostFromAdapter(postViewData.id)?.first ?: return@observe
                updatePostItem(index, postViewData)
            }
        }

        searchViewModel.postViewModel.errorMessageEventFlow.onEach { response ->
            when (response) {
                is LMFeedPostViewModel.ErrorMessageEvent.SubmitVote -> {
                    LMFeedViewUtils.showErrorMessageToast(
                        requireContext(),
                        response.errorMessage
                    )
                }

                is LMFeedPostViewModel.ErrorMessageEvent.AddPollOption -> {
                    LMFeedViewUtils.showErrorMessageToast(
                        requireContext(),
                        response.errorMessage
                    )
                }

                is LMFeedPostViewModel.ErrorMessageEvent.GetPost -> {
                    LMFeedViewUtils.showErrorMessageToast(
                        requireContext(),
                        response.errorMessage
                    )
                }

                else -> {}
            }
        }

        searchViewModel.helperViewModel.errorMessageEventFlow.onEach { response ->
            when (response) {
                is LMFeedHelperViewModel.ErrorMessageEvent.DeletePost -> {
                    val errorMessage = response.errorMessage
                    LMFeedViewUtils.showErrorMessageToast(requireContext(), errorMessage)
                }

                is LMFeedHelperViewModel.ErrorMessageEvent.LikePost -> {
                    val postId = response.postId

                    //get post and index
                    val pair =
                        binding.rvSearch.getIndexAndPostFromAdapter(postId) ?: return@onEach
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

                    // notifies the subscribers about the change
                    postEvent.notify(Pair(updatedPostData.id, updatedPostData))

                    binding.rvSearch.updatePostItem(index, updatedPostData)

                    //show error message
                    LMFeedViewUtils.showSomethingWentWrongToast(requireContext())
                }

                is LMFeedHelperViewModel.ErrorMessageEvent.PinPost -> {
                    binding.rvSearch.apply {
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
                    binding.rvSearch.apply {
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

                else -> {}
            }
        }

        searchViewModel.errorMessageEventFlow.onEach { response ->
            when (response) {
                is LMFeedSearchViewModel.ErrorMessageEvent.SearchPost -> {
                    val errorMessage = response.errorMessage
                    LMFeedProgressBarHelper.hideProgress(binding.progressBar)
                    LMFeedViewUtils.showErrorMessageToast(requireContext(), errorMessage)
                }
            }
        }.observeInLifecycle(viewLifecycleOwner)
    }

    //callback when the user clicks on the post content
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

        // notifies the subscribers about the change
        postEvent.notify(Pair(postViewData.id, postViewData))

        //call api
        searchViewModel.helperViewModel.likePost(
            postViewData.id,
            postViewData.actionViewData.isLiked,
            loggedInUUID
        )

        binding.rvSearch.apply {
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

        // notifies the subscribers about the change
        postEvent.notify(Pair(postViewData.id, postViewData))

        //call api
        searchViewModel.helperViewModel.savePost(postViewData)

        binding.rvSearch.apply {
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

        binding.rvSearch.updatePostWithoutNotifying(position, updatedPostData)
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
        binding.rvSearch.refreshVideoAutoPlayer()
    }

    //called when show more is clicked in the documents type post
    override fun onPostMultipleDocumentsExpanded(
        position: Int,
        postViewData: LMFeedPostViewData
    ) {
        binding.rvSearch.apply {
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

            binding.rvSearch.apply {
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
            binding.rvSearch.getIndexAndPostFromAdapter(deleteExtras.postId)?.second ?: return
        searchViewModel.helperViewModel.deletePost(post, reason)
    }

    override fun onEntityDeletedByAuthor(deleteExtras: LMFeedDeleteExtras) {
        val post =
            binding.rvSearch.getIndexAndPostFromAdapter(deleteExtras.postId)?.second ?: return
        searchViewModel.helperViewModel.deletePost(post)
    }

    override fun update(postData: Pair<String, LMFeedPostViewData?>) {
        val postId = postData.first
        // fetches post from adapter
        binding.rvSearch.apply {
            val postIndexPair = getIndexAndPostFromAdapter(postId) ?: return
            val postIndex = postIndexPair.first

            //updated post:{} from event
            var updatedPost = postData.second

            //existing post in adapter
            val existingPost = postIndexPair.second

            // updates the item in adapter
            if (updatedPost == null) {
                // Post was deleted!
                removePostAtIndex(postIndex)
            } else {
                //updated post content
                val updatedPostContent = updatedPost.contentViewData

                //existing matched keywords
                val existingKeywords = existingPost.contentViewData.keywordMatchedInPostText

                //add the matched keywords into updated post
                updatedPost = updatedPost.toBuilder()
                    .contentViewData(
                        updatedPostContent.toBuilder()
                            .keywordMatchedInPostText(existingKeywords)
                            .build()
                    )
                    .build()

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
    override fun onPostMemberVotedCountClicked(
        position: Int,
        postViewData: LMFeedPostViewData
    ) {
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
            searchViewModel.postViewModel.submitPollVote(
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

        binding.rvSearch.apply {
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

        val postViewData = binding.rvSearch.allPosts()[pollPosition] as LMFeedPostViewData
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
                searchViewModel.postViewModel.submitPollVote(
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
                binding.rvSearch.updatePostItem(pollPosition, updatedPostViewData)
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

        val postViewData = binding.rvSearch.allPosts()[pollPosition] as LMFeedPostViewData
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
        val post = binding.rvSearch.getIndexAndPostFromAdapter(postId)?.second ?: return

        // notifies the subscribers about the change
        postEvent.notify(Pair(postId, post))

        searchViewModel.postViewModel.addPollOption(
            post,
            option
        )
    }

    // callback when the user clicks on the post answer prompt
    override fun onPostAnswerPromptClicked(position: Int, postViewData: LMFeedPostViewData) {
        super.onPostAnswerPromptClicked(position, postViewData)

        // sends comment list open event
        LMFeedAnalytics.sendCommentListOpenEvent()

        val postDetailExtras = LMFeedPostDetailExtras.Builder()
            .postId(postViewData.id)
            .isEditTextFocused(true)
            .build()
        LMFeedPostDetailActivity.start(requireContext(), postDetailExtras)
    }

    //callback when the user clicks on the post menu icon
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
                val data =
                    result.data?.getStringExtra(LMFeedReportFragment.LM_FEED_REPORT_RESULT)
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
        searchViewModel.helperViewModel.pinPost(post)

        binding.rvSearch.apply {
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
        searchViewModel.helperViewModel.pinPost(post)

        binding.rvSearch.apply {
            val adapterPosition = getIndexAndPostFromAdapter(post.id)?.first ?: return

            //update recycler
            updatePostItem(adapterPosition, post)
        }
    }

    // callback when the user clicks on the post heading
    override fun onPostHeadingClicked(position: Int, postViewData: LMFeedPostViewData) {
        super.onPostHeadingClicked(position, postViewData)

        // sends comment list open event
        LMFeedAnalytics.sendCommentListOpenEvent()

        val postDetailExtras = LMFeedPostDetailExtras.Builder()
            .postId(postViewData.id)
            .isEditTextFocused(false)
            .build()
        LMFeedPostDetailActivity.start(requireContext(), postDetailExtras)
    }

    // callback when the see more button is clicked on the post heading
    override fun onPostHeadingSeeMoreClicked(position: Int, postViewData: LMFeedPostViewData) {
        super.onPostHeadingSeeMoreClicked(position, postViewData)

        binding.rvSearch.apply {
            val adapterPosition = getIndexAndPostFromAdapter(postViewData.id)?.first ?: return

            //update recycler
            updatePostItem(adapterPosition, postViewData)
        }
    }
}