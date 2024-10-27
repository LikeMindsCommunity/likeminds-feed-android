package com.likeminds.feed.android.core.poll.create.style

import android.text.TextUtils
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.ui.base.styles.*
import com.likeminds.feed.android.core.ui.theme.LMFeedAppearance
import com.likeminds.feed.android.core.ui.widgets.headerview.style.LMFeedHeaderViewStyle
import com.likeminds.feed.android.core.ui.widgets.post.postheaderview.style.LMFeedPostHeaderViewStyle
import com.likeminds.feed.android.core.utils.LMFeedViewStyle
import com.likeminds.feed.android.core.utils.model.LMFeedPadding

/**
 * [LMFeedCreatePollFragmentViewStyle] helps to customize the [LMFeedCreatePollFragment] view.
 * @property headerViewStyle [LMFeedHeaderViewStyle] this will help to customize the header view.
 * @property authorViewStyle [LMFeedPostHeaderViewStyle] this will help to customize the author view.
 * @property pollQuestionTitleViewStyle [LMFeedTextStyle] this will help to customize the poll question title view.
 * @property pollQuestionViewStyle [LMFeedEditTextStyle] this will help to customize the poll question view.
 * @property pollOptionsTitleViewStyle [LMFeedTextStyle] this will help to customize the poll options title view.
 * @property pollAddOptionViewStyle [LMFeedTextStyle] this will help to customize the poll add option view.
 * @property pollExpiryTimeTitleViewStyle [LMFeedTextStyle] this will help to customize the poll expiry time title view.
 * @property pollExpiryTimeViewStyle [LMFeedTextStyle] this will help to customize the poll expiry time view.
 * @property pollAdvanceOptionViewStyle [LMFeedTextStyle] this will help to customize the poll advance option view.
 * @property pollAdvanceOptionSwitchViewStyle [LMFeedSwitchStyle] this will help to customize the poll advance option switch view.
 * @property pollOptionsViewStyle [LMFeedCreatePollOptionViewStyle] this will help to customize the poll options view.
 */
class LMFeedCreatePollFragmentViewStyle private constructor(
    //header view style
    val headerViewStyle: LMFeedHeaderViewStyle,
    //author frame view style
    val authorViewStyle: LMFeedPostHeaderViewStyle,
    //poll question title view style
    val pollQuestionTitleViewStyle: LMFeedTextStyle,
    //poll question view style
    val pollQuestionViewStyle: LMFeedEditTextStyle,
    //poll options title view style
    val pollOptionsTitleViewStyle: LMFeedTextStyle,
    //poll options view style
    val pollAddOptionViewStyle: LMFeedTextStyle,
    //poll expiry time title view style
    val pollExpiryTimeTitleViewStyle: LMFeedTextStyle,
    //poll expiry time view style
    val pollExpiryTimeViewStyle: LMFeedTextStyle,
    //poll advance option view style
    val pollAdvanceOptionViewStyle: LMFeedTextStyle,
    //poll advance option switch view style
    val pollAdvanceOptionSwitchViewStyle: LMFeedSwitchStyle,
    //poll options view style
    val pollOptionsViewStyle: LMFeedCreatePollOptionViewStyle,
    //poll dropdown option view style
    val pollDropdownViewStyle: LMFeedTextStyle
) : LMFeedViewStyle {

    class Builder {
        private var headerViewStyle: LMFeedHeaderViewStyle = LMFeedHeaderViewStyle.Builder()
            .titleTextStyle(
                LMFeedTextStyle.Builder()
                    .textColor(R.color.lm_feed_black)
                    .textSize(R.dimen.lm_feed_header_view_title_text_size)
                    .maxLines(1)
                    .fontResource(R.font.lm_feed_roboto_medium)
                    .ellipsize(TextUtils.TruncateAt.END)
                    .build()
            )
            .backgroundColor(R.color.lm_feed_white)
            .submitTextStyle(
                LMFeedTextStyle.Builder()
                    .textColor(R.color.lm_feed_grey)
                    .textSize(R.dimen.lm_feed_text_large)
                    .fontResource(R.font.lm_feed_roboto_medium)
                    .build()
            )
            .activeSubmitColor(LMFeedAppearance.getButtonColor())
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
            .build()

        private var authorViewStyle: LMFeedPostHeaderViewStyle = LMFeedPostHeaderViewStyle.Builder()
            .authorImageViewStyle(
                LMFeedImageStyle.Builder()
                    .isCircle(true)
                    .build()
            )
            .authorNameViewStyle(
                LMFeedTextStyle.Builder()
                    .textColor(R.color.lm_feed_raisin_black)
                    .textSize(R.dimen.lm_feed_text_large)
                    .fontResource(R.font.lm_feed_roboto)
                    .maxLines(1)
                    .ellipsize(TextUtils.TruncateAt.END)
                    .build()
            )
            .build()

        private var pollQuestionTitleViewStyle: LMFeedTextStyle = LMFeedTextStyle.Builder()
            .textColor(LMFeedAppearance.getButtonColor())
            .textSize(R.dimen.lm_feed_text_large)
            .fontResource(R.font.lm_feed_roboto)
            .maxLines(1)
            .build()

        private var pollQuestionViewStyle: LMFeedEditTextStyle = LMFeedEditTextStyle.Builder()
            .hintTextColor(R.color.lm_feed_brown_grey)
            .inputTextStyle(
                LMFeedTextStyle.Builder()
                    .textColor(R.color.lm_feed_dark_grey)
                    .fontResource(R.font.lm_feed_roboto)
                    .textSize(R.dimen.lm_feed_text_large)
                    .build()
            )
            .build()

        private var pollOptionsTitleViewStyle: LMFeedTextStyle = LMFeedTextStyle.Builder()
            .textColor(LMFeedAppearance.getButtonColor())
            .textSize(R.dimen.lm_feed_text_large)
            .fontResource(R.font.lm_feed_roboto)
            .maxLines(1)
            .build()

        private var pollAddOptionViewStyle: LMFeedTextStyle = LMFeedTextStyle.Builder()
            .textColor(LMFeedAppearance.getButtonColor())
            .textSize(R.dimen.lm_feed_text_large)
            .fontResource(R.font.lm_feed_roboto)
            .maxLines(1)
            .drawableLeftSrc(R.drawable.lm_feed_ic_add_option)
            .drawablePadding(R.dimen.lm_feed_drawable_padding)
            .build()

        private var pollExpiryTimeTitleViewStyle: LMFeedTextStyle = LMFeedTextStyle.Builder()
            .textColor(LMFeedAppearance.getButtonColor())
            .textSize(R.dimen.lm_feed_text_large)
            .fontResource(R.font.lm_feed_roboto)
            .maxLines(1)
            .build()

        private var pollExpiryTimeViewStyle: LMFeedTextStyle = LMFeedTextStyle.Builder()
            .hintTextColor(R.color.lm_feed_brown_grey)
            .textColor(R.color.lm_feed_dark_grey)
            .fontResource(R.font.lm_feed_roboto)
            .textSize(R.dimen.lm_feed_text_large)
            .maxLines(1)
            .build()

        private var pollAdvanceOptionViewStyle: LMFeedTextStyle = LMFeedTextStyle.Builder()
            .textColor(R.color.lm_feed_brown_grey)
            .textSize(R.dimen.lm_feed_text_small)
            .textAllCaps(true)
            .drawableRightSrc(R.drawable.lm_feed_ic_arrow_edge_down)
            .maxLines(1)
            .fontResource(R.font.lm_feed_roboto)
            .build()

        private var pollAdvanceOptionSwitchViewStyle: LMFeedSwitchStyle =
            LMFeedSwitchStyle.Builder()
                .textStyle(
                    LMFeedTextStyle.Builder()
                        .maxLines(1)
                        .textAllCaps(false)
                        .textSize(R.dimen.lm_feed_text_large)
                        .textColor(R.color.lm_feed_black)
                        .build()
                )
                .trackColor(LMFeedAppearance.getButtonColor())
                .thumbColor(R.color.lm_feed_white_smoke)
                .build()

        private var pollOptionsViewStyle: LMFeedCreatePollOptionViewStyle =
            LMFeedCreatePollOptionViewStyle.Builder().build()

        private var pollDropdownViewStyle: LMFeedTextStyle = LMFeedTextStyle.Builder()
            .fontResource(R.font.lm_feed_roboto)
            .textSize(R.dimen.lm_feed_text_large)
            .textColor(R.color.lm_feed_black)
            .maxLines(1)
            .build()

        fun headerViewStyle(headerViewStyle: LMFeedHeaderViewStyle): Builder = apply {
            this.headerViewStyle = headerViewStyle
        }

        fun authorViewStyle(authorViewStyle: LMFeedPostHeaderViewStyle): Builder = apply {
            this.authorViewStyle = authorViewStyle
        }

        fun pollQuestionTitleViewStyle(pollQuestionTitleViewStyle: LMFeedTextStyle): Builder =
            apply {
                this.pollQuestionTitleViewStyle = pollQuestionTitleViewStyle
            }

        fun pollQuestionViewStyle(pollQuestionViewStyle: LMFeedEditTextStyle): Builder = apply {
            this.pollQuestionViewStyle = pollQuestionViewStyle
        }

        fun pollOptionsTitleViewStyle(pollOptionsTitleViewStyle: LMFeedTextStyle): Builder = apply {
            this.pollOptionsTitleViewStyle = pollOptionsTitleViewStyle
        }

        fun pollAddOptionViewStyle(pollAddOptionViewStyle: LMFeedTextStyle): Builder = apply {
            this.pollAddOptionViewStyle = pollAddOptionViewStyle
        }

        fun pollExpiryTimeTitleViewStyle(pollExpiryTimeTitleViewStyle: LMFeedTextStyle): Builder =
            apply {
                this.pollExpiryTimeTitleViewStyle = pollExpiryTimeTitleViewStyle
            }

        fun pollExpiryTimeViewStyle(pollExpiryTimeViewStyle: LMFeedTextStyle): Builder = apply {
            this.pollExpiryTimeViewStyle = pollExpiryTimeViewStyle
        }

        fun pollAdvanceOptionViewStyle(pollAdvanceOptionViewStyle: LMFeedTextStyle): Builder =
            apply {
                this.pollAdvanceOptionViewStyle = pollAdvanceOptionViewStyle
            }

        fun pollAdvanceOptionSwitchViewStyle(pollAdvanceOptionSwitchViewStyle: LMFeedSwitchStyle): Builder =
            apply {
                this.pollAdvanceOptionSwitchViewStyle = pollAdvanceOptionSwitchViewStyle
            }

        fun pollOptionsViewStyle(pollOptionsViewStyle: LMFeedCreatePollOptionViewStyle): Builder =
            apply {
                this.pollOptionsViewStyle = pollOptionsViewStyle
            }

        fun pollDropdownViewStyle(pollDropdownViewStyle: LMFeedTextStyle): Builder = apply {
            this.pollDropdownViewStyle = pollDropdownViewStyle
        }

        fun build() = LMFeedCreatePollFragmentViewStyle(
            headerViewStyle,
            authorViewStyle,
            pollQuestionTitleViewStyle,
            pollQuestionViewStyle,
            pollOptionsTitleViewStyle,
            pollAddOptionViewStyle,
            pollExpiryTimeTitleViewStyle,
            pollExpiryTimeViewStyle,
            pollAdvanceOptionViewStyle,
            pollAdvanceOptionSwitchViewStyle,
            pollOptionsViewStyle,
            pollDropdownViewStyle
        )
    }

    fun toBuilder(): Builder {
        return Builder()
            .headerViewStyle(headerViewStyle)
            .authorViewStyle(authorViewStyle)
            .pollQuestionTitleViewStyle(pollQuestionTitleViewStyle)
            .pollQuestionViewStyle(pollQuestionViewStyle)
            .pollOptionsTitleViewStyle(pollOptionsTitleViewStyle)
            .pollAddOptionViewStyle(pollAddOptionViewStyle)
            .pollExpiryTimeTitleViewStyle(pollExpiryTimeTitleViewStyle)
            .pollExpiryTimeViewStyle(pollExpiryTimeViewStyle)
            .pollAdvanceOptionViewStyle(pollAdvanceOptionViewStyle)
            .pollAdvanceOptionSwitchViewStyle(pollAdvanceOptionSwitchViewStyle)
            .pollOptionsViewStyle(pollOptionsViewStyle)
            .pollDropdownViewStyle(pollDropdownViewStyle)
    }
}