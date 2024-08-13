package com.likeminds.feed.android.core.postmenu.view

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.*
import com.likeminds.feed.android.core.postmenu.adapter.LMFeedPostMenuAdapter
import com.likeminds.feed.android.core.postmenu.adapter.LMFeedPostMenuAdapterListener
import com.likeminds.feed.android.core.postmenu.model.LMFeedPostMenuItemViewData

class LMFeedPostMenuListView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    val linearLayoutManager: LinearLayoutManager

    private lateinit var postMenuAdapter: LMFeedPostMenuAdapter

    init {
        setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(context)
        layoutManager = linearLayoutManager

        if (itemAnimator is SimpleItemAnimator)
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
    }

    //sets the adapter with the provided adapter listener
    fun setAdapter(listener: LMFeedPostMenuAdapterListener) {
        postMenuAdapter = LMFeedPostMenuAdapter(listener)
        adapter = postMenuAdapter
    }

    //replaces the whole list in adapter
    fun replaceMenuItems(menuItems: List<LMFeedPostMenuItemViewData>) {
        postMenuAdapter.replace(menuItems)
    }
}