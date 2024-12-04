package com.likeminds.feed.android.core.likes.style

import android.text.TextUtils
import androidx.annotation.ColorRes
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.ui.base.styles.*
import com.likeminds.feed.android.core.ui.theme.LMFeedAppearance
import com.likeminds.feed.android.core.ui.widgets.headerview.style.LMFeedHeaderViewStyle
import com.likeminds.feed.android.core.ui.widgets.user.style.LMFeedUserViewStyle
import com.likeminds.feed.android.core.utils.LMFeedViewStyle
import com.likeminds.feed.android.core.utils.model.LMFeedPadding

/**
 * [LMFeedLikesFragmentViewStyle] helps you to customize the likes fragment [LMFeedLikesFragment]
 *
 * @property headerViewStyle : [LMFeedHeaderViewStyle] this will help you to customize the header view in the likes fragment
 * @property userViewStyle : [LMFeedUserViewStyle] this will help you to customize the create new post fab in the likes fragment
 * @property backgroundColor: [Int] should be in format of [ColorRes] this will help you to customize the background color of the likes fragment | Default value = [null]
 * */
class LMFeedLikesFragmentViewStyle private constructor(
    //header view style
    val headerViewStyle: LMFeedHeaderViewStyle,
    //user view style
    val userViewStyle: LMFeedUserViewStyle,
    //background color
    @ColorRes val backgroundColor: Int?
) : LMFeedViewStyle {
    class Builder {
        private var headerViewStyle: LMFeedHeaderViewStyle = LMFeedHeaderViewStyle.Builder()
            .titleTextStyle(
                LMFeedTextStyle.Builder()
                    .textColor(R.color.lm_feed_black)
                    .textSize(R.dimen.lm_feed_header_view_title_text_size)
                    .fontResource(R.font.lm_feed_roboto_medium)
                    .maxLines(1)
                    .ellipsize(TextUtils.TruncateAt.END)
                    .build()
            )
            .subtitleTextStyle(
                LMFeedTextStyle.Builder()
                    .textColor(R.color.lm_feed_grey)
                    .maxLines(1)
                    .ellipsize(TextUtils.TruncateAt.END)
                    .textSize(R.dimen.lm_feed_text_medium)
                    .build()
            )
            .backgroundColor(R.color.lm_feed_white)
            .navigationIconStyle(
                LMFeedIconStyle.Builder()
                    .iconTint(R.color.lm_feed_black)
                    .inActiveSrc(R.drawable.lm_feed_ic_arrow_back_black_24dp)
                    .iconPadding(
                        LMFeedPadding(
                            R.dimen.lm_feed_icon_padding,
                            R.dimen.lm_feed_icon_padding,
                            R.dimen.lm_feed_icon_padding,
                            R.dimen.lm_feed_icon_padding
                        )
                    )
                    .build()
            )
            .build()

        private var userViewStyle: LMFeedUserViewStyle = LMFeedUserViewStyle.Builder()
            .userTitleViewStyle(
                LMFeedTextStyle.Builder()
                    .ellipsize(TextUtils.TruncateAt.END)
                    .maxLines(1)
                    .textColor(LMFeedAppearance.getButtonColor())
                    .textSize(R.dimen.lm_feed_text_medium)
                    .fontResource(R.font.lm_feed_roboto_medium)
                    .build()
            )
            .build()

        @ColorRes
        private var backgroundColor: Int? = null

        fun headerViewStyle(headerViewStyle: LMFeedHeaderViewStyle) = apply {
            this.headerViewStyle = headerViewStyle
        }

        fun userViewStyle(userViewStyle: LMFeedUserViewStyle) = apply {
            this.userViewStyle = userViewStyle
        }

        fun backgroundColor(@ColorRes backgroundColor: Int?) = apply {
            this.backgroundColor = backgroundColor
        }

        fun build() = LMFeedLikesFragmentViewStyle(
            headerViewStyle,
            userViewStyle,
            backgroundColor
        )
    }

    fun toBuilder(): Builder {
        return Builder().headerViewStyle(headerViewStyle)
            .userViewStyle(userViewStyle)
            .backgroundColor(backgroundColor)
    }
}