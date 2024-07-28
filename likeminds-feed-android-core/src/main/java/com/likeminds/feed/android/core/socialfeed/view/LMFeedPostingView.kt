package com.likeminds.feed.android.core.socialfeed.view

import android.content.Context
import android.content.res.ColorStateList
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.likeminds.feed.android.core.databinding.LmFeedLayoutPostingBinding
import com.likeminds.feed.android.core.ui.base.styles.*
import com.likeminds.feed.android.core.socialfeed.style.LMFeedPostingViewStyle
import com.likeminds.feed.android.core.utils.LMFeedImageBindingUtil
import com.likeminds.feed.android.core.utils.LMFeedStyleTransformer
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.hide
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.show
import com.likeminds.feed.android.core.utils.listeners.LMFeedOnClickListener

class LMFeedPostingView : ConstraintLayout {

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

    private val binding = LmFeedLayoutPostingBinding.inflate(inflater, this, true)

    /**
     * Sets title text in the posting layout view.
     *
     * @param title Text for the title in the view.
     */
    fun setPostingText(title: String) {
        binding.tvPosting.text = title
    }

    /**
     * Sets progress to progressbar of posting layout view.
     *
     * @param progress to be mapped in progress bar
     */
    fun setProgress(progress: Int) {
        binding.postingProgress.progress = progress
    }


    /**
     * Sets visibility of progress bar in the posting layout view.
     *
     * @param isVisible to show/hide progress bar
     */
    fun setProgressVisibility(isVisible: Boolean) {
        binding.postingProgress.isVisible = isVisible
    }

    /**
     * Sets retry cta text in the posting layout view.
     *
     * @param retryCTAText Text for the title in the view.
     */
    fun setRetryCTAText(retryCTAText: String) {
        binding.tvRetry.text = retryCTAText
    }

    /**
     * Sets visibility of retry cta text in the posting layout view.
     *
     * @param isVisible to show/hide retry cta
     */
    fun setRetryVisibility(isVisible: Boolean) {
        binding.tvRetry.isVisible = isVisible
    }

    /**
     * Sets visibility of post successful in the posting layout view.
     *
     * @param isVisible to show/hide post successful
     */
    fun setPostSuccessfulVisibility(isVisible: Boolean) {
        binding.ivPosted.isVisible = isVisible
    }

    /**
     * Sets click listener on the retry cta of the posting layout view
     *
     * @param listener [LMFeedOnClickListener] interface to have click listener
     */
    fun setRetryCTAClickListener(listener: LMFeedOnClickListener) {
        binding.tvRetry.setOnClickListener {
            listener.onClick()
        }
    }

    /**
     * Sets thumbnail of attachment in posting layout view.
     *
     * @param uri to show thumbnail of attachment
     */
    fun setAttachmentThumbnail(uri: Uri?) {
        binding.ivPostThumbnail.apply {
            if (uri != null) {
                show()
                val thumbnailStyle =
                    LMFeedStyleTransformer.socialFeedFragmentViewStyle.postingViewStyle.attachmentThumbnailImageStyle
                LMFeedImageBindingUtil.loadImage(
                    view = this,
                    file = uri,
                    placeholder = thumbnailStyle?.placeholderSrc,
                    isCircle = thumbnailStyle?.isCircle ?: false,
                    cornerRadius = thumbnailStyle?.cornerRadius ?: 0,
                    showGreyScale = thumbnailStyle?.showGreyScale ?: false
                )
            } else {
                hide()
            }
        }
    }

    //sets provided [postingViewStyle] to the posting view
    fun setStyle(postingViewStyle: LMFeedPostingViewStyle) {

        postingViewStyle.apply {
            //set background color
            backgroundColor?.let {
                binding.clContainer.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(context, it))
            }

            //configures each view inside the posting view
            configureTitle(postingHeadingTextStyle)
            configureRetryCTA(retryButtonTextStyle)
            configureProgressStyle(progressStyle)
            configurePostingDoneStyle(postingDoneImageStyle)
            configurePostAttachmentView(attachmentThumbnailImageStyle)
        }
    }

    private fun configureTitle(titleStyle: LMFeedTextStyle) {
        binding.tvPosting.setStyle(titleStyle)
    }

    private fun configureRetryCTA(retryCTAStyle: LMFeedTextStyle) {
        binding.tvRetry.setStyle(retryCTAStyle)
    }

    private fun configureProgressStyle(progressStyle: LMFeedProgressBarStyle) {
        binding.postingProgress.apply {
            setStyle(progressStyle)
        }
    }

    private fun configurePostingDoneStyle(postingDoneStyle: LMFeedImageStyle) {
        binding.ivPosted.setStyle(postingDoneStyle)
    }

    private fun configurePostAttachmentView(attachmentViewStyle: LMFeedImageStyle?) {
        binding.ivPostThumbnail.apply {
            if (attachmentViewStyle == null) {
                hide()
            } else {
                show()
                setStyle(attachmentViewStyle)
            }
        }
    }
}