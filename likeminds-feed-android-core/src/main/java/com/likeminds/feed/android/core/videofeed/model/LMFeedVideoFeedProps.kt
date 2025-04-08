package com.likeminds.feed.android.core.videofeed.model

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