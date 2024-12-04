package com.likeminds.feed.android.core.post.edit.style

import android.graphics.Typeface
import android.text.TextUtils
import com.likeminds.feed.android.core.*
import com.likeminds.feed.android.core.ui.base.styles.*
import com.likeminds.feed.android.core.ui.theme.LMFeedAppearance
import com.likeminds.feed.android.core.ui.widgets.alertdialog.style.LMFeedAlertDialogViewStyle
import com.likeminds.feed.android.core.ui.widgets.headerview.style.LMFeedHeaderViewStyle
import com.likeminds.feed.android.core.ui.widgets.post.postheaderview.style.LMFeedPostHeaderViewStyle
import com.likeminds.feed.android.core.utils.LMFeedViewStyle
import com.likeminds.feed.android.core.utils.model.LMFeedPadding

/**
 * [LMFeedEditPostFragmentViewStyle] helps you to customize the edit post fragment [LMFeedEditPostFragment]
 *
 * @property headerViewStyle : [LMFeedHeaderViewStyle] this will help you to customize the header view in the edit post fragment
 * @property postHeaderViewStyle : [LMFeedPostHeaderViewStyle] this will help you to customize the post header view in the edit post fragment
 * @property postComposerStyle : [LMFeedEditTextStyle] this will help you to customize the post composer edit text in the edit post fragment
 * @property postHeadingComposerStyle: [LMFeedEditTextStyle] helps to customize the post heading composer in the edit post fragment
 * @property postHeadingLimitTextStyle: [LMFeedTextStyle] helps to customize the post heading limit text view in the edit post fragment
 * @property progressBarStyle: [LMFeedProgressBarStyle] this will help you to customize the progress bar in the edit post fragment
 * @property selectTopicsChipStyle: [LMFeedChipStyle] this will help you to customize the select topics chip in the edit post fragment
 * @property editTopicsChipStyle: [LMFeedChipStyle] this will help you to customize the edit topics chip in the edit post fragment
 * @property disabledTopicsAlertDialogStyle: [LMFeedAlertDialogViewStyle] this will help you to customize the alert dialog shown when user selects disabled topics in the edit post fragment
 * */
class LMFeedEditPostFragmentViewStyle private constructor(
    //header view style
    val headerViewStyle: LMFeedHeaderViewStyle,
    //post header style
    val postHeaderViewStyle: LMFeedPostHeaderViewStyle,
    //post composer view style
    val postComposerStyle: LMFeedEditTextStyle,
    //post heading composer view style
    val postHeadingComposerStyle: LMFeedEditTextStyle?,
    //post heading limit text style
    val postHeadingLimitTextStyle: LMFeedTextStyle?,
    //progress bar view style
    val progressBarStyle: LMFeedProgressBarStyle,
    //select topics chip style
    val selectTopicsChipStyle: LMFeedChipStyle,
    //edit chip style
    val editTopicsChipStyle: LMFeedChipStyle,
    //disabled topics alert dialog style
    val disabledTopicsAlertDialogStyle: LMFeedAlertDialogViewStyle
) : LMFeedViewStyle {
    class Builder {
        private var headerViewStyle: LMFeedHeaderViewStyle = LMFeedHeaderViewStyle.Builder()
            .titleTextStyle(
                LMFeedTextStyle.Builder()
                    .textColor(R.color.lm_feed_black)
                    .textSize(R.dimen.lm_feed_header_view_title_text_size)
                    .fontResource(R.font.lm_feed_roboto_medium)
                    .maxLines(1)
                    .ellipsize(TextUtils.TruncateAt.END)
                    .build()
            )
            .backgroundColor(R.color.lm_feed_white)
            .navigationIconStyle(
                LMFeedIconStyle.Builder()
                    .iconTint(R.color.lm_feed_black)
                    .inActiveSrc(R.drawable.lm_feed_ic_arrow_back_black_24dp)
                    .iconPadding(
                        LMFeedPadding(
                            R.dimen.lm_feed_icon_padding,
                            R.dimen.lm_feed_icon_padding,
                            R.dimen.lm_feed_icon_padding,
                            R.dimen.lm_feed_icon_padding
                        )
                    )
                    .build()
            )
            .submitTextStyle(
                LMFeedTextStyle.Builder()
                    .textColor(R.color.lm_feed_grey)
                    .textSize(R.dimen.lm_feed_text_large)
                    .fontResource(R.font.lm_feed_roboto_medium)
                    .build()
            )
            .activeSubmitColor(LMFeedAppearance.getButtonColor())
            .build()

        private var postHeaderViewStyle: LMFeedPostHeaderViewStyle =
            LMFeedPostHeaderViewStyle.Builder()
                .authorImageViewStyle(
                    LMFeedImageStyle.Builder()
                        .isCircle(true)
                        .build()
                )
                .authorNameViewStyle(
                    LMFeedTextStyle.Builder()
                        .textColor(R.color.lm_feed_raisin_black)
                        .textSize(R.dimen.lm_feed_text_large)
                        .maxLines(1)
                        .fontResource(R.font.lm_feed_roboto_medium)
                        .ellipsize(TextUtils.TruncateAt.END)
                        .build()
                )
                .build()

        private var postComposerStyle: LMFeedEditTextStyle = LMFeedEditTextStyle.Builder()
            .inputTextStyle(
                LMFeedTextStyle.Builder()
                    .ellipsize(TextUtils.TruncateAt.END)
                    .maxHeight(R.dimen.lm_feed_post_composer_max_height)
                    .minHeight(R.dimen.lm_feed_post_composer_min_height)
                    .textColor(R.color.lm_feed_dark_grey)
                    .textSize(R.dimen.lm_feed_text_large)
                    .build()
            )
            .hintTextColor(R.color.lm_feed_maastricht_blue_40)
            .build()

        private var postHeadingComposerStyle: LMFeedEditTextStyle? =
            if (LMFeedCoreApplication.selectedTheme == LMFeedTheme.QNA_FEED) {
                LMFeedEditTextStyle.Builder()
                    .inputTextStyle(
                        LMFeedTextStyle.Builder()
                            .ellipsize(TextUtils.TruncateAt.END)
                            .maxHeight(R.dimen.lm_feed_post_heading_composer_max_height)
                            .minHeight(R.dimen.lm_feed_post_heading_composer_min_height)
                            .textColor(R.color.lm_feed_dark_grey)
                            .fontResource(R.font.lm_feed_roboto_medium)
                            .textSize(R.dimen.lm_feed_text_large)
                            .build()
                    )
                    .hintTextColor(R.color.lm_feed_maastricht_blue_40)
                    .build()
            } else {
                null
            }

        private var postHeadingLimitTextStyle: LMFeedTextStyle? =
            if (LMFeedCoreApplication.selectedTheme == LMFeedTheme.QNA_FEED) {
                LMFeedTextStyle.Builder()
                    .textColor(R.color.lm_feed_dark_grey_70)
                    .textSize(R.dimen.lm_feed_text_small)
                    .fontResource(R.font.lm_feed_roboto)
                    .build()
            } else {
                null
            }

        private var selectTopicsChipStyle: LMFeedChipStyle = LMFeedChipStyle.Builder()
            .chipBackgroundColor(R.color.lm_feed_majorelle_blue_10)
            .chipStartPadding(R.dimen.lm_feed_regular_padding)
            .chipIcon(R.drawable.lm_feed_ic_add_topics)
            .chipIconSize(R.dimen.lm_feed_chip_default_icon_size)
            .chipIconTint(LMFeedAppearance.getButtonColor())
            .build()

        private var editTopicsChipStyle: LMFeedChipStyle = LMFeedChipStyle.Builder()
            .chipBackgroundColor(R.color.lm_feed_majorelle_blue_10)
            .chipEndPadding(R.dimen.lm_feed_edit_chip_end_size)
            .chipStartPadding(R.dimen.lm_feed_edit_chip_end_size)
            .chipIcon(R.drawable.lm_feed_ic_edit_topic)
            .chipIconSize(R.dimen.lm_feed_chip_default_icon_size)
            .chipIconTint(LMFeedAppearance.getButtonColor())
            .build()

        private var progressBarStyle: LMFeedProgressBarStyle = LMFeedProgressBarStyle.Builder()
            .isIndeterminate(false)
            .progressColor(LMFeedAppearance.getButtonColor())
            .build()

        private var disabledTopicsAlertDialogStyle: LMFeedAlertDialogViewStyle =
            LMFeedAlertDialogViewStyle.Builder()
                .alertSubtitleText(
                    LMFeedTextStyle.Builder()
                        .textColor(R.color.lm_feed_grey)
                        .textSize(R.dimen.lm_feed_text_medium)
                        .typeface(Typeface.NORMAL)
                        .fontResource(R.font.lm_feed_roboto)
                        .build()
                )
                .alertPositiveButtonStyle(
                    LMFeedTextStyle.Builder()
                        .textAllCaps(true)
                        .textColor(LMFeedAppearance.getButtonColor())
                        .textSize(R.dimen.lm_feed_text_small)
                        .typeface(Typeface.NORMAL)
                        .fontResource(R.font.lm_feed_roboto_medium)
                        .build()
                )
                .build()

        fun headerViewStyle(headerViewStyle: LMFeedHeaderViewStyle) = apply {
            this.headerViewStyle = headerViewStyle
        }

        fun postHeaderViewStyle(postHeaderViewStyle: LMFeedPostHeaderViewStyle) = apply {
            this.postHeaderViewStyle = postHeaderViewStyle
        }

        fun postComposerStyle(postComposerStyle: LMFeedEditTextStyle) = apply {
            this.postComposerStyle = postComposerStyle
        }

        fun postHeadingComposerStyle(postHeadingComposerStyle: LMFeedEditTextStyle?) = apply {
            this.postHeadingComposerStyle = postHeadingComposerStyle
        }

        fun postHeadingLimitTextStyle(postHeadingLimitTextStyle: LMFeedTextStyle?) = apply {
            this.postHeadingLimitTextStyle = postHeadingLimitTextStyle
        }

        fun progressBarStyle(progressBarStyle: LMFeedProgressBarStyle) = apply {
            this.progressBarStyle = progressBarStyle
        }

        fun selectTopicsChipStyle(selectTopicsChipStyle: LMFeedChipStyle) = apply {
            this.selectTopicsChipStyle = selectTopicsChipStyle
        }

        fun editTopicsChipStyle(editTopicsChipStyle: LMFeedChipStyle) = apply {
            this.editTopicsChipStyle = editTopicsChipStyle
        }

        fun disabledTopicsAlertDialogStyle(disabledTopicsAlertDialogStyle: LMFeedAlertDialogViewStyle) =
            apply {
                this.disabledTopicsAlertDialogStyle = disabledTopicsAlertDialogStyle
            }

        fun build() = LMFeedEditPostFragmentViewStyle(
            headerViewStyle,
            postHeaderViewStyle,
            postComposerStyle,
            postHeadingComposerStyle,
            postHeadingLimitTextStyle,
            progressBarStyle,
            selectTopicsChipStyle,
            editTopicsChipStyle,
            disabledTopicsAlertDialogStyle
        )
    }

    fun toBuilder(): Builder {
        return Builder().headerViewStyle(headerViewStyle)
            .postHeaderViewStyle(postHeaderViewStyle)
            .postComposerStyle(postComposerStyle)
            .postHeadingComposerStyle(postHeadingComposerStyle)
            .postHeadingLimitTextStyle(postHeadingLimitTextStyle)
            .progressBarStyle(progressBarStyle)
            .selectTopicsChipStyle(selectTopicsChipStyle)
            .editTopicsChipStyle(editTopicsChipStyle)
            .disabledTopicsAlertDialogStyle(disabledTopicsAlertDialogStyle)
    }
}