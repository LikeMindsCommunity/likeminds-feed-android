package com.likeminds.feed.android.core.ui.base.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.likeminds.feed.android.core.ui.base.styles.LMFeedImageStyle
import com.likeminds.feed.android.core.utils.LMFeedImageBindingUtil

/**
 * Represents a basic image view
 * To customize this view use [LMFeedImageStyle]
 */
class LMFeedImageView : AppCompatImageView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    //updates the image view with the provided [imageSrc] and uses the provided [imageViewStyle] to customize it
    fun setImage(imageSrc: Any?, imageViewStyle: LMFeedImageStyle) {
        imageViewStyle.apply {
            LMFeedImageBindingUtil.loadImage(
                this@LMFeedImageView,
                imageSrc,
                placeholderSrc,
                isCircle,
                cornerRadius ?: 0,
                showGreyScale
            )
        }
    }
}