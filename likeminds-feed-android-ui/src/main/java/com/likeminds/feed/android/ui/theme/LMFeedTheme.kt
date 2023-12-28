package com.likeminds.feed.android.ui.theme

import androidx.annotation.FontRes
import com.likeminds.feed.android.ui.theme.model.LMFeedSetThemeRequest

object LMFeedTheme {
    @FontRes
    private var fontResource: Int? = null
    private var fontAssetsPath: String? = null

    /**
     * @param lmFeedSetThemeRequest - Request to set base theme
     * sets fonts, used throughout the app as base theme
     * */
    fun setTheme(lmFeedSetThemeRequest: LMFeedSetThemeRequest) {
        fontResource = lmFeedSetThemeRequest.fontResource
        fontAssetsPath = lmFeedSetThemeRequest.fontAssetsPath
    }

    fun getFontResources(): Pair<Int?, String?> {
        return Pair(fontResource, fontAssetsPath)
    }
}