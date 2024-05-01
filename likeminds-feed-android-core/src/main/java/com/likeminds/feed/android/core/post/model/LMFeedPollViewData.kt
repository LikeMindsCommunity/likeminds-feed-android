package com.likeminds.feed.android.core.post.model

import com.likeminds.likemindsfeed.post.model.PollMultiSelectState
import com.likeminds.likemindsfeed.post.model.PollType

class LMFeedPollViewData private constructor(
    val id: String,
    val title: String,
    val pollAnswerText: String,
    val toShowResults: Boolean,
    val options: List<LMFeedPollOptionViewData>,
    val expiryTime: Long,
    val isAnonymous: Boolean?,
    val allowAddOption: Boolean,
    val multipleSelectState: String,
    val multipleSelectNumber: Int,
    val pollType: String
) {

    class Builder {
        private var id: String = ""
        private var title: String = ""
        private var pollAnswerText: String = ""
        private var toShowResults: Boolean = false
        private var options: List<LMFeedPollOptionViewData> = emptyList()
        private var expiryTime: Long = 0L
        private var isAnonymous: Boolean? = null
        private var allowAddOption: Boolean = false
        private var multipleSelectState: String = PollMultiSelectState.EXACTLY.value
        private var multipleSelectNumber: Int = 0
        private var pollType: String = PollType.INSTANT.value

        fun id(id: String) = apply {
            this.id = id
        }

        fun title(title: String) = apply {
            this.title = title
        }

        fun pollAnswerText(pollAnswerText: String) = apply {
            this.pollAnswerText = pollAnswerText
        }

        fun toShowResults(toShowResults: Boolean) = apply {
            this.toShowResults = toShowResults
        }

        fun options(options: List<LMFeedPollOptionViewData>) = apply {
            this.options = options
        }

        fun expiryTime(expiryTime: Long) = apply {
            this.expiryTime = expiryTime
        }

        fun isAnonymous(isAnonymous: Boolean?) = apply {
            this.isAnonymous = isAnonymous
        }

        fun allowAddOption(allowAddOption: Boolean) = apply {
            this.allowAddOption = allowAddOption
        }

        fun multipleSelectState(multipleSelectState: String) = apply {
            this.multipleSelectState = multipleSelectState
        }

        fun multipleSelectNumber(multipleSelectNumber: Int) = apply {
            this.multipleSelectNumber = multipleSelectNumber
        }

        fun pollType(pollType: String) = apply {
            this.pollType = pollType
        }

        fun build() = LMFeedPollViewData(
            id,
            title,
            pollAnswerText,
            toShowResults,
            options,
            expiryTime,
            isAnonymous,
            allowAddOption,
            multipleSelectState,
            multipleSelectNumber,
            pollType
        )
    }

    fun toBuilder(): Builder {
        return Builder().id(id)
            .title(title)
            .pollAnswerText(pollAnswerText)
            .toShowResults(toShowResults)
            .options(options)
            .expiryTime(expiryTime)
            .isAnonymous(isAnonymous)
            .allowAddOption(allowAddOption)
            .multipleSelectState(multipleSelectState)
            .multipleSelectNumber(multipleSelectNumber)
            .pollType(pollType)
    }
}