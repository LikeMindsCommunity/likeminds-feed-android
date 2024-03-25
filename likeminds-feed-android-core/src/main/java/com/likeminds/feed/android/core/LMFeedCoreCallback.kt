package com.likeminds.feed.android.core

import com.likeminds.feed.android.core.utils.user.LMFeedUserViewData

interface LMFeedCoreCallback {

    fun login() {
        //implement to re-login user when refresh token expires
    }

    fun openProfile(user: LMFeedUserViewData) {
        //implement to open your profile page with user data
    }

    fun trackEvent(eventName: String, eventProperties: Map<String, String?> = mapOf()) {
        //implement to track events triggered feed
    }
}