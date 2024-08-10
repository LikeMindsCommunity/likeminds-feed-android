package com.likeminds.feed.android.core.overflowmenu.model

import androidx.annotation.DrawableRes
import com.likeminds.feed.android.core.utils.base.LMFeedBaseViewType
import com.likeminds.feed.android.core.utils.base.model.ITEM_OVERFLOW_MENU_ITEM

class LMFeedOverflowMenuItemViewData private constructor(
    @LMFeedOverflowMenuItemId
    val id: Int,
    val title: String,
    @DrawableRes val icon: Int?
) : LMFeedBaseViewType {

    override val viewType: Int
        get() = ITEM_OVERFLOW_MENU_ITEM

    class Builder {
        @LMFeedOverflowMenuItemId
        private var id: Int = DELETE_POST_MENU_ITEM_ID
        private var title: String = ""

        @DrawableRes
        private var icon: Int? = null

        fun id(@LMFeedOverflowMenuItemId id: Int) = apply { this.id = id }
        fun title(title: String) = apply { this.title = title }
        fun icon(icon: Int?) = apply { this.icon = icon }

        fun build() = LMFeedOverflowMenuItemViewData(
            id,
            title,
            icon
        )
    }

    fun toBuilder(): Builder {
        return Builder().id(id)
            .title(title)
            .icon(icon)
    }

    override fun toString(): String {
        return buildString {
            append("LMOverflowMenuItemViewData(id=")
            append(id)
            append(", title='")
            append(title)
            append("', icon=")
            append(icon)
            append(")")
        }
    }
}