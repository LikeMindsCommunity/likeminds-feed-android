package com.likeminds.feed.android.ui.theme.model

import androidx.annotation.FontRes

// todo: add other theme parameters
class LMFeedSetThemeRequest private constructor(
    @FontRes
    val fontResource: Int?,
    val fontAssetsPath: String?
) {
    class Builder {
        @FontRes
        private var fontResource: Int? = null
        private var fontAssetsPath: String? = null

        fun fontResource(@FontRes fontResource: Int?) = apply { this.fontResource = fontResource }
        fun fontAssetsPath(fontAssetsPath: String?) = apply { this.fontAssetsPath = fontAssetsPath }

        fun build() = LMFeedSetThemeRequest(fontResource, fontAssetsPath)
    }

    fun toBuilder(): Builder {
        return Builder().fontResource(fontResource)
            .fontAssetsPath(fontAssetsPath)
    }
}