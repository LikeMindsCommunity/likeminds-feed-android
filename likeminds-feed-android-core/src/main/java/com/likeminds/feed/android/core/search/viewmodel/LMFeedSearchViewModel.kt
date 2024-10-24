package com.likeminds.feed.android.core.search.viewmodel

import androidx.lifecycle.*
import com.likeminds.feed.android.core.post.viewmodel.LMFeedHelperViewModel
import com.likeminds.feed.android.core.post.viewmodel.LMFeedPostViewModel
import com.likeminds.feed.android.core.socialfeed.model.LMFeedPostViewData
import com.likeminds.feed.android.core.utils.LMFeedViewDataConvertor
import com.likeminds.feed.android.core.utils.coroutine.launchIO
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.search.model.SearchPostsRequest
import com.likeminds.likemindsfeed.search.model.SearchType
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class LMFeedSearchViewModel : ViewModel() {

    companion object {
        const val PAGE_SIZE = 10
    }

    val helperViewModel: LMFeedHelperViewModel by lazy {
        LMFeedHelperViewModel()
    }

    val postViewModel: LMFeedPostViewModel by lazy {
        LMFeedPostViewModel()
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

    private val errorMessageChannel by lazy {
        Channel<ErrorMessageEvent>(Channel.BUFFERED)
    }

    val errorMessageEventFlow by lazy {
        errorMessageChannel.receiveAsFlow()
    }

    sealed class ErrorMessageEvent {
        data class SearchPost(val errorMessage: String?) : ErrorMessageEvent()
    }

    // calls searchPosts API and posts the response in LiveData
    fun searchPosts(
        page: Int,
        searchString: String,
        searchType: SearchType = SearchType.TEXT
    ) {
        viewModelScope.launchIO {
            val requestBuilder = SearchPostsRequest.Builder()
                .page(page)
                .pageSize(PAGE_SIZE)
                .searchType(searchType)

            if (searchString.isNotEmpty()) {
                requestBuilder.search(searchString)
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
                        searchType,
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
}