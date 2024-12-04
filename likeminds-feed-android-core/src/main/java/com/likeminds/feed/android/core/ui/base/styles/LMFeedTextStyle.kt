package com.likeminds.feed.android.core.ui.base.styles

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.text.TextUtils.TruncateAt
import android.util.TypedValue
import android.widget.TextView
import androidx.annotation.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.ui.base.views.*
import com.likeminds.feed.android.core.ui.theme.LMFeedAppearance
import com.likeminds.feed.android.core.utils.LMFeedViewStyle
import kotlin.math.roundToInt

/**
 * [LMFeedTextStyle] helps you to customize a [LMFeedTextView] with the following properties
 * @property textColor : [Int] to customize the color of the text
 * @property textSize: [Int] should be in format of [DimenRes] to customize the size of the text | Default value =  [R.dimen.lm_feed_ui_text_small]
 * @property textAllCaps: [Boolean] to customize whether the text should be all caps or not | Default value = [false]
 * @property fontResource: [Int] should be in format of [FontRes] to customize the font of the text using font resources | Default value = [null]
 * @property fontAssetsPath: [String] to to customize the font of the text using the path of the font assets | Default value = [null]
 * @property typeface: [Int] to customize the typeface of the text | Default value = [null]
 * @property maxLines: [Int] to customize the max lines for a text | Default value = [null]
 * @property ellipsize: [TruncateAt] to customize how to ellipsize the text  | Default value = [null]
 * @property maxHeight: [Int] to customize the maximum height of the text | Default value = [null]
 * @property minHeight: [Int] to customize the minimum height of the text | Default value = [null]
 * @property backgroundColor: [Int] should be in format of [ColorRes] to customize the background color of the text | Default value = [null]
 * @property textAlignment: [Int] to customize the alignment of the text | Default value = [null]
 * @property elevation: [Int] should be in format of [DimenRes] to customize the elevation of the text | Default value = [null]
 * @property hintTextColor: [Int] should be in format of [ColorRes] to customize the hint color of the text | Default value = [null]
 * @property drawableLeftSrc: [Int] should be in format of [DrawableRes] to customize the left drawable of the text | Default value = [null]
 * @property drawableTopSrc: [Int] should be in format of [DrawableRes] to customize the top drawable of the text  | Default value = [null]
 * @property drawableRightSrc: [Int] should be in format of [DrawableRes] to customize the right drawable of the text  | Default value = [null]
 * @property drawableBottomSrc: [Int] should be in format of [DrawableRes] to customize the bottom drawable of the text  | Default value = [null]
 * @property drawablePadding: [Int] should be in format of [DimenRes] to customize the padding of drawable of the text  | Default value = [null]
 *
 **/
class LMFeedTextStyle private constructor(
    @ColorRes val textColor: Int,
    @DimenRes val textSize: Int,
    val textAllCaps: Boolean,
    @FontRes val fontResource: Int?,
    val fontAssetsPath: String?,
    val typeface: Int,
    val maxLines: Int?,
    val ellipsize: TruncateAt?,
    @DimenRes val maxHeight: Int?,
    @DimenRes val minHeight: Int?,
    @ColorRes val backgroundColor: Int?,
    val textAlignment: Int?,
    @DimenRes val elevation: Int?,
    @ColorRes val hintTextColor: Int?,
    @DrawableRes val drawableLeftSrc: Int?,
    @DrawableRes val drawableTopSrc: Int?,
    @DrawableRes val drawableRightSrc: Int?,
    @DrawableRes val drawableBottomSrc: Int?,
    @DimenRes val drawablePadding: Int?,
    val expandableCTAText: String?,
    @ColorRes val expandableCTAColor: Int?
) : LMFeedViewStyle {

    class Builder {
        @ColorRes
        private var textColor: Int = R.color.lm_feed_black

        @DimenRes
        private var textSize: Int = R.dimen.lm_feed_text_small
        private var textAllCaps: Boolean = false

        @FontRes
        private var fontResource: Int? = null
        private var fontAssetsPath: String? = null
        private var typeface: Int = Typeface.NORMAL
        private var maxLines: Int? = null
        private var ellipsize: TruncateAt? = null

        @DimenRes
        private var maxHeight: Int? = null

        @DimenRes
        private var minHeight: Int? = null

        @ColorRes
        private var backgroundColor: Int? = null
        private var textAlignment: Int? = null

        @DimenRes
        private var elevation: Int? = null

        @ColorRes
        private var hintTextColor: Int? = null

        @DrawableRes
        private var drawableLeftSrc: Int? = null

        @DrawableRes
        private var drawableTopSrc: Int? = null

        @DrawableRes
        private var drawableRightSrc: Int? = null

        @DrawableRes
        private var drawableBottomSrc: Int? = null

        @DimenRes
        private var drawablePadding: Int? = null

        private var expandableCTAText: String? = null

        @ColorRes
        private var expandableCTAColor: Int? = null

        fun textColor(@ColorRes textColor: Int) = apply {
            this.textColor = textColor
        }

        fun textSize(@DimenRes textSize: Int) = apply {
            this.textSize = textSize
        }

        fun textAllCaps(textAllCaps: Boolean) = apply {
            this.textAllCaps = textAllCaps
        }

        fun fontResource(@FontRes fontResource: Int?) = apply {
            this.fontResource = fontResource
        }

        fun fontAssetsPath(fontAssetsPath: String?) = apply {
            this.fontAssetsPath = fontAssetsPath
        }

        fun typeface(typeface: Int) = apply {
            this.typeface = typeface
        }

        fun maxLines(maxLines: Int?) = apply {
            this.maxLines = maxLines
        }

        fun ellipsize(ellipsize: TruncateAt?) = apply {
            this.ellipsize = ellipsize
        }

        fun maxHeight(@DimenRes maxHeight: Int?) = apply {
            this.maxHeight = maxHeight
        }

        fun minHeight(@DimenRes minHeight: Int?) = apply {
            this.minHeight = minHeight
        }

        fun backgroundColor(@ColorRes backgroundColor: Int?) = apply {
            this.backgroundColor = backgroundColor
        }

        fun textAlignment(textAlignment: Int?) = apply {
            this.textAlignment = textAlignment
        }

        fun elevation(@DimenRes elevation: Int?) = apply {
            this.elevation = elevation
        }

        fun hintTextColor(@ColorRes hintTextColor: Int?) = apply {
            this.hintTextColor = hintTextColor
        }

        fun drawableLeftSrc(@DrawableRes drawableLeftSrc: Int?) = apply {
            this.drawableLeftSrc = drawableLeftSrc
        }

        fun drawableTopSrc(@DrawableRes drawableTopSrc: Int?) = apply {
            this.drawableTopSrc = drawableTopSrc
        }

        fun drawableRightSrc(@DrawableRes drawableRightSrc: Int?) = apply {
            this.drawableRightSrc = drawableRightSrc
        }

        fun drawableBottomSrc(@DrawableRes drawableBottomSrc: Int?) = apply {
            this.drawableBottomSrc = drawableBottomSrc
        }

        fun drawablePadding(@DimenRes drawablePadding: Int?) = apply {
            this.drawablePadding = drawablePadding
        }

        fun expandableCTAText(expandableCTAText: String?) = apply {
            this.expandableCTAText = expandableCTAText
        }

        fun expandableCTAColor(@ColorRes expandableCTAColor: Int?) = apply {
            this.expandableCTAColor = expandableCTAColor
        }

        fun build() = LMFeedTextStyle(
            textColor,
            textSize,
            textAllCaps,
            fontResource,
            fontAssetsPath,
            typeface,
            maxLines,
            ellipsize,
            maxHeight,
            minHeight,
            backgroundColor,
            textAlignment,
            elevation,
            hintTextColor,
            drawableLeftSrc,
            drawableTopSrc,
            drawableRightSrc,
            drawableBottomSrc,
            drawablePadding,
            expandableCTAText,
            expandableCTAColor
        )
    }

    fun toBuilder(): Builder {
        return Builder().textColor(textColor)
            .textSize(textSize)
            .textAllCaps(textAllCaps)
            .fontResource(fontResource)
            .fontAssetsPath(fontAssetsPath)
            .typeface(typeface)
            .maxLines(maxLines)
            .ellipsize(ellipsize)
            .maxHeight(maxHeight)
            .minHeight(minHeight)
            .backgroundColor(backgroundColor)
            .textAlignment(textAlignment)
            .elevation(elevation)
            .hintTextColor(hintTextColor)
            .drawableLeftSrc(drawableLeftSrc)
            .drawableTopSrc(drawableTopSrc)
            .drawableRightSrc(drawableRightSrc)
            .drawableBottomSrc(drawableRightSrc)
            .drawablePadding(drawablePadding)
            .expandableCTAText(expandableCTAText)
            .expandableCTAColor(expandableCTAColor)
    }

    // applies the [LMFeedTextStyle] to [LMFeedTextView]
    fun apply(lmFeedTextView: LMFeedTextView) {
        applyImpl(lmFeedTextView)
    }

    // applies the [LMFeedTextStyle] to [LMFeedEditText]
    fun apply(lmFeedEditText: LMFeedEditText) {
        applyImpl(lmFeedEditText)
    }

    // applies the [LMFeedTextStyle] to [LMFeedButton]
    fun apply(lmFeedButton: LMFeedButton) {
        applyImpl(lmFeedButton)
    }

    // applies the [LMFeedTextStyle] to [LMFeedFAB]
    fun apply(lmFeedFAB: LMFeedFAB) {
        applyImpl(lmFeedFAB)
    }

    fun apply(lmFeedSwitch: LMFeedSwitch) {
        applyImpl(lmFeedSwitch)
    }

    private fun applyImpl(textView: TextView) {
        textView.apply {
            val textColor = ContextCompat.getColor(context, this@LMFeedTextStyle.textColor)

            // sets the text color
            this.setTextColor(textColor)

            this.textAlignment = textAlignment

            this.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                resources.getDimension(this@LMFeedTextStyle.textSize)
            )

            // sets whether the text is all caps or not
            this.isAllCaps = textAllCaps

            if (this@LMFeedTextStyle.maxLines != null && this@LMFeedTextStyle.ellipsize != null) {
                this.maxLines = this@LMFeedTextStyle.maxLines
                this.ellipsize = this@LMFeedTextStyle.ellipsize
            }

            if (this@LMFeedTextStyle.maxHeight != null) {
                maxHeight = resources.getDimension(this@LMFeedTextStyle.maxHeight).roundToInt()
            }

            if (this@LMFeedTextStyle.minHeight != null) {
                minHeight = resources.getDimension(this@LMFeedTextStyle.minHeight).roundToInt()
            }

            // sets background color of the text
            if (this@LMFeedTextStyle.backgroundColor != null) {
                val backgroundColor =
                    ContextCompat.getColor(context, this@LMFeedTextStyle.backgroundColor)
                this.backgroundTintList = ColorStateList.valueOf(backgroundColor)
            }

            if (this@LMFeedTextStyle.elevation != null) {
                elevation = resources.getDimension(this@LMFeedTextStyle.elevation)
            }

            if (this@LMFeedTextStyle.textAlignment != null) {
                textAlignment = this@LMFeedTextStyle.textAlignment
            }

            //sets the text link color
            this.setLinkTextColor(ContextCompat.getColor(context, LMFeedAppearance.getTextLinkColor()))

            this@LMFeedTextStyle.hintTextColor?.let {
                setHintTextColor(
                    ContextCompat.getColor(
                        context,
                        this@LMFeedTextStyle.hintTextColor
                    )
                )
            }

            // sets drawable to the text view
            setCompoundDrawablesWithIntrinsicBounds(
                drawableLeftSrc ?: 0,
                drawableTopSrc ?: 0,
                drawableRightSrc ?: 0,
                drawableBottomSrc ?: 0
            )

            // sets drawable padding
            if (drawablePadding != null) {
                compoundDrawablePadding = resources.getDimensionPixelOffset(drawablePadding)
            }

            setFont(this)
        }
    }

    //sets font to the text view
    private fun setFont(textView: TextView) {
        textView.apply {
            val defaultFont = LMFeedAppearance.getFontResources()
            when {
                fontResource != null -> {
                    val font = ResourcesCompat.getFont(context, fontResource)
                    setTypeface(font, this@LMFeedTextStyle.typeface)
                }

                fontAssetsPath != null -> {
                    val font = Typeface.createFromAsset(context.assets, fontAssetsPath)
                    setTypeface(font, this@LMFeedTextStyle.typeface)
                }

                defaultFont.first != null -> {
                    val font = ResourcesCompat.getFont(context, (defaultFont.first ?: 0))
                    setTypeface(font, this@LMFeedTextStyle.typeface)
                }

                else -> {
                    val font = Typeface.createFromAsset(context.assets, defaultFont.second)
                    setTypeface(font, this@LMFeedTextStyle.typeface)
                }
            }
        }
    }

    override fun toString(): String {
        return """
            textSize: $textSize
            textAllCaps: $textAllCaps
            textColor: $textColor
            fontResource: $fontResource
        """.trimIndent()
    }
}

/**
 * Util function that helps to apply all the styling [LMFeedTextStyle] to [LMFeedTextView]
 **/
fun LMFeedTextView.setStyle(viewStyle: LMFeedTextStyle) {
    viewStyle.apply(this)
}