package com.likeminds.feed.android.core.universalfeed.model

import com.likeminds.feed.android.core.post.detail.model.LMFeedCommentViewData

class LMFeedPostFooterViewData private constructor(
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

        fun build() = LMFeedPostFooterViewData(
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
}