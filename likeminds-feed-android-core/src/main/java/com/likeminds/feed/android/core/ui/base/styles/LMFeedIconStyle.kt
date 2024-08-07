package com.likeminds.feed.android.core.ui.base.styles

import android.content.res.ColorStateList
import android.widget.ImageView.ScaleType
import androidx.annotation.*
import androidx.core.content.ContextCompat
import com.likeminds.feed.android.core.ui.base.views.LMFeedIcon
import com.likeminds.feed.android.core.utils.LMFeedViewStyle
import com.likeminds.feed.android.core.utils.model.LMFeedPadding

/**
 * [LMFeedIconStyle] helps you to customize an icon
 *
 * @property activeSrc : [Int] should be in format of [DrawableRes] this will be used as the source for the icon when it is in active state | Default value [null]
 * @property inActiveSrc : [Int] should be in format of [DrawableRes] this will be used as the source for the icon when it is in inactive state | Default value [null]
 * @property iconTint : [Int] should be in format of [ColorRes] this will help to customize the tint of the icon  | Default value [null]
 * @property elevation: [Int] should be in format of [DimenRes] to customize the elevation of the icon | Default value = [null]
 * @property alpha : [Float] this will help to customize the alpha of the icon  | Default value [null]
 * @property scaleType : [ScaleType] this will help to customize the scale type of the image view  | Default value [null]
 * @property iconPadding : [LMFeedPadding] this will help to customize the padding of the icon  | Default value [null]
 * @property backgroundColor: [Int] should be in format of [ColorRes] to customize the background color of the icon | Default value = [null]
 * */
class LMFeedIconStyle private constructor(
    @DrawableRes val activeSrc: Int?,
    @DrawableRes val inActiveSrc: Int?,
    @ColorRes val iconTint: Int?,
    @DimenRes val elevation: Int?,
    val alpha: Float?,
    val scaleType: ScaleType?,
    val iconPadding: LMFeedPadding?,
    @ColorRes val backgroundColor: Int?,
) : LMFeedViewStyle {

    class Builder {
        @DrawableRes
        private var activeSrc: Int? = null

        @DrawableRes
        private var inActiveSrc: Int? = null

        @ColorRes
        private var iconTint: Int? = null

        @DimenRes
        private var elevation: Int? = null
        private var alpha: Float? = null
        private var scaleType: ScaleType? = null
        private var iconPadding: LMFeedPadding? = null

        @ColorRes
        private var backgroundColor: Int? = null

        fun activeSrc(@DrawableRes activeSrc: Int?) = apply {
            this.activeSrc = activeSrc
        }

        fun inActiveSrc(@DrawableRes inActiveSrc: Int?) = apply {
            this.inActiveSrc = inActiveSrc
        }

        fun iconTint(@ColorRes iconTint: Int?) = apply {
            this.iconTint = iconTint
        }

        fun elevation(@DimenRes elevation: Int?) = apply { this.elevation = elevation }
        fun alpha(alpha: Float?) = apply { this.alpha = alpha }
        fun scaleType(scaleType: ScaleType?) = apply { this.scaleType = scaleType }
        fun iconPadding(iconPadding: LMFeedPadding?) = apply { this.iconPadding = iconPadding }

        fun backgroundColor(@ColorRes backgroundColor: Int?) = apply {
            this.backgroundColor = backgroundColor
        }

        fun build() = LMFeedIconStyle(
            activeSrc,
            inActiveSrc,
            iconTint,
            elevation,
            alpha,
            scaleType,
            iconPadding,
            backgroundColor
        )
    }

    fun toBuilder(): Builder {
        return Builder().activeSrc(activeSrc)
            .inActiveSrc(inActiveSrc)
            .iconTint(iconTint)
            .elevation(elevation)
            .alpha(alpha)
            .scaleType(scaleType)
            .iconPadding(iconPadding)
            .backgroundColor(backgroundColor)
    }

    fun apply(icon: LMFeedIcon) {
        icon.apply {
            if (inActiveSrc != null) {
                setImageDrawable(ContextCompat.getDrawable(context, inActiveSrc))
            }

            if (this@LMFeedIconStyle.scaleType != null) {
                this.scaleType = this@LMFeedIconStyle.scaleType
            }

            if (this@LMFeedIconStyle.iconTint != null) {
                val iconTint =
                    ContextCompat.getColorStateList(context, this@LMFeedIconStyle.iconTint)
                imageTintList = iconTint
            }

            if (this@LMFeedIconStyle.elevation != null) {
                this.elevation = resources.getDimension(this@LMFeedIconStyle.elevation)
            }

            if (this@LMFeedIconStyle.alpha != null) {
                alpha = this@LMFeedIconStyle.alpha
            }

            if (this@LMFeedIconStyle.iconPadding != null) {
                val padding = this@LMFeedIconStyle.iconPadding
                setPadding(
                    resources.getDimension(padding.paddingLeft).toInt(),
                    resources.getDimension(padding.paddingTop).toInt(),
                    resources.getDimension(padding.paddingRight).toInt(),
                    resources.getDimension(padding.paddingBottom).toInt()
                )
            }

            if (this@LMFeedIconStyle.backgroundColor != null) {
                val backgroundColor =
                    ContextCompat.getColor(context, this@LMFeedIconStyle.backgroundColor)

                setBackgroundColor(backgroundColor)
            }
        }
    }
}

fun LMFeedIcon.setStyle(viewStyle: LMFeedIconStyle) {
    viewStyle.apply(this)
}