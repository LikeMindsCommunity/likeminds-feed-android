package com.likeminds.feed.android.core.utils.video

interface LMFeedVideoPlayerListener {
    // triggered when video is viewed at threshold set
    fun onDurationThresholdReached(duration: Long, totalDuration: Long)

    // triggered when video is swiped or scrolled at threshold set
    fun onVideoSwipedOrScrolled(duration: Long, totalDuration: Long)

    fun onIdleSwipeReached()
}