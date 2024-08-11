package com.likeminds.feed.android.core.poll.util

import com.likeminds.feed.android.core.poll.result.model.LMFeedPollViewData

object LMFeedPollUtil {

    //checks whether added option already exists in the options list or not, returns null if poll is null in the post data
    fun isDuplicationOption(
        poll: LMFeedPollViewData,
        addedOptionText: String
    ): Boolean {
        val pollOptions = poll.options

        pollOptions.forEach { option ->
            if (option.text == addedOptionText) {
                return true
            }
        }

        return false
    }
}