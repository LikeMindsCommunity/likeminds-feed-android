package com.likeminds.feed.android.core.videofeed.viewmodel

import androidx.lifecycle.*
import com.likeminds.feed.android.core.socialfeed.model.LMFeedPostViewData
import com.likeminds.feed.android.core.utils.LMFeedViewDataConvertor
import com.likeminds.feed.android.core.utils.analytics.LMFeedAnalytics
import com.likeminds.feed.android.core.utils.coroutine.launchIO
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.feed.model.GetFeedRequest
import com.likeminds.likemindsfeed.post.model.LikePostRequest
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class LMFeedVideoFeedViewModel : ViewModel() {

    companion object {
        const val PAGE_SIZE = 20
    }

    private var pageToCall = 0
    var previousTotal: Int = 0
    val adapterItems: MutableList<LMFeedPostViewData> = mutableListOf()
    var postsFinished = false
    var adapterPosition = 0

    private val lmFeedClient: LMFeedClient by lazy {
        LMFeedClient.getInstance()
    }

    private val _videoFeedResponse =
        MutableLiveData<Pair<Int, List<LMFeedPostViewData>>>()

    val videoFeedResponse: LiveData<Pair<Int, List<LMFeedPostViewData>>> =
        _videoFeedResponse

    private val errorMessageChannel by lazy {
        Channel<ErrorMessageEvent>(Channel.BUFFERED)
    }

    val errorMessageEventFlow by lazy {
        errorMessageChannel.receiveAsFlow()
    }

    sealed class ErrorMessageEvent {
        data class VideoFeed(val errorMessage: String?) : ErrorMessageEvent()

        data class LikePost(val postId: String, val errorMessage: String?) : ErrorMessageEvent()
    }

    //gets video feed
    fun getFeed(isRefreshed: Boolean = false, topicsIds: List<String>? = null) {
        viewModelScope.launchIO {
            if (isRefreshed) {
                pageToCall = 0
            }
            pageToCall++
            val request = GetFeedRequest.Builder()
                .page(pageToCall)
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

                // update the variable that no new posts are available now
                if (listOfPostViewData.isEmpty()) {
                    postsFinished = true
                }

                //send it to ui
                _videoFeedResponse.postValue(Pair(pageToCall, listOfPostViewData))
            } else {
                //for error
                pageToCall--
                errorMessageChannel.send(ErrorMessageEvent.VideoFeed(response.errorMessage))
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

    // stores the state of the view pager by saving the current position and items in adapter
    fun setViewPagerState(position: Int, items: List<LMFeedPostViewData>) {
        adapterPosition = position
        adapterItems.clear()
        adapterItems.addAll(items)
    }
}