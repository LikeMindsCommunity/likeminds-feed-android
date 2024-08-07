package com.likeminds.feedvideo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.likeminds.feed.android.core.LMFeedCore
import com.likeminds.feed.android.core.videofeed.view.LMFeedVideoFeedFragment
import com.likeminds.feedvideo.LMVideoFeed.Companion.LM_VIDEO_FEED_TAG
import com.likeminds.feedvideo.auth.util.LMVideoFeedAuthPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var lmVideoFeedAuthPreferences: LMVideoFeedAuthPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lmVideoFeedAuthPreferences = LMVideoFeedAuthPreferences(this)

        //with API Key Security
        callInitiateUser { accessToken, refreshToken ->
            LMFeedCore.showFeed(
                this,
                accessToken,
                refreshToken,
                success = {
                    replaceFragment()
                },
                error = {
                    Log.e(LM_VIDEO_FEED_TAG, "$it")
                }
            )
        }
    }

    private fun replaceFragment() {
        val containerViewId = R.id.frame_layout
        val fragment = LMFeedVideoFeedFragment()

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(containerViewId, fragment, containerViewId.toString())
        transaction.commit()
    }

    private fun callInitiateUser(callback: (String, String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val task = GetTokensTask()
            val tokens = task.getTokens(applicationContext, false)
            Log.d(LM_VIDEO_FEED_TAG, "tokens: $tokens")
            callback(tokens.first, tokens.second)
        }
    }
}