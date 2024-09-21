package com.likeminds.feed.android.core.qnafeed.view

import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.likeminds.feed.android.core.databinding.LmFeedFragmentQnaFeedBinding
import com.likeminds.feed.android.core.post.util.LMFeedPostEvent
import com.likeminds.feed.android.core.post.util.LMFeedPostObserver
import com.likeminds.feed.android.core.qnafeed.viewmodel.LMFeedQnAFeedViewModel
import com.likeminds.feed.android.core.socialfeed.model.LMFeedPostViewData
import com.likeminds.feed.android.core.ui.theme.LMFeedTheme
import com.likeminds.feed.android.core.utils.LMFeedEndlessRecyclerViewScrollListener
import com.likeminds.feed.android.core.utils.LMFeedProgressBarHelper

open class LMFeedQnAFeedFragment :
    Fragment(),
    LMFeedPostObserver {

    private lateinit var binding: LmFeedFragmentQnaFeedBinding
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout

    private val qnaFeedViewModel: LMFeedQnAFeedViewModel by viewModels()

    private val postPublisher by lazy {
        LMFeedPostEvent.getPublisher()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LmFeedFragmentQnaFeedBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
    }

    override fun onStart() {
        super.onStart()
        postPublisher.subscribe(this)
    }

    private fun initUI() {
        initQnAFeedRecyclerView()
        initSwipeRefreshLayout()
        initSelectedTopicRecyclerView()
    }

    // initializes the recycler view of the qna feed
    private fun initQnAFeedRecyclerView() {
        LMFeedProgressBarHelper.showProgress(binding.progressBar)
        binding.rvQna.apply {
            setAdapter()

            val paginationScrollListener =
                object : LMFeedEndlessRecyclerViewScrollListener(linearLayoutManager) {
                    override fun onLoadMore(currentPage: Int) {
                        if (currentPage > 0) {
                            //todo:
//                            qnaFeedViewModel.getFeed(
//                                currentPage,
//                                qnaFeedViewModel.getTopicIdsFromAdapterList(binding.topicSelectorBar.getAllSelectedTopics())
//                            )
                        }
                    }
                }
            setPaginationScrollListener(paginationScrollListener)
        }
    }

    // initializes the swipe to refresh layout
    private fun initSwipeRefreshLayout() {
        mSwipeRefreshLayout = binding.swipeRefreshLayout
        mSwipeRefreshLayout.apply {
            setColorSchemeColors(
                ContextCompat.getColor(
                    requireContext(),
                    LMFeedTheme.getButtonColor()
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
            //todo:
//            qnaFeedViewModel.getAllTopics(false)
//            setSelectedTopicAdapter(this@LMFeedQnAFeedFragment)

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
            rvQna.resetScrollListenerData()
            LMFeedProgressBarHelper.showProgress(progressBar, true)
            //todo:
//            qnaFeedViewModel.getFeed(1, null)

            //show layout accordingly
            topicSelectorBar.setSelectedTopicFilterVisibility(false)
            topicSelectorBar.setAllTopicsTextVisibility(true)
        }
    }

    //processes the feed refreshed event
    protected open fun onFeedRefreshed() {
        binding.apply {
            mSwipeRefreshLayout.isRefreshing = true
            rvQna.resetScrollListenerData()
            //todo:
//            qnaFeedViewModel.getUnreadNotificationCount()
//            qnaFeedViewModel.getFeed(
//                1,
//                qnaFeedViewModel.getTopicIdsFromAdapterList(topicSelectorBar.getAllSelectedTopics())
//            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // unsubscribes itself from the [PostPublisher]
        postPublisher.unsubscribe(this)
    }

    //callback when publisher publishes any updated postData
    override fun update(postData: Pair<String, LMFeedPostViewData?>) {
        val postId = postData.first
        // fetches post from adapter
        binding.rvQna.apply {
            //todo:
//            val postIndex = getIndexAndPostFromAdapter(postId)?.first ?: return
//
//            val updatedPost = postData.second
//
//            // updates the item in adapter
//            if (updatedPost == null) {
//                // Post was deleted!
//                removePostAtIndex(postIndex)
//            } else {
//                // Post was updated
//                updatePostItem(postIndex, updatedPost)
//            }
        }
    }
}