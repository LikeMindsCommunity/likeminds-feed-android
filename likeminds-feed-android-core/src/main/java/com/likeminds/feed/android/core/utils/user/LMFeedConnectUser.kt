package com.likeminds.feed.android.core.utils.user

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.likeminds.feed.android.core.LMFeedCoreApplication.Companion.LOG_TAG
import com.likeminds.feed.android.core.utils.LMFeedCommunityUtil
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.configuration.ConfigurationClient.Companion.POST_KEY
import com.likeminds.likemindsfeed.configuration.model.ConfigurationType
import com.likeminds.likemindsfeed.helper.model.RegisterDeviceRequest
import com.likeminds.likemindsfeed.user.model.InitiateUserRequest
import com.likeminds.likemindsfeed.user.model.InitiateUserResponse
import kotlinx.coroutines.*

/**
 * This class helps to call `initiateUser()` API, to get access and refresh token
 */
class LMFeedConnectUser private constructor(
    val apiKey: String,
    val userName: String,
    val uuid: String?,
    val deviceId: String,
    val enablePushNotifications: Boolean
) {
    companion object {

    }

    class Builder {
        private var apiKey: String = ""
        private var userName: String = ""
        private var uuid: String? = null
        private var deviceId: String = ""
        private var enablePushNotifications: Boolean = false

        fun apiKey(apiKey: String) = apply {
            this.apiKey = apiKey
        }

        fun userName(userName: String) = apply {
            this.userName = userName
        }

        fun uuid(uuid: String?) = apply {
            this.uuid = uuid
        }

        fun deviceId(deviceId: String) = apply {
            this.deviceId = deviceId
        }

        fun enablePushNotifications(enablePushNotifications: Boolean) = apply {
            this.enablePushNotifications = enablePushNotifications
        }

        fun build(): LMFeedConnectUser {
            //validate API key
            if (apiKey.isEmpty()) {
                throw IllegalAccessException("API Key is not entered, it can't be empty. Please use InitiateUser.Builder() to add API Key.")
            }

            //validate user name
            if (userName.isEmpty()) {
                throw IllegalAccessException("User name is not entered, it can't be empty. Please use InitiateUser.Builder() to add User Name.")
            }

            //validate device id
            if (deviceId.isEmpty()) {
                throw IllegalAccessException("Device Id is not entered, it can't be empty. Please use InitiateUser.Builder() to add Device Id.")

            }

            //return the instance
            return LMFeedConnectUser(
                apiKey,
                userName,
                uuid,
                deviceId,
                enablePushNotifications
            )
        }
    }

    fun toBuilder(): Builder {
        return Builder().apiKey(apiKey)
            .uuid(uuid)
            .deviceId(deviceId)
            .userName(userName)
            .enablePushNotifications(enablePushNotifications)
    }

    //initiate user API Call
    fun initiateUser(
        success: ((InitiateUserResponse?) -> Unit)? = null,
        error: ((String?) -> Unit)? = null
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val lmFeedClient = LMFeedClient.getInstance()
            val initiateRequest = InitiateUserRequest.Builder()
                .apiKey(apiKey)
                .uuid(uuid)
                .deviceId(deviceId)
                .userName(userName)
                .isGuest(false)
                .build()

            val initiateResponse = lmFeedClient.initiateUser(initiateRequest)

            Log.d(
                LOG_TAG, """
                initiate response: ${initiateResponse.success} ${initiateResponse.data?.user?.sdkClientInfo?.uuid}
            """.trimIndent()
            )

            if (initiateResponse.success) {
                pushToken()
                getCommunityConfiguration()
                success?.let { it(initiateResponse.data) }
            } else {
                error?.let { it(initiateResponse.errorMessage) }
            }
        }
    }

    //gets user push token and registers the token in backend
    private fun pushToken() {
        if (enablePushNotifications) {
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
            val lmFeedClient = LMFeedClient.getInstance()

            //create request
            val request = RegisterDeviceRequest.Builder()
                .deviceId(deviceId)
                .token(token)
                .build()

            //call api
            lmFeedClient.registerDevice(request)
        }
    }

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
}