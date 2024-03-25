package com.likeminds.feed.android.core.activityfeed.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.likeminds.feed.android.core.activityfeed.adapter.LMFeedActivityFeedAdapterListener
import com.likeminds.feed.android.core.activityfeed.model.LMFeedActivityViewData
import com.likeminds.feed.android.core.databinding.LmFeedItemActivityFeedBinding
import com.likeminds.feed.android.core.utils.LMFeedStyleTransformer
import com.likeminds.feed.android.core.utils.base.LMFeedViewDataBinder
import com.likeminds.feed.android.core.utils.base.model.ITEM_ACTIVITY_FEED

class LMFeedItemActivityFeedViewDataBinder(
    private val listener: LMFeedActivityFeedAdapterListener
) : LMFeedViewDataBinder<LmFeedItemActivityFeedBinding, LMFeedActivityViewData>() {

    override val viewType: Int
        get() = ITEM_ACTIVITY_FEED

    override fun createBinder(parent: ViewGroup): LmFeedItemActivityFeedBinding {
        val binding = LmFeedItemActivityFeedBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        binding.activityView.setStyle(LMFeedStyleTransformer.activityFeedFragmentViewStyle.activityViewStyle)
        setClickListeners(binding)

        return binding
    }

    override fun bindData(
        binding: LmFeedItemActivityFeedBinding,
        data: LMFeedActivityViewData,
        position: Int
    ) {
        binding.apply {
            //set value for binding variables
            this.position = position
            activityViewData = data

            initActivityView(this, data)
        }
    }

    //initializes notification item
    private fun initActivityView(
        binding: LmFeedItemActivityFeedBinding,
        data: LMFeedActivityViewData
    ) {
        binding.apply {
            val activityViewStyle =
                LMFeedStyleTransformer.activityFeedFragmentViewStyle.activityViewStyle

            val context = root.context

            if (data.isRead) {
                activityViewStyle.readActivityBackgroundColor?.let { readActivityBackgroundColor ->
                    root.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            readActivityBackgroundColor
                        )
                    )
                }
            } else {
                activityViewStyle.unreadActivityBackgroundColor?.let { unreadActivityBackgroundColor ->
                    root.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            unreadActivityBackgroundColor
                        )
                    )
                }
            }

            activityView.apply {
                setActivityContent(data.activityText)
                setActivityRead(data.isRead)
                setUserImage(data.activityByUser)
                setPostTypeBadge(data.activityEntityData?.attachments?.firstOrNull()?.attachmentType)
                setTimestamp(data.updatedAt)
            }
        }
    }

    private fun setClickListeners(binding: LmFeedItemActivityFeedBinding) {
        binding.apply {
            root.setOnClickListener {
                val activityData = activityViewData ?: return@setOnClickListener
                listener.onActivityFeedItemClicked(position, activityData)
            }
        }
    }
}