package com.likeminds.feed.android.core.utils.base

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.likeminds.feed.android.core.socialfeed.adapter.LMFeedPostAdapterListener
import com.likeminds.feed.android.core.socialfeed.model.LMFeedPostViewData
import com.likeminds.feed.android.core.utils.base.model.LMFeedViewType

abstract class PostItemViewDataBinder<V : ViewDataBinding>(postAdapterListener: LMFeedPostAdapterListener) :
    LMFeedViewDataBinder<V, LMFeedPostViewData>() {

    @get:LMFeedViewType
    abstract override val viewType: Int

    override abstract fun createBinder(parent: ViewGroup): V

    override abstract fun bindData(binding: V, data: LMFeedPostViewData, position: Int)
}