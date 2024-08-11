package com.likeminds.feed.android.core.post.detail.model

import com.likeminds.feed.android.core.postmenu.model.LMFeedPostMenuItemViewData
import com.likeminds.feed.android.core.utils.base.LMFeedBaseViewType
import com.likeminds.feed.android.core.utils.base.model.ITEM_COMMENT
import com.likeminds.feed.android.core.utils.base.model.ITEM_REPLY
import com.likeminds.feed.android.core.utils.user.LMFeedUserViewData

class LMFeedCommentViewData private constructor(
    val id: String,
    val postId: String,
    val isLiked: Boolean,
    val isEdited: Boolean,
    val userId: String,
    val text: String,
    val level: Int,
    val likesCount: Int,
    val repliesCount: Int,
    val user: LMFeedUserViewData,
    val createdAt: Long,
    val updatedAt: Long,
    val menuItems: List<LMFeedPostMenuItemViewData>,
    val replies: MutableList<LMFeedCommentViewData>,
    val parentId: String?,
    val parentComment: LMFeedCommentViewData?,
    val alreadySeenFullContent: Boolean?,
    val fromCommentLiked: Boolean,
    val fromCommentEdited: Boolean,
    val uuid: String,
    val tempId: String?,
) : LMFeedBaseViewType {

    override val viewType: Int
        get() = when (level) {
            0 -> ITEM_COMMENT
            else -> ITEM_REPLY
        }

    class Builder {
        private var id: String = ""
        private var postId: String = ""
        private var isLiked: Boolean = false
        private var isEdited: Boolean = false
        private var userId: String = ""
        private var text: String = ""
        private var level: Int = 0
        private var likesCount: Int = 0
        private var repliesCount: Int = 0
        private var user: LMFeedUserViewData = LMFeedUserViewData.Builder().build()
        private var createdAt: Long = 0
        private var updatedAt: Long = 0
        private var menuItems: List<LMFeedPostMenuItemViewData> = listOf()
        private var replies: MutableList<LMFeedCommentViewData> = mutableListOf()
        private var parentId: String? = null
        private var parentComment: LMFeedCommentViewData? = null
        private var alreadySeenFullContent: Boolean? = null
        private var fromCommentLiked: Boolean = false
        private var fromCommentEdited: Boolean = false
        private var uuid: String = ""
        private var tempId: String? = null

        fun id(id: String) = apply { this.id = id }
        fun postId(postId: String) = apply { this.postId = postId }
        fun isLiked(isLiked: Boolean) = apply { this.isLiked = isLiked }
        fun isEdited(isEdited: Boolean) = apply { this.isEdited = isEdited }
        fun userId(userId: String) = apply { this.userId = userId }
        fun text(text: String) = apply { this.text = text }
        fun level(level: Int) = apply { this.level = level }
        fun likesCount(likesCount: Int) = apply { this.likesCount = likesCount }
        fun repliesCount(repliesCount: Int) = apply { this.repliesCount = repliesCount }
        fun user(user: LMFeedUserViewData) = apply { this.user = user }
        fun createdAt(createdAt: Long) = apply { this.createdAt = createdAt }
        fun updatedAt(updatedAt: Long) = apply { this.updatedAt = updatedAt }
        fun menuItems(menuItems: List<LMFeedPostMenuItemViewData>) =
            apply { this.menuItems = menuItems }

        fun parentId(parentId: String?) = apply { this.parentId = parentId }
        fun parentComment(parentComment: LMFeedCommentViewData?) =
            apply { this.parentComment = parentComment }

        fun replies(replies: MutableList<LMFeedCommentViewData>) =
            apply { this.replies = replies }

        fun alreadySeenFullContent(alreadySeenFullContent: Boolean?) =
            apply { this.alreadySeenFullContent = alreadySeenFullContent }

        fun fromCommentLiked(fromCommentLiked: Boolean) =
            apply { this.fromCommentLiked = fromCommentLiked }

        fun fromCommentEdited(fromCommentEdited: Boolean) =
            apply { this.fromCommentEdited = fromCommentEdited }

        fun uuid(uuid: String) = apply { this.uuid = uuid }
        fun tempId(tempId: String?) = apply { this.tempId = tempId }

        fun build() = LMFeedCommentViewData(
            id,
            postId,
            isLiked,
            isEdited,
            userId,
            text,
            level,
            likesCount,
            repliesCount,
            user,
            createdAt,
            updatedAt,
            menuItems,
            replies,
            parentId,
            parentComment,
            alreadySeenFullContent,
            fromCommentLiked,
            fromCommentEdited,
            uuid,
            tempId
        )
    }

    fun toBuilder(): Builder {
        return Builder().id(id)
            .postId(postId)
            .isLiked(isLiked)
            .isEdited(isEdited)
            .userId(userId)
            .text(text)
            .level(level)
            .likesCount(likesCount)
            .repliesCount(repliesCount)
            .user(user)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .menuItems(menuItems)
            .replies(replies)
            .parentId(parentId)
            .parentComment(parentComment)
            .alreadySeenFullContent(alreadySeenFullContent)
            .fromCommentLiked(fromCommentLiked)
            .fromCommentEdited(fromCommentEdited)
            .uuid(uuid)
            .tempId(tempId)
    }

    override fun toString(): String {
        return buildString {
            append("LMFeedCommentViewData(id='")
            append(id)
            append("', postId='")
            append(postId)
            append("', isLiked=")
            append(isLiked)
            append(", isEdited=")
            append(isEdited)
            append(", userId='")
            append(userId)
            append("', text='")
            append(text)
            append("', level=")
            append(level)
            append(", likesCount=")
            append(likesCount)
            append(", repliesCount=")
            append(repliesCount)
            append(", user=")
            append(user)
            append(",createdAt=")
            append(createdAt)
            append(", updatedAt=")
            append(updatedAt)
            append(",")
            append(" menuItems=")
            append(menuItems)
            append(", replies=")
            append(replies)
            append(", parentId=")
            append(parentId)
            append(", parentComment=")
            append(parentComment)
            append(", alreadySeenFullContent=")
            append(alreadySeenFullContent)
            append(", fromCommentLiked=")
            append(fromCommentLiked)
            append(", fromCommentEdited=")
            append(fromCommentEdited)
            append(", uuid='")
            append(uuid)
            append("', tempId='")
            append(tempId)
            append("')")
        }
    }
}