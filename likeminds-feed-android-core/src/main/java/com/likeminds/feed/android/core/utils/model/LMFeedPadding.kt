package com.likeminds.feed.android.core.utils.model

import androidx.annotation.DimenRes
import androidx.annotation.Keep

@Keep
data class LMFeedPadding(
    @DimenRes val paddingLeft: Int,
    @DimenRes val paddingTop: Int,
    @DimenRes val paddingRight: Int,
    @DimenRes val paddingBottom: Int
)