package com.likeminds.feed.android.core.activityfeed.view

import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.activityfeed.adapter.LMFeedActivityFeedAdapterListener
import com.likeminds.feed.android.core.activityfeed.model.LMFeedActivityViewData
import com.likeminds.feed.android.core.activityfeed.viewmodel.LMFeedActivityFeedViewModel
import com.likeminds.feed.android.core.databinding.LmFeedFragmentActivityFeedBinding
import com.likeminds.feed.android.core.ui.widgets.headerview.view.LMFeedHeaderView
import com.likeminds.feed.android.core.ui.widgets.noentitylayout.view.LMFeedNoEntityLayoutView
import com.likeminds.feed.android.core.utils.*
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.hide
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.show
import com.likeminds.feed.android.core.utils.base.LMFeedBaseViewType
import com.likeminds.feed.android.core.utils.coroutine.observeInLifecycle
import kotlinx.coroutines.flow.onEach

open class LMFeedActivityFeedFragment : Fragment(), LMFeedActivityFeedAdapterListener {

    private lateinit var binding: LmFeedFragmentActivityFeedBinding
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout

    private val activityFeedViewModel: LMFeedActivityFeedViewModel by viewModels()

    companion object {
        const val TAG = "LMFeedActivityFeedFragment"

        fun getInstance(): LMFeedActivityFeedFragment {
            return LMFeedActivityFeedFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LmFeedFragmentActivityFeedBinding.inflate(layoutInflater)

        binding.apply {
            customizeActivityFeedHeaderView(headerViewActivityFeed)
            customizeNoTopicsLayout(layoutNoActivity)

            //set background color
            val backgroundColor =
                LMFeedStyleTransformer.activityFeedFragmentViewStyle.backgroundColor
            backgroundColor?.let { color ->
                root.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        color
                    )
                )
            }

            return root
        }
    }

    //customizes the header view of the activity feed fragment with the header style set for the activity feed fragment
    protected open fun customizeActivityFeedHeaderView(headerViewActivityFeed: LMFeedHeaderView) {
        headerViewActivityFeed.apply {
            setStyle(LMFeedStyleTransformer.activityFeedFragmentViewStyle.headerViewStyle)

            setTitleText(getString(R.string.lm_feed_notification_feed))
        }
    }

    protected open fun customizeNoTopicsLayout(layoutNoActivity: LMFeedNoEntityLayoutView) {
        layoutNoActivity.apply {
            val noActivityLayoutStyle =
                LMFeedStyleTransformer.activityFeedFragmentViewStyle.noActivityLayoutViewStyle

            setStyle(noActivityLayoutStyle)
            setTitleText(getString(R.string.lm_feed_no_notifications_yet))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
        initListeners()
        fetchData()
        observeData()
    }

    private fun initUI() {
        initRecyclerView()
        initSwipeRefreshLayout()
    }

    private fun initListeners() {
        binding.apply {
            headerViewActivityFeed.setNavigationIconClickListener {
                onNavigationIconClick()
            }
        }
    }

    protected open fun onNavigationIconClick() {
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    private fun initRecyclerView() {
        binding.rvActivityFeed.apply {
            setAdapter(this@LMFeedActivityFeedFragment)
            //set scroll listener
            val paginationScrollListener =
                object : LMFeedEndlessRecyclerViewScrollListener(linearLayoutManager) {
                    override fun onLoadMore(currentPage: Int) {
                        if (currentPage > 0) {
                            // calls api for paginated data
                            activityFeedViewModel.getActivityFeed(currentPage)
                        }
                    }
                }
            setPaginationScrollListener(paginationScrollListener)
        }
    }

    //todo: set loader color using style
    private fun initSwipeRefreshLayout() {
        mSwipeRefreshLayout = binding.swipeRefreshLayout
        mSwipeRefreshLayout.setColorSchemeColors(
            //todo: change this color as per the style
            ContextCompat.getColor(
                requireContext(),
                R.color.lm_feed_majorelle_blue
            )
        )

        mSwipeRefreshLayout.setOnRefreshListener {
            fetchData(true)
        }
    }

    private fun fetchData(fromRefresh: Boolean = false) {
        if (fromRefresh) {
            mSwipeRefreshLayout.isRefreshing = true
            binding.rvActivityFeed.resetScrollListenerData()
        } else {
            LMFeedProgressBarHelper.showProgress(binding.progressBar)
        }
        activityFeedViewModel.getActivityFeed(1)
    }

    private fun observeData() {
        // observes get notification feed response
        activityFeedViewModel.activityFeedResponse.observe(viewLifecycleOwner) { pair ->
            observeNotificationFeed(pair)
        }

        activityFeedViewModel.errorMessageEventFlow.onEach { response ->
            observeErrorMessage(response)
        }.observeInLifecycle(viewLifecycleOwner)
    }

    private fun observeNotificationFeed(pair: Pair<Int, List<LMFeedActivityViewData>>) {
        //hide progress bar
        LMFeedProgressBarHelper.hideProgress(binding.progressBar)

        //page in api send
        val page = pair.first

        //list of activities
        val activities = pair.second

        //if pull to refresh is called
        if (mSwipeRefreshLayout.isRefreshing) {
            setFeedAndScrollToTop(activities)
            mSwipeRefreshLayout.isRefreshing = false
            return
        }

        //normal adding
        if (page == 1) {
            checkForNoActivity(activities)
            setFeedAndScrollToTop(activities)
        } else {
            binding.rvActivityFeed.addActivities(activities)
        }
    }

    //set activities through diff utils and scroll to top of the feed
    private fun setFeedAndScrollToTop(feed: List<LMFeedActivityViewData>) {
        binding.rvActivityFeed.apply {
            replaceActivities(feed)
            scrollToPosition(0)
        }
    }

    //checks if there is any activity or not
    private fun checkForNoActivity(feed: List<LMFeedBaseViewType>) {
        if (feed.isNotEmpty()) {
            binding.apply {
                layoutNoActivity.hide()
                rvActivityFeed.show()
            }
        } else {
            binding.apply {
                layoutNoActivity.show()
                rvActivityFeed.hide()
            }
        }
    }

    private fun observeErrorMessage(response: LMFeedActivityFeedViewModel.ErrorMessageEvent) {
        when (response) {
            is LMFeedActivityFeedViewModel.ErrorMessageEvent.GetActivityFeed -> {
                val errorMessage = response.errorMessage
                LMFeedViewUtils.showErrorMessageToast(requireContext(), errorMessage)
                requireActivity().finish()
            }

            is LMFeedActivityFeedViewModel.ErrorMessageEvent.MarkReadNotification -> {
                val errorMessage = response.errorMessage
                LMFeedViewUtils.showErrorMessageToast(requireContext(), errorMessage)
            }
        }
    }

    override fun onActivityFeedItemClicked(
        position: Int,
        activityViewData: LMFeedActivityViewData
    ) {
        //mark the notification as read
        val updatedActivityViewData = activityViewData.toBuilder()
            .isRead(true)
            .build()
        binding.rvActivityFeed.updateActivity(position, updatedActivityViewData)

        // call api to mark notification as read
        activityFeedViewModel.markReadActivity(activityViewData.id)

        // handle route
        val routeIntent = LMFeedRoute.getRouteIntent(
            requireContext(),
            activityViewData.cta,
        )
        if (routeIntent != null) {
            requireContext().startActivity(routeIntent)
        }
    }
}