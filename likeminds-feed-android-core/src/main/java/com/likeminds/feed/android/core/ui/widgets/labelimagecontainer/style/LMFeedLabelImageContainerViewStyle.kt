package com.likeminds.feed.android.core.ui.widgets.labelimagecontainer.style

import androidx.annotation.ColorRes
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.ui.base.styles.*
import com.likeminds.feed.android.core.utils.LMFeedViewStyle

/**
 * [LMFeedLabelImageContainerViewStyle] helps you to customize the label icon container view [LMFeedLabelImageContainerView]
 *
 * @property containerImageStyle : [LMFeedImageStyle] this will help you to customize the image in the label image container view
 * @property containerLabelStyle : [LMFeedTextStyle] this will help you to customize the label text in the label image container view
 * @property backgroundColor:  [Int] should be in format of [ColorRes] to add background color of the label image container view | Default value = [null]
 * */
class LMFeedLabelImageContainerViewStyle private constructor(
    val containerImageStyle: LMFeedImageStyle,
    val containerLabelStyle: LMFeedTextStyle,
    @ColorRes val backgroundColor: Int?
) : LMFeedViewStyle {

    class Builder {
        private var containerImageStyle: LMFeedImageStyle = LMFeedImageStyle.Builder()
            .isCircle(true)
            .build()

        private var containerLabelStyle: LMFeedTextStyle = LMFeedTextStyle.Builder()
            .textSize(R.dimen.lm_feed_text_medium)
            .textColor(R.color.lm_feed_grey_brown_50)
            .fontResource(R.font.lm_feed_roboto)
            .build()

        @ColorRes
        private var backgroundColor: Int? = null

        fun containerImageStyle(containerImageStyle: LMFeedImageStyle) = apply {
            this.containerImageStyle = containerImageStyle
        }

        fun containerLabelStyle(containerLabelStyle: LMFeedTextStyle) = apply {
            this.containerLabelStyle = containerLabelStyle
        }

        fun backgroundColor(@ColorRes backgroundColor: Int?) = apply {
            this.backgroundColor = backgroundColor
        }

        fun build() =
            LMFeedLabelImageContainerViewStyle(
                containerImageStyle,
                containerLabelStyle,
                backgroundColor
            )
    }

    fun toBuilder(): Builder {
        return Builder().containerImageStyle(containerImageStyle)
            .containerLabelStyle(containerLabelStyle)
            .backgroundColor(backgroundColor)
    }
}