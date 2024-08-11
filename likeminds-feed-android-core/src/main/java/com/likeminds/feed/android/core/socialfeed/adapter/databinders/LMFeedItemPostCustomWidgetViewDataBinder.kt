package com.likeminds.feed.android.core.socialfeed.adapter.databinders

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feed.android.core.databinding.LmFeedItemPostCustomWidgetBinding
import com.likeminds.feed.android.core.post.model.CUSTOM_WIDGET
import com.likeminds.feed.android.core.socialfeed.adapter.LMFeedPostAdapterListener
import com.likeminds.feed.android.core.socialfeed.model.LMFeedPostViewData
import com.likeminds.feed.android.core.socialfeed.util.LMFeedPostBinderUtils
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.hide
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.show
import com.likeminds.feed.android.core.utils.base.LMFeedViewDataBinder
import com.likeminds.feed.android.core.utils.base.model.ITEM_POST_CUSTOM_WIDGET

class LMFeedItemPostCustomWidgetViewDataBinder(
    private val universalFeedAdapterListener: LMFeedPostAdapterListener
) : LMFeedViewDataBinder<LmFeedItemPostCustomWidgetBinding, LMFeedPostViewData>() {
    override val viewType: Int
        get() = ITEM_POST_CUSTOM_WIDGET

    override fun createBinder(parent: ViewGroup): LmFeedItemPostCustomWidgetBinding {
        val binding = LmFeedItemPostCustomWidgetBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        binding.apply {
            LMFeedPostBinderUtils.customizePostHeaderView(postHeader)

            LMFeedPostBinderUtils.customizePostContentView(tvPostContent)

            LMFeedPostBinderUtils.customizePostActionHorizontalView(postAction)

            LMFeedPostBinderUtils.customizePostTopicsGroup(postTopicsGroup)

            setClickListeners(this)
        }

        return binding
    }

    override fun bindData(
        binding: LmFeedItemPostCustomWidgetBinding,
        data: LMFeedPostViewData,
        position: Int
    ) {
        binding.apply {
            this.position = position
            postViewData = data

            // updates the data in the post action view
            LMFeedPostBinderUtils.setPostHorizontalActionViewData(
                postAction,
                data.actionViewData
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
                }, executeBinder = {}
            )

            //get custom widget attachment
            val customWidgetData = data.mediaViewData.attachments.find {
                it.attachmentType == CUSTOM_WIDGET
            }
            if (customWidgetData != null) {
                //get widget metadata
                val widgetData = customWidgetData.attachmentMeta.widgetViewData
                if (widgetData != null) {
                    //convert metadata to string
                    val metadata = widgetData.metadata.toString()
                    binding.tvPostCw.show()
                    binding.tvPostCw.text = metadata
                } else {
                    binding.tvPostCw.hide()
                }
            } else {
                binding.tvPostCw.hide()
            }
        }
    }

    private fun setClickListeners(binding: LmFeedItemPostCustomWidgetBinding) {
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
                val post = this.postViewData ?: return@setAuthorFrameClickListener
                universalFeedAdapterListener.onPostAuthorHeaderClicked(position, post)
            }

            postAction.setLikeIconClickListener {
                val post = this.postViewData ?: return@setLikeIconClickListener
                val updatedPost = LMFeedPostBinderUtils.updatePostForLike(post)
                universalFeedAdapterListener.onPostLikeClicked(position, updatedPost)
            }

            postAction.setLikesCountClickListener {
                val post = this.postViewData ?: return@setLikesCountClickListener
                if (post.actionViewData.likesCount > 0) {
                    universalFeedAdapterListener.onPostLikesCountClicked(position, post)
                } else {
                    return@setLikesCountClickListener
                }
            }

            postAction.setCommentsCountClickListener {
                val post = this.postViewData ?: return@setCommentsCountClickListener
                universalFeedAdapterListener.onPostCommentsCountClicked(position, post)
            }

            postAction.setSaveIconListener {
                val post = this.postViewData ?: return@setSaveIconListener
                val updatedPost = LMFeedPostBinderUtils.updatePostForSave(post)
                universalFeedAdapterListener.onPostSaveClicked(position, updatedPost)
            }

            postAction.setShareIconListener {
                val post = this.postViewData ?: return@setShareIconListener
                universalFeedAdapterListener.onPostShareClicked(position, post)
            }
        }
    }
}