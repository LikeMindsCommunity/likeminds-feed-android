package com.likeminds.feed.android.core.socialfeed.adapter.databinders.postmultiplemedia

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.customgallery.media.model.IMAGE
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.databinding.LmFeedItemMultipleMediaImageBinding
import com.likeminds.feed.android.core.post.model.LMFeedAttachmentViewData
import com.likeminds.feed.android.core.ui.base.styles.LMFeedIconStyle
import com.likeminds.feed.android.core.socialfeed.adapter.LMFeedPostAdapterListener
import com.likeminds.feed.android.core.socialfeed.util.LMFeedPostBinderUtils
import com.likeminds.feed.android.core.utils.LMFeedStyleTransformer
import com.likeminds.feed.android.core.utils.base.LMFeedViewDataBinder
import com.likeminds.feed.android.core.utils.base.model.ITEM_MULTIPLE_MEDIA_IMAGE

class LMFeedItemMultipleMediaImageViewDataBinder(
    private val parentPosition: Int,
    private val listener: LMFeedPostAdapterListener,
    private val isMediaRemovable: Boolean
) : LMFeedViewDataBinder<LmFeedItemMultipleMediaImageBinding, LMFeedAttachmentViewData>() {

    override val viewType: Int
        get() = ITEM_MULTIPLE_MEDIA_IMAGE

    override fun createBinder(parent: ViewGroup): LmFeedItemMultipleMediaImageBinding {
        val binding = LmFeedItemMultipleMediaImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        binding.apply {
            setClickListeners(this)

            //sets image media style to multiple media image view
            val postImageMediaStyle =
                LMFeedStyleTransformer.postViewStyle.postMediaViewStyle.postImageMediaStyle

            val finalPostImageMediaStyle = if (isMediaRemovable) {
                postImageMediaStyle?.toBuilder()
                    ?.removeIconStyle(
                        LMFeedIconStyle.Builder()
                            .inActiveSrc(R.drawable.lm_feed_ic_cross)
                            .build()
                    )
                    ?.build()
            } else {
                postImageMediaStyle
            } ?: return@apply

            postImageView.setStyle(finalPostImageMediaStyle)
        }

        return binding
    }

    override fun bindData(
        binding: LmFeedItemMultipleMediaImageBinding,
        data: LMFeedAttachmentViewData,
        position: Int
    ) {
        binding.apply {
            //set data to the binding
            this.position = position
            this.attachmentViewData = data

            // loads post image inside the multiple media image view
            LMFeedPostBinderUtils.bindMultipleMediaImageView(postImageView, data)
        }
    }

    private fun setClickListeners(binding: LmFeedItemMultipleMediaImageBinding) {
        binding.apply {
            postImageView.setOnClickListener {
                val attachment = attachmentViewData ?: return@setOnClickListener
                listener.onPostMultipleMediaImageClicked(
                    position,
                    parentPosition,
                    attachment
                )
            }

            postImageView.setRemoveIconClickListener {
                listener.onMediaRemovedClicked(
                    position,
                    IMAGE
                )
            }
        }
    }
}