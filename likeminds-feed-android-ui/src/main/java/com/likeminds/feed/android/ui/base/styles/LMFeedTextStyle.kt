package com.likeminds.feed.android.ui.base.styles

import android.graphics.Typeface
import android.text.TextUtils.TruncateAt
import android.widget.TextView
import androidx.annotation.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.likeminds.feed.android.ui.R
import com.likeminds.feed.android.ui.base.views.*
import com.likeminds.feed.android.ui.theme.LMFeedTheme
import com.likeminds.feed.android.ui.utils.ViewStyle
import com.likeminds.feed.android.ui.utils.model.LMFeedPadding

/**
 * [LMFeedTextStyle] helps you to customize a [LMFeedTextView] with the following properties
 * @property textColor : [Int] to customize the color of the text
 * @property textSize: [Int] should be in format of [DimenRes] to customize the size of the text | Default value =  [R.dimen.lm_feed_ui_text_small]
 * @property textAllCaps: [Boolean] to customize whether the text should be all caps or not | Default value = [false]
 * @property fontResource: [Int] should be in format of [FontRes] to customize the font of the text using font resources | Default value = [null]
 * @property fontAssetsPath: [String] to to customize the font of the text using the path of the font assets | Default value = [null]
 * @property typeface: [Int] to customize the typeface of the button | Default value = [null]
 * @property maxLines:[Int] to customize the max lines for a text | Default value = [null]
 * @property ellipsize: [TruncateAt] to customize how to ellipsize the text  | Default value = [null]
 * @property padding: [LMFeedPadding] to customize the padding of the text | Default value = [null]
 * @property backgroundColor: [Int] should be in format of [ColorRes] to customize the background color of the text | Default value = [null]
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
    val padding: LMFeedPadding?,
    @ColorRes val backgroundColor: Int?
) : ViewStyle {

    class Builder {
        @ColorRes
        private var textColor: Int = R.color.black
        private var textSize: Int = R.dimen.lm_feed_ui_text_small
        private var textAllCaps: Boolean = false

        @FontRes
        private var fontResource: Int? = null
        private var fontAssetsPath: String? = null
        private var typeface: Int = Typeface.NORMAL
        private var maxLines: Int? = null
        private var ellipsize: TruncateAt? = null
        private var padding: LMFeedPadding? = null

        @ColorRes
        private var backgroundColor: Int? = null

        fun textColor(@ColorRes textColor: Int) = apply {
            this.textColor = textColor
        }

        fun textSize(textSize: Int) = apply {
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

        fun padding(padding: LMFeedPadding?) = apply {
            this.padding = padding
        }

        fun backgroundColor(@ColorRes backgroundColor: Int?) = apply {
            this.backgroundColor = backgroundColor
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
            padding,
            backgroundColor
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
            .padding(padding)
            .backgroundColor(backgroundColor)
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

    private fun applyImpl(textView: TextView) {
        textView.apply {
            val textColor = ContextCompat.getColor(context, this@LMFeedTextStyle.textColor)

            // sets the text color
            this.setTextColor(textColor)

            // sets the text size
            this.textSize = this@LMFeedTextStyle.textSize.toFloat()

            // sets whether the text is all caps or not
            this.isAllCaps = textAllCaps

            // sets max lines to the text view
            if (this@LMFeedTextStyle.maxLines != null) {
                this.maxLines = this@LMFeedTextStyle.maxLines
            }

            // sets ellipsize to the text view
            if (this@LMFeedTextStyle.ellipsize != null) {
                this.ellipsize = this@LMFeedTextStyle.ellipsize
            }

            // sets background color of the text
            if (this@LMFeedTextStyle.backgroundColor != null) {
                val backgroundColor =
                    ContextCompat.getColor(context, this@LMFeedTextStyle.backgroundColor)
                this.setBackgroundColor(backgroundColor)
            }

            // sets font
            setFont(this)

            // sets text padding
            if (padding != null) {
                setPadding(
                    padding.paddingLeft,
                    padding.paddingTop,
                    padding.paddingRight,
                    padding.paddingBottom
                )
            }
        }
    }

    // sets font to the text view
    private fun setFont(textView: TextView) {
        textView.apply {
            val defaultFont = LMFeedTheme.getFontResources()
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
}

/**
 * Util function that helps to apply all the styling [LMFeedTextStyle] to [LMFeedTextView]
 **/
fun LMFeedTextView.setStyle(viewStyle: LMFeedTextStyle) {
    viewStyle.apply(this)
}