package com.likeminds.feed.android.core.videofeed.view

import android.util.Log
import com.likeminds.feed.android.core.socialfeed.model.LMFeedPostViewData

class CVPF : LMFeedVideoFeedPersonalizedFragment() {

    override fun onPostLikeClicked(position: Int, postViewData: LMFeedPostViewData) {
        super.onPostLikeClicked(position, postViewData)
        Log.d("PUI", "post with ${postViewData.id} liked")
    }
}