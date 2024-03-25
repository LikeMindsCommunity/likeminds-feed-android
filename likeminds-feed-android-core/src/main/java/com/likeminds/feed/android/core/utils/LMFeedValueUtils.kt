package com.likeminds.feed.android.core.utils

import android.util.Patterns
import android.webkit.URLUtil
import com.likeminds.feed.android.core.utils.pluralize.LMFeedPluralize.pluralize
import com.likeminds.feed.android.core.utils.pluralize.LMFeedPluralize.singularize
import com.likeminds.feed.android.core.utils.pluralize.model.LMFeedWordAction

object LMFeedValueUtils {

    fun String.getValidTextForLinkify(): String {
        return this.replace("\u202C", "")
            .replace("\u202D", "")
            .replace("\u202E", "")
    }

    fun String?.isImageValid(): Boolean {
        return this?.isValidUrl() ?: false
    }

    fun <T> List<T>.getItemInList(position: Int): T? {
        if (position < 0 || position >= this.size) {
            return null
        }
        return this[position]
    }

    //returns the url from the provided string if present
    fun String.getUrlIfExist(): String? {
        return try {
            val links: MutableList<String> = ArrayList()
            val matcher = Patterns.WEB_URL.matcher(this)
            while (matcher.find()) {
                val link = matcher.group()
                if (URLUtil.isValidUrl(link)) {
                    links.add(link)
                } else {
                    val newHttpsLink = "https://$link"
                    if (URLUtil.isValidUrl(newHttpsLink)) {
                        links.add(newHttpsLink)
                    }
                }
            }
            if (links.isNotEmpty()) {
                links.first()
            } else null
        } catch (e: Exception) {
            return null
        }
    }

    private fun String.isValidUrl(): Boolean {
        if (this.isEmpty()) {
            return false
        }
        return Patterns.WEB_URL.matcher(this).matches()
    }

    fun String.pluralizeOrCapitalize(action: LMFeedWordAction): String {
        return when (action) {
            LMFeedWordAction.FIRST_LETTER_CAPITAL_SINGULAR -> {
                val singular = this.singularize()
                singular.replaceFirstChar {
                    it.uppercase()
                }
            }

            LMFeedWordAction.ALL_CAPITAL_SINGULAR -> {
                val singular = this.singularize()
                singular.uppercase()
            }

            LMFeedWordAction.ALL_SMALL_SINGULAR -> {
                val singular = this.singularize()
                singular.lowercase()
            }

            LMFeedWordAction.FIRST_LETTER_CAPITAL_PLURAL -> {
                val plural = this.pluralize()
                plural.replaceFirstChar {
                    it.uppercase()
                }
            }

            LMFeedWordAction.ALL_CAPITAL_PLURAL -> {
                val plural = this.pluralize()
                plural.uppercase()
            }

            LMFeedWordAction.ALL_SMALL_PLURAL -> {
                val plural = this.pluralize()
                plural.lowercase()
            }
        }
    }
}