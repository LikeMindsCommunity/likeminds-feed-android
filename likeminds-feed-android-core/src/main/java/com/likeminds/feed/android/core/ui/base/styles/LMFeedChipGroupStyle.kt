package com.likeminds.feed.android.core.ui.base.styles

import androidx.annotation.DimenRes
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.ui.base.views.LMFeedButton
import com.likeminds.feed.android.core.ui.base.views.LMFeedChipGroup
import com.likeminds.feed.android.core.utils.LMFeedViewStyle

class LMFeedChipGroupStyle private constructor(
    val isSingleLine: Boolean,
    val isSingleSelection: Boolean,
    @DimenRes val chipGroupHorizontalSpacing: Int,
    @DimenRes val chipGroupVerticalSpacing: Int,
) : LMFeedViewStyle {

    class Builder {
        private var isSingleLine: Boolean = false

        private var isSingleSelection: Boolean = true

        @DimenRes
        private var chipGroupHorizontalSpacing: Int =
            R.dimen.lm_feed_default_chip_group_horizontal_spacing

        @DimenRes
        private var chipGroupVerticalSpacing: Int =
            R.dimen.lm_feed_default_chip_group_vertical_spacing

        fun isSingleLine(isSingleLine: Boolean) = apply {
            this.isSingleLine = isSingleLine
        }

        fun isSingleSelection(isSingleSelection: Boolean) = apply {
            this.isSingleSelection = isSingleSelection
        }

        fun chipGroupHorizontalSpacing(@DimenRes chipGroupHorizontalSpacing: Int) = apply {
            this.chipGroupHorizontalSpacing = chipGroupHorizontalSpacing
        }

        fun chipGroupVerticalSpacing(@DimenRes chipGroupVerticalSpacing: Int) = apply {
            this.chipGroupVerticalSpacing = chipGroupVerticalSpacing
        }

        fun build() = LMFeedChipGroupStyle(
            isSingleLine,
            isSingleSelection,
            chipGroupHorizontalSpacing,
            chipGroupVerticalSpacing,
        )
    }

    fun apply(chipGroup: LMFeedChipGroup) {
        chipGroup.apply {
            isSingleLine = this@LMFeedChipGroupStyle.isSingleLine
            isSingleSelection = this@LMFeedChipGroupStyle.isSingleSelection

            setChipSpacingHorizontalResource(this@LMFeedChipGroupStyle.chipGroupHorizontalSpacing)
            setChipSpacingVerticalResource(this@LMFeedChipGroupStyle.chipGroupVerticalSpacing)
        }
    }

    fun toBuilder(): Builder {
        return Builder().isSingleLine(isSingleLine)
            .isSingleSelection(isSingleSelection)
            .chipGroupHorizontalSpacing(chipGroupHorizontalSpacing)
            .chipGroupVerticalSpacing(chipGroupVerticalSpacing)
    }
}

/**
 * Util function to helps to apply all the styling [LMFeedChipGroupStyle] to [LMFeedButton]
 **/
fun LMFeedChipGroup.setStyle(viewStyle: LMFeedChipGroupStyle) {
    viewStyle.apply(this)
}