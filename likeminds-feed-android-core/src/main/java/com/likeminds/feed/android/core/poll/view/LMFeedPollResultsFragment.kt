package com.likeminds.feed.android.core.poll.view

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.databinding.LmFeedFragmentPollResultsBinding
import com.likeminds.feed.android.core.databinding.LmFeedLayoutPollResultsTabBinding
import com.likeminds.feed.android.core.poll.adapter.LMFeedPollResultsTabAdapter
import com.likeminds.feed.android.core.poll.model.LMFeedPollResultsExtras
import com.likeminds.feed.android.core.poll.view.LMFeedPollResultsActivity.Companion.LM_FEED_POLL_RESULTS_EXTRAS
import com.likeminds.feed.android.core.ui.base.styles.setStyle
import com.likeminds.feed.android.core.ui.base.views.LMFeedTextView
import com.likeminds.feed.android.core.ui.widgets.headerview.view.LMFeedHeaderView
import com.likeminds.feed.android.core.utils.*

open class LMFeedPollResultsFragment : Fragment() {

    private lateinit var binding: LmFeedFragmentPollResultsBinding
    private lateinit var pollResultsExtras: LMFeedPollResultsExtras

    private lateinit var pollResultsTabAdapter: LMFeedPollResultsTabAdapter

    companion object {
        const val TAG = "LMFeedPollResultsFragment"

        fun getInstance(pollResultsExtras: LMFeedPollResultsExtras): LMFeedPollResultsFragment {
            val pollResultsFragment = LMFeedPollResultsFragment()
            val bundle = Bundle()
            bundle.putParcelable(LM_FEED_POLL_RESULTS_EXTRAS, pollResultsExtras)
            pollResultsFragment.arguments = bundle
            return pollResultsFragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        receiveExtras()
    }

    private fun receiveExtras() {
        arguments?.let {
            pollResultsExtras = LMFeedExtrasUtil.getParcelable(
                it,
                LM_FEED_POLL_RESULTS_EXTRAS,
                LMFeedPollResultsExtras::class.java
            ) ?: throw emptyExtrasException(TAG)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LmFeedFragmentPollResultsBinding.inflate(inflater, container, false)

        binding.apply {
            customizePollResultsHeaderView(headerViewPollResults)

            //set background color
            val backgroundColor =
                LMFeedStyleTransformer.pollResultsFragmentViewStyle.backgroundColor
            backgroundColor?.let { color ->
                root.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        color
                    )
                )
            }
        }
        return binding.root
    }

    //customizes the header view of the poll results fragment with the header style set for the poll results fragment
    protected open fun customizePollResultsHeaderView(headerViewPollResults: LMFeedHeaderView) {
        headerViewPollResults.apply {
            setStyle(LMFeedStyleTransformer.pollResultsFragmentViewStyle.headerViewStyle)

            setTitleText(getString(R.string.lm_feed_poll_results))
        }
    }

    //customizes the poll option and poll option count view of the poll results tab
    protected open fun customizePollResultsTabTextView(
        tvPollOptionCount: LMFeedTextView,
        tvPollOptionText: LMFeedTextView
    ) {
        val pollResultsTabTextViewStyle =
            LMFeedStyleTransformer.pollResultsFragmentViewStyle.pollResultsTabTextViewStyle

        tvPollOptionCount.setStyle(pollResultsTabTextViewStyle)
        tvPollOptionText.setStyle(pollResultsTabTextViewStyle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initPollResultsTabLayout()
    }

    private fun initPollResultsTabLayout() {
        pollResultsTabAdapter = LMFeedPollResultsTabAdapter(
            this,
            pollResultsExtras.pollId,
            pollResultsExtras.pollOptions
        )

        binding.apply {
            viewPagerPollResults.adapter = pollResultsTabAdapter

            val screenWidth = LMFeedViewUtils.getDeviceDimension(requireContext()).first
            TabLayoutMediator(
                tabLayoutPollResults,
                viewPagerPollResults
            ) { tab, position ->
                val pollOption = pollResultsExtras.pollOptions[position]
                val tabView = LmFeedLayoutPollResultsTabBinding.inflate(layoutInflater)
                tabView.apply {
                    customizePollResultsTabTextView(tvPollOptionCount, tvPollOptionText)

                    clPollResultsTab.maxWidth = (screenWidth * 0.48).toInt()
                    clPollResultsTab.minWidth = (screenWidth * 0.33).toInt()

                    //set poll option count and text value to tabs
                    pollOptionCount = pollOption.voteCount.toString()
                    pollOptionText = pollOption.text

                    tab.customView = root
                }
            }.attach()


            val tab = if (!pollResultsExtras.selectedPollOptionId.isNullOrEmpty()) {
                val selectedOptionPosition = pollResultsExtras.pollOptions.indexOfFirst {
                    it.id == pollResultsExtras.selectedPollOptionId
                }

                viewPagerPollResults.setCurrentItem(selectedOptionPosition, true)
                tabLayoutPollResults.setScrollPosition(
                    selectedOptionPosition,
                    0f,
                    true
                )
                tabLayoutPollResults.getTabAt(selectedOptionPosition)
            } else {
                tabLayoutPollResults.getTabAt(0)
            }

            tab?.select()
            updatePollResultsTab(
                tab,
                LMFeedStyleTransformer.pollResultsFragmentViewStyle.selectedPollResultsTabColor
            )

            tabLayoutPollResults.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    onPollResultsTabSelected(tab)
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    onPollResultsTabUnselected(tab)
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    onPollResultsTabReselected(tab)
                }
            })
        }
    }

    protected open fun onPollResultsTabSelected(tab: TabLayout.Tab?) {
        updatePollResultsTab(
            tab,
            LMFeedStyleTransformer.pollResultsFragmentViewStyle.selectedPollResultsTabColor
        )

        //todo: analytics
    }

    protected open fun onPollResultsTabUnselected(tab: TabLayout.Tab?) {
        updatePollResultsTab(
            tab,
            LMFeedStyleTransformer.pollResultsFragmentViewStyle.unselectedPollResultsTabColor
        )
    }

    protected open fun onPollResultsTabReselected(tab: TabLayout.Tab?) {
        updatePollResultsTab(
            tab,
            LMFeedStyleTransformer.pollResultsFragmentViewStyle.selectedPollResultsTabColor
        )
    }

    private fun updatePollResultsTab(tab: TabLayout.Tab?, tabColor: Int) {
        val customTabView = tab?.customView ?: return

        val tvPollOptionCount =
            customTabView.findViewById<LMFeedTextView>(R.id.tv_poll_option_count)
                ?: return

        val tvPollOptionText =
            customTabView.findViewById<LMFeedTextView>(R.id.tv_poll_option_text)
                ?: return

        customizePollResultsTabTextView(tvPollOptionCount, tvPollOptionText)

        tvPollOptionCount.apply {
            setTextColor(ContextCompat.getColor(requireContext(), tabColor))
        }
    }
}