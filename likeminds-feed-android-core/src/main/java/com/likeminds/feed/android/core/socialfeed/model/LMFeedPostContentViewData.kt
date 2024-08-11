package com.likeminds.feed.android.core.socialfeed.model

import com.likeminds.feed.android.core.utils.base.LMFeedBaseViewType
import com.likeminds.feed.android.core.utils.base.model.ITEM_POST_CONTENT

class LMFeedPostContentViewData private constructor(
    val text: String?,
    val alreadySeenFullContent: Boolean?
) : LMFeedBaseViewType {

    override val viewType: Int
        get() = ITEM_POST_CONTENT

    class Builder {
        private var text: String? = null
        private var alreadySeenFullContent: Boolean? = null

        fun text(text: String?) = apply { this.text = text }
        fun alreadySeenFullContent(alreadySeenFullContent: Boolean?) =
            apply { this.alreadySeenFullContent = alreadySeenFullContent }

        fun build() = LMFeedPostContentViewData(
            text,
            alreadySeenFullContent
        )
    }

    fun toBuilder(): Builder {
        return Builder().text(text)
            .alreadySeenFullContent(alreadySeenFullContent)
    }

    override fun toString(): String {
        return "LMFeedPostContentViewData(text=$text, alreadySeenFullContent=$alreadySeenFullContent)"
    }
}