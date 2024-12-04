package com.likeminds.feed.android.core.ui.base.styles

import android.content.res.ColorStateList
import android.graphics.Typeface
import androidx.annotation.*
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton.IconGravity
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.ui.base.views.LMFeedButton
import com.likeminds.feed.android.core.ui.theme.LMFeedAppearance
import com.likeminds.feed.android.core.utils.LMFeedViewStyle
import kotlin.math.roundToInt

/**
 * [LMFeedButtonStyle] helps you to customize a button in the following way
 *
 * @property textStyle : [LMFeedTextStyle] this will help to customize the text of the button
 *
 * @property backgroundColor: [Int] should be in format of [ColorRes] to add background color of the button | Default value =  [R.color.majorelle_blue]
 * @property strokeColor: [Int] should be in format of [ColorRes] to add border color of the button | Default value = [null]
 * @property strokeWidth: [Int] should be in format of [DimenRes] to add border width of the button | Default value = [null]
 * @property elevation: [Int] should be in format of [DimenRes] to add custom elevation of the button | Default value = [null]
 *
 * @property icon: [Int] should be in format of [DrawableRes] to add custom icon of the button | Default value = [null]
 * @property iconTint:[Int] should be in format of [ColorRes] to change custom icon color | Default value = [null]
 * @property iconSize: [Int] should be in format of [DimenRes] to change custom size of the icon | Default value = [null]
 * @property iconGravity: [Int] should be in format of [IconGravity] to change gravity of the icon | Default value = [ICON_GRAVITY_START]
 * @property iconPadding: [Int] should be in the format of [DimenRes] to change padding of the icon | Default value = [null]
 * @property cornerRadius: [Int] should be in the format of [DimenRes] to change corner radius of the button | Default value = [null]
 * @property disabledButtonColor: [Int] should be in the format of [ColorRes] to change disabled button color | Default value = [R.color.lm_feed_cloudy_blue]
 **/
class LMFeedButtonStyle private constructor(
    //text related
    val textStyle: LMFeedTextStyle,

    //button related
    @ColorRes val backgroundColor: Int,
    @ColorRes val strokeColor: Int?,
    @DimenRes val strokeWidth: Int?,
    @DimenRes val elevation: Int?,

    //icon related
    @DrawableRes val icon: Int?,
    @ColorRes val iconTint: Int?,
    @DimenRes val iconSize: Int?,
    @IconGravity val iconGravity: Int?,
    @DimenRes val iconPadding: Int?,
    @DimenRes val cornerRadius: Int?,
    @ColorRes val disabledButtonColor: Int
) : LMFeedViewStyle {

    class Builder {
        private var textStyle: LMFeedTextStyle = LMFeedTextStyle.Builder()
            .textColor(R.color.lm_feed_white)
            .typeface(Typeface.BOLD)
            .build()

        @ColorRes
        private var backgroundColor: Int = LMFeedAppearance.getButtonColor()

        @ColorRes
        private var strokeColor: Int? = null

        @DimenRes
        private var strokeWidth: Int? = null

        @DimenRes
        private var elevation: Int? = null

        //icon related
        @DrawableRes
        private var icon: Int? = null

        @ColorRes
        private var iconTint: Int? = null

        @DimenRes
        private var iconSize: Int? = null

        @IconGravity
        private var iconGravity: Int? = null

        @DimenRes
        private var iconPadding: Int? = null

        @DimenRes
        private var cornerRadius: Int? = null

        @ColorRes
        private var disabledButtonColor: Int = R.color.lm_feed_cloudy_blue

        fun textStyle(textStyle: LMFeedTextStyle) = apply { this.textStyle = textStyle }

        fun backgroundColor(@ColorRes backgroundColor: Int) =
            apply { this.backgroundColor = backgroundColor }

        fun strokeColor(@ColorRes strokeColor: Int?) = apply { this.strokeColor = strokeColor }

        fun strokeWidth(@DimenRes strokeWidth: Int?) = apply { this.strokeWidth = strokeWidth }

        fun elevation(@DimenRes elevation: Int?) = apply { this.elevation = elevation }

        fun icon(@DrawableRes icon: Int?) = apply { this.icon = icon }

        fun iconTint(@ColorRes iconTint: Int?) = apply { this.iconTint = iconTint }

        fun iconSize(@DimenRes iconSize: Int?) = apply { this.iconSize = iconSize }

        fun iconGravity(@IconGravity iconGravity: Int?) = apply { this.iconGravity = iconGravity }

        fun iconPadding(@DimenRes iconPadding: Int?) = apply { this.iconPadding = iconPadding }

        fun cornerRadius(@DimenRes cornerRadius: Int?) = apply { this.cornerRadius = cornerRadius }

        fun disabledButtonColor(@ColorRes disabledButtonColor: Int) = apply {
            this.disabledButtonColor = disabledButtonColor
        }

        fun build() = LMFeedButtonStyle(
            textStyle,
            backgroundColor,
            strokeColor,
            strokeWidth,
            elevation,
            icon,
            iconTint,
            iconSize,
            iconGravity,
            iconPadding,
            cornerRadius,
            disabledButtonColor
        )
    }

    fun apply(button: LMFeedButton) {
        button.apply {
            //all text related styling
            textStyle.apply(this)

            //button related styling
            val backgroundColor = this@LMFeedButtonStyle.backgroundColor
            backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    context,
                    backgroundColor
                )
            )

            //stroke color
            val strokeColor = this@LMFeedButtonStyle.strokeColor
            strokeColor?.let {
                this.strokeColor = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        context,
                        strokeColor
                    )
                )
            }

            //stroke width
            val strokeWidth = this@LMFeedButtonStyle.strokeWidth
            strokeWidth?.let {
                this.strokeWidth = resources.getDimension(strokeWidth).roundToInt()
            }

            //elevation
            val elevation = this@LMFeedButtonStyle.elevation
            elevation?.let {
                this.elevation = resources.getDimension(elevation)
            }

            //icon related
            val icon = this@LMFeedButtonStyle.icon
            icon?.let {
                this.icon = ContextCompat.getDrawable(context, icon)
            }

            //iconTint
            val iconTint = this@LMFeedButtonStyle.iconTint
            iconTint?.let {
                this.iconTint = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        context,
                        iconTint
                    )
                )
            }

            //iconSize
            val iconSize = this@LMFeedButtonStyle.iconSize
            iconSize?.let {
                this.iconSize = resources.getDimension(iconSize).roundToInt()
            }

            //iconGravity
            val iconGravity = this@LMFeedButtonStyle.iconGravity
            iconGravity?.let {
                this.iconGravity = iconGravity
            }

            //iconPadding
            val iconPadding = this@LMFeedButtonStyle.iconPadding
            iconPadding?.let {
                this.iconPadding = resources.getDimension(iconPadding).roundToInt()
            }

            //corner radius
            val cornerRadius = this@LMFeedButtonStyle.cornerRadius
            cornerRadius?.let {
                this.cornerRadius = resources.getDimension(cornerRadius).toInt()
            }
        }
    }

    fun toBuilder(): Builder {
        return Builder().textStyle(textStyle)
            .backgroundColor(backgroundColor)
            .strokeColor(strokeColor)
            .strokeWidth(strokeWidth)
            .elevation(elevation)
            .icon(icon)
            .iconTint(iconTint)
            .iconSize(iconSize)
            .iconGravity(iconGravity)
            .iconPadding(iconPadding)
            .cornerRadius(cornerRadius)
            .disabledButtonColor(disabledButtonColor)
    }
}

/**
 * Util function to helps to apply all the styling [LMFeedButtonStyle] to [LMFeedButton]
 **/
fun LMFeedButton.setStyle(viewStyle: LMFeedButtonStyle) {
    viewStyle.apply(this)
}