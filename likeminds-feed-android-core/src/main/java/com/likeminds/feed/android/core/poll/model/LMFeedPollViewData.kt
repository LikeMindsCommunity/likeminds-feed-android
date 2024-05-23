package com.likeminds.feed.android.core.poll.model

import android.content.Context
import android.os.Parcelable
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.utils.LMFeedTimeUtil
import com.likeminds.likemindsfeed.post.model.PollMultiSelectState
import com.likeminds.likemindsfeed.post.model.PollType
import kotlinx.parcelize.Parcelize

@Parcelize
class LMFeedPollViewData private constructor(
    val id: String,
    val title: String,
    val pollAnswerText: String,
    val toShowResults: Boolean,
    val options: List<LMFeedPollOptionViewData>,
    val expiryTime: Long,
    val isAnonymous: Boolean,
    val allowAddOption: Boolean,
    val multipleSelectState: PollMultiSelectState,
    val multipleSelectNumber: Int,
    val pollType: PollType,
    val isPollSubmitted: Boolean,
) : Parcelable {

    fun isInstantPoll() = (pollType == PollType.INSTANT)
    fun isDeferredPoll() = (pollType == PollType.DEFERRED)

    fun hasPollEnded() = (expiryTime - System.currentTimeMillis()) <= 0

    fun isMultiChoicePoll() =
        !(multipleSelectState == PollMultiSelectState.EXACTLY && multipleSelectNumber == 1)

    fun isAddOptionAllowedForInstantPoll() = (allowAddOption && isInstantPoll() && !isPollSubmitted)
    fun isAddOptionAllowedForDeferredPoll() = (allowAddOption && isDeferredPoll())

    fun getTimeLeftInPoll(context: Context): String = if (hasPollEnded()) {
        context.getString(R.string.lm_feed_poll_ended)
    } else {
        context.getString(
            R.string.lm_feed_poll_vote_time_left,
            LMFeedTimeUtil.getRelativeExpiryTimeInString(expiryTime)
        )
    }

    fun getPollSelectionText(context: Context): String? =
        if (multipleSelectState == PollMultiSelectState.EXACTLY && multipleSelectNumber == 1) {
            null
        } else {
            when (multipleSelectState) {
                PollMultiSelectState.EXACTLY -> {
                    context.resources.getQuantityString(
                        R.plurals.lm_feed_select_exactly_d_options,
                        multipleSelectNumber,
                        multipleSelectNumber
                    )
                }

                PollMultiSelectState.AT_MAX -> {
                    context.resources.getQuantityString(
                        R.plurals.lm_feed_select_at_most_d_options,
                        multipleSelectNumber,
                        multipleSelectNumber
                    )
                }

                PollMultiSelectState.AT_LEAST -> {
                    context.resources.getQuantityString(
                        R.plurals.lm_feed_select_at_least_d_options,
                        multipleSelectNumber,
                        multipleSelectNumber
                    )
                }
            }
        }

    class Builder {
        private var id: String = ""
        private var title: String = ""
        private var pollAnswerText: String = ""
        private var toShowResults: Boolean = false
        private var options: List<LMFeedPollOptionViewData> = emptyList()
        private var expiryTime: Long = 0L
        private var isAnonymous: Boolean = false
        private var allowAddOption: Boolean = false
        private var multipleSelectState: PollMultiSelectState = PollMultiSelectState.EXACTLY
        private var multipleSelectNumber: Int = 0
        private var pollType: PollType = PollType.INSTANT
        private var isPollSubmitted: Boolean = false

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

        fun isAnonymous(isAnonymous: Boolean) = apply {
            this.isAnonymous = isAnonymous
        }

        fun allowAddOption(allowAddOption: Boolean) = apply {
            this.allowAddOption = allowAddOption
        }

        fun multipleSelectState(multipleSelectState: PollMultiSelectState) = apply {
            this.multipleSelectState = multipleSelectState
        }

        fun multipleSelectNumber(multipleSelectNumber: Int) = apply {
            this.multipleSelectNumber = multipleSelectNumber
        }

        fun pollType(pollType: PollType) = apply {
            this.pollType = pollType
        }

        fun isPollSubmitted(isPollSubmitted: Boolean) = apply {
            this.isPollSubmitted = isPollSubmitted
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
            pollType,
            isPollSubmitted
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
            .isPollSubmitted(isPollSubmitted)
    }
}