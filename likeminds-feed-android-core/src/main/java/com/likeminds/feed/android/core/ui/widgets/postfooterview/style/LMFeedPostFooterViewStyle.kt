package com.likeminds.feed.android.core.ui.widgets.postfooterview.style

import android.view.View
import androidx.annotation.ColorRes
import com.likeminds.feed.android.ui.R
import com.likeminds.feed.android.ui.base.styles.LMFeedIconStyle
import com.likeminds.feed.android.ui.base.styles.LMFeedTextStyle
import com.likeminds.feed.android.ui.utils.LMFeedViewStyle

class LMFeedPostFooterViewStyle private constructor(
    val likeIconStyle: LMFeedIconStyle,
    val likeTextStyle: LMFeedTextStyle?,
    val commentTextStyle: LMFeedTextStyle,
    val saveIconStyle: LMFeedIconStyle?,
    val shareIconStyle: LMFeedIconStyle?,
    @ColorRes val backgroundColor: Int?,
) : LMFeedViewStyle {

    class Builder {
        private var likeTextStyle: LMFeedTextStyle? = null
        private var likeIconStyle: LMFeedIconStyle = LMFeedIconStyle.Builder()
            .activeSrc(R.drawable.lm_feed_ic_like_filled)
            .inActiveSrc(R.drawable.lm_feed_ic_like_unfilled)
            .build()
        private var commentTextStyle: LMFeedTextStyle = LMFeedTextStyle.Builder()
            .textColor(R.color.lm_feed_grey)
            .textSize(R.dimen.lm_feed_text_medium)
            .fontResource(R.font.lm_feed_ui_roboto)
            .textAllCaps(false)
            .textAlignment(View.TEXT_ALIGNMENT_CENTER)
            .drawableLeftSrc(R.drawable.lm_feed_ic_comment)
            .drawablePadding(R.dimen.lm_feed_padding_big)
            .build()
        private var saveIconStyle: LMFeedIconStyle? = null
        private var shareIconStyle: LMFeedIconStyle? = null

        @ColorRes
        private var backgroundColor: Int? = null

        fun likeTextStyle(likeTextStyle: LMFeedTextStyle?) = apply {
            this.likeTextStyle = likeTextStyle
        }

        fun likeIconStyle(likeIconStyle: LMFeedIconStyle) = apply {
            this.likeIconStyle = likeIconStyle
        }

        fun commentTextStyle(commentTextStyle: LMFeedTextStyle) = apply {
            this.commentTextStyle = commentTextStyle
        }

        fun saveIconStyle(saveIconStyle: LMFeedIconStyle?) = apply {
            this.saveIconStyle = saveIconStyle
        }

        fun shareIconStyle(shareIconStyle: LMFeedIconStyle?) = apply {
            this.shareIconStyle = shareIconStyle
        }

        fun backgroundColor(@ColorRes backgroundColor: Int?) =
            apply { this.backgroundColor = backgroundColor }

        fun build() = LMFeedPostFooterViewStyle(
            likeIconStyle,
            likeTextStyle,
            commentTextStyle,
            saveIconStyle,
            shareIconStyle,
            backgroundColor
        )
    }

    fun toBuilder(): Builder {
        return Builder().likeTextStyle(likeTextStyle)
            .likeIconStyle(likeIconStyle)
            .commentTextStyle(commentTextStyle)
            .saveIconStyle(saveIconStyle)
            .shareIconStyle(shareIconStyle)
            .backgroundColor(backgroundColor)
    }
}