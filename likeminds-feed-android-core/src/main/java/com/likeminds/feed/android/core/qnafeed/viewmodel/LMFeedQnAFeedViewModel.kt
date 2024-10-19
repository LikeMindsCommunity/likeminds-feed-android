package com.likeminds.feed.android.core.qnafeed.viewmodel

import androidx.lifecycle.ViewModel
import com.likeminds.feed.android.core.post.viewmodel.LMFeedHelperViewModel
import com.likeminds.feed.android.core.post.viewmodel.LMFeedPostViewModel

class LMFeedQnAFeedViewModel : ViewModel() {

    val helperViewModel: LMFeedHelperViewModel by lazy {
        LMFeedHelperViewModel()
    }

    val postViewModel: LMFeedPostViewModel by lazy {
        LMFeedPostViewModel()
    }
}