package com.likeminds.feed.android.core.topicselection.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.databinding.LmFeedFragmentTopicSelectionBinding
import com.likeminds.feed.android.core.topics.model.LMFeedTopicViewData
import com.likeminds.feed.android.core.topicselection.adapter.LMFeedTopicSelectionAdapterListener
import com.likeminds.feed.android.core.topicselection.model.*
import com.likeminds.feed.android.core.topicselection.view.LMFeedTopicSelectionActivity.Companion.LM_FEED_TOPIC_SELECTION_EXTRAS
import com.likeminds.feed.android.core.topicselection.view.LMFeedTopicSelectionActivity.Companion.LM_FEED_TOPIC_SELECTION_RESULT_EXTRAS
import com.likeminds.feed.android.core.topicselection.viewmodel.LMFeedTopicSelectionViewModel
import com.likeminds.feed.android.core.ui.base.styles.setStyle
import com.likeminds.feed.android.core.ui.base.views.LMFeedFAB
import com.likeminds.feed.android.core.ui.widgets.headerview.view.LMFeedHeaderView
import com.likeminds.feed.android.core.ui.widgets.noentitylayout.view.LMFeedNoEntityLayoutView
import com.likeminds.feed.android.core.ui.widgets.searchbar.view.LMFeedSearchBarListener
import com.likeminds.feed.android.core.ui.widgets.searchbar.view.LMFeedSearchBarView
import com.likeminds.feed.android.core.utils.*
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.hide
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.show

open class LMFeedTopicSelectionFragment :
    Fragment(),
    LMFeedTopicSelectionAdapterListener {

    private lateinit var binding: LmFeedFragmentTopicSelectionBinding

    private val topicSelectionViewModel: LMFeedTopicSelectionViewModel by viewModels()

    private lateinit var topicSelectionExtras: LMFeedTopicSelectionExtras

    private var selectedTopics = 0
    private var searchKeyword: String? = null

    companion object {
        const val TAG = "LMFeedTopicSelectionFragment"

        fun getInstance(topicSelectionExtras: LMFeedTopicSelectionExtras): LMFeedTopicSelectionFragment {
            val topicSelectionFragment = LMFeedTopicSelectionFragment()
            val bundle = Bundle()
            bundle.putParcelable(LM_FEED_TOPIC_SELECTION_EXTRAS, topicSelectionExtras)
            topicSelectionFragment.arguments = bundle
            return topicSelectionFragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        receiveExtras()
    }

    private fun receiveExtras() {
        topicSelectionExtras = LMFeedExtrasUtil.getParcelable(
            arguments,
            LMFeedTopicSelectionActivity.LM_FEED_TOPIC_SELECTION_EXTRAS,
            LMFeedTopicSelectionExtras::class.java
        ) ?: throw emptyExtrasException(TAG)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LmFeedFragmentTopicSelectionBinding.inflate(layoutInflater)

        binding.apply {
            //set background color
            val backgroundColor =
                LMFeedStyleTransformer.topicSelectionFragmentViewStyle.backgroundColor
            backgroundColor?.let { color ->
                root.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        color
                    )
                )
            }

            customizeTopicSelectionHeaderView(headerViewTopicSelection)
            customizeNoTopicsLayout(layoutNoTopics)
            customizeSubmitTopicsFab(fabSubmitSelectedTopics)
            customizeSearchBar(searchBar)
            return root
        }
    }

    //customizes the topic selection header view
    protected open fun customizeTopicSelectionHeaderView(headerViewTopicSelection: LMFeedHeaderView) {
        headerViewTopicSelection.apply {
            val headerStyle = LMFeedStyleTransformer.topicSelectionFragmentViewStyle.headerViewStyle

            setStyle(headerStyle)
            setTitleText(getString(R.string.lm_feed_select_topic))
        }
    }

    //customizes the no topics layout
    protected open fun customizeNoTopicsLayout(layoutNoTopics: LMFeedNoEntityLayoutView) {
        layoutNoTopics.apply {
            val noTopicsLayoutStyle =
                LMFeedStyleTransformer.topicSelectionFragmentViewStyle.noTopicsLayoutViewStyle

            setStyle(noTopicsLayoutStyle)
            setTitleText(getString(R.string.lm_feed_no_topics_yet))
        }
    }

    //customizes the fab to submit the selected topics
    protected open fun customizeSubmitTopicsFab(fabSubmitSelectedTopics: LMFeedFAB) {
        fabSubmitSelectedTopics.apply {
            val submitSelectedTopicsFABStyle =
                LMFeedStyleTransformer.topicSelectionFragmentViewStyle.submitSelectedTopicsFABStyle

            setStyle(submitSelectedTopicsFABStyle)
        }
    }

    //customizes the search bar to search for the topics
    protected open fun customizeSearchBar(searchBar: LMFeedSearchBarView) {
        searchBar.apply {
            val searchBarStyle =
                LMFeedStyleTransformer.topicSelectionFragmentViewStyle.topicSearchBarViewStyle

            setStyle(searchBarStyle)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchData()
        observeData()
        initUI()
        initListeners()
    }

    private fun fetchData() {
        checkForSelectedTopics()

        //calls api to get the list of topics
        topicSelectionViewModel.getTopics(
            topicSelectionExtras.showAllTopicFilter,
            topicSelectionExtras.showEnabledTopicOnly,
            1,
            null
        )
    }

    private fun observeData() {
        topicSelectionViewModel.topicsViewData.observe(viewLifecycleOwner) { response ->
            val topics = response.second

            binding.apply {
                if (rvTopics.getAllTopics().isEmpty() && topics.isEmpty()) {
                    rvTopics.hide()
                    layoutNoTopics.show()
                    fabSubmitSelectedTopics.hide()
                    rvTopics.clearAllTopicsAndNotify()
                } else {
                    rvTopics.show()
                    layoutNoTopics.hide()
                    fabSubmitSelectedTopics.show()
                    rvTopics.addAllTopics(topics)
                }
            }
        }

        topicSelectionViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            LMFeedViewUtils.showErrorMessageToast(requireContext(), errorMessage)
        }
    }

    //check whether extras contains selected and disabled topics and set it to view model
    private fun checkForSelectedTopics() {
        val previousSelectedTopics = topicSelectionExtras.selectedTopics
        val disabledTopics = topicSelectionExtras.disabledTopics

        topicSelectionViewModel.setPreviousSelectedTopics(previousSelectedTopics)
        topicSelectionViewModel.setPreviousDisabledTopics(disabledTopics)

        selectedTopics = previousSelectedTopics?.size ?: 0
        updateSelectedTopicsCount()
    }

    //update selected count in subtitle
    private fun updateSelectedTopicsCount() {
        if (selectedTopics > 0) {
            binding.headerViewTopicSelection.setSubTitleText(
                getString(
                    R.string.lm_feed_topics_selected,
                    selectedTopics
                )
            )
        }
    }

    private fun initUI() {
        initSearchView()
        initRecyclerView()
    }

    private fun initSearchView() {
        binding.searchBar.apply {
            initialize(lifecycleScope)
            setSearchViewListener(object : LMFeedSearchBarListener {

                override fun onSearchViewClosed() {
                    super.onSearchViewClosed()
                    hide()
                    updateSearchedTopics(null, topicSelectionExtras.showAllTopicFilter)
                }

                override fun onSearchCrossed() {
                    super.onSearchCrossed()
                    updateSearchedTopics(null, topicSelectionExtras.showAllTopicFilter)
                }

                override fun onKeywordEntered(keyword: String) {
                    super.onKeywordEntered(keyword)
                    updateSearchedTopics(keyword, false)
                }

                override fun onEmptyKeywordEntered() {
                    super.onEmptyKeywordEntered()
                    if (!searchKeyword.isNullOrEmpty()) {
                        updateSearchedTopics(null, topicSelectionExtras.showAllTopicFilter)
                    }
                }
            })
            observeSearchView(true)
        }
    }

    //reset data and show data as per the entered keyword
    private fun updateSearchedTopics(keyword: String?, showAllTopicFilter: Boolean) {
        binding.rvTopics.apply {
            resetScrollListenerData()
            clearAllTopicsAndNotify()
        }
        searchKeyword = keyword
        topicSelectionViewModel.getTopics(
            showAllTopicFilter,
            topicSelectionExtras.showEnabledTopicOnly,
            1,
            keyword
        )
    }

    private fun initRecyclerView() {
        binding.rvTopics.apply {
            setAdapter(this@LMFeedTopicSelectionFragment)
            //set scroll listener
            val paginationScrollListener =
                object : LMFeedEndlessRecyclerViewScrollListener(linearLayoutManager) {
                    override fun onLoadMore(currentPage: Int) {
                        if (currentPage > 0) {
                            // calls api for paginated data
                            topicSelectionViewModel.getTopics(
                                topicSelectionExtras.showAllTopicFilter,
                                topicSelectionExtras.showEnabledTopicOnly,
                                currentPage,
                                searchKeyword
                            )
                        }
                    }
                }
            setPaginationScrollListener(paginationScrollListener)
        }
    }

    private fun initListeners() {
        binding.apply {
            headerViewTopicSelection.setSearchIconClickListener {
                onSearchIconClicked()
            }

            headerViewTopicSelection.setNavigationIconClickListener {
                onNavigationIconClicked()
            }

            fabSubmitSelectedTopics.setOnClickListener {
                onSubmitFABClicked()
            }
        }
    }

    protected open fun onSearchIconClicked() {
        binding.searchBar.apply {
            show()
            post {
                openSearch()
            }
        }
    }

    protected open fun onNavigationIconClicked() {
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    protected open fun onSubmitFABClicked() {
        binding.rvTopics.apply {
            //check for all topic is selected
            val allTopicViewData = getAllTopics().find {
                it is LMFeedAllTopicsViewData
            } as? LMFeedAllTopicsViewData

            //create result extras
            val resultExtras = if (allTopicViewData != null && allTopicViewData.isSelected) {
                LMFeedTopicSelectionResultExtras.Builder()
                    .isAllTopicSelected(true)
                    .build()
            } else {
                val selectedTopics = getAllTopics().filter {
                    it is LMFeedTopicViewData && it.isSelected
                }.map {
                    it as LMFeedTopicViewData
                }
                LMFeedTopicSelectionResultExtras.Builder()
                    .selectedTopics(selectedTopics)
                    .build()
            }

            //send result
            val resultIntent = Intent().apply {
                putExtra(LM_FEED_TOPIC_SELECTION_RESULT_EXTRAS, resultExtras)
            }
            requireActivity().setResult(Activity.RESULT_OK, resultIntent)
            requireActivity().finish()
        }
    }

    override fun onTopicSelected(position: Int, topic: LMFeedTopicViewData) {
        super.onTopicSelected(position, topic)

        binding.rvTopics.apply {
            val allTopicViewData = getAllTopics().find {
                it is LMFeedAllTopicsViewData
            } as? LMFeedAllTopicsViewData

            //check all topic filter exists and it is selected
            if (allTopicViewData != null && allTopicViewData.isSelected) {
                //update view data to not selected
                val updatedAllTopicViewData = allTopicViewData.toBuilder()
                    .isSelected(false)
                    .build()

                updateTopicAtIndex(0, updatedAllTopicViewData)
            }

            //update topic
            val updatedTopic = if (topic.isSelected) {
                selectedTopics--
                topicSelectionViewModel.removeSelectedTopic(topic)
                topic.toBuilder()
                    .isSelected(false)
                    .build()
            } else {
                selectedTopics++
                topicSelectionViewModel.addSelectedTopic(topic)
                topic.toBuilder()
                    .isSelected(true)
                    .build()
            }

            //update recycle
            updateTopicAtIndex(position, updatedTopic)

            //update sub title
            updateSelectedTopicsCount()
        }
    }

    override fun onAllTopicsSelected(position: Int, allTopicsViewData: LMFeedAllTopicsViewData) {
        super.onAllTopicsSelected(position, allTopicsViewData)

        binding.rvTopics.apply {
            if (allTopicsViewData.isSelected) return

            //update lmFeedAllTopic
            val updateAllTopic = allTopicsViewData.toBuilder()
                .isSelected(true)
                .build()

            //update recyclerview for all topics
            updateTopicAtIndex(position, updateAllTopic)

            //update other topics
            getAllTopics().forEachIndexed { index, topic ->
                if (topic is LMFeedTopicViewData) {
                    val updatedTopic = topic.toBuilder().isSelected(false).build()

                    updateTopicAtIndex(index, updatedTopic)
                }
            }

            //update sub title
            selectedTopics = 0
            updateSelectedTopicsCount()
            topicSelectionViewModel.clearSelectedTopic()
        }
    }
}