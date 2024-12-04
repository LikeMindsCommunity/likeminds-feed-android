package com.likeminds.feed.android.core.topicselection.style

import android.graphics.Typeface
import android.text.TextUtils
import androidx.annotation.ColorRes
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.ui.base.styles.*
import com.likeminds.feed.android.core.ui.theme.LMFeedAppearance
import com.likeminds.feed.android.core.ui.widgets.headerview.style.LMFeedHeaderViewStyle
import com.likeminds.feed.android.core.ui.widgets.noentitylayout.style.LMFeedNoEntityLayoutViewStyle
import com.likeminds.feed.android.core.ui.widgets.searchbar.style.LMFeedSearchBarViewStyle
import com.likeminds.feed.android.core.utils.LMFeedViewStyle
import com.likeminds.feed.android.core.utils.model.LMFeedPadding

/**
 * [LMFeedTopicSelectionFragmentViewStyle] helps you to customize the likes fragment [LMFeedTopicSelectionFragment]
 *
 * @property headerViewStyle : [LMFeedHeaderViewStyle] this will help you to customize the header view in the topic selection fragment
 * @property topicItemStyle : [LMFeedTextStyle] this will help you to customize the topic item in the topic selection fragment
 * @property noTopicsLayoutViewStyle : [LMFeedNoEntityLayoutViewStyle] this will help you to customize the no topics layout in the topic selection fragment
 * @property submitSelectedTopicsFABStyle : [LMFeedFABStyle] this will help you to customize the submit selected topics fab in the topic selection fragment
 * @property topicSearchBarViewStyle : [LMFeedSearchBarViewStyle] this will help you to customize the search bar in the topic selection fragment
 * @property backgroundColor: [Int] should be in format of [ColorRes] this will help you to customize the background color of the topic selection fragment | Default value = [null]
 * */
class LMFeedTopicSelectionFragmentViewStyle private constructor(
    //header
    val headerViewStyle: LMFeedHeaderViewStyle,
    //topic item style
    val topicItemStyle: LMFeedTextStyle,
    //no topics layout style
    val noTopicsLayoutViewStyle: LMFeedNoEntityLayoutViewStyle,
    //submit selected topics fab style
    val submitSelectedTopicsFABStyle: LMFeedFABStyle,
    //topic search bar styles
    val topicSearchBarViewStyle: LMFeedSearchBarViewStyle,
    //background color
    @ColorRes val backgroundColor: Int?
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
            .searchIconStyle(
                LMFeedIconStyle.Builder()
                    .iconTint(R.color.lm_feed_black)
                    .inActiveSrc(R.drawable.lm_feed_ic_search_icon)
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

        private var topicItemStyle: LMFeedTextStyle = LMFeedTextStyle.Builder()
            .textSize(R.dimen.lm_feed_text_large)
            .textColor(R.color.lm_feed_dark_grey)
            .drawableRightSrc(R.drawable.lm_feed_ic_topic_selected)
            .build()

        private var noTopicsLayoutViewStyle: LMFeedNoEntityLayoutViewStyle =
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
                .build()

        private var submitSelectedTopicsFABStyle: LMFeedFABStyle = LMFeedFABStyle.Builder()
            .isExtended(false)
            .backgroundColor(LMFeedAppearance.getButtonColor())
            .icon(R.drawable.lm_feed_ic_arrow_right_white)
            .iconTint(R.color.lm_feed_white)
            .build()

        private var topicSearchBarViewStyle: LMFeedSearchBarViewStyle =
            LMFeedSearchBarViewStyle.Builder()
                .searchCloseIconStyle(
                    LMFeedIconStyle.Builder()
                        .inActiveSrc(R.drawable.lm_feed_ic_cross_black)
                        .iconTint(R.color.lm_feed_black)
                        .build()
                )
                .searchBackIconStyle(
                    LMFeedIconStyle.Builder()
                        .inActiveSrc(R.drawable.lm_feed_ic_arrow_back_black_24dp)
                        .build()
                )
                .backgroundColor(R.color.lm_feed_white)
                .elevation(R.dimen.lm_feed_elevation_small)
                .build()

        @ColorRes
        private var backgroundColor: Int? = null

        fun headerViewStyle(headerViewStyle: LMFeedHeaderViewStyle) = apply {
            this.headerViewStyle = headerViewStyle
        }

        fun topicItemStyle(topicItemStyle: LMFeedTextStyle) = apply {
            this.topicItemStyle = topicItemStyle
        }

        fun noTopicsLayoutViewStyle(noTopicsLayoutViewStyle: LMFeedNoEntityLayoutViewStyle) =
            apply {
                this.noTopicsLayoutViewStyle = noTopicsLayoutViewStyle
            }

        fun submitSelectedTopicsFABStyle(submitSelectedTopicsFABStyle: LMFeedFABStyle) = apply {
            this.submitSelectedTopicsFABStyle = submitSelectedTopicsFABStyle
        }

        fun topicSearchBarViewStyle(topicSearchBarViewStyle: LMFeedSearchBarViewStyle) =
            apply {
                this.topicSearchBarViewStyle = topicSearchBarViewStyle
            }

        fun backgroundColor(@ColorRes backgroundColor: Int?) = apply {
            this.backgroundColor = backgroundColor
        }

        fun build() = LMFeedTopicSelectionFragmentViewStyle(
            headerViewStyle,
            topicItemStyle,
            noTopicsLayoutViewStyle,
            submitSelectedTopicsFABStyle,
            topicSearchBarViewStyle,
            backgroundColor
        )
    }

    fun toBuilder(): Builder {
        return Builder().headerViewStyle(headerViewStyle)
            .topicItemStyle(topicItemStyle)
            .noTopicsLayoutViewStyle(noTopicsLayoutViewStyle)
            .submitSelectedTopicsFABStyle(submitSelectedTopicsFABStyle)
            .topicSearchBarViewStyle(topicSearchBarViewStyle)
            .backgroundColor(backgroundColor)
    }
}