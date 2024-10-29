package com.likeminds.feed.android.core.socialfeed.adapter.databinders

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feed.android.core.*
import com.likeminds.feed.android.core.databinding.LmFeedItemPostSingleImageBinding
import com.likeminds.feed.android.core.socialfeed.adapter.LMFeedPostAdapterListener
import com.likeminds.feed.android.core.socialfeed.model.LMFeedPostViewData
import com.likeminds.feed.android.core.socialfeed.util.LMFeedPostBinderUtils
import com.likeminds.feed.android.core.utils.LMFeedStyleTransformer
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.hide
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.show
import com.likeminds.feed.android.core.utils.base.LMFeedViewDataBinder
import com.likeminds.feed.android.core.utils.base.model.ITEM_POST_SINGLE_IMAGE

class LMFeedItemPostSingleImageViewDataBinder(
    private val postAdapterListener: LMFeedPostAdapterListener
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

            LMFeedPostBinderUtils.customizePostHeadingView(tvPostHeading)

            LMFeedPostBinderUtils.customizePostContentView(tvPostContent)

            when (LMFeedCoreApplication.selectedTheme) {
                LMFeedTheme.SOCIAL_FEED -> {
                    LMFeedPostBinderUtils.customizePostActionHorizontalView(postAction)
                }

                LMFeedTheme.QNA_FEED -> {
                    LMFeedPostBinderUtils.customizePostQnAActionHorizontalView(qnaPostAction)
                }

                else -> {
                    LMFeedPostBinderUtils.customizePostActionHorizontalView(postAction)
                }
            }

            LMFeedPostBinderUtils.customizePostTopicsGroup(postTopicsGroup)

            LMFeedPostBinderUtils.customizePostTopResponseView(postTopResponse)

            LMFeedPostBinderUtils.customizePostQnAAnswerPromptView(containerQnaBeFirstLabel)

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

            // updates the data in the post action view
            when (LMFeedCoreApplication.selectedTheme) {
                LMFeedTheme.SOCIAL_FEED -> {
                    qnaPostAction.hide()
                    postAction.show()

                    LMFeedPostBinderUtils.setPostHorizontalActionViewData(
                        postAction,
                        data.actionViewData
                    )
                }

                LMFeedTheme.QNA_FEED -> {
                    postAction.hide()
                    qnaPostAction.show()

                    LMFeedPostBinderUtils.setPostQnAHorizontalActionViewData(
                        qnaPostAction,
                        data.actionViewData
                    )
                }

                else -> {
                    qnaPostAction.hide()
                    postAction.show()

                    LMFeedPostBinderUtils.setPostHorizontalActionViewData(
                        postAction,
                        data.actionViewData
                    )
                }
            }

            // checks whether to bind complete data or not and execute corresponding lambda function
            LMFeedPostBinderUtils.setPostBindData(
                postHeader,
                tvPostHeading,
                tvPostContent,
                postTopResponse,
                containerQnaBeFirstLabel,
                data,
                position,
                postTopicsGroup,
                postAdapterListener,
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

            postImageView.setOnClickListener {
                val post = postViewData ?: return@setOnClickListener
                postAdapterListener.onPostImageMediaClicked(position, post)
            }

            postAction.setLikeIconClickListener {
                val post = postViewData ?: return@setLikeIconClickListener
                val updatedPost = LMFeedPostBinderUtils.updatePostForLike(post)
                postAdapterListener.onPostLikeClicked(position, updatedPost)
            }

            postAction.setLikesCountClickListener {
                val post = postViewData ?: return@setLikesCountClickListener
                if (post.actionViewData.likesCount > 0) {
                    postAdapterListener.onPostLikesCountClicked(position, post)
                } else {
                    return@setLikesCountClickListener
                }
            }

            postAction.setCommentsCountClickListener {
                val post = postViewData ?: return@setCommentsCountClickListener
                postAdapterListener.onPostCommentsCountClicked(position, post)
            }

            postAction.setSaveIconListener {
                val post = postViewData ?: return@setSaveIconListener
                val updatedPost = LMFeedPostBinderUtils.updatePostForSave(post)
                postAdapterListener.onPostSaveClicked(position, updatedPost)
            }

            postAction.setShareIconListener {
                val post = postViewData ?: return@setShareIconListener
                postAdapterListener.onPostShareClicked(position, post)
            }

            qnaPostAction.setUpvoteIconClickListener {
                val post = this.postViewData ?: return@setUpvoteIconClickListener
                val updatedPost = LMFeedPostBinderUtils.updatePostForLike(post)
                postAdapterListener.onPostLikeClicked(position, updatedPost)
            }

            qnaPostAction.setUpvoteCountClickListener {
                val post = this.postViewData ?: return@setUpvoteCountClickListener
                if (post.actionViewData.likesCount > 0) {
                    postAdapterListener.onPostLikesCountClicked(position, post)
                } else {
                    return@setUpvoteCountClickListener
                }
            }

            qnaPostAction.setCommentsCountClickListener {
                val post = this.postViewData ?: return@setCommentsCountClickListener
                postAdapterListener.onPostCommentsCountClicked(position, post)
            }

            qnaPostAction.setSaveIconListener {
                val post = this.postViewData ?: return@setSaveIconListener
                val updatedPost = LMFeedPostBinderUtils.updatePostForSave(post)
                postAdapterListener.onPostSaveClicked(position, updatedPost)
            }

            qnaPostAction.setShareIconListener {
                val post = this.postViewData ?: return@setShareIconListener
                postAdapterListener.onPostShareClicked(position, post)
            }

            postTopResponse.setTopResponseClickListener {
                val post = this.postViewData ?: return@setTopResponseClickListener
                postAdapterListener.onPostTopResponseClicked(position, post)
            }

            postTopResponse.setAuthorFrameClickListener {
                val post = this.postViewData ?: return@setAuthorFrameClickListener
                postAdapterListener.onPostTopResponseAuthorFrameCLicked(position, post)
            }

            containerQnaBeFirstLabel.setContainerClickListener {
                val post = this.postViewData ?: return@setContainerClickListener
                postAdapterListener.onPostAnswerPromptClicked(position, post)
            }
        }
    }
}