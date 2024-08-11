package com.likeminds.feed.android.core.ui.widgets.overflowmenu.view

import android.content.Context
import android.view.Menu
import android.view.View
import android.widget.PopupMenu
import com.likeminds.feed.android.core.postmenu.model.LMFeedPostMenuItemViewData
import com.likeminds.feed.android.core.utils.listeners.LMFeedMenuItemClickListener

class LMFeedOverflowMenu(context: Context, view: View) : PopupMenu(context, view) {

    /**
     * Sets click listener on the menu icon
     *
     * @param listener [LMFeedMenuItemClickListener] interface to have click listener for menu item
     */
    fun setMenuItemClickListener(listener: LMFeedMenuItemClickListener) {
        setOnMenuItemClickListener {
            listener.onMenuItemClicked(it.itemId)
            true
        }
    }

    /**
     * Sets menu items to the popup menu
     *
     * @param menuItems list of [LMFeedPostMenuItemViewData]
     */
    fun addMenuItems(
        menuItems: List<LMFeedPostMenuItemViewData>
    ) {
        menuItems.forEach { menuItem ->
            menu.add(
                Menu.NONE,
                menuItem.id,
                Menu.NONE,
                menuItem.title
            )
        }
    }
}