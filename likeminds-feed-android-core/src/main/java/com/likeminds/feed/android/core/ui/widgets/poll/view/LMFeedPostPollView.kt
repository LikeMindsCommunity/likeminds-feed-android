package com.likeminds.feed.android.core.ui.widgets.poll.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.likeminds.feed.android.core.databinding.LmFeedPostPollViewBinding
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

    /**
     * Sets click listener on the add poll options button
     *
     * @param listener [LMFeedOnClickListener] interface to have click listener
     */
    fun setAddPollOptionsClicked(listener: LMFeedOnClickListener) {

    }
}