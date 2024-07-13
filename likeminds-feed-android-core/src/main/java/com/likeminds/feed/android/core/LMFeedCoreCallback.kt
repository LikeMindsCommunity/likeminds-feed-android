package com.likeminds.feed.android.core

import com.likeminds.feed.android.core.utils.user.LMFeedUserViewData

interface LMFeedCoreCallback {

    fun openProfile(user: LMFeedUserViewData) {
        //implement to open your profile page with user data
    }

    fun trackEvent(eventName: String, eventProperties: Map<String, String?> = mapOf()) {
        //implement to track events triggered feed
    }

    fun openProfileWithUUID(uuid: String) {
        //implement to open the profile of the user with uuid = [uuid]
    }

    fun onAccessTokenExpiredAndRefreshed(accessToken: String, refreshToken: String) {
        //implement to handle access token refresh
    }

    fun onRefreshTokenExpired(): Pair<String?, String?> {
        //implement to handle refresh token refresh
        return Pair(null, null)
    }
}