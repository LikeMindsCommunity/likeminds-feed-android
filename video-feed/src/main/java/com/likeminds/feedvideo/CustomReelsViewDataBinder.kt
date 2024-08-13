package com.likeminds.feedvideo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.socialfeed.adapter.LMFeedPostAdapterListener
import com.likeminds.feed.android.core.socialfeed.model.LMFeedPostViewData
import com.likeminds.feed.android.core.socialfeed.util.LMFeedPostBinderUtils
import com.likeminds.feed.android.core.utils.LMFeedStyleTransformer
import com.likeminds.feed.android.core.utils.LMFeedValueUtils.getFormatedNumber
import com.likeminds.feed.android.core.utils.base.PostItemViewDataBinder
import com.likeminds.feed.android.core.utils.base.model.ITEM_POST_VIDEO_FEED
import com.likeminds.feedvideo.databinding.ItemCustomReelsViewDataBinderBinding

class CustomReelsViewDataBinder(
    private val postAdapterListener: LMFeedPostAdapterListener,
    private val investClickListener: InvestClickListener
) : PostItemViewDataBinder<ItemCustomReelsViewDataBinderBinding>(postAdapterListener) {

    override val viewType: Int
        get() = ITEM_POST_VIDEO_FEED

    override fun createBinder(parent: ViewGroup): ItemCustomReelsViewDataBinderBinding {
        val binding = ItemCustomReelsViewDataBinderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        binding.apply {
            LMFeedPostBinderUtils.customizePostHeaderView(postHeader)

            LMFeedPostBinderUtils.customizePostContentView(tvPostContent)

            //set video media style to post video view
            val postVerticalVideoMediaStyle =
                LMFeedStyleTransformer.postViewStyle.postMediaViewStyle.postVerticalVideoMediaStyle
                    ?: return@apply

            postVideoView.setStyle(postVerticalVideoMediaStyle)

            setListener(this)
        }

        return binding
    }

    fun setListener(
        binding: ItemCustomReelsViewDataBinderBinding,
    ) {
        binding.apply {
            postHeader.setAuthorFrameClickListener {
                val post = this.postViewData ?: return@setAuthorFrameClickListener
                postAdapterListener.onPostAuthorHeaderClicked(position, post)
            }

            ivLike.setOnClickListener {
                val post = this.postViewData ?: return@setOnClickListener
                val updatedPost = LMFeedPostBinderUtils.updatePostForLike(post)
                postAdapterListener.onPostLikeClicked(position, updatedPost)
            }

            ivPostMenu.setOnClickListener {
                val post = this.postViewData ?: return@setOnClickListener
                postAdapterListener.onPostActionMenuClicked(position, post)
            }

            ivInvest.setOnClickListener {
                val post = this.postViewData ?: return@setOnClickListener
                investClickListener.onInvestIconClick(post)
            }
        }
    }

    override fun bindData(
        binding: ItemCustomReelsViewDataBinderBinding,
        data: LMFeedPostViewData,
        position: Int
    ) {
        binding.apply {
            this.position = position
            postViewData = data

            val iconStyle = LMFeedStyleTransformer.postViewStyle.postActionViewStyle.likeIconStyle

            val likeIcon = if (data.actionViewData.isLiked) {
                iconStyle.activeSrc
            } else {
                iconStyle.inActiveSrc
            }

            if (likeIcon != null) {
                binding.ivLike.setImageDrawable(
                    ContextCompat.getDrawable(
                        root.context,
                        likeIcon
                    )
                )
            }

            val likesCount = data.actionViewData.likesCount

            val likesCountText = if (likesCount == 0) {
                root.context.getString(R.string.lm_feed_like)
            } else {
                likesCount.toLong().getFormatedNumber()
            }

            tvLikesCount.text = likesCountText

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