package com.likeminds.feed.android.core.socialfeed.viewmodel

import androidx.lifecycle.ViewModel
import com.likeminds.feed.android.core.post.viewmodel.LMFeedHelperViewModel
import com.likeminds.feed.android.core.post.viewmodel.LMFeedPostViewModel

class LMFeedSocialFeedViewModel : ViewModel() {

    val helperViewModel: LMFeedHelperViewModel by lazy {
        LMFeedHelperViewModel()
    }

    val postViewModel: LMFeedPostViewModel by lazy {
        LMFeedPostViewModel()
    }
}