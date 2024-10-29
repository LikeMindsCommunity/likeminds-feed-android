package com.likeminds.feed.android.core.ui.widgets.post.postmedia.style

import android.text.TextUtils
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.ui.base.styles.LMFeedIconStyle
import com.likeminds.feed.android.core.ui.base.styles.LMFeedTextStyle
import com.likeminds.feed.android.core.ui.theme.LMFeedAppearance.DEFAULT_VISIBLE_DOCUMENTS_LIMIT
import com.likeminds.feed.android.core.utils.LMFeedViewStyle

class LMFeedPostDocumentsMediaViewStyle private constructor(
    val documentNameStyle: LMFeedTextStyle,
    val documentIconStyle: LMFeedIconStyle?,
    val documentPageCountStyle: LMFeedTextStyle?,
    val documentSizeStyle: LMFeedTextStyle?,
    val documentTypeStyle: LMFeedTextStyle?,
    val documentShowMoreStyle: LMFeedTextStyle?,
    val visibleDocumentsLimit: Int,
    val removeIconStyle: LMFeedIconStyle?
) : LMFeedViewStyle {

    class Builder {
        private var documentNameStyle: LMFeedTextStyle = LMFeedTextStyle.Builder()
            .maxLines(1)
            .textColor(R.color.lm_feed_grey)
            .textSize(R.dimen.lm_feed_text_large)
            .fontResource(R.font.lm_feed_roboto_medium)
            .ellipsize(TextUtils.TruncateAt.END)
            .build()

        private var documentIconStyle: LMFeedIconStyle? = null
        private var documentPageCountStyle: LMFeedTextStyle? = null
        private var documentSizeStyle: LMFeedTextStyle? = null
        private var documentTypeStyle: LMFeedTextStyle? = null
        private var documentShowMoreStyle: LMFeedTextStyle? = null
        private var removeIconStyle: LMFeedIconStyle? = null

        private var visibleDocumentsLimit: Int = DEFAULT_VISIBLE_DOCUMENTS_LIMIT

        fun documentNameStyle(documentNameStyle: LMFeedTextStyle) = apply {
            this.documentNameStyle = documentNameStyle
        }

        fun documentIconStyle(documentIconStyle: LMFeedIconStyle?) = apply {
            this.documentIconStyle = documentIconStyle
        }

        fun documentPageCountStyle(documentPageCountStyle: LMFeedTextStyle?) = apply {
            this.documentPageCountStyle = documentPageCountStyle
        }

        fun documentSizeStyle(documentSizeStyle: LMFeedTextStyle?) = apply {
            this.documentSizeStyle = documentSizeStyle
        }

        fun documentTypeStyle(documentTypeStyle: LMFeedTextStyle?) = apply {
            this.documentTypeStyle = documentTypeStyle
        }

        fun documentShowMoreStyle(documentShowMoreStyle: LMFeedTextStyle?) = apply {
            this.documentShowMoreStyle = documentShowMoreStyle
        }

        fun visibleDocumentsLimit(visibleDocumentsLimit: Int) = apply {
            this.visibleDocumentsLimit = visibleDocumentsLimit
        }

        fun removeIconStyle(removeIconStyle: LMFeedIconStyle?) = apply {
            this.removeIconStyle = removeIconStyle
        }

        fun build() = LMFeedPostDocumentsMediaViewStyle(
            documentNameStyle,
            documentIconStyle,
            documentPageCountStyle,
            documentSizeStyle,
            documentTypeStyle,
            documentShowMoreStyle,
            visibleDocumentsLimit,
            removeIconStyle
        )
    }

    fun toBuilder(): Builder {
        return Builder().documentNameStyle(documentNameStyle)
            .documentIconStyle(documentIconStyle)
            .documentPageCountStyle(documentPageCountStyle)
            .documentSizeStyle(documentSizeStyle)
            .documentTypeStyle(documentTypeStyle)
            .documentShowMoreStyle(documentShowMoreStyle)
            .visibleDocumentsLimit(visibleDocumentsLimit)
            .removeIconStyle(removeIconStyle)
    }
}