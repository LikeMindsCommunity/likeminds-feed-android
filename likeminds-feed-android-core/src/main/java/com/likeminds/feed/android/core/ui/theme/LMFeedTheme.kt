package com.likeminds.feed.android.core.ui.theme

import androidx.annotation.*
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.ui.theme.model.LMFeedSetThemeRequest

object LMFeedTheme {
    private const val DEFAULT_POST_CHARACTER_LIMIT = 500
    const val DEFAULT_POST_MAX_LINES = 3
    const val DEFAULT_VISIBLE_DOCUMENTS_LIMIT = 3

    //font related
    @FontRes
    private var fontResource: Int? = R.font.lm_feed_roboto
    private var fontAssetsPath: String? = null

    //text link color
    @ColorRes
    private var textLinkColor: Int = R.color.lm_feed_pure_blue

    //button color
    @ColorRes
    private var buttonColor: Int = R.color.lm_feed_majorelle_blue

    //post character limits
    private var postCharacterLimit: Int = DEFAULT_POST_CHARACTER_LIMIT

    //notification related
    @DrawableRes
    private var notificationIcon: Int? = null

    /**
     * @param lmFeedSetThemeRequest - Request to set base theme
     * sets fonts, used throughout the app as base theme
     * */
    fun setTheme(lmFeedSetThemeRequest: LMFeedSetThemeRequest?) {
        if (lmFeedSetThemeRequest == null) {
            return
        }

        fontResource = lmFeedSetThemeRequest.fontResource

        lmFeedSetThemeRequest.fontAssetsPath?.let {
            fontAssetsPath = it
        }

        lmFeedSetThemeRequest.textLinkColor?.let {
            textLinkColor = it
        }

        lmFeedSetThemeRequest.buttonColor?.let {
            buttonColor = it
        }

        lmFeedSetThemeRequest.postCharacterLimit?.let {
            postCharacterLimit = it
        }

        notificationIcon = lmFeedSetThemeRequest.notificationIcon
    }

    //returns the pair of theme font resource and assets path
    fun getFontResources(): Pair<Int?, String?> {
        return Pair(fontResource, fontAssetsPath)
    }

    //returns the limit of characters in the post text view to see more
    fun getPostCharacterLimit(): Int {
        return postCharacterLimit
    }

    fun getNotificationIcon(): Int? {
        return notificationIcon
    }

    fun getTextLinkColor(): Int {
        return textLinkColor
    }

    fun getButtonColor(): Int {
        return buttonColor
    }
}