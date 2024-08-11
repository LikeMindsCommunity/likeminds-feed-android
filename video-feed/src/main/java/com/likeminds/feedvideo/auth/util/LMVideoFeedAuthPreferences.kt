package com.likeminds.feedvideo.auth.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings

class LMVideoFeedAuthPreferences(context: Context) {

    companion object {
        const val VIDEO_FEED_AUTH_PREFS = "video_feed_auth_prefs"
        const val API_KEY = "api_key"
        const val USER_NAME = "user_name"
        const val USER_ID = "user_id"
        const val IS_LOGGED_IN = "is_logged_in"
    }

    private val preferences: SharedPreferences =
        context.getSharedPreferences(VIDEO_FEED_AUTH_PREFS, Context.MODE_PRIVATE)

    @SuppressLint("HardwareIds")
    private val deviceId =
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

    fun getApiKey(): String {
        return preferences.getString(API_KEY, "") ?: ""
    }

    fun saveApiKey(apiKey: String) {
        preferences.edit().putString(API_KEY, apiKey).apply()
    }

    fun getUserName(): String {
        return preferences.getString(USER_NAME, "") ?: ""
    }

    fun saveUserName(userName: String) {
        preferences.edit().putString(USER_NAME, userName).apply()
    }

    fun getUserId(): String {
        return preferences.getString(USER_ID, "") ?: ""
    }

    fun saveUserId(userId: String) {
        preferences.edit().putString(USER_ID, userId).apply()
    }

    fun getIsLoggedIn(): Boolean {
        return preferences.getBoolean(IS_LOGGED_IN, false)
    }

    fun saveIsLoggedIn(isLoggedIn: Boolean) {
        preferences.edit().putBoolean(IS_LOGGED_IN, isLoggedIn).apply()
    }

    @SuppressLint("HardwareIds")
    fun getDeviceId(): String {
        return deviceId ?: ""
    }

    fun clearPrefs() {
        saveApiKey("")
        saveUserName("")
        saveUserId("")
        saveIsLoggedIn(false)
    }
}
