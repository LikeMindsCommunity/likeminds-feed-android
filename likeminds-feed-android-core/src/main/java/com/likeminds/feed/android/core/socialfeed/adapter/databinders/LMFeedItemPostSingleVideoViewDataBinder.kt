package com.likeminds.feed.android.core.socialfeed.adapter.databinders

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feed.android.core.databinding.LmFeedItemPostSingleVideoBinding
import com.likeminds.feed.android.core.socialfeed.adapter.LMFeedPostAdapterListener
import com.likeminds.feed.android.core.socialfeed.model.LMFeedPostViewData
import com.likeminds.feed.android.core.socialfeed.util.LMFeedPostBinderUtils
import com.likeminds.feed.android.core.utils.LMFeedStyleTransformer
import com.likeminds.feed.android.core.utils.base.LMFeedViewDataBinder
import com.likeminds.feed.android.core.utils.base.model.ITEM_POST_SINGLE_VIDEO

class LMFeedItemPostSingleVideoViewDataBinder(
    private val postAdapterListener: LMFeedPostAdapterListener
) : LMFeedViewDataBinder<LmFeedItemPostSingleVideoBinding, LMFeedPostViewData>() {

    override val viewType: Int
        get() = ITEM_POST_SINGLE_VIDEO

    override fun createBinder(parent: ViewGroup): LmFeedItemPostSingleVideoBinding {
        val binding = LmFeedItemPostSingleVideoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        binding.apply {
            LMFeedPostBinderUtils.customizePostHeaderView(postHeader)

            LMFeedPostBinderUtils.customizePostContentView(tvPostContent)

            LMFeedPostBinderUtils.customizePostActionHorizontalView(postFooter)

            LMFeedPostBinderUtils.customizePostTopicsGroup(postTopicsGroup)

            setClickListeners(this)

            //set video media style to post video view
            val postVideoMediaStyle =
                LMFeedStyleTransformer.postViewStyle.postMediaViewStyle.postVideoMediaStyle
                    ?: return@apply

            postVideoView.setStyle(postVideoMediaStyle)
        }

        return binding
    }

    override fun bindData(
        binding: LmFeedItemPostSingleVideoBinding,
        data: LMFeedPostViewData,
        position: Int
    ) {
        binding.apply {
            // set variables in the binding
            this.position = position
            postViewData = data

            // updates the data in the post action view
            LMFeedPostBinderUtils.setPostFooterViewData(
                postFooter,
                data.footerViewData
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

    private fun setClickListeners(binding: LmFeedItemPostSingleVideoBinding) {
        binding.apply {
            postHeader.setMenuIconClickListener {
                val post = postViewData ?: return@setMenuIconClickListener
                postAdapterListener.onPostMenuIconClicked(
                    position,
                    postHeader.headerMenu,
                    post
                )
            }

            postHeader.setAuthorFrameClickListener {
                val post = postViewData ?: return@setAuthorFrameClickListener
                postAdapterListener.onPostAuthorHeaderClicked(position, post)
            }

            postVideoView.setOnClickListener {
                val post = postViewData ?: return@setOnClickListener
                postAdapterListener.onPostVideoMediaClicked(position, post)
            }

            postFooter.setLikeIconClickListener {
                val post = postViewData ?: return@setLikeIconClickListener
                val updatedPost = LMFeedPostBinderUtils.updatePostForLike(post)
                postAdapterListener.onPostLikeClicked(position, updatedPost)
            }

            postFooter.setLikesCountClickListener {
                val post = postViewData ?: return@setLikesCountClickListener
                if (post.footerViewData.likesCount > 0) {
                    postAdapterListener.onPostLikesCountClicked(position, post)
                } else {
                    return@setLikesCountClickListener
                }
            }

            postFooter.setCommentsCountClickListener {
                val post = postViewData ?: return@setCommentsCountClickListener
                postAdapterListener.onPostCommentsCountClicked(position, post)
            }

            postFooter.setSaveIconListener {
                val post = postViewData ?: return@setSaveIconListener
                val updatedPost = LMFeedPostBinderUtils.updatePostForSave(post)
                postAdapterListener.onPostSaveClicked(position, updatedPost)
            }

            postFooter.setShareIconListener {
                val post = postViewData ?: return@setShareIconListener
                postAdapterListener.onPostShareClicked(position, post)
            }
        }
    }
}