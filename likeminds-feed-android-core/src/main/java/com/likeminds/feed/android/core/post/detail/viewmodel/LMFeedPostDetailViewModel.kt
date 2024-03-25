package com.likeminds.feed.android.core.post.detail.viewmodel

import androidx.lifecycle.*
import com.likeminds.feed.android.core.post.detail.model.LMFeedCommentViewData
import com.likeminds.feed.android.core.universalfeed.model.LMFeedPostViewData
import com.likeminds.feed.android.core.utils.LMFeedViewDataConvertor
import com.likeminds.feed.android.core.utils.analytics.LMFeedAnalytics
import com.likeminds.feed.android.core.utils.coroutine.launchIO
import com.likeminds.feed.android.core.utils.user.LMFeedMemberRightsUtil
import com.likeminds.feed.android.core.utils.user.LMFeedUserViewData
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.comment.model.*
import com.likeminds.likemindsfeed.post.model.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class LMFeedPostDetailViewModel : ViewModel() {

    private val lmFeedClient: LMFeedClient by lazy {
        LMFeedClient.getInstance()
    }

    // it holds the Pair of [page] and [postViewData]
    private val _postResponse by lazy {
        MutableLiveData<Pair<Int, LMFeedPostViewData>>()
    }

    val postResponse: LiveData<Pair<Int, LMFeedPostViewData>> by lazy {
        _postResponse
    }

    private val _addCommentResponse by lazy {
        MutableLiveData<LMFeedCommentViewData>()
    }

    val addCommentResponse: LiveData<LMFeedCommentViewData> by lazy {
        _addCommentResponse
    }

    private val _editCommentResponse by lazy {
        MutableLiveData<LMFeedCommentViewData>()
    }

    val editCommentResponse: LiveData<LMFeedCommentViewData> by lazy {
        _editCommentResponse
    }

    //it holds pair of [parentCommentId] and [replyComment]
    private val _addReplyResponse by lazy {
        MutableLiveData<Pair<String, LMFeedCommentViewData>>()
    }

    val addReplyResponse: LiveData<Pair<String, LMFeedCommentViewData>> by lazy {
        _addReplyResponse
    }

    // it holds the Pair of [page] and [commentViewData]
    private val _getCommentResponse by lazy {
        MutableLiveData<Pair<Int, LMFeedCommentViewData>>()
    }

    val getCommentResponse: LiveData<Pair<Int, LMFeedCommentViewData>> by lazy {
        _getCommentResponse
    }

    private val _hasCommentRights by lazy {
        MutableLiveData(true)
    }

    val hasCommentRights: LiveData<Boolean> by lazy {
        _hasCommentRights
    }

    /**
     * it holds the Pair of [commentId] and [parentCommentId]
     * if comment level is 0 then [parentCommentId] is null
     * if comment level is 1 then [parentCommentId] is non null
     */
    private val _deleteCommentResponse by lazy {
        MutableLiveData<Pair<String, String?>>()
    }

    val deleteCommentResponse: LiveData<Pair<String, String?>> by lazy {
        _deleteCommentResponse
    }

    private val _deletePostResponse by lazy {
        MutableLiveData<String>()
    }

    val deletePostResponse: LiveData<String> by lazy {
        _deletePostResponse
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

    sealed class ErrorMessageEvent {
        data class GetPost(val errorMessage: String?) : ErrorMessageEvent()

        data class LikePost(val postId: String, val errorMessage: String?) : ErrorMessageEvent()

        data class SavePost(val postId: String, val errorMessage: String?) : ErrorMessageEvent()

        data class DeletePost(val errorMessage: String?) : ErrorMessageEvent()

        data class PinPost(val postId: String, val errorMessage: String?) : ErrorMessageEvent()

        data class LikeComment(
            val commentId: String,
            val errorMessage: String?
        ) : ErrorMessageEvent()

        data class AddComment(
            val tempId: String,
            val errorMessage: String?
        ) : ErrorMessageEvent()

        data class ReplyComment(
            val parentCommentId: String,
            val tempId: String,
            val errorMessage: String?
        ) : ErrorMessageEvent()

        data class EditComment(val errorMessage: String?) : ErrorMessageEvent()

        data class DeleteComment(val errorMessage: String?) : ErrorMessageEvent()

        data class GetComment(val errorMessage: String?) : ErrorMessageEvent()
    }

    private val errorMessageChannel by lazy {
        Channel<ErrorMessageEvent>(Channel.BUFFERED)
    }

    val errorMessageEventFlow by lazy {
        errorMessageChannel.receiveAsFlow()
    }

    companion object {
        const val PAGE_SIZE = 10
        const val REPLIES_PAGE_SIZE = 5
    }

    // to getPost and paginated comments
    fun getPost(postId: String, page: Int) {
        viewModelScope.launchIO {
            // builds api request
            val request = GetPostRequest.Builder()
                .postId(postId)
                .page(page)
                .pageSize(PAGE_SIZE)
                .build()

            // calls api
            val response = lmFeedClient.getPost(request)

            if (response.success) {
                val data = response.data ?: return@launchIO
                val post = data.post
                val users = data.users
                val topics = data.topics
                _postResponse.postValue(
                    Pair(
                        page,
                        LMFeedViewDataConvertor.convertPost(
                            post,
                            users,
                            topics
                        )
                    )
                )
            } else {
                errorMessageChannel.send(ErrorMessageEvent.GetPost(response.errorMessage))
            }
        }
    }

    //for like/unlike a comment
    fun likeComment(
        postId: String,
        commentId: String,
        commentLiked: Boolean,
        loggedInUUID: String
    ) {
        viewModelScope.launchIO {
            val request = LikeCommentRequest.Builder()
                .postId(postId)
                .commentId(commentId)
                .build()

            //call like post api
            val response = lmFeedClient.likeComment(request)

            //check for error
            if (response.success) {
                //sends event for user liking a comment
                LMFeedAnalytics.sendCommentLikedEvent(
                    postId,
                    commentId,
                    commentLiked,
                    loggedInUUID
                )
            } else {
                errorMessageChannel.send(
                    ErrorMessageEvent.LikeComment(
                        commentId,
                        response.errorMessage
                    )
                )
            }
        }
    }

    //for adding comment on post
    fun addComment(
        postId: String,
        tempId: String,
        text: String
    ) {
        viewModelScope.launchIO {
            // initializes temp id for local handling of comment

            // builds api request
            val request = AddCommentRequest.Builder()
                .postId(postId)
                .text(text)
                .tempId(tempId)
                .build()

            // calls api
            val response = lmFeedClient.addComment(request)
            if (response.success) {
                val data = response.data ?: return@launchIO
                val comment = data.comment
                val users = data.users

                //sends user commented on a post event
                LMFeedAnalytics.sendCommentPostedEvent(postId, comment.id)

                _addCommentResponse.postValue(
                    LMFeedViewDataConvertor.convertComment(
                        comment,
                        users,
                        postId
                    )
                )
            } else {
                errorMessageChannel.send(
                    ErrorMessageEvent.AddComment(
                        tempId,
                        response.errorMessage
                    )
                )
            }
        }
    }

    // for editing comment on post
    fun editComment(
        postId: String,
        commentId: String,
        text: String
    ) {
        viewModelScope.launchIO {
            // builds api request
            val request = EditCommentRequest.Builder()
                .postId(postId)
                .commentId(commentId)
                .text(text)
                .build()

            // calls api
            val response = lmFeedClient.editComment(request)
            if (response.success) {
                val data = response.data ?: return@launchIO
                val comment = data.comment
                val users = data.users

                val commentViewData = LMFeedViewDataConvertor.convertComment(
                    comment,
                    users,
                    postId
                )

                //sends comment edited event
                LMFeedAnalytics.sendCommentEditedEvent(commentViewData)

                _editCommentResponse.postValue(commentViewData)
            } else {
                errorMessageChannel.send(ErrorMessageEvent.EditComment(response.errorMessage))
            }
        }
    }

    // for replying on a comment on the post
    fun replyComment(
        parentCommentCreatorUUID: String,
        postId: String,
        parentCommentId: String,
        text: String,
        tempId: String
    ) {
        viewModelScope.launchIO {
            // builds api request
            val request = ReplyCommentRequest.Builder()
                .postId(postId)
                .commentId(parentCommentId)
                .text(text)
                .tempId(tempId)
                .build()

            // calls api
            val response = lmFeedClient.replyComment(request)
            if (response.success) {
                val data = response.data ?: return@launchIO
                val comment = data.comment
                val users = data.users

                //sends event for user replied on an comment
                LMFeedAnalytics.sendReplyPostedEvent(
                    parentCommentCreatorUUID,
                    postId,
                    parentCommentId,
                    comment.id
                )

                _addReplyResponse.postValue(
                    Pair(
                        parentCommentId,
                        LMFeedViewDataConvertor.convertComment(
                            comment,
                            users,
                            postId,
                            parentCommentId
                        )
                    )
                )
            } else {
                errorMessageChannel.send(
                    ErrorMessageEvent.ReplyComment(
                        parentCommentId,
                        tempId,
                        response.errorMessage
                    )
                )
            }
        }
    }

    // to get comment with paginated replies
    fun getComment(
        postId: String,
        commentId: String,
        page: Int
    ) {
        viewModelScope.launchIO {
            // builds api request
            val request = GetCommentRequest.Builder()
                .postId(postId)
                .commentId(commentId)
                .page(page)
                .pageSize(REPLIES_PAGE_SIZE)
                .build()

            // calls api
            val response = lmFeedClient.getComment(request)
            if (response.success) {
                val data = response.data ?: return@launchIO
                val comment = data.comment
                val users = data.users
                _getCommentResponse.postValue(
                    Pair(
                        page,
                        LMFeedViewDataConvertor.convertComment(
                            comment,
                            users,
                            postId
                        )
                    )
                )
            } else {
                errorMessageChannel.send(ErrorMessageEvent.GetComment(response.errorMessage))
            }
        }
    }

    //for delete post
    fun deletePost(
        post: LMFeedPostViewData,
        reason: String? = null
    ) {
        viewModelScope.launchIO {
            val request = DeletePostRequest.Builder()
                .postId(post.id)
                .deleteReason(reason)
                .build()

            //call delete post api
            val response = lmFeedClient.deletePost(request)

            if (response.success) {
                //sends post deleted event
                LMFeedAnalytics.sendPostDeletedEvent(post, reason)

                _deletePostResponse.postValue(post.id)
            } else {
                errorMessageChannel.send(ErrorMessageEvent.DeletePost(response.errorMessage))
            }
        }
    }

    // for deleting comment/reply
    fun deleteComment(
        postId: String,
        commentId: String,
        parentCommentId: String? = null,
        reason: String? = null
    ) {
        viewModelScope.launchIO {
            val request = DeleteCommentRequest.Builder()
                .postId(postId)
                .commentId(commentId)
                .reason(reason)
                .build()

            //call delete comment api
            val response = lmFeedClient.deleteComment(request)

            if (response.success) {
                //send comment's reply deleted even
                LMFeedAnalytics.sendCommentReplyDeletedEvent(
                    postId,
                    commentId,
                    parentCommentId
                )

                _deleteCommentResponse.postValue(Pair(commentId, parentCommentId))
            } else {
                errorMessageChannel.send(ErrorMessageEvent.DeleteComment(response.errorMessage))
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
                    postSaved = postViewData.footerViewData.isSaved
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
                //sends event for post pinned/unpinned
                LMFeedAnalytics.sendPostPinnedEvent(postViewData)

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

    fun checkCommentRights() {
        viewModelScope.launchIO {
            // fetches user with rights from DB with user.id
            val response = lmFeedClient.getLoggedInUserWithRights()

            val loggedInUserData = response.data ?: return@launchIO

            val memberState = loggedInUserData.user.state
            val memberRights = loggedInUserData.rights

            _hasCommentRights.postValue(
                LMFeedMemberRightsUtil.hasCommentRight(
                    memberState,
                    memberRights
                )
            )
        }
    }

    // returns [CommentViewData] for local handling of comment
    fun getCommentViewDataForLocalHandling(
        postId: String,
        creatorUserName: String,
        createdAt: Long,
        tempId: String,
        text: String,
        parentCommentId: String?,
        level: Int = 0
    ): LMFeedCommentViewData {
        // adds comment locally
        return LMFeedCommentViewData.Builder()
            .postId(postId)
            .user(
                LMFeedUserViewData.Builder()
                    .name(creatorUserName)
                    .build()
            )
            .createdAt(createdAt)
            .id(tempId)
            .tempId(tempId)
            .text(text)
            .parentId(parentCommentId)
            .level(level)
            .build()
    }
}