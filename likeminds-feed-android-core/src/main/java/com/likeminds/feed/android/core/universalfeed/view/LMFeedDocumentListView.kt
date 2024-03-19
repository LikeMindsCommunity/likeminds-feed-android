package com.likeminds.feed.android.core.universalfeed.view

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.*
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.ui.base.views.LMFeedTextView
import com.likeminds.feed.android.core.universalfeed.adapter.LMFeedDocumentsAdapter
import com.likeminds.feed.android.core.universalfeed.adapter.LMFeedUniversalFeedAdapterListener
import com.likeminds.feed.android.core.universalfeed.model.LMFeedMediaViewData
import com.likeminds.feed.android.core.utils.LMFeedStyleTransformer
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.hide
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.show

class LMFeedDocumentListView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    private val linearLayoutManager: LinearLayoutManager
    private val dividerDecoration: DividerItemDecoration =
        DividerItemDecoration(context, DividerItemDecoration.VERTICAL)

    private lateinit var documentsAdapter: LMFeedDocumentsAdapter

    init {
        setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(context)
        layoutManager = linearLayoutManager

        //item decorator to add spacing between items
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

    //sets the adapter with the provided [listener] to the recycler view and handles the visible documents logic
    fun setAdapter(
        parentPosition: Int,
        mediaViewData: LMFeedMediaViewData,
        tvShowMore: LMFeedTextView,
        listener: LMFeedUniversalFeedAdapterListener
    ) {
        documentsAdapter = LMFeedDocumentsAdapter(parentPosition, listener)
        adapter = documentsAdapter
        handleVisibleDocuments(mediaViewData, tvShowMore)
    }

    private fun handleVisibleDocuments(
        mediaViewData: LMFeedMediaViewData,
        tvShowMore: LMFeedTextView,
    ) {
        //get documents media style
        val postDocumentsMediaStyle =
            LMFeedStyleTransformer.postViewStyle.postMediaStyle.postDocumentsMediaStyle ?: return

        //gets the limit of visible documents
        val visibleDocumentsLimit = postDocumentsMediaStyle.visibleDocumentsLimit

        val documents = mediaViewData.attachments

        //handle show more logic
        if (mediaViewData.isExpanded || documents.size <= visibleDocumentsLimit) {
            tvShowMore.hide()
            documentsAdapter.replace(documents)
        } else {
            tvShowMore.show()
            "+${documents.size - visibleDocumentsLimit} more".also { tvShowMore.text = it }
            documentsAdapter.replace(documents.take(visibleDocumentsLimit))
        }

        //if show more style is not provided then we will not show this view
        postDocumentsMediaStyle.documentShowMoreStyle ?: tvShowMore.hide()
    }
}