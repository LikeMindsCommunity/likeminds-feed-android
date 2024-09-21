package com.likeminds.feed.android.core.qnafeed.view

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.*
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.socialfeed.adapter.LMFeedPostAdapterListener
import com.likeminds.feed.android.core.utils.LMFeedEndlessRecyclerViewScrollListener
import com.likeminds.feed.android.core.utils.LMFeedViewUtils
import com.likeminds.feed.android.core.utils.video.LMFeedPostVideoAutoPlayHelper

/**
 * Represents a recycler view with list of posts in the qna feed fragment
 */
class LMFeedQnAFeedListView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    val linearLayoutManager: LinearLayoutManager
    private val dividerDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)

    private var postVideoAutoPlayHelper: LMFeedPostVideoAutoPlayHelper? = null
    private lateinit var paginationScrollListener: LMFeedEndlessRecyclerViewScrollListener

    init {
        setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(context)
        layoutManager = linearLayoutManager

        if (itemAnimator is SimpleItemAnimator) {
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }

        //item decorator to add spacing between items
        val dividerDrawable = ContextCompat.getDrawable(
            context,
            R.drawable.lm_feed_item_divider
        )
        dividerDrawable?.let { drawable ->
            dividerDecoration.setDrawable(drawable)
        }

        addItemDecoration(dividerDecoration)
    }

    /**
     * Initializes the [postVideoAutoPlayHelper] with the recyclerView
     * And starts observing
     **/
    fun initiateVideoAutoPlayer() {
        postVideoAutoPlayHelper = LMFeedPostVideoAutoPlayHelper.getInstance(this)
        postVideoAutoPlayHelper?.attachScrollListenerForVideo()
        postVideoAutoPlayHelper?.playMostVisibleItem()
    }

    // removes the old player and refreshes auto play
    fun refreshVideoAutoPlayer() {
        if (postVideoAutoPlayHelper == null) {
            initiateVideoAutoPlayer()
        }
        postVideoAutoPlayHelper?.removePlayer()
        postVideoAutoPlayHelper?.playMostVisibleItem()
    }

    // removes the player and destroys the [postVideoAutoPlayHelper]
    fun destroyVideoAutoPlayer() {
        if (postVideoAutoPlayHelper != null) {
            postVideoAutoPlayHelper?.detachScrollListenerForVideo()
            postVideoAutoPlayHelper?.destroy()
            postVideoAutoPlayHelper = null
        }
    }

    //create the adapter with the provided [listener] to the universal feed recycler view
    fun initAdapterAndSetListener(listener: LMFeedPostAdapterListener) {
        //todo:
    }

    //sets the adapter with the provided [listener] to the universal feed recycler view
    fun setAdapter() {
        //setting adapter
        //todo:
    }

    //sets the pagination scroll listener to the social feed recycler view
    fun setPaginationScrollListener(scrollListener: LMFeedEndlessRecyclerViewScrollListener) {
        paginationScrollListener = scrollListener
        addOnScrollListener(scrollListener)
    }

    //resets the scroll listener data
    fun resetScrollListenerData() {
        if (::paginationScrollListener.isInitialized) {
            paginationScrollListener.resetData()
        }
    }

    /**
     * Scroll to a position with offset from the top header
     * @param position Index of the item to scroll to
     */
    fun scrollToPositionWithOffset(position: Int) {
        post {
            val px = (LMFeedViewUtils.dpToPx(75) * 1.5).toInt()
            (layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(
                position,
                px
            )
        }
    }
}