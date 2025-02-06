package com.likeminds.feed.android.core.utils

import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class LMFeedRecyclerViewScrollStateListener(private val mLinearLayoutManager: LinearLayoutManager) :
    RecyclerView.OnScrollListener() {

    private var scrollHandler: Handler? = null
    private var scrollRunnable: Runnable? = null

    companion object {
        private const val SCROLL_DEBOUNCE = 5000L
        private const val ITEM_VISIBILITY_PERCENTAGE = 40
    }

    init {
        scrollHandler = Handler(Looper.getMainLooper())
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        // Loop through visible items
        val firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition()
        val lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition()

        for (i in firstVisibleItem..lastVisibleItem) {
            // Get the view for the current item
            val view = mLinearLayoutManager.findViewByPosition(i) ?: continue

            // Calculate visibility percentage
            val itemHeight = view.height
            val visibleHeight = getVisibleHeight(view)

            val visibilityPercentage = (visibleHeight.toFloat() / itemHeight.toFloat()) * 100

            // Check if the item is at least 40% visible
            if (visibilityPercentage >= ITEM_VISIBILITY_PERCENTAGE) {
                // Perform action for 40% visible items
                onItemVisibleMoreThan40Percent(i)
            }
        }
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)

        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            scrollRunnable?.let {
                scrollHandler?.removeCallbacks(it)
            }

            scrollRunnable = Runnable {
                onScrollStateIdleReached()
            }
            scrollHandler?.postDelayed(scrollRunnable!!, SCROLL_DEBOUNCE)
        }
    }

    // resets the listener
    fun resetListener() {
        scrollHandler = null
        scrollRunnable = null
    }

    // Helper function to calculate visible height of an item
    private fun getVisibleHeight(view: View): Int {
        val rect = Rect()
        view.getGlobalVisibleRect(rect)

        val visibleHeight = rect.bottom - rect.top
        return visibleHeight.coerceAtLeast(0)
    }

    abstract fun onItemVisibleMoreThan40Percent(position: Int)
    abstract fun onScrollStateIdleReached()
}