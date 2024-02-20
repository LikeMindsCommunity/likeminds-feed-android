package com.likeminds.feed.android.core.utils.base.model

import androidx.annotation.IntDef

const val ITEM_NONE = 0
const val ITEM_POST_TEXT_ONLY = 1
const val ITEM_POST_SINGLE_IMAGE = 2
const val ITEM_POST_SINGLE_VIDEO = 3
const val ITEM_POST_DOCUMENTS = 4
const val ITEM_POST_LINK = 5
const val ITEM_POST_MULTIPLE_MEDIA = 6
const val ITEM_MULTIPLE_MEDIA_IMAGE = 7
const val ITEM_MULTIPLE_MEDIA_VIDEO = 8
const val ITEM_POST_MEDIA = 9
const val ITEM_POST_ATTACHMENT = 10
const val ITEM_OVERFLOW_MENU_ITEM = 11
const val ITEM_POST_DOCUMENTS_ITEM = 12

@IntDef(
    ITEM_NONE,
    ITEM_POST_TEXT_ONLY,
    ITEM_POST_SINGLE_IMAGE,
    ITEM_POST_SINGLE_VIDEO,
    ITEM_POST_DOCUMENTS,
    ITEM_POST_LINK,
    ITEM_POST_MULTIPLE_MEDIA,
    ITEM_MULTIPLE_MEDIA_IMAGE,
    ITEM_MULTIPLE_MEDIA_VIDEO,
    ITEM_POST_MEDIA,
    ITEM_POST_ATTACHMENT,
    ITEM_OVERFLOW_MENU_ITEM,
    ITEM_POST_DOCUMENTS_ITEM
)

@Retention(AnnotationRetention.SOURCE)
annotation class LMFeedViewType