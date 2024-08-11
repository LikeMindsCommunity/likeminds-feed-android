package com.likeminds.feed.android.core.post.detail.adapter

import android.view.View
import com.likeminds.feed.android.core.post.detail.adapter.databinders.*
import com.likeminds.feed.android.core.post.detail.model.LMFeedCommentViewData
import com.likeminds.feed.android.core.socialfeed.adapter.LMFeedPostAdapterListener
import com.likeminds.feed.android.core.socialfeed.adapter.databinders.*
import com.likeminds.feed.android.core.utils.LMFeedValueUtils.getItemInList
import com.likeminds.feed.android.core.utils.base.*

class LMFeedPostDetailAdapter(
    private val postAdapterListener: LMFeedPostAdapterListener,
    private val postDetailAdapterListener: LMFeedPostDetailAdapterListener,
    private val replyAdapterListener: LMFeedReplyAdapterListener
) : LMFeedBaseRecyclerAdapter<LMFeedBaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): MutableList<LMFeedViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<LMFeedViewDataBinder<*, *>>(10)

        val itemCommentsCountViewDataBinder = LMFeedItemCommentsCountViewDataBinder()
        viewDataBinders.add(itemCommentsCountViewDataBinder)

        val itemCommentViewDataBinder =
            LMFeedItemCommentViewDataBinder(postDetailAdapterListener, replyAdapterListener)
        viewDataBinders.add(itemCommentViewDataBinder)

        val itemPostTextOnlyBinder =
            LMFeedItemPostTextOnlyViewDataBinder(postAdapterListener)
        viewDataBinders.add(itemPostTextOnlyBinder)

        val itemPostSingleImageViewDataBinder =
            LMFeedItemPostSingleImageViewDataBinder(postAdapterListener)
        viewDataBinders.add(itemPostSingleImageViewDataBinder)

        val itemPostSingleVideoViewDataBinder =
            LMFeedItemPostSingleVideoViewDataBinder(postAdapterListener)
        viewDataBinders.add(itemPostSingleVideoViewDataBinder)

        val itemPostLinkViewDataBinder =
            LMFeedItemPostLinkViewDataBinder(postAdapterListener)
        viewDataBinders.add(itemPostLinkViewDataBinder)

        val lmFeedItemPostDocumentsViewDataBinder =
            LMFeedItemPostDocumentsViewDataBinder(postAdapterListener)
        viewDataBinders.add(lmFeedItemPostDocumentsViewDataBinder)

        val itemPostMultipleMediaViewDataBinder =
            LMFeedItemPostMultipleMediaViewDataBinder(postAdapterListener)
        viewDataBinders.add(itemPostMultipleMediaViewDataBinder)

        val itemPostPollViewDataBinder =
            LMFeedItemPostPollViewDataBinder(postAdapterListener)
        viewDataBinders.add(itemPostPollViewDataBinder)

        val itemPostCustomWidgetViewDataBinder =
            LMFeedItemPostCustomWidgetViewDataBinder(postAdapterListener)
        viewDataBinders.add(itemPostCustomWidgetViewDataBinder)

        val itemNoCommentsFoundBinder = LMFeedItemNoCommentsFoundViewDataBinder()
        viewDataBinders.add(itemNoCommentsFoundBinder)

        return viewDataBinders
    }

    //replace any view data binder provide by the customer
    fun replaceViewDataBinder(viewType: Int, viewDataBinder: LMFeedViewDataBinder<*, *>) {
        supportedViewBinderResolverMap.put(viewType, viewDataBinder)
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

    fun onCommentTaggedMemberClicked(position: Int, uuid: String) {
        // triggered when user click on tag in a comment
    }
}