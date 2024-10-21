package com.likeminds.feed.android.core.search.viewmodel

import android.content.Context
import androidx.lifecycle.*
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.poll.util.LMFeedPollUtil
import com.likeminds.feed.android.core.socialfeed.model.LMFeedPostViewData
import com.likeminds.feed.android.core.socialfeed.viewmodel.LMFeedSocialFeedViewModel
import com.likeminds.feed.android.core.utils.LMFeedViewDataConvertor
import com.likeminds.feed.android.core.utils.analytics.LMFeedAnalytics
import com.likeminds.feed.android.core.utils.coroutine.launchIO
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.poll.model.AddPollOptionRequest
import com.likeminds.likemindsfeed.poll.model.SubmitVoteRequest
import com.likeminds.likemindsfeed.post.model.*
import com.likeminds.likemindsfeed.search.model.SearchPostsRequest
import com.likeminds.likemindsfeed.search.model.SearchType
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class LMFeedSearchViewModel : ViewModel() {

    companion object {
        const val PAGE_SIZE = 10
    }

    private val lmFeedClient: LMFeedClient by lazy {
        LMFeedClient.getInstance()
    }

    // it holds the Pair of [page] and list of [postViewData]
    private val _searchFeedResponse by lazy {
        MutableLiveData<Pair<Int, List<LMFeedPostViewData>>>()
    }

    val searchFeedResponse: LiveData<Pair<Int, List<LMFeedPostViewData>>> by lazy {
        _searchFeedResponse
    }

    // it holds [postViewData]
    private val _getPostResponse by lazy {
        MutableLiveData<LMFeedPostViewData>()
    }

    val getPostResponse: LiveData<LMFeedPostViewData> by lazy {
        _getPostResponse
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

    //it holds the [postId]
    private val _deletePostResponse by lazy {
        MutableLiveData<String>()
    }

    val deletePostResponse: LiveData<String> by lazy {
        _deletePostResponse
    }

    // it holds [postViewData]
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
        data class SearchPost(val errorMessage: String?) : ErrorMessageEvent()

        data class SavePost(val postId: String, val errorMessage: String?) : ErrorMessageEvent()

        data class LikePost(val postId: String, val errorMessage: String?) : ErrorMessageEvent()

        data class PinPost(val postId: String, val errorMessage: String?) : ErrorMessageEvent()

        data class DeletePost(val errorMessage: String?) : ErrorMessageEvent()

        data class SubmitVote(val errorMessage: String?) : ErrorMessageEvent()

        data class AddPollOption(val errorMessage: String?) : ErrorMessageEvent()

        data class GetPost(val errorMessage: String?) : ErrorMessageEvent()
    }

    // calls searchPosts API and posts the response in LiveData
    fun searchPosts(page: Int, searchString: String) {
        viewModelScope.launchIO {
            val requestBuilder = SearchPostsRequest.Builder()
                .page(page)
                .pageSize(PAGE_SIZE)

            if (searchString.isNotEmpty()) {
                requestBuilder.search(searchString)
                    .searchType(SearchType.TEXT)
            }

            val request = requestBuilder.build()

            val response = lmFeedClient.searchPosts(request)

            if (response.success) {
                val data = response.data ?: return@launchIO
                val posts = data.posts
                val usersMap = data.users
                val topicsMap = data.topics
                val widgetsMap = data.widgets

                //convert to view data
                val listOfPostViewData =
                    LMFeedViewDataConvertor.convertSearchedPosts(
                        searchString,
                        posts,
                        usersMap,
                        topicsMap,
                        widgetsMap
                    )

                //send it to ui
                _searchFeedResponse.postValue(Pair(page, listOfPostViewData))
            } else {
                errorMessageChannel.send(ErrorMessageEvent.SearchPost(response.errorMessage))
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
                LMFeedAnalytics.sendPostPinnedEvent(
                    postViewData,
                    LMFeedAnalytics.LMFeedScreenNames.SEARCH_SCREEN
                )

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
                .pageSize(LMFeedSocialFeedViewModel.GET_POST_PAGE_SIZE)
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
}