package com.likeminds.feedvideo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.likeminds.feed.android.core.LMFeedCore
import com.likeminds.feed.android.core.utils.feed.LMFeedType
import com.likeminds.feed.android.core.videofeed.model.LMFeedVideoFeedProps
import com.likeminds.feed.android.core.videofeed.view.LMFeedVideoFeedFragment
import com.likeminds.feedvideo.auth.util.LMVideoFeedAuthPreferences
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    private lateinit var lmVideoFeedAuthPreferences: LMVideoFeedAuthPreferences

    companion object {
        const val POST_ID_TO_START_WITH = "post_id_to_start_with"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lmVideoFeedAuthPreferences = LMVideoFeedAuthPreferences(this)

        //without API Key Security
        LMFeedCore.showFeed(
            this,
            apiKey = lmVideoFeedAuthPreferences.getApiKey(),
            uuid = lmVideoFeedAuthPreferences.getUserId(),
            userName = lmVideoFeedAuthPreferences.getUserName(),
            success = {
                replaceFragment()
            },
            error = {
                Log.e("Example", "$it")
            }
        )
    }

    private fun replaceFragment() {
        val containerViewId = R.id.frame_layout

        val startFeedWithPostId = intent.getStringExtra(POST_ID_TO_START_WITH)
        val props = if (startFeedWithPostId != null) {
            LMFeedVideoFeedProps.Builder()
                .startFeedWithPostIds(listOf(startFeedWithPostId))
                .build()
        } else {
            null
        }

        val fragment = LMFeedVideoFeedFragment.getInstance(
            feedType = LMFeedType.UNIVERSAL_FEED,
            props = props
        )

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(containerViewId, fragment, containerViewId.toString())
        transaction.commit()
    }

    private fun callInitiateUser(callback: (String, String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val task = GetTokensTask()
            val tokens = task.getTokens(applicationContext, true)
            Log.d(LMVideoFeed.LM_VIDEO_FEED_TAG, "tokens: $tokens")
            callback(tokens.first, tokens.second)
        }
    }
}