package com.likeminds.feed.android.core.ui.widgets.post.postmedia.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.likeminds.feed.android.core.databinding.LmFeedPostImageMediaViewBinding
import com.likeminds.feed.android.core.ui.base.styles.LMFeedIconStyle
import com.likeminds.feed.android.core.ui.base.styles.LMFeedImageStyle
import com.likeminds.feed.android.core.ui.base.styles.setStyle
import com.likeminds.feed.android.core.ui.widgets.post.postmedia.style.LMFeedPostImageMediaViewStyle
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.hide
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.show

class LMFeedPostImageMediaView : ConstraintLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet?, defStyle: Int) : super(
        context,
        attributeSet,
        defStyle
    )

    private val inflater =
        (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)

    private val binding = LmFeedPostImageMediaViewBinding.inflate(inflater, this, true)

    //sets provided [lmFeedPostImageMediaViewStyle] to the image view in the post
    fun setStyle(lmFeedPostImageMediaViewStyle: LMFeedPostImageMediaViewStyle) {
        lmFeedPostImageMediaViewStyle.apply {
            configureImageView(imageStyle)
            configureRemoveIconView(removeIconStyle)
        }
    }

    private fun configureImageView(imageStyle: LMFeedImageStyle) {
        binding.imageView.setStyle(imageStyle)
    }

    private fun configureRemoveIconView(removeIconStyle: LMFeedIconStyle?) {
        binding.ivCross.apply {
            if (removeIconStyle == null) {
                hide()
            } else {
                show()
                setStyle(removeIconStyle)
            }
        }
    }
}