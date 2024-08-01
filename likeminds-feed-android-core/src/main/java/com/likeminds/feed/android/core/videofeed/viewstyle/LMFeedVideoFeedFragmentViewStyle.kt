package com.likeminds.feed.android.core.videofeed.viewstyle

import com.likeminds.feed.android.core.utils.LMFeedViewStyle

/**
 * [LMFeedVideoFeedFragmentViewStyle] helps you to customize the video feed fragment [LMFeedVideoFeedFragment]
 *
 *
 * */
class LMFeedVideoFeedFragmentViewStyle private constructor(

) : LMFeedViewStyle {

    class Builder {
        fun build() = LMFeedVideoFeedFragmentViewStyle()
    }

    fun toBuilder(): Builder {
        return Builder()
    }
}