package com.likeminds.feed.android.core.post.detail.adapter

import android.view.View
import com.likeminds.feed.android.core.post.detail.adapter.databinders.LMFeedItemReplyViewDataBinder
import com.likeminds.feed.android.core.post.detail.adapter.databinders.LMFeedItemViewMoreReplyViewDataBinder
import com.likeminds.feed.android.core.post.detail.model.LMFeedCommentViewData
import com.likeminds.feed.android.core.post.detail.model.LMFeedViewMoreReplyViewData
import com.likeminds.feed.android.core.utils.base.*

class LMFeedReplyAdapter(
    private val replyAdapterListener: LMFeedReplyAdapterListener
) : LMFeedBaseRecyclerAdapter<LMFeedBaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): MutableList<LMFeedViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<LMFeedViewDataBinder<*, *>>(2)

        val itemReplyViewDataBinder = LMFeedItemReplyViewDataBinder(replyAdapterListener)
        viewDataBinders.add(itemReplyViewDataBinder)

        val itemViewMoreReplyViewDataBinder =
            LMFeedItemViewMoreReplyViewDataBinder(replyAdapterListener)
        viewDataBinders.add(itemViewMoreReplyViewDataBinder)

        return viewDataBinders
    }
}

interface LMFeedReplyAdapterListener {

    fun onReplyContentSeeMoreClicked(position: Int, replyViewData: LMFeedCommentViewData) {
        //triggered when the user clicks on "See More" on comment content
    }

    fun onReplyLiked(position: Int, replyViewData: LMFeedCommentViewData) {
        //triggered when the reply is liked
    }

    fun onViewMoreRepliesClicked(
        position: Int,
        viewMoreReplyViewData: LMFeedViewMoreReplyViewData
    ) {
        //triggered when the view more button in replies list is clicked
    }

    fun onReplyLikesCountClicked(position: Int, replyViewData: LMFeedCommentViewData) {
        //triggered when the user clicks on the reply's likes count
    }

    fun onReplyMenuIconClicked(
        position: Int,
        anchorView: View,
        replyViewData: LMFeedCommentViewData
    ) {
        //triggered when the menu item of any reply is clicked
    }

    fun onReplyContentLinkClicked(url: String) {
        //triggered when link in the reply content is clicked
    }

    fun onReplierHeaderClicked(position: Int, replyViewData: LMFeedCommentViewData) {
        //triggered when the header of the replier is clicked
    }
}