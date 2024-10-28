package com.likeminds.feed.android.core.socialfeed.adapter

import android.view.View
import com.likeminds.feed.android.core.poll.result.model.LMFeedPollOptionViewData
import com.likeminds.feed.android.core.post.model.LMFeedAttachmentViewData
import com.likeminds.feed.android.core.socialfeed.model.LMFeedPostViewData

interface LMFeedPostAdapterListener {

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
        position: Int, anchorView: View, postViewData: LMFeedPostViewData
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
        position: Int, parentPosition: Int, attachmentViewData: LMFeedAttachmentViewData
    ) {
        //triggered when the document media in the post is clicked
    }

    fun onPostMultipleMediaImageClicked(
        position: Int, parentPosition: Int, attachmentViewData: LMFeedAttachmentViewData
    ) {
        //triggered when the image media of multiple media is clicked
    }

    fun onPostMultipleMediaVideoClicked(
        position: Int, parentPosition: Int, attachmentViewData: LMFeedAttachmentViewData
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

    fun onMediaRemovedClicked(position: Int, mediaType: String) {
        //triggered when user removes a media while creating or editing a post
    }

    fun onPostTaggedMemberClicked(position: Int, uuid: String) {
        //triggered when the member tag in the post text content is clicked
    }

    fun onPostPollTitleClicked(position: Int, postViewData: LMFeedPostViewData) {
        //triggered when the user clicks on poll title
    }

    fun onPostEditPollClicked(position: Int, postViewData: LMFeedPostViewData) {
        //triggered when the user clicks on edit poll icon
    }

    fun onPostClearPollClicked(position: Int, postViewData: LMFeedPostViewData) {
        //triggered when the user clicks on clear poll icon
    }

    fun onPostAddPollOptionClicked(position: Int, postViewData: LMFeedPostViewData) {
        //triggered when the user clicks on add poll option button
    }

    fun onPostMemberVotedCountClicked(position: Int, postViewData: LMFeedPostViewData) {
        //triggered when the user clicks on poll's member voted count is clicked
    }

    fun onPostSubmitPollVoteClicked(position: Int, postViewData: LMFeedPostViewData) {
        //triggered when the user clicks on submit poll vote button
    }

    fun onPostEditPollVoteClicked(position: Int, postViewData: LMFeedPostViewData) {
        //triggered when the user clicks on edit poll vote
    }

    fun onPollOptionClicked(
        pollPosition: Int,
        pollOptionPosition: Int,
        pollOptionViewData: LMFeedPollOptionViewData
    ) {
        //triggered when a poll option is clicked
    }

    fun onPollOptionVoteCountClicked(
        pollPosition: Int,
        pollOptionPosition: Int,
        pollOptionViewData: LMFeedPollOptionViewData
    ) {
        //triggered when a poll option vote count is clicked
    }

    fun onPostActionMenuClicked(position: Int, postViewData: LMFeedPostViewData) {
        //triggered when the post action menu is clicked
    }

    fun onPostVideoFeedCaughtUpClicked() {
        //triggered when the user clicks on the cta on video feed caught up screen
    }

    fun onPostHeadingClicked(position: Int, postViewData: LMFeedPostViewData) {
        //triggered when the user clicks on the heading of the post
    }

    fun onPostHeadingSeeMoreClicked(position: Int, postViewData: LMFeedPostViewData) {
        //triggered when the user clicks on "See More" on post content
    }

    fun onPostTopResponseClicked(position: Int, postViewData: LMFeedPostViewData) {
        //triggered when the user clicks on the top response of the post
    }

    fun onPostTopResponseSeeMoreClicked(position: Int, postViewData: LMFeedPostViewData) {
        //triggered when the user clicks on the see more button in top response of the post
    }

    fun onPostTopResponseTaggedMemberClicked(position: Int, uuid: String) {
        //triggered when the user clicks on tagged member in the top response of the post
    }

    fun onPostTopResponseContentClicked(position: Int, postViewData: LMFeedPostViewData) {
        //triggered when the user clicks on the content of the post top response
    }

    fun onPostTopResponseAuthorFrameCLicked(position: Int, postViewData: LMFeedPostViewData) {
        //triggered when the user clicks on the author frame of the post top response
    }

    fun onPostAnswerPromptClicked(position: Int, postViewData: LMFeedPostViewData) {
        //triggered when the user clicks on the answer prompt of the post
    }
}