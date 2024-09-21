package com.likeminds.feed.android.core.qnafeed.viewmodel

import androidx.lifecycle.ViewModel
import com.likeminds.likemindsfeed.LMFeedClient

class LMFeedQnAFeedViewModel : ViewModel() {

    private val lmFeedClient: LMFeedClient by lazy {
        LMFeedClient.getInstance()
    }
}