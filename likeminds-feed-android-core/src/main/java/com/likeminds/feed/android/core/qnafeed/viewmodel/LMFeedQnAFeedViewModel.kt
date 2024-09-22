package com.likeminds.feed.android.core.qnafeed.viewmodel

import androidx.lifecycle.ViewModel
import com.likeminds.feed.android.core.post.viewmodel.LMFeedHelperViewModel
import com.likeminds.likemindsfeed.LMFeedClient

class LMFeedQnAFeedViewModel : ViewModel() {

    val helperViewModel: LMFeedHelperViewModel by lazy {
        LMFeedHelperViewModel()
    }

    private val lmFeedClient: LMFeedClient by lazy {
        LMFeedClient.getInstance()
    }
}