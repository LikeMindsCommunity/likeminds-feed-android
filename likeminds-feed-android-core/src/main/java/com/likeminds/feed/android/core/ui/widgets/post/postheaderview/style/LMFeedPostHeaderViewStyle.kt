package com.likeminds.feed.android.core.ui.widgets.post.postheaderview.style

import android.text.TextUtils
import androidx.annotation.ColorRes
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.ui.base.styles.*
import com.likeminds.feed.android.core.utils.LMFeedViewStyle

/**
 * [LMFeedPostHeaderViewStyle] helps you to customize the post header view [LMFeedPostHeaderView]
 *
 * @property authorImageViewStyle : [LMFeedImageStyle] this will help you to customize the author image in the post header
 * @property authorNameViewStyle : [LMFeedTextStyle] this will help you to customize the author name in the post header
 * @property timestampTextStyle: [LMFeedTextStyle] this will help you to customize the timestamp text in the post header | set its value to [null] if you want to hide the timestamp in the post header
 * @property postEditedTextStyle: [LMFeedTextStyle] this will help you to customize the edited text tag in an edited post header |  set its value to [null] if you want to hide the edited tag in the post header
 * @property authorCustomTitleTextStyle: [LMFeedTextStyle] this will help you to customize the custom title of the author in the post header | set its value to [null] if you want to hide the custom title of the author in the post header
 * @property pinIconStyle: [LMFeedIconStyle] this will help you to customize the pin icon of the post | set its value to [null] if you want to hide the pin icon in the post header
 * @property menuIconStyle: [LMFeedIconStyle] this will help you to customize the menu icon of the post | set its value to [null] if you want to hide the menu icon in the post header
 * @property backgroundColor:  [Int] should be in format of [ColorRes] to add background color of the post header | Default value = [null]
 *
 * */
class LMFeedPostHeaderViewStyle private constructor(
    val authorImageViewStyle: LMFeedImageStyle,
    val authorNameViewStyle: LMFeedTextStyle,
    val timestampTextStyle: LMFeedTextStyle?,
    val postEditedTextStyle: LMFeedTextStyle?,
    val authorCustomTitleTextStyle: LMFeedTextStyle?,
    val pinIconStyle: LMFeedIconStyle?,
    val menuIconStyle: LMFeedIconStyle?,
    @ColorRes val backgroundColor: Int?
) : LMFeedViewStyle {

    class Builder {
        private var authorImageViewStyle: LMFeedImageStyle = LMFeedImageStyle.Builder()
            .isCircle(true)
            .showGreyScale(false)
            .build()

        private var authorNameViewStyle: LMFeedTextStyle = LMFeedTextStyle.Builder()
            .textSize(R.dimen.lm_feed_text_large)
            .textColor(R.color.lm_feed_raisin_black)
            .maxLines(1)
            .ellipsize(TextUtils.TruncateAt.END)
            .fontResource(R.font.lm_feed_roboto_medium)
            .build()

        private var timestampTextStyle: LMFeedTextStyle? = null

        private var postEditedTextStyle: LMFeedTextStyle? = null

        private var authorCustomTitleTextStyle: LMFeedTextStyle? = null

        private var pinIconStyle: LMFeedIconStyle? = null

        private var menuIconStyle: LMFeedIconStyle? = null

        @ColorRes
        private var backgroundColor: Int? = null

        fun authorImageViewStyle(authorImageViewStyle: LMFeedImageStyle) = apply {
            this.authorImageViewStyle = authorImageViewStyle
        }

        fun authorNameViewStyle(authorNameViewStyle: LMFeedTextStyle) = apply {
            this.authorNameViewStyle = authorNameViewStyle
        }

        fun timestampTextStyle(timestampTextStyle: LMFeedTextStyle?) = apply {
            this.timestampTextStyle = timestampTextStyle
        }

        fun postEditedTextStyle(postEditedTextStyle: LMFeedTextStyle?) = apply {
            this.postEditedTextStyle = postEditedTextStyle
        }

        fun authorCustomTitleTextStyle(authorCustomTitleTextStyle: LMFeedTextStyle?) = apply {
            this.authorCustomTitleTextStyle = authorCustomTitleTextStyle
        }

        fun pinIconStyle(pinIconStyle: LMFeedIconStyle?) = apply {
            this.pinIconStyle = pinIconStyle
        }

        fun menuIconStyle(menuIconStyle: LMFeedIconStyle?) = apply {
            this.menuIconStyle = menuIconStyle
        }

        fun backgroundColor(@ColorRes backgroundColor: Int?) =
            apply { this.backgroundColor = backgroundColor }

        fun build() = LMFeedPostHeaderViewStyle(
            authorImageViewStyle,
            authorNameViewStyle,
            timestampTextStyle,
            postEditedTextStyle,
            authorCustomTitleTextStyle,
            pinIconStyle,
            menuIconStyle,
            backgroundColor
        )
    }

    fun toBuilder(): Builder {
        return Builder().authorImageViewStyle(authorImageViewStyle)
            .authorNameViewStyle(authorNameViewStyle)
            .timestampTextStyle(timestampTextStyle)
            .postEditedTextStyle(postEditedTextStyle)
            .authorCustomTitleTextStyle(authorCustomTitleTextStyle)
            .pinIconStyle(pinIconStyle)
            .menuIconStyle(menuIconStyle)
            .backgroundColor(backgroundColor)
    }
}