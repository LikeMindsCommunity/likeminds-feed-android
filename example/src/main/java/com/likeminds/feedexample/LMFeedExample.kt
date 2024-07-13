package com.likeminds.feedexample

import android.annotation.SuppressLint
import android.app.Application
import android.provider.Settings
import android.util.Log
import com.likeminds.feed.android.core.LMFeedCore
import com.likeminds.feed.android.core.LMFeedCoreCallback
import kotlinx.coroutines.runBlocking


class LMFeedExample : Application(), LMFeedCoreCallback {

    companion object {
        const val LM_FEED_EXAMPLE_TAG = "LMFeedExample"
    }

    @SuppressLint("HardwareIds")
    override fun onCreate() {
        super.onCreate()

        val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID) ?: ""
        LMFeedCore.setup(
            application = this,
            enablePushNotifications = true,
            deviceId = deviceId,
            domain = "https://www.examplefeed.com",
            lmFeedCoreCallback = this
        )
    }

    override fun onAccessTokenExpiredAndRefreshed(accessToken: String, refreshToken: String) {
        Log.d(
            LM_FEED_EXAMPLE_TAG, """
            Example Layer Callback -> onAccessTokenExpiredAndRefreshed
            accessToken: $accessToken
            refreshToken: $refreshToken
        """.trimIndent()
        )
    }

    override fun onRefreshTokenExpired(): Pair<String?, String?> {
        return runBlocking {
            Log.d(
                LM_FEED_EXAMPLE_TAG, """
                Example Layer Callback -> onRefreshTokenExpired
            """.trimIndent()
            )

            val task = GetTokensTask()
            val tokens = task.getTokens(applicationContext, false)
            Log.d(LM_FEED_EXAMPLE_TAG, "tokens: $tokens")
            tokens
        }
    }
}