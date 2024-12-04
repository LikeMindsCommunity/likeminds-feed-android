package com.likeminds.feed.android.core.post.viewmodel

import androidx.lifecycle.*
import com.likeminds.feed.android.core.socialfeed.model.LMFeedPostViewData
import com.likeminds.feed.android.core.topics.model.LMFeedTopicViewData
import com.likeminds.feed.android.core.utils.LMFeedViewDataConvertor
import com.likeminds.feed.android.core.utils.analytics.LMFeedAnalytics
import com.likeminds.feed.android.core.utils.analytics.LMFeedAnalytics.LMFeedScreenNames
import com.likeminds.feed.android.core.utils.base.LMFeedBaseViewType
import com.likeminds.feed.android.core.utils.coroutine.launchIO
import com.likeminds.feed.android.core.utils.user.LMFeedUserViewData
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.post.model.*
import com.likeminds.likemindsfeed.topic.model.GetTopicRequest
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class LMFeedHelperViewModel : ViewModel() {

    private val lmFeedClient: LMFeedClient by lazy {
        LMFeedClient.getInstance()
    }

    private val _postSavedResponse by lazy {
        MutableLiveData<LMFeedPostViewData>()
    }

    val postSavedResponse: LiveData<LMFeedPostViewData> by lazy {
        _postSavedResponse
    }

    private val _postPinnedResponse by lazy {
        MutableLiveData<LMFeedPostViewData>()
    }

    val postPinnedResponse: LiveData<LMFeedPostViewData> by lazy {
        _postPinnedResponse
    }

    private val _deletePostResponse by lazy {
        MutableLiveData<String>()
    }

    val deletePostResponse: LiveData<String> by lazy {
        _deletePostResponse
    }

    private val _unreadNotificationCount by lazy {
        MutableLiveData<Int>()
    }

    val unreadNotificationCount: LiveData<Int> by lazy {
        _unreadNotificationCount
    }

    private val _showTopicFilter by lazy {
        MutableLiveData<Boolean>()
    }

    val showTopicFilter: LiveData<Boolean> by lazy {
        _showTopicFilter
    }

    private val _userResponse by lazy {
        MutableLiveData<LMFeedUserViewData>()
    }

    val userResponse: LiveData<LMFeedUserViewData> by lazy {
        _userResponse
    }

    private val errorMessageChannel by lazy {
        Channel<ErrorMessageEvent>(Channel.BUFFERED)
    }

    val errorMessageEventFlow by lazy {
        errorMessageChannel.receiveAsFlow()
    }

    sealed class ErrorMessageEvent {
        data class LikePost(val postId: String, val errorMessage: String?) : ErrorMessageEvent()

        data class SavePost(val postId: String, val errorMessage: String?) : ErrorMessageEvent()

        data class DeletePost(val errorMessage: String?) : ErrorMessageEvent()

        data class GetUnreadNotificationCount(val errorMessage: String?) : ErrorMessageEvent()

        data class PinPost(val postId: String, val errorMessage: String?) : ErrorMessageEvent()

        data class GetTopic(val errorMessage: String?) : ErrorMessageEvent()
    }

    //for like/unlike a post
    fun likePost(
        postId: String,
        postLiked: Boolean,
        loggedInUUID: String
    ) {
        viewModelScope.launchIO {
            val request = LikePostRequest.Builder()
                .postId(postId)
                .build()

            //call like post api
            val response = lmFeedClient.likePost(request)

            //check for error
            if (response.success) {
                //sends event for post liked
                LMFeedAnalytics.sendPostLikedEvent(
                    uuid = loggedInUUID,
                    postId = postId,
                    postLiked = postLiked
                )
            } else {
                errorMessageChannel.send(
                    ErrorMessageEvent.LikePost(
                        postId,
                        response.errorMessage
                    )
                )
            }
        }
    }

    //for save/unsave a post
    fun savePost(postViewData: LMFeedPostViewData) {
        viewModelScope.launchIO {
            val request = SavePostRequest.Builder()
                .postId(postViewData.id)
                .build()

            //call like post api
            val response = lmFeedClient.savePost(request)

            //check for error
            if (response.success) {
                //sends event for post saved/unsaved
                LMFeedAnalytics.sendPostSavedEvent(
                    uuid = postViewData.headerViewData.user.sdkClientInfoViewData.uuid,
                    postId = postViewData.id,
                    postSaved = postViewData.actionViewData.isSaved
                )

                _postSavedResponse.postValue(postViewData)
            } else {
                errorMessageChannel.send(
                    ErrorMessageEvent.SavePost(
                        postViewData.id,
                        response.errorMessage
                    )
                )
            }
        }
    }

    //for pin/unpin post
    fun pinPost(postViewData: LMFeedPostViewData) {
        viewModelScope.launchIO {
            val request = PinPostRequest.Builder()
                .postId(postViewData.id)
                .build()

            //call pin api
            val response = lmFeedClient.pinPost(request)

            if (response.success) {
                //sends event for pin/unpin post
                LMFeedAnalytics.sendPostPinnedEvent(postViewData, LMFeedScreenNames.UNIVERSAL_FEED)

                _postPinnedResponse.postValue(postViewData)
            } else {
                errorMessageChannel.send(
                    ErrorMessageEvent.PinPost(
                        postViewData.id,
                        response.errorMessage
                    )
                )
            }
        }
    }

    //for delete post
    fun deletePost(post: LMFeedPostViewData, reason: String? = null) {
        viewModelScope.launchIO {
            val request = DeletePostRequest.Builder()
                .postId(post.id)
                .deleteReason(reason)
                .build()

            //call delete post api
            val response = lmFeedClient.deletePost(request)

            if (response.success) {
                // sends post deleted event
                LMFeedAnalytics.sendPostDeletedEvent(post, reason)

                _deletePostResponse.postValue(post.id)
            } else {
                errorMessageChannel.send(
                    ErrorMessageEvent.DeletePost(
                        response.errorMessage
                    )
                )
            }
        }
    }

    //get unread notification count
    fun getUnreadNotificationCount() {
        viewModelScope.launchIO {
            //call unread notification count api
            val response = lmFeedClient.getUnreadNotificationCount()

            if (response.success) {
                val data = response.data ?: return@launchIO
                val count = data.count

                _unreadNotificationCount.postValue(count)
            } else {
                //for error
                errorMessageChannel.send(ErrorMessageEvent.GetUnreadNotificationCount(response.errorMessage))
            }
        }
    }

    //calls to topics api and check whether to show topics view or not
    fun getAllTopics(showEnabledTopicsOnly: Boolean) {
        viewModelScope.launchIO {
            val requestBuilder = GetTopicRequest.Builder()
                .page(1)
                .pageSize(10)

            if (showEnabledTopicsOnly) {
                requestBuilder.isEnabled(true)
            }

            val request = requestBuilder.build()

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
                errorMessageChannel.send(
                    ErrorMessageEvent.GetTopic(
                        response.errorMessage
                    )
                )
            }
        }
    }

    //gets logged in user
    fun getLoggedInUser() {
        viewModelScope.launchIO {
            val response = lmFeedClient.getLoggedInUserWithRights()

            if (response.success) {
                val user = response.data?.user ?: return@launchIO

                val userViewData = LMFeedViewDataConvertor.convertUser(user)

                //post the user response in LiveData
                _userResponse.postValue(userViewData)
            }
        }
    }

    //get ids from topic selected adapter
    fun getTopicIdsFromAdapterList(items: List<LMFeedBaseViewType>): List<String> {
        return items.map {
            (it as LMFeedTopicViewData).id
        }
    }
}