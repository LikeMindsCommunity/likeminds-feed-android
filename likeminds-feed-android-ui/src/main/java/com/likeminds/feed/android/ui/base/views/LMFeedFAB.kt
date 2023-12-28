package com.likeminds.feed.android.ui.base.views

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

/**
 * Represents a floating action button
 * To customize this view use [LMFeedFABStyle]
 */
class LMFeedFAB : ExtendedFloatingActionButton {

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
    }
}