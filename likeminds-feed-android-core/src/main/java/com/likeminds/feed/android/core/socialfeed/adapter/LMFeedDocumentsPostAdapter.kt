package com.likeminds.feed.android.core.socialfeed.adapter

import com.likeminds.feed.android.core.socialfeed.adapter.databinders.postdocuments.LMFeedItemDocumentViewDataBinder
import com.likeminds.feed.android.core.utils.base.*

class LMFeedDocumentsAdapter(
    private val parentPosition: Int,
    private val universalFeedAdapterListener: LMFeedUniversalFeedAdapterListener,
    private val isMediaRemovable: Boolean = false
) : LMFeedBaseRecyclerAdapter<LMFeedBaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): MutableList<LMFeedViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<LMFeedViewDataBinder<*, *>>(1)

        val documentsBinder =
            LMFeedItemDocumentViewDataBinder(
                parentPosition,
                universalFeedAdapterListener,
                isMediaRemovable
            )
        viewDataBinders.add(documentsBinder)

        return viewDataBinders
    }
}