package com.likeminds.feed.android.core.ui.base.styles

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.ui.base.views.LMFeedSwitch
import com.likeminds.feed.android.core.ui.theme.LMFeedAppearance

/***
 * [LMFeedSwitchStyle] helps to customize the switch [LMFeedSwitch]
 *
 * @property textStyle [LMFeedTextStyle] helps to customize the text style of the switch | Default: [LMFeedTextStyle]
 * @property thumbColor [Int] should be in the form of [ColorRes] to add color to the thumb | Default: [R.color.lm_feed_white_smoke]
 * @property trackColor [Int] should be in the form of [ColorRes] to add color to the track | Default: [LMFeedAppearance.getButtonColor]
 * @property thumbIcon [Int] should be in the form of [DrawableRes] to add icon to the track | Default: null
 * @property thumbIconColor [Int] should be in the form of [ColorRes] to add color to the icon | Default: null
 */
class LMFeedSwitchStyle private constructor(
    //text related
    val textStyle: LMFeedTextStyle,
    @ColorRes
    val thumbColor: Int?,
    @ColorRes
    val trackColor: Int?,
    @DrawableRes
    val thumbIcon: Int?,
    @ColorRes
    val thumbIconColor: Int?,
) {
    class Builder {
        private var textStyle = LMFeedTextStyle.Builder()
            .build()

        private var thumbColor: Int? = R.color.lm_feed_white_smoke
        private var trackColor: Int? = LMFeedAppearance.getButtonColor()
        private var thumbIcon: Int? = null
        private var thumbIconColor: Int? = null

        fun textStyle(textStyle: LMFeedTextStyle) = apply {
            this.textStyle = textStyle
        }

        fun thumbColor(@ColorRes thumbColor: Int?) = apply {
            this.thumbColor = thumbColor
        }

        fun trackColor(@ColorRes trackColor: Int?) = apply {
            this.trackColor = trackColor
        }

        fun thumbIcon(@DrawableRes thumbIcon: Int?) = apply {
            this.thumbIcon = thumbIcon
        }

        fun thumbIconColor(@ColorRes thumbIconColor: Int?) = apply {
            this.thumbIconColor = thumbIconColor
        }

        fun build() = LMFeedSwitchStyle(
            textStyle,
            thumbColor,
            trackColor,
            thumbIcon,
            thumbIconColor
        )
    }

    fun toBuilder(): Builder {
        return Builder()
            .textStyle(textStyle)
            .thumbColor(thumbColor)
            .trackColor(trackColor)
            .thumbIcon(thumbIcon)
            .thumbIconColor(thumbIconColor)
    }

    fun apply(switch: LMFeedSwitch) {
        switch.apply {
            //text related
            textStyle.apply(this)

            //thumb color related
            thumbColor?.let { thumbColor ->
                thumbTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(context, thumbColor)
                )
            }

            //track color related
            trackColor?.let { trackColor ->
                val states = arrayOf(
                    intArrayOf(-android.R.attr.state_checked),
                    intArrayOf(android.R.attr.state_checked)
                )

                val trackColors = intArrayOf(
                    Color.LTGRAY,
                    ContextCompat.getColor(context, trackColor)
                )

                trackTintList = ColorStateList(states, trackColors)
            }

            //thumb icon related
            thumbIcon?.let { thumbIcon ->
                thumbIconDrawable = ContextCompat.getDrawable(context, thumbIcon)
            }

            //thumb icon color related
            thumbIconColor?.let { thumbIconColor ->
                thumbIconTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(context, thumbIconColor)
                )
            }
        }
    }
}

/**
 * Util function that helps to apply all the styling [LMFeedSwitchStyle] to [LMFeedSwitch]
 **/
fun LMFeedSwitch.setStyle(style: LMFeedSwitchStyle) {
    style.apply(this)
}