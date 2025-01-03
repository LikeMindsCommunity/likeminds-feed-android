package com.likeminds.feed.android.core.videofeed.viewstyle

import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.ui.base.styles.LMFeedIconStyle
import com.likeminds.feed.android.core.ui.base.styles.LMFeedTextStyle
import com.likeminds.feed.android.core.utils.LMFeedViewStyle

/**
 * [LMFeedVideoFeedFragmentViewStyle] helps you to customize the video feed fragment [LMFeedVideoFeedFragment]
 *
 * @property videoFeedCaughtUpIcon : [LMFeedIconStyle] this will help you to customize the icon in the video feed caught up layout | set its value to null if you don't want to show any icon
 * @property videoFeedCaughtUpTitleStyle : [LMFeedTextStyle] this will help you to customize the title in the video feed caught up layout | set its value to null if you don't want to show any title
 * @property videoFeedCaughtUpSubtitleStyle : [LMFeedTextStyle] this will help you to customize the subtitle in the video feed caught up layout | set its value to null if you don't want to show any subtitle
 * */
class LMFeedVideoFeedFragmentViewStyle private constructor(
    val videoFeedCaughtUpIcon: LMFeedIconStyle?,
    val videoFeedCaughtUpTitleStyle: LMFeedTextStyle?,
    val videoFeedCaughtUpSubtitleStyle: LMFeedTextStyle?
) : LMFeedViewStyle {

    class Builder {
        private var videoFeedCaughtUpIcon: LMFeedIconStyle? = LMFeedIconStyle.Builder()
            .inActiveSrc(R.drawable.lm_feed_ic_circle_tick)
            .build()

        private var videoFeedCaughtUpTitleStyle: LMFeedTextStyle? = LMFeedTextStyle.Builder()
            .textSize(R.dimen.lm_feed_video_feed_caught_up_title_size)
            .textColor(R.color.lm_feed_black)
            .fontResource(R.font.lm_feed_roboto_medium)
            .build()

        private var videoFeedCaughtUpSubtitleStyle: LMFeedTextStyle? = LMFeedTextStyle.Builder()
            .textSize(R.dimen.lm_feed_text_large)
            .textColor(R.color.lm_feed_pure_blue)
            .fontResource(R.font.lm_feed_roboto)
            .build()

        fun videoFeedCaughtUpIcon(videoFeedCaughtUpIcon: LMFeedIconStyle?) = apply {
            this.videoFeedCaughtUpIcon = videoFeedCaughtUpIcon
        }

        fun videoFeedCaughtUpTitleStyle(videoFeedCaughtUpTitleStyle: LMFeedTextStyle?) = apply {
            this.videoFeedCaughtUpTitleStyle = videoFeedCaughtUpTitleStyle
        }

        fun videoFeedCaughtUpSubtitleStyle(videoFeedCaughtUpSubtitleStyle: LMFeedTextStyle?) =
            apply {
                this.videoFeedCaughtUpSubtitleStyle = videoFeedCaughtUpSubtitleStyle
            }

        fun build() = LMFeedVideoFeedFragmentViewStyle(
            videoFeedCaughtUpIcon,
            videoFeedCaughtUpTitleStyle,
            videoFeedCaughtUpSubtitleStyle
        )
    }

    fun toBuilder(): Builder {
        return Builder().videoFeedCaughtUpIcon(videoFeedCaughtUpIcon)
            .videoFeedCaughtUpTitleStyle(videoFeedCaughtUpTitleStyle)
            .videoFeedCaughtUpSubtitleStyle(videoFeedCaughtUpSubtitleStyle)
    }
}