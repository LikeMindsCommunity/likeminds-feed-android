package com.likeminds.feed.android.core.ui.widgets.comment.commentcomposer.style

import android.graphics.Typeface
import android.text.TextUtils
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.ui.base.styles.*
import com.likeminds.feed.android.core.ui.theme.LMFeedAppearance
import com.likeminds.feed.android.core.utils.LMFeedViewStyle

/**
 * [LMFeedCommentComposerViewStyle] helps you to customize the comment view [LMFeedCommentComposerView]
 *
 * @property commentInputStyle : [LMFeedEditTextStyle] this will help you to customize the comment input box (edit text)
 * @property commentSendStyle : [LMFeedIconStyle] this will help you to customize the comment submit button
 * @property commentRestrictedStyle: [LMFeedTextStyle] this will help you to customize the comment restricted view | set its value to [null] if you want to hide the comment restricted view
 * @property replyingToStyle: [LMFeedTextStyle] this will help you to customize the replying to text view | set its value to [null] if you want to hide the replying to text view
 * @property removeReplyingToStyle: [LMFeedIconStyle] this will help you to customize the remove replying to view icon | set its value to [null] if you want to hide the remove replying to view icon
 * @property elevation: [Int] should be in format of [DimenRes] to customize the elevation of the comment composer | Default value = [null]
 * @property backgroundColor: [Int] should be in format of [ColorRes] this will help you to customize the background color of the comment composer | Default value = [null]
 * */
class LMFeedCommentComposerViewStyle private constructor(
    //comment input style
    val commentInputStyle: LMFeedEditTextStyle,
    //comment send style
    val commentSendStyle: LMFeedIconStyle,
    //comment restricted style
    val commentRestrictedStyle: LMFeedTextStyle?,
    //replying to view style
    val replyingToStyle: LMFeedTextStyle?,
    //remove replying to view style
    val removeReplyingToStyle: LMFeedIconStyle?,
    //elevation of the composer
    @DimenRes val elevation: Int?,
    //background color of the composer
    @ColorRes val backgroundColor: Int?
) : LMFeedViewStyle {
    class Builder {
        private var commentInputStyle: LMFeedEditTextStyle = LMFeedEditTextStyle.Builder()
            .inputTextStyle(
                LMFeedTextStyle.Builder()
                    .ellipsize(TextUtils.TruncateAt.END)
                    .maxHeight(R.dimen.lm_feed_text_max_height)
                    .minHeight(R.dimen.lm_feed_text_min_height)
                    .textColor(R.color.lm_feed_dark_grey)
                    .textSize(R.dimen.lm_feed_text_large)
                    .typeface(Typeface.NORMAL)
                    .fontResource(R.font.lm_feed_roboto)
                    .backgroundColor(R.color.lm_feed_white)
                    .build()
            )
            .backgroundColor(R.color.lm_feed_white)
            .hintTextColor(R.color.lm_feed_maastricht_blue_40)
            .build()

        private var commentSendStyle: LMFeedIconStyle = LMFeedIconStyle.Builder()
            .activeSrc(R.drawable.lm_feed_ic_comment_send_enable)
            .inActiveSrc(R.drawable.lm_feed_ic_comment_send_disable)
            .backgroundColor(R.color.lm_feed_white)
            .iconTint(LMFeedAppearance.getButtonColor())
            .build()

        private var commentRestrictedStyle: LMFeedTextStyle? = null

        private var replyingToStyle: LMFeedTextStyle? = null

        private var removeReplyingToStyle: LMFeedIconStyle? = null

        @DimenRes
        private var elevation: Int? = null

        @ColorRes
        private var backgroundColor: Int? = null

        fun commentInputStyle(commentInputStyle: LMFeedEditTextStyle) = apply {
            this.commentInputStyle = commentInputStyle
        }

        fun commentSendStyle(commentSendStyle: LMFeedIconStyle) = apply {
            this.commentSendStyle = commentSendStyle
        }

        fun commentRestrictedStyle(commentRestrictedStyle: LMFeedTextStyle?) = apply {
            this.commentRestrictedStyle = commentRestrictedStyle
        }

        fun replyingToStyle(replyingToStyle: LMFeedTextStyle?) = apply {
            this.replyingToStyle = replyingToStyle
        }

        fun removeReplyingToStyle(removeReplyingToStyle: LMFeedIconStyle?) = apply {
            this.removeReplyingToStyle = removeReplyingToStyle
        }

        fun elevation(@DimenRes elevation: Int?) = apply {
            this.elevation = elevation
        }

        fun backgroundColor(@ColorRes backgroundColor: Int?) = apply {
            this.backgroundColor = backgroundColor
        }

        fun build() = LMFeedCommentComposerViewStyle(
            commentInputStyle,
            commentSendStyle,
            commentRestrictedStyle,
            replyingToStyle,
            removeReplyingToStyle,
            elevation,
            backgroundColor
        )
    }

    fun toBuilder(): Builder {
        return Builder().commentInputStyle(commentInputStyle)
            .commentSendStyle(commentSendStyle)
            .commentRestrictedStyle(commentRestrictedStyle)
            .replyingToStyle(replyingToStyle)
            .removeReplyingToStyle(removeReplyingToStyle)
            .elevation(elevation)
            .backgroundColor(backgroundColor)
    }
}