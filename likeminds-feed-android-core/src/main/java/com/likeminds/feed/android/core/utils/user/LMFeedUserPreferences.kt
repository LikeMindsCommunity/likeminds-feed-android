package com.likeminds.feed.android.core.utils.user

import android.content.Context
import com.likeminds.feed.android.core.utils.sharedpreferences.LMFeedBasePreferences
import javax.inject.Singleton

@Singleton
class LMFeedUserPreferences(
    private val context: Context
) : LMFeedBasePreferences(LM_FEED_USER_PREFS, context) {

    companion object {
        const val LM_FEED_USER_PREFS = "LM_FEED_USER_PREFS"
        const val LM_FEED_USER_NAME = "LM_FEED_USER_NAME"
        const val LM_FEED_UUID = "LM_FEED_UUID"
        const val LM_FEED_USER_IMAGE = "LM_FEED_IMAGE"
    }

    fun getUserName(): String {
        return getPreference(LM_FEED_USER_NAME, "") ?: ""
    }

    fun saveUserName(userName: String) {
        putPreference(LM_FEED_USER_NAME, userName)
    }

    fun getUUID(): String {
        return getPreference(LM_FEED_UUID, "") ?: ""
    }

    fun saveUUID(uuid: String) {
        putPreference(LM_FEED_UUID, uuid)
    }

    fun getUserImage(): String {
        return getPreference(LM_FEED_USER_IMAGE, "") ?: ""
    }

    fun setUserImage(userImage: String) {
        putPreference(LM_FEED_USER_IMAGE, userImage)
    }

    fun clearPrefs() {
        saveUserName("")
        saveUUID("")
    }
}