package com.likeminds.feed.android.core.utils.analytics

import android.util.Log
import com.likeminds.feed.android.core.LMFeedCoreApplication
import com.likeminds.feed.android.core.LMFeedCoreApplication.Companion.LOG_TAG
import com.likeminds.feed.android.core.post.detail.model.LMFeedCommentViewData
import com.likeminds.feed.android.core.socialfeed.model.LMFeedPostViewData
import com.likeminds.feed.android.core.utils.LMFeedViewUtils

object LMFeedAnalytics {

    /*
    * Event names variables
    * */
    object LMFeedEvents {
        const val POST_CREATION_STARTED = "post_creation_started"
        const val CLICKED_ON_ATTACHMENT = "clicked_on_attachment"
        const val ADD_MORE_ATTACHMENT = "add_more_attachment_clicked"
        const val USER_TAGGED_IN_POST = "user_tagged_in_a_post"
        const val LINK_ATTACHED_IN_POST = "link_attached_in_the_post"
        const val IMAGE_ATTACHED_TO_POST = "image_attached_to_post"
        const val VIDEO_ATTACHED_TO_POST = "video_attached_to_post"
        const val DOCUMENT_ATTACHED_TO_POST = "document_attached_in_post"
        const val POST_CREATION_COMPLETED = "post_creation_completed"
        const val POST_PINNED = "post_pinned"
        const val POST_UNPINNED = "post_unpinned"
        const val POST_REPORTED = "post_reported"
        const val POST_DELETED = "post_deleted"
        const val FEED_OPENED = "feed_opened"
        const val LIKE_LIST_OPEN = "like_list_open"
        const val COMMENT_LIST_OPEN = "comment_list_open"
        const val COMMENT_DELETED = "comment_deleted"
        const val COMMENT_REPORTED = "comment_reported"
        const val COMMENT_POSTED = "comment_posted"
        const val REPLY_POSTED = "reply_posted"
        const val REPLY_DELETED = "reply_deleted"
        const val REPLY_REPORTED = "reply_reported"
        const val POST_EDITED = "post_edited"
        const val POST_SHARED = "post_shared"
        const val POST_LIKED = "post_liked"
        const val POST_UNLIKED = "post_unliked"
        const val POST_SAVED = "post_saved"
        const val POST_UNSAVED = "post_unsaved"
        const val COMMENT_LIKED = "comment_liked"
        const val COMMENT_UNLIKED = "comment_unliked"
        const val COMMENT_EDITED = "comment_edited"

        const val NOTIFICATION_RECEIVED = "notification_received"
        const val NOTIFICATION_CLICKED = "notification_clicked"

        const val NOTIFICATION_PAGE_OPENED = "notification_page_opened"
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
        const val SOCIAL_FEED = "social_feed"
        const val POST_DETAIL = "post_detail"
    }

    object LMFeedScreenNames {
        const val UNIVERSAL_FEED = "universalFeed"
        const val FEEDROOM = "feedroom"
        const val POST_DETAIL = "postDetailScreen"
        const val USER_FEED = "userFeed"
        const val ACTIVITY_FEED = "activityScreen"
        const val CREATE_POST = "createPostScreen"
        const val EDIT_POST = "editPostScreen"
        const val TOPIC_SELECTION = "topicSelectScreen"
        const val LIKES_SCREEN = "likesScreen"
        const val MEDIA_PREVIEW = "mediaPreviewScreen"
        const val REPORT_SCREEN = "reportScreen"
        const val SEARCH_SCREEN = "searchScreen"
        const val SAVED_POST = "savedPostScreen"
        const val USER_CREATED_COMMENTS = "userCreatedCommentScreen"
        const val USER_PROFILE = "userProfileScreen"
        const val TOPIC_DETAIL = "topicDetailScreen"
        const val OTHER = "other"
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
            LMFeedEvents.FEED_OPENED,
            mapOf(
                "feed_type" to "social_feed"
            )
        )
    }

    /**
     * Triggers when the user clicks on New Post button
     **/
    fun sendPostCreationStartedEvent(screenName: String) {
        track(
            LMFeedEvents.POST_CREATION_STARTED, mapOf(
                "screen_name" to screenName
            )
        )
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
            LMFeedEvents.POST_LIKED
        } else {
            LMFeedEvents.POST_UNLIKED
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
            LMFeedEvents.POST_SAVED
        } else {
            LMFeedEvents.POST_UNSAVED
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
            LMFeedEvents.POST_PINNED
        } else {
            LMFeedEvents.POST_UNPINNED
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
        track(LMFeedEvents.COMMENT_LIST_OPEN)
    }

    /**
     * Triggers when the current user shares a post
     */
    fun sendPostShared(post: LMFeedPostViewData) {
        val postType = LMFeedViewUtils.getPostTypeFromViewType(post.viewType)

        val postCreatorUUID = post.headerViewData.user.sdkClientInfoViewData.uuid
        track(
            LMFeedEvents.POST_SHARED,
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
            LMFeedEvents.POST_EDITED,
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
    fun sendLinkAttachedEvent(link: String, screenName: String) {
        track(
            LMFeedEvents.LINK_ATTACHED_IN_POST,
            mapOf(
                "link" to link,
                "screen_name" to screenName
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
            LMFeedEvents.POST_DELETED,
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
                LMFeedEvents.COMMENT_DELETED,
                mapOf(
                    Keys.POST_ID to postId,
                    Keys.COMMENT_ID to commentId
                )
            )
        } else {
            //reply deleted event
            track(
                LMFeedEvents.REPLY_DELETED,
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
            LMFeedEvents.REPLY_POSTED,
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
            LMFeedEvents.COMMENT_POSTED,
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
            LMFeedEvents.COMMENT_LIKED
        } else {
            LMFeedEvents.COMMENT_UNLIKED
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
            LMFeedEvents.COMMENT_EDITED,
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
            LMFeedEvents.POST_REPORTED,
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
            LMFeedEvents.COMMENT_REPORTED,
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
            LMFeedEvents.REPLY_REPORTED,
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
            LMFeedEvents.LIKE_LIST_OPEN,
            map
        )
    }

    /**
     * Triggers when the user taps on the bell icon and lands on the notification page
     **/
    fun sendNotificationPageOpenedEvent() {
        track(LMFeedEvents.NOTIFICATION_PAGE_OPENED)
    }

    /**
     * Triggers event when the user tags someone
     * @param uuid user-unique-id
     * @param userCount count of tagged users
     * @param screenName screen name
     */
    fun sendUserTagEvent(uuid: String, userCount: Int, screenName: String) {
        track(
            LMFeedEvents.USER_TAGGED_IN_POST,
            mapOf(
                "tagged_user_uuid" to uuid,
                "tagged_user_count" to userCount.toString(),
                "screen_name" to screenName
            )
        )
    }

}