package com.likeminds.feed.android.core.ui.base.styles

import android.graphics.Typeface
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.likeminds.feed.android.core.R

import com.likeminds.feed.android.core.ui.base.views.LMFeedEditText
import com.likeminds.feed.android.core.utils.LMFeedViewStyle

class LMFeedEditTextStyle private constructor(
    val inputTextStyle: LMFeedTextStyle,
    @ColorRes val hintTextColor: Int?,
    val inputType: Int?,
    @ColorRes val backgroundColor: Int?
) : LMFeedViewStyle {

    class Builder {
        private var inputTextStyle: LMFeedTextStyle = LMFeedTextStyle.Builder()
            .textColor(R.color.lm_feed_white)
            .typeface(Typeface.NORMAL)
            .build()

        @ColorRes
        private var hintTextColor: Int? = null
        private var inputType: Int? = null

        @ColorRes
        private var backgroundColor: Int? = null

        fun inputTextStyle(inputTextStyle: LMFeedTextStyle) =
            apply { this.inputTextStyle = inputTextStyle }

        fun hintTextColor(@ColorRes hintTextColor: Int?) =
            apply { this.hintTextColor = hintTextColor }

        fun inputType(inputType: Int?) = apply { this.inputType = inputType }
        fun backgroundColor(@ColorRes backgroundColor: Int?) =
            apply { this.backgroundColor = backgroundColor }

        fun build() = LMFeedEditTextStyle(
            inputTextStyle,
            hintTextColor,
            inputType,
            backgroundColor
        )

        fun toBuilder(): Builder {
            return Builder().inputTextStyle(inputTextStyle)
                .hintTextColor(hintTextColor)
                .inputType(inputType)
                .backgroundColor(backgroundColor)
        }
    }

    fun apply(editText: LMFeedEditText) {
        editText.apply {
            inputTextStyle.apply(this)

            if (hintTextColor != null) {
                setHintTextColor(ContextCompat.getColor(this.context, hintTextColor))
            }

            if (this@LMFeedEditTextStyle.inputType != null) {
                inputType = this@LMFeedEditTextStyle.inputType
            }

            if (backgroundColor != null) {
                setBackgroundColor(ContextCompat.getColor(context, backgroundColor))
            }
        }
    }
}

fun LMFeedEditText.setStyle(viewStyle: LMFeedEditTextStyle) {
    viewStyle.apply(this)
}