package com.likeminds.feed.android.ui.base.styles

import android.text.TextUtils.TruncateAt
import androidx.annotation.*
import androidx.core.content.ContextCompat
import com.likeminds.feed.android.ui.R
import com.likeminds.feed.android.ui.base.views.LMFeedImageView
import com.likeminds.feed.android.ui.base.views.LMFeedTextView
import com.likeminds.feed.android.ui.utils.LMFeedImageBindingUtil
import com.likeminds.feed.android.ui.utils.ViewStyle
import com.likeminds.feed.android.ui.utils.model.LMFeedPadding

/**
 * [LMFeedImageStyle] helps you to customize a [LMFeedTextView] with the following properties
 * @property imageSrc: [Any] to customize the image source | Default value =  [R.drawable.lm_feed_picture_placeholder]
 * @property drawableSrc: [Any] to customize the drawable source | Default value = [R.drawable.lm_feed_picture_placeholder]
 * @property isCircle: [Boolean] to customize whether the image is circular or not | Default value = [false]
 * @property showGreyScale: [Boolean] to customize whether to show grey scale or not | Default value = [false]
 * @property cornerRadius: [Int] should be in format of [DimenRes] to customize the corner radius of the image | Default value = [null]
 * @property padding: [LMFeedPadding] to customize the padding of the image | Default value = [null]
 * @property backgroundColor: [Int] should be in format of [ColorRes] to customize the background color of the image | Default value = [null]
 **/
class LMFeedImageStyle private constructor(
    val imageSrc: Any,
    val drawableSrc: Any,
    val isCircle: Boolean,
    val showGreyScale: Boolean,
    @DimenRes val cornerRadius: Int?,
    val padding: LMFeedPadding?,
    @ColorRes val backgroundColor: Int?
) : ViewStyle {

    class Builder {
        private var imageSrc: Any = R.drawable.lm_feed_picture_placeholder
        private var drawableSrc: Any = R.drawable.lm_feed_picture_placeholder
        private var isCircle: Boolean = false
        private var showGreyScale: Boolean = false

        @DimenRes
        private var cornerRadius: Int? = null
        private var padding: LMFeedPadding? = null

        @ColorRes
        private var backgroundColor: Int? = null

        fun imageSrc(imageSrc: Any) = apply {
            this.imageSrc = imageSrc
        }

        fun drawableSrc(drawableSrc: Any) = apply {
            this.drawableSrc = drawableSrc
        }

        fun isCircle(isCircle: Boolean) = apply {
            this.isCircle = isCircle
        }

        fun showGreyScale(showGreyScale: Boolean) = apply {
            this.showGreyScale = showGreyScale
        }

        fun cornerRadius(@DimenRes cornerRadius: Int?) = apply {
            this.cornerRadius = cornerRadius
        }

        fun padding(padding: LMFeedPadding?) = apply {
            this.padding = padding
        }

        fun backgroundColor(@ColorRes backgroundColor: Int?) = apply {
            this.backgroundColor = backgroundColor
        }

        fun build() = LMFeedImageStyle(
            imageSrc,
            drawableSrc,
            isCircle,
            showGreyScale,
            cornerRadius,
            padding,
            backgroundColor
        )
    }

    fun toBuilder(): Builder {
        return Builder().imageSrc(imageSrc)
            .drawableSrc(drawableSrc)
            .isCircle(isCircle)
            .showGreyScale(showGreyScale)
            .cornerRadius(cornerRadius)
            .padding(padding)
            .backgroundColor(backgroundColor)
    }

    fun apply(imageView: LMFeedImageView) {
        imageView.apply {

            // loads image and drawable as per the passed values
            LMFeedImageBindingUtil.loadImage(
                this,
                imageSrc,
                drawableSrc,
                isCircle,
                cornerRadius ?: 0,
                showGreyScale
            )

            // sets background color of the image
            if (this@LMFeedImageStyle.backgroundColor != null) {
                val backgroundColor =
                    ContextCompat.getColor(context, this@LMFeedImageStyle.backgroundColor)
                this.setBackgroundColor(backgroundColor)
            }

            // sets text padding
            if (padding != null) {
                setPadding(
                    padding.paddingLeft,
                    padding.paddingTop,
                    padding.paddingRight,
                    padding.paddingBottom
                )
            }
        }
    }
}

/**
 * Util function that helps to apply all the styling [LMFeedImageStyle] to [LMFeedImageView]
 **/
fun LMFeedImageView.setStyle(viewStyle: LMFeedImageStyle) {
    viewStyle.apply(this)
}