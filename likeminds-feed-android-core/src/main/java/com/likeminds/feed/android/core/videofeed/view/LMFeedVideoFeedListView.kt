package com.likeminds.feed.android.core.videofeed.view

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.*

/**
 * Represents a recycler view with list of posts in the video feed fragment
 */
class LMFeedVideoFeedListView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    val linearLayoutManager: LinearLayoutManager

    init {
        setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(context)
        layoutManager = linearLayoutManager

        if (itemAnimator is SimpleItemAnimator) {
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }
    }
}