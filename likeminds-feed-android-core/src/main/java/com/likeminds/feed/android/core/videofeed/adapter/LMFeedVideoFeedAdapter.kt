package com.likeminds.feed.android.core.videofeed.adapter

import com.likeminds.feed.android.core.utils.base.*
import com.likeminds.feed.android.core.videofeed.adapter.databinders.LMFeedItemPostVideoFeedViewDataBinder

class LMFeedVideoFeedAdapter(
    private val videoFeedAdapterListener: LMFeedVideoFeedAdapterListener
) : LMFeedBaseRecyclerAdapter<LMFeedBaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): MutableList<LMFeedViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<LMFeedViewDataBinder<*, *>>(1)

        val itemPostVideoFeedViewDataBinder =
            LMFeedItemPostVideoFeedViewDataBinder(videoFeedAdapterListener)

        viewDataBinders.add(itemPostVideoFeedViewDataBinder)

        return viewDataBinders
    }
}

interface LMFeedVideoFeedAdapterListener {

}