package com.likeminds.feed.android.core.post.create.viewstyle

import android.text.TextUtils
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.ui.base.styles.LMFeedChipStyle
import com.likeminds.feed.android.core.ui.base.styles.LMFeedEditTextStyle
import com.likeminds.feed.android.core.ui.base.styles.LMFeedIconStyle
import com.likeminds.feed.android.core.ui.base.styles.LMFeedImageStyle
import com.likeminds.feed.android.core.ui.base.styles.LMFeedTextStyle
import com.likeminds.feed.android.core.ui.widgets.headerview.style.LMFeedHeaderViewStyle
import com.likeminds.feed.android.core.ui.widgets.post.postheaderview.style.LMFeedPostHeaderViewStyle
import com.likeminds.feed.android.core.utils.LMFeedViewStyle
import com.likeminds.feed.android.core.utils.model.LMFeedPadding

class LMFeedCreatePostFragmentViewStyle private constructor(
    //header view style
    val headerViewStyle: LMFeedHeaderViewStyle,
    //author frame view style
    val authorViewStyle: LMFeedPostHeaderViewStyle,
    //select topics chip style
    val selectTopicsChipStyle: LMFeedChipStyle,
    //edit chip style
    val editChipStyle: LMFeedChipStyle,
    //post composer view style
    val postComposerStyle: LMFeedEditTextStyle,
) : LMFeedViewStyle {
    class Builder {
        private var headerViewStyle: LMFeedHeaderViewStyle = LMFeedHeaderViewStyle.Builder()
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
            .submitTextStyle(
                LMFeedTextStyle.Builder()
                    .textColor(R.color.lm_feed_grey)
                    .textSize(R.dimen.lm_feed_text_medium)
                    .fontResource(R.font.lm_feed_roboto_medium)
                    .build()
            )
            .activeSubmitColor(R.color.lm_feed_majorelle_blue)
            .build()

        private var authorViewStyle: LMFeedPostHeaderViewStyle = LMFeedPostHeaderViewStyle.Builder()
            .authorImageViewStyle(
                LMFeedImageStyle.Builder()
                    .isCircle(true)
                    .build()
            )
            .authorNameViewStyle(
                LMFeedTextStyle.Builder()
                    .textColor(R.color.lm_feed_raisin_black)
                    .textSize(R.dimen.lm_feed_text_large)
                    .maxLines(1)
                    .ellipsize(TextUtils.TruncateAt.END)
                    .build()
            )
            .build()

        private var selectTopicsChipStyle: LMFeedChipStyle = LMFeedChipStyle.Builder()
            .chipBackgroundColor(R.color.lm_feed_majorelle_blue_10)
            .chipStartPadding(R.dimen.lm_feed_padding_big)
            .chipIcon(R.drawable.lm_feed_ic_add_topics)
            .chipIconSize(R.dimen.lm_feed_chip_default_icon_size)
            .chipIconTint(R.color.lm_feed_majorelle_blue)
            .build()

        private var editChipStyle: LMFeedChipStyle = LMFeedChipStyle.Builder()
            .chipBackgroundColor(R.color.lm_feed_majorelle_blue_10)
            .chipEndPadding(R.dimen.lm_feed_edit_chip_end_size)
            .chipStartPadding(R.dimen.lm_feed_edit_chip_end_size)
            .chipIcon(R.drawable.lm_feed_ic_edit_topic)
            .chipIconSize(R.dimen.lm_feed_chip_default_icon_size)
            .chipIconTint(R.color.lm_feed_majorelle_blue)
            .build()

        private var postComposerStyle: LMFeedEditTextStyle = LMFeedEditTextStyle.Builder()
            .inputTextStyle(
                LMFeedTextStyle.Builder()
                    .ellipsize(TextUtils.TruncateAt.END)
                    .maxHeight(R.dimen.lm_feed_post_composer_max_height)
                    .minHeight(R.dimen.lm_feed_post_composer_min_height)
                    .textColor(R.color.lm_feed_dark_grey)
                    .textSize(R.dimen.lm_feed_text_medium)
                    .build()
            )
            .hintTextColor(R.color.lm_feed_maastricht_blue_40)
            .build()

        fun headerViewStyle(headerViewStyle: LMFeedHeaderViewStyle) = apply {
            this.headerViewStyle = headerViewStyle
        }

        fun authorViewStyle(authorViewStyle: LMFeedPostHeaderViewStyle) =
            apply { this.authorViewStyle = authorViewStyle }

        fun selectTopicsChipStyle(selectTopicsChipStyle: LMFeedChipStyle) =
            apply { this.selectTopicsChipStyle = selectTopicsChipStyle }

        fun editChipStyle(editChipStyle: LMFeedChipStyle) =
            apply { this.editChipStyle = editChipStyle }

        fun postComposerStyle(postComposerStyle: LMFeedEditTextStyle) = apply {
            this.postComposerStyle = postComposerStyle
        }

        fun build() = LMFeedCreatePostFragmentViewStyle(
            headerViewStyle,
            authorViewStyle,
            selectTopicsChipStyle,
            editChipStyle,
            postComposerStyle
        )
    }

    fun toBuilder(): Builder {
        return Builder().headerViewStyle(headerViewStyle)
            .authorViewStyle(authorViewStyle)
            .selectTopicsChipStyle(selectTopicsChipStyle)
            .editChipStyle(editChipStyle)
            .postComposerStyle(postComposerStyle)
    }
}