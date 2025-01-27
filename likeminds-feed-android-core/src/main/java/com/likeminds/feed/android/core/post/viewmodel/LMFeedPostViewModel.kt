package com.likeminds.feed.android.core.post.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.*
import androidx.work.WorkContinuation
import androidx.work.WorkManager
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.poll.util.LMFeedPollUtil
import com.likeminds.feed.android.core.post.create.util.LMFeedPostAttachmentUploadWorker
import com.likeminds.feed.android.core.socialfeed.model.LMFeedPostViewData
import com.likeminds.feed.android.core.utils.LMFeedViewDataConvertor
import com.likeminds.feed.android.core.utils.analytics.LMFeedAnalytics
import com.likeminds.feed.android.core.utils.analytics.LMFeedAnalytics.LMFeedKeys
import com.likeminds.feed.android.core.utils.analytics.LMFeedAnalytics.LMFeedScreenNames
import com.likeminds.feed.android.core.utils.coroutine.launchIO
import com.likeminds.feed.android.core.utils.user.LMFeedMemberRightsUtil
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.feed.model.GetFeedRequest
import com.likeminds.likemindsfeed.feed.model.GetPersonalisedFeedRequest
import com.likeminds.likemindsfeed.poll.model.AddPollOptionRequest
import com.likeminds.likemindsfeed.poll.model.SubmitVoteRequest
import com.likeminds.likemindsfeed.post.model.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class LMFeedPostViewModel : ViewModel() {

    private val lmFeedClient: LMFeedClient by lazy {
        LMFeedClient.getInstance()
    }

    private val _universalFeedResponse by lazy {
        MutableLiveData<Pair<Int, List<LMFeedPostViewData>>>()
    }

    val universalFeedResponse: LiveData<Pair<Int, List<LMFeedPostViewData>>> by lazy {
        _universalFeedResponse
    }

    private val _personalisedFeedResponse by lazy {
        MutableLiveData<Pair<Int, List<LMFeedPostViewData>>>()
    }

    val personalisedFeedResponse: LiveData<Pair<Int, List<LMFeedPostViewData>>> by lazy {
        _personalisedFeedResponse
    }

    private val _hasCreatePostRights by lazy {
        MutableLiveData(true)
    }

    val hasCreatePostRights: LiveData<Boolean> by lazy {
        _hasCreatePostRights
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
        data class UniversalFeed(val errorMessage: String?) : ErrorMessageEvent()

        data class AddPost(val errorMessage: String?) : ErrorMessageEvent()

        data class SubmitVote(val errorMessage: String?) : ErrorMessageEvent()

        data class AddPollOption(val errorMessage: String?) : ErrorMessageEvent()

        data class GetPost(val errorMessage: String?) : ErrorMessageEvent()

        data class PersonalisedFeed(val errorMessage: String?) : ErrorMessageEvent()
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

            postDataEventChannel.send(PostDataEvent.PostDbData(postViewData))
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

            val topicIds = postingData.topicsViewData.map { it.id }

            val attachments = LMFeedViewDataConvertor.createAttachments(
                postingData.mediaViewData.attachments
            )

            val request = AddPostRequest.Builder()
                .text(updatedText)
                .attachments(attachments)
                .tempId(postingData.mediaViewData.temporaryId.toString())
                .topicIds(topicIds)
                .heading(postingData.contentViewData.heading)
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

    fun getUniversalFeed(page: Int, topicsIds: List<String>? = null) {
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
                val filteredCommentsMap = data.filteredComments

                //convert to view data
                val listOfPostViewData =
                    LMFeedViewDataConvertor.convertGetFeedPosts(
                        posts,
                        usersMap,
                        topicsMap,
                        widgetsMap,
                        filteredCommentsMap
                    )

                //send it to ui
                _universalFeedResponse.postValue(Pair(page, listOfPostViewData))
            } else {
                //for error
                errorMessageChannel.send(ErrorMessageEvent.UniversalFeed(response.errorMessage))
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

    // get personalised feed
    fun getPersonalisedFeed(
        page: Int,
        shouldReorder: Boolean? = null,
        shouldRecompute: Boolean? = null
    ) {
        viewModelScope.launchIO {
            // build api request
            val request = GetPersonalisedFeedRequest.Builder()
                .page(page)
                .pageSize(PAGE_SIZE)
                .shouldReorder(shouldReorder)
                .shouldRecompute(shouldRecompute)
                .build()

            //call api
            val response = lmFeedClient.getPersonalisedFeed(request)

            //process the response
            if (response.success) {
                //get all entities
                val data = response.data ?: return@launchIO
                val posts = data.posts
                val usersMap = data.users
                val topicsMap = data.topics
                val widgetsMap = data.widgets
                val filteredCommentsMap = data.filteredComments

                //convert to view data
                val listOfPostViewData =
                    LMFeedViewDataConvertor.convertGetFeedPosts(
                        posts,
                        usersMap,
                        topicsMap,
                        widgetsMap,
                        filteredCommentsMap
                    )

                //send to fragment
                _personalisedFeedResponse.postValue(Pair(page, listOfPostViewData))
            } else {
                //send error message
                errorMessageChannel.send(ErrorMessageEvent.PersonalisedFeed(response.errorMessage))
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