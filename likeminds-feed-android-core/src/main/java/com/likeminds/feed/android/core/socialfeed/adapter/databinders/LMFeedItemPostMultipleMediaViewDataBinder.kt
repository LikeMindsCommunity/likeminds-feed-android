package com.likeminds.feed.android.core.socialfeed.adapter.databinders

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feed.android.core.LMFeedCore
import com.likeminds.feed.android.core.LMFeedTheme
import com.likeminds.feed.android.core.databinding.LmFeedItemPostMultipleMediaBinding
import com.likeminds.feed.android.core.socialfeed.adapter.LMFeedPostAdapterListener
import com.likeminds.feed.android.core.socialfeed.model.LMFeedPostViewData
import com.likeminds.feed.android.core.socialfeed.util.LMFeedPostBinderUtils
import com.likeminds.feed.android.core.utils.LMFeedStyleTransformer
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.hide
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.show
import com.likeminds.feed.android.core.utils.base.LMFeedViewDataBinder
import com.likeminds.feed.android.core.utils.base.model.ITEM_POST_MULTIPLE_MEDIA

class LMFeedItemPostMultipleMediaViewDataBinder(
    private val postAdapterListener: LMFeedPostAdapterListener
) : LMFeedViewDataBinder<LmFeedItemPostMultipleMediaBinding, LMFeedPostViewData>() {

    override val viewType: Int
        get() = ITEM_POST_MULTIPLE_MEDIA

    override fun createBinder(parent: ViewGroup): LmFeedItemPostMultipleMediaBinding {
        val binding = LmFeedItemPostMultipleMediaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        binding.apply {
            LMFeedPostBinderUtils.customizePostHeaderView(postHeader)

            LMFeedPostBinderUtils.customizePostHeadingView(tvPostHeading)

            LMFeedPostBinderUtils.customizePostContentView(tvPostContent)

            when (LMFeedCore.theme) {
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

            //sets link media style to multiple media view
            val postMultipleMediaViewStyle =
                LMFeedStyleTransformer.postViewStyle.postMediaViewStyle.postMultipleMediaStyle
                    ?: return@apply

            multipleMediaView.setStyle(postMultipleMediaViewStyle)
        }

        return binding
    }

    override fun bindData(
        binding: LmFeedItemPostMultipleMediaBinding,
        data: LMFeedPostViewData,
        position: Int
    ) {
        binding.apply {
            // set variables in the binding
            this.position = position
            postViewData = data

            // updates the data in the post action view
            when (LMFeedCore.theme) {
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
                    LMFeedPostBinderUtils.bindMultipleMediaView(
                        position,
                        multipleMediaView,
                        data.mediaViewData,
                        postAdapterListener
                    )
                }
            )
        }
    }

    private fun setClickListeners(binding: LmFeedItemPostMultipleMediaBinding) {
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

            containerQnaBeFirstLabel.setContainerClickListener {
                val post = this.postViewData ?: return@setContainerClickListener
                postAdapterListener.onPostAnswerPromptClicked(position, post)
            }
        }
    }
}