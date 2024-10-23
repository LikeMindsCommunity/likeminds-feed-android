package com.likeminds.feed.android.core.ui.widgets.labelimagecontainer.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.likeminds.feed.android.core.databinding.LmFeedLabelImageContainerViewBinding
import com.likeminds.feed.android.core.ui.base.styles.*
import com.likeminds.feed.android.core.ui.widgets.labelimagecontainer.style.LMFeedLabelImageContainerViewStyle
import com.likeminds.feed.android.core.utils.listeners.LMFeedOnClickListener

class LMFeedLabelImageContainerView : ConstraintLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet?, defStyle: Int) : super(
        context,
        attributeSet,
        defStyle
    )

    private val inflater =
        (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)

    private val binding = LmFeedLabelImageContainerViewBinding.inflate(inflater, this, true)

    private lateinit var labelImageContainerViewStyle: LMFeedLabelImageContainerViewStyle

    //sets provided [labelImageContainerViewStyle] to the label container view
    fun setStyle(labelImageContainerViewStyle: LMFeedLabelImageContainerViewStyle) {

        this.labelImageContainerViewStyle = labelImageContainerViewStyle
        labelImageContainerViewStyle.apply {
            //set background color
            backgroundColor?.let {
                setBackgroundColor(ContextCompat.getColor(context, backgroundColor))
            }

            //configure each view in label image container as per their styles
            configureContainerImage(containerImageStyle)
            configureContainerLabel(containerLabelStyle)
        }
    }

    private fun configureContainerImage(containerImageStyle: LMFeedImageStyle) {
        binding.ivContainer.setStyle(containerImageStyle)
    }

    private fun configureContainerLabel(containerLabelStyle: LMFeedTextStyle) {
        binding.tvContainerLabel.setStyle(containerLabelStyle)
    }

    /**
     * Sets author image view.
     *
     * @param imageSrc - source of the image
     */
    fun setContainerImage(imageSrc: Any) {
        binding.ivContainer.setImage(imageSrc, labelImageContainerViewStyle.containerImageStyle)
    }

    /**
     * Sets the label of the label image container view
     *
     * @param labelText - string to be set for label.
     */
    fun setContainerLabel(labelText: String) {
        binding.tvContainerLabel.text = labelText
    }

    /**
     * Sets click listener on the container view
     *
     * @param listener [LMFeedOnClickListener] interface to have click listener
     */
    fun setContainerClickListener(listener: LMFeedOnClickListener) {
        binding.root.setOnClickListener {
            listener.onClick()
        }
    }
}