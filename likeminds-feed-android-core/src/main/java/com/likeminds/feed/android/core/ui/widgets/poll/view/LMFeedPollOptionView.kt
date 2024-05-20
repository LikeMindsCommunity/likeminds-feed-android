package com.likeminds.feed.android.core.ui.widgets.poll.view

import android.content.Context
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.databinding.LmFeedPollOptionViewBinding
import com.likeminds.feed.android.core.poll.model.LMFeedPollOptionViewData
import com.likeminds.feed.android.core.ui.base.styles.*
import com.likeminds.feed.android.core.ui.widgets.poll.style.LMFeedPostPollOptionViewStyle
import com.likeminds.feed.android.core.utils.LMFeedStyleTransformer
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.hide
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.show
import com.likeminds.feed.android.core.utils.listeners.LMFeedOnClickListener
import com.likeminds.feed.android.core.utils.user.LMFeedUserPreferences
import kotlin.math.roundToInt

class LMFeedPollOptionView : ConstraintLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet?, defStyle: Int) : super(
        context,
        attributeSet,
        defStyle
    )

    private val inflater =
        (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)

    private val binding = LmFeedPollOptionViewBinding.inflate(inflater, this, true)

    //sets provided [postPollOptionViewStyle] to the post poll option view
    fun setStyle(postPollOptionViewStyle: LMFeedPostPollOptionViewStyle) {
        postPollOptionViewStyle.apply {
            configurePollOptionText(pollOptionTextStyle)
            configurePollOptionAddedByText(pollOptionAddedByTextStyle)
            configurePollOptionCheckedIcon(pollOptionCheckIconStyle)
            configurePollOptionVotesCountText(pollOptionVotesCountTextStyle)
        }
    }

    private fun configurePollOptionText(pollOptionTextStyle: LMFeedTextStyle) {
        binding.tvPollOption.setStyle(pollOptionTextStyle)
    }

    private fun configurePollOptionAddedByText(pollOptionAddedByTextStyle: LMFeedTextStyle?) {
        binding.tvAddedBy.apply {
            if (pollOptionAddedByTextStyle == null) {
                hide()
            } else {
                setStyle(pollOptionAddedByTextStyle)
            }
        }
    }

    private fun configurePollOptionCheckedIcon(pollOptionCheckIconStyle: LMFeedIconStyle?) {
        binding.ivChecked.apply {
            if (pollOptionCheckIconStyle == null) {
                hide()
            } else {
                setStyle(pollOptionCheckIconStyle)
            }
        }
    }

    private fun configurePollOptionVotesCountText(pollOptionVotesCountTextStyle: LMFeedTextStyle?) {
        binding.tvNoVotes.apply {
            if (pollOptionVotesCountTextStyle == null) {
                hide()
            } else {
                setStyle(pollOptionVotesCountTextStyle)
            }
        }
    }

    fun setPollOptionText(pollOptionText: String) {
        binding.tvPollOption.text = pollOptionText
    }

    fun setPollOptionAddedByText(pollOptionViewData: LMFeedPollOptionViewData) {
        val postMediaStyle = LMFeedStyleTransformer.postViewStyle.postMediaViewStyle
        postMediaStyle.postPollMediaStyle?.pollOptionsViewStyle?.pollOptionAddedByTextStyle
            ?: return

        binding.tvAddedBy.apply {
            if (pollOptionViewData.allowAddOption) {
                val addedByUser = pollOptionViewData.addedByUser

                val userPreferences = LMFeedUserPreferences(context)
                val loggedInUUID = userPreferences.getUUID()

                text = if (addedByUser.sdkClientInfoViewData.uuid == loggedInUUID) {
                    context.getString(R.string.lm_feed_added_by_you)
                } else {
                    context.getString(R.string.lm_feed_added_by_s, addedByUser.name)
                }
                show()
            } else {
                hide()
            }
        }
    }

    /**
     * Sets poll option progress background as per the percentage of votes on the option
     *
     * @param pollOptionViewData [LMFeedPollOptionViewData] data for the poll option
     * @param pollOptionViewStyle [LMFeedPostPollOptionViewStyle] view style for the poll option
     */
    fun setPollOptionBackgroundProgress(
        pollOptionViewData: LMFeedPollOptionViewData,
        pollOptionViewStyle: LMFeedPostPollOptionViewStyle
    ) {
        binding.apply {
            val drawable = pbPollBackground.progressDrawable as LayerDrawable
            val clip =
                drawable.findDrawableByLayerId(R.id.lm_feed_poll_progress_clip) as ClipDrawable

            if (pollOptionViewData.toShowResults) {
                pbPollBackground.progress = pollOptionViewData.percentage.roundToInt()
            } else {
                pbPollBackground.progress = 0
            }

            if (pollOptionViewData.isSelected) {
                clip.setTint(
                    ContextCompat.getColor(
                        binding.root.context,
                        pollOptionViewStyle.pollSelectedOptionColor
                    )
                )
            } else {
                clip.setTint(
                    ContextCompat.getColor(
                        binding.root.context,
                        pollOptionViewStyle.pollOtherOptionColor
                    )
                )
            }
        }
    }

    /**
     * Sets click listener on the poll option
     *
     * @param listener [LMFeedOnClickListener] interface to have click listener
     */
    fun setPollOptionClicked(listener: LMFeedOnClickListener) {
        binding.clPollOption.setOnClickListener {
            listener.onClick()
        }
    }

    /**
     * Sets click listener on the poll option votes count text
     *
     * @param listener [LMFeedOnClickListener] interface to have click listener
     */
    fun setPollOptionVotesCountClicked(listener: LMFeedOnClickListener) {
        binding.tvNoVotes.setOnClickListener {
            listener.onClick()
        }
    }
}