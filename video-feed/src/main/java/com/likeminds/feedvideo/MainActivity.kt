package com.likeminds.feedvideo

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
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

        setStatusBarColor()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.frame_layout)) { view, windowInsets ->
            val innerPadding = windowInsets.getInsets(
                // Notice we're using systemBars, not statusBar
                WindowInsetsCompat.Type.systemBars()
                        // Notice we're also accounting for the display cutouts
                        or WindowInsetsCompat.Type.displayCutout()
            )
            // Apply the insets as padding to the view. Here, set all the dimensions
            // as appropriate to your layout. You can also update the view's margin if
            // more appropriate.
            view.setPadding(0, innerPadding.top, 0, innerPadding.bottom)

            // Return CONSUMED if you don't want the window insets to keep passing down
            // to descendant views.
            WindowInsetsCompat.CONSUMED
        }

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

    @SuppressLint("InlinedApi")
    @Suppress("Deprecation")
    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
            windowInsetsController.isAppearanceLightStatusBars = true
            window.statusBarColor = ContextCompat.getColor(this, com.likeminds.feed.android.core.R.color.lm_feed_white)
        } else {
            val insetsController = WindowInsetsControllerCompat(window, window.decorView)
            insetsController.isAppearanceLightStatusBars = true

            window.decorView.setBackgroundColor(ContextCompat.getColor(this, com.likeminds.feed.android.core.R.color.lm_feed_white))
            window.insetsController?.hide(WindowInsets.Type.statusBars())
            window.insetsController?.show(WindowInsets.Type.statusBars())
        }
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