package com.likeminds.feed.android.core.universalfeed.adapter.databinders

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feed.android.core.databinding.LmFeedItemPostMultipleMediaBinding
import com.likeminds.feed.android.core.universalfeed.adapter.LMFeedUniversalFeedAdapterListener
import com.likeminds.feed.android.core.universalfeed.model.LMFeedPostViewData
import com.likeminds.feed.android.core.universalfeed.util.LMFeedPostBinderUtils
import com.likeminds.feed.android.core.utils.base.LMFeedViewDataBinder
import com.likeminds.feed.android.core.utils.base.model.ITEM_POST_MULTIPLE_MEDIA

class LMFeedItemPostMultipleMediaViewDataBinder(
    private val universalFeedAdapterListener: LMFeedUniversalFeedAdapterListener
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
            LMFeedPostBinderUtils.customizePostHeaderView(
                postHeader,
                universalFeedAdapterListener,
                user
            )

            LMFeedPostBinderUtils.customizePostContentView(
                tvPostContent,
                universalFeedAdapterListener,
                (postId ?: "")
            )

            LMFeedPostBinderUtils.customizePostFooterView(
                postFooter,
                universalFeedAdapterListener,
                (postId ?: ""),
                position
            )
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
            postId = data.id
            user = data.headerViewData.user

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
                universalFeedAdapterListener,
                returnBinder = {
                    return@setPostBindData
                }, executeBinder = {
                    // todo: initialize media view here
                }
            )
        }
    }
}