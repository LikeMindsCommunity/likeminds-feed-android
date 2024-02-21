package com.likeminds.feed.android.core.universalfeed.adapter.databinders.postdocuments

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.*
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.universalfeed.adapter.LMFeedDocumentsAdapter

class LMFeedDocumentListView(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int
) : RecyclerView(context, attrs, defStyleAttr) {

    private val linearLayoutManager: LinearLayoutManager
    private val dividerDecoration: DividerItemDecoration =
        DividerItemDecoration(context, DividerItemDecoration.VERTICAL)

    private lateinit var adapter: LMFeedDocumentsAdapter

    init {
        setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(context)
        layoutManager = linearLayoutManager

        // item decorator to add spacing between items
        val dividerDrawable = ContextCompat.getDrawable(
            context,
            R.drawable.lm_feed_document_item_divider
        )
        dividerDrawable?.let { drawable ->
            dividerDecoration.setDrawable(drawable)
        }

        // if separator is not there already, then only add
        if (itemDecorationCount < 1) {
            addItemDecoration(dividerDecoration)
        }
    }
}