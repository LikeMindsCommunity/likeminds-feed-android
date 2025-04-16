package com.likeminds.feedvideo.auth.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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