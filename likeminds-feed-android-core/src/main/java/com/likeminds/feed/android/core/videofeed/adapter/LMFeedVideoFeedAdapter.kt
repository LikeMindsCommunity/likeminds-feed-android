package com.likeminds.feed.android.core.videofeed.adapter

import com.likeminds.feed.android.core.socialfeed.adapter.LMFeedPostAdapterListener
import com.likeminds.feed.android.core.utils.base.*
import com.likeminds.feed.android.core.utils.video.LMFeedPostVideoPreviewAutoPlayHelper
import com.likeminds.feed.android.core.videofeed.adapter.databinders.LMFeedItemPostVideoFeedViewDataBinder

class LMFeedVideoFeedAdapter(
    private val postAdapterListener: LMFeedPostAdapterListener,
    private val postVideoPreviewAutoPlayHelper: LMFeedPostVideoPreviewAutoPlayHelper
) : LMFeedBaseRecyclerAdapter<LMFeedBaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): MutableList<LMFeedViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<LMFeedViewDataBinder<*, *>>(1)

        val itemPostVideoFeedViewDataBinder =
            LMFeedItemPostVideoFeedViewDataBinder(
                postAdapterListener,
                postVideoPreviewAutoPlayHelper
            )

        viewDataBinders.add(itemPostVideoFeedViewDataBinder)

        return viewDataBinders
    }
}