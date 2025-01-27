package com.likeminds.feed.android.core.ui.base.views

import android.content.Context
import android.net.Uri
import android.os.Looper
import android.util.AttributeSet
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultAllocator
import com.google.android.exoplayer2.util.Util
import com.likeminds.feed.android.core.ui.widgets.post.postmedia.style.LMFeedPostVideoMediaViewStyle
import com.likeminds.feed.android.core.utils.LMFeedStyleTransformer
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.hide
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.show
import com.likeminds.feed.android.core.utils.video.LMFeedVideoCache
import com.likeminds.feed.android.core.utils.video.LMFeedVideoPlayerListener
import com.likeminds.feed.android.core.videofeed.model.LMFeedVideoFeedConfig

/**
 * Represents a video view
 * To customize this view use [LMFeedPostVideoMediaViewStyle]
 */
class LMFeedVideoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : StyledPlayerView(context, attrs, defStyleAttr) {

    companion object {
        private const val SCROLL_STATE_IDLE_THRESHOLD = 5000L
    }

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
                        thumbnailView?.hide()
                        progressBar?.hide()
                        show()
                    }

                    Player.STATE_BUFFERING -> {
                        progressBar?.show()
                    }

                    Player.STATE_IDLE -> {
                        progressBar?.hide()
                        thumbnailView?.hide()
                    }

                    Player.STATE_ENDED -> {
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
        thumbnailSrc: Any? = null,
        config: LMFeedVideoFeedConfig? = null,
        videoPlayerListener: LMFeedVideoPlayerListener? = null
    ) {
        //progress style is null then we don't have to show the progressBar
        if (LMFeedStyleTransformer.postViewStyle.postMediaViewStyle.postVideoMediaStyle?.videoProgressStyle != null) {
            this.progressBar = progressBar
        }

        val thresholdValue = (config?.reelViewedAnalyticThreshold ?: 2) * 1000L

        setThumbnail(thumbnailView, thumbnailSrc)

        // create and send analytic for playing reel at threshold
        exoPlayer.createMessage { _, _ ->
            val currentTime = exoPlayer.currentPosition
            val totalDuration = exoPlayer.duration
            videoPlayerListener?.onDurationThresholdReached(currentTime, totalDuration)
        }.setPosition(thresholdValue)
            .setDeleteAfterDelivery(true)
            .setLooper(Looper.getMainLooper())
            .send()

        // create message on reaching swipe idle state
        exoPlayer.createMessage { _, _ ->
            videoPlayerListener?.onIdleSwipeReached()
        }.setPosition(SCROLL_STATE_IDLE_THRESHOLD)
            .setDeleteAfterDelivery(true)
            .setLooper(Looper.getMainLooper())
            .send()

        val mediaSource = createCachedMediaSource(context.applicationContext, videoUri)
        exoPlayer.setMediaSource(mediaSource)
        exoPlayer.seekTo(lastPos)
        exoPlayer.playWhenReady = true
        exoPlayer.prepare()
    }

    private fun createCachedMediaSource(context: Context, uri: Uri): MediaSource {
        val type = inferContentType(uri)
        return when (type) {
            C.CONTENT_TYPE_DASH ->
                DashMediaSource.Factory(LMFeedVideoCache.getCacheDataSourceFactory(context))
                    .createMediaSource(MediaItem.fromUri(uri))

            C.CONTENT_TYPE_SS ->
                SsMediaSource.Factory(LMFeedVideoCache.getCacheDataSourceFactory(context))
                    .createMediaSource(MediaItem.fromUri(uri))

            C.CONTENT_TYPE_HLS ->
                HlsMediaSource.Factory(LMFeedVideoCache.getCacheDataSourceFactory(context))
                    .createMediaSource(MediaItem.fromUri(uri))

            else -> {
                return ProgressiveMediaSource.Factory(
                    LMFeedVideoCache.getCacheDataSourceFactory(
                        context
                    )
                )
                    .createMediaSource(MediaItem.fromUri(uri))
            }
        }
    }

    private fun inferContentType(uri: Uri): Int {
        val fileName: String = (uri.toString())
        if (fileName.endsWith(".m3u8")) {
            return C.CONTENT_TYPE_HLS
        }
        return Util.inferContentType(uri)
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
    fun removePlayer(
        triggerSwipeOrScrollEvent: Boolean = false,
        videoPlayerListener: LMFeedVideoPlayerListener? = null
    ) {
        exoPlayer.playWhenReady = false
        lastPos = exoPlayer.currentPosition
        if (triggerSwipeOrScrollEvent) {
            videoPlayerListener?.onVideoSwipedOrScrolled(lastPos, exoPlayer.duration)
        }
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