package com.likeminds.feed.android.core.utils.analytics

import android.util.Log
import com.likeminds.feed.android.core.LMFeedCoreApplication
import com.likeminds.feed.android.core.LMFeedCoreApplication.Companion.LOG_TAG
import com.likeminds.feed.android.core.post.detail.model.LMFeedCommentViewData
import com.likeminds.feed.android.core.universalfeed.model.LMFeedPostViewData
import com.likeminds.feed.android.core.utils.LMFeedViewUtils

object LMFeedAnalytics {

    /*
    * Event names variables
    * */
    object Events {
        const val POST_CREATION_STARTED = "Post creation started"
        const val CLICKED_ON_ATTACHMENT = "Clicked on Attachment"
        const val USER_TAGGED_IN_POST = "User tagged in a post"
        const val LINK_ATTACHED_IN_POST = "link attached in the post"
        const val IMAGE_ATTACHED_TO_POST = "Image attached to post"
        const val VIDEO_ATTACHED_TO_POST = "Video attached to post"
        const val DOCUMENT_ATTACHED_TO_POST = "Document attached in post"
        const val POST_CREATION_COMPLETED = "Post creation completed"
        const val POST_PINNED = "Post pinned"
        const val POST_UNPINNED = "Post unpinned"
        const val POST_REPORTED = "Post reported"
        const val POST_DELETED = "Post deleted"
        const val FEED_OPENED = "Feed opened"
        const val LIKE_LIST_OPEN = "Like list open"
        const val COMMENT_LIST_OPEN = "Comment list open"
        const val COMMENT_DELETED = "Comment deleted"
        const val COMMENT_REPORTED = "Comment reported"
        const val COMMENT_POSTED = "Comment posted"
        const val REPLY_POSTED = "Reply posted"
        const val REPLY_DELETED = "Reply deleted"
        const val REPLY_REPORTED = "Reply reported"
        const val POST_EDITED = "Post edited"
        const val POST_SHARED = "Post shared"
        const val POST_LIKED = "Post Liked"
        const val POST_UNLIKED = "Post Unliked"
        const val POST_SAVED = "Post Saved"
        const val POST_UNSAVED = "Post Unsaved"
        const val COMMENT_LIKED = "Comment Liked"
        const val COMMENT_UNLIKED = "Comment Unliked"
        const val COMMENT_EDITED = "Comment edited"

        const val NOTIFICATION_RECEIVED = "Notification Received"
        const val NOTIFICATION_CLICKED = "Notification Clicked"

        const val NOTIFICATION_PAGE_OPENED = "Notification page opened"
    }

    /*
    * Event keys variables
    * */
    object Keys {
        const val POST_ID = "post_id"
        const val UUID = "uuid"
        const val COMMENT_ID = "comment_id"
        const val COMMENT_REPLY_ID = "comment_reply_id"
        const val POST_TYPE_TEXT = "text"
        const val POST_TYPE_IMAGE = "image"
        const val POST_TYPE_VIDEO = "video"
        const val POST_TYPE_IMAGE_VIDEO = "image,video"
        const val POST_TYPE_DOCUMENT = "document"
        const val POST_TYPE_LINK = "link"
    }

    /**
     * Source keys variables
     **/
    object Source {
        const val DEEP_LINK = "deep_link"
        const val NOTIFICATION = "notification"
        const val UNIVERSAL_FEED = "universal_feed"
        const val POST_DETAIL = "post_detail"
    }

    /**
     * called to trigger events
     * @param eventName - name of the event to trigger
     * @param eventProperties - {key: value} pair for properties related to event
     * */
    fun track(eventName: String, eventProperties: Map<String, String?> = mapOf()) {
        Log.d(
            LOG_TAG, """
            eventName: $eventName
            eventProperties: $eventProperties
        """.trimIndent()
        )

        val coreCallback = LMFeedCoreApplication.getLMFeedCoreCallback()
        coreCallback?.trackEvent(eventName, eventProperties)
    }

    /*
    * Analytics events
    */

    /**
     * Triggers when the user opens feed fragment
     **/
    fun sendFeedOpenedEvent() {
        track(
            Events.FEED_OPENED,
            mapOf(
                "feed_type" to "universal_feed"
            )
        )
    }

    /**
     * Triggers when the user clicks on New Post button
     **/
    fun sendPostCreationStartedEvent() {
        track(Events.POST_CREATION_STARTED)
    }

    /**
     * Triggers when the current user likes/unlikes a post
     */
    fun sendPostLikedEvent(
        uuid: String,
        postId: String,
        postLiked: Boolean
    ) {
        val event = if (postLiked) {
            Events.POST_LIKED
        } else {
            Events.POST_UNLIKED
        }

        track(
            event,
            mapOf(
                Keys.UUID to uuid,
                Keys.POST_ID to postId
            )
        )
    }

    /**
     * Triggers when the current user saves/unsaves a post
     */
    fun sendPostSavedEvent(
        uuid: String,
        postId: String,
        postSaved: Boolean
    ) {
        val event = if (postSaved) {
            Events.POST_SAVED
        } else {
            Events.POST_UNSAVED
        }

        track(
            event,
            mapOf(
                Keys.UUID to uuid,
                Keys.POST_ID to postId
            )
        )
    }

    /**
     * Triggers when the current user pins/unpins a post
     */
    fun sendPostPinnedEvent(post: LMFeedPostViewData) {
        val headerViewData = post.headerViewData
        val event = if (headerViewData.isPinned) {
            Events.POST_PINNED
        } else {
            Events.POST_UNPINNED
        }

        track(
            event,
            mapOf(
                Keys.UUID to headerViewData.user.sdkClientInfoViewData.uuid,
                Keys.POST_ID to post.id,
                "post_type" to LMFeedViewUtils.getPostTypeFromViewType(post.viewType),
            )
        )
    }

    /**
     * Triggers when the user opens post detail screen
     **/
    fun sendCommentListOpenEvent() {
        track(Events.COMMENT_LIST_OPEN)
    }

    /**
     * Triggers when the current user shares a post
     */
    fun sendPostShared(post: LMFeedPostViewData) {
        val postType = LMFeedViewUtils.getPostTypeFromViewType(post.viewType)

        val postCreatorUUID = post.headerViewData.user.sdkClientInfoViewData.uuid
        track(
            Events.POST_SHARED,
            mapOf(
                "created_by_uuid" to postCreatorUUID,
                Keys.POST_ID to post.id,
                "post_type" to postType,
            )
        )
    }

    /**
     * Triggers when the user edits a post
     **/
    fun sendPostEditedEvent(post: LMFeedPostViewData) {
        val postType = LMFeedViewUtils.getPostTypeFromViewType(post.viewType)
        val postCreatorUUID = post.headerViewData.user.sdkClientInfoViewData.uuid
        track(
            Events.POST_EDITED,
            mapOf(
                "created_by_uuid" to postCreatorUUID,
                Keys.POST_ID to post.id,
                "post_type" to postType,
            )
        )
    }

    /**
     * Triggers when the user attaches link
     * @param link - url of the link
     **/
    fun sendLinkAttachedEvent(link: String) {
        track(
            Events.LINK_ATTACHED_IN_POST,
            mapOf(
                "link" to link
            )
        )
    }

    /**
     * Triggers when a post is deleted
     **/
    fun sendPostDeletedEvent(post: LMFeedPostViewData, reason: String? = null) {
        /**
         * if [reason] is [null], then it is a self delete
         */
        val userStateString = if (reason == null) {
            "Member"
        } else {
            "Community Manager"
        }

        //uuid of the post creator
        val postCreatorUUID = post.headerViewData.user.sdkClientInfoViewData.uuid
        val map = mapOf(
            "user_state" to userStateString,
            Keys.UUID to postCreatorUUID,
            Keys.POST_ID to post.id,
            "post_type" to LMFeedViewUtils.getPostTypeFromViewType(post.viewType),
        )
        track(
            Events.POST_DELETED,
            map
        )
    }

    /**
     * Triggers when a comment/reply is deleted
     **/
    fun sendCommentReplyDeletedEvent(
        postId: String,
        commentId: String,
        parentCommentId: String?
    ) {
        if (parentCommentId == null) {
            //comment deleted event
            track(
                Events.COMMENT_DELETED,
                mapOf(
                    Keys.POST_ID to postId,
                    Keys.COMMENT_ID to commentId
                )
            )
        } else {
            //reply deleted event
            track(
                Events.REPLY_DELETED,
                mapOf(
                    Keys.POST_ID to postId,
                    Keys.COMMENT_ID to parentCommentId,
                    Keys.COMMENT_REPLY_ID to commentId,
                )
            )
        }
    }

    /**
     * Triggers when the reply is posted on a comment
     **/
    fun sendReplyPostedEvent(
        parentCommentCreatorUUID: String,
        postId: String,
        parentCommentId: String,
        commentId: String,
    ) {
        track(
            Events.REPLY_POSTED,
            mapOf(
                Keys.UUID to parentCommentCreatorUUID,
                Keys.POST_ID to postId,
                Keys.COMMENT_ID to parentCommentId,
                Keys.COMMENT_REPLY_ID to commentId
            )
        )
    }

    /**
     * Triggers when a comment is posted on a post
     **/
    fun sendCommentPostedEvent(postId: String, commentId: String) {
        track(
            Events.COMMENT_POSTED,
            mapOf(
                Keys.POST_ID to postId,
                Keys.COMMENT_ID to commentId
            )
        )
    }

    /**
     * Triggers when the current user likes/unlikes a comment
     */
    fun sendCommentLikedEvent(
        postId: String,
        commentId: String,
        commentLiked: Boolean,
        loggedInUUID: String,
    ) {
        val event = if (commentLiked) {
            Events.COMMENT_LIKED
        } else {
            Events.COMMENT_UNLIKED
        }

        track(
            event,
            mapOf(
                Keys.UUID to loggedInUUID,
                Keys.POST_ID to postId,
                Keys.COMMENT_ID to commentId,
            )
        )
    }

    /**
     * Triggers when the user edits a comment
     **/
    fun sendCommentEditedEvent(comment: LMFeedCommentViewData) {
        track(
            Events.COMMENT_EDITED,
            mapOf(
                "created_by_uuid" to comment.user.sdkClientInfoViewData.uuid,
                Keys.COMMENT_ID to comment.id,
                "level" to comment.level.toString()
            )
        )
    }

    /**
     * Triggers when the user reports a post
     **/
    fun sendPostReportedEvent(
        postId: String,
        uuid: String,
        postType: String,
        reason: String
    ) {
        track(
            Events.POST_REPORTED,
            mapOf(
                "created_by_uuid" to uuid,
                Keys.POST_ID to postId,
                "report_reason" to reason,
                "post_type" to postType,
            )
        )
    }

    /**
     * Triggers when the user reports a comment
     **/
    fun sendCommentReportedEvent(
        postId: String,
        uuid: String,
        commentId: String,
        reason: String
    ) {
        track(
            Events.COMMENT_REPORTED,
            mapOf(
                Keys.POST_ID to postId,
                Keys.UUID to uuid,
                Keys.COMMENT_ID to commentId,
                "reason" to reason,
            )
        )
    }

    /**
     * Triggers when the user reports a reply
     **/
    fun sendReplyReportedEvent(
        postId: String,
        uuid: String,
        parentCommentId: String?,
        replyId: String,
        reason: String
    ) {
        val updatedParentId = parentCommentId ?: ""
        track(
            Events.REPLY_REPORTED,
            mapOf(
                Keys.POST_ID to postId,
                Keys.COMMENT_ID to updatedParentId,
                Keys.COMMENT_REPLY_ID to replyId,
                Keys.UUID to uuid,
                "reason" to reason,
            )
        )
    }

    /**
     * Triggers when the user opens likes screen for post/comment
     **/
    fun sendLikeListOpenEvent(
        postId: String,
        commentId: String?
    ) {
        val map = hashMapOf<String, String>()
        map[Keys.POST_ID] = postId
        if (commentId != null) {
            map[Keys.COMMENT_ID] = commentId
        }
        track(
            Events.LIKE_LIST_OPEN,
            map
        )
    }

    /**
     * Triggers when the user taps on the bell icon and lands on the notification page
     **/
    fun sendNotificationPageOpenedEvent() {
        track(Events.NOTIFICATION_PAGE_OPENED)
    }
}