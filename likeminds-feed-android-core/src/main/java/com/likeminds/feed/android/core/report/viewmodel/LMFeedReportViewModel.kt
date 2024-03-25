package com.likeminds.feed.android.core.report.viewmodel

import androidx.lifecycle.*
import com.likeminds.feed.android.core.report.model.LMFeedReportTagViewData
import com.likeminds.feed.android.core.utils.LMFeedViewDataConvertor
import com.likeminds.feed.android.core.utils.coroutine.launchIO
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.LMResponse
import com.likeminds.likemindsfeed.moderation.model.*

class LMFeedReportViewModel : ViewModel() {

    private val lmFeedClient by lazy {
        LMFeedClient.getInstance()
    }

    private val _listOfTagViewData by lazy {
        MutableLiveData<List<LMFeedReportTagViewData>>()
    }
    val listOfTagViewData: LiveData<List<LMFeedReportTagViewData>> by lazy {
        _listOfTagViewData
    }

    private val _errorMessage: MutableLiveData<String?> by lazy {
        MutableLiveData()
    }

    val errorMessage: LiveData<String?> by lazy {
        _errorMessage
    }

    private val _postReportResponse by lazy {
        MutableLiveData<Boolean>()
    }

    val postReportResponse: LiveData<Boolean> by lazy {
        _postReportResponse
    }

    companion object {
        const val REPORT_TAG_TYPE = 3
    }

    //get report tags for reporting
    fun getReportTags() {
        viewModelScope.launchIO {
            val request = GetReportTagsRequest.Builder()
                .type(REPORT_TAG_TYPE)
                .build()

            reportTagsFetched(lmFeedClient.getReportTags(request))
        }
    }

    //to convert to TagViewData
    private fun reportTagsFetched(response: LMResponse<GetReportTagsResponse>) {
        if (response.success) {
            val data = response.data ?: return
            val tags = data.tags
            val tagsViewData = LMFeedViewDataConvertor.convertReportTag(tags)
            _listOfTagViewData.postValue(tagsViewData)
        } else {
            _errorMessage.postValue(response.errorMessage)
        }
    }

    //for reporting post/comment/reply
    fun postReport(
        entityId: String,
        uuid: String,
        entityType: Int,
        tagId: Int?,
        reason: String?
    ) {
        viewModelScope.launchIO {
            //if reason is empty then send [null] in request
            val updatedReason = if (reason.isNullOrEmpty()) {
                null
            } else {
                reason
            }

            val request = PostReportRequest.Builder()
                .entityId(entityId)
                .uuid(uuid)
                .entityType(entityType)
                .tagId(tagId ?: 0)
                .reason(updatedReason)
                .build()

            val response = lmFeedClient.postReport(request)

            if (response.success) {
                _postReportResponse.postValue(true)
            } else {
                _errorMessage.postValue(response.errorMessage)
            }
        }
    }
}