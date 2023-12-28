package com.likeminds.feed.android.ui.base.styles

import android.content.res.ColorStateList
import androidx.annotation.*
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton.IconGravity
import com.likeminds.feed.android.ui.R
import com.likeminds.feed.android.ui.base.views.LMFeedButton
import com.likeminds.feed.android.ui.base.views.LMFeedFAB
import com.likeminds.feed.android.ui.utils.ViewStyle
import kotlin.math.roundToInt

/**
 * [LMFeedFABStyle] helps you to customize a [LMFeedFAB] with the following properties
 * @property textStyle : [LMFeedTextStyle] to customize the text of the FAB
 * @property isExtended : [Boolean] to customize whether the FAB is extended or not
 *
 * @property backgroundColor: [Int] should be in format of [ColorRes] to customize the background color of the FAB | Default value =  [R.color.majorelle_blue]
 * @property strokeColor: [Int] should be in format of [ColorRes] to customize the border color of the FAB | Default value = [null]
 * @property strokeWidth: [Int] should be in format of [DimenRes] to customize the border width of the FAB | Default value = [null]
 * @property elevation: [Int] should be in format of [DimenRes] to customize the elevation of the FAB | Default value = [null]
 *
 *
 * @property icon: [Int] should be in format of [DrawableRes] to customize the icon of the FAB | Default value = [null]
 * @property iconTint:[Int] should be in format of [ColorRes] to customize the icon color | Default value = [null]
 * @property iconSize: [Int] should be in format of [DimenRes] to customize the size of the icon | Default value = [null]
 * @property iconPadding: [Int] should be in the format of [DimenRes] to customize the padding of the icon | Default value = [null]
 **/
class LMFeedFABStyle(
    val textStyle: LMFeedTextStyle?,
    val isExtended: Boolean,

    //fab related
    @ColorRes val backgroundColor: Int,
    @ColorRes val strokeColor: Int?,
    @DimenRes val strokeWidth: Int?,
    @DimenRes val elevation: Int?,

    //icon related
    @DrawableRes val icon: Int?,
    @ColorRes val iconTint: Int?,
    @DimenRes val iconSize: Int?,
    @DimenRes val iconPadding: Int?
) : ViewStyle {

    class Builder {
        private var isExtended: Boolean = false
        private var textStyle: LMFeedTextStyle? = null

        //fab related
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

        @DimenRes
        private var iconPadding: Int? = null

        fun textStyle(textStyle: LMFeedTextStyle?) = apply { this.textStyle = textStyle }
        fun isExtended(isExtended: Boolean) = apply { this.isExtended = isExtended }

        fun backgroundColor(@ColorRes backgroundColor: Int) =
            apply { this.backgroundColor = backgroundColor }

        fun strokeColor(@ColorRes strokeColor: Int?) = apply { this.strokeColor = strokeColor }

        fun strokeWidth(@DimenRes strokeWidth: Int?) = apply { this.strokeWidth = strokeWidth }

        fun elevation(@DimenRes elevation: Int?) = apply { this.elevation = elevation }

        fun icon(icon: Int?) = apply { this.icon = icon }

        fun iconTint(iconTint: Int?) = apply { this.iconTint = iconTint }

        fun iconSize(iconSize: Int?) = apply { this.iconSize = iconSize }

        fun iconPadding(iconPadding: Int?) = apply { this.iconPadding = iconPadding }

        fun build() = LMFeedFABStyle(
            textStyle,
            isExtended,
            backgroundColor,
            strokeColor,
            strokeWidth,
            elevation,
            icon,
            iconTint,
            iconSize,
            iconPadding
        )
    }

    fun apply(fab: LMFeedFAB) {
        fab.apply {
            //text related
            textStyle?.apply(this)

            this.isExtended = this@LMFeedFABStyle.isExtended

            //button related styling
            backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    context,
                    this@LMFeedFABStyle.backgroundColor
                )
            )

            //stroke color
            val strokeColor = this@LMFeedFABStyle.strokeColor
            if (strokeColor != null) {
                this.strokeColor = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        context,
                        strokeColor
                    )
                )
            }

            //stroke width
            val strokeWidth = this@LMFeedFABStyle.strokeWidth
            if (strokeWidth != null) {
                this.strokeWidth = resources.getDimension(strokeWidth).roundToInt()
            }

            //elevation
            val elevation = this@LMFeedFABStyle.elevation
            if (elevation != null) {
                this.elevation = resources.getDimension(elevation)
            }

            //icon related
            val icon = this@LMFeedFABStyle.icon
            if (icon != null) {
                this.icon = ContextCompat.getDrawable(context, icon)
            }

            //iconTint
            val iconTint = this@LMFeedFABStyle.iconTint
            if (iconTint != null) {
                this.iconTint = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        context,
                        iconTint
                    )
                )
            }

            //iconSize
            val iconSize = this@LMFeedFABStyle.iconSize
            if (iconSize != null) {
                this.iconSize = resources.getDimension(iconSize).roundToInt()
            }

            //iconPadding
            val iconPadding = this@LMFeedFABStyle.iconPadding
            if (iconPadding != null) {
                this.iconPadding = resources.getDimension(iconPadding).roundToInt()
            }
        }
    }

    fun toBuilder(): Builder {
        return Builder().isExtended(isExtended)
            .textStyle(textStyle)
            .backgroundColor(backgroundColor)
            .strokeColor(strokeColor)
            .strokeWidth(strokeWidth)
            .elevation(elevation)
            .icon(icon)
            .iconTint(iconTint)
            .iconSize(iconSize)
            .iconPadding(iconPadding)
    }
}

/**
 * Util function that helps to apply all the styling [LMFeedFABStyle] to [LMFeedFAB]
 **/
fun LMFeedFAB.setStyle(viewStyle: LMFeedFABStyle) {
    viewStyle.apply(this)
}