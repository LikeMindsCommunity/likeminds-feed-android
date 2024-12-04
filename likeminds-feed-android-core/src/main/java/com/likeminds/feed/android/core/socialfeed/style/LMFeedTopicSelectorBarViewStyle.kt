package com.likeminds.feed.android.core.socialfeed.style

import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.ui.base.styles.*
import com.likeminds.feed.android.core.ui.theme.LMFeedAppearance
import com.likeminds.feed.android.core.utils.LMFeedViewStyle

/**
 * [LMFeedTopicSelectorBarViewStyle] helps you to customize the likes fragment [LMFeedTopicSelectorBarView]
 *
 * @property allTopicsSelectorStyle : [LMFeedTextStyle] this will help you to customize the all topics selector text of the topic selector bar
 * @property clearTopicFilterStyle : [LMFeedTextStyle] this will help you to customize the clear topic filter text of the topic selector bar
 * @property selectedTopicTextStyle : [LMFeedTextStyle] this will help you to customize the selected topic text of the topic selector bar
 * @property removeSelectedTopicIconStyle : [LMFeedIconStyle] this will help you to customize the remove selected topic icon of the topic selector bar
 * @property backgroundColor: [Int] should be in format of [ColorRes] this will help you to customize the background color of the topic selector bar | Default value = [null]
 * @property elevation: [Int] should be in format of [DimenRes] to add custom elevation of the topic selector bar | Default value =  [null]
 * */
class LMFeedTopicSelectorBarViewStyle private constructor(
    //all topics selector style
    val allTopicsSelectorStyle: LMFeedTextStyle,
    //clear topic filter view style
    val clearTopicFilterStyle: LMFeedTextStyle,
    //selected topic name text style
    val selectedTopicTextStyle: LMFeedTextStyle,
    //remove selected topic icon style
    val removeSelectedTopicIconStyle: LMFeedIconStyle,
    //background color of the selector bar
    @ColorRes val backgroundColor: Int?,
    //elevation of the bar
    @DimenRes val elevation: Int?,
) : LMFeedViewStyle {
    class Builder {
        private var allTopicsSelectorStyle: LMFeedTextStyle = LMFeedTextStyle.Builder()
            .drawableRightSrc(R.drawable.lm_feed_ic_arrow_down)
            .textColor(R.color.lm_feed_grey)
            .textSize(R.dimen.lm_feed_text_large)
            .build()

        private var clearTopicFilterStyle: LMFeedTextStyle = LMFeedTextStyle.Builder()
            .textColor(LMFeedAppearance.getButtonColor())
            .textSize(R.dimen.lm_feed_text_large)
            .build()

        private var selectedTopicTextStyle: LMFeedTextStyle = LMFeedTextStyle.Builder()
            .textColor(LMFeedAppearance.getButtonColor())
            .textSize(R.dimen.lm_feed_text_large)
            .build()

        private var removeSelectedTopicIconStyle: LMFeedIconStyle = LMFeedIconStyle.Builder()
            .iconTint(LMFeedAppearance.getButtonColor())
            .inActiveSrc(R.drawable.lm_feed_ic_cross_topics)
            .build()

        @ColorRes
        private var backgroundColor: Int? = null

        @DimenRes
        private var elevation: Int? = null

        fun allTopicsSelectorStyle(allTopicsSelectorStyle: LMFeedTextStyle) = apply {
            this.allTopicsSelectorStyle = allTopicsSelectorStyle
        }

        fun clearTopicFilterStyle(clearTopicFilterStyle: LMFeedTextStyle) = apply {
            this.clearTopicFilterStyle = clearTopicFilterStyle
        }

        fun selectedTopicTextStyle(selectedTopicTextStyle: LMFeedTextStyle) = apply {
            this.selectedTopicTextStyle = selectedTopicTextStyle
        }

        fun removeSelectedTopicIconStyle(removeSelectedTopicIconStyle: LMFeedIconStyle) = apply {
            this.removeSelectedTopicIconStyle = removeSelectedTopicIconStyle
        }

        fun backgroundColor(@ColorRes backgroundColor: Int?) = apply {
            this.backgroundColor = backgroundColor
        }

        fun elevation(@DimenRes elevation: Int?) = apply {
            this.elevation = elevation
        }

        fun build() = LMFeedTopicSelectorBarViewStyle(
            allTopicsSelectorStyle,
            clearTopicFilterStyle,
            selectedTopicTextStyle,
            removeSelectedTopicIconStyle,
            backgroundColor,
            elevation
        )
    }

    fun toBuilder(): Builder {
        return Builder().allTopicsSelectorStyle(allTopicsSelectorStyle)
            .clearTopicFilterStyle(clearTopicFilterStyle)
            .selectedTopicTextStyle(selectedTopicTextStyle)
            .removeSelectedTopicIconStyle(removeSelectedTopicIconStyle)
            .backgroundColor(backgroundColor)
            .elevation(elevation)
    }
}