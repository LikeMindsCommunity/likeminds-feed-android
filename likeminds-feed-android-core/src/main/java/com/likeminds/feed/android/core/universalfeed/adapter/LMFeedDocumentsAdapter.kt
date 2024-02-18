package com.likeminds.feed.android.core.universalfeed.adapter

import com.likeminds.feed.android.core.universalfeed.adapter.databinders.postdocuments.LMFeedItemDocumentViewDataBinder
import com.likeminds.feed.android.core.util.base.*

class LMFeedDocumentsAdapter(
    private val universalFeedAdapterListener: LMFeedUniversalFeedAdapterListener
) : LMFeedBaseRecyclerAdapter<LMFeedBaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): MutableList<LMFeedViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<LMFeedViewDataBinder<*, *>>(1)

        val documentsBinder = LMFeedItemDocumentViewDataBinder(universalFeedAdapterListener)
        viewDataBinders.add(documentsBinder)

        return viewDataBinders
    }
}