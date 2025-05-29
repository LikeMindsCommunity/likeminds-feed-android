package com.likeminds.feedsocial.auth.view

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
import com.likeminds.feedsocial.MainActivity
import com.likeminds.feedsocial.R
import com.likeminds.feedsocial.auth.util.LMSocialFeedAuthPreferences
import com.likeminds.feedsocial.databinding.ActivityFeedSocialAuthBinding

class LMSocialFeedAuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFeedSocialAuthBinding

    private lateinit var lmSocialFeedAuthPreferences: LMSocialFeedAuthPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lmSocialFeedAuthPreferences = LMSocialFeedAuthPreferences(this)
        binding = ActivityFeedSocialAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setStatusBarColor()
        ViewCompat.setOnApplyWindowInsetsListener(binding.clSocialFeedAuth) { view, windowInsets ->
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

        val isLoggedIn = lmSocialFeedAuthPreferences.getIsLoggedIn()

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
        //get intent for route
        val intent = LMFeedRoute.handleDeepLink(
            this,
            intent.data.toString()
        )
        startActivity(intent)
        finish()
    }

    // navigates user to [MainActivity]
    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
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
                lmSocialFeedAuthPreferences.saveIsLoggedIn(true)
                lmSocialFeedAuthPreferences.saveApiKey(apiKey)
                lmSocialFeedAuthPreferences.saveUserName(userName)
                lmSocialFeedAuthPreferences.saveUserId(userId)

                navigateToMainActivity()
            }
        }
    }
}