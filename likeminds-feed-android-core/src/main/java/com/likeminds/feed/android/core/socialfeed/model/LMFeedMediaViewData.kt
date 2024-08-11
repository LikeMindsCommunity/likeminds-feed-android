package com.likeminds.feed.android.core.socialfeed.model

import com.likeminds.feed.android.core.post.model.LMFeedAttachmentViewData
import com.likeminds.feed.android.core.utils.base.LMFeedBaseViewType
import com.likeminds.feed.android.core.utils.base.model.ITEM_POST_MEDIA

class LMFeedMediaViewData private constructor(
    val attachments: List<LMFeedAttachmentViewData>,
    val isExpanded: Boolean,
    val temporaryId: Long?,
    val workerUUID: String
) : LMFeedBaseViewType {

    override val viewType: Int
        get() = ITEM_POST_MEDIA

    class Builder {
        private var attachments: List<LMFeedAttachmentViewData> = listOf()
        private var isExpanded: Boolean = false
        private var temporaryId: Long? = null
        private var workerUUID: String = ""

        fun attachments(attachments: List<LMFeedAttachmentViewData>) = apply {
            this.attachments = attachments
        }

        fun isExpanded(isExpanded: Boolean) = apply {
            this.isExpanded = isExpanded
        }

        fun temporaryId(temporaryId: Long?) = apply {
            this.temporaryId = temporaryId
        }

        fun workerUUID(workerUUID: String) = apply {
            this.workerUUID = workerUUID
        }

        fun build() = LMFeedMediaViewData(
            attachments,
            isExpanded,
            temporaryId,
            workerUUID
        )
    }

    fun toBuilder(): Builder {
        return Builder().attachments(attachments)
            .isExpanded(isExpanded)
            .temporaryId(temporaryId)
            .workerUUID(workerUUID)
    }

    override fun toString(): String {
        return "LMFeedMediaViewData:(attachments=$attachments, isExpanded=$isExpanded, temporaryId=$temporaryId, workerUUID=$workerUUID)"
    }
}