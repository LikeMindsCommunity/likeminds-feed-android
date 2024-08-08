package com.likeminds.feed.android.core.socialfeed.adapter.databinders.postmultiplemedia

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.customgallery.media.model.VIDEO
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.databinding.LmFeedItemMultipleMediaVideoBinding
import com.likeminds.feed.android.core.post.model.LMFeedAttachmentViewData
import com.likeminds.feed.android.core.ui.base.styles.LMFeedIconStyle
import com.likeminds.feed.android.core.socialfeed.adapter.LMFeedPostAdapterListener
import com.likeminds.feed.android.core.utils.LMFeedStyleTransformer
import com.likeminds.feed.android.core.utils.base.LMFeedViewDataBinder
import com.likeminds.feed.android.core.utils.base.model.ITEM_MULTIPLE_MEDIA_VIDEO

class LMFeedItemMultipleMediaVideoViewDataBinder(
    private val parentPosition: Int,
    private val listener: LMFeedPostAdapterListener,
    private val isMediaRemovable: Boolean
) : LMFeedViewDataBinder<LmFeedItemMultipleMediaVideoBinding, LMFeedAttachmentViewData>() {

    override val viewType: Int
        get() = ITEM_MULTIPLE_MEDIA_VIDEO

    override fun createBinder(parent: ViewGroup): LmFeedItemMultipleMediaVideoBinding {
        val binding = LmFeedItemMultipleMediaVideoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        binding.apply {
            setClickListeners(this)

            //sets video media style to multiple media video view
            val postVideoMediaStyle =
                LMFeedStyleTransformer.postViewStyle.postMediaViewStyle.postVideoMediaStyle

            val finalPostVideoMediaStyle = if (isMediaRemovable) {
                postVideoMediaStyle?.toBuilder()
                    ?.removeIconStyle(
                        LMFeedIconStyle.Builder()
                            .inActiveSrc(R.drawable.lm_feed_ic_cross)
                            .build()
                    )
                    ?.build()
            } else {
                postVideoMediaStyle
            } ?: return@apply

            postVideoView.setStyle(finalPostVideoMediaStyle)
        }

        return binding
    }

    override fun bindData(
        binding: LmFeedItemMultipleMediaVideoBinding,
        data: LMFeedAttachmentViewData,
        position: Int
    ) {
        binding.apply {
            //set data to the binding
            this.position = position
            this.attachmentViewData = data
        }
    }

    private fun setClickListeners(binding: LmFeedItemMultipleMediaVideoBinding) {
        binding.apply {
            postVideoView.setOnClickListener {
                val attachment = attachmentViewData ?: return@setOnClickListener
                listener.onPostMultipleMediaVideoClicked(
                    position,
                    parentPosition,
                    attachment
                )
            }

            postVideoView.setRemoveIconClickListener {
                listener.onMediaRemovedClicked(
                    position,
                    VIDEO
                )
            }
        }
    }
}