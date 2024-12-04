package com.likeminds.feed.android.core.qnafeed.style

import android.text.TextUtils
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.socialfeed.style.LMFeedPostingViewStyle
import com.likeminds.feed.android.core.socialfeed.style.LMFeedTopicSelectorBarViewStyle
import com.likeminds.feed.android.core.ui.base.styles.*
import com.likeminds.feed.android.core.ui.theme.LMFeedAppearance
import com.likeminds.feed.android.core.ui.widgets.headerview.style.LMFeedHeaderViewStyle
import com.likeminds.feed.android.core.ui.widgets.noentitylayout.style.LMFeedNoEntityLayoutViewStyle
import com.likeminds.feed.android.core.utils.LMFeedViewStyle
import com.likeminds.feed.android.core.utils.model.LMFeedPadding

/**
 * [LMFeedQnAFeedFragmentViewStyle] helps you to customize the qna feed fragment [LMFeedQnAFeedFragment]
 *
 * @property headerViewStyle : [LMFeedHeaderViewStyle] this will help you to customize the header view in the qna feed fragment
 * @property createNewPostButtonViewStyle : [LMFeedFABStyle] this will help you to customize the create new post fab in the qna feed fragment
 * @property noPostLayoutViewStyle: [LMFeedNoEntityLayoutViewStyle] this will help you to customize the no post layout in the qna feed fragment
 * @property postingViewStyle: [LMFeedPostingViewStyle] this will help you to customize the posting progress view in the qna feed fragment
 * @property topicSelectorBarStyle: [LMFeedTopicSelectorBarViewStyle] this will help you to customize the topic selector bar in the qna feed fragment
 * */
class LMFeedQnAFeedFragmentViewStyle private constructor(
    //header
    val headerViewStyle: LMFeedHeaderViewStyle,
    //create post button
    val createNewPostButtonViewStyle: LMFeedFABStyle,
    //no post layout
    val noPostLayoutViewStyle: LMFeedNoEntityLayoutViewStyle,
    //posting view
    val postingViewStyle: LMFeedPostingViewStyle,
    //topic selector bar view
    val topicSelectorBarStyle: LMFeedTopicSelectorBarViewStyle
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
            .searchIconStyle(
                LMFeedIconStyle.Builder()
                    .inActiveSrc(R.drawable.lm_feed_ic_search_icon)
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
            .build()

        private var createNewPostButtonViewStyle = LMFeedFABStyle.Builder()
            .isExtended(false)
            .backgroundColor(LMFeedAppearance.getButtonColor())
            .icon(R.drawable.lm_feed_ic_new_post_plus)
            .iconTint(R.color.lm_feed_white)
            .iconSize(R.dimen.lm_feed_create_new_post_icon_size)
            .textStyle(
                LMFeedTextStyle.Builder()
                    .textColor(R.color.lm_feed_white)
                    .textAllCaps(true)
                    .fontResource(R.font.lm_feed_roboto_medium)
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

        private var topicSelectorBarStyle: LMFeedTopicSelectorBarViewStyle =
            LMFeedTopicSelectorBarViewStyle.Builder()
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

        fun topicSelectorBarStyle(topicSelectorBarStyle: LMFeedTopicSelectorBarViewStyle) =
            apply {
                this.topicSelectorBarStyle = topicSelectorBarStyle
            }

        fun build() =
            LMFeedQnAFeedFragmentViewStyle(
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