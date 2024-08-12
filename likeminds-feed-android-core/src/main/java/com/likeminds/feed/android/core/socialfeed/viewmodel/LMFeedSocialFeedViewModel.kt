package com.likeminds.feed.android.core.socialfeed.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.*
import androidx.work.WorkContinuation
import androidx.work.WorkManager
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.poll.util.LMFeedPollUtil
import com.likeminds.feed.android.core.post.create.util.LMFeedPostAttachmentUploadWorker
import com.likeminds.feed.android.core.socialfeed.model.LMFeedPostViewData
import com.likeminds.feed.android.core.topics.model.LMFeedTopicViewData
import com.likeminds.feed.android.core.utils.LMFeedViewDataConvertor
import com.likeminds.feed.android.core.utils.analytics.LMFeedAnalytics
import com.likeminds.feed.android.core.utils.analytics.LMFeedAnalytics.LMFeedKeys
import com.likeminds.feed.android.core.utils.analytics.LMFeedAnalytics.LMFeedScreenNames
import com.likeminds.feed.android.core.utils.base.LMFeedBaseViewType
import com.likeminds.feed.android.core.utils.coroutine.launchIO
import com.likeminds.feed.android.core.utils.user.LMFeedMemberRightsUtil
import com.likeminds.feed.android.core.utils.user.LMFeedUserViewData
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.feed.model.GetFeedRequest
import com.likeminds.likemindsfeed.poll.model.AddPollOptionRequest
import com.likeminds.likemindsfeed.poll.model.SubmitVoteRequest
import com.likeminds.likemindsfeed.post.model.*
import com.likeminds.likemindsfeed.topic.model.GetTopicRequest
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class LMFeedSocialFeedViewModel : ViewModel() {

    private val lmFeedClient: LMFeedClient by lazy {
        LMFeedClient.getInstance()
    }

    private val _socialFeedResponse by lazy {
        MutableLiveData<Pair<Int, List<LMFeedPostViewData>>>()
    }

    val socialFeedResponse: LiveData<Pair<Int, List<LMFeedPostViewData>>> by lazy {
        _socialFeedResponse
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

    private val _showTopicFilter by lazy {
        MutableLiveData<Boolean>()
    }

    val showTopicFilter: LiveData<Boolean> by lazy {
        _showTopicFilter
    }

    private val _hasCreatePostRights by lazy {
        MutableLiveData(true)
    }

    val hasCreatePostRights: LiveData<Boolean> by lazy {
        _hasCreatePostRights
    }

    private val _unreadNotificationCount by lazy {
        MutableLiveData<Int>()
    }

    val unreadNotificationCount: LiveData<Int> by lazy {
        _unreadNotificationCount
    }

    private val _userResponse by lazy {
        MutableLiveData<LMFeedUserViewData>()
    }

    val userResponse: LiveData<LMFeedUserViewData> by lazy {
        _userResponse
    }

    private val _postResponse by lazy {
        MutableLiveData<LMFeedPostViewData>()
    }

    val postResponse: LiveData<LMFeedPostViewData> by lazy {
        _postResponse
    }

    private val errorMessageChannel by lazy {
        Channel<ErrorMessageEvent>(Channel.BUFFERED)
    }

    val errorMessageEventFlow by lazy {
        errorMessageChannel.receiveAsFlow()
    }

    sealed class ErrorMessageEvent {
        data class SocialFeed(val errorMessage: String?) : ErrorMessageEvent()

        data class LikePost(val postId: String, val errorMessage: String?) : ErrorMessageEvent()

        data class SavePost(val postId: String, val errorMessage: String?) : ErrorMessageEvent()

        data class DeletePost(val errorMessage: String?) : ErrorMessageEvent()

        data class PinPost(val postId: String, val errorMessage: String?) : ErrorMessageEvent()

        data class GetTopic(val errorMessage: String?) : ErrorMessageEvent()

        data class GetUnreadNotificationCount(val errorMessage: String?) : ErrorMessageEvent()

        data class AddPost(val errorMessage: String?) : ErrorMessageEvent()

        data class SubmitVote(val errorMessage: String?) : ErrorMessageEvent()

        data class AddPollOption(val errorMessage: String?) : ErrorMessageEvent()

        data class GetPost(val errorMessage: String?) : ErrorMessageEvent()
    }

    sealed class PostDataEvent {
        data class PostDbData(val post: LMFeedPostViewData) : PostDataEvent()

        data class PostResponseData(val post: LMFeedPostViewData) : PostDataEvent()
    }

    private val postDataEventChannel by lazy { Channel<PostDataEvent>(Channel.BUFFERED) }
    val postDataEventFlow by lazy { postDataEventChannel.receiveAsFlow() }

    companion object {
        const val PAGE_SIZE = 20
        const val GET_POST_PAGE_SIZE = 5
    }

    // fetches pending post data from db
    fun fetchPendingPostFromDB() {
        viewModelScope.launchIO {
            val currentUploadingPost = lmFeedClient.getCurrentUploadingPost()
            val data = currentUploadingPost.data ?: return@launchIO
            val post = data.post
            val topics = data.topics

            val postViewData = LMFeedViewDataConvertor.convertPost(
                post,
                topics
            )

            postDataEventChannel.send(
                PostDataEvent.PostDbData(
                    postViewData
                )
            )
        }
    }

    // calls AddPost API and posts the response in LiveData
    fun addPost(postingData: LMFeedPostViewData) {
        viewModelScope.launchIO {
            val text = postingData.contentViewData.text
            val updatedText =
                if (text.isNullOrEmpty()) {
                    null
                } else {
                    text
                }

            val topicIds = postingData.topicsViewData.map {
                it.id
            }

            val attachments = LMFeedViewDataConvertor.createAttachments(
                postingData.mediaViewData.attachments
            )

            val request = AddPostRequest.Builder()
                .text(updatedText)
                .attachments(attachments)
                .tempId(postingData.mediaViewData.temporaryId.toString())
                .topicIds(topicIds)
                .build()

            val response = lmFeedClient.addPost(request)
            if (response.success) {
                val data = response.data ?: return@launchIO
                val usersMap = data.users
                val topicsMap = data.topics
                val widgetsMap = data.widgets

                val postViewData = LMFeedViewDataConvertor.convertPost(
                    data.post,
                    usersMap,
                    topicsMap,
                    widgetsMap
                )

                // sends post creation completed event
                sendPostCreationCompletedEvent(postViewData)

                postDataEventChannel.send(PostDataEvent.PostResponseData(postViewData))
            } else {
                val errorMessage = response.errorMessage

                // sends post creation failed event
                sendPostCreationFailedEvent(postingData, errorMessage)

                errorMessageChannel.send(ErrorMessageEvent.AddPost(errorMessage))
            }
        }
    }

    fun getFeed(page: Int, topicsIds: List<String>? = null) {
        viewModelScope.launchIO {
            val request = GetFeedRequest.Builder()
                .page(page)
                .pageSize(PAGE_SIZE)
                .topicIds(topicsIds)
                .build()

            //call get feed api
            val response = lmFeedClient.getFeed(request)

            if (response.success) {
                val data = response.data ?: return@launchIO
                val posts = data.posts
                val usersMap = data.users
                val topicsMap = data.topics
                val widgetsMap = data.widgets

                //convert to view data
                val listOfPostViewData =
                    LMFeedViewDataConvertor.convertGetFeedPosts(
                        posts,
                        usersMap,
                        topicsMap,
                        widgetsMap
                    )

                //send it to ui
                _socialFeedResponse.postValue(Pair(page, listOfPostViewData))
            } else {
                //for error
                errorMessageChannel.send(ErrorMessageEvent.SocialFeed(response.errorMessage))
            }
        }
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
                errorMessageChannel.send(ErrorMessageEvent.DeletePost(response.errorMessage))
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
                errorMessageChannel.send(ErrorMessageEvent.GetTopic(response.errorMessage))
            }
        }
    }

    //call member state api
    fun getCreatePostRights() {
        viewModelScope.launchIO {
            //get member state response
            // fetches user with rights from DB with user.id
            val response = lmFeedClient.getLoggedInUserWithRights()

            val loggedInUserData = response.data ?: return@launchIO

            val memberState = loggedInUserData.user.state
            val memberRights = loggedInUserData.rights

            _hasCreatePostRights.postValue(
                LMFeedMemberRightsUtil.hasCreatePostsRight(
                    memberState,
                    memberRights
                )
            )
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

    // starts a media upload worker to retry failed uploads
    fun createRetryPostMediaWorker(
        context: Context,
        postId: Long?,
        attachmentCount: Int,
    ) {
        viewModelScope.launchIO {
            if (postId == null || attachmentCount <= 0) {
                return@launchIO
            }
            val uploadData = startMediaUploadWorker(context, postId, attachmentCount)
            val request = UpdatePostWorkerUUIDRequest.Builder()
                .temporaryId(postId.toString())
                .workerUUID(uploadData.second)
                .build()
            lmFeedClient.updatePostWorkerUUID(request)
            uploadData.first.enqueue()
            fetchPendingPostFromDB()
        }
    }

    // creates PostAttachmentUploadWorker to start media upload
    @SuppressLint("EnqueueWork")
    private fun startMediaUploadWorker(
        context: Context,
        postId: Long,
        filesCount: Int
    ): Pair<WorkContinuation, String> {
        val oneTimeWorkRequest = LMFeedPostAttachmentUploadWorker.getInstance(postId, filesCount)
        val workContinuation = WorkManager.getInstance(context).beginWith(oneTimeWorkRequest)
        return Pair(workContinuation, oneTimeWorkRequest.id.toString())
    }

    //calls api to submit vote on poll
    fun submitPollVote(
        context: Context,
        postId: String,
        pollId: String,
        optionIds: List<String>
    ) {
        viewModelScope.launchIO {
            if (optionIds.isEmpty()) {
                errorMessageChannel.send(
                    ErrorMessageEvent.SubmitVote(
                        context.getString(
                            R.string.lm_feed_please_select_options_before_submitting_vote
                        )
                    )
                )
            }

            val request = SubmitVoteRequest.Builder()
                .pollId(pollId)
                .votes(optionIds)
                .build()

            val response = lmFeedClient.submitVote(request)

            if (response.success) {
                getPost(postId)
            } else {
                errorMessageChannel.send(ErrorMessageEvent.SubmitVote(response.errorMessage))
            }
        }
    }

    // to getPost
    private fun getPost(postId: String) {
        viewModelScope.launchIO {
            // builds api request
            val request = GetPostRequest.Builder()
                .postId(postId)
                .page(1)
                .pageSize(GET_POST_PAGE_SIZE)
                .build()

            // calls api
            val response = lmFeedClient.getPost(request)

            if (response.success) {
                val data = response.data ?: return@launchIO
                val post = data.post
                val users = data.users
                val topics = data.topics
                val widgets = data.widgets

                _postResponse.postValue(
                    LMFeedViewDataConvertor.convertPost(
                        post,
                        users,
                        topics,
                        widgets
                    )
                )
            } else {
                errorMessageChannel.send(ErrorMessageEvent.GetPost(response.errorMessage))
            }
        }
    }

    //calls api to add option on the poll
    fun addPollOption(
        post: LMFeedPostViewData,
        addedOptionText: String
    ) {
        viewModelScope.launchIO {
            val pollAttachment = post.mediaViewData.attachments.firstOrNull() ?: return@launchIO
            val poll = pollAttachment.attachmentMeta.poll ?: return@launchIO

            val isDuplicationOption =
                LMFeedPollUtil.isDuplicationOption(poll, addedOptionText)

            if (isDuplicationOption) {
                errorMessageChannel.send(
                    ErrorMessageEvent.AddPollOption(
                        "Poll options cannot contain similar text"
                    )
                )
                return@launchIO
            }

            val request = AddPollOptionRequest.Builder()
                .pollId(poll.id)
                .text(addedOptionText)
                .build()

            val response = lmFeedClient.addPollOption(request)

            if (response.success) {
                getPost(post.id)
            } else {
                errorMessageChannel.send(ErrorMessageEvent.AddPollOption(response.errorMessage))
            }
        }
    }

    /**
     * Triggers when the user opens post is created successfully
     **/
    private fun sendPostCreationCompletedEvent(
        post: LMFeedPostViewData
    ) {
        LMFeedAnalytics.track(
            LMFeedAnalytics.LMFeedEvents.POST_CREATION_COMPLETED,
            LMFeedAnalytics.getPostMetaAnalytics(post)
        )
    }

    /**
     * Triggers when the user opens post is created successfully
     **/
    private fun sendPostCreationFailedEvent(
        post: LMFeedPostViewData,
        errorMessage: String?
    ) {
        val map = LMFeedAnalytics.getPostMetaAnalytics(post)

        map["error_message"] = errorMessage ?: "Something went wrong"
        map[LMFeedKeys.SCREEN_NAME] = LMFeedScreenNames.UNIVERSAL_FEED

        LMFeedAnalytics.track(
            LMFeedAnalytics.LMFeedEvents.POST_CREATION_ERROR,
            map
        )
    }
}