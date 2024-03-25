package com.likeminds.feed.android.core.post.create.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class LMFeedCreatePostExtras private constructor(
    val source: String?
) : Parcelable {
    class Builder {
        private var source: String? = null
        fun source(source: String?) = apply { this.source = source }

        fun build() = LMFeedCreatePostExtras(source)
    }

    fun toBuilder(): Builder {
        return Builder().source(source)
    }
}