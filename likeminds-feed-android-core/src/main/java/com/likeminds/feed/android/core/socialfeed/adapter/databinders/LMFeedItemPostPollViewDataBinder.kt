package com.likeminds.feed.android.core.socialfeed.adapter.databinders

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feed.android.core.databinding.LmFeedItemPostPollBinding
import com.likeminds.feed.android.core.poll.result.model.LMFeedPollOptionViewData
import com.likeminds.feed.android.core.ui.widgets.poll.adapter.LMFeedPollOptionsAdapterListener
import com.likeminds.feed.android.core.socialfeed.adapter.LMFeedSocialFeedAdapterListener
import com.likeminds.feed.android.core.socialfeed.model.LMFeedPostViewData
import com.likeminds.feed.android.core.socialfeed.util.LMFeedPostBinderUtils
import com.likeminds.feed.android.core.utils.LMFeedStyleTransformer
import com.likeminds.feed.android.core.utils.base.LMFeedViewDataBinder
import com.likeminds.feed.android.core.utils.base.model.ITEM_POST_POLL

class LMFeedItemPostPollViewDataBinder(
    private val socialFeedAdapterListener: LMFeedSocialFeedAdapterListener,
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

            LMFeedPostBinderUtils.customizePostActionHorizontalView(postFooter)

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
                socialFeedAdapterListener.onPostMenuIconClicked(
                    position,
                    postHeader.headerMenu,
                    post
                )
            }

            postHeader.setAuthorFrameClickListener {
                val post = this.postViewData ?: return@setAuthorFrameClickListener
                socialFeedAdapterListener.onPostAuthorHeaderClicked(position, post)
            }

            postPollView.setPollTitleClicked {
                val post = this.postViewData ?: return@setPollTitleClicked
                socialFeedAdapterListener.onPostPollTitleClicked(position, post)
            }

            postPollView.setEditPollClicked {
                val post = this.postViewData ?: return@setEditPollClicked
                socialFeedAdapterListener.onPostEditPollClicked(position, post)
            }

            postPollView.setClearPollClicked {
                val post = this.postViewData ?: return@setClearPollClicked
                socialFeedAdapterListener.onPostClearPollClicked(position, post)
            }

            postPollView.setAddPollOptionClicked {
                val post = this.postViewData ?: return@setAddPollOptionClicked
                socialFeedAdapterListener.onPostAddPollOptionClicked(position, post)
            }

            postPollView.setSubmitPollVoteClicked {
                val post = this.postViewData ?: return@setSubmitPollVoteClicked
                socialFeedAdapterListener.onPostSubmitPollVoteClicked(position, post)
            }

            postPollView.setMemberVotedCountClicked {
                val post = this.postViewData ?: return@setMemberVotedCountClicked
                socialFeedAdapterListener.onPostMemberVotedCountClicked(position, post)
            }

            postPollView.setEditPollVoteClicked {
                val post = this.postViewData ?: return@setEditPollVoteClicked
                socialFeedAdapterListener.onPostEditPollVoteClicked(position, post)
            }

            postFooter.setLikeIconClickListener {
                val post = this.postViewData ?: return@setLikeIconClickListener
                val updatedPost = LMFeedPostBinderUtils.updatePostForLike(post)
                socialFeedAdapterListener.onPostLikeClicked(position, updatedPost)
            }

            postFooter.setLikesCountClickListener {
                val post = this.postViewData ?: return@setLikesCountClickListener
                if (post.footerViewData.likesCount > 0) {
                    socialFeedAdapterListener.onPostLikesCountClicked(position, post)
                } else {
                    return@setLikesCountClickListener
                }
            }

            postFooter.setCommentsCountClickListener {
                val post = this.postViewData ?: return@setCommentsCountClickListener
                socialFeedAdapterListener.onPostCommentsCountClicked(position, post)
            }

            postFooter.setSaveIconListener {
                val post = this.postViewData ?: return@setSaveIconListener
                val updatedPost = LMFeedPostBinderUtils.updatePostForSave(post)
                socialFeedAdapterListener.onPostSaveClicked(position, updatedPost)
            }

            postFooter.setShareIconListener {
                val post = this.postViewData ?: return@setShareIconListener
                socialFeedAdapterListener.onPostShareClicked(position, post)
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

        socialFeedAdapterListener.onPollOptionClicked(
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

        socialFeedAdapterListener.onPollOptionVoteCountClicked(
            pollPosition,
            pollOptionPosition,
            pollOptionViewData
        )
    }
}