package com.likeminds.feed.android.core.post.edit.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class LMFeedEditPostExtras private constructor(
    val postId: String
) : Parcelable {

    class Builder {
        private var postId: String = ""

        fun postId(postId: String) = apply {
            this.postId = postId
        }

        fun build() = LMFeedEditPostExtras(postId)
    }

    fun toBuilder(): Builder {
        return Builder().postId(postId)
    }
}