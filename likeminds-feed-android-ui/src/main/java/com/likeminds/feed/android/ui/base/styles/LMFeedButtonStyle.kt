package com.likeminds.feed.android.ui.base.styles

import android.content.res.ColorStateList
import android.graphics.Typeface
import androidx.annotation.*
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton.IconGravity
import com.likeminds.feed.android.ui.R
import com.likeminds.feed.android.ui.base.views.LMFeedButton
import kotlin.math.roundToInt

/**
 * [LMFeedButtonStyle] helps you to customize a [LMFeedButton] with the following properties
 * @property textStyle : [LMFeedTextStyle] to customize the text of the button
 *
 * @property backgroundColor: [Int] should be in format of [ColorRes] to customize the background color of the button | Default value =  [R.color.majorelle_blue]
 * @property strokeColor: [Int] should be in format of [ColorRes] to customize the border color of the button | Default value = [null]
 * @property strokeWidth: [Int] should be in format of [DimenRes] to customize the border width of the button | Default value = [null]
 * @property elevation: [Int] should be in format of [DimenRes] to customize the elevation of the button | Default value = [null]
 *
 *
 * @property icon: [Int] should be in format of [DrawableRes] to customize the icon of the button | Default value = [null]
 * @property iconTint:[Int] should be in format of [ColorRes] to customize the icon color | Default value = [null]
 * @property iconSize: [Int] should be in format of [DimenRes] to customize the size of the icon | Default value = [null]
 * @property iconGravity: [Int] should be in format of [IconGravity] to customize the gravity of the icon | Default value = [ICON_GRAVITY_START]
 * @property iconPadding: [Int] should be in the format of [DimenRes] to customize the padding of the icon | Default value = [null]
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
    @DimenRes val iconPadding: Int?
) {

    class Builder {
        private var textStyle: LMFeedTextStyle = LMFeedTextStyle.Builder()
            .textColor(R.color.white)
            .typeface(Typeface.BOLD)
            .build()

        @ColorRes
        private var backgroundColor: Int = R.color.majorelle_blue

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

        fun textStyle(textStyle: LMFeedTextStyle) = apply { this.textStyle = textStyle }

        fun backgroundColor(@ColorRes backgroundColor: Int) =
            apply { this.backgroundColor = backgroundColor }

        fun strokeColor(@ColorRes strokeColor: Int?) = apply { this.strokeColor = strokeColor }

        fun strokeWidth(@DimenRes strokeWidth: Int?) = apply { this.strokeWidth = strokeWidth }

        fun elevation(@DimenRes elevation: Int?) = apply { this.elevation = elevation }

        fun icon(icon: Int?) = apply { this.icon = icon }

        fun iconTint(iconTint: Int?) = apply { this.iconTint = iconTint }

        fun iconSize(iconSize: Int?) = apply { this.iconSize = iconSize }

        fun iconGravity(iconGravity: Int?) = apply { this.iconGravity = iconGravity }

        fun iconPadding(iconPadding: Int?) = apply { this.iconPadding = iconPadding }

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
            iconPadding
        )
    }

    fun apply(button: LMFeedButton) {
        button.apply {
            // applies all text related styling
            textStyle.apply(this)

            //button related styling
            backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    context,
                    this@LMFeedButtonStyle.backgroundColor
                )
            )

            //stroke color
            val strokeColor = this@LMFeedButtonStyle.strokeColor
            if (strokeColor != null) {
                this.strokeColor = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        context,
                        strokeColor
                    )
                )
            }

            //stroke width
            val strokeWidth = this@LMFeedButtonStyle.strokeWidth
            if (strokeWidth != null) {
                this.strokeWidth = resources.getDimension(strokeWidth).roundToInt()
            }

            //elevation
            val elevation = this@LMFeedButtonStyle.elevation
            if (elevation != null) {
                this.elevation = resources.getDimension(elevation)
            }

            //icon related
            val icon = this@LMFeedButtonStyle.icon
            if (icon != null) {
                this.icon = ContextCompat.getDrawable(context, icon)
            }

            //iconTint
            val iconTint = this@LMFeedButtonStyle.iconTint
            if (iconTint != null) {
                this.iconTint = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        context,
                        iconTint
                    )
                )
            }

            //iconSize
            val iconSize = this@LMFeedButtonStyle.iconSize
            if (iconSize != null) {
                this.iconSize = resources.getDimension(iconSize).roundToInt()
            }

            //iconGravity
            val iconGravity = this@LMFeedButtonStyle.iconGravity
            if (iconGravity != null) {
                this.iconGravity = iconGravity
            }

            //iconPadding
            val iconPadding = this@LMFeedButtonStyle.iconPadding
            if (iconPadding != null) {
                this.iconPadding = resources.getDimension(iconPadding).roundToInt()
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
    }
}

/**
* Util function that helps to apply all the styling [LMFeedButtonStyle] to [LMFeedButton]
 **/
fun LMFeedButton.setStyle(viewStyle: LMFeedButtonStyle) {
    viewStyle.apply(this)
}