package com.likeminds.feed.android.core.ui.widgets.headerview.style

import android.graphics.Typeface
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.ui.base.styles.LMFeedIconStyle
import com.likeminds.feed.android.core.ui.base.styles.LMFeedTextStyle
import com.likeminds.feed.android.core.utils.LMFeedViewStyle

/**
 * [LMFeedHeaderViewStyle] helps you to customize the header view
 *
 * @property titleTextStyle : [LMFeedTextStyle] this will help you to customize the title text of the header view
 * @property subtitleTextStyle : [LMFeedTextStyle] this will help you to customize the subtitle text of the header view | set its value to [null] if you want to hide the subtitle in the header
 * @property backgroundColor: [Int] should be in format of [ColorRes] to add background color of the header | Default value =  [R.color.lm_feed_white]
 * @property elevation: [Int] should be in format of [DimenRes] to add custom elevation to the header view | Default value =  [R.dimen.lm_feed_elevation_small]
 * @property navigationIconStyle: [LMFeedIconStyle] this will help you to customize the navigation icon of the header view | set its value to [null] if you want to hide the navigation icon in the header
 * @property searchIconStyle: [LMFeedIconStyle] this will help you to customize the search icon of the header view | set its value to [null] if you want to hide the search icon in the header
 * */
class LMFeedHeaderViewStyle private constructor(
    //title text style
    val titleTextStyle: LMFeedTextStyle,
    //subtitle text style
    val subtitleTextStyle: LMFeedTextStyle?,
    //background color for the view
    @ColorRes val backgroundColor: Int,
    //elevation for the view
    @DimenRes val elevation: Int,
    //submit text style
    val submitTextStyle: LMFeedTextStyle?,
    //color of the active submit button
    @ColorRes val activeSubmitColor: Int?,
    //navigation icon style
    val navigationIconStyle: LMFeedIconStyle?,
    //search icon style
    val searchIconStyle: LMFeedIconStyle?
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

        private var submitTextStyle: LMFeedTextStyle? = null

        @ColorRes
        private var activeSubmitColor: Int? = null

        private var navigationIconStyle: LMFeedIconStyle? = null

        private var searchIconStyle: LMFeedIconStyle? = null

        fun titleTextStyle(titleTextStyle: LMFeedTextStyle) = apply {
            this.titleTextStyle = titleTextStyle
        }

        fun subtitleTextStyle(subtitleTextStyle: LMFeedTextStyle?) = apply {
            this.subtitleTextStyle = subtitleTextStyle
        }

        fun backgroundColor(@ColorRes backgroundColor: Int) = apply {
            this.backgroundColor = backgroundColor
        }

        fun elevation(@DimenRes elevation: Int) = apply {
            this.elevation = elevation
        }

        fun submitTextStyle(submitTextStyle: LMFeedTextStyle?) = apply {
            this.submitTextStyle = submitTextStyle
        }

        fun activeSubmitColor(@ColorRes activeSubmitColor: Int?) = apply {
            this.activeSubmitColor = activeSubmitColor
        }

        fun navigationIconStyle(navigationIconStyle: LMFeedIconStyle?) = apply {
            this.navigationIconStyle = navigationIconStyle
        }

        fun searchIconStyle(searchIconStyle: LMFeedIconStyle?) = apply {
            this.searchIconStyle = searchIconStyle
        }

        fun build() = LMFeedHeaderViewStyle(
            titleTextStyle,
            subtitleTextStyle,
            backgroundColor,
            elevation,
            submitTextStyle,
            activeSubmitColor,
            navigationIconStyle,
            searchIconStyle
        )
    }

    fun toBuilder(): Builder {
        return Builder().titleTextStyle(titleTextStyle)
            .subtitleTextStyle(subtitleTextStyle)
            .backgroundColor(backgroundColor)
            .elevation(elevation)
            .submitTextStyle(submitTextStyle)
            .activeSubmitColor(activeSubmitColor)
            .navigationIconStyle(navigationIconStyle)
            .searchIconStyle(searchIconStyle)
    }
}