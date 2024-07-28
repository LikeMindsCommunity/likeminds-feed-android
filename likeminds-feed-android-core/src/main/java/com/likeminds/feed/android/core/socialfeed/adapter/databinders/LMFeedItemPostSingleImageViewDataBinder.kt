package com.likeminds.feed.android.core.socialfeed.adapter.databinders

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feed.android.core.databinding.LmFeedItemPostSingleImageBinding
import com.likeminds.feed.android.core.socialfeed.adapter.LMFeedUniversalFeedAdapterListener
import com.likeminds.feed.android.core.socialfeed.model.LMFeedPostViewData
import com.likeminds.feed.android.core.socialfeed.util.LMFeedPostBinderUtils
import com.likeminds.feed.android.core.utils.LMFeedStyleTransformer
import com.likeminds.feed.android.core.utils.base.LMFeedViewDataBinder
import com.likeminds.feed.android.core.utils.base.model.ITEM_POST_SINGLE_IMAGE

class LMFeedItemPostSingleImageViewDataBinder(
    private val universalFeedAdapterListener: LMFeedUniversalFeedAdapterListener
) : LMFeedViewDataBinder<LmFeedItemPostSingleImageBinding, LMFeedPostViewData>() {

    override val viewType: Int
        get() = ITEM_POST_SINGLE_IMAGE

    override fun createBinder(parent: ViewGroup): LmFeedItemPostSingleImageBinding {
        val binding = LmFeedItemPostSingleImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        binding.apply {
            LMFeedPostBinderUtils.customizePostHeaderView(postHeader)

            LMFeedPostBinderUtils.customizePostContentView(tvPostContent)

            LMFeedPostBinderUtils.customizePostFooterView(postFooter)

            LMFeedPostBinderUtils.customizePostTopicsGroup(postTopicsGroup)

            setClickListeners(this)

            //set styles to the image media in the post
            val postImageMediaStyle =
                LMFeedStyleTransformer.postViewStyle.postMediaViewStyle.postImageMediaStyle
                    ?: return@apply

            postImageView.setStyle(postImageMediaStyle)
        }
        return binding
    }

    override fun bindData(
        binding: LmFeedItemPostSingleImageBinding,
        data: LMFeedPostViewData,
        position: Int
    ) {
        binding.apply {
            // set variables in the binding
            this.position = position
            postViewData = data

            // updates the data in the post footer view
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
                universalFeedAdapterListener,
                returnBinder = {
                    return@setPostBindData
                }, executeBinder = {
                    // binds the image to the single image post view
                    LMFeedPostBinderUtils.bindPostSingleImage(
                        postImageView,
                        data.mediaViewData
                    )
                }
            )
        }
    }

    private fun setClickListeners(binding: LmFeedItemPostSingleImageBinding) {
        binding.apply {
            postHeader.setMenuIconClickListener {
                val post = postViewData ?: return@setMenuIconClickListener
                universalFeedAdapterListener.onPostMenuIconClicked(
                    position,
                    postHeader.headerMenu,
                    post
                )
            }

            postHeader.setAuthorFrameClickListener {
                val post = postViewData ?: return@setAuthorFrameClickListener
                universalFeedAdapterListener.onPostAuthorHeaderClicked(position, post)
            }

            postImageView.setOnClickListener {
                val post = postViewData ?: return@setOnClickListener
                universalFeedAdapterListener.onPostImageMediaClicked(position, post)
            }

            postFooter.setLikeIconClickListener {
                val post = postViewData ?: return@setLikeIconClickListener
                val updatedPost = LMFeedPostBinderUtils.updatePostForLike(post)
                universalFeedAdapterListener.onPostLikeClicked(position, updatedPost)
            }

            postFooter.setLikesCountClickListener {
                val post = postViewData ?: return@setLikesCountClickListener
                if (post.footerViewData.likesCount > 0) {
                    universalFeedAdapterListener.onPostLikesCountClicked(position, post)
                } else {
                    return@setLikesCountClickListener
                }
            }

            postFooter.setCommentsCountClickListener {
                val post = postViewData ?: return@setCommentsCountClickListener
                universalFeedAdapterListener.onPostCommentsCountClicked(position, post)
            }

            postFooter.setSaveIconListener {
                val post = postViewData ?: return@setSaveIconListener
                val updatedPost = LMFeedPostBinderUtils.updatePostForSave(post)
                universalFeedAdapterListener.onPostSaveClicked(position, updatedPost)
            }

            postFooter.setShareIconListener {
                val post = postViewData ?: return@setShareIconListener
                universalFeedAdapterListener.onPostShareClicked(position, post)
            }
        }
    }
}