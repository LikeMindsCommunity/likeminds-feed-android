package com.likeminds.feed.android.core.socialfeed.adapter.databinders

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feed.android.core.databinding.LmFeedItemPostPollBinding
import com.likeminds.feed.android.core.poll.result.model.LMFeedPollOptionViewData
import com.likeminds.feed.android.core.ui.widgets.poll.adapter.LMFeedPollOptionsAdapterListener
import com.likeminds.feed.android.core.socialfeed.adapter.LMFeedPostAdapterListener
import com.likeminds.feed.android.core.socialfeed.model.LMFeedPostViewData
import com.likeminds.feed.android.core.socialfeed.util.LMFeedPostBinderUtils
import com.likeminds.feed.android.core.utils.LMFeedStyleTransformer
import com.likeminds.feed.android.core.utils.base.LMFeedViewDataBinder
import com.likeminds.feed.android.core.utils.base.model.ITEM_POST_POLL

class LMFeedItemPostPollViewDataBinder(
    private val postAdapterListener: LMFeedPostAdapterListener,
) : LMFeedViewDataBinder<LmFeedItemPostPollBinding, LMFeedPostViewData>(),
    LMFeedPollOptionsAdapterListener {

    override val viewType: Int
        get() = ITEM_POST_POLL

    override fun createBinder(parent: ViewGroup): LmFeedItemPostPollBinding {
        val binding = LmFeedItemPostPollBinding.inflate(
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

            //sets poll media style to the poll view
            val postPollMediaViewStyle =
                LMFeedStyleTransformer.postViewStyle.postMediaViewStyle.postPollMediaStyle
                    ?: return@apply

            postPollView.setStyle(postPollMediaViewStyle)
        }

        return binding
    }

    override fun bindData(
        binding: LmFeedItemPostPollBinding,
        data: LMFeedPostViewData,
        position: Int
    ) {
        binding.apply {
            // set variables in the binding
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
                postAdapterListener,
                returnBinder = {
                    return@setPostBindData
                }, executeBinder = {
                    //sets the post poll media view
                    LMFeedPostBinderUtils.bindPostPollMediaView(
                        position,
                        postPollView,
                        data.mediaViewData,
                        this@LMFeedItemPostPollViewDataBinder
                    )
                }
            )
        }
    }

    private fun setClickListeners(binding: LmFeedItemPostPollBinding) {
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
                val post = this.postViewData ?: return@setAuthorFrameClickListener
                postAdapterListener.onPostAuthorHeaderClicked(position, post)
            }

            postPollView.setPollTitleClicked {
                val post = this.postViewData ?: return@setPollTitleClicked
                postAdapterListener.onPostPollTitleClicked(position, post)
            }

            postPollView.setEditPollClicked {
                val post = this.postViewData ?: return@setEditPollClicked
                postAdapterListener.onPostEditPollClicked(position, post)
            }

            postPollView.setClearPollClicked {
                val post = this.postViewData ?: return@setClearPollClicked
                postAdapterListener.onPostClearPollClicked(position, post)
            }

            postPollView.setAddPollOptionClicked {
                val post = this.postViewData ?: return@setAddPollOptionClicked
                postAdapterListener.onPostAddPollOptionClicked(position, post)
            }

            postPollView.setSubmitPollVoteClicked {
                val post = this.postViewData ?: return@setSubmitPollVoteClicked
                postAdapterListener.onPostSubmitPollVoteClicked(position, post)
            }

            postPollView.setMemberVotedCountClicked {
                val post = this.postViewData ?: return@setMemberVotedCountClicked
                postAdapterListener.onPostMemberVotedCountClicked(position, post)
            }

            postPollView.setEditPollVoteClicked {
                val post = this.postViewData ?: return@setEditPollVoteClicked
                postAdapterListener.onPostEditPollVoteClicked(position, post)
            }

            postAction.setLikeIconClickListener {
                val post = this.postViewData ?: return@setLikeIconClickListener
                val updatedPost = LMFeedPostBinderUtils.updatePostForLike(post)
                postAdapterListener.onPostLikeClicked(position, updatedPost)
            }

            postAction.setLikesCountClickListener {
                val post = this.postViewData ?: return@setLikesCountClickListener
                if (post.actionViewData.likesCount > 0) {
                    postAdapterListener.onPostLikesCountClicked(position, post)
                } else {
                    return@setLikesCountClickListener
                }
            }

            postAction.setCommentsCountClickListener {
                val post = this.postViewData ?: return@setCommentsCountClickListener
                postAdapterListener.onPostCommentsCountClicked(position, post)
            }

            postAction.setSaveIconListener {
                val post = this.postViewData ?: return@setSaveIconListener
                val updatedPost = LMFeedPostBinderUtils.updatePostForSave(post)
                postAdapterListener.onPostSaveClicked(position, updatedPost)
            }

            postAction.setShareIconListener {
                val post = this.postViewData ?: return@setShareIconListener
                postAdapterListener.onPostShareClicked(position, post)
            }
        }
    }

    override fun onPollOptionClicked(
        pollPosition: Int,
        pollOptionPosition: Int,
        pollOptionViewData: LMFeedPollOptionViewData
    ) {
        super.onPollOptionClicked(
            pollPosition,
            pollOptionPosition,
            pollOptionViewData
        )

        postAdapterListener.onPollOptionClicked(
            pollPosition,
            pollOptionPosition,
            pollOptionViewData
        )
    }

    override fun onPollOptionVoteCountClicked(
        pollPosition: Int,
        pollOptionPosition: Int,
        pollOptionViewData: LMFeedPollOptionViewData
    ) {
        super.onPollOptionVoteCountClicked(
            pollPosition,
            pollOptionPosition,
            pollOptionViewData
        )

        postAdapterListener.onPollOptionVoteCountClicked(
            pollPosition,
            pollOptionPosition,
            pollOptionViewData
        )
    }
}