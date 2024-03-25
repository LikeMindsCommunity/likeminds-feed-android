package com.likeminds.feed.android.core.post.detail.adapter.databinders

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.databinding.LmFeedItemNoCommentsFoundBinding
import com.likeminds.feed.android.core.utils.LMFeedStyleTransformer
import com.likeminds.feed.android.core.utils.base.LMFeedBaseViewType
import com.likeminds.feed.android.core.utils.base.LMFeedViewDataBinder
import com.likeminds.feed.android.core.utils.base.model.ITEM_NO_COMMENTS_FOUND

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

            root.setBackgroundColor(noCommentFoundStyle.backgroundColor)
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
            setTitleText(context.getString(R.string.lm_feed_no_comments_found))
            setSubtitleText(context.getString(R.string.lm_feed_be_the_first_one_to_comment))
        }
    }
}