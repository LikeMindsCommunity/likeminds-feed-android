package com.likeminds.feed.android.core.search.util

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.*
import androidx.annotation.ColorInt

object LMFeedSearchUtil {
    // finds the keyword in the post's text and highlight it
    fun getTrimmedText(
        text: String,
        keywords: List<String>,
        @ColorInt color: Int,
        @ColorInt highlightColor: Int? = null,
    ): SpannableStringBuilder {
        val keyword = keywords[0]
        val trimmedText: String
        val ind: Int
        var wordPos = -1
        val listOfWords = text.split(" ").map { it.trim() }

        for (i in listOfWords.indices) {
            if (listOfWords[i].startsWith(keyword, ignoreCase = true)) {
                wordPos = i
                break
            }
        }
        if (wordPos <= 3) {
            trimmedText = text
        } else if (wordPos > 3 && wordPos < listOfWords.size - 5) {
            ind = text.indexOf(" $keyword", ignoreCase = true)
            val totalLen =
                listOfWords[wordPos - 3].length + listOfWords[wordPos - 2].length + listOfWords[wordPos - 1].length + 3
            trimmedText = "... " + text.substring(ind - totalLen)
        } else {
            var totalLen = 0
            for (i in listOfWords.size - 1 downTo listOfWords.size - 5) {
                totalLen += listOfWords[i].length
            }
            totalLen += 4
            trimmedText = if (listOfWords.size == 5) {
                text
            } else {
                "... " + text.substring(text.length - totalLen)
            }
        }
        return getHighlightedText(trimmedText, keywords, color, highlightColor)
    }

    private fun getHighlightedText(
        stringToBeMatched: String,
        keywordsMatched: List<String>,
        color: Int,
        highlightColor: Int? = null
    ): SpannableStringBuilder {
        val str = SpannableStringBuilder(stringToBeMatched)
        keywordsMatched.forEach { keyword ->
            if (str.startsWith(keyword, ignoreCase = true)) {
                highlightMatchedText(str, color, highlightColor, 0, keyword.length)
            }
            if (str.contains(" $keyword", ignoreCase = true)) {
                var lastIndex = 0
                while (lastIndex != -1) {
                    lastIndex = str.indexOf(keyword, lastIndex, ignoreCase = true)
                    if (lastIndex != -1) {
                        highlightMatchedText(
                            str,
                            color,
                            highlightColor,
                            lastIndex,
                            lastIndex + keyword.length
                        )
                        lastIndex += keyword.length
                    }
                }
            }
        }
        return str
    }

    // gives text color and highlightColor to the keyword in post's text
    private fun highlightMatchedText(
        str: SpannableStringBuilder,
        @ColorInt color: Int,
        @ColorInt highlightColor: Int? = null,
        startIndex: Int,
        endIndex: Int,
        applyBoldSpan: Boolean = true
    ) {
        if (applyBoldSpan) {
            str.setSpan(
                StyleSpan(Typeface.BOLD),
                startIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        str.setSpan(
            ForegroundColorSpan(color),
            startIndex,
            endIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        // apply background highlight color if provided
        highlightColor?.let {
            str.setSpan(
                BackgroundColorSpan(it),
                startIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    // finds keywords searched in the post's text and add it in a list
    fun findMatchedKeyword(
        keywordSearched: String?,
        string: String?,
    ): MutableList<String> {
        val listOfKeywords = keywordSearched?.split(" ")?.map { it.trim() }
        val matchedKeywords = mutableListOf<String>()

        if (!listOfKeywords.isNullOrEmpty() && !string.isNullOrEmpty()) {
            listOfKeywords.forEach { keyword ->
                if (string.lowercase().contains(" ${keyword.lowercase()}") ||
                    string.lowercase().startsWith(keyword.lowercase())
                ) {
                    matchedKeywords.add(keyword)
                }
            }
        }
        return matchedKeywords
    }
}