package com.likeminds.feed.android.core.videofeed.adapter

import com.likeminds.feed.android.core.socialfeed.adapter.LMFeedPostAdapterListener
import com.likeminds.feed.android.core.utils.base.*
import com.likeminds.feed.android.core.videofeed.adapter.databinders.LMFeedItemPostVideoFeedCaughtUpViewDataBinder
import com.likeminds.feed.android.core.videofeed.adapter.databinders.LMFeedItemPostVideoFeedViewDataBinder

class LMFeedVideoFeedAdapter(
    private val postAdapterListener: LMFeedPostAdapterListener
) : LMFeedBaseRecyclerAdapter<LMFeedBaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): MutableList<LMFeedViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<LMFeedViewDataBinder<*, *>>(1)

        val itemPostVideoFeedViewDataBinder =
            LMFeedItemPostVideoFeedViewDataBinder(postAdapterListener)

        viewDataBinders.add(itemPostVideoFeedViewDataBinder)

        val itemPostVideoFeedCaughtUpViewDataBinder =
            LMFeedItemPostVideoFeedCaughtUpViewDataBinder(postAdapterListener)

        viewDataBinders.add(itemPostVideoFeedCaughtUpViewDataBinder)

        return viewDataBinders
    }
}