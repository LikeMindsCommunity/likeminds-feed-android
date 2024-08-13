package com.likeminds.feed.android.core.postmenu.adapter

import com.likeminds.feed.android.core.postmenu.model.LMFeedPostMenuItemViewData
import com.likeminds.feed.android.core.postmenu.view.LMFeedPostMenuItemViewDataBinder
import com.likeminds.feed.android.core.utils.base.*

class LMFeedPostMenuAdapter(
    private val listener: LMFeedPostMenuAdapterListener
) : LMFeedBaseRecyclerAdapter<LMFeedBaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): MutableList<LMFeedViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<LMFeedViewDataBinder<*, *>>(1)

        val itemPostMenuViewDataBinder = LMFeedPostMenuItemViewDataBinder(listener)
        viewDataBinders.add(itemPostMenuViewDataBinder)

        return viewDataBinders
    }
}

interface LMFeedPostMenuAdapterListener {
    fun onPostMenuItemClicked(position: Int, menuItem: LMFeedPostMenuItemViewData) {
        //triggered when the user clicks on the post menu item
    }
}