package com.likeminds.feed.android.core.ui.widgets.postheaderview.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.likeminds.feed.android.core.databinding.LmFeedPostHeaderViewBinding
import com.likeminds.feed.android.core.ui.base.styles.*
import com.likeminds.feed.android.core.ui.widgets.postheaderview.style.LMFeedPostHeaderViewStyle
import com.likeminds.feed.android.core.utils.*
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.hide
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.show
import com.likeminds.feed.android.core.utils.listeners.LMFeedOnClickListener

class LMFeedPostHeaderView : ConstraintLayout {

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {
    }

    constructor(context: Context, attributeSet: AttributeSet?, defStyle: Int) : super(
        context,
        attributeSet,
        defStyle
    ) {
    }

    private val inflater =
        (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)

    private val binding = LmFeedPostHeaderViewBinding.inflate(inflater, this, true)

    fun setStyle(postHeaderViewStyle: LMFeedPostHeaderViewStyle) {

        postHeaderViewStyle.apply {
            //set background color
            backgroundColor?.let {
                setBackgroundColor(ContextCompat.getColor(context, backgroundColor))
            }

            configureAuthorImage(authorImageViewStyle)
            configureAuthorName(authorNameViewStyle)
            configureTimestamp(timestampTextStyle)
            configureEditedTag(postEditedTextStyle)
            configureAuthorCustomTitle(authorCustomTitleTextStyle)
            configurePinIcon(pinIconStyle)
            configureMenuIcon(menuIconStyle)
        }
    }

    private fun configureMenuIcon(menuIconStyle: LMFeedIconStyle?) {
        binding.ivPostMenu.apply {
            if (menuIconStyle == null) {
                hide()
            } else {
                show()
                setStyle(menuIconStyle)
            }
        }
    }

    private fun configurePinIcon(pinIconStyle: LMFeedIconStyle?) {
        binding.ivPin.apply {
            if (pinIconStyle == null) {
                hide()
            } else {
                show()
                setStyle(pinIconStyle)
            }
        }
    }

    private fun configureAuthorCustomTitle(authorCustomTitleTextStyle: LMFeedTextStyle?) {
        binding.tvCustomTitle.apply {
            if (authorCustomTitleTextStyle == null) {
                hide()
            } else {
                show()
                setStyle(authorCustomTitleTextStyle)
            }
        }
    }

    private fun configureEditedTag(postEditedTextStyle: LMFeedTextStyle?) {
        binding.apply {
            if (postEditedTextStyle == null) {
                tvEdited.hide()
                viewDotEdited.hide()
            } else {
                viewDotEdited.show()
                tvEdited.setStyle(postEditedTextStyle)
            }
        }
    }

    private fun configureTimestamp(timestampTextStyle: LMFeedTextStyle?) {
        binding.tvTime.apply {
            if (timestampTextStyle == null) {
                hide()
            } else {
                show()
                setStyle(timestampTextStyle)
            }
        }
    }

    private fun configureAuthorName(authorNameViewStyle: LMFeedTextStyle) {
        binding.tvAuthorName.setStyle(authorNameViewStyle)
    }

    private fun configureAuthorImage(authorImageViewStyle: LMFeedImageStyle) {
        binding.ivAuthorImage.setStyle(authorImageViewStyle)
    }

    /**
     * Sets author image view.
     *
     * @param imageSrc - image source to be set as author image.
     */
    fun setAuthorImage(imageSrc: Any) {
        val authorImageViewStyle =
            LMFeedStyleTransformer.postViewStyle.postHeaderViewStyle.authorImageViewStyle

        LMFeedImageBindingUtil.loadImage(
            binding.ivAuthorImage,
            imageSrc,
            authorImageViewStyle.placeholderSrc,
            authorImageViewStyle.isCircle,
            (authorImageViewStyle.cornerRadius ?: 0),
            authorImageViewStyle.showGreyScale,
        )
    }

    /**
     * Sets the name of the post author
     *
     * @param authorName - string to be set for author name.
     */
    fun setAuthorName(authorName: String) {
        binding.tvAuthorName.text = authorName
    }

    /**
     * Sets the time the post was created.
     *
     * @param createdAtTimeStamp - timestamp when the post was created.
     */
    fun setTimestamp(createdAtTimeStamp: Long) {
        binding.tvTime.text = LMFeedTimeUtil.getRelativeTimeInString(createdAtTimeStamp)
    }

    /**
     * Shows the `Edited` text if the post was edited.
     *
     * @param isEdited - whether the post was edited or not.
     */
    fun setPostEdited(isEdited: Boolean) {
        binding.viewDotEdited.isVisible = isEdited
        binding.tvEdited.isVisible = isEdited
    }

    /**
     * Shows the pinned icon if the post is pinned.
     *
     * @param isPinned - whether the post is pinned or not.
     */
    fun setPinIcon(isPinned: Boolean) {
        binding.ivPin.apply {
            if (isPinned) {
                show()
            } else {
                hide()
            }
        }
    }

    /**
     * Sets the custom title of the post author
     *
     * @param customTitle - string to be set for author custom title.
     */
    fun setAuthorCustomTitle(customTitle: String?) {
        binding.tvCustomTitle.apply {
            if (customTitle == null) {
                hide()
            } else {
                show()
                text = customTitle
            }
        }
    }

    /**
     * Sets click listener on the author frame
     *
     * @param listener [LMFeedOnClickListener] interface to have click listener
     */
    fun setAuthorFrameClickListener(listener: LMFeedOnClickListener) {
        binding.viewAuthorFrame.setOnClickListener {
            listener.onClick()
        }
    }

    /**
     * Sets click listener on the menu icon
     *
     * @param listener [LMFeedOnClickListener] interface to have click listener
     */
    fun setMenuIconClickListener(listener: LMFeedOnClickListener) {
        binding.ivPostMenu.setOnClickListener {
            listener.onClick()
        }
    }
}