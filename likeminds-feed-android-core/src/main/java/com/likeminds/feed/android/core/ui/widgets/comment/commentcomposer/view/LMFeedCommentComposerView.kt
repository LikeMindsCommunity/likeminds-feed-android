package com.likeminds.feed.android.core.ui.widgets.comment.commentcomposer.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.databinding.LmFeedCommentComposerViewBinding
import com.likeminds.feed.android.core.ui.base.styles.*
import com.likeminds.feed.android.core.ui.base.views.LMFeedEditText
import com.likeminds.feed.android.core.ui.widgets.comment.commentcomposer.style.LMFeedCommentComposerViewStyle
import com.likeminds.feed.android.core.utils.LMFeedCommunityUtil
import com.likeminds.feed.android.core.utils.LMFeedStyleTransformer
import com.likeminds.feed.android.core.utils.LMFeedValueUtils.pluralizeOrCapitalize
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.hide
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.show
import com.likeminds.feed.android.core.utils.listeners.LMFeedOnClickListener
import com.likeminds.feed.android.core.utils.pluralize.model.LMFeedWordAction

class LMFeedCommentComposerView : ConstraintLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    private val inflater =
        (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)

    private val binding: LmFeedCommentComposerViewBinding =
        LmFeedCommentComposerViewBinding.inflate(inflater, this, true)

    val etComment: LMFeedEditText
        get() = binding.etComment

    //sets provided [LMFeedCommentComposerViewStyle] to the comment composer view
    fun setStyle(commentComposerStyle: LMFeedCommentComposerViewStyle) {

        commentComposerStyle.apply {
            //sets elevation to the comment composer view
            elevation?.let {
                this@LMFeedCommentComposerView.elevation = resources.getDimension(it)
            }

            //sets background color of the comment composer view
            backgroundColor?.let {
                this@LMFeedCommentComposerView.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        it
                    )
                )
            }

            //configures each view inside comment composer view
            configureCommentInput(commentInputStyle)
            configureCommentSend(commentSendStyle)
            configureCommentRestricted(commentRestrictedStyle)
            configureReplyingTo(replyingToStyle)
            configureRemoveReplyingTo(removeReplyingToStyle)
        }
    }

    private fun configureCommentInput(commentInputStyle: LMFeedEditTextStyle) {
        binding.etComment.setStyle(commentInputStyle)
    }

    private fun configureCommentSend(commentSendStyle: LMFeedIconStyle) {
        binding.ivCommentSend.setStyle(commentSendStyle)
    }

    private fun configureCommentRestricted(commentRestrictedStyle: LMFeedTextStyle?) {
        binding.tvRestricted.apply {
            if (commentRestrictedStyle == null) {
                hide()
            } else {
                setStyle(commentRestrictedStyle)
            }
        }
    }

    private fun configureReplyingTo(replyingToStyle: LMFeedTextStyle?) {
        binding.tvReplyingTo.apply {
            if (replyingToStyle == null) {
                hide()
            } else {
                setStyle(replyingToStyle)
            }
        }
    }

    private fun configureRemoveReplyingTo(removeReplyingToStyle: LMFeedIconStyle?) {
        binding.ivRemoveReplyingTo.apply {
            if (removeReplyingToStyle == null) {
                hide()
            } else {
                setStyle(removeReplyingToStyle)
            }
        }
    }

    /**
     * Sets hint text to the comment input bon
     *
     * @param hint - hint text to be set as hint
     */
    fun setCommentInputBoxHint(hint: String) {
        binding.etComment.hint = hint
    }

    /**
     * Sets active/inactive comment send icon.
     *
     * @param isEnabled - whether the comment send is enabled or not.
     */
    fun setCommentSendButton(isEnabled: Boolean = false) {
        val commentComposerStyle =
            LMFeedStyleTransformer.postDetailFragmentViewStyle.commentComposerStyle
        val iconStyle = commentComposerStyle.commentSendStyle

        binding.ivCommentSend.apply {
            this.isEnabled = isEnabled

            val commentSendIcon = if (isEnabled) {
                iconStyle.activeSrc
            } else {
                iconStyle.inActiveSrc
            }

            if (commentSendIcon != null) {
                setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        commentSendIcon
                    )
                )
            }
        }
    }

    /**
     * Sets click listener on the comment send icon
     *
     * @param listener [LMFeedOnClickListener] interface to have click listener
     */
    fun setCommentSendClickListener(listener: LMFeedOnClickListener) {
        binding.ivCommentSend.setOnClickListener {
            listener.onClick()
        }
    }

    /**
     * Sets click listener on the remove reply view
     *
     * @param listener [LMFeedOnClickListener] interface to have click listener
     */
    fun setRemoveReplyingToClickListener(listener: LMFeedOnClickListener) {
        binding.ivRemoveReplyingTo.setOnClickListener {
            listener.onClick()
        }
    }

    /**
     * Shows the replying if user is replying to the comment
     *
     * @param replyingTo - name of the user to which current user is replying, if this is empty then we hide replyingTo view
     */
    fun setReplyingView(replyingTo: String) {
        binding.apply {
            if (replyingTo.isEmpty()) {
                tvReplyingTo.hide()
                ivRemoveReplyingTo.hide()
            } else {
                tvReplyingTo.show()
                ivRemoveReplyingTo.show()

                tvReplyingTo.text = replyingTo
            }
        }
    }

    /**
     * Restricts the user to comment if they don't have the rights
     *
     * @param hasCommentRights - whether the user has the rights to comment or not
     */
    fun setCommentRights(hasCommentRights: Boolean) {
        binding.apply {
            if (hasCommentRights) {
                etComment.show()
                ivCommentSend.show()
                tvRestricted.hide()
            } else {
                etComment.hide()
                ivCommentSend.hide()
                tvRestricted.show()
                tvRestricted.text =
                    context.getString(
                        R.string.lm_feed_you_do_not_have_permission_to_s_comment,
                        LMFeedCommunityUtil.getCommentVariable()
                            .pluralizeOrCapitalize(LMFeedWordAction.ALL_SMALL_SINGULAR)
                    )
            }
        }
    }
}