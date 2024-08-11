package com.likeminds.feed.android.core.post.model

import com.likeminds.feed.android.core.utils.base.LMFeedBaseViewType
import com.likeminds.feed.android.core.utils.base.model.ITEM_POST_ATTACHMENT
import com.likeminds.feed.android.core.utils.base.model.ITEM_POST_DOCUMENTS_ITEM

class LMFeedAttachmentViewData private constructor(
    @LMFeedAttachmentType var attachmentType: Int,
    val attachmentMeta: LMFeedAttachmentMetaViewData,
    val dynamicViewType: Int?,
    val postId: String
) : LMFeedBaseViewType {

    override val viewType: Int
        get() = when {
            dynamicViewType != null -> {
                dynamicViewType
            }

            attachmentType == DOCUMENT -> {
                ITEM_POST_DOCUMENTS_ITEM
            }

            else -> {
                ITEM_POST_ATTACHMENT
            }
        }

    class Builder {
        @LMFeedAttachmentType
        private var attachmentType: Int = 0
        private var attachmentMeta: LMFeedAttachmentMetaViewData =
            LMFeedAttachmentMetaViewData.Builder().build()
        private var dynamicViewType: Int? = null
        private var postId: String = ""

        fun attachmentType(@LMFeedAttachmentType attachmentType: Int) =
            apply { this.attachmentType = attachmentType }

        fun attachmentMeta(attachmentMeta: LMFeedAttachmentMetaViewData) =
            apply { this.attachmentMeta = attachmentMeta }

        fun dynamicViewType(dynamicViewType: Int?) =
            apply { this.dynamicViewType = dynamicViewType }

        fun postId(postId: String) =
            apply { this.postId = postId }

        fun build() = LMFeedAttachmentViewData(
            attachmentType,
            attachmentMeta,
            dynamicViewType,
            postId
        )
    }

    fun toBuilder(): Builder {
        return Builder().attachmentType(attachmentType)
            .attachmentMeta(attachmentMeta)
            .dynamicViewType(dynamicViewType)
            .postId(postId)
    }

    override fun toString(): String {
        return buildString {
            append("LMFeedAttachmentViewData(attachmentType=")
            append(attachmentType)
            append(", attachmentMeta=")
            append(attachmentMeta)
            append(", dynamicViewType=")
            append(dynamicViewType)
            append(", postId='")
            append(postId)
            append("')")
        }
    }

}