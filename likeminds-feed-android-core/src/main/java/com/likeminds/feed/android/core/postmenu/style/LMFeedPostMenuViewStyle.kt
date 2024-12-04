package com.likeminds.feed.android.core.postmenu.style

import androidx.annotation.ColorRes
import com.likeminds.feed.android.core.ui.widgets.labeliconcontainer.style.LMFeedLabelIconContainerViewStyle
import com.likeminds.feed.android.core.utils.LMFeedViewStyle

class LMFeedPostMenuViewStyle private constructor(
    //post menu item style
    val postMenuItemStyle: LMFeedLabelIconContainerViewStyle,
    //post menu background color
    @ColorRes val backgroundColor: Int?
) : LMFeedViewStyle {

    class Builder {
        private var postMenuItemStyle: LMFeedLabelIconContainerViewStyle =
            LMFeedLabelIconContainerViewStyle.Builder()
                .build()

        private var backgroundColor: Int? = null

        fun postMenuItemStyle(postMenuItemStyle: LMFeedLabelIconContainerViewStyle) = apply {
            this.postMenuItemStyle = postMenuItemStyle
        }

        fun backgroundColor(backgroundColor: Int?) = apply {
            this.backgroundColor = backgroundColor
        }

        fun build() = LMFeedPostMenuViewStyle(postMenuItemStyle, backgroundColor)
    }

    fun toBuilder(): Builder {
        return Builder().postMenuItemStyle(postMenuItemStyle)
            .backgroundColor(backgroundColor)
    }
}