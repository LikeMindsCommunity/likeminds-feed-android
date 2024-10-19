package com.likeminds.feed.android.core.videofeed.viewmodel

import androidx.lifecycle.ViewModel
import com.likeminds.feed.android.core.post.viewmodel.LMFeedHelperViewModel
import com.likeminds.feed.android.core.post.viewmodel.LMFeedPostViewModel
import com.likeminds.feed.android.core.socialfeed.model.LMFeedPostViewData

class LMFeedVideoFeedViewModel : ViewModel() {

    val helperViewModel: LMFeedHelperViewModel by lazy {
        LMFeedHelperViewModel()
    }

    val postViewModel: LMFeedPostViewModel by lazy {
        LMFeedPostViewModel()
    }

    var pageToCall = 0
    var previousTotal: Int = 0
    val adapterItems: MutableList<LMFeedPostViewData> = mutableListOf()
    var postsFinished = false
    var adapterPosition = 0

    // stores the state of the view pager by saving the current position and items in adapter
    fun setViewPagerState(position: Int, items: List<LMFeedPostViewData>) {
        adapterPosition = position
        adapterItems.clear()
        adapterItems.addAll(items)
    }
}