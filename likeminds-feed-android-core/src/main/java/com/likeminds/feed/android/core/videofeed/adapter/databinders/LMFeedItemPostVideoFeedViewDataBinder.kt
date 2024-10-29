package com.likeminds.feed.android.core.videofeed.adapter.databinders

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feed.android.core.databinding.LmFeedItemPostVideoFeedBinding
import com.likeminds.feed.android.core.socialfeed.adapter.LMFeedPostAdapterListener
import com.likeminds.feed.android.core.socialfeed.model.LMFeedPostViewData
import com.likeminds.feed.android.core.socialfeed.util.LMFeedPostBinderUtils
import com.likeminds.feed.android.core.utils.LMFeedStyleTransformer
import com.likeminds.feed.android.core.utils.base.LMFeedViewDataBinder
import com.likeminds.feed.android.core.utils.base.model.ITEM_POST_VIDEO_FEED

class LMFeedItemPostVideoFeedViewDataBinder(
    private val postAdapterListener: LMFeedPostAdapterListener
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
            LMFeedPostBinderUtils.customizePostHeaderView(postHeader)

            LMFeedPostBinderUtils.customizePostContentView(tvPostContent)

            LMFeedPostBinderUtils.customizePostActionVerticalView(postActionView)

            //set video media style to post video view
            val postVerticalVideoMediaStyle =
                LMFeedStyleTransformer.postViewStyle.postMediaViewStyle.postVerticalVideoMediaStyle
                    ?: return@apply

            postVideoView.setStyle(postVerticalVideoMediaStyle)

            setClickListeners(this)
        }

        return binding
    }

    override fun bindData(
        binding: LmFeedItemPostVideoFeedBinding,
        data: LMFeedPostViewData,
        position: Int
    ) {
        binding.apply {
            this.position = position
            postViewData = data

            // updates the data in the post action view
            LMFeedPostBinderUtils.setPostVerticalActionViewData(
                postActionView,
                data.actionViewData
            )

            // checks whether to bind complete data or not and execute corresponding lambda function
            LMFeedPostBinderUtils.setPostBindData(
                postHeader,
                tvPostHeading,
                tvPostContent,
                null,
                null,
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

    //sets the required click listeners to the binding
    private fun setClickListeners(binding: LmFeedItemPostVideoFeedBinding) {
        binding.apply {
            postHeader.setAuthorFrameClickListener {
                val post = this.postViewData ?: return@setAuthorFrameClickListener
                postAdapterListener.onPostAuthorHeaderClicked(position, post)
            }

            postActionView.setLikeIconClickListener {
                val post = this.postViewData ?: return@setLikeIconClickListener
                val updatedPost = LMFeedPostBinderUtils.updatePostForLike(post)
                postAdapterListener.onPostLikeClicked(position, updatedPost)
            }

            postActionView.setMenuIconListener {
                val post = this.postViewData ?: return@setMenuIconListener
                postAdapterListener.onPostActionMenuClicked(position, post)
            }
        }
    }
}