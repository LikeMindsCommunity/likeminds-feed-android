package com.likeminds.feedsocial

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.likeminds.feed.android.core.LMFeedCore
import com.likeminds.feed.android.core.socialfeed.view.LMFeedSocialFeedFragment
import com.likeminds.feed.android.core.utils.feed.LMFeedType.PERSONALISED_FEED
import com.likeminds.feedsocial.LMSocialFeed.Companion.LM_SOCIAL_FEED_TAG
import com.likeminds.feedsocial.auth.util.LMSocialFeedAuthPreferences
import kotlinx.coroutines.*

class SocialFeedActivity : AppCompatActivity() {

    private lateinit var lmSocialFeedAuthPreferences: LMSocialFeedAuthPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_social_feed)

        lmSocialFeedAuthPreferences = LMSocialFeedAuthPreferences(this)

        //without API Key Security
        LMFeedCore.showFeed(
            this,
            apiKey = lmSocialFeedAuthPreferences.getApiKey(),
            uuid = lmSocialFeedAuthPreferences.getUserId(),
            userName = lmSocialFeedAuthPreferences.getUserName(),
            success = {
                replaceFragment()
            },
            error = {
                Log.e("Example", "$it")
            }
        )

        //with API Key Security
//        callInitiateUser { accessToken, refreshToken ->
//            LMFeedCore.showFeed(
//                this,
//                accessToken,
//                refreshToken,
//                success = {
//                    replaceFragment()
//                },
//                error = {
//                    Log.e(LM_SOCIAL_FEED_TAG, "$it")
//                }
//            )
//        }
    }

    private fun replaceFragment() {
        val containerViewId = R.id.frame_layout
        val fragment = LMFeedSocialFeedFragment.getInstance(PERSONALISED_FEED)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(containerViewId, fragment, containerViewId.toString())
        transaction.commit()
    }

    private fun callInitiateUser(callback: (String, String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val task = GetTokensTask()
            val tokens = task.getTokens(applicationContext, true)
            Log.d(LM_SOCIAL_FEED_TAG, "tokens: $tokens")
            callback(tokens.first, tokens.second)
        }
    }
}