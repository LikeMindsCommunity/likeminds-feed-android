package com.likeminds.feed.android.core.socialfeed.adapter.databinders

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feed.android.core.databinding.LmFeedItemPostDocumentsBinding
import com.likeminds.feed.android.core.socialfeed.adapter.LMFeedSocialFeedAdapterListener
import com.likeminds.feed.android.core.socialfeed.model.LMFeedPostViewData
import com.likeminds.feed.android.core.socialfeed.util.LMFeedPostBinderUtils
import com.likeminds.feed.android.core.utils.LMFeedStyleTransformer
import com.likeminds.feed.android.core.utils.base.LMFeedViewDataBinder
import com.likeminds.feed.android.core.utils.base.model.ITEM_POST_DOCUMENTS

class LMFeedItemPostDocumentsViewDataBinder(
    private val socialFeedAdapterListener: LMFeedSocialFeedAdapterListener
) : LMFeedViewDataBinder<LmFeedItemPostDocumentsBinding, LMFeedPostViewData>() {

    override val viewType: Int
        get() = ITEM_POST_DOCUMENTS

    override fun createBinder(parent: ViewGroup): LmFeedItemPostDocumentsBinding {
        val binding = LmFeedItemPostDocumentsBinding.inflate(
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

            //sets documents media style to documents view
            val postDocumentsMediaViewStyle =
                LMFeedStyleTransformer.postViewStyle.postMediaViewStyle.postDocumentsMediaStyle
                    ?: return@apply

            postDocumentsMediaView.setStyle(postDocumentsMediaViewStyle)
        }

        return binding
    }

    override fun bindData(
        binding: LmFeedItemPostDocumentsBinding,
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
                socialFeedAdapterListener,
                returnBinder = {
                    return@setPostBindData
                }, executeBinder = {
                    //sets the documents media view
                    LMFeedPostBinderUtils.bindPostDocuments(
                        position,
                        postDocumentsMediaView,
                        data.mediaViewData,
                        socialFeedAdapterListener
                    )
                }
            )
        }
    }

    private fun setClickListeners(binding: LmFeedItemPostDocumentsBinding) {
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

            postDocumentsMediaView.setShowMoreTextClickListener {
                val post = postViewData ?: return@setShowMoreTextClickListener
                val updatedPostData = LMFeedPostBinderUtils.updatePostForDocumentExpanded(post)
                socialFeedAdapterListener.onPostMultipleDocumentsExpanded(
                    position,
                    updatedPostData
                )
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