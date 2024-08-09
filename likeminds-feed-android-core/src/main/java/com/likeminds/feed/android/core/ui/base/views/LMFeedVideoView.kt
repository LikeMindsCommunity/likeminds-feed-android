package com.likeminds.feed.android.core.ui.base.views

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultAllocator
import com.likeminds.feed.android.core.LMFeedCoreApplication.Companion.LOG_TAG
import com.likeminds.feed.android.core.ui.widgets.post.postmedia.style.LMFeedPostVideoMediaViewStyle
import com.likeminds.feed.android.core.utils.LMFeedStyleTransformer
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.hide
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.show
import com.likeminds.feed.android.core.utils.video.LMFeedVideoCache

/**
 * Represents a video view
 * To customize this view use [LMFeedPostVideoMediaViewStyle]
 */
class LMFeedVideoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : StyledPlayerView(context, attrs, defStyleAttr) {

    private lateinit var exoPlayer: ExoPlayer
    private var progressBar: LMFeedProgressBar? = null
    private var thumbnailView: LMFeedImageView? = null

    private var lastPos: Long = 0

    init {
        descendantFocusability = FOCUS_AFTER_DESCENDANTS
        init()
    }

    // initializes the exoplayer and sets player
    private fun init() {
        // used to configure ms of media to buffer before starting playback
        val defaultLoadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                DefaultLoadControl.DEFAULT_MAX_BUFFER_MS,
                DefaultLoadControl.DEFAULT_MAX_BUFFER_MS,
                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS,
                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
            )
            .setAllocator(DefaultAllocator(true, 16))
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()

        exoPlayer = ExoPlayer.Builder(context)
            .setLoadControl(defaultLoadControl)
            .build()

        exoPlayer.repeatMode = Player.REPEAT_MODE_ONE
        exoPlayer.playWhenReady = false
        player = exoPlayer

        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                when (playbackState) {
                    Player.STATE_READY -> {
                        Log.d(LOG_TAG, "STATE_READY: ")
                        thumbnailView?.hide()
                        progressBar?.hide()
                        show()
                    }

                    Player.STATE_BUFFERING -> {
                        Log.d(LOG_TAG, "STATE_BUFFERING: ")
                        progressBar?.show()
                    }

                    Player.STATE_IDLE -> {
                        Log.d(LOG_TAG, "STATE_IDLE: ")
                        progressBar?.hide()
                        thumbnailView?.hide()
                    }

                    Player.STATE_ENDED -> {
                        Log.d(LOG_TAG, "STATE_ENDED: ")
                        progressBar?.hide()
                        thumbnailView?.hide()
                    }
                }
            }
        })
    }

    /**
     * This will reuse the player and will play new URI (remote url) we have provided
     */
    fun startPlayingRemoteUri(
        videoUri: Uri,
        progressBar: LMFeedProgressBar,
        thumbnailView: LMFeedImageView,
        thumbnailSrc: Any? = null
    ) {
        //progress style is null then we don't have to show the progressBar
        if (LMFeedStyleTransformer.postViewStyle.postMediaViewStyle.postVideoMediaStyle?.videoProgressStyle != null) {
            this.progressBar = progressBar
        }

        setThumbnail(thumbnailView, thumbnailSrc)

        val mediaSource =
            ProgressiveMediaSource.Factory(LMFeedVideoCache.getInstance(context.applicationContext))
                .createMediaSource(MediaItem.fromUri(videoUri))
        exoPlayer.setMediaSource(mediaSource)
        exoPlayer.seekTo(lastPos)
        exoPlayer.playWhenReady = true
        exoPlayer.prepare()
    }

    /**
     * This will reuse the player and will play new URI (local uri) we have provided
     */
    fun startPlayingLocalUri(
        videoUri: Uri,
        progressBar: LMFeedProgressBar,
        thumbnailView: LMFeedImageView,
        thumbnailSrc: Any? = null
    ) {
        //progress style is null then we don't have to show the progressBar
        if (LMFeedStyleTransformer.postViewStyle.postMediaViewStyle.postVideoMediaStyle?.videoProgressStyle != null) {
            this.progressBar = progressBar
        }

        setThumbnail(thumbnailView, thumbnailSrc)

        val mediaSource = MediaItem.fromUri(videoUri)
        exoPlayer.setMediaItem(mediaSource)
        exoPlayer.seekTo(lastPos)
        exoPlayer.playWhenReady = true
        exoPlayer.prepare()
    }

    /**
     * This will stop the player, but stopping the player shows black screen
     * so to cover that we set alpha to 0 of player
     * and lastFrame of player using imageView over player to make it look like paused player
     *
     * If we will not stop the player, only pause it, then it can cause memory issue due to overload of player
     * and paused player can not be played with new URL, after stopping the player we can reuse that with new URL
     *
     */
    fun removePlayer() {
        exoPlayer.playWhenReady = false
        lastPos = exoPlayer.currentPosition
        exoPlayer.stop()
    }

    //sets provided [postVideoMediaStyle] to the post video view
    fun setStyle(postVideoMediaStyle: LMFeedPostVideoMediaViewStyle) {
        keepScreenOn = postVideoMediaStyle.keepScreenOn
        useController = postVideoMediaStyle.showController
        controllerAutoShow = postVideoMediaStyle.controllerAutoShow
        controllerShowTimeoutMs = postVideoMediaStyle.controllerShowTimeoutMs
    }

    private fun setThumbnail(thumbnailView: LMFeedImageView, thumbnailSrc: Any?) {
        this.thumbnailView = thumbnailView

        val thumbnailStyle =
            LMFeedStyleTransformer.postViewStyle.postMediaViewStyle.postVideoMediaStyle?.videoThumbnailStyle
                ?: return

        val finalThumbnailSrc = thumbnailSrc ?: thumbnailStyle.imageSrc

        thumbnailView.show()
        thumbnailView.setImage(finalThumbnailSrc, thumbnailStyle)
    }
}