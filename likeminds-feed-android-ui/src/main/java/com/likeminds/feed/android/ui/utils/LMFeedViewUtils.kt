package com.likeminds.feed.android.ui.utils

import android.content.res.Resources

//view related utils class
object LMFeedViewUtils {
    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }
}