package com.likeminds.feed.android.core.search.view

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.*
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.socialfeed.adapter.LMFeedPostAdapterListener
import com.likeminds.feed.android.core.socialfeed.adapter.LMFeedSocialFeedAdapter
import com.likeminds.feed.android.core.socialfeed.model.LMFeedPostViewData
import com.likeminds.feed.android.core.utils.LMFeedEndlessRecyclerViewScrollListener
import com.likeminds.feed.android.core.utils.LMFeedViewUtils
import com.likeminds.feed.android.core.utils.base.LMFeedBaseViewType
import com.likeminds.feed.android.core.utils.video.LMFeedPostVideoAutoPlayHelper

/**
 * Represents a recycler view with list of posts in the search feed fragment
 */
class LMFeedSearchListView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    val linearLayoutManager: LinearLayoutManager
    private val dividerDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)

    private lateinit var socialFeedAdapter: LMFeedSocialFeedAdapter
    private var postVideoAutoPlayHelper: LMFeedPostVideoAutoPlayHelper? = null
    private lateinit var paginationScrollListener: LMFeedEndlessRecyclerViewScrollListener
    val itemCount: Int get() = socialFeedAdapter.itemCount

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

    //create the adapter with the provided [listener] to the search recycler view
    fun initAdapterAndSetListener(listener: LMFeedPostAdapterListener) {
        socialFeedAdapter = LMFeedSocialFeedAdapter(listener)
    }

    //sets the adapter with the provided [listener] to the search recycler view
    fun setAdapter() {
        //setting adapter
        adapter = socialFeedAdapter
    }

    //sets the pagination scroll listener to the search feed recycler view
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

    //returns the list of all the posts in the social feed adapter
    fun allPosts(): List<LMFeedBaseViewType> {
        return socialFeedAdapter.items()
    }

    //replaces the [posts] in the social feed adapter with the provided posts
    fun replacePosts(posts: List<LMFeedPostViewData>) {
        socialFeedAdapter.replace(posts)
    }

    //adds the provided [posts] in the social feed adapter
    fun addPosts(posts: List<LMFeedPostViewData>) {
        socialFeedAdapter.addAll(posts)
    }

    //removes the post at the provided [index] in the social feed adapter
    fun removePostAtIndex(index: Int) {
        socialFeedAdapter.removeIndex(index)
    }

    //clears all the posts in the social feed adapter and notifies the recycler view
    fun clearPostsAndNotify() {
        socialFeedAdapter.clearAndNotify()
    }

    /**
     * Adapter Util Block
     **/

    //get index and post from the adapter using postId
    fun getIndexAndPostFromAdapter(postId: String): Pair<Int, LMFeedPostViewData>? {
        val index = socialFeedAdapter.items().indexOfFirst {
            (it is LMFeedPostViewData) && (it.id == postId)
        }

        if (index == -1) {
            return null
        }

        val post = getPostFromAdapter(index) ?: return null

        return Pair(index, post)
    }

    //get post from the adapter using index
    fun getPostFromAdapter(position: Int): LMFeedPostViewData? {
        return socialFeedAdapter.items()[position] as? LMFeedPostViewData
    }

    //updates the post item at the provided position without notifying the recycler view
    fun updatePostWithoutNotifying(position: Int, postItem: LMFeedPostViewData) {
        socialFeedAdapter.updateWithoutNotifyingRV(position, postItem)
    }

    //returns the post item at the provided index
    fun updatePostItem(position: Int, updatedPostItem: LMFeedPostViewData) {
        socialFeedAdapter.update(position, updatedPostItem)
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