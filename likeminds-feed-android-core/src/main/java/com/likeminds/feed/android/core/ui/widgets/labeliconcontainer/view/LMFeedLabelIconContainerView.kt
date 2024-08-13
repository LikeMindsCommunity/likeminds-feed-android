package com.likeminds.feed.android.core.ui.widgets.labeliconcontainer.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.likeminds.feed.android.core.databinding.LmFeedLabelIconContainerViewBinding
import com.likeminds.feed.android.core.ui.base.styles.*
import com.likeminds.feed.android.core.ui.widgets.labeliconcontainer.style.LMFeedLabelIconContainerViewStyle

class LMFeedLabelIconContainerView : ConstraintLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet?, defStyle: Int) : super(
        context,
        attributeSet,
        defStyle
    )

    private val inflater =
        (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)

    private val binding = LmFeedLabelIconContainerViewBinding.inflate(inflater, this, true)

    //sets provided [labelIconContainerViewStyle] to the label container view
    fun setStyle(labelIconContainerViewStyle: LMFeedLabelIconContainerViewStyle) {

        labelIconContainerViewStyle.apply {
            //set background color
            backgroundColor?.let {
                setBackgroundColor(ContextCompat.getColor(context, backgroundColor))
            }

            //configure each view in label icon container as per their styles
            configureContainerIcon(containerIconStyle)
            configureContainerLabel(containerLabelStyle)
        }
    }

    private fun configureContainerIcon(containerIconStyle: LMFeedIconStyle) {
        binding.ivContainerIcon.setStyle(containerIconStyle)
    }

    private fun configureContainerLabel(containerLabelStyle: LMFeedTextStyle) {
        binding.tvContainerLabel.setStyle(containerLabelStyle)
    }

    /**
     * Sets the provided icon to the icon of the label icon container view
     *
     * @param icon - drawable resource to be set as icon
     */
    fun setContainerIcon(@DrawableRes icon: Int) {
        binding.ivContainerIcon.setImageResource(icon)
    }

    /**
     * Sets the label of the label icon container view
     *
     * @param labelText - string to be set for label.
     */
    fun setContainerLabel(labelText: String) {
        binding.tvContainerLabel.text = labelText
    }
}