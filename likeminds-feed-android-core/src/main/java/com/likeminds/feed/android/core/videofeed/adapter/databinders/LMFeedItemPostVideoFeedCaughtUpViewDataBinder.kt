package com.likeminds.feed.android.core.videofeed.adapter.databinders

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feed.android.core.databinding.LmFeedItemPostVideoFeedCaughtUpBinding
import com.likeminds.feed.android.core.socialfeed.adapter.LMFeedPostAdapterListener
import com.likeminds.feed.android.core.ui.base.styles.setStyle
import com.likeminds.feed.android.core.utils.LMFeedStyleTransformer
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.hide
import com.likeminds.feed.android.core.utils.base.LMFeedViewDataBinder
import com.likeminds.feed.android.core.utils.base.model.ITEM_POST_VIDEO_FEED_CAUGHT_UP
import com.likeminds.feed.android.core.videofeed.model.LMFeedCaughtUpViewData

class LMFeedItemPostVideoFeedCaughtUpViewDataBinder(
    private val postAdapterListener: LMFeedPostAdapterListener
) : LMFeedViewDataBinder<LmFeedItemPostVideoFeedCaughtUpBinding, LMFeedCaughtUpViewData>() {

    override val viewType: Int
        get() = ITEM_POST_VIDEO_FEED_CAUGHT_UP

    override fun createBinder(parent: ViewGroup): LmFeedItemPostVideoFeedCaughtUpBinding {
        val binding = LmFeedItemPostVideoFeedCaughtUpBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        binding.apply {
            val videoFeedFragmentStyle = LMFeedStyleTransformer.videoFeedFragmentViewStyle

            if (videoFeedFragmentStyle.videoFeedCaughtUpIcon != null) {
                ivTick.setStyle(videoFeedFragmentStyle.videoFeedCaughtUpIcon)
            } else {
                ivTick.hide()
            }

            if (videoFeedFragmentStyle.videoFeedCaughtUpTitleStyle != null) {
                tvTitle.setStyle(videoFeedFragmentStyle.videoFeedCaughtUpTitleStyle)
            } else {
                ivTick.hide()
            }

            if (videoFeedFragmentStyle.videoFeedCaughtUpSubtitleStyle != null) {
                tvSubtitle.setStyle(videoFeedFragmentStyle.videoFeedCaughtUpSubtitleStyle)
            } else {
                ivTick.hide()
            }

            setClickListener(this)
        }

        return binding
    }

    override fun bindData(
        binding: LmFeedItemPostVideoFeedCaughtUpBinding,
        data: LMFeedCaughtUpViewData,
        position: Int
    ) {
    }

    //sets the required click listeners to the binding
    private fun setClickListener(binding: LmFeedItemPostVideoFeedCaughtUpBinding) {
        binding.tvSubtitle.setOnClickListener {
            postAdapterListener.onPostVideoFeedCaughtUpClicked()
        }
    }
}