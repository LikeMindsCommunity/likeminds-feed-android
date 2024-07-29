package com.likeminds.feed.android.core.videofeed.adapter.databinders

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feed.android.core.databinding.LmFeedItemPostVideoFeedBinding
import com.likeminds.feed.android.core.socialfeed.model.LMFeedPostViewData
import com.likeminds.feed.android.core.utils.base.LMFeedViewDataBinder
import com.likeminds.feed.android.core.utils.base.model.ITEM_POST_VIDEO_FEED
import com.likeminds.feed.android.core.videofeed.adapter.LMFeedVideoFeedAdapterListener

class LMFeedItemPostVideoFeedViewDataBinder(
    private val videoFeedAdapterListener: LMFeedVideoFeedAdapterListener
) : LMFeedViewDataBinder<LmFeedItemPostVideoFeedBinding, LMFeedPostViewData>() {

    override val viewType: Int
        get() = ITEM_POST_VIDEO_FEED

    override fun createBinder(parent: ViewGroup): LmFeedItemPostVideoFeedBinding {
        val binding = LmFeedItemPostVideoFeedBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return binding
    }

    override fun bindData(
        binding: LmFeedItemPostVideoFeedBinding,
        data: LMFeedPostViewData,
        position: Int
    ) {
        //todo:
    }
}