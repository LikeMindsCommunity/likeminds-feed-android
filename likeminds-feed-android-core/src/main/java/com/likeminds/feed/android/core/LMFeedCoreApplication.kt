package com.likeminds.feed.android.core

import android.app.Application
import android.content.Context
import android.util.Base64
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.likeminds.feed.android.core.utils.mediauploader.utils.LMFeedAWSKeys
import com.likeminds.likemindsfeed.LMCallback
import com.likeminds.likemindsfeed.LMFeedClient

class LMFeedCoreApplication : LMCallback {

    companion object {
        const val LOG_TAG = "LikeMindsFeedCore"
        private var coreApplicationInstance: LMFeedCoreApplication? = null
        private var lmFeedCoreCallback: LMFeedCoreCallback? = null
        private lateinit var transferUtility: TransferUtility
        private var credentialsProvider: CognitoCachingCredentialsProvider? = null
        private var s3Client: AmazonS3Client? = null

        var domain: String? = null

        /**
         * @return Singleton Instance of Core Application class
         * */
        @JvmStatic
        fun getInstance(): LMFeedCoreApplication {
            if (coreApplicationInstance == null) {
                coreApplicationInstance = LMFeedCoreApplication()
            }
            return coreApplicationInstance!!
        }

        /**
         * @return Singleton Instance of Callbacks required
         * */
        @JvmStatic
        fun getLMFeedCoreCallback(): LMFeedCoreCallback? {
            return lmFeedCoreCallback
        }

        /**
         * @return Singleton Instance of transfer utility
         * */
        @JvmStatic
        fun getTransferUtility(context: Context): TransferUtility {
            if (!this::transferUtility.isInitialized) {
                val bucketName =
                    String(Base64.decode(LMFeedAWSKeys.getBucketName(), Base64.DEFAULT))

                if (credentialsProvider == null) {
                    credentialsProvider = CognitoCachingCredentialsProvider(
                        context.applicationContext,
                        String(
                            Base64.decode(
                                LMFeedAWSKeys.getIdentityPoolId(),
                                Base64.DEFAULT
                            )
                        ), // Identity Pool ID
                        Regions.AP_SOUTH_1 // Region
                    )
                }

                if (s3Client == null) {
                    s3Client = AmazonS3Client(
                        credentialsProvider,
                        Region.getRegion(Regions.AP_SOUTH_1)
                    )
                    s3Client?.setRegion(Region.getRegion(Regions.AP_SOUTH_1))
                }

                transferUtility = TransferUtility.builder()
                    .context(context)
                    .defaultBucket(bucketName)
                    .awsConfiguration(AWSMobileClient.getInstance().configuration)
                    .s3Client(s3Client)
                    .build()
            }

            return transferUtility
        }
    }

    fun initCoreApplication(
        application: Application,
        lmFeedCoreCallback: LMFeedCoreCallback?,
        domain: String? = null
    ) {
        LMFeedClient.Builder(application)
            .lmCallback(this)
            .build()

        LMFeedCoreApplication.domain = domain
        LMFeedCoreApplication.lmFeedCoreCallback = lmFeedCoreCallback
    }

    override fun login() {
        super.login()
        lmFeedCoreCallback?.login()
    }
}