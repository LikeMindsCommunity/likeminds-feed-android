package com.likeminds.feed.android.core.ui.widgets.poll.style

import android.text.TextUtils
import androidx.annotation.ColorRes
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.ui.base.styles.*
import com.likeminds.feed.android.core.utils.LMFeedViewStyle
import com.likeminds.feed.android.core.utils.model.LMFeedPadding

/**
 * [LMFeedAddPollOptionBottomSheetFragmentStyle] helps you to customize the post detail fragment [LMFeedAddPollOptionBottomSheetFragment]
 *
 * @property addOptionTitleTextStyle : [LMFeedTextStyle] this will help you to customize the title text in the add poll option bottom sheet
 * @property addOptionSubtitleTextStyle : [LMFeedTextStyle] this will help you to customize the subtitle text in the add poll option bottom sheet
 * @property addOptionInputBoxStyle: [LMFeedEditTextStyle] this will help you to customize the input box in the add poll option bottom sheet
 * @property addOptionSubmitButtonStyle: [LMFeedButtonStyle] this will help you to customize the submit button in the add poll option bottom sheet
 * @property addPollOptionCrossIconStyle: [LMFeedIconStyle] this will help you to the cross icon in the add poll option bottom sheet
 * @property backgroundColor: [Int] should be in format of [ColorRes] to customize the background color of the add option bottom sheet | Default value = null
 * */
class LMFeedAddPollOptionBottomSheetFragmentStyle private constructor(
    //add option title text style
    val addOptionTitleTextStyle: LMFeedTextStyle,
    //add option subtitle text style
    val addOptionSubtitleTextStyle: LMFeedTextStyle,
    //add option input box style
    val addOptionInputBoxStyle: LMFeedEditTextStyle,
    //submit option button style
    val addOptionSubmitButtonStyle: LMFeedButtonStyle,
    //cross add poll option sheet style
    val addPollOptionCrossIconStyle: LMFeedIconStyle,
    //background color of the bottom sheet
    @ColorRes val backgroundColor: Int?
) : LMFeedViewStyle {

    class Builder {
        private var addOptionTitleTextStyle: LMFeedTextStyle = LMFeedTextStyle.Builder()
            .fontResource(R.font.lm_feed_roboto_medium)
            .textSize(R.dimen.lm_feed_text_large)
            .textColor(R.color.lm_feed_black)
            .build()

        private var addOptionSubtitleTextStyle: LMFeedTextStyle = LMFeedTextStyle.Builder()
            .fontResource(R.font.lm_feed_roboto)
            .textSize(R.dimen.lm_feed_text_medium)
            .textColor(R.color.lm_feed_grey)
            .build()

        private var addOptionInputBoxStyle: LMFeedEditTextStyle = LMFeedEditTextStyle.Builder()
            .inputTextStyle(
                LMFeedTextStyle.Builder()
                    .textColor(R.color.lm_feed_dark_grey)
                    .textSize(R.dimen.lm_feed_poll_option_input_text_size)
                    .fontResource(R.font.lm_feed_roboto)
                    .ellipsize(TextUtils.TruncateAt.END)
                    .build()
            )
            .hintTextColor(R.color.lm_feed_dusty_grey)
            .build()

        private var addOptionSubmitButtonStyle: LMFeedButtonStyle = LMFeedButtonStyle.Builder()
            .textStyle(
                LMFeedTextStyle.Builder()
                    .textAllCaps(true)
                    .textSize(R.dimen.lm_feed_text_medium)
                    .fontResource(R.font.lm_feed_roboto_medium)
                    .textColor(R.color.lm_feed_white)
                    .build()
            )
            .cornerRadius(R.dimen.lm_feed_poll_option_submit_button_corner_radius)
            .disabledButtonColor(R.color.lm_feed_cloudy_blue)
            .build()

        private var addPollOptionCrossIconStyle: LMFeedIconStyle = LMFeedIconStyle.Builder()
            .inActiveSrc(R.drawable.lm_feed_ic_cross_black)
            .iconPadding(
                LMFeedPadding(
                    R.dimen.lm_feed_medium_padding,
                    R.dimen.lm_feed_medium_padding,
                    R.dimen.lm_feed_medium_padding,
                    R.dimen.lm_feed_medium_padding
                )
            )
            .build()

        @ColorRes
        private var backgroundColor: Int? = null

        fun addOptionTitleTextStyle(addOptionTitleTextStyle: LMFeedTextStyle) = apply {
            this.addOptionTitleTextStyle = addOptionTitleTextStyle
        }

        fun addOptionSubtitleTextStyle(addOptionSubtitleTextStyle: LMFeedTextStyle) = apply {
            this.addOptionSubtitleTextStyle = addOptionSubtitleTextStyle
        }

        fun addOptionInputBoxStyle(addOptionInputBoxStyle: LMFeedEditTextStyle) = apply {
            this.addOptionInputBoxStyle = addOptionInputBoxStyle
        }

        fun addOptionSubmitButtonStyle(addOptionSubmitButtonStyle: LMFeedButtonStyle) = apply {
            this.addOptionSubmitButtonStyle = addOptionSubmitButtonStyle
        }

        fun addPollOptionCrossIconStyle(addPollOptionCrossIconStyle: LMFeedIconStyle) = apply {
            this.addPollOptionCrossIconStyle = addPollOptionCrossIconStyle
        }

        fun backgroundColor(@ColorRes backgroundColor: Int?) = apply {
            this.backgroundColor = backgroundColor
        }

        fun build() = LMFeedAddPollOptionBottomSheetFragmentStyle(
            addOptionTitleTextStyle,
            addOptionSubtitleTextStyle,
            addOptionInputBoxStyle,
            addOptionSubmitButtonStyle,
            addPollOptionCrossIconStyle,
            backgroundColor
        )
    }

    fun toBuilder(): Builder {
        return Builder().addOptionTitleTextStyle(addOptionTitleTextStyle)
            .addOptionSubtitleTextStyle(addOptionSubtitleTextStyle)
            .addOptionInputBoxStyle(addOptionInputBoxStyle)
            .addOptionSubmitButtonStyle(addOptionSubmitButtonStyle)
            .addPollOptionCrossIconStyle(addPollOptionCrossIconStyle)
            .backgroundColor(backgroundColor)
    }
}