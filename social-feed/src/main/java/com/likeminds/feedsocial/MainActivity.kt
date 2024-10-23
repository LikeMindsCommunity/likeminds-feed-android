package com.likeminds.feedsocial

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
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

        lmSocialFeedAuthPreferences = LMSocialFeedAuthPreferences(this)

        binding.btnLogout.setOnClickListener {
            logout()
        }

        binding.btnStartFeed.setOnClickListener {
            val intent = Intent(this, SocialFeedActivity::class.java)
            startActivity(intent)
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