package com.likeminds.feed.android.core.ui.widgets.post.postmedia.style

import android.text.TextUtils
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.ui.base.styles.*
import com.likeminds.feed.android.core.utils.LMFeedViewStyle

/**
 * [LMFeedPostLinkMediaViewStyle] helps you to customize the post link media view [LMFeedPostLinkMediaView]
 *
 * @property linkTitleStyle : [LMFeedTextStyle] this will help you to customize the title text in the link media view
 * @property linkDescriptionStyle : [LMFeedTextStyle] this will help you to customize the description text in the link media view
 * @property linkUrlStyle: [LMFeedTextStyle] this will help you to customize the link url text in the link media view | set its value to [null] if you want to hide the url text in the link media view
 * @property linkImageStyle: [LMFeedImageStyle] this will help you to customize the link image in the link media view
 * @property linkRemoveIconStyle: [LMFeedIconStyle] this will help you to customize the remove link icon in the link media view | set its value to [null] if you want to hide the remove link icon in the link media view
 * @property linkBoxCornerRadius: [Int] should be in format of [DimenRes] this will help you to customize the corner radius of the link media view | Default value = [null]
 * @property linkBoxElevation: [Int] should be in format of [DimenRes] this will help you to customize the elevation of the link media view | Default value = [null]
 * @property linkBoxStrokeColor: [Int] should be in format of [ColorRes] this will help you to customize the stroke color of the link media view | Default value = [null]
 * @property linkBoxStrokeWidth: [Int] should be in format of [DimenRes] this will help you to customize the stroke width of the link media view | Default value = [null]
 * @property backgroundColor: [Int] should be in format of [ColorRes] this will help you to customize the background color of the link media view | Default value = [null]
 * */
class LMFeedPostLinkMediaViewStyle private constructor(
    //link title style
    val linkTitleStyle: LMFeedTextStyle,
    //link description style
    val linkDescriptionStyle: LMFeedTextStyle?,
    //link url style
    val linkUrlStyle: LMFeedTextStyle?,
    //link image style
    val linkImageStyle: LMFeedImageStyle,
    //link remove icon style
    val linkRemoveIconStyle: LMFeedIconStyle?,
    //link box corner radius style
    @DimenRes val linkBoxCornerRadius: Int?,
    //link box elevation style
    @DimenRes val linkBoxElevation: Int?,
    //link box stroke color
    @ColorRes val linkBoxStrokeColor: Int?,
    //link box stroke width
    @DimenRes val linkBoxStrokeWidth: Int?,
    //link box background color
    @ColorRes val backgroundColor: Int?
) : LMFeedViewStyle {

    class Builder {
        private var linkTitleStyle: LMFeedTextStyle = LMFeedTextStyle.Builder()
            .ellipsize(TextUtils.TruncateAt.END)
            .textSize(R.dimen.lm_feed_text_large)
            .maxLines(2)
            .fontResource(R.font.lm_feed_roboto_medium)
            .textColor(R.color.lm_feed_grey)
            .build()

        private var linkDescriptionStyle: LMFeedTextStyle? = null

        private var linkUrlStyle: LMFeedTextStyle? = null

        private var linkImageStyle: LMFeedImageStyle = LMFeedImageStyle.Builder()
            .placeholderSrc(R.drawable.lm_feed_ic_link)
            .scaleType(ImageView.ScaleType.CENTER)
            .isCircle(false)
            .build()

        private var linkRemoveIconStyle: LMFeedIconStyle? = null

        @DimenRes
        private var linkBoxCornerRadius: Int? = null

        @DimenRes
        private var linkBoxElevation: Int? = null

        @ColorRes
        private var linkBoxStrokeColor: Int? = null

        @DimenRes
        private var linkBoxStrokeWidth: Int? = null

        @ColorRes
        private var backgroundColor: Int? = null

        fun linkTitleStyle(linkTitleStyle: LMFeedTextStyle) = apply {
            this.linkTitleStyle = linkTitleStyle
        }

        fun linkDescriptionStyle(linkDescriptionStyle: LMFeedTextStyle?) = apply {
            this.linkDescriptionStyle = linkDescriptionStyle
        }

        fun linkUrlStyle(linkUrlStyle: LMFeedTextStyle?) = apply {
            this.linkUrlStyle = linkUrlStyle
        }

        fun linkImageStyle(linkImageStyle: LMFeedImageStyle) = apply {
            this.linkImageStyle = linkImageStyle
        }

        fun linkRemoveIconStyle(linkRemoveIconStyle: LMFeedIconStyle?) = apply {
            this.linkRemoveIconStyle = linkRemoveIconStyle
        }

        fun linkBoxCornerRadius(@DimenRes linkBoxCornerRadius: Int?) = apply {
            this.linkBoxCornerRadius = linkBoxCornerRadius
        }

        fun linkBoxElevation(@DimenRes linkBoxElevation: Int?) = apply {
            this.linkBoxElevation = linkBoxElevation
        }

        fun linkBoxStrokeColor(@ColorRes linkBoxStrokeColor: Int?) = apply {
            this.linkBoxStrokeColor = linkBoxStrokeColor
        }

        fun linkBoxStrokeWidth(@DimenRes linkBoxStrokeWidth: Int?) = apply {
            this.linkBoxStrokeWidth = linkBoxStrokeWidth
        }

        fun backgroundColor(@ColorRes backgroundColor: Int?) = apply {
            this.backgroundColor = backgroundColor
        }

        fun build() = LMFeedPostLinkMediaViewStyle(
            linkTitleStyle,
            linkDescriptionStyle,
            linkUrlStyle,
            linkImageStyle,
            linkRemoveIconStyle,
            linkBoxCornerRadius,
            linkBoxElevation,
            linkBoxStrokeColor,
            linkBoxStrokeWidth,
            backgroundColor
        )
    }

    fun toBuilder(): Builder {
        return Builder().linkTitleStyle(linkTitleStyle)
            .linkDescriptionStyle(linkDescriptionStyle)
            .linkUrlStyle(linkUrlStyle)
            .linkImageStyle(linkImageStyle)
            .linkRemoveIconStyle(linkRemoveIconStyle)
            .linkBoxStrokeColor(linkBoxStrokeColor)
            .linkBoxStrokeWidth(linkBoxStrokeWidth)
            .backgroundColor(backgroundColor)
            .linkBoxCornerRadius(linkBoxCornerRadius)
            .linkBoxElevation(linkBoxElevation)
    }
}