package com.likeminds.feed.android.core.ui.widgets.post.postactionview.style

import android.view.View
import androidx.annotation.ColorRes
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.ui.base.styles.*
import com.likeminds.feed.android.core.utils.LMFeedViewStyle

/**
 * [LMFeedPostActionViewStyle] helps you to customize the post action view
 *
 * @property likeIconStyle : [LMFeedIconStyle] this will help you to customize the like icon in the post action view
 * @property likeTextStyle : [LMFeedTextStyle] this will help you to customize the like text in the post action view | set its value to [null] if you want to hide the like text in the post action
 * @property commentTextStyle: [LMFeedTextStyle] this will help you to customize the comment text in the post action view
 * @property saveIconStyle: [LMFeedIconStyle] this will help you to customize the save icon in the post action view | set its value to [null] if you want to hide the save icon in the post action
 * @property shareIconStyle: [LMFeedIconStyle] this will help you to customize the share icon in the post action view | set its value to [null] if you want to hide the share icon in the post action
 * @property shareIconStyle: [LMFeedIconStyle] this will help you to customize the menu icon in the post action view | set its value to [null] if you want to hide the menu icon in the post action
 * @property backgroundColor: [Int] should be in format of [ColorRes] to add background color of the action view | Default value =  [null]
 * */
class LMFeedPostActionViewStyle private constructor(
    //likes icon style
    val likeIconStyle: LMFeedIconStyle,
    //likes text style
    val likeTextStyle: LMFeedTextStyle?,
    //comment text style
    val commentTextStyle: LMFeedTextStyle?,
    //save icon style
    val saveIconStyle: LMFeedIconStyle?,
    //share icon style
    val shareIconStyle: LMFeedIconStyle?,
    //menu icon style
    val menuIconStyle: LMFeedIconStyle?,
    //background color of the action view
    @ColorRes val backgroundColor: Int?,
) : LMFeedViewStyle {

    class Builder {
        private var likeTextStyle: LMFeedTextStyle? = null

        private var likeIconStyle: LMFeedIconStyle = LMFeedIconStyle.Builder()
            .activeSrc(R.drawable.lm_feed_ic_like_filled)
            .inActiveSrc(R.drawable.lm_feed_ic_like_unfilled)
            .build()

        private var commentTextStyle: LMFeedTextStyle? = null

        private var saveIconStyle: LMFeedIconStyle? = null

        private var shareIconStyle: LMFeedIconStyle? = null

        private var menuIconStyle: LMFeedIconStyle? = null

        @ColorRes
        private var backgroundColor: Int? = null

        fun likeTextStyle(likeTextStyle: LMFeedTextStyle?) = apply {
            this.likeTextStyle = likeTextStyle
        }

        fun likeIconStyle(likeIconStyle: LMFeedIconStyle) = apply {
            this.likeIconStyle = likeIconStyle
        }

        fun commentTextStyle(commentTextStyle: LMFeedTextStyle?) = apply {
            this.commentTextStyle = commentTextStyle
        }

        fun saveIconStyle(saveIconStyle: LMFeedIconStyle?) = apply {
            this.saveIconStyle = saveIconStyle
        }

        fun shareIconStyle(shareIconStyle: LMFeedIconStyle?) = apply {
            this.shareIconStyle = shareIconStyle
        }

        fun menuIconStyle(menuIconStyle: LMFeedIconStyle?) = apply {
            this.menuIconStyle = menuIconStyle
        }

        fun backgroundColor(@ColorRes backgroundColor: Int?) =
            apply { this.backgroundColor = backgroundColor }

        fun build() = LMFeedPostActionViewStyle(
            likeIconStyle,
            likeTextStyle,
            commentTextStyle,
            saveIconStyle,
            shareIconStyle,
            menuIconStyle,
            backgroundColor
        )
    }

    fun toBuilder(): Builder {
        return Builder().likeTextStyle(likeTextStyle)
            .likeIconStyle(likeIconStyle)
            .commentTextStyle(commentTextStyle)
            .saveIconStyle(saveIconStyle)
            .shareIconStyle(shareIconStyle)
            .menuIconStyle(menuIconStyle)
            .backgroundColor(backgroundColor)
    }
}