package com.likeminds.feed.android.core.search.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.likeminds.feed.android.core.socialfeed.model.LMFeedPostViewData
import com.likeminds.feed.android.core.utils.LMFeedViewDataConvertor
import com.likeminds.feed.android.core.utils.coroutine.launchIO
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.search.model.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class LMFeedSearchViewModel : ViewModel() {

    companion object {
        const val PAGE_SIZE = 10
    }

    private val lmFeedClient: LMFeedClient by lazy {
        LMFeedClient.getInstance()
    }


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

    fun searchPosts(page: Int, searchString: String) {
        viewModelScope.launchIO {
            Log.d("PUI", "searchString:$searchString")
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

                Log.d("PUI", posts.toString())
                //convert to view data
                val listOfPostViewData =
                    LMFeedViewDataConvertor.convertGetFeedPosts(
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