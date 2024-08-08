package com.likeminds.feed.android.core.socialfeed.adapter

import com.likeminds.feed.android.core.socialfeed.adapter.databinders.postmultiplemedia.LMFeedItemMultipleMediaImageViewDataBinder
import com.likeminds.feed.android.core.socialfeed.adapter.databinders.postmultiplemedia.LMFeedItemMultipleMediaVideoViewDataBinder
import com.likeminds.feed.android.core.utils.base.*

class LMFeedMultipleMediaPostAdapter(
    private val parentPosition: Int,
    private val listener: LMFeedPostAdapterListener,
    private val isMediaRemovable: Boolean = false
) : LMFeedBaseRecyclerAdapter<LMFeedBaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): MutableList<LMFeedViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<LMFeedViewDataBinder<*, *>>(2)

        val multipleMediaImageBinder = LMFeedItemMultipleMediaImageViewDataBinder(
            parentPosition,
            listener,
            isMediaRemovable
        )
        viewDataBinders.add(multipleMediaImageBinder)

        val multipleMediaVideoBinder =
            LMFeedItemMultipleMediaVideoViewDataBinder(
                parentPosition,
                listener,
                isMediaRemovable
            )
        viewDataBinders.add(multipleMediaVideoBinder)

        return viewDataBinders
    }
}