package com.likeminds.feed.android.core.poll.view

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.likeminds.feed.android.core.LMFeedCoreApplication
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.databinding.LmFeedFragmentPollResultsTabBinding
import com.likeminds.feed.android.core.poll.adapter.LMFeedPollVoteResultsAdapterListener
import com.likeminds.feed.android.core.poll.model.LMFeedPollResultsTabExtras
import com.likeminds.feed.android.core.poll.viewmodel.LMFeedPollResultsViewModel
import com.likeminds.feed.android.core.ui.widgets.noentitylayout.view.LMFeedNoEntityLayoutView
import com.likeminds.feed.android.core.utils.*
import com.likeminds.feed.android.core.utils.user.LMFeedUserViewData

open class LMFeedPollResultsTabFragment : Fragment(), LMFeedPollVoteResultsAdapterListener {

    private lateinit var binding: LmFeedFragmentPollResultsTabBinding

    private lateinit var pollResultsTabExtras: LMFeedPollResultsTabExtras

    private val pollResultsViewModel: LMFeedPollResultsViewModel by viewModels()

    companion object {
        const val TAG = "LMFeedPollResultsTabFragment"
        const val LM_FEED_POLL_RESULT_TAB_EXTRA = "LM_FEED_POLL_RESULT_TAB_EXTRA"

        @JvmStatic
        fun addFragment(pollResultsTabExtras: LMFeedPollResultsTabExtras): LMFeedPollResultsTabFragment {
            val fragment = LMFeedPollResultsTabFragment()
            val bundle = Bundle()
            bundle.putParcelable(LM_FEED_POLL_RESULT_TAB_EXTRA, pollResultsTabExtras)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        receiveExtras()
    }

    private fun receiveExtras() {
        arguments?.let {
            pollResultsTabExtras = LMFeedExtrasUtil.getParcelable(
                it,
                LM_FEED_POLL_RESULT_TAB_EXTRA,
                LMFeedPollResultsTabExtras::class.java
            ) ?: throw emptyExtrasException(TAG)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LmFeedFragmentPollResultsTabBinding.inflate(layoutInflater)

        customizeNoPollResultsLayout(binding.layoutNoResults)

        return binding.root
    }

    protected open fun customizeNoPollResultsLayout(layoutNoResults: LMFeedNoEntityLayoutView) {
        layoutNoResults.apply {
            val noResultsLayoutStyle =
                LMFeedStyleTransformer.pollResultsFragmentViewStyle.noResultsLayoutViewStyle

            setStyle(noResultsLayoutStyle)
            setTitleText(getString(R.string.lm_feed_no_responses))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
        fetchData()
        observeData()
    }

    private fun initUI() {
        binding.rvPollVoteResults.apply {
            setAdapter(this@LMFeedPollResultsTabFragment)

            //set scroll listener
            val paginationScrollListener =
                object : LMFeedEndlessRecyclerViewScrollListener(linearLayoutManager) {
                    override fun onLoadMore(currentPage: Int) {
                        // calls api for paginated data
                        if (currentPage > 0) {
                            pollResultsViewModel.getPollVotes(
                                pollResultsTabExtras.pollId,
                                pollResultsTabExtras.pollOptionId,
                                currentPage
                            )
                        }
                    }
                }
            setPaginationScrollListener(paginationScrollListener)
        }
    }

    private fun fetchData() {
        LMFeedProgressBarHelper.showProgress(binding.progressBar)
        
        pollResultsViewModel.getPollVotes(
            pollResultsTabExtras.pollId,
            pollResultsTabExtras.pollOptionId,
            1
        )
    }

    private fun observeData() {
        pollResultsViewModel.pollVotesResponse.observe(viewLifecycleOwner) { response ->
            LMFeedProgressBarHelper.hideProgress(binding.progressBar)

            val page = response.first
            val usersVoted = response.second.usersVoted

            if (page == 1) {
                binding.rvPollVoteResults.replacePollVotes(usersVoted)
            } else {
                binding.rvPollVoteResults.addPollVotes(usersVoted)
            }
        }
    }

    override fun onPollVoteResultItemClicked(
        position: Int,
        pollVoteResultItem: LMFeedUserViewData
    ) {
        super.onPollVoteResultItemClicked(position, pollVoteResultItem)

        val coreCallback = LMFeedCoreApplication.getLMFeedCoreCallback()
        coreCallback?.openProfile(pollVoteResultItem)
    }
}