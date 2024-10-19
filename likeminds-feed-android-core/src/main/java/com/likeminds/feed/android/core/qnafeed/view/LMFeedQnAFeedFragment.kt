package com.likeminds.feed.android.core.qnafeed.view

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
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
import com.likeminds.feed.android.core.socialfeed.adapter.LMFeedPostAdapterListener
import com.likeminds.feed.android.core.socialfeed.model.LMFeedPostViewData
import com.likeminds.feed.android.core.ui.base.styles.*
import com.likeminds.feed.android.core.ui.base.views.LMFeedFAB
import com.likeminds.feed.android.core.ui.theme.LMFeedThemeConstants
import com.likeminds.feed.android.core.ui.widgets.headerview.view.LMFeedHeaderView
import com.likeminds.feed.android.core.ui.widgets.post.posttopresponse.style.LMFeedPostTopResponseViewStyle
import com.likeminds.feed.android.core.utils.*
import com.likeminds.feed.android.core.utils.LMFeedValueUtils.pluralizeOrCapitalize
import com.likeminds.feed.android.core.utils.analytics.LMFeedAnalytics
import com.likeminds.feed.android.core.utils.model.LMFeedPadding
import com.likeminds.feed.android.core.utils.pluralize.model.LMFeedWordAction

open class LMFeedQnAFeedFragment :
    Fragment(),
    LMFeedPostObserver,
    LMFeedPostAdapterListener {

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
            rvQna.initAdapterAndSetListener(this@LMFeedQnAFeedFragment)

            customizeQnAFeedHeaderView(headerViewQna)
            customizeCreateNewPostButton(fabNewPost)
            customizePostHeaderView()
            customizePostHeadingView()
            customizePostContentView()
            customizePostTopResponseView()
            customizePostActionView()
        }
        return binding.root
    }

    //customizes the create new post fab
    protected open fun customizeCreateNewPostButton(fabNewPost: LMFeedFAB) {
        fabNewPost.apply {
            setStyle(LMFeedStyleTransformer.qnaFeedFragmentViewStyle.createNewPostButtonViewStyle)

            Log.d("PUI", "customizeCreateNewPostButton: ${LMFeedCommunityUtil.getPostVariable()}")

            text = getString(
                R.string.lm_feed_ask_question_s,
                LMFeedCommunityUtil.getPostVariable()
                    .pluralizeOrCapitalize(LMFeedWordAction.ALL_CAPITAL_SINGULAR)
            )
        }
    }

    //customizes the header view
    protected open fun customizeQnAFeedHeaderView(headerViewQnA: LMFeedHeaderView) {
        headerViewQnA.apply {
            setStyle(LMFeedStyleTransformer.qnaFeedFragmentViewStyle.headerViewStyle)

            setTitleText(getString(R.string.lm_feed_feed))
        }
    }

    // customizes the post header view
    protected open fun customizePostHeaderView() {
        LMFeedStyleTransformer.postViewStyle = LMFeedStyleTransformer.postViewStyle.toBuilder()
            .postHeaderViewStyle(
                LMFeedStyleTransformer.postViewStyle.postHeaderViewStyle.toBuilder()
                    .menuIconStyle(
                        LMFeedIconStyle.Builder()
                            .inActiveSrc(R.drawable.lm_feed_ic_overflow_menu_vertical)
                            .iconPadding(
                                LMFeedPadding(
                                    R.dimen.lm_feed_icon_padding,
                                    R.dimen.lm_feed_icon_padding,
                                    R.dimen.lm_feed_icon_padding,
                                    R.dimen.lm_feed_icon_padding
                                )
                            )
                            .build()
                    )
                    .build()
            )
            .build()
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

    protected open fun customizePostActionView() {
        LMFeedStyleTransformer.postViewStyle = LMFeedStyleTransformer.postViewStyle.toBuilder()
            .postActionViewStyle(
                LMFeedStyleTransformer.postViewStyle.postActionViewStyle.toBuilder()
                    .likeIconStyle(
                        LMFeedIconStyle.Builder()
                            .activeSrc(R.drawable.lm_feed_ic_upvote_filled)
                            .inActiveSrc(R.drawable.lm_feed_ic_upvote_unfilled)
                            .build()
                    )
                    .build()
            )
            .build()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
        fetchData()
        observeResponses()
    }

    override fun onStart() {
        super.onStart()
        postPublisher.subscribe(this)
    }

    override fun onResume() {
        super.onResume()

        // sends feed opened event
        LMFeedAnalytics.sendFeedOpenedEvent()

        qnaFeedViewModel.fetchPendingPostFromDB()
        binding.rvQna.initiateVideoAutoPlayer()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (hidden) {
            // destroy the video player when fragment is not hidden
            binding.rvQna.destroyVideoAutoPlayer()
        } else {
            // initiate the video player when fragment is not hidden
            binding.rvQna.initiateVideoAutoPlayer()
        }
    }

    private fun fetchData() {
//        qnaFeedViewModel.getLoggedInUser()
//        qnaFeedViewModel.getCreatePostRights()
//        qnaFeedViewModel.getUnreadNotificationCount()
        qnaFeedViewModel.getFeed(1, null)
    }

    private fun observeResponses() {

        qnaFeedViewModel.socialFeedResponse.observe(viewLifecycleOwner) { response ->
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
                binding.rvQna.addPosts(posts)
                binding.rvQna.refreshVideoAutoPlayer()
            }
        }
    }

    private fun checkPostsAndReplace(posts: List<LMFeedPostViewData>) {
        binding.rvQna.apply {
//            checkForNoPost(posts)
            replacePosts(posts)
            scrollToPosition(0)
            refreshVideoAutoPlayer()
        }
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
                    LMFeedThemeConstants.getButtonColor()
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