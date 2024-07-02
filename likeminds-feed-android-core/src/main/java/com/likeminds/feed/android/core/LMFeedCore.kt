package com.likeminds.feed.android.core

import android.app.Application
import android.content.Context
import com.google.android.exoplayer2.util.Log
import com.likeminds.feed.android.core.ui.theme.LMFeedTheme
import com.likeminds.feed.android.core.ui.theme.model.LMFeedSetThemeRequest
import com.likeminds.feed.android.core.utils.user.LMFeedUserPreferences
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.user.model.InitiateUserRequest
import com.likeminds.likemindsfeed.user.model.ValidateUserRequest
import kotlinx.coroutines.*

object LMFeedCore {

    /**
     * Initial setup function for customers and blocker function
     * @param application: Instance of the application class
     * @param lmFeedTheme: Object of [LMFeedTheme] to add your customizable theme in whole feed
     * @param lmFeedCoreCallback: Instance of [LMFeedCoreCallback] so that we can share data/events to customers code
     */
    fun setup(
        application: Application,
        domain: String? = null,
        lmFeedTheme: LMFeedSetThemeRequest? = null,
        lmFeedCoreCallback: LMFeedCoreCallback? = null
    ) {
        LMFeedTheme.setTheme(lmFeedTheme)

        val coreApplication = LMFeedCoreApplication.getInstance()
        coreApplication.initCoreApplication(application, lmFeedCoreCallback, domain)
    }

    fun showFeed(
        context: Context,
        apiKey: String,
        uuid: String,
        userName: String,
        success: (() -> Unit)?,
        error: ((String?) -> Unit)?
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d(
                "PUI",
                "calling show feed (without Security) with api key: $apiKey and uuid: $uuid and userName: $userName"
            )

            val lmFeedClient = LMFeedClient.getInstance()
            val tokens = lmFeedClient.getTokens().data

            if (tokens?.first == null || tokens.second == null) {
                Log.d("PUI", "tokens are not present")
                val initiateUserRequest = InitiateUserRequest.Builder()
                    .apiKey(apiKey)
                    .userName(userName)
                    .uuid(uuid)
                    .build()

                val response = lmFeedClient.initiateUser(initiateUserRequest)
                if (response.success) {
                    success?.let {
                        it()
                    }
                } else {
                    error?.let { it(response.errorMessage) }
                }
            } else {
                Log.d("PUI", "tokens are present")
                showFeed(context, tokens.first, tokens.second, success, error)
            }
        }
    }

    fun showFeed(
        context: Context,
        accessToken: String?,
        refreshToken: String?,
        success: (() -> Unit)?,
        error: ((String?) -> Unit)?
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d(
                "PUI",
                "calling show feed (with Security): access token: $accessToken and refresh token: $refreshToken"
            )
            val lmFeedClient = LMFeedClient.getInstance()
            val tokens = if (accessToken == null || refreshToken == null) {
                Log.d("PUI", "tokens are not received")
                lmFeedClient.getTokens().data ?: Pair("", "")
            } else {
                Log.d("PUI", "tokens are received")
                Pair(accessToken, refreshToken)
            }

            val validateUserRequest = ValidateUserRequest.Builder()
                .accessToken(tokens.first)
                .refreshToken(tokens.second)
                .build()

            val response = lmFeedClient.validateUser(validateUserRequest)
            if (response.success) {
                success?.let { it() }
            } else {
                error?.let { it(response.errorMessage) }
            }
        }
    }

    private fun saveUserPreferences(
        context: Context,
        userName: String?,
        uuid: String?,
        enablePushNotifications: Boolean
    ) {
        //save details to pref
        val userPreferences = LMFeedUserPreferences(context)
        userPreferences.apply {
            saveUserName(userName ?: "")
            saveUUID(uuid ?: "")
            savePushNotificationsEnabled(enablePushNotifications)
        }
    }
}