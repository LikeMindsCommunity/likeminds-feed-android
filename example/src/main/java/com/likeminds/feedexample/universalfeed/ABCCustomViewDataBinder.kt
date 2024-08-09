package com.likeminds.feedexample.universalfeed

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feed.android.core.post.model.CUSTOM_WIDGET
import com.likeminds.feed.android.core.universalfeed.model.LMFeedPostViewData
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.hide
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.show
import com.likeminds.feed.android.core.utils.base.PostItemViewDataBinder
import com.likeminds.feed.android.core.utils.base.model.ITEM_POST_CUSTOM_WIDGET
import com.likeminds.feedexample.databinding.ItemAbcCustomViewDataBinding

class ABCCustomViewDataBinder : PostItemViewDataBinder<ItemAbcCustomViewDataBinding>() {
    override val viewType: Int
        get() = ITEM_POST_CUSTOM_WIDGET

    override fun createBinder(parent: ViewGroup): ItemAbcCustomViewDataBinding {
        return ItemAbcCustomViewDataBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun bindData(
        binding: ItemAbcCustomViewDataBinding,
        data: LMFeedPostViewData,
        position: Int
    ) {
        val customWidgetData = data.mediaViewData.attachments.find {
            it.attachmentType == CUSTOM_WIDGET
        }
        binding.apply {
            if (customWidgetData != null) {
                val widgetData = customWidgetData.attachmentMeta.widgetViewData
                if (widgetData != null) {
                    val metadata = widgetData.metadata
                    val abc = metadata.getString("abc")
                    val def = metadata.getString("787")
                    tvAbc.text = abc
                    tvAbc.show()
                    tvDef.text = def
                    tvDef.show()
                } else {
                    tvAbc.text = "Default ABC null"
                    tvDef.text = "Default DEF null"
                }
            } else {
                tvAbc.hide()
                tvDef.hide()
            }
        }
    }
}