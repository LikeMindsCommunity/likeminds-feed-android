package com.likeminds.feed.android.core.post.edit.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.likeminds.feed.android.core.post.model.LMFeedAttachmentViewData
import com.likeminds.feed.android.core.post.model.LMFeedLinkOGTagsViewData
import com.likeminds.feed.android.core.topics.model.LMFeedTopicViewData
import com.likeminds.feed.android.core.universalfeed.model.LMFeedPostViewData
import com.likeminds.feed.android.core.utils.LMFeedViewDataConvertor
import com.likeminds.feed.android.core.utils.analytics.LMFeedAnalytics
import com.likeminds.feed.android.core.utils.coroutine.launchIO
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.LMResponse
import com.likeminds.likemindsfeed.helper.model.DecodeUrlRequest
import com.likeminds.likemindsfeed.helper.model.DecodeUrlResponse
import com.likeminds.likemindsfeed.post.model.EditPostRequest
import com.likeminds.likemindsfeed.post.model.GetPostRequest
import com.likeminds.likemindsfeed.topic.model.GetTopicRequest
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class LMFeedEditPostViewModel : ViewModel() {

    private val lmFeedClient by lazy {
        LMFeedClient.getInstance()
    }

    private val _showTopicFilter by lazy {
        MutableLiveData<Boolean>()
    }
    val showTopicFilter: LiveData<Boolean> by lazy {
        _showTopicFilter
    }

    private val _decodeUrlResponse by lazy {
        MutableLiveData<LMFeedLinkOGTagsViewData>()
    }

    val decodeUrlResponse: LiveData<LMFeedLinkOGTagsViewData> by lazy {
        _decodeUrlResponse
    }

    sealed class PostDataEvent {
        data class GetPost(val post: LMFeedPostViewData) : PostDataEvent()

        data class EditPost(val post: LMFeedPostViewData) : PostDataEvent()
    }

    private val postDataEventChannel by lazy {
        Channel<PostDataEvent>(Channel.BUFFERED)
    }
    val postDataEventFlow by lazy {
        postDataEventChannel.receiveAsFlow()
    }

    sealed class ErrorMessageEvent {
        data class GetPost(val errorMessage: String?) : ErrorMessageEvent()

        data class EditPost(val errorMessage: String?) : ErrorMessageEvent()

        data class GetTopic(val errorMessage: String?) : ErrorMessageEvent()

        data class DecodeUrl(val errorMessage: String?) : ErrorMessageEvent()
    }

    private val errorEventChannel by lazy {
        Channel<ErrorMessageEvent>(Channel.BUFFERED)
    }

    val errorEventFlow by lazy {
        errorEventChannel.receiveAsFlow()
    }

    // to get the Post to be edited
    fun getPost(postId: String) {
        viewModelScope.launchIO {
            // builds api request
            val request = GetPostRequest.Builder()
                .postId(postId)
                .page(1)
                .pageSize(5)
                .build()

            // calls api
            val response = lmFeedClient.getPost(request)
            if (response.success) {
                val data = response.data ?: return@launchIO
                val post = data.post
                val users = data.users
                val topics = data.topics

                postDataEventChannel.send(
                    PostDataEvent.GetPost(
                        LMFeedViewDataConvertor.convertPost(
                            post,
                            users,
                            topics
                        )
                    )
                )
            } else {
                errorEventChannel.send(ErrorMessageEvent.GetPost(response.errorMessage))
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

    // calls EditPost API and posts the response in LiveData
    fun editPost(
        postId: String,
        postTextContent: String?,
        attachments: List<LMFeedAttachmentViewData>? = null,
        ogTags: LMFeedLinkOGTagsViewData? = null,
        selectedTopics: List<LMFeedTopicViewData>? = null
    ) {
        viewModelScope.launchIO {
            var updatedText = postTextContent?.trim()
            if (updatedText.isNullOrEmpty()) {
                updatedText = null
            }

            val topicIds = selectedTopics?.map {
                it.id
            }

            val request =
                if (attachments != null) {
                    // if the post has any file attachments
                    EditPostRequest.Builder()
                        .postId(postId)
                        .text(updatedText)
                        .attachments(LMFeedViewDataConvertor.createAttachments(attachments))
                        .topicIds(topicIds)
                        .build()
                } else {
                    // if the post does not have any file attachments
                    val requestBuilder = EditPostRequest.Builder()
                        .postId(postId)
                        .text(updatedText)
                        .topicIds(topicIds)
                    if (ogTags != null) {
                        // if the post has ogTags
                        requestBuilder.attachments(LMFeedViewDataConvertor.convertAttachments(ogTags))
                    }
                    requestBuilder.build()
                }

            // calls api
            val response = lmFeedClient.editPost(request)
            if (response.success) {
                val data = response.data ?: return@launchIO
                val post = data.post
                val users = data.users
                val topics = data.topics
                val postViewData = LMFeedViewDataConvertor.convertPost(post, users, topics)
                postDataEventChannel.send(PostDataEvent.EditPost(postViewData))

                // sends post edited event
                LMFeedAnalytics.sendPostEditedEvent(postViewData)
            } else {
                errorEventChannel.send(ErrorMessageEvent.EditPost(response.errorMessage))
            }
        }
    }

    //calls DecodeUrl API
    fun decodeUrl(url: String) {
        viewModelScope.launchIO {
            val request = DecodeUrlRequest.Builder().url(url).build()

            val response = lmFeedClient.decodeUrl(request)
            postDecodeUrlResponse(response)
        }
    }

    //processes and posts the DecodeUrl response in LiveData
    private fun postDecodeUrlResponse(response: LMResponse<DecodeUrlResponse>) {
        viewModelScope.launchIO {
            if (response.success) {
                // processes link og tags if API call was successful
                val data = response.data ?: return@launchIO
                val ogTags = data.ogTags
                _decodeUrlResponse.postValue(LMFeedViewDataConvertor.convertLinkOGTags(ogTags))
            } else {
                // posts error message if API call failed
                errorEventChannel.send(ErrorMessageEvent.DecodeUrl(response.errorMessage))
            }
        }
    }
}