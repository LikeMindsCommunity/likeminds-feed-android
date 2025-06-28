package com.likeminds.feed.android.core.ui.widgets.comment.commentlayout.style

import android.text.TextUtils
import androidx.annotation.ColorRes
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.ui.base.styles.*
import com.likeminds.feed.android.core.utils.LMFeedViewStyle

/**
 * [LMFeedCommentViewStyle] helps you to customize the comment view [com.likeminds.feed.android.core.ui.widgets.comment.commentlayout.view.LMFeedCommentView]
 *
 * @property commenterImageViewStyle : [com.likeminds.feed.android.core.ui.base.styles.LMFeedImageStyle] this will help you to customize the comment creator image in the comment view
 * @property commenterNameTextStyle : [com.likeminds.feed.android.core.ui.base.styles.LMFeedTextStyle] this will help you to customize the comment creator name in the comment view
 * @property commentContentStyle: [com.likeminds.feed.android.core.ui.base.styles.LMFeedTextStyle] this will help you to customize the comment content text in the comment view
 * @property menuIconStyle: [com.likeminds.feed.android.core.ui.base.styles.LMFeedIconStyle] this will help you to customize the menu icon in the comment view |  set its value to [null] if you want to hide the menu icon in the comment view
 * @property likeIconStyle: [com.likeminds.feed.android.core.ui.base.styles.LMFeedIconStyle] this will help you to customize the like icon in the comment view
 * @property likeTextStyle: [com.likeminds.feed.android.core.ui.base.styles.LMFeedTextStyle] this will help you to customize the like text in the comment view | set its value to [null] if you want to hide the like text in the comment view
 * @property replyTextStyle: [com.likeminds.feed.android.core.ui.base.styles.LMFeedTextStyle] this will help you to customize the reply text in the comment view | set its value to [null] if you want to hide the reply text in the comment view
 * @property replyCountTextStyle: [com.likeminds.feed.android.core.ui.base.styles.LMFeedTextStyle] this will help you to customize the reply count text in the comment view | set its value to [null] if you want to hide the reply count text in the comment view
 * @property timestampTextStyle: [com.likeminds.feed.android.core.ui.base.styles.LMFeedTextStyle] this will help you to customize the timestamp text in the comment view | set its value to [null] if you want to hide the timestamp text in the comment view
 * @property commentEditedTextStyle: [com.likeminds.feed.android.core.ui.base.styles.LMFeedTextStyle] this will help you to customize the edited text tag in an edited comment view |  set its value to [null] if you want to hide the edited tag in the comment view
 * @property backgroundColor:  [Int] should be in format of [androidx.annotation.ColorRes] to add background color of the comment view | Default value = [null]
 *
 * */
class LMFeedCommentViewStyle private constructor(
    //commenter image style
    val commenterImageViewStyle: LMFeedImageStyle?,
    //commenter name style
    val commenterNameTextStyle: LMFeedTextStyle,
    //comment content style
    val commentContentStyle: LMFeedTextStyle,
    //comment menu icon style
    val menuIconStyle: LMFeedIconStyle?,
    //comment like icon style
    val likeIconStyle: LMFeedIconStyle,
    //comment like count text style
    val likeTextStyle: LMFeedTextStyle?,
    //comment reply text style
    val replyTextStyle: LMFeedTextStyle?,
    //comment replies count text style
    val replyCountTextStyle: LMFeedTextStyle?,
    //comment timestamp text style
    val timestampTextStyle: LMFeedTextStyle?,
    //comment edited text style
    val commentEditedTextStyle: LMFeedTextStyle?,
    //comment view background color
    @ColorRes val backgroundColor: Int?
) : LMFeedViewStyle {

    class Builder {
        private var commenterImageViewStyle: LMFeedImageStyle? = null

        private var commenterNameTextStyle: LMFeedTextStyle = LMFeedTextStyle.Builder()
            .ellipsize(TextUtils.TruncateAt.END)
            .maxLines(1)
            .textColor(R.color.lm_feed_raisin_black)
            .textSize(R.dimen.lm_feed_text_medium)
            .fontResource(R.font.lm_feed_roboto_medium)
            .build()

        private var commentContentStyle: LMFeedTextStyle = LMFeedTextStyle.Builder()
            .textColor(R.color.lm_feed_raisin_black)
            .textSize(R.dimen.lm_feed_text_medium)
            .build()

        private var menuIconStyle: LMFeedIconStyle? = null

        private var likeIconStyle: LMFeedIconStyle = LMFeedIconStyle.Builder()
            .activeSrc(R.drawable.lm_feed_ic_like_comment_filled)
            .inActiveSrc(R.drawable.lm_feed_ic_like_comment_unfilled)
            .build()

        private var likeTextStyle: LMFeedTextStyle? = null

        private var replyTextStyle: LMFeedTextStyle? = null

        private var replyCountTextStyle: LMFeedTextStyle? = null

        private var timestampTextStyle: LMFeedTextStyle? = null

        private var commentEditedTextStyle: LMFeedTextStyle? = null

        @ColorRes
        private var backgroundColor: Int? = null

        fun commenterImageViewStyle(commenterImageViewStyle: LMFeedImageStyle?) = apply {
            this.commenterImageViewStyle = commenterImageViewStyle
        }

        fun commenterNameTextStyle(commenterNameTextStyle: LMFeedTextStyle) = apply {
            this.commenterNameTextStyle = commenterNameTextStyle
        }

        fun commentContentStyle(commentContentStyle: LMFeedTextStyle) = apply {
            this.commentContentStyle = commentContentStyle
        }

        fun menuIconStyle(menuIconStyle: LMFeedIconStyle?) = apply {
            this.menuIconStyle = menuIconStyle
        }

        fun likeIconStyle(likeIconStyle: LMFeedIconStyle) = apply {
            this.likeIconStyle = likeIconStyle
        }

        fun likeTextStyle(likeTextStyle: LMFeedTextStyle?) = apply {
            this.likeTextStyle = likeTextStyle
        }

        fun replyTextStyle(replyTextStyle: LMFeedTextStyle?) = apply {
            this.replyTextStyle = replyTextStyle
        }

        fun replyCountTextStyle(replyCountTextStyle: LMFeedTextStyle?) = apply {
            this.replyCountTextStyle = replyCountTextStyle
        }

        fun timestampTextStyle(timestampTextStyle: LMFeedTextStyle?) = apply {
            this.timestampTextStyle = timestampTextStyle
        }

        fun commentEditedTextStyle(commentEditedTextStyle: LMFeedTextStyle?) = apply {
            this.commentEditedTextStyle = commentEditedTextStyle
        }

        fun backgroundColor(@ColorRes backgroundColor: Int?) = apply {
            this.backgroundColor = backgroundColor
        }

        fun build() = LMFeedCommentViewStyle(
            commenterImageViewStyle,
            commenterNameTextStyle,
            commentContentStyle,
            menuIconStyle,
            likeIconStyle,
            likeTextStyle,
            replyTextStyle,
            replyCountTextStyle,
            timestampTextStyle,
            commentEditedTextStyle,
            backgroundColor
        )
    }

    fun toBuilder(): Builder {
        return Builder().commenterImageViewStyle(commenterImageViewStyle)
            .commenterNameTextStyle(commenterNameTextStyle)
            .commentContentStyle(commentContentStyle)
            .menuIconStyle(menuIconStyle)
            .likeIconStyle(likeIconStyle)
            .likeTextStyle(likeTextStyle)
            .replyTextStyle(replyTextStyle)
            .replyCountTextStyle(replyCountTextStyle)
            .timestampTextStyle(timestampTextStyle)
            .commentEditedTextStyle(commentEditedTextStyle)
            .backgroundColor(backgroundColor)
    }
}