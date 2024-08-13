package com.likeminds.feed.android.core.postmenu.view

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feed.android.core.databinding.LmFeedItemPostMenuBinding
import com.likeminds.feed.android.core.postmenu.adapter.LMFeedPostMenuAdapterListener
import com.likeminds.feed.android.core.postmenu.model.LMFeedPostMenuItemViewData
import com.likeminds.feed.android.core.utils.LMFeedStyleTransformer
import com.likeminds.feed.android.core.utils.base.LMFeedViewDataBinder
import com.likeminds.feed.android.core.utils.base.model.ITEM_POST_MENU_ITEM

class LMFeedPostMenuItemViewDataBinder(
    private val listener: LMFeedPostMenuAdapterListener
) : LMFeedViewDataBinder<LmFeedItemPostMenuBinding, LMFeedPostMenuItemViewData>() {

    override val viewType: Int
        get() = ITEM_POST_MENU_ITEM

    override fun createBinder(parent: ViewGroup): LmFeedItemPostMenuBinding {
        val binding = LmFeedItemPostMenuBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        binding.menuContainer.setStyle(LMFeedStyleTransformer.postMenuViewStyle.postMenuItemStyle)

        setClickListeners(binding)

        return binding
    }

    override fun bindData(
        binding: LmFeedItemPostMenuBinding,
        data: LMFeedPostMenuItemViewData,
        position: Int
    ) {
        binding.apply {
            this.position = position
            menuItemViewData = data

            data.icon?.let { menuContainer.setContainerIcon(it) }
            menuContainer.setContainerLabel(data.title)
        }
    }

    //sets click listeners to the post menu item view
    private fun setClickListeners(binding: LmFeedItemPostMenuBinding) {
        binding.apply {
            root.setOnClickListener {
                val menu = this.menuItemViewData ?: return@setOnClickListener
                listener.onPostMenuItemClicked(position, menu)
            }
        }
    }
}