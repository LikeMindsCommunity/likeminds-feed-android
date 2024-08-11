package com.likeminds.feed.android.core.socialfeed.adapter

import com.likeminds.feed.android.core.socialfeed.adapter.databinders.LMFeedItemSelectedFilterTopicViewDataBinder
import com.likeminds.feed.android.core.topics.model.LMFeedTopicViewData
import com.likeminds.feed.android.core.utils.base.*

class LMFeedSelectedTopicAdapter(
    private val listener: LMFeedSelectedTopicAdapterListener
) : LMFeedBaseRecyclerAdapter<LMFeedBaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): MutableList<LMFeedViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<LMFeedViewDataBinder<*, *>>(1)

        val itemSelectedTopicViewDataBinder =
            LMFeedItemSelectedFilterTopicViewDataBinder(listener)
        viewDataBinders.add(itemSelectedTopicViewDataBinder)

        return viewDataBinders
    }
}

interface LMFeedSelectedTopicAdapterListener {
    fun onTopicRemoved(position: Int, topicViewData: LMFeedTopicViewData) {
        //triggered when the user removes a selected topic filter
    }
}