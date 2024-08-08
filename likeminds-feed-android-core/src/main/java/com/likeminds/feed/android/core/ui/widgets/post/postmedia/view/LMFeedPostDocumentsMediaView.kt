package com.likeminds.feed.android.core.ui.widgets.post.postmedia.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.likeminds.feed.android.core.databinding.LmFeedPostDocumentsMediaViewBinding
import com.likeminds.feed.android.core.ui.base.styles.LMFeedTextStyle
import com.likeminds.feed.android.core.ui.base.styles.setStyle
import com.likeminds.feed.android.core.ui.widgets.post.postmedia.style.LMFeedPostDocumentsMediaViewStyle
import com.likeminds.feed.android.core.socialfeed.adapter.LMFeedPostAdapterListener
import com.likeminds.feed.android.core.socialfeed.model.LMFeedMediaViewData
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.show
import com.likeminds.feed.android.core.utils.listeners.LMFeedOnClickListener

class LMFeedPostDocumentsMediaView : ConstraintLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet?, defStyle: Int) : super(
        context,
        attributeSet,
        defStyle
    )

    private val inflater =
        (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)

    private val binding = LmFeedPostDocumentsMediaViewBinding.inflate(inflater, this, true)

    //sets provided [postDocumentsMediaViewStyle] to the document type post
    fun setStyle(postDocumentsMediaViewStyle: LMFeedPostDocumentsMediaViewStyle) {

        //configures the show more text in the document type post
        configureShowMore(postDocumentsMediaViewStyle.documentShowMoreStyle)
    }

    private fun configureShowMore(documentShowMoreStyle: LMFeedTextStyle?) {
        binding.tvShowMore.apply {
            documentShowMoreStyle?.let {
                setStyle(documentShowMoreStyle)
                show()
            }
        }
    }

    fun setAdapter(
        parentPosition: Int,
        mediaData: LMFeedMediaViewData,
        listener: LMFeedPostAdapterListener,
        isMediaRemovable: Boolean = false
    ) {
        binding.apply {
            rvDocuments.setAdapter(
                parentPosition,
                mediaData,
                tvShowMore,
                listener,
                isMediaRemovable
            )
        }
    }

    /**
     * Removes the document at the provided index
     * @param position: index from which the media is to be removed
     */
    fun removeDocument(position: Int) {
        binding.rvDocuments.removeDocument(position)
    }

    fun setShowMoreTextClickListener(listener: LMFeedOnClickListener) {
        binding.tvShowMore.setOnClickListener {
            listener.onClick()
        }
    }
}