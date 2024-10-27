package com.likeminds.feed.android.core.ui.widgets.alertdialog.style

import android.graphics.Typeface
import android.text.TextUtils
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.ui.base.styles.*
import com.likeminds.feed.android.core.ui.theme.LMFeedAppearance
import com.likeminds.feed.android.core.utils.LMFeedViewStyle

/**
 * [LMFeedAlertDialogViewStyle] helps you to customize the alert dialog view [LMFeedAlertDialogView]
 *
 * @property alertTitleText : [LMFeedTextStyle] this will help you to customize the title text in the alert dialog view
 * @property alertSubtitleText : [LMFeedTextStyle] this will help you to customize the subtitle text in the alert dialog view | set its value to [null] if you want to hide the subtitle text in the alert dialog view
 * @property alertNegativeButtonStyle: [LMFeedTextStyle] this will help you to customize the negative button in the alert dialog view | set its value to [null] if you want to hide the negative button in the alert dialog view
 * @property alertPositiveButtonStyle: [LMFeedTextStyle] this will help you to customize the positive button in the alert dialog view | set its value to [null] if you want to hide the positive button in the alert dialog view
 * @property alertSelectorStyle: [LMFeedTextStyle] this will help you to customize the selector view in the alert dialog view | set its value to [null] if you want to hide the selector view in the alert dialog view
 * @property alertInputStyle: [LMFeedEditTextStyle] this will help you to customize the input box in the alert dialog view  | set its value to [null] if you want to hide the input box in the alert dialog view
 * @property alertActivePositiveButtonColor: [Int] should be in format of [ColorRes] this will help you to customize the color of the active positive button | Default value = [R.color.lm_feed_majorelle_blue]
 * @property alertBoxElevation: [Int] should be in format of [DimenRes] this will help you to customize the elevation of the alert dialog view | Default value = [null]
 * @property alertBoxCornerRadius: [Int] should be in format of [DimenRes] this will help you to customize the corner radius of the alert dialog view | Default value = [null]
 * @property backgroundColor: [Int] should be in format of [ColorRes] to add background color of the alert dialog view | Default value =  [null]
 * */
class LMFeedAlertDialogViewStyle private constructor(
    //alert title text style
    val alertTitleText: LMFeedTextStyle,
    //alert subtitle text style
    val alertSubtitleText: LMFeedTextStyle?,
    //alert negative button style
    val alertNegativeButtonStyle: LMFeedTextStyle?,
    //alert positive button style
    val alertPositiveButtonStyle: LMFeedTextStyle?,
    //alert selector box style
    val alertSelectorStyle: LMFeedTextStyle?,
    //alert input field style
    val alertInputStyle: LMFeedEditTextStyle?,
    //alert active positive button color
    @ColorRes val alertActivePositiveButtonColor: Int,
    //alert box elevation
    @DimenRes val alertBoxElevation: Int?,
    //alert box corner radius
    @DimenRes val alertBoxCornerRadius: Int?,
    //alert box background color
    @ColorRes val backgroundColor: Int?
) : LMFeedViewStyle {

    class Builder {
        private var alertTitleText: LMFeedTextStyle = LMFeedTextStyle.Builder()
            .ellipsize(TextUtils.TruncateAt.END)
            .textSize(R.dimen.lm_feed_text_medium)
            .textColor(R.color.lm_feed_black_87)
            .fontResource(R.font.lm_feed_roboto_medium)
            .typeface(Typeface.NORMAL)
            .build()

        private var alertSubtitleText: LMFeedTextStyle? = null

        private var alertNegativeButtonStyle: LMFeedTextStyle? = null

        private var alertPositiveButtonStyle: LMFeedTextStyle? = null

        private var alertSelectorStyle: LMFeedTextStyle? = null

        private var alertInputStyle: LMFeedEditTextStyle? = null

        @ColorRes
        private var alertActivePositiveButtonColor: Int = LMFeedAppearance.getButtonColor()

        @DimenRes
        private var alertBoxElevation: Int? = null

        @DimenRes
        private var alertBoxCornerRadius: Int? = null

        @ColorRes
        private var backgroundColor: Int? = null

        fun alertTitleText(alertTitleText: LMFeedTextStyle) = apply {
            this.alertTitleText = alertTitleText
        }

        fun alertSubtitleText(alertSubtitleText: LMFeedTextStyle?) = apply {
            this.alertSubtitleText = alertSubtitleText
        }

        fun alertNegativeButtonStyle(alertNegativeButtonStyle: LMFeedTextStyle?) = apply {
            this.alertNegativeButtonStyle = alertNegativeButtonStyle
        }

        fun alertPositiveButtonStyle(alertPositiveButtonStyle: LMFeedTextStyle?) = apply {
            this.alertPositiveButtonStyle = alertPositiveButtonStyle
        }

        fun alertSelectorStyle(alertSelectorStyle: LMFeedTextStyle?) = apply {
            this.alertSelectorStyle = alertSelectorStyle
        }

        fun alertInputStyle(alertInputStyle: LMFeedEditTextStyle?) = apply {
            this.alertInputStyle = alertInputStyle
        }

        fun alertActivePositiveButtonColor(@ColorRes alertActivePositiveButtonColor: Int) =
            apply {
                this.alertActivePositiveButtonColor = alertActivePositiveButtonColor
            }

        fun alertBoxElevation(@DimenRes alertBoxElevation: Int?) = apply {
            this.alertBoxElevation = alertBoxElevation
        }

        fun alertBoxCornerRadius(@DimenRes alertBoxCornerRadius: Int?) = apply {
            this.alertBoxCornerRadius = alertBoxCornerRadius
        }

        fun backgroundColor(@ColorRes backgroundColor: Int?) = apply {
            this.backgroundColor = backgroundColor
        }

        fun build() = LMFeedAlertDialogViewStyle(
            alertTitleText,
            alertSubtitleText,
            alertNegativeButtonStyle,
            alertPositiveButtonStyle,
            alertSelectorStyle,
            alertInputStyle,
            alertActivePositiveButtonColor,
            alertBoxElevation,
            alertBoxCornerRadius,
            backgroundColor
        )
    }

    fun toBuilder(): Builder {
        return Builder().alertTitleText(alertTitleText)
            .alertSubtitleText(alertSubtitleText)
            .alertNegativeButtonStyle(alertNegativeButtonStyle)
            .alertPositiveButtonStyle(alertPositiveButtonStyle)
            .alertSelectorStyle(alertSelectorStyle)
            .alertInputStyle(alertInputStyle)
            .alertActivePositiveButtonColor(alertActivePositiveButtonColor)
            .alertBoxElevation(alertBoxElevation)
            .alertBoxCornerRadius(alertBoxCornerRadius)
            .alertBoxCornerRadius(alertBoxCornerRadius)
            .backgroundColor(backgroundColor)
    }
}