package com.likeminds.feed.android.core.delete.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class LMFeedDeleteExtras private constructor(
    @LMFeedDeleteType
    val entityType: Int,
    val postId: String,
    val commentId: String?,
    val parentCommentId: String?
) : Parcelable {

    class Builder {
        private var entityType: Int = 0
        private var postId: String = ""
        private var commentId: String? = null
        private var parentCommentId: String? = null

        fun entityType(@LMFeedDeleteType entityType: Int) = apply {
            this.entityType = entityType
        }

        fun postId(postId: String) = apply {
            this.postId = postId
        }

        fun commentId(commentId: String?) = apply {
            this.commentId = commentId
        }

        fun parentCommentId(parentCommentId: String?) = apply {
            this.parentCommentId = parentCommentId
        }

        fun build() = LMFeedDeleteExtras(
            entityType,
            postId,
            commentId,
            parentCommentId
        )
    }

    fun toBuilder(): Builder {
        return Builder().entityType(entityType)
            .postId(postId)
            .commentId(commentId)
            .parentCommentId(parentCommentId)
    }
}