package com.likeminds.feedsocial.auth.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.likeminds.feed.android.core.utils.LMFeedRoute
import com.likeminds.feed.android.core.utils.LMFeedViewUtils
import com.likeminds.feedsocial.MainActivity
import com.likeminds.feedsocial.R
import com.likeminds.feedsocial.auth.util.AuthPreferences
import com.likeminds.feedsocial.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    private lateinit var authPreferences: AuthPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authPreferences = AuthPreferences(this)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val isLoggedIn = authPreferences.getIsLoggedIn()

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
                authPreferences.saveIsLoggedIn(true)
                authPreferences.saveApiKey(apiKey)
                authPreferences.saveUserName(userName)
                authPreferences.saveUserId(userId)

                navigateToMainActivity()
            }
        }
    }
}