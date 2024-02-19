package com.likeminds.feed.android.core

import android.app.Application
import com.likeminds.feed.android.core.ui.theme.LMFeedTheme
import com.likeminds.feed.android.core.ui.theme.model.LMFeedSetThemeRequest

object LMFeedCore {

    private var apiKey: String? = null

    fun setup(
        application: Application,
        apiKey: String,
        lmFeedTheme: LMFeedSetThemeRequest,
        lmFeedCoreCallback: LMFeedCoreCallback? = null
    ) {
        this.apiKey = apiKey
        LMFeedTheme.setTheme(lmFeedTheme)

        val coreApplication = LMFeedCoreApplication.getInstance()
        coreApplication.initCoreApplication(application, lmFeedCoreCallback)
    }
}