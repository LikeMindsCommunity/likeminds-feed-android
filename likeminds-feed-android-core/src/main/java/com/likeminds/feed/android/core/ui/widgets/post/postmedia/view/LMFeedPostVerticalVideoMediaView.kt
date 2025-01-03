package com.likeminds.feed.android.core.ui.widgets.post.postmedia.view

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.likeminds.feed.android.core.databinding.LmFeedPostVerticalVideoMediaViewBinding
import com.likeminds.feed.android.core.ui.base.styles.*
import com.likeminds.feed.android.core.ui.widgets.post.postmedia.style.LMFeedPostVideoMediaViewStyle
import com.likeminds.feed.android.core.utils.LMFeedStyleTransformer
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.hide
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.show
import com.likeminds.feed.android.core.utils.listeners.LMFeedOnClickListener
import com.likeminds.feed.android.core.utils.video.LMFeedVideoPlayerListener
import com.likeminds.feed.android.core.videofeed.model.LMFeedVideoFeedConfig

class LMFeedPostVerticalVideoMediaView : ConstraintLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet?, defStyle: Int) : super(
        context,
        attributeSet,
        defStyle
    )

    private val inflater =
        (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)

    private val binding = LmFeedPostVerticalVideoMediaViewBinding.inflate(inflater, this, true)

    val videoView = binding.videoView

    //sets provided [postVideoMediaViewStyle] to the video view in the post
    fun setStyle(postVideoMediaViewStyle: LMFeedPostVideoMediaViewStyle) {

        postVideoMediaViewStyle.apply {
            //set background color
            backgroundColor?.let {
                binding.root.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        backgroundColor
                    )
                )

                binding.videoView.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        backgroundColor
                    )
                )
            }

            //configure each view of the video view
            configureVideoView(postVideoMediaViewStyle)
            configureProgressBar(videoProgressStyle)
            configureVideoThumbnail(videoThumbnailStyle)
            configurePlayIcon(videoPlayPauseButton, showController)
            configureMuteIcon(videoMuteUnmuteButton)
            configureRemoveIcon(removeIconStyle)
        }
    }

    private fun configureVideoView(postVideoMediaViewStyle: LMFeedPostVideoMediaViewStyle) {
        binding.videoView.apply {
            setStyle(postVideoMediaViewStyle)
        }
    }

    private fun configureProgressBar(videoProgressStyle: LMFeedProgressBarStyle?) {
        binding.pbVideoLoader.apply {
            if (videoProgressStyle != null) {
                setStyle(videoProgressStyle)
                show()
            } else {
                hide()
            }
        }
    }

    private fun configureVideoThumbnail(videoThumbnailStyle: LMFeedImageStyle?) {
        binding.ivVideoThumbnail.apply {
            if (videoThumbnailStyle != null) {
                setStyle(videoThumbnailStyle)
                show()
            } else {
                hide()
            }
        }
    }

    private fun configurePlayIcon(videoPlayPauseButton: LMFeedIconStyle?, showController: Boolean) {
        binding.apply {
            if (showController || videoPlayPauseButton == null) {
                ivPlayPauseVideo.hide()
            } else {
                ivPlayPauseVideo.setStyle(videoPlayPauseButton)
                ivPlayPauseVideo.show()
            }
        }
    }

    private fun configureMuteIcon(videoMuteUnmuteButton: LMFeedIconStyle?) {
        binding.ivMuteUnmuteVideo.apply {
            if (videoMuteUnmuteButton == null) {
                hide()
            } else {
                setStyle(videoMuteUnmuteButton)
                show()
            }
        }
    }

    private fun configureRemoveIcon(removeIconStyle: LMFeedIconStyle?) {
        binding.ivCross.apply {
            if (removeIconStyle == null) {
                hide()
            } else {
                show()
                setStyle(removeIconStyle)
            }
        }
    }

    /**
     * Sets play/pause icon.
     *
     * @param isPlaying - whether the video is playing or not
     */
    fun setPlayPauseIcon(isPlaying: Boolean = false) {
        val iconStyle =
            LMFeedStyleTransformer.postViewStyle.postMediaViewStyle.postVideoMediaStyle?.videoPlayPauseButton
                ?: return

        val playPauseIcon = if (isPlaying) {
            iconStyle.activeSrc
        } else {
            iconStyle.inActiveSrc
        }

        if (playPauseIcon != null) {
            binding.ivPlayPauseVideo.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    playPauseIcon
                )
            )
        }
    }

    /**
     * Sets mute/unmute icon.
     *
     * @param isMute - whether the video playing is mute or not
     */
    fun setMuteUnmuteIcon(isMute: Boolean = false) {
        val iconStyle =
            LMFeedStyleTransformer.postViewStyle.postMediaViewStyle.postVideoMediaStyle?.videoMuteUnmuteButton
                ?: return

        val muteUnmuteIcon = if (isMute) {
            iconStyle.activeSrc
        } else {
            iconStyle.inActiveSrc
        }

        if (muteUnmuteIcon != null) {
            binding.ivMuteUnmuteVideo.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    muteUnmuteIcon
                )
            )
        }
    }

    /**
     * This will play new URI we have provided
     * @param uri - uri to play the video
     * @param isVideoLocal - whether the played video is local or not
     * @param isVideoLocal - whether the played video is local or not
     */
    fun playVideo(
        uri: Uri,
        isVideoLocal: Boolean,
        thumbnailSrc: Any? = null,
        config: LMFeedVideoFeedConfig,
        videoPlayerListener: LMFeedVideoPlayerListener
    ) {
        binding.apply {
            if (isVideoLocal) {
                videoView.startPlayingLocalUri(
                    uri,
                    pbVideoLoader,
                    ivVideoThumbnail,
                    thumbnailSrc
                )
            } else {
                videoView.startPlayingRemoteUri(
                    uri,
                    pbVideoLoader,
                    ivVideoThumbnail,
                    thumbnailSrc,
                    config,
                    videoPlayerListener
                )
            }
        }
    }

    /**
     * Sets click listener on the cross button of the video view
     *
     * @param listener [LMFeedOnClickListener] interface to have click listener
     */
    fun setRemoveIconClickListener(listener: LMFeedOnClickListener) {
        binding.ivCross.setOnClickListener {
            listener.onClick()
        }
    }
}