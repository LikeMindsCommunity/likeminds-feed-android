package com.likeminds.feed.android.core.poll.result.style

import android.graphics.Typeface
import android.text.TextUtils
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.ui.base.styles.*
import com.likeminds.feed.android.core.ui.theme.LMFeedAppearance
import com.likeminds.feed.android.core.ui.widgets.headerview.style.LMFeedHeaderViewStyle
import com.likeminds.feed.android.core.ui.widgets.noentitylayout.style.LMFeedNoEntityLayoutViewStyle
import com.likeminds.feed.android.core.ui.widgets.user.style.LMFeedUserViewStyle
import com.likeminds.feed.android.core.utils.LMFeedViewStyle
import com.likeminds.feed.android.core.utils.model.LMFeedPadding

/**
 * [LMFeedPollResultsFragmentViewStyle] helps you to customize the poll results fragment [LMFeedPollResultsFragment]
 * @property headerViewStyle : [LMFeedHeaderViewStyle] this will help you to customize the header view in the poll results fragment
 * @property pollResultsTabElevation: [Int] should be in format of [DimenRes] this will help you to customize the elevation of the poll results tab layout | Default value =  [R.dimen.lm_feed_elevation_extra_small]
 * @property noResultsLayoutViewStyle : [LMFeedNoEntityLayoutViewStyle] this will help you to customize the no poll results layout in the poll results fragment
 * @property selectedPollResultsTabColor : [Int] should be in format of [ColorRes] this will help you to customize the selected poll results tab color in the poll results fragment | Default value = [LMFeedAppearance.getButtonColor]
 * @property unselectedPollResultsTabColor :  [Int] should be in format of [ColorRes] this will help you to customize the unselected poll results tab color in the poll results fragment | Default value = [R.color.lm_feed_grey]
 * @property pollResultsTabTextViewStyle :  [LMFeedTextStyle] this will help you to customize the text view of the poll vote results tab in the poll results fragment
 * @property userViewStyle: [LMFeedUserViewStyle] this will help you to customize the user view in the poll results fragment
 * @property backgroundColor: [Int] should be in format of [ColorRes] this will help you to customize the background color of the poll results fragment | Default value = [null]
 * */
class LMFeedPollResultsFragmentViewStyle private constructor(
    //header view style
    val headerViewStyle: LMFeedHeaderViewStyle,
    //elevation of poll results tab layout
    @DimenRes val pollResultsTabElevation: Int,
    //no results layout style
    val noResultsLayoutViewStyle: LMFeedNoEntityLayoutViewStyle,
    //color of the selected poll results tab
    @ColorRes val selectedPollResultsTabColor: Int,
    //color of the unselected poll results tab
    @ColorRes val unselectedPollResultsTabColor: Int,
    //style of the poll results tab text view
    val pollResultsTabTextViewStyle: LMFeedTextStyle,
    //user view style
    val userViewStyle: LMFeedUserViewStyle,
    //background color of the screen
    @ColorRes val backgroundColor: Int?
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

        @DimenRes
        private var pollResultsTabElevation: Int = R.dimen.lm_feed_elevation_extra_small

        private var noResultsLayoutViewStyle: LMFeedNoEntityLayoutViewStyle =
            LMFeedNoEntityLayoutViewStyle.Builder()
                .imageStyle(
                    LMFeedImageStyle.Builder()
                        .imageSrc(R.drawable.lm_feed_ic_not_found)
                        .build()
                )
                .titleStyle(
                    LMFeedTextStyle.Builder()
                        .textColor(R.color.lm_feed_dark_grey)
                        .textSize(R.dimen.lm_feed_text_medium)
                        .typeface(Typeface.NORMAL)
                        .fontResource(R.font.lm_feed_roboto_medium)
                        .build()
                )
                .backgroundColor(R.color.lm_feed_white)
                .build()

        @ColorRes
        private var selectedPollResultsTabColor: Int = LMFeedAppearance.getButtonColor()

        @ColorRes
        private var unselectedPollResultsTabColor: Int = R.color.lm_feed_grey

        private var pollResultsTabTextViewStyle: LMFeedTextStyle = LMFeedTextStyle.Builder()
            .textSize(R.dimen.lm_feed_text_large)
            .ellipsize(TextUtils.TruncateAt.END)
            .maxLines(1)
            .fontResource(R.font.lm_feed_roboto_medium)
            .build()

        private var userViewStyle: LMFeedUserViewStyle = LMFeedUserViewStyle.Builder()
            .userTitleViewStyle(
                LMFeedTextStyle.Builder()
                    .ellipsize(TextUtils.TruncateAt.END)
                    .maxLines(1)
                    .textColor(LMFeedAppearance.getButtonColor())
                    .textSize(R.dimen.lm_feed_text_medium)
                    .fontResource(R.font.lm_feed_roboto_medium)
                    .build()
            )
            .build()

        @ColorRes
        private var backgroundColor: Int? = null

        fun headerViewStyle(headerViewStyle: LMFeedHeaderViewStyle) = apply {
            this.headerViewStyle = headerViewStyle
        }

        fun pollResultsTabElevation(@DimenRes pollResultsTabElevation: Int) = apply {
            this.pollResultsTabElevation = pollResultsTabElevation
        }

        fun noResultsLayoutViewStyle(noResultsLayoutViewStyle: LMFeedNoEntityLayoutViewStyle) =
            apply {
                this.noResultsLayoutViewStyle = noResultsLayoutViewStyle
            }

        fun selectedPollResultsTabColor(@ColorRes selectedPollResultsTabColor: Int) = apply {
            this.selectedPollResultsTabColor = selectedPollResultsTabColor
        }

        fun unselectedPollResultsTabColor(@ColorRes unselectedPollResultsTabColor: Int) = apply {
            this.unselectedPollResultsTabColor = unselectedPollResultsTabColor
        }

        fun pollResultsTabTextViewStyle(pollResultsTabTextViewStyle: LMFeedTextStyle) = apply {
            this.pollResultsTabTextViewStyle = pollResultsTabTextViewStyle
        }

        fun userViewStyle(userViewStyle: LMFeedUserViewStyle) = apply {
            this.userViewStyle = userViewStyle
        }

        fun backgroundColor(@ColorRes backgroundColor: Int?) = apply {
            this.backgroundColor = backgroundColor
        }

        fun build() = LMFeedPollResultsFragmentViewStyle(
            headerViewStyle,
            pollResultsTabElevation,
            noResultsLayoutViewStyle,
            selectedPollResultsTabColor,
            unselectedPollResultsTabColor,
            pollResultsTabTextViewStyle,
            userViewStyle,
            backgroundColor
        )
    }

    fun toBuilder(): Builder {
        return Builder()
            .headerViewStyle(headerViewStyle)
            .pollResultsTabElevation(pollResultsTabElevation)
            .noResultsLayoutViewStyle(noResultsLayoutViewStyle)
            .selectedPollResultsTabColor(selectedPollResultsTabColor)
            .unselectedPollResultsTabColor(unselectedPollResultsTabColor)
            .pollResultsTabTextViewStyle(pollResultsTabTextViewStyle)
            .userViewStyle(userViewStyle)
            .backgroundColor(backgroundColor)
    }
}