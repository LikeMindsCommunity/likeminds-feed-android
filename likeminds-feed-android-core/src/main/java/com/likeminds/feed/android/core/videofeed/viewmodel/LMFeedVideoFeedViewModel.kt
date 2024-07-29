package com.likeminds.feed.android.core.videofeed.viewmodel

import androidx.lifecycle.ViewModel
import com.likeminds.likemindsfeed.LMFeedClient

class LMFeedVideoFeedViewModel : ViewModel() {

    private val lmFeedClient: LMFeedClient by lazy {
        LMFeedClient.getInstance()
    }
}