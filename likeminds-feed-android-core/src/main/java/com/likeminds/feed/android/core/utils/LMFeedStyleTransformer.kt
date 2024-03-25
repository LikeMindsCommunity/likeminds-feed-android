package com.likeminds.feed.android.core.utils

import com.likeminds.feed.android.core.activityfeed.style.LMFeedActivityFeedFragmentViewStyle
import com.likeminds.feed.android.core.delete.style.LMFeedAdminDeleteDialogFragmentStyle
import com.likeminds.feed.android.core.delete.style.LMFeedSelfDeleteDialogFragmentStyle
import com.likeminds.feed.android.core.likes.style.LMFeedLikesFragmentViewStyle
import com.likeminds.feed.android.core.post.create.viewstyle.LMFeedCreatePostFragmentViewStyle
import com.likeminds.feed.android.core.post.detail.viewstyle.LMFeedPostDetailFragmentViewStyle
import com.likeminds.feed.android.core.post.edit.style.LMFeedEditPostFragmentViewStyle
import com.likeminds.feed.android.core.post.style.LMFeedPostViewStyle
import com.likeminds.feed.android.core.report.style.LMFeedReportFragmentViewStyle
import com.likeminds.feed.android.core.topicselection.style.LMFeedTopicSelectionFragmentViewStyle
import com.likeminds.feed.android.core.universalfeed.style.LMFeedUniversalFeedFragmentViewStyle

object LMFeedStyleTransformer {

    @JvmStatic
    var universalFeedFragmentViewStyle: LMFeedUniversalFeedFragmentViewStyle =
        LMFeedUniversalFeedFragmentViewStyle.Builder().build()

    @JvmStatic
    var postViewStyle: LMFeedPostViewStyle = LMFeedPostViewStyle.Builder().build()

    @JvmStatic
    var postDetailFragmentViewStyle: LMFeedPostDetailFragmentViewStyle =
        LMFeedPostDetailFragmentViewStyle.Builder().build()

    @JvmStatic
    var createPostFragmentViewStyle: LMFeedCreatePostFragmentViewStyle =
        LMFeedCreatePostFragmentViewStyle.Builder().build()

    @JvmStatic
    var editPostFragmentViewStyle: LMFeedEditPostFragmentViewStyle =
        LMFeedEditPostFragmentViewStyle.Builder().build()

    @JvmStatic
    var activityFeedFragmentViewStyle: LMFeedActivityFeedFragmentViewStyle =
        LMFeedActivityFeedFragmentViewStyle.Builder().build()

    @JvmStatic
    var likesFragmentViewStyle: LMFeedLikesFragmentViewStyle =
        LMFeedLikesFragmentViewStyle.Builder().build()

    @JvmStatic
    var topicSelectionFragmentViewStyle: LMFeedTopicSelectionFragmentViewStyle =
        LMFeedTopicSelectionFragmentViewStyle.Builder().build()

    @JvmStatic
    var reportFragmentViewStyle: LMFeedReportFragmentViewStyle =
        LMFeedReportFragmentViewStyle.Builder().build()

    @JvmStatic
    var adminDeleteDialogFragmentStyle: LMFeedAdminDeleteDialogFragmentStyle =
        LMFeedAdminDeleteDialogFragmentStyle.Builder().build()

    @JvmStatic
    var selfDeleteDialogFragmentStyle: LMFeedSelfDeleteDialogFragmentStyle =
        LMFeedSelfDeleteDialogFragmentStyle.Builder().build()
}