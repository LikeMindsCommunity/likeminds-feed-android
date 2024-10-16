package com.likeminds.feed.android.core.qnafeed.view

import android.os.Bundle
import android.text.TextUtils
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.databinding.LmFeedFragmentQnaFeedBinding
import com.likeminds.feed.android.core.post.util.LMFeedPostEvent
import com.likeminds.feed.android.core.post.util.LMFeedPostObserver
import com.likeminds.feed.android.core.qnafeed.viewmodel.LMFeedQnAFeedViewModel
import com.likeminds.feed.android.core.socialfeed.model.LMFeedPostViewData
import com.likeminds.feed.android.core.ui.base.styles.*
import com.likeminds.feed.android.core.ui.base.views.LMFeedFAB
import com.likeminds.feed.android.core.ui.theme.LMFeedTheme
import com.likeminds.feed.android.core.ui.widgets.headerview.view.LMFeedHeaderView
import com.likeminds.feed.android.core.ui.widgets.post.posttopresponse.style.LMFeedPostTopResponseViewStyle
import com.likeminds.feed.android.core.utils.*

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
        binding.apply {
            customizeCreateNewPostButton(fabNewPost)
            customizeQnAFeedHeaderView(headerViewQna)
            customizePostHeadingView()
            customizePostContentView()
            customizePostTopResponseView()
        }
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

    //customizes the create new post fab
    protected open fun customizeCreateNewPostButton(fabNewPost: LMFeedFAB) {
        fabNewPost.apply {
            setStyle(LMFeedStyleTransformer.qnaFeedFragmentViewStyle.createNewPostButtonViewStyle)

            text = getString(R.string.lm_feed_ask_question)
        }
    }

    //customizes the header view
    protected open fun customizeQnAFeedHeaderView(headerViewQnA: LMFeedHeaderView) {
        headerViewQnA.apply {
            setStyle(LMFeedStyleTransformer.qnaFeedFragmentViewStyle.headerViewStyle)

            setTitleText(getString(R.string.lm_feed_feed))
        }
    }

    // customizes the heading view in the post
    protected open fun customizePostHeadingView() {
        LMFeedStyleTransformer.postViewStyle = LMFeedStyleTransformer.postViewStyle.toBuilder()
            .postHeadingTextStyle(
                //todo: see more?
                LMFeedTextStyle.Builder()
                    .textColor(R.color.lm_feed_dark_grey)
                    .textSize(R.dimen.lm_feed_text_large)
                    .maxLines(3)
                    .fontResource(R.font.lm_feed_roboto_medium)
                    .build()
            )
            .build()
    }

    // customizes the post content view
    protected open fun customizePostContentView() {
        LMFeedStyleTransformer.postViewStyle = LMFeedStyleTransformer.postViewStyle.toBuilder()
            .postContentTextStyle(
                LMFeedTextStyle.Builder()
                    .textColor(R.color.lm_feed_dark_grey)
                    .textSize(R.dimen.lm_feed_text_medium)
                    .maxLines(3)
                    .fontResource(R.font.lm_feed_roboto_medium)
                    .build()
            )
            .build()
    }

    // customizes the post top response view
    protected open fun customizePostTopResponseView() {
        LMFeedStyleTransformer.postViewStyle = LMFeedStyleTransformer.postViewStyle.toBuilder()
            .postTopResponseViewStyle(
                LMFeedPostTopResponseViewStyle.Builder()
                    .authorImageViewStyle(
                        LMFeedImageStyle.Builder()
                            .isCircle(true)
                            .showGreyScale(false)
                            .build()
                    )
                    .authorNameTextStyle(
                        LMFeedTextStyle.Builder()
                            .textColor(R.color.lm_feed_dark_grey)
                            .textSize(R.dimen.lm_feed_text_medium)
                            .maxLines(1)
                            .ellipsize(TextUtils.TruncateAt.END)
                            .fontResource(R.font.lm_feed_roboto_bold)
                            .build()
                    )
                    .timestampTextStyle(
                        LMFeedTextStyle.Builder()
                            .textColor(R.color.lm_feed_brown_grey)
                            .textSize(R.dimen.lm_feed_text_small)
                            .fontResource(R.font.lm_feed_roboto)
                            .build()
                    )
                    .contentTextStyle(
                        LMFeedTextStyle.Builder()
                            .textColor(R.color.lm_feed_grey)
                            .textSize(R.dimen.lm_feed_text_medium)
                            .maxLines(5)
                            .fontResource(R.font.lm_feed_roboto)
                            .expandableCTAText("...Read More")
                            .expandableCTAColor(R.color.lm_feed_dark_grey)
                            .build()
                    )
                    .backgroundColor(R.color.lm_feed_light_grayish_blue_50)
                    .build()
            )
            .build()
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