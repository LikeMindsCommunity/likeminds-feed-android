package com.likeminds.feed.android.core.post.detail.adapter

import android.view.View
import com.likeminds.feed.android.core.post.detail.adapter.databinders.*
import com.likeminds.feed.android.core.post.detail.model.LMFeedCommentViewData
import com.likeminds.feed.android.core.universalfeed.adapter.LMFeedUniversalFeedAdapterListener
import com.likeminds.feed.android.core.universalfeed.adapter.databinders.*
import com.likeminds.feed.android.core.utils.LMFeedValueUtils.getItemInList
import com.likeminds.feed.android.core.utils.base.*

class LMFeedPostDetailAdapter(
    private val universalFeedAdapterListener: LMFeedUniversalFeedAdapterListener,
    private val postDetailAdapterListener: LMFeedPostDetailAdapterListener,
    private val replyAdapterListener: LMFeedReplyAdapterListener
) : LMFeedBaseRecyclerAdapter<LMFeedBaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): MutableList<LMFeedViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<LMFeedViewDataBinder<*, *>>(8)

        val itemCommentsCountViewDataBinder = LMFeedItemCommentsCountViewDataBinder()
        viewDataBinders.add(itemCommentsCountViewDataBinder)

        val itemCommentViewDataBinder =
            LMFeedItemCommentViewDataBinder(postDetailAdapterListener, replyAdapterListener)
        viewDataBinders.add(itemCommentViewDataBinder)

        val itemPostTextOnlyBinder =
            LMFeedItemPostTextOnlyViewDataBinder(universalFeedAdapterListener)
        viewDataBinders.add(itemPostTextOnlyBinder)

        val itemPostSingleImageViewDataBinder =
            LMFeedItemPostSingleImageViewDataBinder(universalFeedAdapterListener)
        viewDataBinders.add(itemPostSingleImageViewDataBinder)

        val itemPostSingleVideoViewDataBinder =
            LMFeedItemPostSingleVideoViewDataBinder(universalFeedAdapterListener)
        viewDataBinders.add(itemPostSingleVideoViewDataBinder)

        val itemPostLinkViewDataBinder =
            LMFeedItemPostLinkViewDataBinder(universalFeedAdapterListener)
        viewDataBinders.add(itemPostLinkViewDataBinder)

        val lmFeedItemPostDocumentsViewDataBinder =
            LMFeedItemPostDocumentsViewDataBinder(universalFeedAdapterListener)
        viewDataBinders.add(lmFeedItemPostDocumentsViewDataBinder)

        val itemPostMultipleMediaViewDataBinder =
            LMFeedItemPostMultipleMediaViewDataBinder(universalFeedAdapterListener)
        viewDataBinders.add(itemPostMultipleMediaViewDataBinder)

        val itemNoCommentsFoundBinder = LMFeedItemNoCommentsFoundViewDataBinder()
        viewDataBinders.add(itemNoCommentsFoundBinder)

        return viewDataBinders
    }

    operator fun get(position: Int): LMFeedBaseViewType? {
        return items().getItemInList(position)
    }
}

interface LMFeedPostDetailAdapterListener {

    fun onCommentContentSeeMoreClicked(position: Int, comment: LMFeedCommentViewData) {
        //triggered when the user clicks on "See More" on comment content
    }

    fun onCommentLiked(position: Int, comment: LMFeedCommentViewData) {
        //triggered when the user likes a comment
    }

    fun onCommentLikesCountClicked(position: Int, comment: LMFeedCommentViewData) {
        //triggered when the user clicks on the comment's likes count
    }

    fun onCommentReplyClicked(position: Int, comment: LMFeedCommentViewData) {
        //triggered when the user clicks on reply on a comment
    }

    fun onCommentReplyCountClicked(
        position: Int,
        comment: LMFeedCommentViewData,
        areRepliesVisible: Boolean
    ) {
        //triggered when the user clicks on reply count on a comment
    }

    fun onCommentMenuIconClicked(
        position: Int,
        anchorView: View,
        comment: LMFeedCommentViewData
    ) {
        //triggered when the menu item of any comment is clicked
    }

    fun onCommentContentLinkClicked(url: String) {
        //triggered when link in the reply content is clicked
    }

    fun onCommenterHeaderClicked(position: Int, commentViewData: LMFeedCommentViewData) {
        //triggered when the header of the commenter is clicked
    }
}