package com.likeminds.feed.android.core.post.edit.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class LMFeedEditPostDisabledTopicsDialogExtras private constructor(
    val title: String,
    val subtitle: String
) : Parcelable {
    class Builder {
        private var title: String = ""
        private var subtitle: String = ""

        fun title(title: String) = apply {
            this.title = title
        }

        fun subtitle(subtitle: String) = apply {
            this.subtitle = subtitle
        }

        fun build() = LMFeedEditPostDisabledTopicsDialogExtras(title, subtitle)
    }

    fun toBuilder(): Builder {
        return Builder().title(title)
            .subtitle(subtitle)
    }
}