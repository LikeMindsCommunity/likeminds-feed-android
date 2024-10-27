package com.likeminds.feed.android.core.ui.theme

import androidx.annotation.*
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.ui.theme.model.LMFeedAppearanceRequest

object LMFeedAppearance {
    private const val DEFAULT_POST_CHARACTER_LIMIT = 500
    private const val DEFAULT_POST_HEADING_LIMIT = 200
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

    //post heading limit
    private var postHeadingLimit: Int = DEFAULT_POST_HEADING_LIMIT

    //notification related
    @DrawableRes
    private var notificationIcon: Int? = null

    /**
     * @param lmFeedAppearanceRequest - Request to set base appearance
     * sets fonts, used throughout the app as base lmFeedAppearance
     * */
    fun setAppearance(lmFeedAppearanceRequest: LMFeedAppearanceRequest?) {
        if (lmFeedAppearanceRequest == null) {
            return
        }

        fontResource = lmFeedAppearanceRequest.fontResource

        lmFeedAppearanceRequest.fontAssetsPath?.let {
            fontAssetsPath = it
        }

        lmFeedAppearanceRequest.textLinkColor?.let {
            textLinkColor = it
        }

        lmFeedAppearanceRequest.buttonColor?.let {
            buttonColor = it
        }

        lmFeedAppearanceRequest.postCharacterLimit?.let {
            postCharacterLimit = it
        }

        lmFeedAppearanceRequest.postHeadingLimit?.let {
            postHeadingLimit = it
        }

        notificationIcon = lmFeedAppearanceRequest.notificationIcon
    }

    //returns the pair of appearance font resource and assets path
    fun getFontResources(): Pair<Int?, String?> {
        return Pair(fontResource, fontAssetsPath)
    }

    //returns the limit of characters in the post text view to see more
    fun getPostCharacterLimit(): Int {
        return postCharacterLimit
    }

    //returns the limit of characters in the post heading view
    fun getPostHeadingLimit(): Int {
        return postHeadingLimit
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