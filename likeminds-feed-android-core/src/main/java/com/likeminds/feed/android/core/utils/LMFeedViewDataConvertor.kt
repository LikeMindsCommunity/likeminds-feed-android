package com.likeminds.feed.android.core.utils

import android.util.Base64
import com.likeminds.customgallery.media.model.SingleUriData
import com.likeminds.feed.android.core.activityfeed.model.LMFeedActivityEntityViewData
import com.likeminds.feed.android.core.activityfeed.model.LMFeedActivityViewData
import com.likeminds.feed.android.core.delete.model.LMFeedReasonChooseViewData
import com.likeminds.feed.android.core.likes.model.LMFeedLikeViewData
import com.likeminds.feed.android.core.postmenu.model.LMFeedPostMenuItemViewData
import com.likeminds.feed.android.core.poll.result.model.*
import com.likeminds.feed.android.core.post.create.model.LMFeedFileUploadViewData
import com.likeminds.feed.android.core.post.detail.model.LMFeedCommentViewData
import com.likeminds.feed.android.core.post.detail.model.LMFeedCommentsCountViewData
import com.likeminds.feed.android.core.post.model.*
import com.likeminds.feed.android.core.report.model.LMFeedReportTagViewData
import com.likeminds.feed.android.core.topics.model.LMFeedTopicViewData
import com.likeminds.feed.android.core.socialfeed.model.*
import com.likeminds.feed.android.core.utils.LMFeedValueUtils.findBooleanOrDefault
import com.likeminds.feed.android.core.utils.LMFeedValueUtils.findIntOrDefault
import com.likeminds.feed.android.core.utils.LMFeedValueUtils.findLongOrDefault
import com.likeminds.feed.android.core.utils.LMFeedValueUtils.findStringOrDefault
import com.likeminds.feed.android.core.utils.base.model.*
import com.likeminds.feed.android.core.utils.mediauploader.utils.LMFeedAWSKeys
import com.likeminds.feed.android.core.utils.user.LMFeedUserViewData
import com.likeminds.feed.android.core.widget.model.LMFeedWidgetViewData
import com.likeminds.likemindsfeed.comment.model.Comment
import com.likeminds.likemindsfeed.moderation.model.ReportTag
import com.likeminds.likemindsfeed.notificationfeed.model.Activity
import com.likeminds.likemindsfeed.notificationfeed.model.ActivityEntityData
import com.likeminds.likemindsfeed.poll.model.PollVote
import com.likeminds.likemindsfeed.poll.util.PollUtil.getPollMultiSelectState
import com.likeminds.likemindsfeed.poll.util.PollUtil.getPollType
import com.likeminds.likemindsfeed.post.model.*
import com.likeminds.likemindsfeed.post.util.AttachmentUtil.getAttachmentType
import com.likeminds.likemindsfeed.post.util.AttachmentUtil.getAttachmentValue
import com.likeminds.likemindsfeed.sdk.model.SDKClientInfo
import com.likeminds.likemindsfeed.sdk.model.User
import com.likeminds.likemindsfeed.topic.model.Topic
import com.likeminds.likemindsfeed.widgets.model.Widget
import org.json.JSONObject

object LMFeedViewDataConvertor {

    private const val POLL_ALLOW_ADD_OPTION_KEY = "allow_add_option"
    private const val POLL_TYPE_KEY = "poll_type"
    private const val POLL_MULTIPLE_SELECT_STATE_KEY = "multiple_select_state"
    private const val POLL_MULTIPLE_SELECT_NUMBER_KEY = "multiple_select_number"
    private const val POLL_TITLE_KEY = "title"
    private const val POLL_EXPIRY_TIME_KEY = "expiry_time"
    private const val POLL_IS_ANONYMOUS_KEY = "is_anonymous"

    /**--------------------------------
     * Media Model -> View Data Model
    --------------------------------*/

    // Converts the list of SingleUriData (contains the data of media) to the list of [LMFeedAttachmentViewData]
    fun convertSingleDataUri(singleUris: List<SingleUriData>): List<LMFeedAttachmentViewData> {
        return singleUris.map {
            convertSingleDataUri(it)
        }
    }

    // Converts the SingleUriData (contains the data of media) to [LMFeedAttachmentViewData]
    private fun convertSingleDataUri(singleUriData: SingleUriData): LMFeedAttachmentViewData {
        val attachmentType: Int?
        val viewType = when (singleUriData.fileType) {
            com.likeminds.customgallery.media.model.IMAGE -> {
                attachmentType = IMAGE
                ITEM_MULTIPLE_MEDIA_IMAGE
            }

            com.likeminds.customgallery.media.model.VIDEO -> {
                attachmentType = VIDEO
                ITEM_MULTIPLE_MEDIA_VIDEO
            }

            else -> {
                attachmentType = DOCUMENT
                ITEM_POST_DOCUMENTS_ITEM
            }
        }
        return LMFeedAttachmentViewData.Builder()
            .dynamicViewType(viewType)
            .attachmentType(attachmentType)
            .attachmentMeta(
                LMFeedAttachmentMetaViewData.Builder()
                    .name(singleUriData.mediaName)
                    .duration(singleUriData.duration)
                    .pageCount(singleUriData.pdfPageCount)
                    .width(singleUriData.width)
                    .height(singleUriData.height)
                    .size(singleUriData.size)
                    .uri(singleUriData.uri)
                    .thumbnail(singleUriData.thumbnailUri.toString())
                    .build()
            )
            .build()
    }

    /**--------------------------------
     * Network Model -> View Data Model
    --------------------------------*/

    /**
     * converts [Post] to [LMFeedPostViewData] while creating a post
     * @param post: [Post] instance
     * @param topics: list of [Topic]
     *
     * @return [LMFeedPostViewData]
     */
    fun convertPost(
        post: Post,
        topics: List<Topic>
    ): LMFeedPostViewData {

        //post content view data
        val postContentViewData = LMFeedPostContentViewData.Builder()
            .text(post.text)
            .build()

        //post media view data
        val postMediaViewData = LMFeedMediaViewData.Builder()
            .attachments(
                convertAttachments(
                    post.id,
                    post.attachments
                )
            )
            .workerUUID(post.workerUUID ?: "")
            .temporaryId(post.tempId?.toLong())
            .build()

        return LMFeedPostViewData.Builder()
            .contentViewData(postContentViewData)
            .mediaViewData(postMediaViewData)
            .topicsViewData(convertTopics(topics))
            .isPosted(post.isPosted)
            .build()
    }

    // converts list of [Attachment] to list of [LMFeedAttachmentViewData]
    private fun convertAttachments(
        postId: String,
        attachments: List<Attachment>?
    ): List<LMFeedAttachmentViewData> {
        if (attachments.isNullOrEmpty()) return emptyList()
        return attachments.map { attachment ->
            LMFeedAttachmentViewData.Builder()
                .postId(postId)
                .attachmentType(attachment.attachmentType.getAttachmentValue())
                .attachmentMeta(convertAttachments(attachment.attachmentMeta))
                .build()
        }
    }

    // converts [AttachmentMeta] to [LMFeedAttachmentMetaViewData]
    private fun convertAttachments(attachmentMeta: AttachmentMeta): LMFeedAttachmentMetaViewData {
        return LMFeedAttachmentMetaViewData.Builder()
            .name(attachmentMeta.name)
            .url(attachmentMeta.url)
            .format(attachmentMeta.format)
            .size(attachmentMeta.size)
            .duration(attachmentMeta.duration)
            .pageCount(attachmentMeta.pageCount)
            .thumbnail(attachmentMeta.thumbnailUrl)
            .height(attachmentMeta.height)
            .width(attachmentMeta.width)
            .widgetViewData(convertWidgetViewData(attachmentMeta.meta))
            .build()
    }

    // converts [Widget] to [LMFeedWidgetViewData]
    private fun convertWidgetViewData(meta: JSONObject?): LMFeedWidgetViewData? {
        if (meta == null) return null
        return LMFeedWidgetViewData.Builder()
            .metadata(meta)
            .build()
    }

    // converts response of the get feed post to list of LMFeedPostViewData
    fun convertGetFeedPosts(
        posts: List<Post>,
        usersMap: Map<String, User>,
        topicsMap: Map<String, Topic>,
        widgetsMap: Map<String, Widget>
    ): List<LMFeedPostViewData> {
        return posts.map { post ->
            convertPost(
                post,
                usersMap,
                topicsMap,
                widgetsMap
            )
        }
    }

    /**
     * converts [Post] which is network model to [LMFeedPostViewData] which is view data model
     * @param post: [Post] instance
     * @param usersMap: Map of [String, User]
     * @param topicsMap: Map of [String, Topic]
     */
    fun convertPost(
        post: Post,
        usersMap: Map<String, User>,
        topicsMap: Map<String, Topic>,
        widgetsMap: Map<String, Widget>
    ): LMFeedPostViewData {
        val postCreatorUUID = post.uuid
        val postCreator = usersMap[postCreatorUUID]
        val postId = post.id
        val replies = post.replies?.toMutableList()
        val topicsIds = post.topicIds ?: emptyList()

        //get user view data
        val userViewData = if (postCreator == null) {
            createDeletedUser()
        } else {
            convertUser(postCreator)
        }

        //get topics view data
        val topicsViewData = topicsIds.mapNotNull { topicId ->
            topicsMap[topicId]
        }.map { topic ->
            convertTopic(topic)
        }

        //post header view data
        val postHeaderViewData = LMFeedPostHeaderViewData.Builder()
            .isEdited(post.isEdited)
            .isPinned(post.isPinned)
            .user(userViewData)
            .userId(postCreatorUUID)
            .createdAt(post.createdAt)
            .updatedAt(post.updatedAt)
            .menuItems(convertOverflowMenuItems(post.menuItems))
            .build()

        //post content view data
        val postContentViewData = LMFeedPostContentViewData.Builder()
            .text(post.text)
            .build()

        //post media view data
        val postMediaViewData = LMFeedMediaViewData.Builder()
            .attachments(
                convertAttachments(
                    post.attachments,
                    postId,
                    usersMap,
                    widgetsMap
                )
            )
            .build()

        //post action view data
        val postActionViewData = LMFeedPostActionViewData.Builder()
            .likesCount(post.likesCount)
            .commentsCount(post.commentsCount)
            .isSaved(post.isSaved)
            .isLiked(post.isLiked)
            .replies(
                convertComments(
                    replies,
                    usersMap,
                    postId
                )
            )
            .build()

        //creating a final instance
        return LMFeedPostViewData.Builder()
            .id(postId)
            .headerViewData(postHeaderViewData)
            .contentViewData(postContentViewData)
            .mediaViewData(postMediaViewData)
            .actionViewData(postActionViewData)
            .topicsViewData(topicsViewData)
            .build()
    }

    /**
     * Converts [User] which is a network model to [LMFeedUserViewData] which is view data model
     * @param user: instance of [User] | nullable
     */
    fun convertUser(
        user: User?
    ): LMFeedUserViewData {
        if (user == null) return LMFeedUserViewData.Builder().build()
        return LMFeedUserViewData.Builder()
            .id(user.id)
            .name(user.name)
            .imageUrl(user.imageUrl)
            .userUniqueId(user.userUniqueId)
            .customTitle(user.customTitle)
            .isGuest(user.isGuest)
            .isDeleted(user.isDeleted)
            .uuid(user.uuid)
            .sdkClientInfoViewData(convertSDKClientInfo(user.sdkClientInfo))
            .build()
    }

    /**
     * Converts [SDKClientInfo] which is a network model to [LMFeedSDKClientInfoViewData] which is view data model
     */
    private fun convertSDKClientInfo(sdkClientInfo: SDKClientInfo): LMFeedSDKClientInfoViewData {
        return LMFeedSDKClientInfoViewData.Builder()
            .user(sdkClientInfo.user)
            .uuid(sdkClientInfo.uuid)
            .userUniqueId(sdkClientInfo.userUniqueId)
            .community(sdkClientInfo.community)
            .build()
    }

    //created a deleted user object
    private fun createDeletedUser(): LMFeedUserViewData {
        val tempUserId = (System.currentTimeMillis() / 1000).toInt()
        return LMFeedUserViewData.Builder()
            .id(tempUserId)
            .name("Deleted User")
            .imageUrl("")
            .userUniqueId("$tempUserId")
            .customTitle(null)
            .isGuest(false)
            .isDeleted(true)
            .build()
    }

    /**
     * convert list of [MenuItem] to [LMFeedPostMenuItemViewData]
     * @param menuItems: list of [MenuItem]
     * */
    private fun convertOverflowMenuItems(
        menuItems: List<MenuItem>
    ): List<LMFeedPostMenuItemViewData> {
        return menuItems.map { menuItem ->
            LMFeedPostMenuItemViewData.Builder()
                .id(menuItem.id)
                .title(menuItem.title)
                .build()
        }
    }

    /**
     * converts list of [Attachment] to list of [LMFeedAttachmentViewData]
     * @param attachments: list of [Attachment]
     * @param postId: id of the post
     */
    private fun convertAttachments(
        attachments: List<Attachment>?,
        postId: String,
        usersMap: Map<String, User>,
        widgetsMap: Map<String, Widget>
    ): List<LMFeedAttachmentViewData> {
        if (attachments == null) return emptyList()
        return attachments.map { attachment ->
            LMFeedAttachmentViewData.Builder()
                .attachmentType(attachment.attachmentType.getAttachmentValue())
                .attachmentMeta(
                    convertAttachmentMeta(
                        attachment.attachmentMeta,
                        usersMap,
                        widgetsMap
                    )
                )
                .postId(postId)
                .build()
        }
    }

    /**
     * converts object of [AttachmentMeta] to object of [LMFeedAttachmentMetaViewData]
     * @param attachmentMeta: instance of [AttachmentMeta]
     * @param usersMap: Map of [String, User]
     * @param widgetsMap: Map of [String, Widget]
     *
     * @return [LMFeedAttachmentMetaViewData]
     */
    private fun convertAttachmentMeta(
        attachmentMeta: AttachmentMeta?,
        usersMap: Map<String, User>,
        widgetsMap: Map<String, Widget>
    ): LMFeedAttachmentMetaViewData {
        if (attachmentMeta == null) {
            return LMFeedAttachmentMetaViewData.Builder().build()
        }

        return LMFeedAttachmentMetaViewData.Builder()
            .name(attachmentMeta.name)
            .url(attachmentMeta.url)
            .format(attachmentMeta.format)
            .size(attachmentMeta.size)
            .duration(attachmentMeta.duration)
            .pageCount(attachmentMeta.pageCount)
            .ogTags(convertLinkOGTags(attachmentMeta.ogTags))
            .thumbnail(attachmentMeta.thumbnailUrl)
            .height(attachmentMeta.height)
            .width(attachmentMeta.width)
            .widgetViewData(convertWidget(attachmentMeta.entityId, widgetsMap))
            .poll(
                convertPoll(
                    attachmentMeta.entityId ?: "",
                    usersMap,
                    widgetsMap
                )
            )
            .build()
    }

    /**
     * convert [LinkOGTags] to [LMFeedLinkOGTagsViewData]
     * @param linkOGTags: object of [LinkOGTags]
     **/
    fun convertLinkOGTags(linkOGTags: LinkOGTags?): LMFeedLinkOGTagsViewData? {
        if (linkOGTags == null) {
            return null
        }

        return LMFeedLinkOGTagsViewData.Builder()
            .url(linkOGTags.url)
            .description(linkOGTags.description)
            .title(linkOGTags.title)
            .image(linkOGTags.image)
            .build()
    }

    /**
     * extracts poll from the [widgetsMap] and converts it to [LMFeedPollViewData]
     * */
    private fun convertPoll(
        pollId: String,
        usersMap: Map<String, User>,
        widgetsMap: Map<String, Widget>
    ): LMFeedPollViewData? {
        if (widgetsMap.isEmpty()) {
            return null
        }

        val pollWidget = widgetsMap[pollId] ?: return null
        val pollLMMeta = pollWidget.lmMeta ?: return null
        val pollMetaData = pollWidget.metadata

        val toShowResults = pollLMMeta.toShowResults ?: false

        val allowAddOption = pollMetaData.findBooleanOrDefault(
            POLL_ALLOW_ADD_OPTION_KEY, false
        )

        val pollType = pollMetaData.findStringOrDefault(
            POLL_TYPE_KEY,
            ""
        ).getPollType()

        val multipleSelectState = pollMetaData.findStringOrDefault(
            POLL_MULTIPLE_SELECT_STATE_KEY,
            ""
        ).getPollMultiSelectState()

        val multipleSelectNumber = pollMetaData.findIntOrDefault(
            POLL_MULTIPLE_SELECT_NUMBER_KEY,
            0
        )

        var poll = LMFeedPollViewData.Builder()
            .id(pollId)
            .title(pollMetaData.findStringOrDefault(POLL_TITLE_KEY, ""))
            .pollAnswerText(pollLMMeta.pollAnswerText ?: "")
            .toShowResults(toShowResults)
            .expiryTime(pollMetaData.findLongOrDefault(POLL_EXPIRY_TIME_KEY, 0))
            .isAnonymous(pollMetaData.findBooleanOrDefault(POLL_IS_ANONYMOUS_KEY, false))
            .allowAddOption(allowAddOption)
            .multipleSelectState(multipleSelectState)
            .multipleSelectNumber(multipleSelectNumber)
            .pollType(pollType)
            .isPollSubmitted(checkIsPollSubmitted(pollLMMeta.options ?: emptyList()))
            .build()

        poll = poll.toBuilder()
            .options(
                convertPollOptions(
                    pollLMMeta.options ?: emptyList(),
                    poll,
                    usersMap
                )
            )
            .build()

        return poll
    }

    // checks whether the poll is submitted or not
    private fun checkIsPollSubmitted(pollOptions: List<PollOption>): Boolean {
        pollOptions.forEach {
            if (it.isSelected) {
                return true
            }
        }

        return false
    }

    /**
     * converts list of [PollOption] which is a network model to [LMFeedPollOptionViewData] which is view data model
     * */
    private fun convertPollOptions(
        options: List<PollOption>,
        poll: LMFeedPollViewData,
        usersMap: Map<String, User>
    ): List<LMFeedPollOptionViewData> {
        return options.map { option ->
            val addedByUser = usersMap[option.uuid]
            LMFeedPollOptionViewData.Builder()
                .id(option.id)
                .isSelected(option.isSelected)
                .percentage(option.percentage)
                .addedByUser(convertUser(addedByUser))
                .voteCount(option.voteCount)
                .text(option.text)
                .toShowResults(poll.toShowResults)
                .allowAddOption(poll.allowAddOption)
                .isInstantPoll(poll.isInstantPoll())
                .isMultiChoicePoll(poll.isMultiChoicePoll())
                .build()
        }
    }

    /**
     * convert [Topic] which is a network model to [LMFeedTopicViewData] which is view data model
     * */
    fun convertTopic(topic: Topic): LMFeedTopicViewData {
        return LMFeedTopicViewData.Builder()
            .id(topic.id)
            .name(topic.name)
            .isEnabled(topic.isEnabled)
            .isSelected(false)
            .build()
    }

    /**
     * convert list of [Comment] and usersMap [Map] of String to User
     * to list of [LMFeedCommentViewData]
     *
     * @param comments: list of [Comment]
     * @param usersMap: [Map] of String to User
     * @param postId: postId of post
     * */
    private fun convertComments(
        comments: MutableList<Comment>?,
        usersMap: Map<String, User>,
        postId: String,
        parentCommentId: String? = null
    ): MutableList<LMFeedCommentViewData> {
        if (comments == null) return mutableListOf()
        return comments.map { comment ->
            convertComment(
                comment,
                usersMap,
                postId,
                parentCommentId
            )
        }.toMutableList()
    }

    /**
     * converts [Comment] which is network model to [LMFeedCommentViewData]
     * @param postId: id of the post
     * @param usersMap: Map of [String, User]
     * @param parentCommentId: Id of the comment to which the user has replied
     */
    fun convertComment(
        comment: Comment,
        usersMap: Map<String, User>,
        postId: String,
        parentCommentId: String? = null
    ): LMFeedCommentViewData {
        val commentCreator = comment.uuid
        val user = usersMap[commentCreator]
        val replies = comment.replies?.toMutableList()
        val parentId = parentCommentId ?: comment.parentComment?.id

        val userViewData = if (user == null) {
            createDeletedUser()
        } else {
            convertUser(user)
        }

        return LMFeedCommentViewData.Builder()
            .id(comment.id)
            .postId(postId)
            .isLiked(comment.isLiked)
            .isEdited(comment.isEdited)
            .userId(commentCreator)
            .text(comment.text)
            .level(comment.level)
            .likesCount(comment.likesCount)
            .repliesCount(comment.commentsCount)
            .user(userViewData)
            .createdAt(comment.createdAt)
            .updatedAt(comment.updatedAt)
            .menuItems(convertOverflowMenuItems(comment.menuItems))
            .replies(
                convertComments(
                    replies,
                    usersMap,
                    postId,
                    comment.id
                )
            )
            .parentId(parentId)
            .parentComment(
                comment.parentComment?.let {
                    convertComment(
                        it,
                        usersMap,
                        postId
                    )
                }
            )
            .uuid(commentCreator)
            .tempId(comment.tempId)
            .build()
    }

    /**
     * convert list of [Activity] and usersMap [Map] of String to User
     * to list of [LMFeedActivityViewData]
     *
     * @param activities: list of [Activity]
     * @param usersMap: [Map] of String to User
     * */
    fun convertActivities(
        activities: List<Activity>,
        usersMap: Map<String, User>,
        widgetsMap: Map<String, Widget>
    ): List<LMFeedActivityViewData> {
        return activities.map {
            convertActivity(
                it,
                usersMap,
                widgetsMap
            )
        }
    }

    /**
     * converts [Activity] and usersMap [Map] of String to User
     * to [LMFeedActivityViewData]
     *
     * @param activity: an activity [LMFeedActivityViewData]
     * @param usersMap: [Map] of String to User
     * */
    private fun convertActivity(
        activity: Activity,
        usersMap: Map<String, User>,
        widgetsMap: Map<String, Widget>
    ): LMFeedActivityViewData {
        val activityByUser = if (activity.actionBy.isNotEmpty()) {
            convertUser(usersMap[activity.actionBy.last()])
        } else {
            LMFeedUserViewData.Builder().build()
        }

        return LMFeedActivityViewData.Builder()
            .id(activity.id)
            .isRead(activity.isRead)
            .actionOn(activity.actionOn)
            .actionBy(activity.actionBy)
            .entityType(activity.entityType)
            .entityId(activity.entityId)
            .entityOwnerId(activity.entityOwnerId)
            .action(activity.action)
            .cta(activity.cta)
            .activityText(activity.activityText)
            .activityEntityData(
                convertActivityEntityData(
                    activity.activityEntityData,
                    usersMap,
                    widgetsMap
                )
            )
            .activityByUser(activityByUser)
            .createdAt(activity.createdAt)
            .updatedAt(activity.updatedAt)
            .uuid(activity.uuid)
            .build()
    }

    private fun convertActivityEntityData(
        activityEntityData: ActivityEntityData?,
        usersMap: Map<String, User>,
        widgetsMap: Map<String, Widget>
    ): LMFeedActivityEntityViewData? {

        if (activityEntityData == null) {
            return null
        }
        val entityCreator = activityEntityData.uuid
        val user = usersMap[entityCreator]
        val replies = activityEntityData.replies?.toMutableList()

        val userViewData = if (user == null) {
            createDeletedUser()
        } else {
            convertUser(user)
        }

        return LMFeedActivityEntityViewData.Builder()
            .id(activityEntityData.id)
            .text(activityEntityData.text)
            .deleteReason(activityEntityData.deleteReason)
            .deletedBy(activityEntityData.deletedBy)
            .heading(activityEntityData.heading)
            .attachments(
                convertAttachments(
                    activityEntityData.attachments,
                    activityEntityData.id,
                    usersMap,
                    widgetsMap
                )
            )
            .communityId(activityEntityData.communityId)
            .isEdited(activityEntityData.isEdited)
            .isPinned(activityEntityData.isPinned)
            .userId(activityEntityData.userId)
            .user(userViewData)
            .replies(
                convertComments(
                    replies,
                    usersMap,
                    activityEntityData.postId ?: activityEntityData.id
                )
            )
            .level(activityEntityData.level)
            .createdAt(activityEntityData.createdAt)
            .updatedAt(activityEntityData.updatedAt)
            .uuid(activityEntityData.uuid)
            .deletedByUUID(activityEntityData.deletedByUUID)
            .build()
    }

    /**
     * convert list of [Like] to list of [LMFeedLikeViewData]
     * @param likes: list of [Like]
     * @param users: [Map] of String to User
     * */
    fun convertLikes(
        likes: List<Like>,
        users: Map<String, User>
    ): List<LMFeedLikeViewData> {
        return likes.map { like ->
            //get user id
            val likedById = like.uuid

            //get user
            val likedBy = users[likedById]

            //convert view data
            val likedByViewData = if (likedBy == null) {
                createDeletedUser()
            } else {
                convertUser(likedBy)
            }

            //create like view data
            LMFeedLikeViewData.Builder()
                .id(like.id)
                .userId(like.userId)
                .createdAt(like.createdAt)
                .updatedAt(like.updatedAt)
                .user(likedByViewData)
                .build()
        }
    }

    /**
     * convert list of [ReportTag] to list of [LMFeedReportTagViewData]
     * @param tags: list of [ReportTag]
     * */
    fun convertReportTag(
        tags: List<ReportTag>
    ): List<LMFeedReportTagViewData> {
        return tags.map { tag ->
            LMFeedReportTagViewData.Builder()
                .id(tag.id)
                .name(tag.name)
                .isSelected(false)
                .build()
        }
    }

    /**
     * convert list of [ReportTag] to list of [LMFeedReasonChooseViewData]
     * @param tags: list of [ReportTag]
     * */
    fun convertDeleteTag(
        tags: List<ReportTag>
    ): MutableList<LMFeedReasonChooseViewData> {
        return tags.map { tag ->
            LMFeedReasonChooseViewData.Builder()
                .value(tag.name)
                .build()
        }.toMutableList()
    }

    /**
     * convert [SingleUriData] to [LMFeedFileUploadViewData]
     * @param singleUriData: [SingleUriData]
     * */
    fun convertFileUploadViewData(
        singleUriData: SingleUriData
    ): LMFeedFileUploadViewData {
        return LMFeedFileUploadViewData.Builder()
            .uri(singleUriData.uri)
            .fileType(singleUriData.fileType)
            .width(singleUriData.width)
            .height(singleUriData.height)
            .thumbnailUri(singleUriData.thumbnailUri)
            .size(singleUriData.size)
            .mediaName(singleUriData.mediaName)
            .pdfPageCount(singleUriData.pdfPageCount)
            .duration(singleUriData.duration)
            .build()
    }

    /**
     * convert list of [Topic] to list of [LMFeedTopicViewData]
     * @param topics: list of [Topic]
     * */
    private fun convertTopics(topics: List<Topic>): List<LMFeedTopicViewData> {
        return topics.map {
            convertTopic(it)
        }
    }

    /**
     * convert list of [PollVote] to [LMFeedPollVoteViewData]
     * @param votes: list of [PollVote]
     * @param usersMap: [Map] of String to [User]
     * */
    fun convertPollVotes(
        votes: List<PollVote>,
        usersMap: Map<String, User>
    ): LMFeedPollVoteViewData {
        val vote = votes.firstOrNull() ?: return LMFeedPollVoteViewData.Builder().build()
        val usersVoted = vote.userIds

        // get object of users who have voted on the option
        val userVotedViewData = usersVoted.map { userVoted ->
            convertUser(usersMap[userVoted])
        }

        return LMFeedPollVoteViewData.Builder()
            .id(vote.id)
            .usersVoted(userVotedViewData)
            .build()
    }

    /**
     * convert [Widget] to [LMFeedWidgetViewData]
     * @param entityId: [String]
     * @param widgetsMap: [Map] of String to [Widget]
     * @return [LMFeedWidgetViewData]
     * */
    private fun convertWidget(
        entityId: String?,
        widgetsMap: Map<String, Widget>
    ): LMFeedWidgetViewData? {
        if (entityId.isNullOrEmpty()) return null
        val widget = widgetsMap[entityId] ?: return null

        return LMFeedWidgetViewData.Builder()
            .id(widget.id)
            .createdAt(widget.createdAt)
            .metadata(widget.metadata)
            .parentEntityId(widget.parentEntityId)
            .parentEntityType(widget.parentEntityType)
            .updatedAt(widget.updatedAt)
            .build()
    }

    /**--------------------------------
     * View Data Model -> Network Model
    --------------------------------*/

    fun convertPost(
        temporaryId: String,
        workerUUID: String,
        text: String?,
        fileUris: List<LMFeedFileUploadViewData>,
        metadata: JSONObject?,
    ): Post {
        return Post.Builder()
            .tempId(temporaryId)
            .id(temporaryId)
            .workerUUID(workerUUID)
            .text(text ?: "")
            .attachments(convertAttachments(fileUris, Pair(null, metadata)))
            .build()
    }

    //creates a list of network model of attachments from the provided list of attachment view data
    fun createAttachments(
        attachments: List<LMFeedAttachmentViewData>
    ): List<Attachment> {
        return attachments.map {
            convertAttachment(it)
        }
    }

    //creates a network model of attachment from the provided attachment view data
    private fun convertAttachment(
        attachment: LMFeedAttachmentViewData
    ): Attachment {
        return Attachment.Builder()
            .attachmentType(attachment.attachmentType.getAttachmentType())
            .attachmentMeta(convertAttachmentMeta(attachment.attachmentMeta))
            .build()
    }

    private fun convertAttachmentMeta(
        attachmentMeta: LMFeedAttachmentMetaViewData
    ): AttachmentMeta {
        return AttachmentMeta.Builder()
            .name(attachmentMeta.name)
            .ogTags(convertOGTags(attachmentMeta.ogTags))
            .url(attachmentMeta.url)
            .size(attachmentMeta.size)
            .duration(attachmentMeta.duration)
            .pageCount(attachmentMeta.pageCount)
            .format(attachmentMeta.format)
            .height(attachmentMeta.height)
            .width(attachmentMeta.width)
            .meta(attachmentMeta.widgetViewData?.metadata)
            .build()
    }

    // creates attachment list of Network Model for link attachment
    fun convertAttachments(
        linkOGTagsViewData: LMFeedLinkOGTagsViewData,
        widgetData: Pair<String?, JSONObject?>?
    ): List<Attachment> {
        val attachments = ArrayList<Attachment>()

        //add link attachment
        attachments.add(
            Attachment.Builder()
                .attachmentType(AttachmentType.LINK)
                .attachmentMeta(convertAttachmentMeta(linkOGTagsViewData))
                .build()
        )

        //add custom widget
        widgetData?.second?.let {
            attachments.add(convertCustomWidget(widgetData.first, it))
        }
        return attachments
    }

    // creates AttachmentMeta Network Model for link attachment meta
    private fun convertAttachmentMeta(
        linkOGTagsViewData: LMFeedLinkOGTagsViewData
    ): AttachmentMeta {
        return AttachmentMeta.Builder()
            .ogTags(convertOGTags(linkOGTagsViewData))
            .build()
    }

    // converts LinkOGTags view data model to network model
    private fun convertOGTags(
        linkOGTagsViewData: LMFeedLinkOGTagsViewData?
    ): LinkOGTags? {
        if (linkOGTagsViewData == null) return null
        return LinkOGTags.Builder()
            .title(linkOGTagsViewData.title)
            .image(linkOGTagsViewData.image)
            .description(linkOGTagsViewData.description)
            .url(linkOGTagsViewData.url)
            .build()
    }

    // converts list of [LMFeedFileUploadViewData] to list of network [Attachment] model
    private fun convertAttachments(
        fileUris: List<LMFeedFileUploadViewData>,
        widgetData: Pair<String?, JSONObject?>?
    ): List<Attachment> {
        val attachments = ArrayList<Attachment>()

        //add media files
        attachments.addAll(
            fileUris.map {
                convertAttachment(it)
            }
        )

        //add meta
        widgetData?.second?.let {
            attachments.add(
                convertCustomWidget(widgetData.first, it)
            )
        }

        return attachments
    }

    // converts [LMFeedFileUploadViewData] to network [Attachment] model
    private fun convertAttachment(fileUri: LMFeedFileUploadViewData): Attachment {
        val attachmentType = when (fileUri.fileType) {
            com.likeminds.customgallery.media.model.IMAGE -> {
                AttachmentType.IMAGE
            }

            com.likeminds.customgallery.media.model.VIDEO -> {
                AttachmentType.VIDEO
            }

            else -> {
                AttachmentType.DOCUMENT
            }
        }

        return Attachment.Builder()
            .attachmentType(attachmentType)
            .attachmentMeta(convertAttachmentMeta(fileUri))
            .build()
    }

    //converts [LMFeedFileUploadViewData] to network [AttachmentMeta] model
    private fun convertAttachmentMeta(fileUri: LMFeedFileUploadViewData): AttachmentMeta {
        val url = String(
            Base64.decode(
                LMFeedAWSKeys.getBucketBaseUrl(),
                Base64.DEFAULT
            )
        ) + fileUri.awsFolderPath
        return AttachmentMeta.Builder()
            .url(url)
            .awsFolderPath(fileUri.awsFolderPath)
            .format(fileUri.format)
            .localFilePath(fileUri.localFilePath)
            .localUri(fileUri.uri)
            .name(fileUri.mediaName)
            .pageCount(fileUri.pdfPageCount)
            .size(fileUri.size)
            .awsFolderPath(fileUri.awsFolderPath)
            .thumbnailUrl(fileUri.thumbnailUri.toString())
            .duration(fileUri.duration)
            .height(fileUri.height)
            .width(fileUri.width)
            .build()
    }

    // converts list of Topic view data model to list of network model
    fun convertTopicsViewData(topics: List<LMFeedTopicViewData>?): List<Topic> {
        if (topics == null) {
            return emptyList()
        }

        return topics.map {
            convertTopic(it)
        }
    }

    // converts Topic view data model to network model
    private fun convertTopic(topic: LMFeedTopicViewData): Topic {
        return Topic.Builder()
            .id(topic.id)
            .isEnabled(topic.isEnabled)
            .name(topic.name)
            .build()
    }

    // converts comments count to [LMFeedCommentsCountViewData]
    fun convertCommentsCount(commentsCount: Int): LMFeedCommentsCountViewData {
        return LMFeedCommentsCountViewData.Builder()
            .commentsCount(commentsCount)
            .build()
    }

    // converts [LMFeedPollViewData] to [Attachment]
    fun convertPoll(
        poll: LMFeedPollViewData,
        widgetData: Pair<String?, JSONObject?>?
    ): List<Attachment> {
        val attachments = ArrayList<Attachment>()

        attachments.add(
            Attachment.Builder()
                .attachmentType(AttachmentType.POLL)
                .attachmentMeta(convertPollAttachmentMeta(poll))
                .build()
        )

        widgetData?.second?.let {
            attachments.add(
                convertCustomWidget(widgetData.first, it)
            )
        }

        return attachments
    }

    // converts [LMFeedPollViewData] to [AttachmentMeta]
    private fun convertPollAttachmentMeta(poll: LMFeedPollViewData): AttachmentMeta {
        return AttachmentMeta.Builder()
            .entityId(poll.id)
            .title(poll.title)
            .expiryTime(poll.expiryTime)
            .pollOptions(poll.options.map { it.text })
            .multiSelectState(poll.multipleSelectState)
            .pollType(poll.pollType)
            .multiSelectNumber(poll.multipleSelectNumber)
            .isAnonymous(poll.isAnonymous)
            .allowAddOption(poll.allowAddOption)
            .build()
    }

    // converts [LMFeedWidgetViewData] to [Attachment]
    fun convertCustomWidget(entityId: String?, metadata: JSONObject): Attachment {
        return Attachment.Builder()
            .attachmentType(AttachmentType.CUSTOM_WIDGET)
            .attachmentMeta(convertCustomWidgetAttachmentMeta(entityId, metadata))
            .build()
    }

    // converts [LMFeedWidgetViewData] to [AttachmentMeta]
    private fun convertCustomWidgetAttachmentMeta(
        entityId: String?,
        metadata: JSONObject
    ): AttachmentMeta {
        return AttachmentMeta.Builder()
            .entityId(entityId)
            .meta(metadata)
            .build()
    }
}