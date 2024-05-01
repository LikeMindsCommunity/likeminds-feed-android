package com.likeminds.feed.android.core.ui.widgets.poll.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.likeminds.feed.android.core.databinding.LmFeedPostPollViewBinding
import com.likeminds.feed.android.core.ui.base.styles.*
import com.likeminds.feed.android.core.ui.widgets.poll.style.LMFeedPostPollViewStyle
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.hide
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.show
import com.likeminds.feed.android.core.utils.listeners.LMFeedOnClickListener

class LMFeedPostPollView : ConstraintLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet?, defStyle: Int) : super(
        context,
        attributeSet,
        defStyle
    )

    private val inflater =
        (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)

    private val binding = LmFeedPostPollViewBinding.inflate(inflater, this, true)

    //sets provided [postPollViewStyle] to the post poll view
    fun setStyle(postPollViewStyle: LMFeedPostPollViewStyle) {
        postPollViewStyle.apply {

            //configures each view in the post poll view with the provided style
            configurePollTitleText(pollTitleTextStyle)
            configurePollInfoText(pollInfoTextStyle)
            configureMemberVotedCountText(membersVotedCountTextStyle)
            configureSubmitPollVoteButton(submitPollVoteButtonStyle)
            configurePollExpiryText(pollExpiryTextStyle)
            configureAddPollOptionButton(addPollOptionButtonStyle)
            configureEditPollVoteText(editPollVoteTextStyle)
            configureClearPollIcon(clearPollIconStyle)
            configureEditPollIcon(editPollIconStyle)
        }
    }

    private fun configurePollTitleText(pollTitleTextStyle: LMFeedTextStyle) {
        binding.tvPollTitle.setStyle(pollTitleTextStyle)
    }

    private fun configurePollInfoText(pollInfoTextStyle: LMFeedTextStyle?) {
        binding.tvPollInfo.apply {
            if (pollInfoTextStyle == null) {
                hide()
            } else {
                setStyle(pollInfoTextStyle)
                show()
            }
        }
    }

    private fun configureMemberVotedCountText(membersVotedCountTextStyle: LMFeedTextStyle?) {
        binding.tvMemberVotedCount.apply {
            if (membersVotedCountTextStyle == null) {
                hide()
            } else {
                setStyle(membersVotedCountTextStyle)
            }
        }
    }

    private fun configureSubmitPollVoteButton(submitPollButtonStyle: LMFeedButtonStyle) {
        binding.btnSubmitVote.setStyle(submitPollButtonStyle)
    }

    private fun configurePollExpiryText(pollExpiryTextStyle: LMFeedTextStyle?) {
        binding.tvPollTimeLeft.apply {
            if (pollExpiryTextStyle == null) {
                hide()
            } else {
                setStyle(pollExpiryTextStyle)
                show()
            }
        }
    }

    private fun configureAddPollOptionButton(addPollOptionButtonStyle: LMFeedButtonStyle) {
        binding.btnAddOption.setStyle(addPollOptionButtonStyle)
    }

    private fun configureEditPollVoteText(editPollVoteTextStyle: LMFeedTextStyle?) {
        binding.tvPollEditVote.apply {
            if (editPollVoteTextStyle == null) {
                hide()
            } else {
                show()
            }
        }
    }

    private fun configureClearPollIcon(clearPollIconStyle: LMFeedIconStyle?) {
        binding.ivClearPoll.apply {
            if (clearPollIconStyle == null) {
                hide()
            } else {
                setStyle(clearPollIconStyle)
                show()
            }
        }
    }

    private fun configureEditPollIcon(editPollIconStyle: LMFeedIconStyle?) {
        binding.ivEditPoll.apply {
            if (editPollIconStyle == null) {
                hide()
            } else {
                setStyle(editPollIconStyle)
                show()
            }
        }
    }

    /**
     * Sets click listener on the edit poll icon
     *
     * @param listener [LMFeedOnClickListener] interface to have click listener
     */
    fun setEditPollClicked(listener: LMFeedOnClickListener) {
        binding.ivEditPoll.setOnClickListener {
            listener.onClick()
        }
    }

    /**
     * Sets click listener on the clear poll icon
     *
     * @param listener [LMFeedOnClickListener] interface to have click listener
     */
    fun setClearPollClicked(listener: LMFeedOnClickListener) {
        binding.ivClearPoll.setOnClickListener {
            listener.onClick()
        }
    }

    /**
     * Sets click listener on the add poll options button
     *
     * @param listener [LMFeedOnClickListener] interface to have click listener
     */
    fun setAddPollOptionsClicked(listener: LMFeedOnClickListener) {
        binding.btnAddOption.setOnClickListener {
            listener.onClick()
        }
    }

    /**
     * Sets click listener on the submit vote button
     *
     * @param listener [LMFeedOnClickListener] interface to have click listener
     */
    fun setSubmitPollVoteClicked(listener: LMFeedOnClickListener) {
        binding.btnAddOption.setOnClickListener {
            listener.onClick()
        }
    }

    /**
     * Sets click listener on the member voted count text
     *
     * @param listener [LMFeedOnClickListener] interface to have click listener
     */
    fun setMemberVotedCountClicked(listener: LMFeedOnClickListener) {
        binding.tvMemberVotedCount.setOnClickListener {
            listener.onClick()
        }
    }

    /**
     * Sets click listener on the edit poll vote text
     *
     * @param listener [LMFeedOnClickListener] interface to have click listener
     */
    fun setEditPollVoteClicked(listener: LMFeedOnClickListener) {
        binding.tvPollEditVote.setOnClickListener {
            listener.onClick()
        }
    }
}