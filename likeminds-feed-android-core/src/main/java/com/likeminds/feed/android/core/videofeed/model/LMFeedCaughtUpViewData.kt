package com.likeminds.feed.android.core.videofeed.model

import com.likeminds.feed.android.core.utils.base.LMFeedBaseViewType
import com.likeminds.feed.android.core.utils.base.model.ITEM_POST_VIDEO_FEED_CAUGHT_UP

class LMFeedCaughtUpViewData private constructor() : LMFeedBaseViewType {

    override val viewType: Int
        get() = ITEM_POST_VIDEO_FEED_CAUGHT_UP

    class Builder {
        fun build() = LMFeedCaughtUpViewData()
    }

    fun toBuilder(): Builder {
        return Builder()
    }
}