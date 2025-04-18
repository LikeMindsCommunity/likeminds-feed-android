package com.likeminds.feed.android.core.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import com.likeminds.feed.android.core.post.create.model.LMFeedCreatePostExtras
import com.likeminds.feed.android.core.post.create.view.LMFeedCreatePostActivity
import com.likeminds.feed.android.core.post.detail.model.LMFeedPostDetailExtras
import com.likeminds.feed.android.core.post.detail.view.LMFeedPostDetailActivity
import com.likeminds.feed.android.core.utils.analytics.LMFeedAnalytics

object LMFeedRoute {

    private const val ROUTE_POST_DETAIL = "post_detail"
    private const val ROUTE_FEED = "feed"
    private const val ROUTE_CREATE_POST = "create_post"
    private const val PARAM_POST_ID = "post_id"
    private const val ROUTE_BROWSER = "browser"
    private const val PARAM_COMMENT_ID = "comment_id"

    private const val DEEP_LINK_POST = "post"

    private const val HTTPS_SCHEME = "https"
    private const val HTTP_SCHEME = "http"
    private const val ROUTE_SCHEME = "route"

    fun getRouteIntent(
        context: Context,
        routeString: String,
        flags: Int = 0,
        source: String? = null
    ): Intent? {
        val route = Uri.parse(routeString)
        var intent: Intent? = null
        when (route.host) {
            ROUTE_POST_DETAIL -> {
                intent = getRouteToPostDetail(
                    context,
                    route,
                    source
                )
            }

            ROUTE_FEED -> {
                // navigation to feed
            }

            ROUTE_CREATE_POST -> {
                intent = getRouteToCreatePost(context, source)
            }

            ROUTE_BROWSER -> {
                intent = getRouteToBrowser(route)
            }
        }
        if (intent != null) {
            intent.flags = flags
        }
        return intent
    }

    // route://post_detail?post_id=<post_id>&comment_id=<comment_id of new comment>
    private fun getRouteToPostDetail(
        context: Context,
        route: Uri,
        source: String?
    ): Intent {
        val postId = route.getQueryParameter(PARAM_POST_ID)
        val commentId = route.getQueryParameter(PARAM_COMMENT_ID)

        val builder = LMFeedPostDetailExtras.Builder()
            .postId(postId.toString())
            .isEditTextFocused(false)
            .commentId(commentId)
            .source(source)
            .build()

        return LMFeedPostDetailActivity.getIntent(context, builder)
    }

    // route://create_post
    private fun getRouteToCreatePost(
        context: Context,
        source: String?
    ): Intent {
        val extras = LMFeedCreatePostExtras.Builder()
            .source(source)
            .build()

        return LMFeedCreatePostActivity.getIntent(context, extras)
    }

    private fun getRouteToBrowser(route: Uri): Intent {
        return Intent(Intent.ACTION_VIEW, route.getQueryParameter("link")?.toUri())
    }

    // creates route for url and returns corresponding intent
    fun handleDeepLink(context: Context, url: String?): Intent? {
        val data = url?.toUri()?.normalizeScheme() ?: return null
        val firstPath = getRouteFromDeepLink(data) ?: return null
        return getRouteIntent(
            context,
            firstPath,
            0,
            source = LMFeedAnalytics.LMFeedSource.DEEP_LINK
        )
    }

    /**
     * Get [postId] from url from query params, if url is off type "<domain>/post?post_id=<post_id>"
     */
    fun getPostIdFromUrl(url: String?): String? {
        val data = url?.toUri()?.normalizeScheme() ?: return null
        return data.getQueryParameter(PARAM_POST_ID)
    }

    //create route string as per uri
    private fun getRouteFromDeepLink(data: Uri?): String? {
        if (data == null) {
            return null
        }
        return when (data.pathSegments.firstOrNull()) {
            DEEP_LINK_POST -> {
                createPostDetailRoute(data)
            }

            else -> {
                createWebsiteRoute(data)
            }
        }
    }

    // https://<domain>/post/post_id=<post_id>
    private fun createPostDetailRoute(data: Uri): String? {
        val postId = data.getQueryParameter(PARAM_POST_ID) ?: return null
        return Uri.Builder()
            .scheme(ROUTE_SCHEME)
            .authority(ROUTE_POST_DETAIL)
            .appendQueryParameter(PARAM_POST_ID, postId)
            .build()
            .toString()
    }

    // creates a website route for the provided url
    private fun createWebsiteRoute(data: Uri): String? {
        if (data.scheme == HTTPS_SCHEME || data.scheme == HTTP_SCHEME) {
            return Uri.Builder()
                .scheme(ROUTE_SCHEME)
                .authority(ROUTE_BROWSER)
                .appendQueryParameter("link", data.toString())
                .build()
                .toString()
        }
        return null
    }
}