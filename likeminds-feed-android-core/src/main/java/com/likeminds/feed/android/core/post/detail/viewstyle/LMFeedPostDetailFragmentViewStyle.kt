package com.likeminds.feed.android.core.post.detail.viewstyle

import android.text.TextUtils
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.ui.base.styles.LMFeedIconStyle
import com.likeminds.feed.android.core.ui.base.styles.LMFeedTextStyle
import com.likeminds.feed.android.core.ui.widgets.comment.commentcomposer.style.LMFeedCommentComposerStyle
import com.likeminds.feed.android.core.ui.widgets.comment.commentlayout.view.LMFeedCommentViewStyle
import com.likeminds.feed.android.core.ui.widgets.headerview.style.LMFeedHeaderViewStyle
import com.likeminds.feed.android.core.ui.widgets.noentitylayout.style.LMFeedNoEntityLayoutViewStyle
import com.likeminds.feed.android.core.utils.LMFeedViewStyle
import com.likeminds.feed.android.core.utils.model.LMFeedPadding

class LMFeedPostDetailFragmentViewStyle private constructor(
    //header
    val headerViewStyle: LMFeedHeaderViewStyle,
    //comments count view style
    val commentsCountViewStyle: LMFeedTextStyle,
    //comment view style
    val commentViewStyle: LMFeedCommentViewStyle,
    //no comments found view
    val noCommentsFoundViewStyle: LMFeedNoEntityLayoutViewStyle,
    //comment composer view
    val commentComposerStyle: LMFeedCommentComposerStyle
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
                .build()

        private var commentComposerStyle: LMFeedCommentComposerStyle =
            LMFeedCommentComposerStyle.Builder()
                .commentRestrictedStyle(
                    LMFeedTextStyle.Builder()
                        .backgroundColor(R.color.lm_feed_white)
                        .elevation(R.dimen.lm_feed_elevation_small)
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

        fun noCommentsFoundViewStyle(noCommentsFoundViewStyle: LMFeedNoEntityLayoutViewStyle) =
            apply {
                this.noCommentsFoundViewStyle = noCommentsFoundViewStyle
            }

        fun commentComposerStyle(commentComposerStyle: LMFeedCommentComposerStyle) = apply {
            this.commentComposerStyle = commentComposerStyle
        }

        fun build() = LMFeedPostDetailFragmentViewStyle(
            headerViewStyle,
            commentsCountViewStyle,
            commentViewStyle,
            noCommentsFoundViewStyle,
            commentComposerStyle
        )
    }

    fun toBuilder(): Builder {
        return Builder().commentComposerStyle(commentComposerStyle)
            .commentsCountViewStyle(commentsCountViewStyle)
            .commentViewStyle(commentViewStyle)
            .noCommentsFoundViewStyle(noCommentsFoundViewStyle)
            .headerViewStyle(headerViewStyle)
    }
}