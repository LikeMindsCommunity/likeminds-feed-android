package com.likeminds.feed.android.core.ui.widgets.post.postmedia.view

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.likeminds.feed.android.core.databinding.LmFeedPostVideoMediaViewBinding
import com.likeminds.feed.android.core.ui.base.styles.*
import com.likeminds.feed.android.core.ui.widgets.post.postmedia.style.LMFeedPostVideoMediaViewStyle
import com.likeminds.feed.android.core.utils.LMFeedStyleTransformer
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.hide
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.show

class LMFeedPostVideoMediaView : ConstraintLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet?, defStyle: Int) : super(
        context,
        attributeSet,
        defStyle
    )

    private val inflater =
        (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)

    private val binding = LmFeedPostVideoMediaViewBinding.inflate(inflater, this, true)

    val videoView = binding.videoView

    //sets provided [postVideoMediaViewStyle] to the video view in the post
    fun setStyle(postVideoMediaViewStyle: LMFeedPostVideoMediaViewStyle) {

        postVideoMediaViewStyle.apply {
            //set background color
            backgroundColor?.let {
                binding.videoView.setShutterBackgroundColor(
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
     * @param isVideoLocal - whether the played video is local or not
     */
    fun playVideo(uri: Uri, isVideoLocal: Boolean) {
        binding.apply {
            if (isVideoLocal) {
                videoView.startPlayingLocalUri(
                    uri,
                    pbVideoLoader,
                    ivVideoThumbnail
                )
            } else {
                videoView.startPlayingRemoteUri(
                    uri,
                    pbVideoLoader,
                    ivVideoThumbnail
                )
            }
        }
    }

    // todo: add click listeners and thumbnail
}