package com.likeminds.feed.android.core.post.create.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.likeminds.feed.android.core.universalfeed.model.LMFeedUserViewData
import com.likeminds.feed.android.core.utils.LMFeedViewDataConvertor
import com.likeminds.feed.android.core.utils.coroutine.launchIO
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.topic.model.GetTopicRequest
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class LMFeedCreatePostViewModel : ViewModel() {

    private val lmFeedClient = LMFeedClient.getInstance()

    private val _loggedInUser: MutableLiveData<LMFeedUserViewData> by lazy { MutableLiveData<LMFeedUserViewData>() }
    val loggedInUser: LiveData<LMFeedUserViewData> by lazy { _loggedInUser }

    private val _showTopicFilter by lazy { MutableLiveData<Boolean>() }
    val showTopicFilter: LiveData<Boolean> by lazy { _showTopicFilter }

    sealed class ErrorMessageEvent {
        data class GetLoggedInUser(val errorMessage: String?) : ErrorMessageEvent()
        data class GetTopic(val errorMessage: String?) : ErrorMessageEvent()
    }

    private val errorEventChannel = Channel<ErrorMessageEvent>(Channel.BUFFERED)
    val errorEventFlow = errorEventChannel.receiveAsFlow()

    // call to get logged user details
    fun getLoggedInUser() {
        viewModelScope.launchIO {
            val response = lmFeedClient.getLoggedInUserWithRights()

            if (response.success) {
                //get user
                val loggedInUser = response.data?.user

                //convert it to [LMFeedUserViewData]
                val userViewData = LMFeedViewDataConvertor.convertUser(loggedInUser)

                //send to UI
                _loggedInUser.postValue(userViewData)
            } else {
                //send error message to UI
                errorEventChannel.send(ErrorMessageEvent.GetLoggedInUser(response.errorMessage))
            }
        }
    }

    //calls to topics api and check whether to show topics view or not
    fun getAllTopics() {
        viewModelScope.launchIO {
            val request = GetTopicRequest.Builder()
                .page(1)
                .pageSize(10)
                .isEnabled(true)
                .build()

            val response = lmFeedClient.getTopics(request)

            if (response.success) {
                val topics = response.data?.topics
                if (topics.isNullOrEmpty()) {
                    _showTopicFilter.postValue(false)
                } else {
                    _showTopicFilter.postValue(true)
                }
            } else {
                _showTopicFilter.postValue(false)
                errorEventChannel.send(ErrorMessageEvent.GetTopic(response.errorMessage))
            }
        }
    }
}