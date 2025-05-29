package com.likeminds.feed.android.core.poll.result.view

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.databinding.LmFeedFragmentPollResultsBinding
import com.likeminds.feed.android.core.databinding.LmFeedLayoutPollResultsTabBinding
import com.likeminds.feed.android.core.poll.result.adapter.LMFeedPollResultsTabAdapter
import com.likeminds.feed.android.core.poll.result.model.LMFeedPollResultsExtras
import com.likeminds.feed.android.core.poll.result.view.LMFeedPollResultsActivity.Companion.LM_FEED_POLL_RESULTS_EXTRAS
import com.likeminds.feed.android.core.ui.base.styles.setStyle
import com.likeminds.feed.android.core.ui.base.views.LMFeedTextView
import com.likeminds.feed.android.core.ui.widgets.headerview.view.LMFeedHeaderView
import com.likeminds.feed.android.core.utils.LMFeedExtrasUtil
import com.likeminds.feed.android.core.utils.LMFeedStyleTransformer
import com.likeminds.feed.android.core.utils.LMFeedViewUtils
import com.likeminds.feed.android.core.utils.emptyExtrasException

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
            val headerViewStyle =
                LMFeedStyleTransformer.pollResultsFragmentViewStyle.headerViewStyle
            setStyle(headerViewStyle)
            setStatusBarColor(headerViewStyle.backgroundColor)
            binding.tabLayoutPollResults.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    headerViewStyle.backgroundColor
                )
            )

            setTitleText(getString(R.string.lm_feed_poll_results))
        }
    }

    @SuppressLint("InlinedApi")
    @Suppress("Deprecation")
    private fun setStatusBarColor(backgroundColor: Int) {
        val window = requireActivity().window
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
            windowInsetsController.isAppearanceLightStatusBars = true
            window.statusBarColor = ContextCompat.getColor(requireContext(), backgroundColor)
        } else {
            val insetsController = WindowInsetsControllerCompat(window, window.decorView)
            insetsController.isAppearanceLightStatusBars = true

            window.decorView.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    backgroundColor
                )
            )
            window.insetsController?.hide(WindowInsets.Type.statusBars())
            window.insetsController?.show(WindowInsets.Type.statusBars())
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

        initListeners()
        initPollResultsTabLayout()
    }

    //initializes all the listeners
    private fun initListeners() {
        binding.headerViewPollResults.setNavigationIconClickListener {
            onNavigationIconClick()
        }
    }

    //initializes poll results tab layout
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

                    //max and min width of the poll results tab
                    clPollResultsTab.maxWidth = (screenWidth * 0.48).toInt()
                    clPollResultsTab.minWidth = (screenWidth * 0.33).toInt()

                    //set poll option count and text value to tabs
                    pollOptionCount = pollOption.voteCount.toString()
                    pollOptionText = pollOption.text

                    tab.customView = root
                }
            }.attach()


            val tab = if (!pollResultsExtras.selectedPollOptionId.isNullOrEmpty()) {
                //finds index of option which is selected by user
                val selectedOptionPosition = pollResultsExtras.pollOptions.indexOfFirst {
                    it.id == pollResultsExtras.selectedPollOptionId
                }

                //sets the current item in the poll results tab to the selected option
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

            //adds tab selected click listener on the poll results tab layout
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

    //processes the event when a poll results tab is selected
    protected open fun onPollResultsTabSelected(tab: TabLayout.Tab?) {
        updatePollResultsTab(
            tab,
            LMFeedStyleTransformer.pollResultsFragmentViewStyle.selectedPollResultsTabColor
        )

        //todo: analytics
    }

    //processes the event when a poll results tab is unselected
    protected open fun onPollResultsTabUnselected(tab: TabLayout.Tab?) {
        updatePollResultsTab(
            tab,
            LMFeedStyleTransformer.pollResultsFragmentViewStyle.unselectedPollResultsTabColor
        )
    }

    //processes the event when a poll results tab is reselected
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

    //customize navigate back icon
    protected open fun onNavigationIconClick() {
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }
}