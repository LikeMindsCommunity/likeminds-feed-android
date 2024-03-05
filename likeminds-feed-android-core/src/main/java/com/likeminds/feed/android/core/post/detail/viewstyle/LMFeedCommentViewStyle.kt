package com.likeminds.feed.android.core.post.detail.viewstyle

import androidx.annotation.ColorRes
import com.likeminds.feed.android.core.ui.base.styles.*
import com.likeminds.feed.android.core.utils.LMFeedViewStyle

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
            .build()
        private var commentContentStyle: LMFeedTextStyle = LMFeedTextStyle.Builder()
            .build()
        private var menuIconStyle: LMFeedIconStyle? = null
        private var likeIconStyle: LMFeedIconStyle = LMFeedIconStyle.Builder()
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