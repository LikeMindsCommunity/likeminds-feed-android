package com.likeminds.feed.android.core.ui.widgets.post.postcontent.style

import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.search.view.LMFeedSearchFragment
import com.likeminds.feed.android.core.ui.base.styles.LMFeedTextStyle
import com.likeminds.feed.android.core.utils.LMFeedViewStyle

/**
 * [LMFeedPostContentViewStyle] helps you to customize the content of the post in the search feed fragment [LMFeedSearchFragment]
 *
 * @property textContentViewStyle : [LMFeedTextStyle] this will help you to customize the content of the post
 * @property searchHighlightedTextViewStyle : [LMFeedTextStyle] this will help you to customize the text of highlighted keyword in the post | Default value = [null]
 *
 * */
class LMFeedPostContentViewStyle private constructor(
    // content text view style
    val textContentViewStyle: LMFeedTextStyle,
    // highlighted search text style
    val searchHighlightedTextViewStyle: LMFeedTextStyle?
) : LMFeedViewStyle {

    class Builder {
        private var postTextViewStyle: LMFeedTextStyle =
            LMFeedTextStyle.Builder()
                .textColor(R.color.lm_feed_grey)
                .textSize(R.dimen.lm_feed_text_large)
                .fontResource(R.font.lm_feed_roboto)
                .build()

        private var searchHighlightedTextViewStyle: LMFeedTextStyle? = null

        fun postTextViewStyle(postTextViewStyle: LMFeedTextStyle) = apply {
            this.postTextViewStyle = postTextViewStyle
        }

        fun searchHighlightedTextViewStyle(searchHighlightedViewStyle: LMFeedTextStyle?) = apply {
            this.searchHighlightedTextViewStyle = searchHighlightedViewStyle
        }

        fun build() = LMFeedPostContentViewStyle(
            postTextViewStyle,
            searchHighlightedTextViewStyle
        )
    }

    fun toBuilder(): Builder {
        return Builder().postTextViewStyle(textContentViewStyle)
            .searchHighlightedTextViewStyle(searchHighlightedTextViewStyle)
    }
}