package com.likeminds.feed.android.core.universalfeed.view

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.*
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.universalfeed.adapter.LMFeedUniversalFeedAdapter
import com.likeminds.feed.android.core.universalfeed.adapter.LMFeedUniversalFeedAdapterListener
import com.likeminds.feed.android.core.universalfeed.model.LMFeedPostViewData
import com.likeminds.feed.android.core.utils.*
import com.likeminds.feed.android.core.utils.base.LMFeedBaseViewType
import com.likeminds.feed.android.core.utils.video.LMFeedPostVideoAutoPlayHelper

class LMFeedUniversalFeedListView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    val linearLayoutManager: LinearLayoutManager
    private val dividerDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)

    private lateinit var universalFeedAdapter: LMFeedUniversalFeedAdapter
    private lateinit var postVideoAutoPlayHelper: LMFeedPostVideoAutoPlayHelper
    private lateinit var paginationScrollListener: LMFeedEndlessRecyclerViewScrollListener

    val itemCount: Int get() = universalFeedAdapter.itemCount

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

        //todo: testing required
        initiateAutoPlayer()
    }

    /**
     * Initializes the [postVideoAutoPlayHelper] with the recyclerView
     * And starts observing
     **/
    private fun initiateAutoPlayer() {
        postVideoAutoPlayHelper = LMFeedPostVideoAutoPlayHelper.getInstance(this)
        postVideoAutoPlayHelper.attachScrollListenerForVideo()
        postVideoAutoPlayHelper.playMostVisibleItem()
    }

    // removes the old player and refreshes auto play
    fun refreshAutoPlayer() {
        if (!::postVideoAutoPlayHelper.isInitialized) {
            initiateAutoPlayer()
        }
        postVideoAutoPlayHelper.removePlayer()
        postVideoAutoPlayHelper.playMostVisibleItem()
    }

    // removes the player and destroys the [postVideoAutoPlayHelper]
    fun destroyAutoPlayer() {
        if (::postVideoAutoPlayHelper.isInitialized) {
            postVideoAutoPlayHelper.detachScrollListenerForVideo()
            postVideoAutoPlayHelper.destroy()
        }
    }

    fun setAdapter(
        listener: LMFeedUniversalFeedAdapterListener
    ) {
        //setting adapter
        universalFeedAdapter = LMFeedUniversalFeedAdapter(listener)
        adapter = universalFeedAdapter
    }

    fun setPaginationScrollListener(scrollListener: LMFeedEndlessRecyclerViewScrollListener) {
        paginationScrollListener = scrollListener
        addOnScrollListener(scrollListener)
    }

    fun resetScrollListenerData() {
        if (::paginationScrollListener.isInitialized) {
            paginationScrollListener.resetData()
        }
    }

    fun allPosts(): List<LMFeedBaseViewType> {
        return universalFeedAdapter.items()
    }

    fun replacePosts(
        posts: List<LMFeedPostViewData>
    ) {
        universalFeedAdapter.replace(posts)
    }

    fun addPosts(
        posts: List<LMFeedPostViewData>
    ) {
        universalFeedAdapter.addAll(posts)
    }

    fun updatePostWithoutNotifying(position: Int, postItem: LMFeedPostViewData) {
        universalFeedAdapter.updateWithoutNotifyingRV(position, postItem)
    }

    fun updatePostItem(position: Int, updatedPostItem: LMFeedPostViewData) {
        universalFeedAdapter.update(position, updatedPostItem)
    }

    fun removePostAtIndex(index: Int) {
        universalFeedAdapter.removeIndex(index)
    }

    fun clearPostsAndNotify() {
        universalFeedAdapter.clearAndNotify()
    }

    /**
     * Adapter Util Block
     **/

    //get index and post from the adapter using postId
    fun getIndexAndPostFromAdapter(postId: String): Pair<Int, LMFeedPostViewData>? {
        val index = universalFeedAdapter.items().indexOfFirst {
            (it is LMFeedPostViewData) && (it.id == postId)
        }

        if (index == -1) {
            return null
        }

        val post = getPostFromAdapter(index) ?: return null

        return Pair(index, post)
    }

    //get post from the adapter using index
    private fun getPostFromAdapter(position: Int): LMFeedPostViewData? {
        return universalFeedAdapter.items()[position] as? LMFeedPostViewData
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