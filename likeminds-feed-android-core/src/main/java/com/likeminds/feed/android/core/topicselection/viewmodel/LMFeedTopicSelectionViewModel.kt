package com.likeminds.feed.android.core.topicselection.viewmodel

import androidx.lifecycle.*
import com.likeminds.feed.android.core.topics.model.LMFeedTopicViewData
import com.likeminds.feed.android.core.topicselection.model.LMFeedAllTopicsViewData
import com.likeminds.feed.android.core.utils.LMFeedViewDataConvertor
import com.likeminds.feed.android.core.utils.base.LMFeedBaseViewType
import com.likeminds.feed.android.core.utils.coroutine.launchIO
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.topic.model.GetTopicRequest

class LMFeedTopicSelectionViewModel : ViewModel() {

    companion object {
        const val SEARCH_TYPE = "name"
        const val PAGE_SIZE = 10
    }

    private val lmFeedClient by lazy {
        LMFeedClient.getInstance()
    }

    //first -> page
    //second -> list of topics
    private val _topicsViewData by lazy {
        MutableLiveData<Pair<Int, List<LMFeedBaseViewType>>>()
    }

    val topicsViewData: LiveData<Pair<Int, List<LMFeedBaseViewType>>> by lazy {
        _topicsViewData
    }

    private val _errorMessage by lazy {
        MutableLiveData<String?>()
    }

    val errorMessage: LiveData<String?> by lazy {
        _errorMessage
    }

    private val selectedTopics by lazy {
        HashMap<String, LMFeedTopicViewData>()
    }

    private val disabledTopics by lazy {
        ArrayList<LMFeedTopicViewData>()
    }

    //set previous selected topics to map
    fun setPreviousSelectedTopics(selectedTopics: List<LMFeedTopicViewData>?) {
        if (selectedTopics.isNullOrEmpty()) return
        selectedTopics.forEach { topic ->
            this.selectedTopics[topic.id] = topic
        }
    }

    //set previous disabled topics to map
    fun setPreviousDisabledTopics(disabledTopics: List<LMFeedTopicViewData>?) {
        if (disabledTopics.isNullOrEmpty()) return
        val updatedDisabledTopics = disabledTopics.map {
            it.toBuilder().isSelected(true).build()
        }
        this.disabledTopics.addAll(updatedDisabledTopics)
    }

    //get topics as per requirement
    fun getTopics(
        showAllTopicFilter: Boolean,
        showEnabledTopicOnly: Boolean,
        page: Int,
        searchString: String? = null
    ) {
        viewModelScope.launchIO {

            val requestBuilder = GetTopicRequest.Builder()
                .page(page)
                .pageSize(PAGE_SIZE)

            if (!searchString.isNullOrEmpty()) {
                requestBuilder.search(searchString)
                    .searchType(SEARCH_TYPE)
            }

            if (showEnabledTopicOnly) {
                requestBuilder.isEnabled(true)
            }

            val request = requestBuilder.build()

            val response = lmFeedClient.getTopics(request)

            if (response.success) {
                val topics = response.data?.topics ?: emptyList()
                val topicsViewData = topics.map { topic ->
                    val topicViewData = LMFeedViewDataConvertor.convertTopic(topic)

                    if (selectedTopics.containsKey(topicViewData.id)) {
                        topicViewData.toBuilder().isSelected(true).build()
                    } else {
                        topicViewData.toBuilder().isSelected(false).build()
                    }
                }

                val viewTypes = ArrayList<LMFeedBaseViewType>()

                if (page == 1 && disabledTopics.isNotEmpty()) {
                    viewTypes.addAll(disabledTopics)
                }

                if (page == 1 && showAllTopicFilter) {
                    val allTopicsFilter =
                        LMFeedAllTopicsViewData.Builder()
                            .isSelected(selectedTopics.isEmpty())
                            .build()
                    viewTypes.add(allTopicsFilter)
                }

                viewTypes.addAll(topicsViewData)

                _topicsViewData.postValue(Pair(page, viewTypes.toList()))
            } else {
                val errorMessage = response.errorMessage
                _errorMessage.postValue(errorMessage)
            }
        }
    }

    //add selected topic into map
    fun addSelectedTopic(topicViewData: LMFeedTopicViewData) {
        selectedTopics[topicViewData.id] = topicViewData
    }

    //remove topic from map
    fun removeSelectedTopic(topicViewData: LMFeedTopicViewData) {
        selectedTopics.remove(topicViewData.id)
    }

    fun clearSelectedTopic() {
        selectedTopics.clear()
    }
}