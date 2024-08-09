package com.likeminds.feed.android.core.ui.widgets.post.postactionview.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.likeminds.customgallery.utils.ViewUtils.show
import com.likeminds.feed.android.core.LMFeedCoreApplication
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.databinding.LmFeedPostActionVerticalViewBinding
import com.likeminds.feed.android.core.ui.base.styles.LMFeedIconStyle
import com.likeminds.feed.android.core.ui.base.styles.LMFeedTextStyle
import com.likeminds.feed.android.core.ui.base.styles.setStyle
import com.likeminds.feed.android.core.ui.widgets.post.postactionview.style.LMFeedPostActionViewStyle
import com.likeminds.feed.android.core.utils.LMFeedStyleTransformer
import com.likeminds.feed.android.core.utils.LMFeedViewUtils
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.hide
import com.likeminds.feed.android.core.utils.listeners.LMFeedOnClickListener
import com.likeminds.feed.android.core.utils.user.LMFeedUserMetaData

class LMFeedPostActionVerticalView : ConstraintLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet?, defStyle: Int) : super(
        context,
        attributeSet,
        defStyle
    )

    private val inflater =
        (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)

    private val binding = LmFeedPostActionVerticalViewBinding.inflate(inflater, this, true)

    //sets provided [postActionViewStyle] to the post action view
    fun setStyle(postActionViewStyle: LMFeedPostActionViewStyle) {

        postActionViewStyle.apply {
            //set background color
            backgroundColor?.let {
                setBackgroundColor(ContextCompat.getColor(context, backgroundColor))
            }

            //configures each view in the post action view with the provided style
            configureLikeText(likeTextStyle)
            configureLikesIcon(likeIconStyle)
            configureCommentsText(commentTextStyle)
            configureShareIcon(shareIconStyle)
            configureMenuIcon(menuIconStyle)
        }
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

    private fun configureLikesIcon(likeIconStyle: LMFeedIconStyle) {
        binding.ivLike.setStyle(likeIconStyle)
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

    private fun configureShareIcon(shareIconStyle: LMFeedIconStyle?) {
        // if the client has not set the domain then hide the share icon and log a warning
        val userMeta = LMFeedUserMetaData.getInstance()
        if (userMeta.domain == null) {
            binding.ivShare.hide()

            Log.w(
                LMFeedCoreApplication.LOG_TAG,
                context.getString(R.string.lm_feed_please_set_domain)
            )
            return
        }

        if (shareIconStyle != null) {
            binding.ivShare.setStyle(shareIconStyle)
            show()
        }
    }

    private fun configureMenuIcon(menuIconStyle: LMFeedIconStyle?) {
        binding.ivPostMenu.apply {
            if (menuIconStyle == null) {
                hide()
            } else {
                setStyle(menuIconStyle)
                show()
            }
        }
    }

    /**
     * Sets likes count text to the likes text view.
     *
     * @param likesCount - string to be set for likes count.
     */
    fun setLikesCount(likesCount: String) {
        binding.tvLikesCount.text = likesCount
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
     * Sets active/inactive like icon.
     *
     * @param isLiked - whether the post is liked or not.
     */
    fun setLikesIcon(isLiked: Boolean = false) {
        val iconStyle = LMFeedStyleTransformer.postViewStyle.postActionViewStyle.likeIconStyle

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
    fun setCommentsCountClickListener(listener: LMFeedOnClickListener) {
        binding.tvCommentsCount.setOnClickListener {
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