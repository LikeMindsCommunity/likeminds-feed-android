package com.likeminds.feed.android.core.socialfeed.adapter.databinders

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feed.android.core.databinding.LmFeedItemSelectedFilterTopicBinding
import com.likeminds.feed.android.core.topics.model.LMFeedTopicViewData
import com.likeminds.feed.android.core.ui.base.styles.setStyle
import com.likeminds.feed.android.core.socialfeed.adapter.LMFeedSelectedTopicAdapterListener
import com.likeminds.feed.android.core.utils.LMFeedStyleTransformer
import com.likeminds.feed.android.core.utils.base.LMFeedViewDataBinder
import com.likeminds.feed.android.core.utils.base.model.ITEM_TOPIC

class LMFeedItemSelectedFilterTopicViewDataBinder(
    private val listener: LMFeedSelectedTopicAdapterListener
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
                LMFeedStyleTransformer.socialFeedFragmentViewStyle.topicSelectorBarStyle

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

    //set click listeners to the view data binder
    private fun setListeners(binding: LmFeedItemSelectedFilterTopicBinding) {
        binding.apply {
            ivCross.setOnClickListener {
                val topicViewData = selectedTopicViewData ?: return@setOnClickListener
                listener.onTopicRemoved(position, topicViewData)
            }
        }
    }
}