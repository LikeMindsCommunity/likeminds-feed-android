package com.likeminds.feed.android.core.socialfeed.adapter.databinders.postdocuments

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.customgallery.media.model.PDF
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.databinding.LmFeedItemDocumentBinding
import com.likeminds.feed.android.core.post.model.LMFeedAttachmentViewData
import com.likeminds.feed.android.core.ui.base.styles.LMFeedIconStyle
import com.likeminds.feed.android.core.socialfeed.adapter.LMFeedPostAdapterListener
import com.likeminds.feed.android.core.socialfeed.util.LMFeedPostBinderUtils
import com.likeminds.feed.android.core.utils.LMFeedStyleTransformer
import com.likeminds.feed.android.core.utils.base.LMFeedViewDataBinder
import com.likeminds.feed.android.core.utils.base.model.ITEM_POST_DOCUMENTS_ITEM

class LMFeedItemDocumentViewDataBinder(
    private val parentPosition: Int,
    private val listener: LMFeedPostAdapterListener,
    private val isMediaRemovable: Boolean
) : LMFeedViewDataBinder<LmFeedItemDocumentBinding, LMFeedAttachmentViewData>() {

    override val viewType: Int
        get() = ITEM_POST_DOCUMENTS_ITEM

    override fun createBinder(parent: ViewGroup): LmFeedItemDocumentBinding {
        val binding = LmFeedItemDocumentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        binding.apply {
            //sets click listener to the documents item
            setClickListeners(binding)

            //sets document media style
            val postDocumentMediaStyle =
                LMFeedStyleTransformer.postViewStyle.postMediaViewStyle.postDocumentsMediaStyle

            val finalDocumentMediaStyle = if (isMediaRemovable) {
                postDocumentMediaStyle?.toBuilder()
                    ?.removeIconStyle(
                        LMFeedIconStyle.Builder()
                            .inActiveSrc(R.drawable.lm_feed_ic_cross)
                            .build()
                    )
                    ?.build()
            } else {
                postDocumentMediaStyle
            } ?: return@apply

            documentItem.setStyle(finalDocumentMediaStyle)
        }

        return binding
    }

    override fun bindData(
        binding: LmFeedItemDocumentBinding,
        data: LMFeedAttachmentViewData,
        position: Int
    ) {
        binding.apply {
            //sets variables in the binding
            this.position = position
            this.attachmentViewData = data

            //sets data on the documents view
            LMFeedPostBinderUtils.bindPostMediaDocument(
                documentItem,
                data
            )
        }
    }

    private fun setClickListeners(binding: LmFeedItemDocumentBinding) {
        binding.apply {
            documentItem.setDocumentClickListener {
                val attachment = attachmentViewData ?: return@setDocumentClickListener
                listener.onPostDocumentMediaClicked(
                    position,
                    parentPosition,
                    attachment
                )
            }

            documentItem.setRemoveIconClickListener {
                listener.onMediaRemovedClicked(
                    position,
                    PDF
                )
            }
        }
    }
}