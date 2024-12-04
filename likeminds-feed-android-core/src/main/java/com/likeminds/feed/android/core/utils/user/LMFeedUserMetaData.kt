package com.likeminds.feed.android.core.utils.user

import android.content.Context
import android.util.Log
import androidx.annotation.Keep
import com.google.firebase.messaging.FirebaseMessaging
import com.likeminds.feed.android.core.LMFeedCoreApplication.Companion.LOG_TAG
import com.likeminds.feed.android.core.utils.LMFeedCommunityUtil
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.configuration.ConfigurationClient.Companion.COMMENT_KEY
import com.likeminds.likemindsfeed.configuration.ConfigurationClient.Companion.LIKE_ENTITY_VARIABLE_KEY
import com.likeminds.likemindsfeed.configuration.ConfigurationClient.Companion.POST_KEY
import com.likeminds.likemindsfeed.configuration.model.ConfigurationType
import com.likeminds.likemindsfeed.helper.model.RegisterDeviceRequest
import kotlinx.coroutines.*
import org.json.JSONObject

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
    fun onPostSessionInit(
        context: Context,
        userName: String?,
        uuid: String?,
        userImage: String?
    ) {
        saveUserPreferences(
            context,
            userName,
            uuid,
            userImage
        )
        getMemberState()
        pushToken()
        getCommunityConfiguration()
    }

    //save user meta in preferences
    private fun saveUserPreferences(
        context: Context,
        userName: String?,
        uuid: String?,
        userImage: String?
    ) {
        //save details to pref
        val userPreferences = LMFeedUserPreferences(context)
        userPreferences.apply {
            saveUserName(userName ?: "")
            saveUUID(uuid ?: "")
            setUserImage(userImage ?: "")
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
                    if (value.has(POST_KEY)) {
                        val variable = value.getString(POST_KEY)
                        LMFeedCommunityUtil.setPostVariable(variable)
                    } else {
                        LMFeedCommunityUtil.setPostVariable(POST_KEY)
                    }

                    //check value has like key
                    if (value.has(LIKE_ENTITY_VARIABLE_KEY)) {
                        val likeEntity =
                            (value.get(LIKE_ENTITY_VARIABLE_KEY) as JSONObject)


                        if (likeEntity.has("entity_name")) {
                            LMFeedCommunityUtil.setLikeVariable(likeEntity.getString("entity_name"))
                        }

                        if (likeEntity.has("past_tense_verb")) {
                            LMFeedCommunityUtil.setLikePastTenseVariable(likeEntity.getString("past_tense_verb"))
                        }
                    }

                    //check value has comment key
                    if (value.has(COMMENT_KEY)) {
                        val variable = value.getString(COMMENT_KEY)
                        LMFeedCommunityUtil.setCommentVariable(variable)
                    } else {
                        LMFeedCommunityUtil.setCommentVariable(COMMENT_KEY)
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