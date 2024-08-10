package com.likeminds.feed.android.core.post.model

import android.net.Uri
import com.likeminds.feed.android.core.poll.result.model.LMFeedPollViewData
import com.likeminds.feed.android.core.widget.model.LMFeedWidgetViewData

class LMFeedAttachmentMetaViewData private constructor(
    val name: String?,
    val url: String?,
    val format: String?,
    val size: Long?,
    val duration: Int?,
    val pageCount: Int?,
    val ogTags: LMFeedLinkOGTagsViewData?,
    val width: Int?,
    val height: Int?,
    val uri: Uri?,
    val thumbnail: String?,
    val poll: LMFeedPollViewData?,
    val widgetViewData: LMFeedWidgetViewData?
) {

    class Builder {
        private var name: String? = null
        private var url: String? = null
        private var format: String? = null
        private var size: Long? = null
        private var duration: Int? = null
        private var pageCount: Int? = null
        private var ogTags: LMFeedLinkOGTagsViewData? = null
        private var width: Int? = null
        private var height: Int? = null
        private var uri: Uri? = null
        private var thumbnail: String? = null
        private var poll: LMFeedPollViewData? = null
        private var widgetViewData: LMFeedWidgetViewData? = null

        fun name(name: String?) = apply {
            this.name = name
        }

        fun url(url: String?) = apply {
            this.url = url
        }

        fun format(format: String?) = apply {
            this.format = format
        }

        fun size(size: Long?) = apply {
            this.size = size
        }

        fun duration(duration: Int?) = apply {
            this.duration = duration
        }

        fun pageCount(pageCount: Int?) = apply {
            this.pageCount = pageCount
        }

        fun ogTags(ogTags: LMFeedLinkOGTagsViewData?) = apply {
            this.ogTags = ogTags
        }

        fun width(width: Int?) = apply {
            this.width = width
        }

        fun height(height: Int?) = apply {
            this.height = height
        }

        fun uri(uri: Uri?) = apply {
            this.uri = uri
        }

        fun thumbnail(thumbnail: String?) = apply {
            this.thumbnail = thumbnail
        }

        fun poll(poll: LMFeedPollViewData?) = apply {
            this.poll = poll
        }

        fun widgetViewData(widgetViewData: LMFeedWidgetViewData?) = apply {
            this.widgetViewData = widgetViewData
        }

        fun build() = LMFeedAttachmentMetaViewData(
            name,
            url,
            format,
            size,
            duration,
            pageCount,
            ogTags,
            width,
            height,
            uri,
            thumbnail,
            poll,
            widgetViewData
        )
    }

    fun toBuilder(): Builder {
        return Builder()
            .name(name)
            .url(url)
            .format(format)
            .size(size)
            .duration(duration)
            .pageCount(pageCount)
            .ogTags(ogTags)
            .width(width)
            .height(height)
            .uri(uri)
            .thumbnail(thumbnail)
            .poll(poll)
            .widgetViewData(widgetViewData)
    }

    override fun toString(): String {
        return buildString {
            append("LMFeedAttachmentMetaViewData(name=")
            append(name)
            append(", url=")
            append(url)
            append(", format=")
            append(format)
            append(", size=")
            append(size)
            append(", duration=")
            append(duration)
            append(", pageCount=")
            append(pageCount)
            append(", ogTags=")
            append(ogTags)
            append(", width=")
            append(width)
            append(", height=")
            append(height)
            append(", uri=")
            append(uri)
            append(", thumbnail=")
            append(thumbnail)
            append(", poll=")
            append(poll)
            append(", widgetViewData=")
            append(widgetViewData)
            append(")")
        }
    }
}