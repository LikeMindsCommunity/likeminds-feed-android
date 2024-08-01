package com.likeminds.feed.android.core.videofeed.adapter.databinders

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feed.android.core.databinding.LmFeedItemPostVideoFeedBinding
import com.likeminds.feed.android.core.socialfeed.model.LMFeedPostViewData
import com.likeminds.feed.android.core.socialfeed.util.LMFeedPostBinderUtils
import com.likeminds.feed.android.core.utils.LMFeedStyleTransformer
import com.likeminds.feed.android.core.utils.base.LMFeedViewDataBinder
import com.likeminds.feed.android.core.utils.base.model.ITEM_POST_VIDEO_FEED
import com.likeminds.feed.android.core.utils.video.LMFeedPostVideoPreviewAutoPlayHelper
import com.likeminds.feed.android.core.videofeed.adapter.LMFeedVideoFeedAdapterListener

class LMFeedItemPostVideoFeedViewDataBinder(
    private val videoFeedAdapterListener: LMFeedVideoFeedAdapterListener,
    private val postVideoPreviewAutoPlayHelper: LMFeedPostVideoPreviewAutoPlayHelper
) : LMFeedViewDataBinder<LmFeedItemPostVideoFeedBinding, LMFeedPostViewData>() {

    override val viewType: Int
        get() = ITEM_POST_VIDEO_FEED

    override fun createBinder(parent: ViewGroup): LmFeedItemPostVideoFeedBinding {
        val binding = LmFeedItemPostVideoFeedBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        binding.apply {
            LMFeedPostBinderUtils.customizePostHeaderView(postHeader)

            LMFeedPostBinderUtils.customizePostContentView(tvPostContent)

            LMFeedPostBinderUtils.customizePostActionVerticalView(postActionView)

            //set video media style to post video view
            val postVerticalVideoMediaStyle =
                LMFeedStyleTransformer.postViewStyle.postMediaViewStyle.postVerticalVideoMediaStyle
                    ?: return@apply

            postVideoView.setStyle(postVerticalVideoMediaStyle)
        }

        return binding
    }

    override fun bindData(
        binding: LmFeedItemPostVideoFeedBinding,
        data: LMFeedPostViewData,
        position: Int
    ) {
        Log.d("PUI", "bindData: ")
        postVideoPreviewAutoPlayHelper.playVideoInView(
            binding.postVideoView,
            data.mediaViewData.attachments.first().attachmentMeta.url
        )
    }
}