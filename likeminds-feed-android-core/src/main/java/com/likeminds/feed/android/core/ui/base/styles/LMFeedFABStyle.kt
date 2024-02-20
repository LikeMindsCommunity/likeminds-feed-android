package com.likeminds.feed.android.core.ui.base.styles

import android.content.res.ColorStateList
import androidx.annotation.*
import androidx.core.content.ContextCompat
import com.likeminds.feed.android.core.R

import com.likeminds.feed.android.core.ui.base.views.LMFeedFAB
import com.likeminds.feed.android.core.utils.LMFeedViewStyle
import kotlin.math.roundToInt

class LMFeedFABStyle private constructor(
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
    @DimenRes val iconPadding: Int?,

    //text related
    val textStyle: LMFeedTextStyle?
) : LMFeedViewStyle {

    class Builder {
        private var isExtended: Boolean = true

        //fab related
        @ColorRes
        private var backgroundColor: Int = R.color.lm_feed_majorelle_blue

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

        //text related
        private var textStyle: LMFeedTextStyle? = null

        fun isExtended(isExtended: Boolean) = apply { this.isExtended = isExtended }

        fun backgroundColor(@ColorRes backgroundColor: Int) =
            apply { this.backgroundColor = backgroundColor }

        fun strokeColor(@ColorRes strokeColor: Int?) = apply { this.strokeColor = strokeColor }

        fun strokeWidth(@DimenRes strokeWidth: Int?) = apply { this.strokeWidth = strokeWidth }

        fun elevation(@DimenRes elevation: Int?) = apply { this.elevation = elevation }

        fun icon(@DrawableRes icon: Int?) = apply { this.icon = icon }

        fun iconTint(@ColorRes iconTint: Int?) = apply { this.iconTint = iconTint }

        fun iconSize(@DimenRes iconSize: Int?) = apply { this.iconSize = iconSize }

        fun iconPadding(@DimenRes iconPadding: Int?) = apply { this.iconPadding = iconPadding }

        fun textStyle(textStyle: LMFeedTextStyle) = apply { this.textStyle = textStyle }

        fun build() = LMFeedFABStyle(
            isExtended,
            backgroundColor,
            strokeColor,
            strokeWidth,
            elevation,
            icon,
            iconTint,
            iconSize,
            iconPadding,
            textStyle
        )
    }

    fun apply(fab: LMFeedFAB) {
        fab.apply {
            //text related
            textStyle?.apply(this)

            //button related styling
            isExtended = this@LMFeedFABStyle.isExtended

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
            .backgroundColor(backgroundColor)
            .strokeColor(strokeColor)
            .strokeWidth(strokeWidth)
            .elevation(elevation)
            .icon(icon)
            .iconTint(iconTint)
            .iconSize(iconSize)
            .iconPadding(iconPadding)
    }

    override fun toString(): String {
        return """
            isExtended: $isExtended
            backgroundColor: $backgroundColor
            icon: $icon
            iconTint: $iconTint
            iconSize: $iconSize
        """.trimIndent()
    }
}

fun LMFeedFAB.setStyle(viewStyle: LMFeedFABStyle) {
    viewStyle.apply(this)
}