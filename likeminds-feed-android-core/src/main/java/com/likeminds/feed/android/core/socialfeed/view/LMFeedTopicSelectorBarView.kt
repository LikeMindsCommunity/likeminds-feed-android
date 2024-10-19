package com.likeminds.feed.android.core.socialfeed.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.likeminds.feed.android.core.databinding.LmFeedSocialTopicSelectorBarBinding
import com.likeminds.feed.android.core.topics.model.LMFeedTopicViewData
import com.likeminds.feed.android.core.ui.base.styles.LMFeedTextStyle
import com.likeminds.feed.android.core.ui.base.styles.setStyle
import com.likeminds.feed.android.core.socialfeed.adapter.LMFeedSelectedTopicAdapterListener
import com.likeminds.feed.android.core.socialfeed.style.LMFeedTopicSelectorBarViewStyle
import com.likeminds.feed.android.core.utils.base.LMFeedBaseViewType
import com.likeminds.feed.android.core.utils.listeners.LMFeedOnClickListener

class LMFeedTopicSelectorBarView : ConstraintLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    private val inflater =
        (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)

    private val binding: LmFeedSocialTopicSelectorBarBinding =
        LmFeedSocialTopicSelectorBarBinding.inflate(inflater, this, true)

    //sets the provided [topicSelectorBarStyle] to the topic selector bar
    fun setStyle(topicSelectorBarStyle: LMFeedTopicSelectorBarViewStyle) {
        topicSelectorBarStyle.apply {

            //sets background color
            backgroundColor?.let {
                setBackgroundColor(ContextCompat.getColor(context, it))
            }

            //sets the elevation of the header view
            elevation?.let {
                this@LMFeedTopicSelectorBarView.elevation = resources.getDimension(it)
            }

            //configures all the views in the topic selector bar
            configureAllTopicsSelector(allTopicsSelectorStyle)
            configureClearTopicFilter(clearTopicFilterStyle)
        }
    }

    private fun configureAllTopicsSelector(allTopicsSelectorStyle: LMFeedTextStyle) {
        binding.tvAllTopics.setStyle(allTopicsSelectorStyle)
    }

    private fun configureClearTopicFilter(clearTopicFilterStyle: LMFeedTextStyle) {
        binding.tvClear.setStyle(clearTopicFilterStyle)
    }

    /**
     * Sets text in all topics text view in the selector bar.
     *
     * @param allTopicsText Text for the all topics text view in the selector bar.
     */
    fun setAllTopicsText(allTopicsText: String) {
        binding.tvAllTopics.text = allTopicsText
    }

    /**
     * Sets text in clear topics text view in the selector bar.
     *
     * @param clearTopicsText Text for clear topics text view in the selector bar.
     */
    fun setClearTopicsText(clearTopicsText: String) {
        binding.tvClear.text = clearTopicsText
    }

    /**
     * Sets click listener on the all topics text view
     *
     * @param listener [LMFeedOnClickListener] interface to have click listener
     */
    fun setAllTopicsClickListener(listener: LMFeedOnClickListener) {
        binding.tvAllTopics.setOnClickListener {
            listener.onClick()
        }
    }

    /**
     * Sets click listener on the clear selected topics text
     *
     * @param listener [LMFeedOnClickListener] interface to have click listener
     */
    fun setClearSelectedTopicsClickListener(listener: LMFeedOnClickListener) {
        binding.tvClear.setOnClickListener {
            listener.onClick()
        }
    }

    /**
     * Sets all topics text view visibility
     *
     * @param isVisible whether to make the all topics text view visible or not
     */
    fun setAllTopicsTextVisibility(isVisible: Boolean) {
        binding.tvAllTopics.isVisible = isVisible
    }

    /**
     * Sets selected topic filter visibility
     *
     * @param isVisible whether to make the selected topics filter visible or not
     */
    fun setSelectedTopicFilterVisibility(isVisible: Boolean) {
        binding.grpSelectedTopics.isVisible = isVisible
    }

    /**
     * Selected topics adapter functions
     */

    //exposed function to set the adapter with the provided [listener] to the selected topic recycler view
    fun setSelectedTopicAdapter(listener: LMFeedSelectedTopicAdapterListener) {
        binding.rvSelectedTopics.setAdapter(listener)
    }

    //exposed function to get all the topics in the selected topics adapter
    fun getAllSelectedTopics(): List<LMFeedBaseViewType> {
        return binding.rvSelectedTopics.getAllSelectedTopics()
    }

    //exposed function to replace the selected topics with the provided [selectedTopics] in the selected topics adapter
    fun replaceSelectedTopics(selectedTopics: List<LMFeedTopicViewData>) {
        binding.rvSelectedTopics.replaceSelectedTopics(selectedTopics)
    }

    //exposed function to clear the selected topics in the selected topics adapter and notify the recycler view
    fun clearSelectedTopicsAndNotify() {
        binding.rvSelectedTopics.clearAllSelectedTopicsAndNotify()
    }

    //exposed function to remove the selected topic at the provided [index] in the selected topics adapter and notify the recycler view
    fun removeTopicAndNotify(position: Int) {
        binding.rvSelectedTopics.removeSelectedTopicAndNotify(position)
    }
}