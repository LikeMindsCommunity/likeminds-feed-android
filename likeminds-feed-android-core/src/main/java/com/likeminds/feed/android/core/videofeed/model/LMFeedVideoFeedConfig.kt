package com.likeminds.feed.android.core.videofeed.model

/**
 * Config class for [LMFeedVideoFeedFragment]
 * @param reelViewedAnalyticThreshold: Int | threshold for sending reel viewed event
 */
class LMFeedVideoFeedConfig private constructor(
    val reelViewedAnalyticThreshold: Int
) {

    class Builder {
        private var reelViewedAnalyticThreshold: Int = 2

        fun reelViewedAnalyticThreshold(reelViewedAnalyticThreshold: Int) = apply {
            this.reelViewedAnalyticThreshold = reelViewedAnalyticThreshold
        }

        fun build() = LMFeedVideoFeedConfig(reelViewedAnalyticThreshold)
    }

    fun toBuilder(): Builder {
        return Builder().reelViewedAnalyticThreshold(reelViewedAnalyticThreshold)
    }
}