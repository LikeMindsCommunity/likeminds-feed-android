package com.likeminds.feed.android.core.socialfeed.style

import android.text.TextUtils
import androidx.annotation.ColorRes
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.ui.base.styles.LMFeedImageStyle
import com.likeminds.feed.android.core.ui.base.styles.LMFeedProgressBarStyle
import com.likeminds.feed.android.core.ui.base.styles.LMFeedTextStyle
import com.likeminds.feed.android.core.ui.theme.LMFeedAppearance
import com.likeminds.feed.android.core.utils.LMFeedViewStyle

/**
 * [LMFeedPostingViewStyle] helps to customize [LMFeedPostingView] in the following way
 * @property attachmentThumbnailImageStyle: [LMFeedImageStyle] this will help to customize thumbnail of the post | Set value to [null] to hide the thumbnail
 * @property postingHeadingTextStyle: [LMFeedTextStyle] this will help to customize posting heading
 * @property progressStyle: [LMFeedProgressBarStyle] this will help to customize progress bar to show uploading status
 * @property retryButtonTextStyle: [LMFeedTextStyle] this will help to customize retry CTA
 * @property postingDoneImageStyle: [LMFeedImageStyle] this will help to customize the image to show, once uploading is done.
 * @property backgroundColor: [Int] should be in format of [ColorRes] to add background Color the view| Default value is [null]
 */
class LMFeedPostingViewStyle private constructor(
    val attachmentThumbnailImageStyle: LMFeedImageStyle?,
    val postingHeadingTextStyle: LMFeedTextStyle,
    val progressStyle: LMFeedProgressBarStyle,
    val retryButtonTextStyle: LMFeedTextStyle,
    val postingDoneImageStyle: LMFeedImageStyle,
    @ColorRes val backgroundColor: Int?
) : LMFeedViewStyle {

    class Builder {
        private var attachmentThumbnailImageStyle: LMFeedImageStyle? = null

        private var postingHeadingTextStyle: LMFeedTextStyle = LMFeedTextStyle.Builder()
            .textSize(R.dimen.lm_feed_text_medium)
            .textColor(R.color.lm_feed_dark_grey)
            .ellipsize(TextUtils.TruncateAt.END)
            .build()

        private var progressStyle: LMFeedProgressBarStyle = LMFeedProgressBarStyle.Builder()
            .isIndeterminate(false)
            .progressColor(LMFeedAppearance.getButtonColor())
            .build()

        private var retryButtonTextStyle: LMFeedTextStyle = LMFeedTextStyle.Builder()
            .textColor(R.color.lm_feed_grey)
            .textSize(R.dimen.lm_feed_text_medium)
            .ellipsize(TextUtils.TruncateAt.END)
            .build()

        private var postingDoneImageStyle: LMFeedImageStyle = LMFeedImageStyle.Builder()
            .isCircle(true)
            .imageSrc(R.drawable.lm_feed_ic_completed_green)
            .build()

        @ColorRes
        private var backgroundColor: Int? = null


        fun attachmentThumbnailImageStyle(attachmentThumbnailImageStyle: LMFeedImageStyle?) =
            apply { this.attachmentThumbnailImageStyle = attachmentThumbnailImageStyle }

        fun postingHeadingTextStyle(postingHeadingTextStyle: LMFeedTextStyle) =
            apply { this.postingHeadingTextStyle = postingHeadingTextStyle }

        fun progressStyle(progressStyle: LMFeedProgressBarStyle) =
            apply { this.progressStyle = progressStyle }

        fun retryButtonTextStyle(retryButtonTextStyle: LMFeedTextStyle) =
            apply { this.retryButtonTextStyle = retryButtonTextStyle }

        fun postingDoneImageStyle(postingDoneImageStyle: LMFeedImageStyle) =
            apply { this.postingDoneImageStyle = postingDoneImageStyle }

        fun backgroundColor(backgroundColor: Int?) = apply {
            this.backgroundColor = backgroundColor
        }

        fun build() = LMFeedPostingViewStyle(
            attachmentThumbnailImageStyle,
            postingHeadingTextStyle,
            progressStyle,
            retryButtonTextStyle,
            postingDoneImageStyle,
            backgroundColor
        )
    }

    fun toBuilder(): Builder {
        return Builder().attachmentThumbnailImageStyle(attachmentThumbnailImageStyle)
            .postingDoneImageStyle(postingDoneImageStyle)
            .progressStyle(progressStyle)
            .retryButtonTextStyle(retryButtonTextStyle)
            .postingHeadingTextStyle(postingHeadingTextStyle)
            .backgroundColor(backgroundColor)
    }
}