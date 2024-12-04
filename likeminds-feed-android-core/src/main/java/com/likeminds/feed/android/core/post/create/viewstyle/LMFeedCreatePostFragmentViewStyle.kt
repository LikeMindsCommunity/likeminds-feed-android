package com.likeminds.feed.android.core.post.create.viewstyle

import android.text.TextUtils
import androidx.annotation.ColorRes
import com.likeminds.feed.android.core.*
import com.likeminds.feed.android.core.ui.base.styles.*
import com.likeminds.feed.android.core.ui.theme.LMFeedAppearance
import com.likeminds.feed.android.core.ui.widgets.headerview.style.LMFeedHeaderViewStyle
import com.likeminds.feed.android.core.ui.widgets.post.postheaderview.style.LMFeedPostHeaderViewStyle
import com.likeminds.feed.android.core.utils.LMFeedViewStyle
import com.likeminds.feed.android.core.utils.model.LMFeedPadding

/***
 * [LMFeedCreatePostFragmentViewStyle] helps you customize [LMFeedCreatePostFragment]
 * @property headerViewStyle: [LMFeedHeaderViewStyle] helps to customize the header/toolbar of the view
 * @property authorViewStyle: [LMFeedPostHeaderViewStyle] helps to customize the author view
 * @property selectTopicsChipStyle: [LMFeedChipStyle] helps to customize the topic selection view to create a post
 * @property editChipStyle: [LMFeedChipStyle] helps to customize the icon of selected topics
 * @property postComposerStyle: [LMFeedEditTextStyle] helps to customize the post composer
 * @property postHeadingComposerStyle: [LMFeedEditTextStyle] helps to customize the post heading composer
 * @property postHeadingLimitTextStyle: [LMFeedTextStyle] helps to customize the post heading limit text view
 * @property addMoreButtonStyle: [LMFeedButtonStyle] helps to customize the add more button style
 * @property backgroundColor: [Int] background color for the view, to provided in [ColorRes] format
 */
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
    //post heading composer view style
    val postHeadingComposerStyle: LMFeedEditTextStyle?,
    //post heading limit text style
    val postHeadingLimitTextStyle: LMFeedTextStyle?,
    //add more media button style
    val addMoreButtonStyle: LMFeedButtonStyle,
    //background color of the screen
    @ColorRes val backgroundColor: Int?,
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
                    .textSize(R.dimen.lm_feed_text_large)
                    .fontResource(R.font.lm_feed_roboto_medium)
                    .build()
            )
            .activeSubmitColor(LMFeedAppearance.getButtonColor())
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
                    .fontResource(R.font.lm_feed_roboto_medium)
                    .maxLines(1)
                    .ellipsize(TextUtils.TruncateAt.END)
                    .build()
            )
            .build()

        private var selectTopicsChipStyle: LMFeedChipStyle = LMFeedChipStyle.Builder()
            .chipBackgroundColor(R.color.lm_feed_majorelle_blue_10)
            .chipStartPadding(R.dimen.lm_feed_regular_padding)
            .chipIcon(R.drawable.lm_feed_ic_add_topics)
            .chipIconSize(R.dimen.lm_feed_chip_default_icon_size)
            .chipIconTint(LMFeedAppearance.getButtonColor())
            .build()

        private var editChipStyle: LMFeedChipStyle = LMFeedChipStyle.Builder()
            .chipBackgroundColor(R.color.lm_feed_majorelle_blue_10)
            .chipEndPadding(R.dimen.lm_feed_edit_chip_end_size)
            .chipStartPadding(R.dimen.lm_feed_edit_chip_end_size)
            .chipIcon(R.drawable.lm_feed_ic_edit_topic)
            .chipIconSize(R.dimen.lm_feed_chip_default_icon_size)
            .chipIconTint(LMFeedAppearance.getButtonColor())
            .build()

        private var postComposerStyle: LMFeedEditTextStyle = LMFeedEditTextStyle.Builder()
            .inputTextStyle(
                LMFeedTextStyle.Builder()
                    .ellipsize(TextUtils.TruncateAt.END)
                    .maxHeight(R.dimen.lm_feed_post_composer_max_height)
                    .minHeight(R.dimen.lm_feed_post_composer_min_height)
                    .textColor(R.color.lm_feed_dark_grey)
                    .textSize(R.dimen.lm_feed_text_large)
                    .build()
            )
            .hintTextColor(R.color.lm_feed_maastricht_blue_40)
            .build()

        private var postHeadingComposerStyle: LMFeedEditTextStyle? =
            if (LMFeedCoreApplication.selectedTheme == LMFeedTheme.QNA_FEED) {
                LMFeedEditTextStyle.Builder()
                    .inputTextStyle(
                        LMFeedTextStyle.Builder()
                            .ellipsize(TextUtils.TruncateAt.END)
                            .maxHeight(R.dimen.lm_feed_post_heading_composer_max_height)
                            .minHeight(R.dimen.lm_feed_post_heading_composer_min_height)
                            .textColor(R.color.lm_feed_dark_grey)
                            .fontResource(R.font.lm_feed_roboto_medium)
                            .textSize(R.dimen.lm_feed_text_large)
                            .build()
                    )
                    .hintTextColor(R.color.lm_feed_maastricht_blue_40)
                    .build()
            } else {
                null
            }

        private var postHeadingLimitTextStyle: LMFeedTextStyle? =
            if (LMFeedCoreApplication.selectedTheme == LMFeedTheme.QNA_FEED) {
                LMFeedTextStyle.Builder()
                    .textColor(R.color.lm_feed_dark_grey_70)
                    .textSize(R.dimen.lm_feed_text_small)
                    .fontResource(R.font.lm_feed_roboto)
                    .build()
            } else {
                null
            }

        private var addMoreButtonStyle: LMFeedButtonStyle = LMFeedButtonStyle.Builder()
            .textStyle(
                LMFeedTextStyle.Builder()
                    .textSize(R.dimen.lm_feed_text_medium)
                    .textColor(LMFeedAppearance.getButtonColor())
                    .fontResource(R.font.lm_feed_roboto_medium)
                    .textAllCaps(false)
                    .build()
            )
            .iconTint(LMFeedAppearance.getButtonColor())
            .icon(R.drawable.lm_feed_ic_plus)
            .build()

        @ColorRes
        private var backgroundColor: Int? = null

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

        fun postHeadingComposerStyle(postHeadingComposerStyle: LMFeedEditTextStyle?) = apply {
            this.postHeadingComposerStyle = postHeadingComposerStyle
        }

        fun postHeadingLimitTextStyle(postHeadingLimitTextStyle: LMFeedTextStyle?) = apply {
            this.postHeadingLimitTextStyle = postHeadingLimitTextStyle
        }

        fun addMoreButtonStyle(addMoreButtonStyle: LMFeedButtonStyle) = apply {
            this.addMoreButtonStyle = addMoreButtonStyle
        }

        fun backgroundColor(@ColorRes backgroundColor: Int?) = apply {
            this.backgroundColor = backgroundColor
        }

        fun build() = LMFeedCreatePostFragmentViewStyle(
            headerViewStyle,
            authorViewStyle,
            selectTopicsChipStyle,
            editChipStyle,
            postComposerStyle,
            postHeadingComposerStyle,
            postHeadingLimitTextStyle,
            addMoreButtonStyle,
            backgroundColor
        )
    }

    fun toBuilder(): Builder {
        return Builder().headerViewStyle(headerViewStyle)
            .authorViewStyle(authorViewStyle)
            .selectTopicsChipStyle(selectTopicsChipStyle)
            .editChipStyle(editChipStyle)
            .postComposerStyle(postComposerStyle)
            .postHeadingComposerStyle(postHeadingComposerStyle)
            .postHeadingLimitTextStyle(postHeadingLimitTextStyle)
            .addMoreButtonStyle(addMoreButtonStyle)
            .backgroundColor(backgroundColor)
    }
}