package com.likeminds.feed.android.core.ui.base.styles

import android.content.res.ColorStateList
import android.util.TypedValue
import androidx.annotation.*
import androidx.core.content.ContextCompat
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.ui.base.views.LMFeedChip
import com.likeminds.feed.android.core.ui.theme.LMFeedAppearance
import com.likeminds.feed.android.core.utils.LMFeedViewStyle

/**
 * [LMFeedChipStyle] helps you to customize a [LMFeedChip] with the following properties
 * @property chipBackgroundColor: [Int] should be in format of [ColorRes] to customize the background color of the chip | Default value =  [null]
 * @property chipStrokeColor: [Int] should be in format of [ColorRes] to customize the stroke color of the chip | Default value =  [null]
 * @property chipTextColor: [Int] should be in format of [ColorRes] to customize the text color of the chip | Default value =  [R.color.lm_feed_majorelle_blue]
 * @property chipStrokeWidth: [Int] should be in format of [DimenRes] to customize the width of the stroke on the chip | Default value =  [null]
 * @property chipMinHeight: [Int] should be in format of [DimenRes] to customize the minimum height of the chip | Default value =  [null]
 * @property chipStartPadding: [Int] should be in format of [DimenRes] to customize the start padding of the chip | Default value =  [null]
 * @property chipEndPadding: [Int] should be in format of [DimenRes] to customize the end padding of the chip | Default value =  [null]
 * @property chipCornerRadius: [Int] should be in format of [DimenRes] to customize the corner radius of the chip | Default value =  [R.dimen.lm_feed_corner_radius_regular]
 * @property chipTextSize: [Int] should be in format of [DimenRes] to customize the text size of the chip | Default value = [R.dimen.lm_feed_text_medium]
 * @property chipIcon: [Int] should be in format of [DrawableRes] to customize the icon of the chip | Default value = [null]
 * @property chipIconSize: [Int] should be in format of [DimenRes] to customize the size of the chip icon | Default value = [null]
 * @property chipIconTint: [Int] should be in format of [ColorRes] to customize the color of the chip icon | Default value = [null]
 *
 **/
class LMFeedChipStyle private constructor(
    @ColorRes val chipBackgroundColor: Int?,
    @ColorRes val chipStrokeColor: Int?,
    @ColorRes val chipTextColor: Int,
    @DimenRes val chipStrokeWidth: Int?,
    @DimenRes val chipMinHeight: Int?,
    @DimenRes val chipStartPadding: Int?,
    @DimenRes val chipEndPadding: Int?,
    @DimenRes val chipCornerRadius: Int,
    @DimenRes val chipTextSize: Int,
    @DrawableRes val chipIcon: Int?,
    @DimenRes val chipIconSize: Int?,
    @ColorRes val chipIconTint: Int?
) : LMFeedViewStyle {

    class Builder {
        @ColorRes
        private var chipBackgroundColor: Int? = null

        @ColorRes
        private var chipStrokeColor: Int? = null

        @ColorRes
        private var chipTextColor: Int = LMFeedAppearance.getButtonColor()

        @DimenRes
        private var chipStrokeWidth: Int? = null

        @DimenRes
        private var chipMinHeight: Int? = null

        @DimenRes
        private var chipStartPadding: Int? = null

        @DimenRes
        private var chipEndPadding: Int? = null

        @DimenRes
        private var chipCornerRadius: Int = R.dimen.lm_feed_corner_radius_regular

        @DimenRes
        private var chipTextSize: Int = R.dimen.lm_feed_text_medium

        @DrawableRes
        private var chipIcon: Int? = null

        @DimenRes
        private var chipIconSize: Int? = null

        @ColorRes
        private var chipIconTint: Int? = null

        fun chipBackgroundColor(@ColorRes chipBackgroundColor: Int?) = apply {
            this.chipBackgroundColor = chipBackgroundColor
        }

        fun chipStrokeColor(@ColorRes chipStrokeColor: Int?) = apply {
            this.chipStrokeColor = chipStrokeColor
        }

        fun chipTextColor(@ColorRes chipTextColor: Int) = apply {
            this.chipTextColor = chipTextColor
        }

        fun chipStrokeWidth(@DimenRes chipStrokeWidth: Int?) = apply {
            this.chipStrokeWidth = chipStrokeWidth
        }

        fun chipMinHeight(@DimenRes chipMinHeight: Int?) = apply {
            this.chipMinHeight = chipMinHeight
        }

        fun chipStartPadding(@DimenRes chipStartPadding: Int?) = apply {
            this.chipStartPadding = chipStartPadding
        }

        fun chipEndPadding(@DimenRes chipEndPadding: Int?) = apply {
            this.chipEndPadding = chipEndPadding
        }

        fun chipCornerRadius(@DimenRes chipCornerRadius: Int) = apply {
            this.chipCornerRadius = chipCornerRadius
        }

        fun chipTextSize(@DimenRes chipTextSize: Int) = apply {
            this.chipTextSize = chipTextSize
        }

        fun chipIcon(@DrawableRes chipIcon: Int?) = apply {
            this.chipIcon = chipIcon
        }

        fun chipIconSize(@DimenRes chipIconSize: Int?) = apply {
            this.chipIconSize = chipIconSize
        }

        fun chipIconTint(@ColorRes chipIconTint: Int?) = apply {
            this.chipIconTint = chipIconTint
        }

        fun build() = LMFeedChipStyle(
            chipBackgroundColor,
            chipStrokeColor,
            chipTextColor,
            chipStrokeWidth,
            chipMinHeight,
            chipStartPadding,
            chipEndPadding,
            chipCornerRadius,
            chipTextSize,
            chipIcon,
            chipIconSize,
            chipIconTint
        )
    }

    fun apply(chip: LMFeedChip) {
        chip.apply {
            //chip background color
            this@LMFeedChipStyle.chipBackgroundColor?.let {
                setChipBackgroundColorResource(this@LMFeedChipStyle.chipBackgroundColor)
            }

            //chip stroke width
            if (this@LMFeedChipStyle.chipStrokeWidth == null) {
                setChipStrokeWidthResource(R.dimen.lm_feed_zero_dp)
            } else {
                setChipStrokeWidthResource(this@LMFeedChipStyle.chipStrokeWidth)
            }

            //chip stroke color
            this@LMFeedChipStyle.chipStrokeColor?.let {
                setChipStrokeColorResource(this@LMFeedChipStyle.chipStrokeColor)
            }

            //chip min height
            this@LMFeedChipStyle.chipMinHeight?.let {
                setChipMinHeightResource(it)
            }

            //chip start padding
            this@LMFeedChipStyle.chipStartPadding?.let {
                setChipStartPaddingResource(it)
            }

            //chip end padding
            this@LMFeedChipStyle.chipEndPadding?.let {
                setChipEndPaddingResource(it)
            }

            val shape = this.shapeAppearanceModel.toBuilder()
                .setAllCornerSizes(context.resources.getDimension(this@LMFeedChipStyle.chipCornerRadius))
                .build()

            shapeAppearanceModel = shape

            //chip text size
            setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                context.resources.getDimension(this@LMFeedChipStyle.chipTextSize)
            )

            //chip text color
            setTextColor(
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        context,
                        this@LMFeedChipStyle.chipTextColor
                    )
                )
            )

            this@LMFeedChipStyle.chipIcon?.let {
                setChipIconResource(it)
            }

            this@LMFeedChipStyle.chipIconSize?.let {
                setChipIconSizeResource(it)
            }

            this@LMFeedChipStyle.chipIconTint?.let {
                setChipIconTintResource(it)
            }

            setEnsureMinTouchTargetSize(false)
        }
    }

    fun toBuilder(): Builder {
        return Builder().chipBackgroundColor(chipBackgroundColor)
            .chipTextColor(chipTextColor)
            .chipStrokeColor(chipStrokeColor)
            .chipStrokeWidth(chipStrokeWidth)
            .chipMinHeight(chipMinHeight)
            .chipStartPadding(chipStartPadding)
            .chipEndPadding(chipEndPadding)
            .chipCornerRadius(chipCornerRadius)
            .chipTextSize(chipTextSize)
            .chipIcon(chipIcon)
            .chipIconSize(chipIconSize)
            .chipIconTint(chipIconTint)
    }
}

/**
 * Util function to helps to apply all the styling [LMFeedChipStyle] to [LMFeedChip]
 **/
fun LMFeedChip.setStyle(viewStyle: LMFeedChipStyle) {
    viewStyle.apply(this)
}