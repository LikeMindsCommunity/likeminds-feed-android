package com.likeminds.feed.android.core.qnafeed.viewmodel

import androidx.lifecycle.*
import com.likeminds.feed.android.core.post.viewmodel.LMFeedHelperViewModel
import com.likeminds.feed.android.core.socialfeed.model.LMFeedPostViewData
import com.likeminds.feed.android.core.socialfeed.viewmodel.LMFeedSocialFeedViewModel
import com.likeminds.feed.android.core.socialfeed.viewmodel.LMFeedSocialFeedViewModel.*
import com.likeminds.feed.android.core.utils.LMFeedViewDataConvertor
import com.likeminds.feed.android.core.utils.coroutine.launchIO
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.feed.model.GetFeedRequest
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class LMFeedQnAFeedViewModel : ViewModel() {

    val helperViewModel: LMFeedHelperViewModel by lazy {
        LMFeedHelperViewModel()
    }

    private val lmFeedClient: LMFeedClient by lazy {
        LMFeedClient.getInstance()
    }

    private val postDataEventChannel by lazy { Channel<PostDataEvent>(Channel.BUFFERED) }
    val postDataEventFlow by lazy { postDataEventChannel.receiveAsFlow() }

    private val _socialFeedResponse by lazy {
        MutableLiveData<Pair<Int, List<LMFeedPostViewData>>>()
    }

    val socialFeedResponse: LiveData<Pair<Int, List<LMFeedPostViewData>>> by lazy {
        _socialFeedResponse
    }

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

    fun getFeed(page: Int, topicsIds: List<String>? = null) {
        viewModelScope.launchIO {
            val request = GetFeedRequest.Builder()
                .page(page)
                .pageSize(LMFeedSocialFeedViewModel.PAGE_SIZE)
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
//                errorMessageChannel.send(ErrorMessageEvent.SocialFeed(response.errorMessage))
            }
        }
    }
}