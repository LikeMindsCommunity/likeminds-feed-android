package com.likeminds.feed.android.core.ui.widgets.post.posttopresponse.style

import android.text.TextUtils
import androidx.annotation.ColorRes
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.ui.base.styles.*
import com.likeminds.feed.android.core.utils.LMFeedViewStyle

/**
 * [LMFeedPostTopResponseViewStyle] helps you to customize the post top response view [LMFeedPostTopResponseView]
 *
 * @property titleTextStyle : [LMFeedTextStyle] this will help you to customize the title text in the post top response view
 * @property authorImageViewStyle : [LMFeedImageStyle] this will help you to customize the author image in the post top response view | set its value to [null] if you want to hide the author image in the post top response
 * @property authorNameTextStyle : [LMFeedTextStyle] this will help you to customize the author name in the post top response view | set its value to [null] if you want to hide the author name in the post top response
 * @property timestampTextStyle: [LMFeedTextStyle] this will help you to customize the timestamp text in the post top response view | set its value to [null] if you want to hide the timestamp in the post top response
 * @property contentTextStyle: [LMFeedTextStyle] this will help you to customize the content text in the post top response view
 * @property backgroundColor:  [Int] should be in format of [ColorRes] to add background color of the post top response | Default value = [null]
 *
 * */
class LMFeedPostTopResponseViewStyle private constructor(
    // top response title style
    val titleTextStyle: LMFeedTextStyle,
    // top response author image style
    val authorImageViewStyle: LMFeedImageStyle?,
    // top response author name style
    val authorNameTextStyle: LMFeedTextStyle?,
    // top response timestamp style
    val timestampTextStyle: LMFeedTextStyle?,
    // top response content style
    val contentTextStyle: LMFeedTextStyle,
    // top response view background color
    @ColorRes val backgroundColor: Int?
) : LMFeedViewStyle {

    class Builder {
        private var titleTextStyle: LMFeedTextStyle = LMFeedTextStyle.Builder()
            .textSize(R.dimen.lm_feed_text_medium)
            .textColor(R.color.lm_feed_dark_grey)
            .maxLines(1)
            .ellipsize(TextUtils.TruncateAt.END)
            .fontResource(R.font.lm_feed_roboto_medium)
            .build()

        private var authorImageViewStyle: LMFeedImageStyle? = null

        private var authorNameTextStyle: LMFeedTextStyle? = null

        private var timestampTextStyle: LMFeedTextStyle? = null

        private var contentTextStyle: LMFeedTextStyle = LMFeedTextStyle.Builder()
            .textSize(R.dimen.lm_feed_text_medium)
            .textColor(R.color.lm_feed_dark_grayish_blue)
            .fontResource(R.font.lm_feed_roboto)
            .build()

        @ColorRes
        private var backgroundColor: Int? = null

        fun titleTextStyle(titleTextStyle: LMFeedTextStyle) = apply {
            this.titleTextStyle = titleTextStyle
        }

        fun authorImageViewStyle(authorImageViewStyle: LMFeedImageStyle?) = apply {
            this.authorImageViewStyle = authorImageViewStyle
        }

        fun authorNameTextStyle(authorNameTextStyle: LMFeedTextStyle?) = apply {
            this.authorNameTextStyle = authorNameTextStyle
        }

        fun timestampTextStyle(timestampTextStyle: LMFeedTextStyle?) = apply {
            this.timestampTextStyle = timestampTextStyle
        }

        fun contentTextStyle(contentTextStyle: LMFeedTextStyle) = apply {
            this.contentTextStyle = contentTextStyle
        }

        fun backgroundColor(@ColorRes backgroundColor: Int?) = apply {
            this.backgroundColor = backgroundColor
        }

        fun build() = LMFeedPostTopResponseViewStyle(
            titleTextStyle,
            authorImageViewStyle,
            authorNameTextStyle,
            timestampTextStyle,
            contentTextStyle,
            backgroundColor
        )
    }

    fun toBuilder(): Builder {
        return Builder().titleTextStyle(titleTextStyle)
            .authorImageViewStyle(authorImageViewStyle)
            .authorNameTextStyle(authorNameTextStyle)
            .timestampTextStyle(timestampTextStyle)
            .contentTextStyle(contentTextStyle)
            .backgroundColor(backgroundColor)
    }
}