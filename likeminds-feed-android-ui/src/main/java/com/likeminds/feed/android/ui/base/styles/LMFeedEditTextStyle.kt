package com.likeminds.feed.android.ui.base.styles

import android.graphics.Typeface
import android.text.TextUtils.TruncateAt
import androidx.annotation.*
import androidx.core.content.ContextCompat
import com.likeminds.feed.android.ui.R
import com.likeminds.feed.android.ui.base.views.*
import com.likeminds.feed.android.ui.utils.ViewStyle
import com.likeminds.feed.android.ui.utils.model.LMFeedPadding

/**
 * [LMFeedEditTextStyle] helps you to customize a [LMFeedEditText] with the following properties
 * @property inputTextStyle : [LMFeedTextStyle] to customize the text of the edit text
 *
 * @property hintTextColor: [Int] should be in format of [ColorRes] to customize the hint text color | Default value = [null]
 * @property inputType: [Int] to customize the input type of edit text | Default value = [null]
 **/
class LMFeedEditTextStyle private constructor(
    val inputTextStyle: LMFeedTextStyle,
    @ColorRes
    val hintTextColor: Int?,
    val inputType: Int?
) : ViewStyle {

    class Builder {
        private var inputTextStyle: LMFeedTextStyle = LMFeedTextStyle.Builder()
            .textColor(R.color.white)
            .typeface(Typeface.NORMAL)
            .build()

        @ColorRes
        private var hintTextColor: Int? = null
        private var inputType: Int? = null

        fun inputTextStyle(inputTextStyle: LMFeedTextStyle) =
            apply { this.inputTextStyle = inputTextStyle }

        fun hintTextColor(@ColorRes hintTextColor: Int?) =
            apply { this.hintTextColor = hintTextColor }

        fun inputType(inputType: Int?) = apply { this.inputType = inputType }

        fun build() = LMFeedEditTextStyle(
            inputTextStyle,
            hintTextColor,
            inputType
        )

        fun toBuilder(): Builder {
            return Builder().inputTextStyle(inputTextStyle)
                .hintTextColor(hintTextColor)
                .inputType(inputType)
        }
    }

    fun apply(editText: LMFeedEditText) {
        editText.apply {
            // applies all text related styling
            inputTextStyle.apply(this)

            // sets hint text color
            if (hintTextColor != null) {
                setHintTextColor(ContextCompat.getColor(this.context, hintTextColor))
            }

            // sets input type of edit text
            if (this@LMFeedEditTextStyle.inputType != null) {
                inputType = this@LMFeedEditTextStyle.inputType
            }
        }
    }
}

/**
 * Util function that helps to apply all the styling [LMFeedEditTextStyle] to [LMFeedEditText]
 **/
fun LMFeedEditText.setStyle(viewStyle: LMFeedEditTextStyle) {
    viewStyle.apply(this)
}