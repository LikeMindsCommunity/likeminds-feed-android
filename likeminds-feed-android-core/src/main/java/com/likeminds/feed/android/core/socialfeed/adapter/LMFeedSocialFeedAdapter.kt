package com.likeminds.feed.android.core.socialfeed.adapter

import com.likeminds.feed.android.core.socialfeed.adapter.databinders.*
import com.likeminds.feed.android.core.utils.LMFeedValueUtils.getItemInList
import com.likeminds.feed.android.core.utils.base.*

class LMFeedSocialFeedAdapter(
    private val postAdapterListener: LMFeedPostAdapterListener,
) : LMFeedBaseRecyclerAdapter<LMFeedBaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): MutableList<LMFeedViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<LMFeedViewDataBinder<*, *>>(7)

        val itemPostTextOnlyBinder =
            LMFeedItemPostTextOnlyViewDataBinder(postAdapterListener)
        viewDataBinders.add(itemPostTextOnlyBinder)

        val itemPostSingleImageViewDataBinder =
            LMFeedItemPostSingleImageViewDataBinder(postAdapterListener)
        viewDataBinders.add(itemPostSingleImageViewDataBinder)

        val itemPostSingleVideoViewDataBinder =
            LMFeedItemPostSingleVideoViewDataBinder(postAdapterListener)
        viewDataBinders.add(itemPostSingleVideoViewDataBinder)

        val itemPostLinkViewDataBinder =
            LMFeedItemPostLinkViewDataBinder(postAdapterListener)
        viewDataBinders.add(itemPostLinkViewDataBinder)

        val itemPostDocumentsViewDataBinder =
            LMFeedItemPostDocumentsViewDataBinder(postAdapterListener)
        viewDataBinders.add(itemPostDocumentsViewDataBinder)

        val itemPostMultipleMediaViewDataBinder =
            LMFeedItemPostMultipleMediaViewDataBinder(postAdapterListener)
        viewDataBinders.add(itemPostMultipleMediaViewDataBinder)

        val itemPostPollViewDataBinder =
            LMFeedItemPostPollViewDataBinder(postAdapterListener)
        viewDataBinders.add(itemPostPollViewDataBinder)

        val itemPostCustomWidgetViewDataBinder =
            LMFeedItemPostCustomWidgetViewDataBinder(postAdapterListener)
        viewDataBinders.add(itemPostCustomWidgetViewDataBinder)

        return viewDataBinders
    }

    operator fun get(position: Int): LMFeedBaseViewType? {
        return items().getItemInList(position)
    }

    //replace any view data binder provide by the customer
    fun replaceViewDataBinder(viewType: Int, viewDataBinder: LMFeedViewDataBinder<*, *>) {
        supportedViewBinderResolverMap.put(viewType, viewDataBinder)
    }
}

