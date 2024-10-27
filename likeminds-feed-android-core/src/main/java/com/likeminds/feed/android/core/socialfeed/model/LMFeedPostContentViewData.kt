package com.likeminds.feed.android.core.socialfeed.model

import com.likeminds.feed.android.core.utils.base.LMFeedBaseViewType
import com.likeminds.feed.android.core.utils.base.model.ITEM_POST_CONTENT

class LMFeedPostContentViewData private constructor(
    val text: String?,
    val alreadySeenFullText: Boolean?,
    val keywordMatchedInPostText: List<String>?,
    val heading: String?,
    val alreadySeenFullHeading: Boolean?,
    val keywordMatchedInPostHeading: List<String>?
) : LMFeedBaseViewType {

    override val viewType: Int
        get() = ITEM_POST_CONTENT

    class Builder {
        private var text: String? = null
        private var alreadySeenFullText: Boolean? = null
        private var keywordMatchedInPostText: List<String>? = null
        private var heading: String? = null
        private var alreadySeenFullHeading: Boolean? = null
        private var keywordMatchedInPostHeading: List<String>? = null

        fun text(text: String?) = apply {
            this.text = text
        }

        fun alreadySeenFullText(alreadySeenFullText: Boolean?) = apply {
            this.alreadySeenFullText = alreadySeenFullText
        }

        fun keywordMatchedInPostText(keywordMatchedInPostText: List<String>?) = apply {
            this.keywordMatchedInPostText = keywordMatchedInPostText
        }

        fun heading(heading: String?) = apply {
            this.heading = heading
        }

        fun alreadySeenFullHeading(alreadySeenFullHeading: Boolean?) = apply {
            this.alreadySeenFullHeading = alreadySeenFullHeading
        }

        fun keywordMatchedInPostHeading(keywordMatchedInPostHeading: List<String>?) = apply {
            this.keywordMatchedInPostHeading = keywordMatchedInPostHeading
        }

        fun build() = LMFeedPostContentViewData(
            text,
            alreadySeenFullText,
            keywordMatchedInPostText,
            heading,
            alreadySeenFullHeading,
            keywordMatchedInPostHeading
        )
    }

    fun toBuilder(): Builder {
        return Builder().text(text)
            .alreadySeenFullText(alreadySeenFullText)
            .keywordMatchedInPostText(keywordMatchedInPostText)
            .heading(heading)
            .alreadySeenFullHeading(alreadySeenFullHeading)
            .keywordMatchedInPostHeading(keywordMatchedInPostHeading)
    }

    override fun toString(): String {
        return buildString {
            append("LMFeedPostContentViewData(text='")
            append(text)
            append("', alreadySeenFullText=")
            append(alreadySeenFullText)
            append(", keywordMatchedInPostText=")
            append(keywordMatchedInPostText)
            append(", heading=")
            append(heading)
            append(", alreadySeenFullHeading=")
            append(alreadySeenFullHeading)
            append(", keywordMatchedInPostHeading=")
            append(keywordMatchedInPostHeading)
            append(")")
        }
    }
}