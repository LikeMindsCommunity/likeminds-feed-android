package com.example.feedqna

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.feedqna.LMQnAFeed.Companion.LM_QNA_FEED_TAG
import com.example.feedqna.auth.util.LMQnAFeedAuthPreferences
import com.likeminds.feed.android.core.LMFeedCore
import com.likeminds.feed.android.core.qnafeed.view.LMFeedQnAFeedFragment
import com.likeminds.feed.android.core.utils.feed.LMFeedType.PERSONALISED_FEED
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    private lateinit var lmQnAFeedAuthPreferences: LMQnAFeedAuthPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lmQnAFeedAuthPreferences = LMQnAFeedAuthPreferences(this)

        //without API Key Security
        LMFeedCore.showFeed(
            this,
            apiKey = lmQnAFeedAuthPreferences.getApiKey(),
            uuid = lmQnAFeedAuthPreferences.getUserId(),
            userName = lmQnAFeedAuthPreferences.getUserName(),
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
        val fragment = LMFeedQnAFeedFragment.getInstance(PERSONALISED_FEED)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(containerViewId, fragment, containerViewId.toString())
        transaction.commit()
    }

    private fun callInitiateUser(callback: (String, String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val task = GetTokensTask()
            val tokens = task.getTokens(applicationContext, true)
            Log.d(LM_QNA_FEED_TAG, "tokens: $tokens")
            callback(tokens.first, tokens.second)
        }
    }
}