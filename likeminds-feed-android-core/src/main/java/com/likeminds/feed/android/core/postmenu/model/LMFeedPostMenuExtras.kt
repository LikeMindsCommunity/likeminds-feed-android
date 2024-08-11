package com.likeminds.feed.android.core.postmenu.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class LMFeedPostMenuExtras private constructor(
    val postId: String,
    val menuItems: List<LMFeedPostMenuItemViewData>
) : Parcelable {

    class Builder {
        private var postId: String = ""
        private var menuItems: List<LMFeedPostMenuItemViewData> = emptyList()

        fun postId(postId: String) = apply {
            this.postId = postId
        }

        fun menuItems(menuItems: List<LMFeedPostMenuItemViewData>) = apply {
            this.menuItems = menuItems
        }

        fun build() = LMFeedPostMenuExtras(postId, menuItems)
    }

    fun toBuilder(): Builder {
        return Builder().postId(postId)
            .menuItems(menuItems)
    }
}