package com.likeminds.feedvideo.auth.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.likeminds.feed.android.core.utils.LMFeedRoute
import com.likeminds.feed.android.core.utils.LMFeedViewUtils
import com.likeminds.feedvideo.MainActivity
import com.likeminds.feedvideo.R
import com.likeminds.feedvideo.auth.util.LMVideoFeedAuthPreferences
import com.likeminds.feedvideo.databinding.ActivityFeedVideoAuthBinding

class LMVideoFeedAuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFeedVideoAuthBinding

    private lateinit var lmVideoFeedAuthPreferences: LMVideoFeedAuthPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lmVideoFeedAuthPreferences = LMVideoFeedAuthPreferences(this)
        binding = ActivityFeedVideoAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setStatusBarColor()
        ViewCompat.setOnApplyWindowInsetsListener(binding.clVideoFeed) { view, windowInsets ->
            val innerPadding = windowInsets.getInsets(
                // Notice we're using systemBars, not statusBar
                WindowInsetsCompat.Type.systemBars()
                        // Notice we're also accounting for the display cutouts
                        or WindowInsetsCompat.Type.displayCutout()
                        // If using EditText, also add
                        or WindowInsetsCompat.Type.ime()
            )
            // Apply the insets as padding to the view. Here, set all the dimensions
            // as appropriate to your layout. You can also update the view's margin if
            // more appropriate.
            view.setPadding(0, innerPadding.top, 0, innerPadding.bottom)

            // Return CONSUMED if you don't want the window insets to keep passing down
            // to descendant views.
            WindowInsetsCompat.CONSUMED
        }

        val isLoggedIn = lmVideoFeedAuthPreferences.getIsLoggedIn()

        if (isLoggedIn) {
            // user already logged in, navigate using deep linking or to [MainActivity]
            if (intent.data != null) {
                parseDeepLink()
            } else {
                navigateToMainActivity()
            }
        } else {
            // user is not logged in, ask login details.
            loginUser()
        }
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

    // parses deep link to start corresponding activity
    private fun parseDeepLink() {
        val postId = LMFeedRoute.getPostIdFromUrl(intent.data.toString())
        if (postId != null) {
            navigateToMainActivity(postId)
        }
    }

    // navigates user to [MainActivity]
    private fun navigateToMainActivity(postId: String? = null) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(MainActivity.POST_ID_TO_START_WITH, postId)
        startActivity(intent)
        finish()
    }

    // validates user input and save login details
    private fun loginUser() {
        binding.apply {
            val context = root.context

            btnLogin.setOnClickListener {
                val apiKey = etApiKey.text.toString().trim()
                val userName = etUserName.text.toString().trim()
                val userId = etUserId.text.toString().trim()

                if (apiKey.isEmpty()) {
                    LMFeedViewUtils.showShortToast(
                        context,
                        getString(R.string.enter_api_key)
                    )
                    return@setOnClickListener
                }

                if (userName.isEmpty()) {
                    LMFeedViewUtils.showShortToast(
                        context,
                        getString(R.string.enter_user_name)
                    )
                    return@setOnClickListener
                }

                if (userId.isEmpty()) {
                    LMFeedViewUtils.showShortToast(
                        context,
                        getString(R.string.enter_user_id)
                    )
                    return@setOnClickListener
                }

                // save login details to auth prefs
                lmVideoFeedAuthPreferences.saveIsLoggedIn(true)
                lmVideoFeedAuthPreferences.saveApiKey(apiKey)
                lmVideoFeedAuthPreferences.saveUserName(userName)
                lmVideoFeedAuthPreferences.saveUserId(userId)

                navigateToMainActivity()
            }
        }
    }
}