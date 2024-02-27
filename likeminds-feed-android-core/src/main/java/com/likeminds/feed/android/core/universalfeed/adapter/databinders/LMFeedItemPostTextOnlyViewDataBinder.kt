package com.likeminds.feed.android.core.universalfeed.adapter.databinders

import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.util.LinkifyCompat
import com.likeminds.feed.android.core.LMFeedCoreApplication
import com.likeminds.feed.android.core.databinding.LmFeedItemPostTextOnlyBinding
import com.likeminds.feed.android.core.universalfeed.adapter.LMFeedUniversalFeedAdapterListener
import com.likeminds.feed.android.core.universalfeed.model.LMFeedPostViewData
import com.likeminds.feed.android.core.universalfeed.util.LMFeedPostBinderUtils
import com.likeminds.feed.android.core.utils.base.LMFeedViewDataBinder
import com.likeminds.feed.android.core.utils.base.model.ITEM_POST_TEXT_ONLY
import com.likeminds.feed.android.core.utils.link.LMFeedLinkMovementMethod

class LMFeedItemPostTextOnlyViewDataBinder(
    private val universalFeedAdapterListener: LMFeedUniversalFeedAdapterListener
) : LMFeedViewDataBinder<LmFeedItemPostTextOnlyBinding, LMFeedPostViewData>() {

    override val viewType: Int
        get() = ITEM_POST_TEXT_ONLY

    override fun createBinder(parent: ViewGroup): LmFeedItemPostTextOnlyBinding {
        val binding = LmFeedItemPostTextOnlyBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        binding.apply {
            LMFeedPostBinderUtils.customizePostHeaderView(postHeader)

            LMFeedPostBinderUtils.customizePostContentView(tvPostContent)

            LMFeedPostBinderUtils.customizePostFooterView(postFooter)

            setClickListeners(this)
        }

        return binding
    }

    override fun bindData(
        binding: LmFeedItemPostTextOnlyBinding,
        data: LMFeedPostViewData,
        position: Int
    ) {
        binding.apply {
            this.position = position
            postViewData = data

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
                }, executeBinder = {}
            )
        }
    }

    private fun setClickListeners(binding: LmFeedItemPostTextOnlyBinding) {
        binding.apply {
            postHeader.setMenuIconClickListener {
                // todo: add required params and extend in the fragment
                universalFeedAdapterListener.onPostMenuIconClick()
            }

            // todo: test this otherwise move this to setTextContent function
            tvPostContent.setOnClickListener {
                val post = this.postViewData ?: return@setOnClickListener
                universalFeedAdapterListener.onPostContentClick(position, post)
            }

            val linkifyLinks =
                (Linkify.WEB_URLS or Linkify.EMAIL_ADDRESSES or Linkify.PHONE_NUMBERS)
            LinkifyCompat.addLinks(tvPostContent, linkifyLinks)
            tvPostContent.movementMethod = LMFeedLinkMovementMethod { url ->
                tvPostContent.setOnClickListener {
                    return@setOnClickListener
                }

                universalFeedAdapterListener.handleLinkClick(url)
                true
            }

            postHeader.setAuthorFrameClickListener {
                val post = this.postViewData ?: return@setAuthorFrameClickListener
                val coreCallback = LMFeedCoreApplication.getLMFeedCoreCallback()
                coreCallback?.openProfile(post.headerViewData.user)
            }

            postFooter.setLikeIconClickListener {
                val post = this.postViewData ?: return@setLikeIconClickListener
                universalFeedAdapterListener.onPostLikeClick(position, post)
            }

            postFooter.setLikesCountClickListener {
                val post = this.postViewData ?: return@setLikesCountClickListener
                universalFeedAdapterListener.onPostLikesCountClick(position, post)
            }

            postFooter.setCommentsCountClickListener {
                val post = this.postViewData ?: return@setCommentsCountClickListener
                universalFeedAdapterListener.onPostCommentsCountClick(position, post)
            }

            postFooter.setSaveIconListener {
                val post = this.postViewData ?: return@setSaveIconListener
                universalFeedAdapterListener.onPostSaveClick(position, post)
            }

            postFooter.setShareIconListener {
                val post = this.postViewData ?: return@setShareIconListener
                universalFeedAdapterListener.onPostShareClick(position, post)
            }
        }
    }
}