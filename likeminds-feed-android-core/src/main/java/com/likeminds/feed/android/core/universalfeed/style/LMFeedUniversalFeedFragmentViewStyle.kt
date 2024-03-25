package com.likeminds.feed.android.core.universalfeed.style

import android.text.TextUtils
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.ui.base.styles.*
import com.likeminds.feed.android.core.ui.widgets.headerview.style.LMFeedHeaderViewStyle
import com.likeminds.feed.android.core.ui.widgets.noentitylayout.style.LMFeedNoEntityLayoutViewStyle
import com.likeminds.feed.android.core.utils.LMFeedViewStyle
import com.likeminds.feed.android.core.utils.model.LMFeedPadding

/**
 * [LMFeedUniversalFeedFragmentViewStyle] helps you to customize the universal feed fragment [LMFeedUniversalFeedFragment]
 *
 * @property headerViewStyle : [LMFeedHeaderViewStyle] this will help you to customize the header view in the universal feed fragment
 * @property createNewPostButtonViewStyle : [LMFeedFABStyle] this will help you to customize the create new post fab in the universal feed fragment
 * @property noPostLayoutViewStyle: [LMFeedNoEntityLayoutViewStyle] this will help you to customize the no post layout in the universal feed fragment
 * @property postingViewStyle: [LMFeedPostingViewStyle] this will help you to customize the posting progress view in the universal feed fragment
 * @property topicSelectorBarStyle: [LMFeedUniversalTopicSelectorBarViewStyle] this will help you to customize the topic selector bar in the universal feed fragment
 * */
class LMFeedUniversalFeedFragmentViewStyle private constructor(
    //header
    val headerViewStyle: LMFeedHeaderViewStyle,
    //create post button
    val createNewPostButtonViewStyle: LMFeedFABStyle,
    //no post layout
    val noPostLayoutViewStyle: LMFeedNoEntityLayoutViewStyle,
    //posting view
    val postingViewStyle: LMFeedPostingViewStyle,
    //topic selector bar view
    val topicSelectorBarStyle: LMFeedUniversalTopicSelectorBarViewStyle
) : LMFeedViewStyle {

    class Builder {
        private var headerViewStyle: LMFeedHeaderViewStyle = LMFeedHeaderViewStyle.Builder()
            .titleTextStyle(
                LMFeedTextStyle.Builder()
                    .textColor(R.color.lm_feed_black)
                    .textSize(R.dimen.lm_feed_header_view_title_text_size)
                    .maxLines(1)
                    .ellipsize(TextUtils.TruncateAt.END)
                    .build()
            )
            .backgroundColor(R.color.lm_feed_white)
            .userProfileStyle(
                LMFeedImageStyle.Builder()
                    .isCircle(true)
                    .showGreyScale(false)
                    .build()
            )
            .notificationIconStyle(
                LMFeedIconStyle.Builder()
                    .inActiveSrc(R.drawable.lm_feed_ic_notification_bell)
                    .iconTint(R.color.lm_feed_black)
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
            .notificationCountTextStyle(
                LMFeedTextStyle.Builder()
                    .textSize(R.dimen.lm_feed_notification_badge_text_size)
                    .textColor(R.color.lm_feed_white)
                    .build()
            )
            .build()

        private var createNewPostButtonViewStyle = LMFeedFABStyle.Builder()
            .isExtended(false)
            .backgroundColor(R.color.lm_feed_majorelle_blue)
            .icon(R.drawable.lm_feed_ic_new_post_plus)
            .iconTint(R.color.lm_feed_white)
            .iconSize(R.dimen.lm_feed_create_new_post_icon_size)
            .textStyle(
                LMFeedTextStyle.Builder()
                    .textColor(R.color.lm_feed_white)
                    .textAllCaps(true)
                    .build()
            )
            .build()

        private var noPostLayoutViewStyle: LMFeedNoEntityLayoutViewStyle =
            LMFeedNoEntityLayoutViewStyle.Builder()
                .backgroundColor(R.color.lm_feed_white)
                .actionStyle(createNewPostButtonViewStyle)
                .titleStyle(
                    LMFeedTextStyle.Builder()
                        .textSize(R.dimen.lm_feed_text_extra_large)
                        .textColor(R.color.lm_feed_black)
                        .fontResource(R.font.lm_feed_roboto_medium)
                        .textAllCaps(false)
                        .maxLines(1)
                        .build()
                )
                .imageStyle(
                    LMFeedImageStyle.Builder()
                        .isCircle(false)
                        .showGreyScale(false)
                        .imageSrc(R.drawable.lm_feed_ic_post)
                        .alpha(0.6f)
                        .build()
                )
                .subtitleStyle(
                    LMFeedTextStyle.Builder()
                        .textAllCaps(false)
                        .textColor(R.color.lm_feed_black)
                        .textSize(R.dimen.lm_feed_text_small)
                        .textAllCaps(false)
                        .build()
                )
                .build()

        private var postingViewStyle: LMFeedPostingViewStyle = LMFeedPostingViewStyle.Builder()
            .build()

        private var topicSelectorBarStyle: LMFeedUniversalTopicSelectorBarViewStyle =
            LMFeedUniversalTopicSelectorBarViewStyle.Builder()
                .backgroundColor(R.color.lm_feed_white)
                .build()

        fun createNewPostButtonViewStyle(createNewPostButtonViewStyle: LMFeedFABStyle) = apply {
            this.createNewPostButtonViewStyle = createNewPostButtonViewStyle
        }

        fun headerViewStyle(headerViewStyle: LMFeedHeaderViewStyle) = apply {
            this.headerViewStyle = headerViewStyle
        }

        fun noPostLayoutViewStyle(noPostLayoutViewStyle: LMFeedNoEntityLayoutViewStyle) = apply {
            this.noPostLayoutViewStyle = noPostLayoutViewStyle
        }

        fun postingViewStyle(postingViewStyle: LMFeedPostingViewStyle) = apply {
            this.postingViewStyle = postingViewStyle
        }

        fun topicSelectorBarStyle(topicSelectorBarStyle: LMFeedUniversalTopicSelectorBarViewStyle) =
            apply {
                this.topicSelectorBarStyle = topicSelectorBarStyle
            }

        fun build() = LMFeedUniversalFeedFragmentViewStyle(
            headerViewStyle,
            createNewPostButtonViewStyle,
            noPostLayoutViewStyle,
            postingViewStyle,
            topicSelectorBarStyle
        )
    }

    fun toBuilder(): Builder {
        return Builder().headerViewStyle(headerViewStyle)
            .createNewPostButtonViewStyle(createNewPostButtonViewStyle)
            .noPostLayoutViewStyle(noPostLayoutViewStyle)
            .postingViewStyle(postingViewStyle)
            .topicSelectorBarStyle(topicSelectorBarStyle)
    }
}