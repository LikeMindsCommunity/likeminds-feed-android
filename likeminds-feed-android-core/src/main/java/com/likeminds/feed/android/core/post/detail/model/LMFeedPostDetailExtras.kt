package com.likeminds.feed.android.core.post.detail.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class LMFeedPostDetailExtras private constructor(
    val postId: String,
    val commentId: String?,
    val isEditTextFocused: Boolean,
    val source: String?
) : Parcelable {

    class Builder {
        private var postId: String = ""
        private var isEditTextFocused: Boolean = false
        private var commentId: String? = null
        private var source: String? = null

        fun postId(postId: String) = apply { this.postId = postId }
        fun isEditTextFocused(isEditTextFocused: Boolean) =
            apply { this.isEditTextFocused = isEditTextFocused }

        fun commentId(commentId: String?) = apply { this.commentId = commentId }
        fun source(source: String?) = apply { this.source = source }

        fun build() = LMFeedPostDetailExtras(
            postId,
            commentId,
            isEditTextFocused,
            source
        )
    }

    fun toBuilder(): Builder {
        return Builder().postId(postId)
            .isEditTextFocused(isEditTextFocused)
            .commentId(commentId)
            .source(source)
    }
}