package com.likeminds.feed.android.core.socialfeed.model

import com.likeminds.feed.android.core.post.detail.model.LMFeedCommentViewData

class LMFeedPostActionViewData private constructor(
    val likesCount: Int,
    val commentsCount: Int,
    val isSaved: Boolean,
    val isLiked: Boolean,
    val replies: MutableList<LMFeedCommentViewData>,
) {

    class Builder {
        private var likesCount: Int = 0
        private var commentsCount: Int = 0
        private var isSaved: Boolean = false
        private var isLiked: Boolean = false
        private var replies: MutableList<LMFeedCommentViewData> = mutableListOf()

        fun likesCount(likesCount: Int) = apply { this.likesCount = likesCount }
        fun commentsCount(commentsCount: Int) = apply { this.commentsCount = commentsCount }
        fun isSaved(isSaved: Boolean) = apply { this.isSaved = isSaved }
        fun isLiked(isLiked: Boolean) = apply { this.isLiked = isLiked }
        fun replies(replies: MutableList<LMFeedCommentViewData>) = apply { this.replies = replies }

        fun build() = LMFeedPostActionViewData(
            likesCount,
            commentsCount,
            isSaved,
            isLiked,
            replies
        )
    }

    fun toBuilder(): Builder {
        return Builder()
            .likesCount(likesCount)
            .commentsCount(commentsCount)
            .isSaved(isSaved)
            .isLiked(isLiked)
            .replies(replies)
    }

    override fun toString(): String {
        return buildString {
            append("LMFeedPostFooterViewData(likesCount=")
            append(likesCount)
            append(", commentsCount=")
            append(commentsCount)
            append(", isSaved=")
            append(isSaved)
            append(", isLiked=")
            append(isLiked)
            append(", replies=")
            append(replies)
            append(")")
        }
    }
}