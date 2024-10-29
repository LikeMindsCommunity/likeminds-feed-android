package com.likeminds.feed.android.core.post.detail.adapter.databinders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.databinding.LmFeedItemNoCommentsFoundBinding
import com.likeminds.feed.android.core.utils.LMFeedCommunityUtil
import com.likeminds.feed.android.core.utils.LMFeedStyleTransformer
import com.likeminds.feed.android.core.utils.LMFeedValueUtils.pluralizeOrCapitalize
import com.likeminds.feed.android.core.utils.base.LMFeedBaseViewType
import com.likeminds.feed.android.core.utils.base.LMFeedViewDataBinder
import com.likeminds.feed.android.core.utils.base.model.ITEM_NO_COMMENTS_FOUND
import com.likeminds.feed.android.core.utils.pluralize.model.LMFeedWordAction

class LMFeedItemNoCommentsFoundViewDataBinder :
    LMFeedViewDataBinder<LmFeedItemNoCommentsFoundBinding, LMFeedBaseViewType>() {

    override val viewType: Int
        get() = ITEM_NO_COMMENTS_FOUND

    override fun createBinder(parent: ViewGroup): LmFeedItemNoCommentsFoundBinding {
        val binding = LmFeedItemNoCommentsFoundBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        binding.apply {
            val noCommentFoundStyle =
                LMFeedStyleTransformer.postDetailFragmentViewStyle.noCommentsFoundViewStyle

            root.setBackgroundColor(
                ContextCompat.getColor(
                    root.context,
                    noCommentFoundStyle.backgroundColor
                )
            )
            layoutNoComments.setStyle(noCommentFoundStyle)
        }

        return binding
    }

    override fun bindData(
        binding: LmFeedItemNoCommentsFoundBinding,
        data: LMFeedBaseViewType,
        position: Int
    ) {
        //showing static data
        binding.layoutNoComments.apply {
            setTitleText(
                context.getString(
                    R.string.lm_feed_no_s_comment_found,
                    LMFeedCommunityUtil.getCommentVariable()
                        .pluralizeOrCapitalize(LMFeedWordAction.ALL_SMALL_PLURAL)
                )
            )
            setSubtitleText(
                context.getString(
                    R.string.lm_feed_be_the_first_one_to_create_a_s_comment,
                    LMFeedCommunityUtil.getCommentVariable()
                        .pluralizeOrCapitalize(LMFeedWordAction.ALL_SMALL_SINGULAR)
                )
            )
        }
    }
}