package com.likeminds.feed.android.core.post.style

import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.ui.base.styles.*
import com.likeminds.feed.android.core.ui.theme.LMFeedTheme
import com.likeminds.feed.android.core.ui.widgets.poll.style.LMFeedPostPollOptionViewStyle
import com.likeminds.feed.android.core.ui.widgets.poll.style.LMFeedPostPollViewStyle
import com.likeminds.feed.android.core.ui.widgets.post.postactionview.style.LMFeedPostActionViewStyle
import com.likeminds.feed.android.core.ui.widgets.post.postheaderview.style.LMFeedPostHeaderViewStyle
import com.likeminds.feed.android.core.ui.widgets.post.postmedia.style.*
import com.likeminds.feed.android.core.utils.LMFeedViewStyle
import com.likeminds.feed.android.core.utils.LMFeedViewUtils
import com.likeminds.feed.android.core.utils.model.LMFeedPadding

/**
 * [LMFeedPostViewStyle] helps you to customize the header view
 *
 * @property postHeaderViewStyle : [LMFeedPostHeaderViewStyle] this will help you to customize the post header view [LMFeedPostHeaderView]
 * @property postContentTextStyle : [LMFeedTextStyle] this will help you to customize the text content of the post
 * @property postMediaViewStyle: [LMFeedPostMediaViewStyle] this will help you to customize the media attached in the post
 * @property postActionViewStyle: [LMFeedPostActionViewStyle] this will help you to customize the action view of the post [LMFeedPostActionView]
 * @property postTopicsGroupStyle: [LMFeedChipGroupStyle] this will help you to customize the chip group in the post [LMFeedChipGroup]
 * @property postTopicChipsStyle: [LMFeedChipStyle] this will help you to customize the chips of the chip group in the post [LMFeedChip]
 *
 * */
class LMFeedPostViewStyle private constructor(
    //post header style
    val postHeaderViewStyle: LMFeedPostHeaderViewStyle,
    // post text content style
    val postContentTextStyle: LMFeedTextStyle,
    //post media style
    val postMediaViewStyle: LMFeedPostMediaViewStyle,
    //post action style
    val postActionViewStyle: LMFeedPostActionViewStyle,
    //post topics chip group style
    val postTopicsGroupStyle: LMFeedChipGroupStyle,
    //post topics chip style
    val postTopicChipsStyle: LMFeedChipStyle
) : LMFeedViewStyle {

    class Builder {
        private var postHeaderViewStyle: LMFeedPostHeaderViewStyle =
            LMFeedPostHeaderViewStyle.Builder()
                .timestampTextStyle(
                    LMFeedTextStyle.Builder()
                        .textColor(R.color.lm_feed_grey)
                        .textSize(R.dimen.lm_feed_text_small)
                        .fontResource(R.font.lm_feed_roboto)
                        .build()
                )
                .postEditedTextStyle(
                    LMFeedTextStyle.Builder()
                        .textColor(R.color.lm_feed_grey)
                        .textSize(R.dimen.lm_feed_text_small)
                        .fontResource(R.font.lm_feed_roboto)
                        .maxLines(1)
                        .build()
                )
                .authorCustomTitleTextStyle(
                    LMFeedTextStyle.Builder()
                        .textColor(R.color.lm_feed_white)
                        .textSize(R.dimen.lm_feed_text_small)
                        .maxLines(1)
                        .fontResource(R.font.lm_feed_roboto_medium)
                        .backgroundColor(LMFeedTheme.getButtonColor())
                        .build()
                )
                .pinIconStyle(
                    LMFeedIconStyle.Builder()
                        .inActiveSrc(R.drawable.lm_feed_ic_pin)
                        .build()
                )
                .menuIconStyle(
                    LMFeedIconStyle.Builder()
                        .inActiveSrc(R.drawable.lm_feed_ic_overflow_menu)
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

        private var postContentTextStyle: LMFeedTextStyle =
            LMFeedTextStyle.Builder()
                .textColor(R.color.lm_feed_grey)
                .textSize(R.dimen.lm_feed_text_large)
                .maxLines(3)
                .fontResource(R.font.lm_feed_roboto)
                .expandableCTAText("... See more")
                .expandableCTAColor(R.color.lm_feed_brown_grey)
                .build()

        private var postActionViewStyle: LMFeedPostActionViewStyle =
            LMFeedPostActionViewStyle.Builder()
                .likeIconStyle(
                    LMFeedIconStyle.Builder()
                        .activeSrc(R.drawable.lm_feed_ic_like_filled)
                        .inActiveSrc(R.drawable.lm_feed_ic_like_unfilled)
                        .build()
                )
                .likeTextStyle(
                    LMFeedTextStyle.Builder()
                        .textColor(R.color.lm_feed_grey)
                        .textSize(R.dimen.lm_feed_text_medium)
                        .fontResource(R.font.lm_feed_roboto)
                        .textAllCaps(false)
                        .textAlignment(View.TEXT_ALIGNMENT_CENTER)
                        .build()
                )
                .commentTextStyle(
                    LMFeedTextStyle.Builder()
                        .textColor(R.color.lm_feed_grey)
                        .textSize(R.dimen.lm_feed_text_medium)
                        .fontResource(R.font.lm_feed_roboto)
                        .textAllCaps(false)
                        .textAlignment(View.TEXT_ALIGNMENT_CENTER)
                        .drawableLeftSrc(R.drawable.lm_feed_ic_comment)
                        .drawablePadding(R.dimen.lm_feed_regular_padding)
                        .build()
                )
                .saveIconStyle(
                    LMFeedIconStyle.Builder()
                        .activeSrc(R.drawable.lm_feed_ic_bookmark_filled)
                        .inActiveSrc(R.drawable.lm_feed_ic_bookmark_unfilled)
                        .build()
                )
                .shareIconStyle(
                    LMFeedIconStyle.Builder()
                        .inActiveSrc(R.drawable.lm_feed_ic_share)
                        .build()
                )
                .build()

        private var postMediaViewStyle: LMFeedPostMediaViewStyle =
            LMFeedPostMediaViewStyle.Builder()
                .postImageMediaStyle(
                    LMFeedPostImageMediaViewStyle.Builder()
                        .imageStyle(
                            LMFeedImageStyle.Builder()
                                .placeholderSrc(LMFeedViewUtils.getShimmer())
                                .scaleType(ImageView.ScaleType.CENTER_CROP)
                                .build()
                        )
                        .removeIconStyle(null)
                        .build()
                )
                .postVideoMediaStyle(
                    LMFeedPostVideoMediaViewStyle.Builder()
                        .videoProgressStyle(
                            LMFeedProgressBarStyle.Builder()
                                .progressColor(LMFeedTheme.getButtonColor())
                                .build()
                        )
                        .backgroundColor(R.color.lm_feed_black)
                        .build()
                )
                .postLinkStyle(
                    LMFeedPostLinkMediaViewStyle.Builder()
                        .linkTitleStyle(
                            LMFeedTextStyle.Builder()
                                .fontResource(R.font.lm_feed_roboto_medium)
                                .textColor(R.color.lm_feed_grey)
                                .textSize(R.dimen.lm_feed_text_medium)
                                .build()
                        )
                        .linkDescriptionStyle(
                            LMFeedTextStyle.Builder()
                                .fontResource(R.font.lm_feed_roboto)
                                .textColor(R.color.lm_feed_grey)
                                .textSize(R.dimen.lm_feed_text_medium)
                                .build()
                        )
                        .linkBoxStrokeWidth(R.dimen.lm_feed_link_box_stroke_width)
                        .linkBoxStrokeColor(R.color.lm_feed_light_grayish_blue)
                        .linkBoxElevation(R.dimen.lm_feed_zero_dp)
                        .linkBoxCornerRadius(R.dimen.lm_feed_link_box_corner_radius)
                        .build()
                )
                .postDocumentsMediaStyle(
                    LMFeedPostDocumentsMediaViewStyle.Builder()
                        .documentIconStyle(
                            LMFeedIconStyle.Builder()
                                .inActiveSrc(R.drawable.lm_feed_ic_pdf)
                                .scaleType(ImageView.ScaleType.FIT_XY)
                                .build()
                        )
                        .documentPageCountStyle(
                            LMFeedTextStyle.Builder()
                                .fontResource(R.font.lm_feed_roboto)
                                .textColor(R.color.lm_feed_grey)
                                .textSize(R.dimen.lm_feed_text_medium)
                                .build()
                        )
                        .documentSizeStyle(
                            LMFeedTextStyle.Builder()
                                .fontResource(R.font.lm_feed_roboto)
                                .textColor(R.color.lm_feed_grey)
                                .textSize(R.dimen.lm_feed_text_medium)
                                .build()
                        )
                        .documentTypeStyle(
                            LMFeedTextStyle.Builder()
                                .fontResource(R.font.lm_feed_roboto)
                                .textColor(R.color.lm_feed_grey)
                                .textSize(R.dimen.lm_feed_text_medium)
                                .build()
                        )
                        .documentShowMoreStyle(
                            LMFeedTextStyle.Builder()
                                .fontResource(R.font.lm_feed_roboto)
                                .textColor(LMFeedTheme.getButtonColor())
                                .textSize(R.dimen.lm_feed_text_large)
                                .build()
                        )
                        .build()
                )
                .postMultipleMediaStyle(
                    LMFeedPostMultipleMediaViewStyle.Builder()
                        .indicatorSpacing(R.dimen.lm_feed_indicator_dots_spacing)
                        .build()
                )
                .postPollMediaStyle(
                    LMFeedPostPollViewStyle.Builder()
                        .pollInfoTextStyle(
                            LMFeedTextStyle.Builder()
                                .fontResource(R.font.lm_feed_roboto)
                                .textSize(R.dimen.lm_feed_text_medium)
                                .textColor(R.color.lm_feed_dusty_grey)
                                .build()
                        )
                        .submitPollVoteButtonStyle(
                            LMFeedButtonStyle.Builder()
                                .textStyle(
                                    LMFeedTextStyle.Builder()
                                        .textColor(R.color.lm_feed_white)
                                        .textSize(R.dimen.lm_feed_text_large)
                                        .fontResource(R.font.lm_feed_roboto)
                                        .build()
                                )
                                .backgroundColor(LMFeedTheme.getButtonColor())
                                .cornerRadius(R.dimen.lm_feed_corner_radius_medium)
                                .build()
                        )
                        .addPollOptionButtonStyle(
                            LMFeedButtonStyle.Builder()
                                .textStyle(
                                    LMFeedTextStyle.Builder()
                                        .textSize(R.dimen.lm_feed_text_large)
                                        .fontResource(R.font.lm_feed_roboto)
                                        .textColor(R.color.lm_feed_dark_grayish_blue)
                                        .textAllCaps(false)
                                        .build()
                                )
                                .cornerRadius(R.dimen.lm_feed_corner_radius_medium)
                                .strokeColor(R.color.lm_feed_dusty_grey_20)
                                .backgroundColor(R.color.lm_feed_white)
                                .build()
                        )
                        .membersVotedCountTextStyle(
                            LMFeedTextStyle.Builder()
                                .fontResource(R.font.lm_feed_roboto)
                                .textSize(R.dimen.lm_feed_text_medium)
                                .textColor(LMFeedTheme.getButtonColor())
                                .build()
                        )
                        .pollExpiryTextStyle(
                            LMFeedTextStyle.Builder()
                                .fontResource(R.font.lm_feed_roboto)
                                .textSize(R.dimen.lm_feed_text_medium)
                                .textColor(R.color.lm_feed_grey)
                                .build()
                        )
                        .pollOptionsViewStyle(
                            LMFeedPostPollOptionViewStyle.Builder()
                                .pollOptionVotesCountTextStyle(
                                    LMFeedTextStyle.Builder()
                                        .textSize(R.dimen.lm_feed_text_medium)
                                        .textColor(R.color.lm_feed_dusty_grey)
                                        .build()
                                )
                                .pollOptionAddedByTextStyle(
                                    LMFeedTextStyle.Builder()
                                        .fontResource(R.font.lm_feed_roboto)
                                        .textSize(R.dimen.lm_feed_text_small)
                                        .textColor(R.color.lm_feed_grey)
                                        .ellipsize(TextUtils.TruncateAt.END)
                                        .build()
                                )
                                .pollOptionCheckIconStyle(
                                    LMFeedIconStyle.Builder()
                                        .iconTint(LMFeedTheme.getButtonColor())
                                        .inActiveSrc(R.drawable.lm_feed_ic_check_circle)
                                        .build()
                                )
                                .build()
                        )
                        .editPollVoteTextStyle(
                            LMFeedTextStyle.Builder()
                                .textColor(LMFeedTheme.getButtonColor())
                                .fontResource(R.font.lm_feed_roboto)
                                .textSize(R.dimen.lm_feed_text_medium)
                                .ellipsize(TextUtils.TruncateAt.END)
                                .build()
                        )
                        .build()
                )
                .postVerticalVideoMediaStyle(
                    LMFeedPostVideoMediaViewStyle.Builder()
                        .videoProgressStyle(
                            LMFeedProgressBarStyle.Builder()
                                .progressColor(LMFeedTheme.getButtonColor())
                                .build()
                        )
                        .backgroundColor(R.color.lm_feed_black)
                        .build()
                )
                .build()

        private var postTopicsGroupStyle: LMFeedChipGroupStyle =
            LMFeedChipGroupStyle.Builder()
                .build()

        private var postTopicChipStyle: LMFeedChipStyle =
            LMFeedChipStyle.Builder()
                .chipBackgroundColor(R.color.lm_feed_majorelle_blue_10)
                .chipCornerRadius(R.dimen.lm_feed_corner_radius_regular)
                .chipTextColor(LMFeedTheme.getButtonColor())
                .chipTextSize(R.dimen.lm_feed_text_medium)
                .build()

        fun postHeaderViewStyle(postHeaderViewStyle: LMFeedPostHeaderViewStyle) = apply {
            this.postHeaderViewStyle = postHeaderViewStyle
        }

        fun postContentTextStyle(postContentTextStyle: LMFeedTextStyle) = apply {
            this.postContentTextStyle = postContentTextStyle
        }

        fun postMediaViewStyle(postMediaViewStyle: LMFeedPostMediaViewStyle) = apply {
            this.postMediaViewStyle = postMediaViewStyle
        }

        fun postActionViewStyle(postActionViewStyle: LMFeedPostActionViewStyle) = apply {
            this.postActionViewStyle = postActionViewStyle
        }

        fun postTopicsGroupStyle(postTopicsGroupStyle: LMFeedChipGroupStyle) = apply {
            this.postTopicsGroupStyle = postTopicsGroupStyle
        }

        fun postTopicChipStyle(postTopicChipStyle: LMFeedChipStyle) = apply {
            this.postTopicChipStyle = postTopicChipStyle
        }

        fun build() = LMFeedPostViewStyle(
            postHeaderViewStyle,
            postContentTextStyle,
            postMediaViewStyle,
            postActionViewStyle,
            postTopicsGroupStyle,
            postTopicChipStyle
        )
    }

    fun toBuilder(): Builder {
        return Builder().postHeaderViewStyle(postHeaderViewStyle)
            .postContentTextStyle(postContentTextStyle)
            .postMediaViewStyle(postMediaViewStyle)
            .postActionViewStyle(postActionViewStyle)
            .postTopicsGroupStyle(postTopicsGroupStyle)
            .postTopicChipStyle(postTopicChipsStyle)
    }
}