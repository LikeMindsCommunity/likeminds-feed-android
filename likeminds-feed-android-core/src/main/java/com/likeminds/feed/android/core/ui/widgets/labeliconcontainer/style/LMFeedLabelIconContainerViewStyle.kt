package com.likeminds.feed.android.core.ui.widgets.labeliconcontainer.style

import androidx.annotation.ColorRes
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.ui.base.styles.LMFeedIconStyle
import com.likeminds.feed.android.core.ui.base.styles.LMFeedTextStyle
import com.likeminds.feed.android.core.ui.widgets.labeliconcontainer.view.LMFeedLabelIconContainerView
import com.likeminds.feed.android.core.utils.LMFeedViewStyle

/**
 * [LMFeedLabelIconContainerViewStyle] helps you to customize the label icon container view [LMFeedLabelIconContainerView]
 *
 * @property containerIconStyle : [LMFeedIconStyle] this will help you to customize the icon in the label icon container view
 * @property containerLabelStyle : [LMFeedTextStyle] this will help you to customize the label text in the label icon container view
 * @property backgroundColor:  [Int] should be in format of [ColorRes] to add background color of the label icon container view | Default value = [null]
 * */
class LMFeedLabelIconContainerViewStyle private constructor(
    val containerIconStyle: LMFeedIconStyle,
    val containerLabelStyle: LMFeedTextStyle,
    @ColorRes val backgroundColor: Int?
) : LMFeedViewStyle {

    class Builder {
        private var containerIconStyle: LMFeedIconStyle = LMFeedIconStyle.Builder()
            .build()

        private var containerLabelStyle: LMFeedTextStyle = LMFeedTextStyle.Builder()
            .textSize(R.dimen.lm_feed_text_large)
            .textColor(R.color.lm_feed_dark_grey)
            .fontResource(R.font.lm_feed_roboto)
            .build()

        @ColorRes
        private var backgroundColor: Int? = null

        fun containerIconStyle(containerIconStyle: LMFeedIconStyle) = apply {
            this.containerIconStyle = containerIconStyle
        }

        fun containerLabelStyle(containerLabelStyle: LMFeedTextStyle) = apply {
            this.containerLabelStyle = containerLabelStyle
        }

        fun backgroundColor(@ColorRes backgroundColor: Int?) = apply {
            this.backgroundColor = backgroundColor
        }

        fun build() = LMFeedLabelIconContainerViewStyle(
            containerIconStyle,
            containerLabelStyle,
            backgroundColor
        )
    }

    fun toBuilder(): Builder {
        return Builder().containerIconStyle(containerIconStyle)
            .containerLabelStyle(containerLabelStyle)
            .backgroundColor(backgroundColor)
    }
}