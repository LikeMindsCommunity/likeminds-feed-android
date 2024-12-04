package com.likeminds.feed.android.core.ui.widgets.post.postactionview.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.likeminds.feed.android.core.LMFeedCoreApplication
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.databinding.LmFeedQnaActionHorizontalViewBinding
import com.likeminds.feed.android.core.ui.base.styles.*
import com.likeminds.feed.android.core.ui.widgets.post.postactionview.style.LMFeedPostActionViewStyle
import com.likeminds.feed.android.core.utils.LMFeedStyleTransformer
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.hide
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.show
import com.likeminds.feed.android.core.utils.listeners.LMFeedOnClickListener
import com.likeminds.feed.android.core.utils.user.LMFeedUserMetaData

class LMFeedPostQnAActionHorizontalView : ConstraintLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet?, defStyle: Int) : super(
        context,
        attributeSet,
        defStyle
    )

    private val inflater =
        (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)

    private val binding = LmFeedQnaActionHorizontalViewBinding.inflate(inflater, this, true)

    //sets provided [postActionViewStyle] to the post action view
    fun setStyle(postActionViewStyle: LMFeedPostActionViewStyle) {
        postActionViewStyle.apply {
            //set background color
            backgroundColor?.let {
                setBackgroundColor(ContextCompat.getColor(context, backgroundColor))
            }

            //configures each view in the post action view with the provided style
            configureUpvoteText(likeTextStyle)
            configureUpvoteIcon(likeIconStyle)
            configureCommentsText(commentTextStyle)
            configureSaveIcon(saveIconStyle)
            configureShareIcon(shareIconStyle)
        }
    }

    private fun configureUpvoteText(upvoteTextStyle: LMFeedTextStyle?) {
        binding.apply {
            if (upvoteTextStyle == null) {
                tvUpvote.hide()
                viewDotUpvotesCount.hide()
                tvUpvotesCount.hide()
            } else {
                tvUpvote.show()
                tvUpvote.setStyle(upvoteTextStyle)
                tvUpvotesCount.setStyle(upvoteTextStyle)
            }
        }
    }

    private fun configureUpvoteIcon(upvoteIconStyle: LMFeedIconStyle) {
        binding.ivUpvote.setStyle(upvoteIconStyle)
    }

    private fun configureCommentsText(commentTextStyle: LMFeedTextStyle?) {
        binding.tvCommentsCount.apply {
            if (commentTextStyle == null) {
                hide()
            } else {
                setStyle(commentTextStyle)
            }
        }
    }

    private fun configureSaveIcon(saveIconStyle: LMFeedIconStyle?) {
        if (saveIconStyle != null) {
            binding.ivSave.setStyle(saveIconStyle)
        }
    }

    private fun configureShareIcon(shareIconStyle: LMFeedIconStyle?) {
        // if the client has not set the domain then hide the share icon and log a warning
        val userMeta = LMFeedUserMetaData.getInstance()
        binding.ivShare.apply {
            if (userMeta.domain == null) {
                hide()

                Log.w(
                    LMFeedCoreApplication.LOG_TAG,
                    context.getString(R.string.lm_feed_please_set_domain)
                )
                return
            }

            if (shareIconStyle != null) {
                setStyle(shareIconStyle)
            }
        }
    }

    /**
     * Sets upvote text to the upvote text view.
     *
     * @param upvoteText - string to be set for upvote.
     */
    fun setUpvoteText(upvoteText: String) {
        binding.tvUpvote.text = upvoteText
    }

    /**
     * Sets upvote count text to the upvote count text view.
     *
     * @param upvoteCount - string to be set for upvote count.
     */
    fun setUpvoteCount(upvoteCount: String) {
        binding.apply {
            if (upvoteCount.isEmpty()) {
                tvUpvotesCount.hide()
                viewDotUpvotesCount.hide()
            } else {
                tvUpvotesCount.show()
                viewDotUpvotesCount.show()
                tvUpvotesCount.text = upvoteCount
            }
        }
    }

    /**
     * Sets comments count text to the comments text view.
     *
     * @param commentsCount - string to be set for comments count.
     */
    fun setCommentsCount(commentsCount: String) {
        binding.tvCommentsCount.text = commentsCount
    }

    /**
     * Sets active/inactive upvote icon.
     *
     * @param isUpvoted - whether the post is upvoted or not.
     */
    fun setUpvoteIcon(isUpvoted: Boolean = false) {
        val iconStyle = LMFeedStyleTransformer.postViewStyle.postActionViewStyle.likeIconStyle

        val upvoteIcon = if (isUpvoted) {
            iconStyle.activeSrc
        } else {
            iconStyle.inActiveSrc
        }

        if (upvoteIcon != null) {
            binding.ivUpvote.setImageDrawable(ContextCompat.getDrawable(context, upvoteIcon))
        }
    }

    /**
     * Sets active/inactive save icon.
     *
     * @param isSaved - whether the post is saved or not.
     */
    fun setSaveIcon(isSaved: Boolean = false) {
        binding.ivSave.apply {
            val iconStyle = LMFeedStyleTransformer.postViewStyle.postActionViewStyle.saveIconStyle

            if (iconStyle == null) {
                hide()
                return
            }

            val saveIcon = if (isSaved) {
                iconStyle.activeSrc
            } else {
                iconStyle.inActiveSrc
            }

            if (saveIcon != null) {
                setImageDrawable(ContextCompat.getDrawable(context, saveIcon))
            }
        }
    }

    /**
     * Sets click listener on the upvote icon
     *
     * @param listener [LMFeedOnClickListener] interface to have click listener
     */
    fun setUpvoteIconClickListener(listener: LMFeedOnClickListener) {
        binding.ivUpvote.setOnClickListener {
            listener.onClick()
        }

        binding.tvUpvote.setOnClickListener {
            listener.onClick()
        }
    }

    /**
     * Sets click listener on the upvote icon
     *
     * @param listener [LMFeedOnClickListener] interface to have click listener
     */
    fun setUpvoteCountClickListener(listener: LMFeedOnClickListener) {
        binding.tvUpvotesCount.setOnClickListener {
            listener.onClick()
        }
    }

    /**
     * Sets click listener on the like icon
     *
     * @param listener [LMFeedOnClickListener] interface to have click listener
     */
    fun setCommentsCountClickListener(listener: LMFeedOnClickListener) {
        binding.tvCommentsCount.setOnClickListener {
            listener.onClick()
        }
    }

    /**
     * Sets click listener on the save icon
     *
     * @param listener [LMFeedOnClickListener] interface to have click listener
     */
    fun setSaveIconListener(listener: LMFeedOnClickListener) {
        binding.ivSave.setOnClickListener {
            listener.onClick()
        }
    }

    /**
     * Sets click listener on the share icon
     *
     * @param listener [LMFeedOnClickListener] interface to have click listener
     */
    fun setShareIconListener(listener: LMFeedOnClickListener) {
        binding.ivShare.setOnClickListener {
            listener.onClick()
        }
    }
}