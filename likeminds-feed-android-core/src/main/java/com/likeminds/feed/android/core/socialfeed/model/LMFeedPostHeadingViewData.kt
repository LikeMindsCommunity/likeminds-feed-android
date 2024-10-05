package com.likeminds.feed.android.core.socialfeed.model

import com.likeminds.feed.android.core.utils.base.LMFeedBaseViewType
import com.likeminds.feed.android.core.utils.base.model.ITEM_POST_HEADING

class LMFeedPostHeadingViewData private constructor(
    val heading: String?
) : LMFeedBaseViewType {

    override val viewType: Int
        get() = ITEM_POST_HEADING

    class Builder {
        private var heading: String? = null

        fun heading(heading: String?) = apply {
            this.heading = heading
        }

        fun build() = LMFeedPostHeadingViewData(heading)
    }

    fun toBuilder(): Builder {
        return Builder().heading(heading)
    }

    override fun toString(): String {
        return "LMFeedPostContentViewData(heading=$heading)"
    }
}