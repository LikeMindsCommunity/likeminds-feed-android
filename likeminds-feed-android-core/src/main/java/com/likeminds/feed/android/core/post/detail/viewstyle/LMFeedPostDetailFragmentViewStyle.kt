package com.likeminds.feed.android.core.post.detail.viewstyle

import android.text.TextUtils
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.ui.base.styles.*
import com.likeminds.feed.android.core.ui.widgets.comment.commentcomposer.style.LMFeedCommentComposerViewStyle
import com.likeminds.feed.android.core.ui.widgets.comment.commentlayout.view.LMFeedCommentViewStyle
import com.likeminds.feed.android.core.ui.widgets.headerview.style.LMFeedHeaderViewStyle
import com.likeminds.feed.android.core.ui.widgets.noentitylayout.style.LMFeedNoEntityLayoutViewStyle
import com.likeminds.feed.android.core.ui.widgets.viewmore.style.LMFeedViewMoreStyle
import com.likeminds.feed.android.core.utils.LMFeedViewStyle
import com.likeminds.feed.android.core.utils.model.LMFeedPadding

/**
 * [LMFeedPostDetailFragmentViewStyle] helps you to customize the post detail fragment [LMFeedPostDetailFragment]
 *
 * @property headerViewStyle : [LMFeedHeaderViewStyle] this will help you to customize the header view in the post detail fragment
 * @property commentsCountViewStyle : [LMFeedTextStyle] this will help you to customize the comments count text in the post detail fragment
 * @property commentViewStyle: [LMFeedCommentViewStyle] this will help you to customize the comment view in the post detail fragment
 * @property replyViewStyle: [LMFeedCommentViewStyle] this will help you to customize the reply view in the post detail fragment
 * @property noCommentsFoundViewStyle: [LMFeedNoEntityLayoutViewStyle] this will help you to customize the no comments found view in the post detail fragment
 * @property commentComposerStyle: [LMFeedCommentComposerViewStyle] this will help you to customize the comment composer view in the post detail fragment
 * @property viewMoreReplyStyle: [LMFeedViewMoreStyle] this will help you to customize the view more reply view in the post detail fragment
 * */
class LMFeedPostDetailFragmentViewStyle private constructor(
    //header
    val headerViewStyle: LMFeedHeaderViewStyle,
    //comments count view style
    val commentsCountViewStyle: LMFeedTextStyle,
    //comment view style
    val commentViewStyle: LMFeedCommentViewStyle,
    //reply view style
    val replyViewStyle: LMFeedCommentViewStyle,
    //no comments found view
    val noCommentsFoundViewStyle: LMFeedNoEntityLayoutViewStyle,
    //comment composer view
    val commentComposerStyle: LMFeedCommentComposerViewStyle,
    //view more reply view style
    val viewMoreReplyStyle: LMFeedViewMoreStyle
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
            .subtitleTextStyle(
                LMFeedTextStyle.Builder()
                    .textColor(R.color.lm_feed_grey)
                    .maxLines(1)
                    .ellipsize(TextUtils.TruncateAt.END)
                    .textSize(R.dimen.lm_feed_text_medium)
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

        private var commentsCountViewStyle: LMFeedTextStyle = LMFeedTextStyle.Builder()
            .textSize(R.dimen.lm_feed_text_medium)
            .textColor(R.color.lm_feed_raisin_black)
            .fontResource(R.font.lm_feed_roboto_medium)
            .build()

        private var commentViewStyle: LMFeedCommentViewStyle = LMFeedCommentViewStyle.Builder()
            .menuIconStyle(
                LMFeedIconStyle.Builder()
                    .inActiveSrc(R.drawable.lm_feed_ic_overflow_menu)
                    .build()
            )
            .likeTextStyle(
                LMFeedTextStyle.Builder()
                    .textColor(R.color.lm_feed_maastricht_blue_40)
                    .textSize(R.dimen.lm_feed_text_small)
                    .fontResource(R.font.lm_feed_roboto)
                    .build()
            )
            .replyTextStyle(
                LMFeedTextStyle.Builder()
                    .textColor(R.color.lm_feed_maastricht_blue_40)
                    .textSize(R.dimen.lm_feed_text_small)
                    .fontResource(R.font.lm_feed_roboto)
                    .build()
            )
            .replyCountTextStyle(
                LMFeedTextStyle.Builder()
                    .textColor(R.color.lm_feed_majorelle_blue)
                    .textSize(R.dimen.lm_feed_text_small)
                    .fontResource(R.font.lm_feed_roboto)
                    .build()
            )
            .timestampTextStyle(
                LMFeedTextStyle.Builder()
                    .textColor(R.color.lm_feed_maastricht_blue_40)
                    .textSize(R.dimen.lm_feed_text_small)
                    .fontResource(R.font.lm_feed_roboto)
                    .build()
            )
            .commentEditedTextStyle(
                LMFeedTextStyle.Builder()
                    .textColor(R.color.lm_feed_maastricht_blue_40)
                    .textSize(R.dimen.lm_feed_text_small)
                    .fontResource(R.font.lm_feed_roboto)
                    .build()
            )
            .build()

        private var replyViewStyle: LMFeedCommentViewStyle = LMFeedCommentViewStyle.Builder()
            .menuIconStyle(
                LMFeedIconStyle.Builder()
                    .inActiveSrc(R.drawable.lm_feed_ic_overflow_menu)
                    .build()
            )
            .likeTextStyle(
                LMFeedTextStyle.Builder()
                    .textColor(R.color.lm_feed_maastricht_blue_40)
                    .textSize(R.dimen.lm_feed_text_small)
                    .fontResource(R.font.lm_feed_roboto)
                    .build()
            )
            .timestampTextStyle(
                LMFeedTextStyle.Builder()
                    .textColor(R.color.lm_feed_maastricht_blue_40)
                    .textSize(R.dimen.lm_feed_text_small)
                    .fontResource(R.font.lm_feed_roboto)
                    .build()
            )
            .commentEditedTextStyle(
                LMFeedTextStyle.Builder()
                    .textColor(R.color.lm_feed_maastricht_blue_40)
                    .textSize(R.dimen.lm_feed_text_small)
                    .fontResource(R.font.lm_feed_roboto)
                    .build()
            )
            .build()

        private var noCommentsFoundViewStyle: LMFeedNoEntityLayoutViewStyle =
            LMFeedNoEntityLayoutViewStyle.Builder()
                .titleStyle(
                    LMFeedTextStyle.Builder()
                        .textColor(R.color.lm_feed_dark_grey)
                        .textSize(R.dimen.lm_feed_text_large)
                        .build()
                )
                .subtitleStyle(
                    LMFeedTextStyle.Builder()
                        .textColor(R.color.lm_feed_grey)
                        .textSize(R.dimen.lm_feed_text_medium)
                        .build()
                )
                .backgroundColor(R.color.lm_feed_cultured)
                .build()

        private var commentComposerStyle: LMFeedCommentComposerViewStyle =
            LMFeedCommentComposerViewStyle.Builder()
                .commentRestrictedStyle(
                    LMFeedTextStyle.Builder()
                        .backgroundColor(R.color.lm_feed_white)
                        .minHeight(R.dimen.lm_feed_text_min_height)
                        .textColor(R.color.lm_feed_grey_brown_50)
                        .textSize(R.dimen.lm_feed_text_medium)
                        .build()
                )
                .replyingToStyle(
                    LMFeedTextStyle.Builder()
                        .backgroundColor(R.color.lm_feed_bright_grey)
                        .textSize(R.dimen.lm_feed_text_medium)
                        .textColor(R.color.lm_feed_grey)
                        .build()
                )
                .removeReplyingToStyle(
                    LMFeedIconStyle.Builder()
                        .iconPadding(
                            LMFeedPadding(
                                R.dimen.lm_feed_icon_padding,
                                R.dimen.lm_feed_icon_padding,
                                R.dimen.lm_feed_icon_padding,
                                R.dimen.lm_feed_icon_padding
                            )
                        )
                        .inActiveSrc(R.drawable.lm_feed_ic_multiply)
                        .build()
                )
                .elevation(R.dimen.lm_feed_elevation_small)
                .backgroundColor(R.color.lm_feed_white)
                .build()

        private var viewMoreReplyStyle = LMFeedViewMoreStyle.Builder()
            .visibleCountTextStyle(
                LMFeedTextStyle.Builder()
                    .textColor(R.color.lm_feed_dark_grayish_blue)
                    .textSize(R.dimen.lm_feed_text_small)
                    .fontResource(R.font.lm_feed_roboto)
                    .build()
            )
            .build()

        fun headerViewStyle(headerViewStyle: LMFeedHeaderViewStyle) = apply {
            this.headerViewStyle = headerViewStyle
        }

        fun commentsCountViewStyle(commentsCountViewStyle: LMFeedTextStyle) = apply {
            this.commentsCountViewStyle = commentsCountViewStyle
        }

        fun commentViewStyle(commentViewStyle: LMFeedCommentViewStyle) = apply {
            this.commentViewStyle = commentViewStyle
        }

        fun replyViewStyle(replyViewStyle: LMFeedCommentViewStyle) = apply {
            this.replyViewStyle = replyViewStyle
        }

        fun noCommentsFoundViewStyle(noCommentsFoundViewStyle: LMFeedNoEntityLayoutViewStyle) =
            apply {
                this.noCommentsFoundViewStyle = noCommentsFoundViewStyle
            }

        fun commentComposerStyle(commentComposerStyle: LMFeedCommentComposerViewStyle) = apply {
            this.commentComposerStyle = commentComposerStyle
        }

        fun viewMoreReplyStyle(viewMoreReplyStyle: LMFeedViewMoreStyle) = apply {
            this.viewMoreReplyStyle = viewMoreReplyStyle
        }

        fun build() = LMFeedPostDetailFragmentViewStyle(
            headerViewStyle,
            commentsCountViewStyle,
            commentViewStyle,
            replyViewStyle,
            noCommentsFoundViewStyle,
            commentComposerStyle,
            viewMoreReplyStyle
        )
    }

    fun toBuilder(): Builder {
        return Builder().commentComposerStyle(commentComposerStyle)
            .commentsCountViewStyle(commentsCountViewStyle)
            .commentViewStyle(commentViewStyle)
            .replyViewStyle(replyViewStyle)
            .noCommentsFoundViewStyle(noCommentsFoundViewStyle)
            .headerViewStyle(headerViewStyle)
            .viewMoreReplyStyle(viewMoreReplyStyle)
    }
}