package com.likeminds.feed.android.core.ui.widgets.post.posttopresponse.view

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
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.databinding.LmFeedPostTopResponseViewBinding
import com.likeminds.feed.android.core.ui.base.styles.*
import com.likeminds.feed.android.core.ui.theme.LMFeedAppearance
import com.likeminds.feed.android.core.ui.widgets.post.posttopresponse.style.LMFeedPostTopResponseViewStyle
import com.likeminds.feed.android.core.utils.*
import com.likeminds.feed.android.core.utils.LMFeedValueUtils.getValidTextForLinkify
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.hide
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.show
import com.likeminds.feed.android.core.utils.link.LMFeedLinkMovementMethod
import com.likeminds.feed.android.core.utils.listeners.LMFeedOnClickListener
import com.likeminds.feed.android.core.utils.listeners.LMFeedOnTaggedMemberClickListener
import com.likeminds.feed.android.core.utils.user.LMFeedUserImageUtil
import com.likeminds.feed.android.core.utils.user.LMFeedUserViewData
import com.likeminds.usertagging.util.UserTaggingDecoder

class LMFeedPostTopResponseView : ConstraintLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    private val inflater =
        (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)

    private val binding = LmFeedPostTopResponseViewBinding.inflate(inflater, this, true)

    //sets provide [postTopResponseViewStyle] to the post top response view
    fun setStyle(postTopResponseViewStyle: LMFeedPostTopResponseViewStyle) {
        postTopResponseViewStyle.apply {
            //set background color of the card view
            backgroundColor?.let {
                binding.cvTopResponse.setCardBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        backgroundColor
                    )
                )
            }

            //configure each view in post top response view as per their styles
            configureTitle(titleTextStyle)
            configureAuthorImage(authorImageViewStyle)
            configureAuthorName(authorNameTextStyle)
            configureTimestamp(timestampTextStyle)
            configureContent(contentTextStyle)
        }
    }

    private fun configureTitle(titleTextStyle: LMFeedTextStyle) {
        binding.tvTopResponseTitle.setStyle(titleTextStyle)
    }

    private fun configureAuthorImage(authorImageViewStyle: LMFeedImageStyle?) {
        binding.ivTopResponseAuthorImage.apply {
            if (authorImageViewStyle == null) {
                hide()
            } else {
                show()
                setStyle(authorImageViewStyle)
            }
        }
    }

    private fun configureAuthorName(authorNameTextStyle: LMFeedTextStyle?) {
        binding.tvTopResponseAuthorName.apply {
            if (authorNameTextStyle == null) {
                hide()
            } else {
                show()
                setStyle(authorNameTextStyle)
            }
        }
    }

    private fun configureTimestamp(timestampTextStyle: LMFeedTextStyle?) {
        binding.tvTopResponseTime.apply {
            if (timestampTextStyle == null) {
                hide()
            } else {
                show()
                setStyle(timestampTextStyle)
            }
        }
    }

    private fun configureContent(contentTextStyle: LMFeedTextStyle) {
        binding.tvTopResponseContent.setStyle(contentTextStyle)
    }

    /**
     * Sets title of the top response view
     *
     * @param title - string to be set as the title
     */
    fun setTopResponseTitle(title: String) {
        binding.tvTopResponseTitle.text = title
    }

    /**
     * Sets author image view.
     *
     * @param user - data of the author.
     */
    fun setAuthorImage(user: LMFeedUserViewData) {
        var authorImageViewStyle =
            LMFeedStyleTransformer.postViewStyle.postTopResponseViewStyle?.authorImageViewStyle
                ?: return

        if (authorImageViewStyle.placeholderSrc == null) {
            authorImageViewStyle = authorImageViewStyle.toBuilder().placeholderSrc(
                LMFeedUserImageUtil.getNameDrawable(
                    user.sdkClientInfoViewData.uuid,
                    user.name,
                    authorImageViewStyle.isCircle,
                ).first
            ).build()
        }
        binding.ivTopResponseAuthorImage.setImage(user.imageUrl, authorImageViewStyle)
    }

    /**
     * Sets the name of the top response author
     *
     * @param authorName - string to be set for author name.
     */
    fun setAuthorName(authorName: String) {
        binding.tvTopResponseAuthorName.text = authorName
    }

    /**
     * Sets the content of the top response
     *
     * @param topResponseText - string to be set for comment text.
     * @param alreadySeenFullContent - whether the comment content was seen completely or not.
     * @param onTopResponseSeeMoreClickListener [LMFeedOnClickListener] - interface to have click listener
     */
    fun setTopResponseContent(
        topResponseText: String,
        alreadySeenFullContent: Boolean?,
        onTopResponseContentClicked: LMFeedOnClickListener,
        onTopResponseSeeMoreClickListener: LMFeedOnClickListener,
        onMemberTagClickListener: LMFeedOnTaggedMemberClickListener
    ) {
        binding.tvTopResponseContent.apply {

            /**
             * Text is modified as Linkify doesn't accept texts with these specific unicode characters
             * @see #Linkify.containsUnsupportedCharacters(String)
             */
            val textForLinkify = topResponseText.getValidTextForLinkify()

            var alreadySeen = alreadySeenFullContent == true

            if (textForLinkify.isEmpty()) {
                hide()
                return
            } else {
                show()
            }

            // post is used here to get lines count in the text view
            post {
                setOnClickListener {
                    onTopResponseContentClicked.onClick()
                }

                // decodes tags in text and creates span around those tags
                UserTaggingDecoder.decodeRegexIntoSpannableText(
                    this,
                    textForLinkify.trim(),
                    enableClick = true,
                    highlightColor = ContextCompat.getColor(
                        context,
                        LMFeedAppearance.getTextLinkColor()
                    ),
                    hasAtRateSymbol = true
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

                //creating see more spannable text for expanding top response content.
                val seeMoreSpannableStringBuilder = SpannableStringBuilder()

                if (!alreadySeen && !shortText.isNullOrEmpty()) {
                    val expandSpannable = SpannableStringBuilder("... See More")
                    expandSpannable.setSpan(
                        ForegroundColorSpan(
                            ContextCompat.getColor(
                                context,
                                R.color.lm_feed_brown_grey
                            )
                        ),
                        0,
                        expandSpannable.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    //handling click of spannable text
                    val seeMoreClickableSpan = object : ClickableSpan() {
                        override fun onClick(view: View) {
                            setOnClickListener {
                                return@setOnClickListener
                            }
                            alreadySeen = true
                            onTopResponseSeeMoreClickListener.onClick()
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

                // appends see more text at last
                text = TextUtils.concat(
                    trimmedText,
                    seeMoreSpannableStringBuilder
                )

                //handling click of web links or email addresses or phone numbers in post content
                val linkifyLinks =
                    (Linkify.WEB_URLS or Linkify.EMAIL_ADDRESSES or Linkify.PHONE_NUMBERS)
                LinkifyCompat.addLinks(this, linkifyLinks)
                movementMethod = LMFeedLinkMovementMethod {
                    setOnClickListener {
                        return@setOnClickListener
                    }

                    true
                }
            }
        }
    }

    /**
     * Sets the time the top response was created.
     *
     * @param createdAtTimeStamp - timestamp when the top response was created.
     */
    fun setTimestamp(createdAtTimeStamp: Long) {
        val context = binding.root.context
        binding.tvTopResponseTime.text =
            LMFeedTimeUtil.getRelativeTime(context, createdAtTimeStamp)
    }

    /**
     * Sets click listener on the author frame
     *
     * @param listener [LMFeedOnClickListener] interface to have click listener
     */
    fun setAuthorFrameClickListener(listener: LMFeedOnClickListener) {

        binding.apply {
            tvTopResponseAuthorName.setOnClickListener {
                listener.onClick()
            }

            ivTopResponseAuthorImage.setOnClickListener {
                listener.onClick()
            }
        }
    }

    /**
     * Sets click listener on the top response content
     *
     * @param listener [LMFeedOnClickListener] interface to have click listener
     */
    fun setTopResponseClickListener(listener: LMFeedOnClickListener) {
        binding.layoutTopResponse.setOnClickListener {
            listener.onClick()
        }
    }
}