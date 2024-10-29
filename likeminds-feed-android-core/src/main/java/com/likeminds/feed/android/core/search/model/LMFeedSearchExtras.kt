package com.likeminds.feed.android.core.search.model

import android.os.Parcelable
import com.likeminds.likemindsfeed.search.model.SearchType
import kotlinx.parcelize.Parcelize

@Parcelize
class LMFeedSearchExtras private constructor(
    val searchType: SearchType
) : Parcelable {

    class Builder {
        private var searchType: SearchType = SearchType.TEXT

        fun searchType(searchType: SearchType) = apply {
            this.searchType = searchType
        }

        fun build() = LMFeedSearchExtras(searchType)
    }

    fun toBuilder(): Builder {
        return Builder().searchType(searchType)
    }
}