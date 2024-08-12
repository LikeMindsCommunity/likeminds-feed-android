package com.likeminds.feed.android.core.pushnotification

import android.app.*
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.pushnotification.model.LMFeedNotificationActionData
import com.likeminds.feed.android.core.ui.theme.LMFeedTheme
import com.likeminds.feed.android.core.utils.LMFeedRoute
import com.likeminds.feed.android.core.utils.analytics.LMFeedAnalytics
import org.json.JSONObject

class LMFeedNotificationHandler {

    private lateinit var mApplication: Application

    //icon of notification
    private var notificationIcon: Int = 0

    //color of notification text
    private var notificationTextColor: Int = 0

    companion object {
        private var notificationHandler: LMFeedNotificationHandler? = null

        const val LM_FEED_GENERAL_CHANNEL_ID = "lm_feed_notification_general"
        const val LM_FEED_NOTIFICATION_TITLE = "title"
        const val LM_FEED_NOTIFICATION_SUB_TITLE = "sub_title"
        const val LM_FEED_NOTIFICATION_ROUTE = "route"
        const val LM_FEED_NOTIFICATION_CATEGORY = "category"
        const val LM_FEED_NOTIFICATION_SUBCATEGORY = "subcategory"

        private const val LM_FEED_NOTIFICATION_DATA = "notification_data"

        @JvmStatic
        fun getInstance(): LMFeedNotificationHandler {
            if (notificationHandler == null) {
                notificationHandler = LMFeedNotificationHandler()
            }
            return notificationHandler!!
        }
    }


    //create the instance of the handler and channel for notification
    fun create(application: Application) {
        mApplication = application

        notificationIcon = LMFeedTheme.getNotificationIcon() ?: com.likeminds.customgallery.R.drawable.ic_notification

        notificationTextColor =
            LMFeedTheme.getNotificationTextColor() ?: LMFeedTheme.getButtonColor()

        createNotificationChannel()
    }

    //create notification channel
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createGeneralNotificationChannel()
        }
    }

    private fun createGeneralNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = mApplication.getString(R.string.lm_feed_general_channel_name)
            val descriptionText =
                mApplication.getString(R.string.lm_feed_general_channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(LM_FEED_GENERAL_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                mApplication.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    //handle and show notification
    fun handleNotification(data: MutableMap<String, String>) {
        val title = data[LM_FEED_NOTIFICATION_TITLE] ?: return
        val subTitle = data[LM_FEED_NOTIFICATION_SUB_TITLE] ?: return
        val route = data[LM_FEED_NOTIFICATION_ROUTE] ?: return
        val category = data[LM_FEED_NOTIFICATION_CATEGORY]
        val subcategory = data[LM_FEED_NOTIFICATION_SUBCATEGORY]

        //validate data
        if (category.isNullOrEmpty() && subcategory.isNullOrEmpty()) {
            return
        }

        //create payload for analytics event
        val payloadJson = JSONObject().apply {
            put(LM_FEED_NOTIFICATION_TITLE, title)
            put(LM_FEED_NOTIFICATION_SUB_TITLE, subTitle)
            put(LM_FEED_NOTIFICATION_ROUTE, route)
        }

        LMFeedAnalytics.track(
            LMFeedAnalytics.LMFeedEvents.NOTIFICATION_RECEIVED, hashMapOf(
                Pair("payload", payloadJson.toString()),
                Pair(LM_FEED_NOTIFICATION_CATEGORY, category),
                Pair(LM_FEED_NOTIFICATION_SUBCATEGORY, subcategory)
            )
        )
        //show notifications
        sendNormalNotification(
            mApplication,
            title,
            subTitle,
            route,
            category,
            subcategory
        )
    }

    /**
     * create pending intent and show notifications accordingly
     * */
    private fun sendNormalNotification(
        context: Context,
        title: String,
        subTitle: String,
        route: String,
        category: String?,
        subcategory: String?,
    ) {
        // notificationId is a unique int for each notification that you must define
        val notificationId = route.hashCode()
        val resultPendingIntent: PendingIntent? =
            getRoutePendingIntent(
                context,
                notificationId,
                route,
                title,
                subTitle,
                category,
                subcategory
            )
        val notificationBuilder = NotificationCompat.Builder(mApplication, LM_FEED_GENERAL_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(subTitle)
            .setSmallIcon(notificationIcon)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(subTitle))
            .setDefaults(NotificationCompat.DEFAULT_ALL)
        if (resultPendingIntent != null) {
            notificationBuilder.setContentIntent(resultPendingIntent)
        }
        with(NotificationManagerCompat.from(mApplication)) {
            notify(notificationId, notificationBuilder.build())
        }
    }

    //create pending intent as per route in notification
    private fun getRoutePendingIntent(
        context: Context,
        notificationId: Int,
        route: String,
        notificationTitle: String,
        notificationMessage: String,
        category: String?,
        subcategory: String?,
    ): PendingIntent? {
        //get intent for route
        val intent = LMFeedRoute.getRouteIntent(
            context,
            route,
            0,
            LMFeedAnalytics.LMFeedSource.NOTIFICATION
        )

        if (intent?.getBundleExtra("lm_feed_bundle") != null) {
            intent.getBundleExtra("lm_feed_bundle")!!.putParcelable(
                LM_FEED_NOTIFICATION_DATA,
                LMFeedNotificationActionData.Builder()
                    .groupRoute(route)
                    .childRoute(route)
                    .notificationTitle(notificationTitle)
                    .notificationMessage(notificationMessage)
                    .category(category)
                    .subcategory(subcategory)
                    .build()
            )
        } else {
            intent?.putExtra(
                LM_FEED_NOTIFICATION_DATA, LMFeedNotificationActionData.Builder()
                    .groupRoute(route)
                    .childRoute(route)
                    .notificationTitle(notificationTitle)
                    .notificationMessage(notificationMessage)
                    .category(category)
                    .subcategory(subcategory)
                    .build()
            )
        }

        var resultPendingIntent: PendingIntent? = null
        if (intent != null) {
            resultPendingIntent = PendingIntent.getActivity(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        return resultPendingIntent
    }
}
