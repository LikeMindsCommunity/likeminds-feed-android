package com.likeminds.feedsocial

import android.content.Context
import android.util.Log
import com.likeminds.feedsocial.LMSocialFeed.Companion.LM_SOCIAL_FEED_TAG
import com.likeminds.feedsocial.auth.util.LMSocialFeedAuthPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class GetTokensTask {

    private lateinit var lmSocialFeedAuthPreferences: LMSocialFeedAuthPreferences

    // Get tokens from the server
    suspend fun getTokens(context: Context, isProd: Boolean): Pair<String, String> {
        return withContext(Dispatchers.IO) {
            //get api url
            val apiUrl = if (isProd) {
                "https://auth.likeminds.community/sdk/initiate"
            } else {
                "https://betaauth.likeminds.community/sdk/initiate"
            }

            lmSocialFeedAuthPreferences = LMSocialFeedAuthPreferences(context)

            // Create connection
            val url = URL(apiUrl)
            val connection = url.openConnection() as HttpURLConnection

            // Set HTTP method and required headers
            connection.apply {
                requestMethod = "POST"
                doOutput = true
                setRequestProperty(
                    "Content-Type",
                    "application/json"
                )
                setRequestProperty("x-api-key", lmSocialFeedAuthPreferences.getApiKey())
                setRequestProperty("x-version-code", "15")
                setRequestProperty("x-platform-code", "an")
                setRequestProperty("x-sdk-source", "feed")
            }

            // Create request body
            val request = JSONObject().apply {
                put("uuid", lmSocialFeedAuthPreferences.getUserId())
                put("user_name", lmSocialFeedAuthPreferences.getUserName())
                put("token_expiry_beta", 1)
                put("rtm_token_expiry_beta", 2)
            }

            // Write POST data
            val writer = OutputStreamWriter(connection.outputStream)
            writer.write(request.toString())
            writer.flush()
            writer.close()

            // Get response
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()
                response.toString()

                val responseObject = JSONObject(response.toString())
                val data = responseObject.getJSONObject("data")
                val accessToken = data.getString("access_token")
                val refreshToken = data.getString("refresh_token")
                Pair(accessToken, refreshToken)
            } else {
                Log.e(LM_SOCIAL_FEED_TAG, "Error: HTTP $responseCode")
                Pair("", "")
            }
        }
    }
}