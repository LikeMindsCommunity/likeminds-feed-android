package com.likeminds.feed.android.core.likes.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feed.android.core.databinding.LmFeedItemLikesBinding
import com.likeminds.feed.android.core.likes.adapter.LMFeedLikesAdapterListener
import com.likeminds.feed.android.core.likes.model.LMFeedLikeViewData
import com.likeminds.feed.android.core.utils.LMFeedStyleTransformer
import com.likeminds.feed.android.core.utils.base.LMFeedViewDataBinder
import com.likeminds.feed.android.core.utils.base.model.ITEM_LIKES_SCREEN

class LMFeedItemLikesViewDataBinder(
    private val listener: LMFeedLikesAdapterListener
) : LMFeedViewDataBinder<LmFeedItemLikesBinding, LMFeedLikeViewData>() {

    override val viewType: Int
        get() = ITEM_LIKES_SCREEN

    override fun createBinder(parent: ViewGroup): LmFeedItemLikesBinding {
        val binding = LmFeedItemLikesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        binding.userView.setStyle(LMFeedStyleTransformer.likesFragmentViewStyle.userViewStyle)
        setClickListeners(binding)

        return binding
    }

    override fun bindData(
        binding: LmFeedItemLikesBinding,
        data: LMFeedLikeViewData,
        position: Int
    ) {
        binding.apply {
            //set data to the binding
            this.position = position
            likesViewData = data

            userView.setUserImage(data.user)
            userView.setUserName(data.user.name)
            userView.setUserTitle(data.user.customTitle)
        }
    }

    //sets click listeners to the likes view
    private fun setClickListeners(binding: LmFeedItemLikesBinding) {
        binding.apply {
            root.setOnClickListener {
                val likesData = likesViewData ?: return@setOnClickListener
                listener.onUserLikeItemClicked(position, likesData)
            }
        }
    }
}