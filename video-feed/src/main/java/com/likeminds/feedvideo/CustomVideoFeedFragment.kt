package com.likeminds.feedvideo

import android.util.Log
import androidx.viewpager2.widget.ViewPager2
import com.likeminds.feed.android.core.socialfeed.model.LMFeedPostViewData
import com.likeminds.feed.android.core.utils.base.model.ITEM_POST_VIDEO_FEED
import com.likeminds.feed.android.core.videofeed.adapter.LMFeedVideoFeedAdapter
import com.likeminds.feed.android.core.videofeed.view.LMFeedVideoFeedFragment

class CustomVideoFeedFragment : LMFeedVideoFeedFragment(), InvestClickListener {

    override fun customizeVideoFeedListView(
        vp2VideoFeed: ViewPager2,
        videoFeedAdapter: LMFeedVideoFeedAdapter
    ) {
        val customReelsViewDataBinder = CustomReelsViewDataBinder(this, this)
        videoFeedAdapter.replaceViewDataBinder(ITEM_POST_VIDEO_FEED, customReelsViewDataBinder)
    }

    override fun onInvestIconClick(postViewData: LMFeedPostViewData) {
        Log.d(
            "PUI", """
            postViewData: ${postViewData.id}
        """.trimIndent()
        )
    }
}