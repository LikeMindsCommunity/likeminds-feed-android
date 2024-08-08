package com.likeminds.feedexample.universalfeed

import com.likeminds.feed.android.core.universalfeed.view.LMFeedUniversalFeedFragment
import com.likeminds.feed.android.core.universalfeed.view.LMFeedUniversalFeedListView
import com.likeminds.feed.android.core.utils.base.model.ITEM_POST_CUSTOM_WIDGET

class CustomLMUniversalFeedAdminFragment : LMFeedUniversalFeedFragment() {
    override fun customizeUniversalFeedListView(rvUniversal: LMFeedUniversalFeedListView) {
        val abcCustomViewDataBinder = ABCCustomViewDataBinder()
        rvUniversal.universalFeedAdapter.replaceViewDataBinder(
            ITEM_POST_CUSTOM_WIDGET,
            abcCustomViewDataBinder
        )
    }
}