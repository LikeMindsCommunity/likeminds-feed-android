package com.likeminds.feed.android.core.ui.widgets.post.postcontent.style

import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.search.view.LMFeedSearchFragment
import com.likeminds.feed.android.core.ui.base.styles.LMFeedTextStyle
import com.likeminds.feed.android.core.utils.LMFeedViewStyle

/**
 * [LMFeedPostContentViewStyle] helps you to customize the content of the post in the search feed fragment [LMFeedSearchFragment]
 *
 * @property postTextViewStyle : [LMFeedTextStyle] this will help you to customize the text of the post
 * @property searchHighlightedViewStyle : [LMFeedTextStyle] this will help you to customize the text of highlighted keyword in the post | Default value = [null]
 *
 * */
class LMFeedPostContentViewStyle private constructor(
    // post text style
    val postTextViewStyle: LMFeedTextStyle,
    // highlighted search text style
    val searchHighlightedViewStyle: LMFeedTextStyle?
) : LMFeedViewStyle {

    class Builder {
        private var postTextViewStyle: LMFeedTextStyle =
            LMFeedTextStyle.Builder()
                .textColor(R.color.lm_feed_grey)
                .textSize(R.dimen.lm_feed_text_large)
                .fontResource(R.font.lm_feed_roboto)
                .build()
        private var searchHighlightedViewStyle: LMFeedTextStyle? = null

        fun postTextViewStyle(postTextViewStyle: LMFeedTextStyle) = apply {
            this.postTextViewStyle = postTextViewStyle
        }

        fun searchHighlightedViewStyle(searchHighlightedViewStyle: LMFeedTextStyle?) = apply {
            this.searchHighlightedViewStyle = searchHighlightedViewStyle
        }

        fun build() = LMFeedPostContentViewStyle(
            postTextViewStyle,
            searchHighlightedViewStyle
        )
    }

    fun toBuilder(): Builder {
        return Builder().postTextViewStyle(postTextViewStyle)
            .searchHighlightedViewStyle(searchHighlightedViewStyle)
    }
}