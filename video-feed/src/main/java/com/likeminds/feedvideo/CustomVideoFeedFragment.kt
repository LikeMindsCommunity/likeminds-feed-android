package com.likeminds.feedvideo

import android.util.Log
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.likeminds.feed.android.core.post.model.CUSTOM_WIDGET
import com.likeminds.feed.android.core.socialfeed.model.LMFeedPostViewData
import com.likeminds.feed.android.core.ui.widgets.post.postmedia.view.LMFeedPostVerticalVideoMediaView
import com.likeminds.feed.android.core.utils.base.LMFeedDataBoundViewHolder
import com.likeminds.feed.android.core.utils.base.model.ITEM_POST_VIDEO_FEED
import com.likeminds.feed.android.core.videofeed.adapter.LMFeedVideoFeedAdapter
import com.likeminds.feed.android.core.videofeed.view.LMFeedVideoFeedFragment
import com.likeminds.feedvideo.databinding.ItemCustomReelsViewDataBinderBinding

class CustomVideoFeedFragment : LMFeedVideoFeedFragment(), InvestClickListener {

    override fun customizeVideoFeedListView(
        vp2VideoFeed: ViewPager2,
        videoFeedAdapter: LMFeedVideoFeedAdapter
    ) {
        val customReelsViewDataBinder = CustomReelsViewDataBinder(this, this)
        videoFeedAdapter.replaceViewDataBinder(ITEM_POST_VIDEO_FEED, customReelsViewDataBinder)
    }

    override fun onInvestIconClick(postViewData: LMFeedPostViewData) {
        val widgetViewData = postViewData.mediaViewData.attachments.firstOrNull {
            it.attachmentType == CUSTOM_WIDGET
        }?.attachmentMeta?.widgetViewData
        Log.d("PUI", "widgetViewData ${widgetViewData?.metadata}")
    }

    override fun replaceVideoView(position: Int): LMFeedPostVerticalVideoMediaView? {
        //get the video feed binding to play the view in [postVideoView]
        val videoFeedBinding =
            ((binding.vp2VideoFeed[0] as? RecyclerView)?.findViewHolderForAdapterPosition(position) as? LMFeedDataBoundViewHolder<*>)
                ?.binding as? ItemCustomReelsViewDataBinderBinding ?: return null

        return (videoFeedBinding.postVideoView)
    }
}