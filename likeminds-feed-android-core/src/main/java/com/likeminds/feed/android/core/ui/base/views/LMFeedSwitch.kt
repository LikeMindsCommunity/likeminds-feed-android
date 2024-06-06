package com.likeminds.feed.android.core.ui.base.views

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.materialswitch.MaterialSwitch

/**
 * Represents a basic switch view
 *
 */
class LMFeedSwitch : MaterialSwitch {
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