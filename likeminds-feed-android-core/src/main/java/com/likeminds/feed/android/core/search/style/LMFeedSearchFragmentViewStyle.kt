package com.likeminds.feed.android.core.search.style

import android.text.TextUtils
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.ui.base.styles.LMFeedEditTextStyle
import com.likeminds.feed.android.core.ui.base.styles.LMFeedIconStyle
import com.likeminds.feed.android.core.ui.base.styles.LMFeedImageStyle
import com.likeminds.feed.android.core.ui.base.styles.LMFeedTextStyle
import com.likeminds.feed.android.core.ui.widgets.noentitylayout.style.LMFeedNoEntityLayoutViewStyle
import com.likeminds.feed.android.core.ui.widgets.searchbar.style.LMFeedSearchBarViewStyle
import com.likeminds.feed.android.core.utils.model.LMFeedPadding

class LMFeedSearchFragmentViewStyle private constructor(
    // search bar
    val searchBarViewStyle: LMFeedSearchBarViewStyle,
){
    class Builder {
        private var searchBarViewStyle: LMFeedSearchBarViewStyle = LMFeedSearchBarViewStyle.Builder()
            .searchInputStyle( LMFeedEditTextStyle.Builder()
                .hintTextColor(com.likeminds.customgallery.R.color.grey)
                .inputTextStyle(
                    LMFeedTextStyle.Builder()
                        .textColor(R.color.lm_feed_black)
                        .build()
                )
                .build())
//            .elevation()
            .searchBackIconStyle(
                LMFeedIconStyle.Builder()
                    .activeSrc(R.drawable.lm_feed_ic_arrow_back_black_24dp)
                    .iconTint(R.color.lm_feed_black)
                    .iconPadding(
                        LMFeedPadding(
                            R.dimen.lm_feed_icon_padding,
                            R.dimen.lm_feed_icon_padding,
                            R.dimen.lm_feed_icon_padding,
                            R.dimen.lm_feed_icon_padding
                        )
                    ).build()
            )
            .searchCloseIconStyle(
                LMFeedIconStyle.Builder()
                    .activeSrc(R.drawable.lm_feed_ic_cross_black)
                    .iconTint(R.color.lm_feed_black)
                    .iconPadding(
                        LMFeedPadding(
                            R.dimen.lm_feed_icon_padding,
                            R.dimen.lm_feed_icon_padding,
                            R.dimen.lm_feed_icon_padding,
                            R.dimen.lm_feed_icon_padding
                        )
                    ).build()
            )
            .build()



    }
}