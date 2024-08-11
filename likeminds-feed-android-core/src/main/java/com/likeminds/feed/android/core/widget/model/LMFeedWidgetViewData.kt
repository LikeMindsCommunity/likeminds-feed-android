package com.likeminds.feed.android.core.widget.model

import org.json.JSONObject

class LMFeedWidgetViewData private constructor(
    val id: String,
    val createdAt: Long,
    val metadata: JSONObject,
    val parentEntityId: String,
    val parentEntityType: String,
    val updatedAt: Long,
) {
    class Builder {
        private var id: String = ""
        private var createdAt: Long = 0L
        private var metadata: JSONObject = JSONObject()
        private var parentEntityId: String = ""
        private var parentEntityType: String = ""
        private var updatedAt: Long = 0L

        fun id(id: String) = apply { this.id = id }
        fun createdAt(createdAt: Long) = apply { this.createdAt = createdAt }
        fun metadata(metadata: JSONObject) = apply { this.metadata = metadata }
        fun parentEntityId(parentEntityId: String) = apply { this.parentEntityId = parentEntityId }
        fun parentEntityType(parentEntityType: String) =
            apply { this.parentEntityType = parentEntityType }

        fun updatedAt(updatedAt: Long) = apply { this.updatedAt = updatedAt }

        fun build() = LMFeedWidgetViewData(
            id,
            createdAt,
            metadata,
            parentEntityId,
            parentEntityType,
            updatedAt
        )
    }

    fun toBuilder(): Builder {
        return Builder()
            .id(id)
            .createdAt(createdAt)
            .metadata(metadata)
            .parentEntityId(parentEntityId)
            .parentEntityType(parentEntityType)
            .updatedAt(updatedAt)
    }

    override fun toString(): String {
        return buildString {
            append("LMFeedWidgetViewData(id='")
            append(id)
            append("', createdAt=")
            append(createdAt)
            append(", metadata=")
            append(metadata)
            append(", parentEntityId='")
            append(parentEntityId)
            append("', parentEntityType='")
            append(parentEntityType)
            append("', updatedAt=")
            append(updatedAt)
            append(")")
        }
    }
}