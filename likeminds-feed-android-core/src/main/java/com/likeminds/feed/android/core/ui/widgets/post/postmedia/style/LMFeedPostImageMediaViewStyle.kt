package com.likeminds.feed.android.core.ui.widgets.post.postmedia.style

import android.widget.ImageView
import com.likeminds.feed.android.core.ui.base.styles.LMFeedIconStyle
import com.likeminds.feed.android.core.ui.base.styles.LMFeedImageStyle
import com.likeminds.feed.android.core.utils.LMFeedViewUtils

/**
 * [LMFeedPostImageMediaViewStyle] helps you to customize a [LMFeedPostImageMediaView] in the following way
 * @property imageStyle: [LMFeedImageStyle] this will help you to customize the image attachment in the post
 * @property removeIconStyle: [LMFeedIconStyle] this help you to customize the remove icon to be used while creating or editing a post | Set to [null] if you want to hide the view
 **/
class LMFeedPostImageMediaViewStyle private constructor(
    val imageStyle: LMFeedImageStyle,
    val removeIconStyle: LMFeedIconStyle?
) {
    class Builder {
        private var imageStyle: LMFeedImageStyle = LMFeedImageStyle.Builder()
            .placeholderSrc(LMFeedViewUtils.getShimmer())
            .scaleType(ImageView.ScaleType.CENTER_CROP)
            .build()

        private var removeIconStyle: LMFeedIconStyle? = null

        fun imageStyle(imageStyle: LMFeedImageStyle) = apply {
            this.imageStyle = imageStyle
        }

        fun removeIconStyle(removeIconStyle: LMFeedIconStyle?) =
            apply { this.removeIconStyle = removeIconStyle }

        fun build() = LMFeedPostImageMediaViewStyle(
            imageStyle,
            removeIconStyle
        )
    }

    fun toBuilder(): Builder {
        return Builder().imageStyle(imageStyle)
            .removeIconStyle(removeIconStyle)
    }
}