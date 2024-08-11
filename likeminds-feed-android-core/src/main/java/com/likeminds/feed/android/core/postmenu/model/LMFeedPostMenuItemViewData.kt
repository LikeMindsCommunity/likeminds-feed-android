package com.likeminds.feed.android.core.postmenu.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import com.likeminds.feed.android.core.utils.base.LMFeedBaseViewType
import com.likeminds.feed.android.core.utils.base.model.ITEM_POST_MENU_ITEM
import kotlinx.parcelize.Parcelize

@Parcelize
class LMFeedPostMenuItemViewData private constructor(
    @LMFeedPostMenuItemId
    val id: Int,
    val title: String,
    @DrawableRes val icon: Int?
) : LMFeedBaseViewType, Parcelable {

    override val viewType: Int
        get() = ITEM_POST_MENU_ITEM

    class Builder {
        @LMFeedPostMenuItemId
        private var id: Int = DELETE_POST_MENU_ITEM_ID
        private var title: String = ""

        @DrawableRes
        private var icon: Int? = null

        fun id(@LMFeedPostMenuItemId id: Int) = apply { this.id = id }
        fun title(title: String) = apply { this.title = title }
        fun icon(icon: Int?) = apply { this.icon = icon }

        fun build() = LMFeedPostMenuItemViewData(
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