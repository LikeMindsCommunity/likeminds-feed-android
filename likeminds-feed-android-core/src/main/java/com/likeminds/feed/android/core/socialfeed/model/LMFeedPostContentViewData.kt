package com.likeminds.feed.android.core.socialfeed.model

import com.likeminds.feed.android.core.post.style.LMFeedPostViewStyle
import com.likeminds.feed.android.core.utils.base.LMFeedBaseViewType
import com.likeminds.feed.android.core.utils.base.model.ITEM_POST_CONTENT

class LMFeedPostContentViewData private constructor(
    val text: String?,
    val alreadySeenFullContent: Boolean?,
    val keywordMatchedInPostText: List<String>?
) : LMFeedBaseViewType {

    override val viewType: Int
        get() = ITEM_POST_CONTENT

    class Builder {
        private var text: String? = null
        private var alreadySeenFullContent: Boolean? = null
        private var keywordMatchedInPostText: List<String>? = null

        fun text(text: String?) = apply { this.text = text }
        fun alreadySeenFullContent(alreadySeenFullContent: Boolean?) =
            apply { this.alreadySeenFullContent = alreadySeenFullContent }

        fun keywordMatchedInPostText(keywordMatchedInPostText: List<String>?) = apply {
            this.keywordMatchedInPostText = keywordMatchedInPostText
        }

        fun build() = LMFeedPostContentViewData(
            text,
            alreadySeenFullContent,
            keywordMatchedInPostText
        )
    }

    fun toBuilder(): Builder {
        return Builder().text(text)
            .alreadySeenFullContent(alreadySeenFullContent)
            .keywordMatchedInPostText(keywordMatchedInPostText)
    }

    override fun toString(): String {
        return "LMFeedPostContentViewData(text=$text, alreadySeenFullContent=$alreadySeenFullContent, keywordMatchedInPostText=$keywordMatchedInPostText)"
    }
}