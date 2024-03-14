package com.likeminds.feed.android.core.post.edit.viewmodel

import androidx.lifecycle.*
import com.likeminds.feed.android.core.post.model.LMFeedAttachmentViewData
import com.likeminds.feed.android.core.post.model.LMFeedLinkOGTagsViewData
import com.likeminds.feed.android.core.topics.model.LMFeedTopicViewData
import com.likeminds.feed.android.core.universalfeed.model.LMFeedPostViewData
import com.likeminds.feed.android.core.utils.LMFeedViewDataConvertor
import com.likeminds.feed.android.core.utils.LMFeedViewUtils
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

    private val lmFeedClient = LMFeedClient.getInstance()

    private val _showTopicFilter = MutableLiveData<Boolean>()
    val showTopicFilter: LiveData<Boolean> = _showTopicFilter


    private val _decodeUrlResponse = MutableLiveData<LMFeedLinkOGTagsViewData>()
    val decodeUrlResponse: LiveData<LMFeedLinkOGTagsViewData> = _decodeUrlResponse

    sealed class PostDataEvent {
        data class GetPost(val post: LMFeedPostViewData) : PostDataEvent()

        data class EditPost(val post: LMFeedPostViewData) : PostDataEvent()
    }

    private val postDataEventChannel = Channel<PostDataEvent>(Channel.BUFFERED)
    val postDataEventFlow = postDataEventChannel.receiveAsFlow()

    sealed class ErrorMessageEvent {
        data class GetPost(val errorMessage: String?) : ErrorMessageEvent()

        data class EditPost(val errorMessage: String?) : ErrorMessageEvent()

        data class GetTopic(val errorMessage: String?) : ErrorMessageEvent()

        data class DecodeUrl(val errorMessage: String?) : ErrorMessageEvent()
    }

    private val errorEventChannel = Channel<ErrorMessageEvent>(Channel.BUFFERED)
    val errorEventFlow = errorEventChannel.receiveAsFlow()

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
                sendPostEditedEvent(postViewData)
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

    /**
     * Triggers when the user edits a post
     **/
    private fun sendPostEditedEvent(post: LMFeedPostViewData) {
        val postType = LMFeedViewUtils.getPostTypeFromViewType(post.viewType)
        val postCreatorUUID = post.headerViewData.user.sdkClientInfoViewData.uuid
        LMFeedAnalytics.track(
            LMFeedAnalytics.Events.POST_EDITED,
            mapOf(
                "created_by_uuid" to postCreatorUUID,
                LMFeedAnalytics.Keys.POST_ID to post.id,
                "post_type" to postType,
            )
        )
    }

    /**
     * Triggers when the user attaches link
     * @param link - url of the link
     **/
    fun sendLinkAttachedEvent(link: String) {
        LMFeedAnalytics.track(
            LMFeedAnalytics.Events.LINK_ATTACHED_IN_POST,
            mapOf(
                "link" to link
            )
        )
    }
}