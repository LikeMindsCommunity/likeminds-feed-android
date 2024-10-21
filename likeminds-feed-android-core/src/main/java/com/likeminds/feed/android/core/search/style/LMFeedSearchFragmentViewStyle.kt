package com.likeminds.feed.android.core.search.style

import android.graphics.Typeface
import androidx.annotation.ColorRes
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.search.view.LMFeedSearchFragment
import com.likeminds.feed.android.core.ui.base.styles.*
import com.likeminds.feed.android.core.ui.widgets.noentitylayout.style.LMFeedNoEntityLayoutViewStyle
import com.likeminds.feed.android.core.ui.widgets.searchbar.style.LMFeedSearchBarViewStyle
import com.likeminds.feed.android.core.utils.model.LMFeedPadding


/**
 * [LMFeedSearchFragmentViewStyle] helps you to customize the search feed fragment [LMFeedSearchFragment]
 *
 * @property feedSearchBarViewStyle : [LMFeedSearchBarViewStyle] this will help you to customize the search bar in the search feed fragment
 * @property noSearchResultLayoutViewStyle : [LMFeedNoEntityLayoutViewStyle] this will help you to customize the no search result layout in the feed search fragment
 * @property backgroundColor:  [Int] should be in format of [ColorRes] to add background color of the search bar | Default value = [null]
 *
 * */
class LMFeedSearchFragmentViewStyle private constructor(
    //search bar
    val feedSearchBarViewStyle: LMFeedSearchBarViewStyle,
    //no search result layout
    val noSearchResultLayoutViewStyle: LMFeedNoEntityLayoutViewStyle,
    //background color
    @ColorRes val backgroundColor: Int?
){
    class Builder {

        private var feedSearchBarViewStyle: LMFeedSearchBarViewStyle =
            LMFeedSearchBarViewStyle.Builder()
                .searchCloseIconStyle(
                    LMFeedIconStyle.Builder()
                        .inActiveSrc(R.drawable.lm_feed_ic_cross_black)
                        .iconTint(R.color.lm_feed_black)
                        .build()
                )
                .searchBackIconStyle(
                    LMFeedIconStyle.Builder()
                        .inActiveSrc(R.drawable.lm_feed_ic_arrow_back_black_24dp)
                        .build()
                )
                .backgroundColor(R.color.lm_feed_white)
                .elevation(R.dimen.lm_feed_elevation_small)
                .build()

        private var noSearchResultLayoutViewStyle: LMFeedNoEntityLayoutViewStyle =
            LMFeedNoEntityLayoutViewStyle.Builder()
                .imageStyle(
                    LMFeedImageStyle.Builder()
                        .imageSrc(R.drawable.lm_feed_ic_not_found)
                        .build()
                )
                .titleStyle(
                    LMFeedTextStyle.Builder()
                        .textColor(R.color.lm_feed_dark_grey)
                        .textSize(R.dimen.lm_feed_text_medium)
                        .typeface(Typeface.NORMAL)
                        .fontResource(R.font.lm_feed_roboto_medium)
                        .build()
                )
                .build()

        @ColorRes
        private var backgroundColor: Int? = null

        fun feedSearchBarViewStyle(feedSearchBarViewStyle: LMFeedSearchBarViewStyle) = apply {
            this.feedSearchBarViewStyle = feedSearchBarViewStyle
        }

        fun noSearchResultLayoutViewStyle(noSearchResultLayoutViewStyle: LMFeedNoEntityLayoutViewStyle) =
            apply {
                this.noSearchResultLayoutViewStyle = noSearchResultLayoutViewStyle
            }

        fun backgroundColor(@ColorRes backgroundColor: Int?) = apply {
            this.backgroundColor = backgroundColor
        }

        fun build() = LMFeedSearchFragmentViewStyle(
            feedSearchBarViewStyle,
            noSearchResultLayoutViewStyle,
            backgroundColor
        )

    }
    fun toBuilder(): Builder {
        return Builder().feedSearchBarViewStyle(feedSearchBarViewStyle)
            .noSearchResultLayoutViewStyle(noSearchResultLayoutViewStyle)
            .backgroundColor(backgroundColor)
    }
}