package com.likeminds.feed.android.core.delete.viewmodel

import androidx.lifecycle.*
import com.likeminds.feed.android.core.delete.model.LMFeedReasonChooseViewData
import com.likeminds.feed.android.core.utils.LMFeedViewDataConvertor
import com.likeminds.feed.android.core.utils.coroutine.launchIO
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.LMResponse
import com.likeminds.likemindsfeed.moderation.model.GetReportTagsRequest
import com.likeminds.likemindsfeed.moderation.model.GetReportTagsResponse

class LMFeedReasonChooseViewModel : ViewModel() {

    private val lmFeedClient by lazy {
        LMFeedClient.getInstance()
    }

    private val _listOfTagViewData by lazy {
        MutableLiveData<List<LMFeedReasonChooseViewData>>()
    }

    val listOfTagViewData: LiveData<List<LMFeedReasonChooseViewData>> by lazy {
        _listOfTagViewData
    }

    private val _errorMessage: MutableLiveData<String?> by lazy {
        MutableLiveData()
    }

    val errorMessage: LiveData<String?> by lazy {
        _errorMessage
    }

    companion object {
        const val REPORT_TAG_TYPE = 0
    }

    // calls api and fetches report tags
    fun getReportTags() {
        viewModelScope.launchIO {
            // builds report tag request
            val request = GetReportTagsRequest.Builder()
                .type(REPORT_TAG_TYPE)
                .build()

            reportTagsFetched(lmFeedClient.getReportTags(request))
        }
    }

    // processes report tag request and sends response to UI
    private fun reportTagsFetched(response: LMResponse<GetReportTagsResponse>) {
        if (response.success) {
            val data = response.data ?: return
            val tags = data.tags
            val tagsViewData = LMFeedViewDataConvertor.convertDeleteTag(tags)
            val lastTag = tagsViewData.last().toBuilder()
                .hideBottomLine(true)
                .build()

            tagsViewData[tagsViewData.lastIndex] = lastTag

            _listOfTagViewData.postValue(tagsViewData.toList())
        } else {
            _errorMessage.postValue(response.errorMessage)
        }
    }
}