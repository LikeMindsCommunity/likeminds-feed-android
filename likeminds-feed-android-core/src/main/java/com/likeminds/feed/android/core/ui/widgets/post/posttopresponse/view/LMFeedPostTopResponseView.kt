package com.likeminds.feed.android.core.ui.widgets.post.posttopresponse.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.likeminds.feed.android.core.databinding.LmFeedPostTopResponseViewBinding
import com.likeminds.feed.android.core.ui.base.styles.*
import com.likeminds.feed.android.core.ui.widgets.post.posttopresponse.style.LMFeedPostTopResponseViewStyle
import com.likeminds.feed.android.core.utils.LMFeedStyleTransformer
import com.likeminds.feed.android.core.utils.LMFeedTimeUtil
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.hide
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.show
import com.likeminds.feed.android.core.utils.listeners.LMFeedOnClickListener
import com.likeminds.feed.android.core.utils.user.LMFeedUserImageUtil
import com.likeminds.feed.android.core.utils.user.LMFeedUserViewData

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
                binding.cvTopResponse.setBackgroundColor(
                    ContextCompat.getColor(context, backgroundColor)
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
        binding.cvTopResponse.setOnClickListener {
            listener.onClick()
        }
    }
}