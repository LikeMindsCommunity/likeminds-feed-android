package com.likeminds.feed.android.core.activityfeed.style

import android.graphics.Typeface
import android.text.TextUtils
import android.widget.ImageView
import androidx.annotation.ColorRes
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.ui.base.styles.*
import com.likeminds.feed.android.core.ui.widgets.acitivityfeed.style.LMFeedActivityViewStyle
import com.likeminds.feed.android.core.ui.widgets.headerview.style.LMFeedHeaderViewStyle
import com.likeminds.feed.android.core.ui.widgets.noentitylayout.style.LMFeedNoEntityLayoutViewStyle
import com.likeminds.feed.android.core.utils.LMFeedViewStyle
import com.likeminds.feed.android.core.utils.model.LMFeedPadding

/**
 * [LMFeedActivityFeedFragmentViewStyle] helps you to customize the activity feed fragment [LMFeedActivityFeedFragment]
 *
 * @property headerViewStyle : [LMFeedHeaderViewStyle] this will help you to customize the header view in the activity feed fragment
 * @property activityViewStyle : [LMFeedActivityViewStyle] this will help you to customize the activity feed item view
 * @property backgroundColor: [Int] should be in format of [ColorRes] this will help you to customize the background color of the activity feed fragment | Default value = [null]
 * */
class LMFeedActivityFeedFragmentViewStyle private constructor(
    //header view style
    val headerViewStyle: LMFeedHeaderViewStyle,
    //activity view style
    val activityViewStyle: LMFeedActivityViewStyle,
    //no activity layout style
    val noActivityLayoutViewStyle: LMFeedNoEntityLayoutViewStyle,
    //background color of the screen
    @ColorRes val backgroundColor: Int?
) : LMFeedViewStyle {

    class Builder {
        private var headerViewStyle = LMFeedHeaderViewStyle.Builder()
            .titleTextStyle(
                LMFeedTextStyle.Builder()
                    .textColor(R.color.lm_feed_black)
                    .textSize(R.dimen.lm_feed_header_view_title_text_size)
                    .maxLines(1)
                    .ellipsize(TextUtils.TruncateAt.END)
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

        private var activityViewStyle = LMFeedActivityViewStyle.Builder()
            .postTypeBadgeStyle(
                LMFeedImageStyle.Builder()
                    .scaleType(ImageView.ScaleType.CENTER)
                    .build()
            )
            .timestampTextStyle(
                LMFeedTextStyle.Builder()
                    .textColor(R.color.lm_feed_brown_grey)
                    .textSize(R.dimen.lm_feed_text_medium)
                    .build()
            )
            .readActivityBackgroundColor(R.color.lm_feed_white)
            .unreadActivityBackgroundColor(R.color.lm_feed_cloudy_blue_40)
            .build()

        private var noActivityLayoutViewStyle: LMFeedNoEntityLayoutViewStyle =
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

        fun headerViewStyle(headerViewStyle: LMFeedHeaderViewStyle) = apply {
            this.headerViewStyle = headerViewStyle
        }

        fun activityViewStyle(activityViewStyle: LMFeedActivityViewStyle) = apply {
            this.activityViewStyle = activityViewStyle
        }

        fun noActivityLayoutViewStyle(noActivityLayoutViewStyle: LMFeedNoEntityLayoutViewStyle) =
            apply {
                this.noActivityLayoutViewStyle = noActivityLayoutViewStyle
            }

        fun backgroundColor(@ColorRes backgroundColor: Int?) = apply {
            this.backgroundColor = backgroundColor
        }

        fun build() = LMFeedActivityFeedFragmentViewStyle(
            headerViewStyle,
            activityViewStyle,
            noActivityLayoutViewStyle,
            backgroundColor,
        )
    }

    fun toBuilder(): Builder {
        return Builder().headerViewStyle(headerViewStyle)
            .activityViewStyle(activityViewStyle)
            .noActivityLayoutViewStyle(noActivityLayoutViewStyle)
            .backgroundColor(backgroundColor)
    }
}