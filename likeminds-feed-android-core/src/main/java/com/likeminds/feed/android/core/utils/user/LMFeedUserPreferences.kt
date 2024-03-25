package com.likeminds.feed.android.core.utils.user

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
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
        const val LM_FEED_API_KEY = "LM_FEED_API_KEY"
    }

    fun getApiKey(): String {
        return getPreference(LM_FEED_API_KEY, "") ?: ""
    }

    fun saveApiKey(apiKey: String) {
        putPreference(LM_FEED_API_KEY, apiKey)
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

    @SuppressLint("HardwareIds")
    fun getDeviceId(): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            ?: ""
    }

    fun clearPrefs() {
        saveUserName("")
        saveUUID("")
    }
}