package com.likeminds.feed.android.core.post.model

import androidx.annotation.IntDef

const val IMAGE = 1
const val VIDEO = 2
const val DOCUMENT = 3
const val LINK = 4
const val POLL = 6

@IntDef(
    IMAGE,
    VIDEO,
    DOCUMENT,
    LINK,
    POLL
)
@Retention(AnnotationRetention.SOURCE)
annotation class LMFeedAttachmentType