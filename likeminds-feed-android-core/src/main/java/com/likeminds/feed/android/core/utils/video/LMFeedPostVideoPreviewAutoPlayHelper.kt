package com.likeminds.feed.android.core.utils.video

import android.net.Uri
import com.likeminds.feed.android.core.ui.base.views.LMFeedVideoView
import com.likeminds.feed.android.core.ui.widgets.post.postmedia.view.LMFeedPostVideoMediaView
import com.likeminds.feed.android.core.utils.LMFeedViewUtils

class LMFeedPostVideoPreviewAutoPlayHelper {

    companion object {
        private var videoPreviewAutoPlayHelper: LMFeedPostVideoPreviewAutoPlayHelper? = null

        fun getInstance(): LMFeedPostVideoPreviewAutoPlayHelper {
            if (videoPreviewAutoPlayHelper == null) {
                videoPreviewAutoPlayHelper = LMFeedPostVideoPreviewAutoPlayHelper()
            }
            return videoPreviewAutoPlayHelper!!
        }
    }

    private var lastPlayerView: LMFeedVideoView? = null

    /**
     * @param [videoPost] - Player view in which the provided video is played
     * @param [uri] - If the video is local, then provided [uri] is used to play locally
     * @param [url] - If the video is remote, then provided [url] is used to play locally
     */
    fun playVideoInView(
        videoPost: LMFeedPostVideoMediaView,
        uri: Uri? = null,
        url: String? = null
    ) {
        if (uri == null && url == null) {
            return
        }

        if (lastPlayerView == null || lastPlayerView != videoPost.videoView) {
            if (uri != null) {
                videoPost.playVideo(
                    uri,
                    true,
                    LMFeedViewUtils.getShimmer()
                )
            } else {
                videoPost.playVideo(
                    Uri.parse(url),
                    true,
                    LMFeedViewUtils.getShimmer()
                )
            }
            // stop last player
            removePlayer()
        }
        lastPlayerView = videoPost.videoView
    }

    // removes the player from view and sets it to null
    fun removePlayer() {
        if (lastPlayerView != null) {
            // stop last player
            lastPlayerView?.removePlayer()
            lastPlayerView = null
        }
    }
}