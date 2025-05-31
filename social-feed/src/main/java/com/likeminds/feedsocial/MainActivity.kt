package com.likeminds.feedsocial

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.WindowInsets
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.likeminds.feed.android.core.R
import com.likeminds.feedsocial.auth.util.LMSocialFeedAuthPreferences
import com.likeminds.feedsocial.auth.view.LMSocialFeedAuthActivity
import com.likeminds.feedsocial.databinding.ActivityMainBinding
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.user.model.LogoutRequest
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private lateinit var lmSocialFeedAuthPreferences: LMSocialFeedAuthPreferences
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setStatusBarColor()
        ViewCompat.setOnApplyWindowInsetsListener(binding.clMain) { view, windowInsets ->
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

        lmSocialFeedAuthPreferences = LMSocialFeedAuthPreferences(this)

        binding.btnLogout.setOnClickListener {
            logout()
        }

        binding.btnStartFeed.setOnClickListener {
            val intent = Intent(this, SocialFeedActivity::class.java)
            startActivity(intent)
        }
    }

    @SuppressLint("InlinedApi")
    @Suppress("Deprecation")
    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
            windowInsetsController.isAppearanceLightStatusBars = true
            window.statusBarColor = ContextCompat.getColor(this, R.color.lm_feed_white)
        } else {
            val insetsController = WindowInsetsControllerCompat(window, window.decorView)
            insetsController.isAppearanceLightStatusBars = true

            window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.lm_feed_white))
            window.insetsController?.hide(WindowInsets.Type.statusBars())
            window.insetsController?.show(WindowInsets.Type.statusBars())
        }
    }

    private fun logout() {
        CoroutineScope(Dispatchers.IO).launch {
            val client = LMFeedClient.getInstance()
            val logoutResponse = client.logout(
                LogoutRequest.Builder()
                    .deviceId(deviceId())
                    .build()
            )
            if (logoutResponse.success) {
                lmSocialFeedAuthPreferences.clearPrefs()
                val intent = Intent(this@MainActivity, LMSocialFeedAuthActivity::class.java)
                finish()
                startActivity(intent)
            } else {
                Log.e("PUI", "logout failed error: ${logoutResponse.errorMessage}")
            }
        }
    }

    @SuppressLint("HardwareIds")
    fun deviceId(): String {
        return Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
            ?: ""
    }


}