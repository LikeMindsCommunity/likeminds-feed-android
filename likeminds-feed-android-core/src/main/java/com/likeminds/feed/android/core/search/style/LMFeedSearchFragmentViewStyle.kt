package com.likeminds.feed.android.core.search.style

import androidx.annotation.ColorRes
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.search.view.LMFeedSearchFragment
import com.likeminds.feed.android.core.ui.base.styles.LMFeedIconStyle
import com.likeminds.feed.android.core.ui.widgets.searchbar.style.LMFeedSearchBarViewStyle
import com.likeminds.feed.android.core.utils.model.LMFeedPadding


/**
 * [LMFeedSearchFragmentViewStyle] helps you to customize the search feed fragment [LMFeedSearchFragment]
 *
 * @property feedSearchBarViewStyle : [LMFeedSearchBarViewStyle] this will help you to customize the search bar in the search feed fragment
 * TODO("No entity - noPostLayout")
 *
 * */
class LMFeedSearchFragmentViewStyle private constructor(
    // search bar
    val feedSearchBarViewStyle: LMFeedSearchBarViewStyle,
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

        @ColorRes
        private var backgroundColor: Int? = null

        fun feedSearchBarViewStyle(feedSearchBarViewStyle: LMFeedSearchBarViewStyle) = apply {
            this.feedSearchBarViewStyle = feedSearchBarViewStyle
        }

        fun backgroundColor(@ColorRes backgroundColor: Int?) = apply {
            this.backgroundColor = backgroundColor
        }

        fun build() = LMFeedSearchFragmentViewStyle(
            feedSearchBarViewStyle,
            backgroundColor
        )

    }
    fun toBuilder(): Builder {
        return Builder().feedSearchBarViewStyle(feedSearchBarViewStyle)
            .backgroundColor(backgroundColor)
    }
}