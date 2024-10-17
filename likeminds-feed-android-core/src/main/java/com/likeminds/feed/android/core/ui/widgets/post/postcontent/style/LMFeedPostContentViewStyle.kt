package com.likeminds.feed.android.core.ui.widgets.post.postcontent.style

import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.ui.base.styles.LMFeedTextStyle
import com.likeminds.feed.android.core.utils.LMFeedViewStyle

class LMFeedPostContentViewStyle private constructor(
    val postTextViewStyle: LMFeedTextStyle,
    val searchHighlightedViewStyle: LMFeedTextStyle?
) : LMFeedViewStyle {

    class Builder {
        private var postTextViewStyle: LMFeedTextStyle =
            LMFeedTextStyle.Builder()
                .textColor(R.color.lm_feed_grey)
                .textSize(R.dimen.lm_feed_text_large)
                .maxLines(3)
                .fontResource(R.font.lm_feed_roboto)
                .expandableCTAText("... See more")
                .expandableCTAColor(R.color.lm_feed_brown_grey)
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