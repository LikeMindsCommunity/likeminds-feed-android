package com.likeminds.feed.android.core.ui.base.views

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.button.MaterialButton

/**
 * Represents a material button
 * To customize this view use [LMFeedButtonStyle]
 */
class LMFeedButton : MaterialButton {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )
}