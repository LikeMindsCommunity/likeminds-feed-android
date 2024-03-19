package com.likeminds.feed.android.core.ui.widgets.headerview.styles

import android.graphics.Typeface
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.ui.base.styles.LMFeedIconStyle
import com.likeminds.feed.android.core.ui.base.styles.LMFeedTextStyle
import com.likeminds.feed.android.core.utils.LMFeedViewStyle

class LMFeedHeaderViewStyle private constructor(
    val titleTextStyle: LMFeedTextStyle,
    val subtitleTextStyle: LMFeedTextStyle?,
    @ColorRes val backgroundColor: Int,
    @DimenRes val elevation: Int,

    // icon related
    val navigationIconStyle: LMFeedIconStyle?,
    val searchIconStyle: LMFeedIconStyle?,
) : LMFeedViewStyle {

    class Builder {
        private var titleTextStyle: LMFeedTextStyle = LMFeedTextStyle.Builder()
            .textColor(R.color.lm_feed_black)
            .typeface(Typeface.NORMAL)
            .build()

        private var subtitleTextStyle: LMFeedTextStyle? = null

        @ColorRes
        private var backgroundColor: Int = R.color.lm_feed_white

        @DimenRes
        private var elevation: Int = R.dimen.lm_feed_elevation_small

        private var navigationIconStyle: LMFeedIconStyle? = null

        private var searchIconStyle: LMFeedIconStyle? = null

        fun titleTextStyle(titleTextStyle: LMFeedTextStyle) =
            apply { this.titleTextStyle = titleTextStyle }

        fun subtitleTextStyle(subtitleTextStyle: LMFeedTextStyle?) =
            apply { this.subtitleTextStyle = subtitleTextStyle }

        fun backgroundColor(@ColorRes backgroundColor: Int) =
            apply { this.backgroundColor = backgroundColor }

        fun elevation(@DimenRes elevation: Int) = apply { this.elevation = elevation }

        fun navigationIconStyle(navigationIconStyle: LMFeedIconStyle?) =
            apply { this.navigationIconStyle = navigationIconStyle }

        fun searchIconStyle(searchIconStyle: LMFeedIconStyle?) = apply {
            this.searchIconStyle = searchIconStyle
        }

        fun build() = LMFeedHeaderViewStyle(
            titleTextStyle,
            subtitleTextStyle,
            backgroundColor,
            elevation,
            navigationIconStyle,
            searchIconStyle
        )
    }

    fun toBuilder(): Builder {
        return Builder().titleTextStyle(titleTextStyle)
            .subtitleTextStyle(subtitleTextStyle)
            .backgroundColor(backgroundColor)
            .elevation(elevation)
            .navigationIconStyle(navigationIconStyle)
            .searchIconStyle(searchIconStyle)
    }
}