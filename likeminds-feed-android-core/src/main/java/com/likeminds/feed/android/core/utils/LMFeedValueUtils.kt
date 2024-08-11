package com.likeminds.feed.android.core.utils

import android.util.Patterns
import android.webkit.URLUtil
import com.likeminds.feed.android.core.utils.pluralize.LMFeedPluralize.pluralize
import com.likeminds.feed.android.core.utils.pluralize.LMFeedPluralize.singularize
import com.likeminds.feed.android.core.utils.pluralize.model.LMFeedWordAction
import org.json.JSONObject
import kotlin.math.abs

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

    //to check if the index is valid
    fun Int.isValidIndex(items: List<*>? = null): Boolean {
        return if (items != null) {
            this > -1 && this < items.size
        } else {
            this > -1
        }
    }

    //to get formatted number (1000 -> 1k)
    fun Long.getFormatedNumber(): String {
        return if (abs(this / 1000000) > 1) {
            (this / 1000000).toString() + "M"
        } else if (abs(this / 1000) > 1) {
            (this / 1000).toString() + "K"
        } else {
            this.toString()
        }
    }

    //to extract String values from JSONObject
    fun JSONObject.findStringOrDefault(key: String, defaultValue: String): String {
        return this.optString(key, defaultValue)
    }

    //to extract Int values from JSONObject
    fun JSONObject.findIntOrDefault(key: String, defaultValue: Int): Int {
        return this.optInt(key, defaultValue)
    }

    //to extract Double values from JSONObject
    fun JSONObject.findLongOrDefault(key: String, defaultValue: Long): Long {
        return this.optLong(key, defaultValue)
    }

    //to extract Boolean values from JSONObject
    fun JSONObject.findBooleanOrDefault(key: String, defaultValue: Boolean): Boolean {
        return this.optBoolean(key, defaultValue)
    }

    fun JSONObject.findJSONObjectOrNull(key: String): JSONObject? {
        return this.optJSONObject(key)
    }
}