package com.likeminds.feed.android.core.universalfeed.adapter.databinders

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feed.android.core.databinding.LmFeedItemSelectedFilterTopicBinding
import com.likeminds.feed.android.core.topics.model.LMFeedTopicViewData
import com.likeminds.feed.android.core.ui.base.styles.setStyle
import com.likeminds.feed.android.core.universalfeed.adapter.LMFeedUniversalSelectedTopicAdapterListener
import com.likeminds.feed.android.core.utils.LMFeedStyleTransformer
import com.likeminds.feed.android.core.utils.base.LMFeedViewDataBinder
import com.likeminds.feed.android.core.utils.base.model.ITEM_TOPIC

class LMFeedItemSelectedFilterTopicViewDataBinder(
    private val listener: LMFeedUniversalSelectedTopicAdapterListener
) : LMFeedViewDataBinder<LmFeedItemSelectedFilterTopicBinding, LMFeedTopicViewData>() {

    override val viewType: Int
        get() = ITEM_TOPIC

    override fun createBinder(parent: ViewGroup): LmFeedItemSelectedFilterTopicBinding {
        val binding = LmFeedItemSelectedFilterTopicBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        binding.apply {
            setListeners(this)

            //set styles
            val topicSelectorBarStyle =
                LMFeedStyleTransformer.universalFeedFragmentViewStyle.topicSelectorBarStyle

            tvTopicName.setStyle(topicSelectorBarStyle.selectedTopicTextStyle)
            ivCross.setStyle(topicSelectorBarStyle.removeSelectedTopicIconStyle)

            return this
        }
    }

    override fun bindData(
        binding: LmFeedItemSelectedFilterTopicBinding,
        data: LMFeedTopicViewData,
        position: Int
    ) {
        binding.apply {
            this.position = position
            selectedTopicViewData = data

            tvTopicName.text = data.name
        }
    }

    private fun setListeners(binding: LmFeedItemSelectedFilterTopicBinding) {
        binding.apply {
            ivCross.setOnClickListener {
                val topicViewData = selectedTopicViewData ?: return@setOnClickListener
                listener.onCleared(position, topicViewData)
            }
        }
    }
}