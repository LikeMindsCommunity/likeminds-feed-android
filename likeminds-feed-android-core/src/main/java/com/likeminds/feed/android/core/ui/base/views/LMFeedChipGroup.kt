package com.likeminds.feed.android.core.ui.base.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import com.google.android.material.chip.ChipGroup
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.databinding.LmFeedChipBinding
import com.likeminds.feed.android.core.ui.base.styles.LMFeedChipStyle
import com.likeminds.feed.android.core.ui.base.styles.setStyle
import com.likeminds.feed.android.core.utils.listeners.LMFeedOnClickListener

class LMFeedChipGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ChipGroup(context, attrs, defStyleAttr) {

    //removes all the chips from the chip group
    fun removeAllChips() {
        this.removeAllViews()
    }

    /**
    * Adds chip to this [LMFeedChipGroup]
    *
    * @param chipText - text to be set in the chip
    * @param chipStyle - style to be used for the chip to be added
    * */
    fun addChip(
        chipText: String? = null,
        chipStyle: LMFeedChipStyle,
        chipClickListener: LMFeedOnClickListener? = null
    ) {
        val chipBinding = LmFeedChipBinding.inflate(
            LayoutInflater.from(context),
            this,
            false
        )

        chipBinding.chip.apply {
            //text related
            if (chipText == null) {
                textStartPadding = resources.getDimension(R.dimen.zero_dp)
                textEndPadding = resources.getDimension(R.dimen.zero_dp)
            } else {
                text = chipText
            }
            setStyle(chipStyle)

            setOnClickListener {
                chipClickListener?.onClick()
            }
        }

        this.addView(chipBinding.chip)
    }
}