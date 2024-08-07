package com.likeminds.feed.android.core.ui.widgets.acitivityfeed.view

import android.content.Context
import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.databinding.LmFeedActivityViewBinding
import com.likeminds.feed.android.core.post.model.*
import com.likeminds.feed.android.core.ui.base.styles.*
import com.likeminds.feed.android.core.ui.widgets.acitivityfeed.style.LMFeedActivityViewStyle
import com.likeminds.feed.android.core.utils.LMFeedStyleTransformer
import com.likeminds.feed.android.core.utils.LMFeedTimeUtil
import com.likeminds.feed.android.core.utils.LMFeedValueUtils.getValidTextForLinkify
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.hide
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.show
import com.likeminds.feed.android.core.utils.user.LMFeedUserImageUtil
import com.likeminds.feed.android.core.utils.user.LMFeedUserViewData
import com.likeminds.usertagging.util.UserTaggingDecoder

class LMFeedActivityView : ConstraintLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    companion object {
        private const val MAX_LINES = 3
    }

    private val inflater =
        (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)

    private val binding: LmFeedActivityViewBinding =
        LmFeedActivityViewBinding.inflate(inflater, this, true)

    //sets provided [LMFeedActivityViewStyle] to the activity view
    fun setStyle(activityViewStyle: LMFeedActivityViewStyle) {

        activityViewStyle.apply {
            //sets background color
            unreadActivityBackgroundColor?.let {
                setBackgroundColor(ContextCompat.getColor(context, unreadActivityBackgroundColor))
            }

            //configure activity view elements
            configureUserImage(userImageViewStyle)
            configureActivityText(activityTextStyle)
            configurePostTypeBadge(postTypeBadgeStyle)
            configureTimestampText(timestampTextStyle)
        }
    }

    private fun configureUserImage(userImageViewStyle: LMFeedImageStyle) {
        binding.ivUserImage.setStyle(userImageViewStyle)
    }

    private fun configureActivityText(activityTextStyle: LMFeedTextStyle) {
        binding.tvActivityContent.setStyle(activityTextStyle)
    }

    private fun configurePostTypeBadge(postTypeBadgeStyle: LMFeedImageStyle?) {
        binding.ivPostType.apply {
            if (postTypeBadgeStyle == null) {
                hide()
            } else {
                setStyle(postTypeBadgeStyle)
                show()
            }
        }
    }

    private fun configureTimestampText(timestampTextStyle: LMFeedTextStyle?) {
        binding.tvActivityDate.apply {
            if (timestampTextStyle == null) {
                hide()
            } else {
                setStyle(timestampTextStyle)
                show()
            }
        }
    }

    /**
     * Sets activity content in the activity context text view
     *
     * @param activityContent Text for the activity content.
     */
    fun setActivityContent(activityContent: String) {
        binding.tvActivityContent.apply {
            /**
             * Text is modified as Linkify doesn't accept texts with these specific unicode characters
             * @see #Linkify.containsUnsupportedCharacters(String)
             */
            val textForLinkify = activityContent.getValidTextForLinkify()

            // post is used here to get lines count in the text view
            post {
                UserTaggingDecoder.decodeRegexIntoSpannableText(
                    this,
                    textForLinkify.trim(),
                    enableClick = false,
                    highlightColor = Color.BLACK,
                    hasAtRateSymbol = false,
                    isBold = true
                )

                // get the short text as per max lines
                var shortText: String? = null
                if (lineCount >= MAX_LINES) {
                    val lineEndIndex: Int = layout.getLineEnd(MAX_LINES - 1)
                    shortText = text.subSequence(0, lineEndIndex).toString().trim()
                }

                val trimmedText =
                    editableText.subSequence(0, (shortText?.length) ?: editableText.length)

                val ellipsizeSpanBuilder = SpannableStringBuilder()
                if (!shortText.isNullOrEmpty()) {
                    ellipsizeSpanBuilder.append("...")
                }

                text = TextUtils.concat(
                    trimmedText,
                    ellipsizeSpanBuilder
                )
            }
        }
    }

    /**
     * Sets the time the post was created.
     *
     * @param createdAtTimeStamp - timestamp when the post was created.
     */
    fun setTimestamp(createdAtTimeStamp: Long) {
        binding.tvActivityDate.apply {
            text = LMFeedTimeUtil.getRelativeTime(context, createdAtTimeStamp)
        }
    }

    /**
     * Sets the activity as read or unread
     *
     * @param isRead - whether the activity is read or not
     */
    fun setActivityRead(isRead: Boolean) {
        binding.root.apply {
            val activityViewStyle =
                LMFeedStyleTransformer.activityFeedFragmentViewStyle.activityViewStyle
            if (isRead) {
                activityViewStyle.readActivityBackgroundColor?.let { readActivityBackgroundColor ->
                    setBackgroundColor(ContextCompat.getColor(context, readActivityBackgroundColor))
                }
            } else {
                activityViewStyle.unreadActivityBackgroundColor?.let { unreadActivityBackgroundColor ->
                    setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            unreadActivityBackgroundColor
                        )
                    )
                }
            }
        }
    }

    /**
     * Sets the user image view.
     *
     * @param user - data of the author.
     */
    fun setUserImage(user: LMFeedUserViewData) {
        var userImageViewStyle =
            LMFeedStyleTransformer.activityFeedFragmentViewStyle.activityViewStyle.userImageViewStyle

        if (userImageViewStyle.placeholderSrc == null) {
            userImageViewStyle = userImageViewStyle.toBuilder().placeholderSrc(
                LMFeedUserImageUtil.getNameDrawable(
                    user.sdkClientInfoViewData.uuid,
                    user.name,
                    userImageViewStyle.isCircle,
                ).first
            ).build()
        }
        binding.ivUserImage.setImage(user.imageUrl, userImageViewStyle)
    }

    /**
     * Sets the post type badge for the activity as per the attachment type in the activity's post entity
     *
     * @param attachmentType - attachment type in the activity's post entity
     */
    fun setPostTypeBadge(@LMFeedAttachmentType attachmentType: Int?) {
        binding.apply {
            val badgeStyle =
                LMFeedStyleTransformer.activityFeedFragmentViewStyle.activityViewStyle.postTypeBadgeStyle
            if (badgeStyle == null) {
                cvPostType.hide()
                return@apply
            } else {
                when (attachmentType) {
                    IMAGE -> {
                        cvPostType.show()
                        ivPostType.setImage(
                            R.drawable.lm_feed_ic_media_attachment,
                            badgeStyle
                        )
                    }

                    VIDEO -> {
                        cvPostType.show()
                        ivPostType.setImage(
                            R.drawable.lm_feed_ic_media_attachment,
                            badgeStyle
                        )
                    }

                    DOCUMENT -> {
                        cvPostType.show()
                        ivPostType.setImage(
                            R.drawable.lm_feed_ic_doc_attachment,
                            badgeStyle
                        )
                    }

                    else -> {
                        cvPostType.hide()
                    }
                }
            }
        }
    }
}