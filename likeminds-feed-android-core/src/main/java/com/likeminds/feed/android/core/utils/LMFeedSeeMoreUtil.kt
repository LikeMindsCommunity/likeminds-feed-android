package com.likeminds.feed.android.core.utils

import android.widget.TextView
import com.likeminds.feed.android.core.ui.base.views.LMFeedTextView

object LMFeedSeeMoreUtil {

    /**
     * This function is for getting short text content for the see more feature
     */
    fun getShortContent(
        textView: LMFeedTextView,
        maxLines: Int,
        seeMoreCountLimit: Int
    ): String? {
        val textContent = textView.editableText ?: return null

        // variable to hold text limit as per max number of lines
        var shortTextLine: String? = null

        //variable to hold text limit as per max character limit
        val shortLimitText: String? =
            if (textContent.length > seeMoreCountLimit) textContent.substring(
                0,
                seeMoreCountLimit
            ) else null

        //calculation of text limit as per max number of lines
        if (textView.lineCount >= maxLines) {
            val lineEndIndex: Int = textView.layout.getLineEnd(maxLines - 1)
            shortTextLine = textView.text.subSequence(0, lineEndIndex).toString()
        }

        // returns null or minimum of shortTextLine & shortLimitText
        if (shortTextLine != null && shortTextLine.length != textContent.length) {
            if (shortLimitText != null && shortLimitText.length < shortTextLine.length) {
                return shortLimitText
            }
            return shortTextLine
        }
        return shortLimitText
    }
}