package com.likeminds.feed.android.core.search.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.activityfeed.view.LMFeedActivityFeedFragment
import com.likeminds.feed.android.core.databinding.LmFeedSearchFragmentBinding
import com.likeminds.feed.android.core.delete.view.LMFeedAdminDeleteDialogListener
import com.likeminds.feed.android.core.delete.view.LMFeedSelfDeleteDialogListener
import com.likeminds.feed.android.core.post.util.LMFeedPostObserver
import com.likeminds.feed.android.core.search.viewmodel.LMFeedSearchViewModel
import com.likeminds.feed.android.core.socialfeed.adapter.LMFeedPostAdapterListener
import com.likeminds.feed.android.core.socialfeed.adapter.LMFeedSelectedTopicAdapterListener
import com.likeminds.feed.android.core.socialfeed.model.LMFeedPostViewData
import com.likeminds.feed.android.core.socialfeed.view.LMFeedSocialFeedListView
import com.likeminds.feed.android.core.topicselection.viewmodel.LMFeedTopicSelectionViewModel
import com.likeminds.feed.android.core.ui.widgets.headerview.view.LMFeedHeaderView
import com.likeminds.feed.android.core.ui.widgets.poll.view.LMFeedAddPollOptionBottomSheetListener
import com.likeminds.feed.android.core.ui.widgets.searchbar.view.LMFeedSearchBarListener
import com.likeminds.feed.android.core.ui.widgets.searchbar.view.LMFeedSearchBarView
import com.likeminds.feed.android.core.utils.*
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.hide
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.show
import com.likeminds.feed.android.core.utils.base.LMFeedBaseViewType

open class LMFeedSearchFragment : Fragment(),
    LMFeedPostAdapterListener,
    LMFeedAdminDeleteDialogListener,
    LMFeedSelfDeleteDialogListener,
    LMFeedSelectedTopicAdapterListener,
    LMFeedAddPollOptionBottomSheetListener,
    LMFeedPostObserver {

    private lateinit var binding: LmFeedSearchFragmentBinding

    private val feedSearchViewModel: LMFeedSearchViewModel by viewModels()

    private var searchKeyword: String? = null

    companion object {
        const val TAG = "LMFeedSearchFragment"

        fun getInstance(): LMFeedSearchFragment {
            return LMFeedSearchFragment()
        }
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
            customizeUniversalFeedListView(rvSearch)
//            customizeNoPostLayout(layoutNoPost)
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

    protected open fun customizeUniversalFeedListView(rvUniversal: LMFeedSearchListView) {

    }

    protected open fun onSearchViewClosed() {
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
        initUI()
        initListeners()
    }

    private fun observeData() {
        feedSearchViewModel.searchFeedResponse.observe(viewLifecycleOwner) { response ->
            val page = response.first
            val posts = response.second
            Log.d(
                "PUI", """
                page:$page
                posts:${posts.size}
            """.trimIndent()
            )
            binding.apply {
                if (page == 1) {
                    checkPostsAndReplace(posts)
                } else {
                    rvSearch.addPosts(posts)
                    rvSearch.refreshVideoAutoPlayer()
                }
            }
        }

        // TODO - Handel Error
//        feedSearchViewModel.errorMessageEventFlow
//        feedSearchViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
//            LMFeedViewUtils.showErrorMessageToast(requireContext(), errorMessage)
//        }
    }

    private fun initListeners() {
        binding.apply {
        }
    }

    private fun checkPostsAndReplace(posts: List<LMFeedPostViewData>) {
        binding.rvSearch.apply {
            checkForNoPost(posts)
            replacePosts(posts)
            scrollToPosition(0)
            refreshVideoAutoPlayer()
        }
    }

    private fun checkForNoPost(feed: List<LMFeedBaseViewType>) {
        binding.apply {
            if (feed.isNotEmpty()) {
//                TODO - NO POST LAYOUT
//                layoutNoPost.hide()
                rvSearch.show()
            } else {
                binding.apply {
//                    layoutNoPost.show()
                    rvSearch.hide()
                }
            }
        }
    }

    private fun initUI() {
        initSearchView()
        initRecyclerView()
    }

    private fun initSearchView() {
        binding.feedSearchBarView.apply {
            initialize(lifecycleScope)

            post {
                openSearch()
            }

            val searchListener = object : LMFeedSearchBarListener {
                override fun onSearchViewClosed() {
                    super.onSearchViewClosed()
                    this@LMFeedSearchFragment.onSearchViewClosed()
                }

                override fun onSearchCrossed() {
                    super.onSearchCrossed()

                }

                override fun onKeywordEntered(keyword: String) {
                    super.onKeywordEntered(keyword)
                    Log.d("PUI", "keyword entered: $keyword")
                    updateSearchedPosts(keyword)
                }

                override fun onEmptyKeywordEntered() {
                    super.onEmptyKeywordEntered()
                    if (!searchKeyword.isNullOrEmpty()) {
                        updateSearchedPosts(null)
                    }
                }
            }

            setSearchViewListener(searchListener)
            observeSearchView(true)
        }
    }

    private fun initRecyclerView() {
        binding.rvSearch.apply {
            setAdapter()
            //set scroll listener
            val paginationScrollListener =
                object : LMFeedEndlessRecyclerViewScrollListener(linearLayoutManager) {
                    override fun onLoadMore(currentPage: Int) {
                        if (currentPage > 0) {
                            // calls api for paginated data
                            searchKeyword?.let {
                                feedSearchViewModel.searchPosts(
                                    currentPage,
                                    it
                                )
                            }
                        }
                    }
                }
            setPaginationScrollListener(paginationScrollListener)
        }
    }

    override fun update(postData: Pair<String, LMFeedPostViewData?>) {
        TODO("Not yet implemented")
    }

    override fun onAddOptionSubmitted(postId: String, pollId: String, option: String) {
        TODO("Not yet implemented")
    }

    private fun updateSearchedPosts(keyword: String?) {
        binding.rvSearch.apply {
            resetScrollListenerData()
            clearPostsAndNotify()
        }
        searchKeyword = keyword

        keyword?.let {
            feedSearchViewModel.searchPosts(
                1,
                keyword
            )
        }
    }
}