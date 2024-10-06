package com.likeminds.feed.android.core.post.detail.adapter.databinders

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.databinding.LmFeedItemReplyViewBinding
import com.likeminds.feed.android.core.post.detail.adapter.LMFeedReplyAdapterListener
import com.likeminds.feed.android.core.post.detail.model.LMFeedCommentViewData
import com.likeminds.feed.android.core.post.detail.util.LMFeedPostDetailBinderUtils
import com.likeminds.feed.android.core.utils.LMFeedCommunityUtil
import com.likeminds.feed.android.core.utils.LMFeedStyleTransformer
import com.likeminds.feed.android.core.utils.LMFeedValueUtils.pluralizeOrCapitalize
import com.likeminds.feed.android.core.utils.base.LMFeedViewDataBinder
import com.likeminds.feed.android.core.utils.base.model.ITEM_REPLY
import com.likeminds.feed.android.core.utils.pluralize.model.LMFeedWordAction

class LMFeedItemReplyViewDataBinder(
    private val replyAdapterListener: LMFeedReplyAdapterListener
) : LMFeedViewDataBinder<LmFeedItemReplyViewBinding, LMFeedCommentViewData>() {

    override val viewType: Int
        get() = ITEM_REPLY

    override fun createBinder(parent: ViewGroup): LmFeedItemReplyViewBinding {
        val binding = LmFeedItemReplyViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        binding.apply {
            replyView.setStyle(
                LMFeedStyleTransformer.postDetailFragmentViewStyle.replyViewStyle
            )
            setClickListeners(this)
        }

        return binding
    }

    override fun bindData(
        binding: LmFeedItemReplyViewBinding,
        data: LMFeedCommentViewData,
        position: Int
    ) {
        binding.apply {
            //set data to the binding
            this.position = position
            replyViewData = data

            val context = root.context

            replyView.setCommentCreatorImage(data.user)
            replyView.setCommentCreatorName(data.user.name)
            replyView.setCommentContent(
                data.text,
                data.alreadySeenFullContent,
                onCommentSeeMoreClickListener = {
                    val updatedReply =
                        LMFeedPostDetailBinderUtils.updateCommentForSeeFullContent(data)
                    replyAdapterListener.onReplyContentSeeMoreClicked(position, updatedReply)
                },
                onMemberTagClickListener = { uuid ->
                    replyAdapterListener.onReplyTaggedMemberClicked(position, uuid)
                }
            )
            replyView.setTimestamp(data.createdAt)
            replyView.setCommentEdited(data.isEdited)
            replyView.setCommentLikesIcon(data.isLiked)

            val likesCountText = if (data.likesCount == 0) {
                ""
            } else {
                val likeString = if (data.likesCount == 1) {
                    LMFeedCommunityUtil.getLikeVariable()
                        .pluralizeOrCapitalize(LMFeedWordAction.FIRST_LETTER_CAPITAL_SINGULAR)
                } else {
                    LMFeedCommunityUtil.getLikeVariable()
                        .pluralizeOrCapitalize(LMFeedWordAction.FIRST_LETTER_CAPITAL_PLURAL)
                }
                context.resources.getQuantityString(
                    R.plurals.lm_feed_s_likes,
                    data.likesCount,
                    data.likesCount,
                    likeString
                )
            }
            replyView.setLikesCount(likesCountText)
        }
    }

    private fun setClickListeners(binding: LmFeedItemReplyViewBinding) {
        binding.apply {
            replyView.linkifyCommentContent { url ->
                replyAdapterListener.onReplyContentLinkClicked(url)
                true
            }

            replyView.setLikesCountClickListener {
                val reply = replyViewData ?: return@setLikesCountClickListener
                if (reply.likesCount > 0) {
                    replyAdapterListener.onReplyLikesCountClicked(position, reply)
                } else {
                    return@setLikesCountClickListener
                }
            }

            replyView.setLikeIconClickListener {
                val reply = replyViewData ?: return@setLikeIconClickListener
                val updatedReply = LMFeedPostDetailBinderUtils.updateCommentForLike(reply)
                replyAdapterListener.onReplyLiked(position, updatedReply)
            }

            replyView.setMenuIconClickListener {
                val reply = replyViewData ?: return@setMenuIconClickListener
                replyAdapterListener.onReplyMenuIconClicked(
                    position,
                    replyView.commentMenu,
                    reply
                )
            }
        }
    }
}