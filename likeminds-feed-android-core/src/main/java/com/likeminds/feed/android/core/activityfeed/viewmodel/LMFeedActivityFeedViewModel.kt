package com.likeminds.feed.android.core.activityfeed.viewmodel

import androidx.lifecycle.*
import com.likeminds.feed.android.core.activityfeed.model.LMFeedActivityViewData
import com.likeminds.feed.android.core.utils.LMFeedViewDataConvertor
import com.likeminds.feed.android.core.utils.coroutine.launchIO
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.notificationfeed.model.GetNotificationFeedRequest
import com.likeminds.likemindsfeed.notificationfeed.model.MarkReadNotificationRequest
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class LMFeedActivityFeedViewModel : ViewModel() {

    private val lmFeedClient: LMFeedClient by lazy {
        LMFeedClient.getInstance()
    }

    private val _activityFeedResponse by lazy {
        MutableLiveData<Pair<Int, List<LMFeedActivityViewData>>>()
    }

    val activityFeedResponse: LiveData<Pair<Int, List<LMFeedActivityViewData>>> by lazy {
        _activityFeedResponse
    }

    private val errorMessageChannel by lazy {
        Channel<ErrorMessageEvent>(Channel.BUFFERED)
    }

    val errorMessageEventFlow by lazy {
        errorMessageChannel.receiveAsFlow()
    }

    sealed class ErrorMessageEvent {
        data class GetActivityFeed(val errorMessage: String?) : ErrorMessageEvent()

        data class MarkReadNotification(val errorMessage: String?) : ErrorMessageEvent()
    }

    companion object {
        const val PAGE_SIZE = 20
    }

    // get activity feed
    fun getActivityFeed(page: Int) {
        viewModelScope.launchIO {
            val request = GetNotificationFeedRequest.Builder()
                .page(page)
                .pageSize(PAGE_SIZE)
                .build()

            //call notification feed api
            val response = lmFeedClient.getNotificationFeed(request)

            if (response.success) {
                val data = response.data ?: return@launchIO
                val activities = data.activities
                val usersMap = data.users

                //convert to view data
                val listOfActivityViewData =
                    LMFeedViewDataConvertor.convertActivities(activities, usersMap)

                //send it to ui
                _activityFeedResponse.postValue(Pair(page, listOfActivityViewData))
            } else {
                //for error
                errorMessageChannel.send(ErrorMessageEvent.GetActivityFeed(response.errorMessage))
            }
        }
    }

    fun markReadActivity(activityId: String) {
        viewModelScope.launchIO {
            val request = MarkReadNotificationRequest.Builder()
                .activityId(activityId)
                .build()

            //call notification feed api
            val response = lmFeedClient.markReadNotification(request)

            if (!response.success) {
                errorMessageChannel.send(ErrorMessageEvent.MarkReadNotification(response.errorMessage))
            }
        }
    }
}