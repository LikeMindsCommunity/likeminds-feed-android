package com.likeminds.feed.android.core.ui.base.views

import android.content.Context
import android.util.AttributeSet
import android.widget.ProgressBar

/**
 * Represents a material circular progress bar
 * To customize this view use [LMFeedProgressBarStyle]
 */
class LMFeedProgressBar : ProgressBar {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )
}