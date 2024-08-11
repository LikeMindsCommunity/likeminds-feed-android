package com.likeminds.feed.android.core.utils.user

import android.content.Context
import android.util.Log
import androidx.annotation.Keep
import com.google.firebase.messaging.FirebaseMessaging
import com.likeminds.feed.android.core.LMFeedCoreApplication.Companion.LOG_TAG
import com.likeminds.feed.android.core.utils.LMFeedCommunityUtil
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.configuration.ConfigurationClient.Companion.POST_KEY
import com.likeminds.likemindsfeed.configuration.model.ConfigurationType
import com.likeminds.likemindsfeed.helper.model.RegisterDeviceRequest
import kotlinx.coroutines.*

@Keep
class LMFeedUserMetaData {
    var domain: String? = null
    var enablePushNotifications: Boolean = false
    var deviceId: String? = null

    companion object {
        private var lmFeedUserMetaData: LMFeedUserMetaData? = null

        @JvmStatic
        fun getInstance(): LMFeedUserMetaData {
            if (lmFeedUserMetaData == null) {
                lmFeedUserMetaData = LMFeedUserMetaData()
            }
            return lmFeedUserMetaData!!
        }
    }

    //initializes the user meta data
    fun init(
        domain: String?,
        enablePushNotifications: Boolean,
        deviceId: String?
    ) {
        this.domain = domain
        this.enablePushNotifications = enablePushNotifications
        this.deviceId = deviceId
    }

    //perform actions post user session is initiated
    fun onPostSessionInit(context: Context, userName: String?, uuid: String?) {
        saveUserPreferences(context, userName, uuid)
        getMemberState()
        pushToken()
        getCommunityConfiguration()
    }

    //save user meta in preferences
    private fun saveUserPreferences(
        context: Context,
        userName: String?,
        uuid: String?,
    ) {
        //save details to pref
        val userPreferences = LMFeedUserPreferences(context)
        userPreferences.apply {
            saveUserName(userName ?: "")
            saveUUID(uuid ?: "")
        }
    }

    //get community configuration
    private fun getCommunityConfiguration() {
        CoroutineScope(Dispatchers.IO).launch {
            val lmFeedClient = LMFeedClient.getInstance()

            val response = lmFeedClient.getCommunityConfigurations()
            if (response.success) {
                val configurations = response.data?.configurations

                val feedMetaData = configurations?.firstOrNull {
                    it.type == ConfigurationType.FEED_METADATA
                }

                feedMetaData?.let {
                    val value = it.value

                    //check value has post key
                    if (value.has("post")) {
                        val variable = value.getString(POST_KEY)
                        LMFeedCommunityUtil.setPostVariable(variable)
                    } else {
                        LMFeedCommunityUtil.setPostVariable(POST_KEY)
                    }
                }
            } else {
                Log.d(LOG_TAG, "community/configuration failed -> ${response.errorMessage}")
            }
        }
    }

    //get member state
    private fun getMemberState() {
        CoroutineScope(Dispatchers.IO).launch {
            val lmFeedClient = LMFeedClient.getInstance()

            //get member state response and update the user with its rights in DB
            lmFeedClient.getMemberState().data
        }
    }

    //gets user push token and registers the token in backend
    private fun pushToken() {
        if (enablePushNotifications && !deviceId.isNullOrEmpty()) {
            try {
                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w(
                            LOG_TAG,
                            "Fetching FCM registration token failed",
                            task.exception
                        )
                        return@addOnCompleteListener
                    }

                    val token = task.result.toString()
                    registerDevice(token)
                }
            } catch (e: IllegalStateException) {
                Log.w(
                    LOG_TAG,
                    "Firebase not initialized: ${e.printStackTrace()}"
                )
            }
        }
    }

    //calls register device api to register user's push token
    private fun registerDevice(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            if (!deviceId.isNullOrEmpty()) {
                val lmFeedClient = LMFeedClient.getInstance()

                //create request
                val request = RegisterDeviceRequest.Builder()
                    .deviceId(deviceId ?: "")
                    .token(token)
                    .build()

                //call api
                lmFeedClient.registerDevice(request)
            }

        }
    }
}