package com.likeminds.feed.android.core.post.detail.adapter.databinders

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.databinding.LmFeedItemCommentsCountBinding
import com.likeminds.feed.android.core.post.detail.model.LMFeedCommentsCountViewData
import com.likeminds.feed.android.core.ui.base.styles.setStyle
import com.likeminds.feed.android.core.utils.LMFeedCommunityUtil
import com.likeminds.feed.android.core.utils.LMFeedStyleTransformer
import com.likeminds.feed.android.core.utils.LMFeedValueUtils.pluralizeOrCapitalize
import com.likeminds.feed.android.core.utils.base.LMFeedViewDataBinder
import com.likeminds.feed.android.core.utils.base.model.ITEM_COMMENTS_COUNT
import com.likeminds.feed.android.core.utils.pluralize.model.LMFeedWordAction

class LMFeedItemCommentsCountViewDataBinder :
    LMFeedViewDataBinder<LmFeedItemCommentsCountBinding, LMFeedCommentsCountViewData>() {

    override val viewType: Int
        get() = ITEM_COMMENTS_COUNT

    override fun createBinder(parent: ViewGroup): LmFeedItemCommentsCountBinding {
        val binding = LmFeedItemCommentsCountBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        binding.tvCommentsCount.setStyle(LMFeedStyleTransformer.postDetailFragmentViewStyle.commentsCountViewStyle)

        return binding
    }

    override fun bindData(
        binding: LmFeedItemCommentsCountBinding,
        data: LMFeedCommentsCountViewData,
        position: Int
    ) {
        val context = binding.root.context
        val commentString = if (data.commentsCount == 1) {
            LMFeedCommunityUtil.getCommentVariable()
                .pluralizeOrCapitalize(LMFeedWordAction.FIRST_LETTER_CAPITAL_SINGULAR)
        } else {
            LMFeedCommunityUtil.getCommentVariable()
                .pluralizeOrCapitalize(LMFeedWordAction.FIRST_LETTER_CAPITAL_PLURAL)
        }

        binding.tvCommentsCount.text = context.resources.getQuantityString(
            R.plurals.lm_feed_s_comments,
            data.commentsCount,
            data.commentsCount,
            commentString
        )
    }
}