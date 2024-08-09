package com.likeminds.feed.android.core.videofeed.adapter.databinders

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.databinding.LmFeedItemPostVideoFeedBinding
import com.likeminds.feed.android.core.socialfeed.adapter.LMFeedPostAdapterListener
import com.likeminds.feed.android.core.socialfeed.model.LMFeedPostViewData
import com.likeminds.feed.android.core.socialfeed.util.LMFeedPostBinderUtils
import com.likeminds.feed.android.core.ui.base.styles.LMFeedIconStyle
import com.likeminds.feed.android.core.utils.LMFeedStyleTransformer
import com.likeminds.feed.android.core.utils.base.LMFeedViewDataBinder
import com.likeminds.feed.android.core.utils.base.model.ITEM_POST_VIDEO_FEED
import com.likeminds.feed.android.core.utils.video.LMFeedPostVideoPreviewAutoPlayHelper

class LMFeedItemPostVideoFeedViewDataBinder(
    private val postAdapterListener: LMFeedPostAdapterListener,
    private val postVideoPreviewAutoPlayHelper: LMFeedPostVideoPreviewAutoPlayHelper
) : LMFeedViewDataBinder<LmFeedItemPostVideoFeedBinding, LMFeedPostViewData>() {

    override val viewType: Int
        get() = ITEM_POST_VIDEO_FEED

    override fun createBinder(parent: ViewGroup): LmFeedItemPostVideoFeedBinding {
        val binding = LmFeedItemPostVideoFeedBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        binding.apply {
            val postViewStyle = LMFeedStyleTransformer.postViewStyle
            val postHeaderViewStyle = postViewStyle.postHeaderViewStyle

            val verticalVideoPostHeaderViewStyle = postHeaderViewStyle.toBuilder()
                .authorNameViewStyle(
                    postHeaderViewStyle.authorNameViewStyle.toBuilder()
                        .textColor(R.color.lm_feed_white)
                        .build()
                )
                .postEditedTextStyle(
                    postHeaderViewStyle.postEditedTextStyle?.toBuilder()
                        ?.textColor(R.color.lm_feed_white)
                        ?.build()
                )
                .timestampTextStyle(
                    postHeaderViewStyle.timestampTextStyle?.toBuilder()
                        ?.textColor(R.color.lm_feed_white)
                        ?.build()
                )
                .backgroundColor(android.R.color.transparent)
                .menuIconStyle(null)
                .pinIconStyle(null)
                .build()

            LMFeedPostBinderUtils.customizePostHeaderView(
                postHeader,
                verticalVideoPostHeaderViewStyle
            )

            val verticalVideoPostContentTextStyle = postViewStyle.postContentTextStyle.toBuilder()
                .textColor(R.color.lm_feed_white)
                // todo: this needs to be handle properly
                .maxLines(1)
                .build()

            LMFeedPostBinderUtils.customizePostContentView(
                tvPostContent,
                verticalVideoPostContentTextStyle
            )

            val postActionViewStyle = LMFeedStyleTransformer.postViewStyle.postActionViewStyle

            val verticalPostActionViewStyle = postActionViewStyle.toBuilder()
                .commentTextStyle(null)
                .shareIconStyle(null)
                .likeIconStyle(
                    postActionViewStyle.likeIconStyle.toBuilder()
                        .inActiveSrc(R.drawable.lm_feed_ic_like_white)
                        .build()
                )
                .menuIconStyle(
                    LMFeedIconStyle.Builder()
                        .inActiveSrc(R.drawable.lm_feed_ic_overflow_menu_white)
                        .build()
                )
                .build()

            LMFeedPostBinderUtils.customizePostActionVerticalView(
                postActionView,
                verticalPostActionViewStyle
            )

            //set video media style to post video view
            val postVerticalVideoMediaStyle =
                LMFeedStyleTransformer.postViewStyle.postMediaViewStyle.postVerticalVideoMediaStyle
                    ?: return@apply

            postVideoView.setStyle(postVerticalVideoMediaStyle)
        }

        return binding
    }

    override fun bindData(
        binding: LmFeedItemPostVideoFeedBinding,
        data: LMFeedPostViewData,
        position: Int
    ) {
        binding.apply {
            // updates the data in the post action view
            LMFeedPostBinderUtils.setPostVerticalActionViewData(
                postActionView,
                data.actionViewData
            )

            // checks whether to bind complete data or not and execute corresponding lambda function
            LMFeedPostBinderUtils.setPostBindData(
                postHeader,
                tvPostContent,
                data,
                position,
                postTopicsGroup,
                postAdapterListener,
                returnBinder = {
                    return@setPostBindData
                }, executeBinder = {}
            )
        }
    }
}