package com.likeminds.feed.android.core.socialfeed.model

import com.likeminds.feed.android.core.postmenu.model.LMFeedPostMenuItemViewData
import com.likeminds.feed.android.core.utils.user.LMFeedUserViewData

class LMFeedPostHeaderViewData private constructor(
    val isEdited: Boolean,
    val isPinned: Boolean,
    val userId: String,
    val user: LMFeedUserViewData,
    val createdAt: Long,
    val updatedAt: Long,
    val menuItems: List<LMFeedPostMenuItemViewData>
) {

    class Builder {
        private var isEdited: Boolean = false
        private var isPinned: Boolean = false
        private var userId: String = ""
        private var user: LMFeedUserViewData = LMFeedUserViewData.Builder().build()
        private var createdAt: Long = 0L
        private var updatedAt: Long = 0L
        private var menuItems: List<LMFeedPostMenuItemViewData> = emptyList()

        fun isEdited(isEdited: Boolean) = apply { this.isEdited = isEdited }
        fun isPinned(isPinned: Boolean) = apply { this.isPinned = isPinned }
        fun userId(userId: String) = apply { this.userId = userId }
        fun user(user: LMFeedUserViewData) = apply { this.user = user }
        fun createdAt(createdAt: Long) = apply { this.createdAt = createdAt }
        fun updatedAt(updatedAt: Long) = apply { this.updatedAt = updatedAt }
        fun menuItems(menuItems: List<LMFeedPostMenuItemViewData>) =
            apply { this.menuItems = menuItems }

        fun build() = LMFeedPostHeaderViewData(
            isEdited,
            isPinned,
            userId,
            user,
            createdAt,
            updatedAt,
            menuItems
        )
    }

    fun toBuilder(): Builder {
        return Builder()
            .isEdited(isEdited)
            .isPinned(isPinned)
            .userId(userId)
            .user(user)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .menuItems(menuItems)
    }

    override fun toString(): String {
        return buildString {
            append("LMFeedPostHeaderViewData(isEdited=")
            append(isEdited)
            append(", isPinned=")
            append(isPinned)
            append(", userId='")
            append(userId)
            append("', user=")
            append(user)
            append(", createdAt=")
            append(createdAt)
            append(", updatedAt=")
            append(updatedAt)
            append(", menuItems=")
            append(menuItems)
            append(")")
        }
    }
}