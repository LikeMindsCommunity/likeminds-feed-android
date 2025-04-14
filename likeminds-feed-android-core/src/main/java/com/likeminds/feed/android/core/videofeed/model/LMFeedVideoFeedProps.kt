package com.likeminds.feed.android.core.videofeed.model

/**
 * Extra properties required to start the video feed
 * @param startFeedWithPostIds List of post ids to start the feed with
 */
class LMFeedVideoFeedProps private constructor(
    val startFeedWithPostIds: List<String>?
) {
    class Builder {
        private var startFeedWithPostIds: List<String>? = null

        fun startFeedWithPostIds(startFeedWithPostIds: List<String>?) = apply {
            this.startFeedWithPostIds = startFeedWithPostIds
        }

        fun build() = LMFeedVideoFeedProps(startFeedWithPostIds)
    }

    fun toBuilder(): Builder {
        return Builder().startFeedWithPostIds(startFeedWithPostIds)
    }
}