package com.likeminds.feed.android.core.videofeed.view

import com.likeminds.feed.android.core.utils.feed.*
import com.likeminds.feed.android.core.utils.feed.LMFeedType.PERSONALISED_FEED
import com.likeminds.feed.android.core.utils.feed.LMFeedType.UNIVERSAL_FEED
import com.likeminds.feed.android.core.videofeed.model.LMFeedVideoFeedConfig

open class LMFeedVideoFeedFragment : LMFeedBaseThemeFragment() {

    protected open var customUniversalFeed: LMFeedBaseListViewFragment? = null
    protected open var customPersonalizedFeed: LMFeedBaseListViewFragment? = null

    fun getInstance(
        feedType: LMFeedType,
        config: LMFeedVideoFeedConfig? = null
    ): LMFeedBaseListViewFragment? {
        return when (feedType) {
            PERSONALISED_FEED -> {
                if (customPersonalizedFeed != null) {
                    customPersonalizedFeed
                } else {
                    LMFeedVideoFeedPersonalizedFragment(config)
                }
            }

            UNIVERSAL_FEED -> {
                if (customUniversalFeed != null) {
                    customUniversalFeed
                } else {
                    LMFeedVideoFeedUniversalFragment(config)
                }
            }
        }
    }
}