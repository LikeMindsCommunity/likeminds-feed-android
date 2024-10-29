package com.example.feedqna

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.feedqna.LMQnAFeed.Companion.LM_QNA_FEED_TAG
import com.likeminds.feed.android.core.LMFeedCore
import com.likeminds.feed.android.core.qnafeed.view.LMFeedQnAFeedFragment
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
                    Log.e(LM_QNA_FEED_TAG, "$it")
                }
            )
        }
    }

    private fun replaceFragment() {
        val containerViewId = R.id.frame_layout
        val fragment = LMFeedQnAFeedFragment()

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