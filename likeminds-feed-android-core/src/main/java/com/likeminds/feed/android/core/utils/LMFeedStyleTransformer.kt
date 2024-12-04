package com.likeminds.feed.android.core.utils

import com.likeminds.feed.android.core.activityfeed.style.LMFeedActivityFeedFragmentViewStyle
import com.likeminds.feed.android.core.delete.style.LMFeedAdminDeleteDialogFragmentStyle
import com.likeminds.feed.android.core.delete.style.LMFeedSelfDeleteDialogFragmentStyle
import com.likeminds.feed.android.core.likes.style.LMFeedLikesFragmentViewStyle
import com.likeminds.feed.android.core.poll.create.style.LMFeedCreatePollFragmentViewStyle
import com.likeminds.feed.android.core.poll.result.style.LMFeedPollResultsFragmentViewStyle
import com.likeminds.feed.android.core.post.create.viewstyle.LMFeedCreatePostFragmentViewStyle
import com.likeminds.feed.android.core.post.detail.style.LMFeedPostDetailFragmentViewStyle
import com.likeminds.feed.android.core.post.edit.style.LMFeedEditPostFragmentViewStyle
import com.likeminds.feed.android.core.post.style.LMFeedPostViewStyle
import com.likeminds.feed.android.core.postmenu.style.LMFeedPostMenuViewStyle
import com.likeminds.feed.android.core.qnafeed.style.LMFeedQnAFeedFragmentViewStyle
import com.likeminds.feed.android.core.report.style.LMFeedReportFragmentViewStyle
import com.likeminds.feed.android.core.search.style.LMFeedSearchFragmentViewStyle
import com.likeminds.feed.android.core.socialfeed.style.LMFeedSocialFeedFragmentViewStyle
import com.likeminds.feed.android.core.topicselection.style.LMFeedTopicSelectionFragmentViewStyle
import com.likeminds.feed.android.core.ui.widgets.poll.style.LMFeedAddPollOptionBottomSheetFragmentStyle
import com.likeminds.feed.android.core.ui.widgets.poll.style.LMFeedAnonymousPollDialogFragmentStyle
import com.likeminds.feed.android.core.videofeed.viewstyle.LMFeedVideoFeedFragmentViewStyle

object LMFeedStyleTransformer {

    @JvmStatic
    var socialFeedFragmentViewStyle: LMFeedSocialFeedFragmentViewStyle =
        LMFeedSocialFeedFragmentViewStyle.Builder().build()

    @JvmStatic
    var searchFeedFragmentViewStyle: LMFeedSearchFragmentViewStyle =
        LMFeedSearchFragmentViewStyle.Builder().build()

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

    @JvmStatic
    var pollResultsFragmentViewStyle: LMFeedPollResultsFragmentViewStyle =
        LMFeedPollResultsFragmentViewStyle.Builder().build()

    @JvmStatic
    var addPollOptionBottomSheetFragmentStyle: LMFeedAddPollOptionBottomSheetFragmentStyle =
        LMFeedAddPollOptionBottomSheetFragmentStyle.Builder().build()

    @JvmStatic
    var alertDialogAnonymousPoll: LMFeedAnonymousPollDialogFragmentStyle =
        LMFeedAnonymousPollDialogFragmentStyle.Builder().build()

    @JvmStatic
    var createPollFragmentViewStyle: LMFeedCreatePollFragmentViewStyle =
        LMFeedCreatePollFragmentViewStyle.Builder().build()

    @JvmStatic
    var videoFeedFragmentViewStyle: LMFeedVideoFeedFragmentViewStyle =
        LMFeedVideoFeedFragmentViewStyle.Builder().build()

    @JvmStatic
    var postMenuViewStyle: LMFeedPostMenuViewStyle = LMFeedPostMenuViewStyle.Builder().build()

    @JvmStatic
    var qnaFeedFragmentViewStyle: LMFeedQnAFeedFragmentViewStyle =
        LMFeedQnAFeedFragmentViewStyle.Builder().build()
}