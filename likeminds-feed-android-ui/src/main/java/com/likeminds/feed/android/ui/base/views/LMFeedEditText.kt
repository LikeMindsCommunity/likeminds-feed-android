package com.likeminds.feed.android.ui.base.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

/**
 * Represents a basic edit text
 * To customize this view use [LMFeedEditTextStyle]
 */
class LMFeedEditText : AppCompatEditText {
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