package com.likeminds.feed.android.core.socialfeed.adapter.databinders

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feed.android.core.databinding.LmFeedItemPostLinkBinding
import com.likeminds.feed.android.core.socialfeed.adapter.LMFeedSocialFeedAdapterListener
import com.likeminds.feed.android.core.socialfeed.model.LMFeedPostViewData
import com.likeminds.feed.android.core.socialfeed.util.LMFeedPostBinderUtils
import com.likeminds.feed.android.core.utils.LMFeedStyleTransformer
import com.likeminds.feed.android.core.utils.base.LMFeedViewDataBinder
import com.likeminds.feed.android.core.utils.base.model.ITEM_POST_LINK

class LMFeedItemPostLinkViewDataBinder(
    private val socialFeedAdapterListener: LMFeedSocialFeedAdapterListener
) : LMFeedViewDataBinder<LmFeedItemPostLinkBinding, LMFeedPostViewData>() {

    override val viewType: Int
        get() = ITEM_POST_LINK

    override fun createBinder(parent: ViewGroup): LmFeedItemPostLinkBinding {
        val binding = LmFeedItemPostLinkBinding.inflate(
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

            //sets link media style to post link view
            val postLinkViewStyle =
                LMFeedStyleTransformer.postViewStyle.postMediaViewStyle.postLinkViewStyle
                    ?: return@apply

            postLinkView.setStyle(postLinkViewStyle)
        }

        return binding
    }

    override fun bindData(
        binding: LmFeedItemPostLinkBinding,
        data: LMFeedPostViewData,
        position: Int
    ) {
        binding.apply {
            val linkAttachment = data.mediaViewData.attachments.first()
            val ogTags = linkAttachment.attachmentMeta.ogTags

            //sets variables in the binding
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
                socialFeedAdapterListener,
                returnBinder = {
                    return@setPostBindData
                }, executeBinder = {
                    //handles the link view
                    LMFeedPostBinderUtils.bindPostMediaLinkView(
                        postLinkView,
                        ogTags
                    )
                }
            )
        }
    }

    private fun setClickListeners(binding: LmFeedItemPostLinkBinding) {
        binding.apply {
            postHeader.setMenuIconClickListener {
                val post = postViewData ?: return@setMenuIconClickListener
                socialFeedAdapterListener.onPostMenuIconClicked(
                    position,
                    postHeader.headerMenu,
                    post
                )
            }

            postHeader.setAuthorFrameClickListener {
                val post = postViewData ?: return@setAuthorFrameClickListener
                socialFeedAdapterListener.onPostAuthorHeaderClicked(position, post)
            }

            postLinkView.setLinkClickListener {
                val post = postViewData ?: return@setLinkClickListener
                socialFeedAdapterListener.onPostLinkMediaClicked(position, post)
            }

            postFooter.setLikeIconClickListener {
                val post = postViewData ?: return@setLikeIconClickListener
                val updatedPost = LMFeedPostBinderUtils.updatePostForLike(post)
                socialFeedAdapterListener.onPostLikeClicked(position, updatedPost)
            }

            postFooter.setLikesCountClickListener {
                val post = postViewData ?: return@setLikesCountClickListener
                if (post.footerViewData.likesCount > 0) {
                    socialFeedAdapterListener.onPostLikesCountClicked(position, post)
                } else {
                    return@setLikesCountClickListener
                }
            }

            postFooter.setCommentsCountClickListener {
                val post = postViewData ?: return@setCommentsCountClickListener
                socialFeedAdapterListener.onPostCommentsCountClicked(position, post)
            }

            postFooter.setSaveIconListener {
                val post = postViewData ?: return@setSaveIconListener
                val updatedPost = LMFeedPostBinderUtils.updatePostForSave(post)
                socialFeedAdapterListener.onPostSaveClicked(position, updatedPost)
            }

            postFooter.setShareIconListener {
                val post = postViewData ?: return@setShareIconListener
                socialFeedAdapterListener.onPostShareClicked(position, post)
            }
        }
    }
}