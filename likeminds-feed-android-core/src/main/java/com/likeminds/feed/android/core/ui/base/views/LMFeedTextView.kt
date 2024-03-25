package com.likeminds.feed.android.core.ui.base.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

/**
 * Represents a basic text view
 * To customize this view use [LMFeedTextStyle]
 */
class LMFeedTextView : AppCompatTextView {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    //clears drawables from the text view
    fun clearDrawables() {
        this.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
    }
}