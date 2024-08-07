package com.likeminds.feed.android.core.universalfeed.model

import com.likeminds.feed.android.core.post.model.*
import com.likeminds.feed.android.core.topics.model.LMFeedTopicViewData
import com.likeminds.feed.android.core.utils.base.LMFeedBaseViewType
import com.likeminds.feed.android.core.utils.base.model.*

class LMFeedPostViewData private constructor(
    val id: String,
    val headerViewData: LMFeedPostHeaderViewData,
    val contentViewData: LMFeedPostContentViewData,
    val mediaViewData: LMFeedMediaViewData,
    val footerViewData: LMFeedPostFooterViewData,
    val topicsViewData: List<LMFeedTopicViewData>,
    val fromPostLiked: Boolean,
    val fromPostSaved: Boolean,
    val fromVideoAction: Boolean,
    val isPosted: Boolean
) : LMFeedBaseViewType {

    private val noOfCustomWidgets =
        mediaViewData.attachments.filter { it.attachmentType == CUSTOM_WIDGET }.size
    private val mediaAttachments =
        mediaViewData.attachments.filter { it.attachmentType != CUSTOM_WIDGET }

    override val viewType: Int
        get() = when {
            (noOfCustomWidgets == mediaViewData.attachments.size) -> {
                ITEM_POST_CUSTOM_WIDGET
            }

            (mediaAttachments.size == 1 && mediaAttachments.first().attachmentType == IMAGE) -> {
                ITEM_POST_SINGLE_IMAGE
            }

            (mediaAttachments.size == 1 && mediaAttachments.first().attachmentType == VIDEO) -> {
                ITEM_POST_SINGLE_VIDEO
            }

            (mediaAttachments.isNotEmpty() && mediaAttachments.first().attachmentType == DOCUMENT) -> {
                ITEM_POST_DOCUMENTS
            }

            (mediaAttachments.size > 1 && (mediaAttachments.first().attachmentType == IMAGE || mediaAttachments.first().attachmentType == VIDEO)) -> {
                ITEM_POST_MULTIPLE_MEDIA
            }

            (mediaAttachments.size == 1 && mediaAttachments.first().attachmentType == LINK) -> {
                ITEM_POST_LINK
            }

            (mediaAttachments.size == 1 && mediaAttachments.first().attachmentType == POLL) -> {
                ITEM_POST_POLL
            }

            else -> {
                ITEM_POST_TEXT_ONLY
            }
        }

    class Builder {
        private var id: String = ""
        private var headerViewData: LMFeedPostHeaderViewData =
            LMFeedPostHeaderViewData.Builder().build()
        private var contentViewData: LMFeedPostContentViewData =
            LMFeedPostContentViewData.Builder().build()
        private var mediaViewData: LMFeedMediaViewData =
            LMFeedMediaViewData.Builder().build()
        private var footerViewData: LMFeedPostFooterViewData =
            LMFeedPostFooterViewData.Builder().build()
        private var topicsViewData: List<LMFeedTopicViewData> = emptyList()
        private var fromPostLiked: Boolean = false
        private var fromPostSaved: Boolean = false
        private var fromVideoAction: Boolean = false
        private var isPosted: Boolean = true

        fun id(id: String) = apply { this.id = id }
        fun headerViewData(headerViewData: LMFeedPostHeaderViewData) =
            apply { this.headerViewData = headerViewData }

        fun contentViewData(contentViewData: LMFeedPostContentViewData) =
            apply { this.contentViewData = contentViewData }

        fun mediaViewData(mediaViewData: LMFeedMediaViewData) =
            apply { this.mediaViewData = mediaViewData }

        fun footerViewData(footerViewData: LMFeedPostFooterViewData) =
            apply { this.footerViewData = footerViewData }

        fun topicsViewData(topicsViewData: List<LMFeedTopicViewData>) =
            apply { this.topicsViewData = topicsViewData }

        fun fromPostLiked(fromPostLiked: Boolean) = apply { this.fromPostLiked = fromPostLiked }
        fun fromPostSaved(fromPostSaved: Boolean) = apply { this.fromPostSaved = fromPostSaved }
        fun fromVideoAction(fromVideoAction: Boolean) =
            apply { this.fromVideoAction = fromVideoAction }

        fun isPosted(isPosted: Boolean) = apply { this.isPosted = isPosted }

        fun build() = LMFeedPostViewData(
            id,
            headerViewData,
            contentViewData,
            mediaViewData,
            footerViewData,
            topicsViewData,
            fromPostLiked,
            fromPostSaved,
            fromVideoAction,
            isPosted
        )
    }

    fun toBuilder(): Builder {
        return Builder()
            .id(id)
            .headerViewData(headerViewData)
            .contentViewData(contentViewData)
            .mediaViewData(mediaViewData)
            .footerViewData(footerViewData)
            .topicsViewData(topicsViewData)
            .fromPostLiked(fromPostLiked)
            .fromPostSaved(fromPostSaved)
            .fromVideoAction(fromVideoAction)
            .isPosted(isPosted)
    }
}