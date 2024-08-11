package com.likeminds.feed.android.core.socialfeed.util

import android.content.Context
import android.text.*
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.util.Linkify
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.text.util.LinkifyCompat
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.post.model.*
import com.likeminds.feed.android.core.postmenu.model.PIN_POST_MENU_ITEM_ID
import com.likeminds.feed.android.core.postmenu.model.UNPIN_POST_MENU_ITEM_ID
import com.likeminds.feed.android.core.socialfeed.adapter.LMFeedPostAdapterListener
import com.likeminds.feed.android.core.socialfeed.model.*
import com.likeminds.feed.android.core.topics.model.LMFeedTopicViewData
import com.likeminds.feed.android.core.ui.base.styles.setStyle
import com.likeminds.feed.android.core.ui.base.views.LMFeedChipGroup
import com.likeminds.feed.android.core.ui.base.views.LMFeedTextView
import com.likeminds.feed.android.core.ui.theme.LMFeedTheme
import com.likeminds.feed.android.core.ui.widgets.poll.adapter.LMFeedPollOptionsAdapterListener
import com.likeminds.feed.android.core.ui.widgets.poll.view.LMFeedPostPollView
import com.likeminds.feed.android.core.ui.widgets.post.postactionview.view.LMFeedPostActionHorizontalView
import com.likeminds.feed.android.core.ui.widgets.post.postactionview.view.LMFeedPostActionVerticalView
import com.likeminds.feed.android.core.ui.widgets.post.postheaderview.view.LMFeedPostHeaderView
import com.likeminds.feed.android.core.ui.widgets.post.postmedia.view.*
import com.likeminds.feed.android.core.utils.*
import com.likeminds.feed.android.core.utils.LMFeedValueUtils.getFormatedNumber
import com.likeminds.feed.android.core.utils.LMFeedValueUtils.getValidTextForLinkify
import com.likeminds.feed.android.core.utils.LMFeedValueUtils.pluralizeOrCapitalize
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.hide
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.show
import com.likeminds.feed.android.core.utils.link.LMFeedLinkMovementMethod
import com.likeminds.feed.android.core.utils.pluralize.model.LMFeedWordAction
import com.likeminds.usertagging.util.UserTaggingDecoder

object LMFeedPostBinderUtils {

    // customizes the header view of the post
    fun customizePostHeaderView(postHeaderView: LMFeedPostHeaderView) {
        val postHeaderViewStyle = LMFeedStyleTransformer.postViewStyle.postHeaderViewStyle
        postHeaderView.setStyle(postHeaderViewStyle)
    }

    // customizes the content view of the post
    fun customizePostContentView(
        postContent: LMFeedTextView,
    ) {
        val postContentTextStyle = LMFeedStyleTransformer.postViewStyle.postContentTextStyle
        postContent.setStyle(postContentTextStyle)
    }

    // customizes the horizontal post action view
    fun customizePostActionHorizontalView(postActionHorizontalView: LMFeedPostActionHorizontalView) {
        val postActionViewStyle = LMFeedStyleTransformer.postViewStyle.postActionViewStyle
        postActionHorizontalView.setStyle(postActionViewStyle)
    }

    // customizes the vertical post action view
    fun customizePostActionVerticalView(postActionVerticalView: LMFeedPostActionVerticalView) {
        val postActionViewStyle = LMFeedStyleTransformer.postViewStyle.postActionViewStyle
        postActionVerticalView.setStyle(postActionViewStyle)
    }

    // customizes the topics view of the post
    fun customizePostTopicsGroup(postTopicsGroup: LMFeedChipGroup) {
        postTopicsGroup.apply {
            val postTopicsGroupViewStyle =
                LMFeedStyleTransformer.postViewStyle.postTopicsGroupStyle

            setStyle(postTopicsGroupViewStyle)
        }
    }

    //bind post data to specific views
    fun setPostBindData(
        headerView: LMFeedPostHeaderView,
        contentView: LMFeedTextView,
        data: LMFeedPostViewData,
        position: Int,
        topicsView: LMFeedChipGroup,
        postAdapterListener: LMFeedPostAdapterListener,
        returnBinder: () -> Unit,
        executeBinder: () -> Unit
    ) {
        if (data.fromPostLiked || data.fromPostSaved || data.fromVideoAction) {
            // update fromLiked/fromSaved variables and return from binder
            postAdapterListener.updateFromLikedSaved(position, data)
            returnBinder()
        } else {
            // call all the common functions

            // sets data to the header view
            setPostHeaderViewData(
                headerView,
                data.headerViewData
            )

            // sets the text content of the post
            setPostContentViewData(
                contentView,
                data,
                postAdapterListener,
                position
            )

            setPostTopicsViewData(
                topicsView,
                data.topicsViewData
            )

            executeBinder()
        }
    }

    // sets the data in the post header view
    private fun setPostHeaderViewData(
        headerView: LMFeedPostHeaderView,
        headerViewData: LMFeedPostHeaderViewData
    ) {
        headerView.apply {
            setPinIcon(headerViewData.isPinned)
            setPostEdited(headerViewData.isEdited)

            // post author data
            val author = headerViewData.user
            setAuthorName(author.name)
            setAuthorCustomTitle(author.customTitle)
            setAuthorImage(author)

            setTimestamp(headerViewData.createdAt)
        }
    }

    //sets the data in the post content view
    private fun setPostContentViewData(
        contentView: LMFeedTextView,
        postViewData: LMFeedPostViewData,
        postAdapterListener: LMFeedPostAdapterListener,
        position: Int
    ) {
        contentView.apply {
            val contentViewData = postViewData.contentViewData
            val postContent = contentViewData.text ?: return

            val postContentStyle = LMFeedStyleTransformer.postViewStyle.postContentTextStyle
            val maxLines = (postContentStyle.maxLines ?: LMFeedTheme.DEFAULT_POST_MAX_LINES)

            /**
             * Text is modified as Linkify doesn't accept texts with these specific unicode characters
             * @see #Linkify.containsUnsupportedCharacters(String)
             */
            val textForLinkify = postContent.getValidTextForLinkify()

            var alreadySeenFullContent = contentViewData.alreadySeenFullContent == true

            if (textForLinkify.isEmpty()) {
                hide()
                return
            } else {
                show()
            }

            // post is used here to get lines count in the text view
            post {
                setOnClickListener {
                    postAdapterListener.onPostContentClicked(position, postViewData)
                }

                UserTaggingDecoder.decodeRegexIntoSpannableText(
                    this,
                    textForLinkify.trim(),
                    enableClick = true,
                    highlightColor = ContextCompat.getColor(
                        context,
                        LMFeedTheme.getTextLinkColor()
                    ),
                    hasAtRateSymbol = true,
                ) { route ->
                    val uuid = route.getQueryParameter("member_id")
                        ?: route.getQueryParameter("user_id")
                        ?: route.getQueryParameter("uuid")
                        ?: route.lastPathSegment
                        ?: return@decodeRegexIntoSpannableText

                    postAdapterListener.onPostTaggedMemberClicked(position, uuid)
                }

                val shortText: String? = LMFeedSeeMoreUtil.getShortContent(
                    this,
                    maxLines,
                    LMFeedTheme.getPostCharacterLimit()
                )

                val trimmedText =
                    if (!alreadySeenFullContent && !shortText.isNullOrEmpty()) {
                        editableText.subSequence(0, shortText.length)
                    } else {
                        editableText
                    }

                val seeMoreSpannableStringBuilder = SpannableStringBuilder()
                val expandableText = postContentStyle.expandableCTAText

                if (!alreadySeenFullContent && !shortText.isNullOrEmpty() && expandableText != null) {

                    val expandableTextColor = ContextCompat.getColor(
                        context,
                        postContentStyle.expandableCTAColor ?: R.color.lm_feed_brown_grey
                    )
                    val expandSpannable = SpannableStringBuilder(expandableText)
                    expandSpannable.setSpan(
                        ForegroundColorSpan(expandableTextColor),
                        0,
                        expandSpannable.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    val seeMoreClickableSpan = object : ClickableSpan() {
                        override fun onClick(view: View) {
                            setOnClickListener {
                                return@setOnClickListener
                            }
                            alreadySeenFullContent = true
                            val updatedPost = updatePostForSeeFullContent(postViewData)
                            postAdapterListener.onPostContentSeeMoreClicked(position, updatedPost)
                        }

                        override fun updateDrawState(textPaint: TextPaint) {
                            textPaint.isUnderlineText = false
                        }
                    }

                    seeMoreSpannableStringBuilder.append(expandSpannable)
                    seeMoreSpannableStringBuilder.setSpan(
                        seeMoreClickableSpan,
                        0,
                        expandSpannable.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }

                text = TextUtils.concat(
                    trimmedText,
                    seeMoreSpannableStringBuilder
                )

                val linkifyLinks =
                    (Linkify.WEB_URLS or Linkify.EMAIL_ADDRESSES or Linkify.PHONE_NUMBERS)
                LinkifyCompat.addLinks(this, linkifyLinks)
                movementMethod = LMFeedLinkMovementMethod { url ->
                    setOnClickListener {
                        return@setOnClickListener
                    }

                    postAdapterListener.onPostContentLinkClicked(url)
                    true
                }
            }
        }
    }

    // sets the data in the post horizontal action view
    fun setPostHorizontalActionViewData(
        horizontalActionView: LMFeedPostActionHorizontalView,
        postActionViewData: LMFeedPostActionViewData
    ) {
        horizontalActionView.apply {
            setLikesIcon(postActionViewData.isLiked)
            setSaveIcon(postActionViewData.isSaved)

            val likesCount = postActionViewData.likesCount

            val likesCountText = if (likesCount == 0) {
                context.getString(R.string.lm_feed_like)
            } else {
                context.resources.getQuantityString(
                    R.plurals.lm_feed_likes,
                    likesCount,
                    likesCount
                )
            }
            setLikesCount(likesCountText)

            val commentsCount = postActionViewData.commentsCount

            val commentsCountText = if (commentsCount == 0) {
                context.getString(R.string.lm_feed_add_comment)
            } else {
                context.resources.getQuantityString(
                    R.plurals.lm_feed_comments,
                    commentsCount,
                    commentsCount
                )
            }
            setCommentsCount(commentsCountText)
        }
    }

    // sets the data in the post vertical action view
    fun setPostVerticalActionViewData(
        verticalActionView: LMFeedPostActionVerticalView,
        postActionViewData: LMFeedPostActionViewData
    ) {
        verticalActionView.apply {
            setLikesIcon(postActionViewData.isLiked)

            val likesCount = postActionViewData.likesCount

            val likesCountText = if (likesCount == 0) {
                context.getString(R.string.lm_feed_like)
            } else {
                likesCount.toLong().getFormatedNumber()
            }

            setLikesCount(likesCountText)
        }
    }

    // sets the data in post topics view
    private fun setPostTopicsViewData(
        postTopicsGroup: LMFeedChipGroup,
        topics: List<LMFeedTopicViewData>
    ) {
        if (topics.isEmpty()) {
            postTopicsGroup.hide()
        } else {
            postTopicsGroup.apply {
                show()
                removeAllChips()
                topics.forEach { topic ->
                    addChip(topic.name, LMFeedStyleTransformer.postViewStyle.postTopicChipsStyle)
                }
            }
        }
    }

    // update post object for a like action
    fun updatePostForLike(oldPostViewData: LMFeedPostViewData): LMFeedPostViewData {
        val postActionData = oldPostViewData.actionViewData
        val newLikesCount = if (postActionData.isLiked) {
            postActionData.likesCount - 1
        } else {
            postActionData.likesCount + 1
        }

        val updatedIsLiked = !postActionData.isLiked

        val updatedFooterData = postActionData.toBuilder()
            .isLiked(updatedIsLiked)
            .likesCount(newLikesCount)
            .build()

        return oldPostViewData.toBuilder()
            .actionViewData(updatedFooterData)
            .fromPostLiked(true)
            .build()
    }

    // update post object for a save action
    fun updatePostForSave(oldPostViewData: LMFeedPostViewData): LMFeedPostViewData {
        val postActionData = oldPostViewData.actionViewData
        val updatedFooterData = postActionData.toBuilder()
            .isSaved(!postActionData.isSaved)
            .build()

        return oldPostViewData.toBuilder()
            .actionViewData(updatedFooterData)
            .fromPostSaved(true)
            .build()
    }

    // update post object for a pin action
    fun updatePostForPin(
        context: Context,
        oldPostViewData: LMFeedPostViewData
    ): LMFeedPostViewData? {
        //get pin menu item
        val menuItems = oldPostViewData.headerViewData.menuItems.toMutableList()
        val pinPostIndex = menuItems.indexOfFirst {
            (it.id == PIN_POST_MENU_ITEM_ID)
        }

        //if pin item doesn't exist
        if (pinPostIndex == -1) return null

        //update pin menu item
        val pinPostMenuItem = menuItems[pinPostIndex]
        val newPinPostMenuItem =
            pinPostMenuItem.toBuilder().id(UNPIN_POST_MENU_ITEM_ID)
                .title(
                    context.getString(
                        R.string.lm_feed_unpin_this_s,
                        LMFeedCommunityUtil.getPostVariable()
                            .pluralizeOrCapitalize(LMFeedWordAction.FIRST_LETTER_CAPITAL_SINGULAR)
                    )
                )
                .build()
        menuItems[pinPostIndex] = newPinPostMenuItem

        //update the header view data
        val updatedHeaderViewData = oldPostViewData.headerViewData.toBuilder()
            .isPinned(!oldPostViewData.headerViewData.isPinned)
            .menuItems(menuItems)
            .build()

        //update the post view data
        return oldPostViewData.toBuilder()
            .headerViewData(updatedHeaderViewData)
            .build()
    }

    // update post object for a unpin action
    fun updatePostForUnpin(
        context: Context,
        oldPostViewData: LMFeedPostViewData
    ): LMFeedPostViewData? {
        val headerViewData = oldPostViewData.headerViewData
        //get unpin menu item
        val menuItems = headerViewData.menuItems.toMutableList()
        val unPinPostIndex = menuItems.indexOfFirst {
            (it.id == UNPIN_POST_MENU_ITEM_ID)
        }

        //if unpin item doesn't exist
        if (unPinPostIndex == -1) return null

        //update unpin menu item
        val unPinPostMenuItem = menuItems[unPinPostIndex]
        val newUnPinPostMenuItem =
            unPinPostMenuItem.toBuilder().id(PIN_POST_MENU_ITEM_ID)
                .title(
                    context.getString(
                        R.string.lm_feed_pin_this_s,
                        LMFeedCommunityUtil.getPostVariable()
                            .pluralizeOrCapitalize(LMFeedWordAction.FIRST_LETTER_CAPITAL_SINGULAR)
                    )
                )
                .build()
        menuItems[unPinPostIndex] = newUnPinPostMenuItem

        //update header view data
        val updatedHeaderViewData = headerViewData.toBuilder()
            .isPinned(!headerViewData.isPinned)
            .menuItems(menuItems)
            .build()

        //update the post view data
        return oldPostViewData.toBuilder()
            .headerViewData(updatedHeaderViewData)
            .build()
    }

    // update post object for document expanded action
    fun updatePostForDocumentExpanded(oldPostViewData: LMFeedPostViewData): LMFeedPostViewData {
        val mediaData = oldPostViewData.mediaViewData

        val updatedMediaData = mediaData.toBuilder()
            .isExpanded(true)
            .build()

        return oldPostViewData.toBuilder()
            .mediaViewData(updatedMediaData)
            .build()
    }

    //updates post object for a see full content action and returns updated post
    fun updatePostForSeeFullContent(oldPostViewData: LMFeedPostViewData): LMFeedPostViewData {
        val contentViewData = oldPostViewData.contentViewData.toBuilder()
            .alreadySeenFullContent(true)
            .build()

        //return updated comment view data
        return oldPostViewData.toBuilder()
            .contentViewData(contentViewData)
            .build()
    }

    // bind data for single image post
    fun bindPostSingleImage(
        ivPost: LMFeedPostImageMediaView,
        mediaData: LMFeedMediaViewData
    ) {
        val postImageMediaStyle =
            LMFeedStyleTransformer.postViewStyle.postMediaViewStyle.postImageMediaStyle ?: return

        mediaData.attachments.first {
            it.attachmentType == IMAGE
        }.attachmentMeta.url?.let { url ->
            ivPost.setImage(url, postImageMediaStyle)
        }
    }

    // bind data for link preview post
    fun bindPostMediaLinkView(
        linkView: LMFeedPostLinkMediaView,
        linkOgTags: LMFeedLinkOGTagsViewData
    ) {
        linkView.apply {
            setLinkTitle(linkOgTags.title)
            setLinkDescription(linkOgTags.description)
            setLinkImage(linkOgTags.image)
            setLinkUrl(linkOgTags.url)
        }
    }


    // bind data for documents post
    fun bindPostDocuments(
        position: Int,
        postDocumentsMediaView: LMFeedPostDocumentsMediaView,
        mediaData: LMFeedMediaViewData,
        listener: LMFeedPostAdapterListener
    ) {
        //sets documents adapter and handles show more functionality of documents
        postDocumentsMediaView.setAdapter(
            position,
            mediaData,
            listener
        )
    }

    // bind data in nested document view
    fun bindPostMediaDocument(
        binding: LMFeedPostDocumentView,
        data: LMFeedAttachmentViewData
    ) {
        binding.apply {
            val attachmentMeta = data.attachmentMeta

            setDocumentName(attachmentMeta.name)
            setDocumentPages(attachmentMeta.pageCount)
            setDocumentSize(attachmentMeta.size)
            setDocumentType(attachmentMeta.format)
        }
    }

    //bind data for multiple media image view post
    fun bindMultipleMediaImageView(
        ivPost: LMFeedPostImageMediaView,
        attachment: LMFeedAttachmentViewData?
    ) {
        val postImageMediaStyle =
            LMFeedStyleTransformer.postViewStyle.postMediaViewStyle.postImageMediaStyle ?: return

        val attachmentUrl =
            attachment?.attachmentMeta?.url ?: attachment?.attachmentMeta?.uri ?: return

        ivPost.setImage(attachmentUrl, postImageMediaStyle)
    }

    // bind data to view page for multi media post
    fun bindMultipleMediaView(
        position: Int,
        multipleMediaView: LMFeedPostMultipleMediaView,
        data: LMFeedMediaViewData,
        listener: LMFeedPostAdapterListener
    ) {
        multipleMediaView.apply {
            //sets multiple media view pager
            setViewPager(
                position,
                listener,
                data.attachments
            )
        }
    }

    //binds data for the poll media view in the post
    fun bindPostPollMediaView(
        pollPosition: Int,
        pollView: LMFeedPostPollView,
        mediaData: LMFeedMediaViewData,
        listener: LMFeedPollOptionsAdapterListener
    ) {
        pollView.apply {
            val pollAttachment = mediaData.attachments.first()
            val pollViewData = pollAttachment.attachmentMeta.poll ?: return

            setPollTitle(pollViewData.title)
            setPollInfo(pollViewData.getPollSelectionText(context))
            setMemberVotedCount(pollViewData.pollAnswerText)
            setTimeLeft(pollViewData.getTimeLeftInPoll(context))
            setPollOptions(
                pollPosition,
                pollViewData.options,
                null,
                listener
            )
            setSubmitButtonVisibility(pollViewData)
            setEditPollVoteVisibility(pollViewData)
            setAddPollOptionButtonVisibility(pollViewData)
        }
    }
}