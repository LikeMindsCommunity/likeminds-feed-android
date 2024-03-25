package com.likeminds.feed.android.core.likes.view

import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.likeminds.feed.android.core.LMFeedCoreApplication
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.databinding.LmFeedFragmentLikesBinding
import com.likeminds.feed.android.core.likes.adapter.LMFeedLikesAdapterListener
import com.likeminds.feed.android.core.likes.model.LMFeedLikeViewData
import com.likeminds.feed.android.core.likes.model.LMFeedLikesScreenExtras
import com.likeminds.feed.android.core.likes.view.LMFeedLikesActivity.Companion.LM_FEED_LIKES_SCREEN_EXTRAS
import com.likeminds.feed.android.core.likes.viewmodel.LMFeedLikesViewModel
import com.likeminds.feed.android.core.ui.widgets.headerview.view.LMFeedHeaderView
import com.likeminds.feed.android.core.utils.*
import com.likeminds.feed.android.core.utils.analytics.LMFeedAnalytics

open class LMFeedLikesFragment : Fragment(), LMFeedLikesAdapterListener {

    private lateinit var binding: LmFeedFragmentLikesBinding
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout

    private lateinit var likesScreenExtras: LMFeedLikesScreenExtras

    private val likesViewModel: LMFeedLikesViewModel by viewModels()

    companion object {
        const val TAG = "LMFeedLikesFragment"

        fun getInstance(likesScreenExtras: LMFeedLikesScreenExtras): LMFeedLikesFragment {
            val likesFragment = LMFeedLikesFragment()
            val bundle = Bundle()
            bundle.putParcelable(LM_FEED_LIKES_SCREEN_EXTRAS, likesScreenExtras)
            likesFragment.arguments = bundle
            return likesFragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        receiveExtras()
    }

    private fun receiveExtras() {
        likesScreenExtras = LMFeedExtrasUtil.getParcelable(
            arguments,
            LM_FEED_LIKES_SCREEN_EXTRAS,
            LMFeedLikesScreenExtras::class.java
        ) ?: throw emptyExtrasException(TAG)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LmFeedFragmentLikesBinding.inflate(layoutInflater)

        binding.apply {
            customizeLikesFragmentHeaderView(headerViewLikes)

            //set background color
            val backgroundColor =
                LMFeedStyleTransformer.likesFragmentViewStyle.backgroundColor
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

    protected open fun customizeLikesFragmentHeaderView(headerViewLikes: LMFeedHeaderView) {
        headerViewLikes.apply {
            setStyle(LMFeedStyleTransformer.likesFragmentViewStyle.headerViewStyle)

            setTitleText(getString(R.string.lm_feed_likes))
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
        //sends like list open event
        LMFeedAnalytics.sendLikeListOpenEvent(
            likesScreenExtras.postId,
            likesScreenExtras.commentId
        )

        initRecyclerView()
        initSwipeRefreshLayout()
    }

    private fun initRecyclerView() {
        binding.rvLikes.apply {
            setAdapter(this@LMFeedLikesFragment)

            //set scroll listener
            val paginationScrollListener =
                object : LMFeedEndlessRecyclerViewScrollListener(linearLayoutManager) {
                    override fun onLoadMore(currentPage: Int) {
                        // calls api for paginated data
                        if (currentPage > 0) {
                            likesViewModel.getLikesData(
                                likesScreenExtras.postId,
                                likesScreenExtras.commentId,
                                likesScreenExtras.entityType,
                                currentPage
                            )
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

    private fun initListeners() {
        binding.apply {
            headerViewLikes.setNavigationIconClickListener {
                onNavigationIconClick()
            }
        }
    }

    protected open fun onNavigationIconClick() {
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    private fun fetchData(fromRefresh: Boolean = false) {
        if (fromRefresh) {
            mSwipeRefreshLayout.isRefreshing = true
            binding.rvLikes.resetScrollListenerData()
        } else {
            LMFeedProgressBarHelper.showProgress(binding.progressBar)
        }
        likesViewModel.getLikesData(
            likesScreenExtras.postId,
            likesScreenExtras.commentId,
            likesScreenExtras.entityType,
            1
        )
    }

    // observes data
    private fun observeData() {
        // observes likes api response
        likesViewModel.likesResponse.observe(viewLifecycleOwner) { response ->
            LMFeedProgressBarHelper.hideProgress(binding.progressBar)

            val listOfLikes = response.first
            val totalLikes = response.second

            binding.rvLikes.addLikes(listOfLikes)
            setTotalLikesCount(totalLikes)
        }

        // observes error message from likes api and shows toast with error message
        likesViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            LMFeedViewUtils.showErrorMessageToast(requireContext(), error)
            requireActivity().finish()
        }
    }

    //set total likes count on toolbar
    private fun setTotalLikesCount(totalLikes: Int) {
        binding.headerViewLikes.setSubTitleText(
            this.resources.getQuantityString(
                R.plurals.lm_feed_likes_small,
                totalLikes,
                totalLikes
            )
        )

    }

    override fun onUserLikeItemClicked(position: Int, likesViewData: LMFeedLikeViewData) {
        super.onUserLikeItemClicked(position, likesViewData)

        val coreCallback = LMFeedCoreApplication.getLMFeedCoreCallback()
        coreCallback?.openProfile(likesViewData.user)
    }
}