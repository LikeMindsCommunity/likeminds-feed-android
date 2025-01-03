package com.likeminds.feed.android.core.utils.video

interface LMFeedVideoPlayerListener {
    // triggered when reel is viewed at threshold set
    fun onDurationThresholdReached(duration: Long, totalDuration: Long)
}