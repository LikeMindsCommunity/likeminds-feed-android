package com.likeminds.feed.android.core.universalfeed.adapter

import android.view.View
import com.likeminds.feed.android.core.post.model.LMFeedAttachmentViewData
import com.likeminds.feed.android.core.universalfeed.adapter.databinders.*
import com.likeminds.feed.android.core.universalfeed.model.LMFeedPostViewData
import com.likeminds.feed.android.core.utils.LMFeedValueUtils.getItemInList
import com.likeminds.feed.android.core.utils.base.*

class LMFeedUniversalFeedAdapter(
    private val universalFeedAdapterListener: LMFeedUniversalFeedAdapterListener
) : LMFeedBaseRecyclerAdapter<LMFeedBaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): MutableList<LMFeedViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<LMFeedViewDataBinder<*, *>>(6)

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

        return viewDataBinders
    }

    operator fun get(position: Int): LMFeedBaseViewType? {
        return items().getItemInList(position)
    }

    fun replaceTextOnlyBinder(viewDataBinder: LMFeedViewDataBinder<*, *>) {
        val view = viewDataBinder.viewType

        val indexOfViewData = this.supportedViewDataBinder.indexOfFirst {
            it.viewType == view
        }

        this.supportedViewDataBinder[indexOfViewData] = viewDataBinder
    }
}

interface LMFeedUniversalFeedAdapterListener {

    fun onPostContentClicked(position: Int, postViewData: LMFeedPostViewData) {
        //triggered when the user clicks on post content
    }

    fun onPostLikeClicked(position: Int, postViewData: LMFeedPostViewData) {
        //triggered when the user clicks on like icon
    }

    fun onPostLikesCountClicked(position: Int, postViewData: LMFeedPostViewData) {
        //triggered when the user clicks on the post's likes count
    }

    fun onPostCommentsCountClicked(position: Int, postViewData: LMFeedPostViewData) {
        //triggered when the user clicks on the comments count
    }

    fun onPostSaveClicked(position: Int, postViewData: LMFeedPostViewData) {
        //triggered when the user clicks on save post icon
    }

    fun onPostShareClicked(position: Int, postViewData: LMFeedPostViewData) {
        //triggered when the user clicks on share icon
    }

    fun updateFromLikedSaved(position: Int, postViewData: LMFeedPostViewData) {
        //triggered to update the data with re-inflation of the item
    }

    fun onPostContentSeeMoreClicked(position: Int, postViewData: LMFeedPostViewData) {
        //triggered when the user clicks on "See More" on post content
    }

    fun onPostContentLinkClicked(url: String) {
        //triggered when link in the post content is clicked
    }

    fun onPostMenuIconClicked(
        position: Int,
        anchorView: View,
        postViewData: LMFeedPostViewData
    ) {
        //triggered when the menu icon of the post is clicked
    }

    fun onPostImageMediaClicked(position: Int, postViewData: LMFeedPostViewData) {
        //triggered when the image media of the post is clicked
    }

    fun onPostVideoMediaClicked(position: Int, postViewData: LMFeedPostViewData) {
        //triggered when the video media of the post is clicked
    }

    fun onPostLinkMediaClicked(position: Int, postViewData: LMFeedPostViewData) {
        //triggered when the link media of the post is clicked
    }

    fun onPostDocumentMediaClicked(
        position: Int,
        parentPosition: Int,
        attachmentViewData: LMFeedAttachmentViewData
    ) {
        //triggered when the document media in the post is clicked
    }

    fun onPostMultipleMediaImageClicked(
        position: Int,
        parentPosition: Int,
        attachmentViewData: LMFeedAttachmentViewData
    ) {
        //triggered when the image media of multiple media is clicked
    }

    fun onPostMultipleMediaVideoClicked(
        position: Int,
        parentPosition: Int,
        attachmentViewData: LMFeedAttachmentViewData
    ) {
        //triggered when the video media of multiple media is clicked
    }

    fun onPostMultipleMediaPageChangeCallback(position: Int, parentPosition: Int) {
        //triggered when the page of the view pager is changed
    }

    fun onPostMultipleDocumentsExpanded(position: Int, postViewData: LMFeedPostViewData) {
        //triggered when a user clicks on "See More" of document type post
    }

    fun onPostAuthorHeaderClicked(position: Int, postViewData: LMFeedPostViewData) {
        //triggered when a user clicks post author header
    }
}