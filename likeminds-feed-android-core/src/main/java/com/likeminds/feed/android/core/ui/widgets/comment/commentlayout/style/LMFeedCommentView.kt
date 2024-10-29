package com.likeminds.feed.android.core.ui.widgets.comment.commentlayout.style

import android.content.Context
import android.text.*
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.util.Linkify
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.text.util.LinkifyCompat
import androidx.core.view.isVisible
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.databinding.LmFeedCommentViewBinding
import com.likeminds.feed.android.core.ui.base.styles.*
import com.likeminds.feed.android.core.ui.theme.LMFeedAppearance
import com.likeminds.feed.android.core.ui.widgets.comment.commentlayout.view.LMFeedCommentViewStyle
import com.likeminds.feed.android.core.utils.*
import com.likeminds.feed.android.core.utils.LMFeedValueUtils.getValidTextForLinkify
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.hide
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.show
import com.likeminds.feed.android.core.utils.link.LMFeedLinkMovementMethod
import com.likeminds.feed.android.core.utils.link.LMFeedOnLinkClickListener
import com.likeminds.feed.android.core.utils.listeners.LMFeedOnClickListener
import com.likeminds.feed.android.core.utils.listeners.LMFeedOnTaggedMemberClickListener
import com.likeminds.feed.android.core.utils.user.LMFeedUserImageUtil
import com.likeminds.feed.android.core.utils.user.LMFeedUserViewData
import com.likeminds.usertagging.util.UserTaggingDecoder

class LMFeedCommentView : ConstraintLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet?, defStyle: Int) : super(
        context,
        attributeSet,
        defStyle
    )

    //returns the comment menu icon
    val commentMenu: View get() = binding.ivCommentMenu

    private val inflater =
        (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)

    private val binding = LmFeedCommentViewBinding.inflate(inflater, this, true)

    //sets provided [LMFeedCommentViewStyle] to the comment view
    fun setStyle(commentViewStyle: LMFeedCommentViewStyle) {

        commentViewStyle.apply {
            //set background color
            backgroundColor?.let {
                setBackgroundColor(ContextCompat.getColor(context, backgroundColor))
            }

            configureCommenterImage(commenterImageViewStyle)
            configureCommenterName(commenterNameTextStyle)
            configureCommentContent(commentContentStyle)
            configureMenuIcon(menuIconStyle)
            configureLikeIcon(likeIconStyle)
            configureLikeText(likeTextStyle)
            configureReplyText(replyTextStyle)
            configureReplyCountText(replyCountTextStyle)
            configureTimestampText(timestampTextStyle)
            configureCommentEditedText(commentEditedTextStyle)
        }
    }

    private fun configureCommenterImage(commenterImageViewStyle: LMFeedImageStyle?) {
        binding.ivCommenterImage.apply {
            if (commenterImageViewStyle == null) {
                hide()
            } else {
                show()
                setStyle(commenterImageViewStyle)
            }
        }
    }

    private fun configureCommenterName(commenterNameTextStyle: LMFeedTextStyle) {
        binding.tvCommenterName.setStyle(commenterNameTextStyle)
    }

    private fun configureCommentContent(commentContentStyle: LMFeedTextStyle) {
        binding.tvCommentContent.setStyle(commentContentStyle)
    }

    private fun configureMenuIcon(menuIconStyle: LMFeedIconStyle?) {
        binding.ivCommentMenu.apply {
            if (menuIconStyle == null) {
                hide()
            } else {
                show()
                setStyle(menuIconStyle)
            }
        }
    }

    private fun configureLikeIcon(likeIconStyle: LMFeedIconStyle) {
        binding.ivLike.setStyle(likeIconStyle)
    }

    private fun configureLikeText(likeTextStyle: LMFeedTextStyle?) {
        binding.tvLikesCount.apply {
            if (likeTextStyle == null) {
                hide()
            } else {
                setStyle(likeTextStyle)
            }
        }
    }

    private fun configureReplyText(replyTextStyle: LMFeedTextStyle?) {
        binding.apply {
            if (replyTextStyle == null) {
                tvReply.hide()
                likesSeparator.hide()
            } else {
                tvReply.setStyle(replyTextStyle)
                tvReply.show()
                likesSeparator.show()
            }
        }
    }

    private fun configureReplyCountText(replyCountTextStyle: LMFeedTextStyle?) {
        binding.apply {
            if (replyCountTextStyle == null) {
                groupReplies.hide()
            } else {
                tvReplyCount.setStyle(replyCountTextStyle)
            }
        }
    }

    private fun configureTimestampText(timestampTextStyle: LMFeedTextStyle?) {
        binding.tvCommentTime.apply {
            if (timestampTextStyle == null) {
                hide()
            } else {
                setStyle(timestampTextStyle)
            }
        }
    }

    private fun configureCommentEditedText(commentEditedTextStyle: LMFeedTextStyle?) {
        binding.apply {
            if (commentEditedTextStyle == null) {
                tvEdited.hide()
                viewDotEdited.hide()
            } else {
                tvEdited.setStyle(commentEditedTextStyle)
            }
        }
    }

    /**
     * Sets comment creator image in the view.
     *
     * @param user - data of the commenter.
     */
    fun setCommentCreatorImage(user: LMFeedUserViewData) {
        val commentViewStyle = LMFeedStyleTransformer.postDetailFragmentViewStyle.commentViewStyle
        var commenterImageViewStyle = commentViewStyle.commenterImageViewStyle ?: return

        if (commenterImageViewStyle.placeholderSrc == null) {
            commenterImageViewStyle = commenterImageViewStyle.toBuilder().placeholderSrc(
                LMFeedUserImageUtil.getNameDrawable(
                    user.sdkClientInfoViewData.uuid,
                    user.name,
                    commenterImageViewStyle.isCircle,
                ).first
            ).build()
        }
        binding.ivCommenterImage.setImage(user.imageUrl, commenterImageViewStyle)
    }

    /**
     * Sets the name of the comment creator
     *
     * @param commenterName - string to be set for commenter's name.
     */
    fun setCommentCreatorName(commenterName: String) {
        binding.tvCommenterName.text = commenterName
    }

    /**
     * Sets the content of the comment
     *
     * @param commentText - string to be set for comment text.
     * @param alreadySeenFullContent - whether the comment content was seen completely or not.
     * @param onCommentSeeMoreClickListener [LMFeedOnClickListener] - interface to have click listener
     */
    fun setCommentContent(
        commentText: String,
        alreadySeenFullContent: Boolean?,
        onCommentSeeMoreClickListener: LMFeedOnClickListener,
        onMemberTagClickListener: LMFeedOnTaggedMemberClickListener
    ) {
        binding.tvCommentContent.apply {

            /**
             * Text is modified as Linkify doesn't accept texts with these specific unicode characters
             * @see #Linkify.containsUnsupportedCharacters(String)
             */
            val textForLinkify = commentText.getValidTextForLinkify()

            var alreadySeen = alreadySeenFullContent == true

            if (textForLinkify.isEmpty()) {
                hide()
                return
            } else {
                show()
            }

            // todo: take see more read more from styles
            // span for seeMore feature
            val seeMoreColor = ContextCompat.getColor(context, R.color.lm_feed_brown_grey)
            val seeMore = SpannableStringBuilder(context.getString(R.string.lm_feed_see_more))
            seeMore.setSpan(
                ForegroundColorSpan(seeMoreColor),
                0,
                seeMore.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            val seeMoreClickableSpan = object : ClickableSpan() {
                override fun onClick(view: View) {
                    alreadySeen = true
                    onCommentSeeMoreClickListener.onClick()
                }

                override fun updateDrawState(textPaint: TextPaint) {
                    textPaint.isUnderlineText = false
                }
            }

            // post is used here to get lines count in the text view
            post {
                // decodes tags in text and creates span around those tags
                UserTaggingDecoder.decodeRegexIntoSpannableText(
                    this,
                    textForLinkify,
                    enableClick = true,
                    highlightColor = ContextCompat.getColor(
                        context,
                        LMFeedAppearance.getTextLinkColor()
                    ),
                ) { route ->
                    val uuid = route.getQueryParameter("member_id")
                        ?: route.getQueryParameter("user_id")
                        ?: route.getQueryParameter("uuid")
                        ?: route.lastPathSegment
                        ?: return@decodeRegexIntoSpannableText

                    onMemberTagClickListener.onMemberTaggedClicked(uuid)
                }

                // gets short text to set with seeMore
                val shortText: String? = LMFeedSeeMoreUtil.getShortContent(
                    this,
                    3,
                    500
                )

                val trimmedText =
                    if (!alreadySeen && !shortText.isNullOrEmpty()) {
                        editableText.subSequence(0, shortText.length)
                    } else {
                        editableText
                    }

                val seeMoreSpannableStringBuilder = SpannableStringBuilder()
                if (!alreadySeen && !shortText.isNullOrEmpty()) {
                    seeMoreSpannableStringBuilder.append("...")
                    seeMoreSpannableStringBuilder.append(seeMore)
                    seeMoreSpannableStringBuilder.setSpan(
                        seeMoreClickableSpan,
                        3,
                        seeMore.length + 3,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }

                // appends see more text at last
                text = TextUtils.concat(
                    trimmedText,
                    seeMoreSpannableStringBuilder
                )
            }
        }
    }

    /**
     * Sets the time the comment was created.
     *
     * @param createdAtTimeStamp - timestamp when the comment was created.
     */
    fun setTimestamp(createdAtTimeStamp: Long) {
        binding.tvCommentTime.text = LMFeedTimeUtil.getRelativeCreationTimeInString(createdAtTimeStamp)
    }

    /**
     * Shows the `Edited` text if the comment was edited.
     *
     * @param isEdited - whether the comment was edited or not.
     */
    fun setCommentEdited(isEdited: Boolean) {
        binding.viewDotEdited.isVisible = isEdited
        binding.tvEdited.isVisible = isEdited
    }

    /**
     * Sets active/inactive like icon based on whether the comment is liked or not.
     *
     * @param isLiked - whether the comment is liked or not.
     */
    fun setCommentLikesIcon(isLiked: Boolean = false) {
        val iconStyle =
            LMFeedStyleTransformer.postDetailFragmentViewStyle.commentViewStyle.likeIconStyle

        val likeIcon = if (isLiked) {
            iconStyle.activeSrc
        } else {
            iconStyle.inActiveSrc
        }

        if (likeIcon != null) {
            binding.ivLike.setImageDrawable(ContextCompat.getDrawable(context, likeIcon))
        }
    }

    /**
     * Sets likes count text to the likes text view.
     *
     * @param likesCount - string to be set for likes count.
     */
    fun setLikesCount(likesCount: String) {
        binding.tvLikesCount.apply {
            if (likesCount.isEmpty()) {
                hide()
            } else {
                show()
                text = likesCount
            }
        }
    }

    /**
     * Sets the reply text for the reply text view
     *
     * @param replyText - string to be set for reply text.
     */
    fun setReplyText(replyText: String) {
        binding.tvReply.apply {
            if (replyText.isEmpty()) {
                hide()
            } else {
                show()
                text = replyText
            }
        }
    }

    /**
     * Sets replies count text to the reply count text view.
     *
     * @param repliesCount - string to be set for replies count.
     */
    fun setRepliesCount(repliesCount: String) {
        binding.apply {
            if (repliesCount.isEmpty()) {
                groupReplies.hide()
            } else {
                groupReplies.show()
                tvReplyCount.text = repliesCount
            }
        }
    }

    /**
     * Sets click listener on the like icon
     *
     * @param listener [LMFeedOnClickListener] interface to have click listener
     */
    fun setLikesCountClickListener(listener: LMFeedOnClickListener) {
        binding.tvLikesCount.setOnClickListener {
            listener.onClick()
        }
    }

    /**
     * Sets click listener on the like icon
     *
     * @param listener [LMFeedOnClickListener] interface to have click listener
     */
    fun setLikeIconClickListener(listener: LMFeedOnClickListener) {
        binding.ivLike.setOnClickListener { view ->
            // bounce animation for like button
            LMFeedViewUtils.showBounceAnim(view.context, view)
            listener.onClick()
        }
    }

    /**
     * Sets click listener on the reply text
     *
     * @param listener [LMFeedOnClickListener] interface to have click listener
     */
    fun setReplyClickListener(listener: LMFeedOnClickListener) {
        binding.tvReply.setOnClickListener {
            listener.onClick()
        }
    }

    /**
     * Sets click listener on the reply count text
     *
     * @param listener [LMFeedOnClickListener] interface to have click listener
     */
    fun setReplyCountClickListener(listener: LMFeedOnClickListener) {
        binding.tvReplyCount.setOnClickListener {
            listener.onClick()
        }
    }

    /**
     * Sets click listener on the menu icon
     *
     * @param listener [LMFeedOnClickListener] interface to have click listener
     */
    fun setMenuIconClickListener(listener: LMFeedOnClickListener) {
        binding.ivCommentMenu.setOnClickListener {
            listener.onClick()
        }
    }

    /**
     * Sets click listener on the commenter header
     *
     * @param listener [LMFeedOnClickListener] interface to have click listener
     */
    fun setCommenterHeaderClickListener(listener: LMFeedOnClickListener) {
        binding.ivCommenterImage.setOnClickListener {
            listener.onClick()
        }

        binding.tvCommenterName.setOnClickListener {
            listener.onClick()
        }
    }

    /**
     * Linkify the comment content and returns a link click listener along with the clicked link url
     *
     * @param linkClickListener [LMFeedOnLinkClickListener] interface to have a  link click listener
     */
    fun linkifyCommentContent(linkClickListener: LMFeedOnLinkClickListener) {
        binding.apply {
            val linkifyLinks =
                (Linkify.WEB_URLS or Linkify.EMAIL_ADDRESSES or Linkify.PHONE_NUMBERS)

            LinkifyCompat.addLinks(tvCommentContent, linkifyLinks)
            tvCommentContent.movementMethod = LMFeedLinkMovementMethod { url ->
                tvCommentContent.setOnClickListener {
                    return@setOnClickListener
                }

                linkClickListener.onLinkClicked((url))
            }
        }
    }
}