package com.likeminds.feed.android.core.utils

import com.likeminds.feed.android.core.activityfeed.view.LMFeedActivityFeedFragment
import com.likeminds.feed.android.core.delete.view.*
import com.likeminds.feed.android.core.likes.view.LMFeedLikesFragment
import com.likeminds.feed.android.core.poll.create.view.LMFeedCreatePollFragment
import com.likeminds.feed.android.core.poll.result.view.LMFeedPollResultsFragment
import com.likeminds.feed.android.core.poll.result.view.LMFeedPollResultsTabFragment
import com.likeminds.feed.android.core.post.create.view.LMFeedCreatePostFragment
import com.likeminds.feed.android.core.post.detail.view.LMFeedPostDetailFragment
import com.likeminds.feed.android.core.post.edit.view.LMFeedEditPostDisabledTopicsDialogFragment
import com.likeminds.feed.android.core.post.edit.view.LMFeedEditPostFragment
import com.likeminds.feed.android.core.postmenu.view.LMFeedPostMenuBottomSheetFragment
import com.likeminds.feed.android.core.report.view.LMFeedReportFragment
import com.likeminds.feed.android.core.report.view.LMFeedReportSuccessDialogFragment
import com.likeminds.feed.android.core.search.view.LMFeedSearchFragment
import com.likeminds.feed.android.core.topicselection.view.LMFeedTopicSelectionFragment
import com.likeminds.feed.android.core.ui.widgets.poll.view.LMFeedAddPollOptionBottomSheetFragment
import com.likeminds.feed.android.core.ui.widgets.poll.view.LMFeedAnonymousPollDialogFragment
import javax.inject.Singleton

@Singleton
class LMFeedNavigationFragments {

    private var activityFeedFragment: LMFeedActivityFeedFragment? = null
    private var adminDeleteDialogFragment: LMFeedAdminDeleteDialogFragment? = null
    private var reasonChooseBottomSheetFragment: LMFeedReasonChooseBottomSheetFragment? = null
    private var selfDeleteDialogFragment: LMFeedSelfDeleteDialogFragment? = null
    private var likesFragment: LMFeedLikesFragment? = null
    private var createPollFragment: LMFeedCreatePollFragment? = null
    private var pollResultsFragment: LMFeedPollResultsFragment? = null
    private var pollResultsTabFragment: LMFeedPollResultsTabFragment? = null
    private var createPostFragment: LMFeedCreatePostFragment? = null
    private var postDetailFragment: LMFeedPostDetailFragment? = null
    private var editPostDisabledTopicsDialogFragment: LMFeedEditPostDisabledTopicsDialogFragment? =
        null
    private var editPostFragment: LMFeedEditPostFragment? = null
    private var postMenuBottomSheetFragment: LMFeedPostMenuBottomSheetFragment? = null
    private var reportFragment: LMFeedReportFragment? = null
    private var reportSuccessDialogFragment: LMFeedReportSuccessDialogFragment? = null
    private var searchFragment: LMFeedSearchFragment? = null
    private var topicSelectionFragment: LMFeedTopicSelectionFragment? = null
    private var addPollOptionBottomSheetFragment: LMFeedAddPollOptionBottomSheetFragment? = null
    private var anonymousPollDialogFragment: LMFeedAnonymousPollDialogFragment? = null

    companion object {
        private var instance: LMFeedNavigationFragments? = null

        @Synchronized
        fun getInstance(): LMFeedNavigationFragments {
            if (instance == null) {
                instance = LMFeedNavigationFragments()
            }

            return instance!!
        }
    }

    fun setActivityFeedFragment(activityFeedFragment: LMFeedActivityFeedFragment) {
        this.activityFeedFragment = activityFeedFragment
    }

    fun getActivityFeedFragment(): LMFeedActivityFeedFragment {
        if (this.activityFeedFragment != null) {
            return this.activityFeedFragment!!
        }

        return LMFeedActivityFeedFragment()
    }

    fun setAdminDeleteDialogFragment(adminDeleteDialogFragment: LMFeedAdminDeleteDialogFragment) {
        this.adminDeleteDialogFragment = adminDeleteDialogFragment
    }

    fun getAdminDeleteDialogFragment(): LMFeedAdminDeleteDialogFragment {
        if (adminDeleteDialogFragment != null) {
            return adminDeleteDialogFragment!!
        }

        return LMFeedAdminDeleteDialogFragment()
    }

    fun setReasonChooseBottomSheetFragment(reasonChooseBottomSheetFragment: LMFeedReasonChooseBottomSheetFragment) {
        this.reasonChooseBottomSheetFragment = reasonChooseBottomSheetFragment
    }

    fun getReasonChooseBottomSheetFragment(): LMFeedReasonChooseBottomSheetFragment {
        if (reasonChooseBottomSheetFragment != null) {
            return reasonChooseBottomSheetFragment!!
        }

        return LMFeedReasonChooseBottomSheetFragment()
    }

    fun setSelfDeleteDialogFragment(selfDeleteDialogFragment: LMFeedSelfDeleteDialogFragment) {
        this.selfDeleteDialogFragment = selfDeleteDialogFragment
    }

    fun getSelfDeleteDialogFragment(): LMFeedSelfDeleteDialogFragment {
        if (selfDeleteDialogFragment != null) {
            return selfDeleteDialogFragment!!
        }

        return LMFeedSelfDeleteDialogFragment()
    }

    fun setLikesFragment(likesFragment: LMFeedLikesFragment) {
        this.likesFragment = likesFragment
    }

    fun getLikesFragment(): LMFeedLikesFragment {
        if (likesFragment != null) {
            return likesFragment!!
        }

        return LMFeedLikesFragment()
    }

    fun setCreatePollFragment(createPollFragment: LMFeedCreatePollFragment) {
        this.createPollFragment = createPollFragment
    }

    fun getCreatePollFragment(): LMFeedCreatePollFragment {
        if (createPollFragment != null) {
            return createPollFragment!!
        }

        return LMFeedCreatePollFragment()
    }

    fun setPollResultsFragment(pollResultsFragment: LMFeedPollResultsFragment) {
        this.pollResultsFragment = pollResultsFragment
    }

    fun getPollResultsFragment(): LMFeedPollResultsFragment {
        if (pollResultsFragment != null) {
            return pollResultsFragment!!
        }

        return LMFeedPollResultsFragment()
    }

    fun setPollResultsTabFragment(pollResultsTabFragment: LMFeedPollResultsTabFragment) {
        this.pollResultsTabFragment = pollResultsTabFragment
    }

    fun getPollResultsTabFragment(): LMFeedPollResultsTabFragment {
        if (pollResultsTabFragment != null) {
            return pollResultsTabFragment!!
        }

        return LMFeedPollResultsTabFragment()
    }

    fun setCreatePostFragment(createPostFragment: LMFeedCreatePostFragment) {
        this.createPostFragment = createPostFragment
    }

    fun getCreatePostFragment(): LMFeedCreatePostFragment {
        if (createPostFragment != null) {
            return createPostFragment!!
        }

        return LMFeedCreatePostFragment()
    }

    fun setPostDetailFragment(postDetailFragment: LMFeedPostDetailFragment) {
        this.postDetailFragment = postDetailFragment
    }

    fun getPostDetailFragment(): LMFeedPostDetailFragment {
        if (postDetailFragment != null) {
            return postDetailFragment!!
        }

        return LMFeedPostDetailFragment()
    }

    fun setEditPostDisabledTopicsDialogFragment(editPostDisabledTopicsDialogFragment: LMFeedEditPostDisabledTopicsDialogFragment) {
        this.editPostDisabledTopicsDialogFragment = editPostDisabledTopicsDialogFragment
    }

    fun getEditPostDisabledTopicsDialogFragment(): LMFeedEditPostDisabledTopicsDialogFragment {
        if (editPostDisabledTopicsDialogFragment != null) {
            return editPostDisabledTopicsDialogFragment!!
        }

        return LMFeedEditPostDisabledTopicsDialogFragment()
    }

    fun setEditPostFragment(editPostFragment: LMFeedEditPostFragment) {
        this.editPostFragment = editPostFragment
    }

    fun getEditPostFragment(): LMFeedEditPostFragment {
        if (editPostFragment != null) {
            return editPostFragment!!
        }

        return LMFeedEditPostFragment()
    }

    fun setPostMenuBottomSheetListener(postMenuBottomSheetFragment: LMFeedPostMenuBottomSheetFragment) {
        this.postMenuBottomSheetFragment = postMenuBottomSheetFragment
    }

    fun getPostMenuBottomSheetFragment(): LMFeedPostMenuBottomSheetFragment {
        if (postMenuBottomSheetFragment != null) {
            return postMenuBottomSheetFragment!!
        }

        return LMFeedPostMenuBottomSheetFragment()
    }

    fun setReportFragment(reportFragment: LMFeedReportFragment) {
        this.reportFragment = reportFragment
    }

    fun getReportFragment(): LMFeedReportFragment {
        if (reportFragment != null) {
            return reportFragment!!
        }

        return LMFeedReportFragment()
    }

    fun setReportSuccessDialogFragment(reportSuccessDialogFragment: LMFeedReportSuccessDialogFragment) {
        this.reportSuccessDialogFragment = reportSuccessDialogFragment
    }

    fun getReportSuccessDialogFragment(): LMFeedReportSuccessDialogFragment {
        if (reportSuccessDialogFragment != null) {
            return reportSuccessDialogFragment!!
        }

        return LMFeedReportSuccessDialogFragment()
    }

    fun setSearchFragment(searchFragment: LMFeedSearchFragment) {
        this.searchFragment = searchFragment
    }

    fun getSearchFragment(): LMFeedSearchFragment {
        if (searchFragment != null) {
            return searchFragment!!
        }

        return LMFeedSearchFragment()
    }

    fun setTopicSelectionFragment(topicSelectionFragment: LMFeedTopicSelectionFragment) {
        this.topicSelectionFragment = topicSelectionFragment
    }

    fun getTopicSelectionFragment(): LMFeedTopicSelectionFragment {
        if (topicSelectionFragment != null) {
            return topicSelectionFragment!!
        }

        return LMFeedTopicSelectionFragment()
    }

    fun setAddPollOptionBottomSheetFragment(addPollOptionBottomSheetFragment: LMFeedAddPollOptionBottomSheetFragment) {
        this.addPollOptionBottomSheetFragment = addPollOptionBottomSheetFragment
    }

    fun getAddPollOptionBottomSheetFragment(): LMFeedAddPollOptionBottomSheetFragment {
        if (addPollOptionBottomSheetFragment != null) {
            return addPollOptionBottomSheetFragment!!
        }

        return LMFeedAddPollOptionBottomSheetFragment()
    }

    fun setAnonymousPollDialogFragment(anonymousPollDialogFragment: LMFeedAnonymousPollDialogFragment) {
        this.anonymousPollDialogFragment = anonymousPollDialogFragment
    }

    fun getAnonymousPollDialogFragment(): LMFeedAnonymousPollDialogFragment {
        if (anonymousPollDialogFragment != null) {
            return anonymousPollDialogFragment!!
        }

        return LMFeedAnonymousPollDialogFragment()
    }
}