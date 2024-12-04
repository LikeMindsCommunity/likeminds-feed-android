package com.likeminds.feed.android.core.search.util

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.*
import androidx.annotation.ColorInt
import com.likeminds.usertagging.util.UserTaggingDecoder

object LMFeedSearchUtil {
    // trims the post text to show only the part of the post that matches the keyword
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
        //split text into words
        val listOfWords = text.split(" ").map { it.trim() }

        //loop to find the position of the word that matches the keyword
        for (i in listOfWords.indices) {
            if (listOfWords[i].startsWith(keyword, ignoreCase = true)) {
                wordPos = i
                break
            }
        }
        //trim the text based on the position of the keyword
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

    //highlights the matched keywords in the provided text
    private fun getHighlightedText(
        stringToBeMatched: String,
        keywordsMatched: List<String>,
        color: Int,
        highlightColor: Int? = null
    ): SpannableStringBuilder {
        val str = SpannableStringBuilder(stringToBeMatched)
        //loop through each keyword to highlight in the post text
        keywordsMatched.forEach { keyword ->
            //check for if text starts with the keyword
            if (str.startsWith(keyword, ignoreCase = true)) {
                highlightMatchedText(str, color, highlightColor, 0, keyword.length)
            }

            //check for if the keyword appears elsewhere in the text
            if (str.contains(" $keyword", ignoreCase = true) ||
                str.contains(" @$keyword", ignoreCase = true) ||
                str.startsWith("@$keyword", ignoreCase = true)
            ) {
                var lastIndex = 0
                //loop to find all occurrences of keyword in text
                while (lastIndex != -1) {
                    lastIndex = str.indexOf(keyword, lastIndex, ignoreCase = true)
                    if (lastIndex != -1) {
                        //highlighting the keyword in the text
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
        // check if the bold styling should be applied
        if (applyBoldSpan) {
            str.setSpan(
                StyleSpan(Typeface.BOLD),
                startIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        //setting color of the keyword
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

        val stringDecoded = UserTaggingDecoder.decode(string)

        if (!listOfKeywords.isNullOrEmpty() && !string.isNullOrEmpty()) {
            // loop to find matches in the text
            listOfKeywords.forEach { keyword ->
                if (stringDecoded.contains(" $keyword", ignoreCase = true) ||
                    stringDecoded.contains(" @${keyword}", ignoreCase = true) ||
                    stringDecoded.startsWith(keyword, ignoreCase = true) ||
                    stringDecoded.startsWith("@$keyword", ignoreCase = true)
                ) {
                    matchedKeywords.add(keyword)
                }
            }
        }
        return matchedKeywords
    }
}