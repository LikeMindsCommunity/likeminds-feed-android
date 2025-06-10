package com.likeminds.feed.android.core

import android.app.Application
import android.content.Context
import com.likeminds.feed.android.core.activityfeed.view.LMFeedActivityFeedFragment
import com.likeminds.feed.android.core.delete.view.LMFeedAdminDeleteDialogFragment
import com.likeminds.feed.android.core.delete.view.LMFeedReasonChooseBottomSheetFragment
import com.likeminds.feed.android.core.delete.view.LMFeedSelfDeleteDialogFragment
import com.likeminds.feed.android.core.likes.view.LMFeedLikesFragment
import com.likeminds.feed.android.core.poll.create.view.LMFeedCreatePollFragment
import com.likeminds.feed.android.core.poll.result.view.LMFeedPollResultsFragment
import com.likeminds.feed.android.core.poll.result.view.LMFeedPollResultsTabFragment
import com.likeminds.feed.android.core.post.create.view.LMFeedCreatePostFragment
import com.likeminds.feed.android.core.post.detail.view.LMFeedPostDetailFragment
import com.likeminds.feed.android.core.post.edit.view.LMFeedEditPostDisabledTopicsDialogFragment
import com.likeminds.feed.android.core.post.edit.view.LMFeedEditPostFragment
import com.likeminds.feed.android.core.postmenu.view.LMFeedPostMenuBottomSheetFragment
import com.likeminds.feed.android.core.qnafeed.view.LMFeedQnAFeedFragment
import com.likeminds.feed.android.core.report.view.LMFeedReportFragment
import com.likeminds.feed.android.core.report.view.LMFeedReportSuccessDialogFragment
import com.likeminds.feed.android.core.search.view.LMFeedSearchFragment
import com.likeminds.feed.android.core.socialfeed.view.LMFeedSocialFeedFragment
import com.likeminds.feed.android.core.topicselection.view.LMFeedTopicSelectionFragment
import com.likeminds.feed.android.core.ui.theme.LMFeedAppearance
import com.likeminds.feed.android.core.ui.theme.model.LMFeedAppearanceRequest
import com.likeminds.feed.android.core.ui.widgets.poll.view.LMFeedAddPollOptionBottomSheetFragment
import com.likeminds.feed.android.core.ui.widgets.poll.view.LMFeedAnonymousPollDialogFragment
import com.likeminds.feed.android.core.utils.LMFeedNavigationFragments
import com.likeminds.feed.android.core.utils.user.*
import com.likeminds.feed.android.core.videofeed.view.LMFeedVideoFeedFragment
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.user.model.InitiateUserRequest
import com.likeminds.likemindsfeed.user.model.ValidateUserRequest
import kotlinx.coroutines.*

object LMFeedCore {

    /**
     * Initial setup function for customers and blocker function
     * @param application: Instance of the application class
     * @param theme: Theme selected for feed
     * @param domain: Domain of the client used in share post
     * @param enablePushNotifications: Whether to enable push notifications or not
     * @param deviceId: Device Id of the user
     * @param lmFeedAppearance: Object of [LMFeedAppearance] to add your customizable theme in whole feed
     * @param lmFeedCoreCallback: Instance of [LMFeedCoreCallback] so that we can share data/events to customers code
     */
    fun setup(
        application: Application,
        theme: LMFeedTheme,
        domain: String? = null,
        enablePushNotifications: Boolean = false,
        deviceId: String? = null,
        lmFeedAppearance: LMFeedAppearanceRequest? = null,
        lmFeedCoreCallback: LMFeedCoreCallback? = null
    ) {
        //set theme
        LMFeedAppearance.setAppearance(lmFeedAppearance)

        //initialize core application
        val coreApplication = LMFeedCoreApplication.getInstance()
        coreApplication.initCoreApplication(
            application,
            theme,
            lmFeedCoreCallback,
            domain,
            enablePushNotifications,
            deviceId
        )
    }

    fun showFeed(
        context: Context,
        apiKey: String,
        uuid: String,
        userName: String,
        success: ((UserResponse) -> Unit)?,
        error: ((String?) -> Unit)?
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val lmFeedClient = LMFeedClient.getInstance()
            val tokens = lmFeedClient.getTokens().data

            val userMeta = LMFeedUserMetaData.getInstance()
            val deviceId = userMeta.deviceId

            if (tokens?.first.isNullOrEmpty() || tokens?.second.isNullOrEmpty()) {
                val initiateUserRequest = InitiateUserRequest.Builder()
                    .apiKey(apiKey)
                    .userName(userName)
                    .uuid(uuid)
                    .deviceId(deviceId)
                    .build()

                val response = lmFeedClient.initiateUser(initiateUserRequest)
                if (response.success) {
                    success?.let { success ->
                        //return user response
                        response.data?.let { data ->
                            val userResponse = UserResponseConvertor.getUserResponse(data)
                            success(userResponse)

                            val imageUrl = userResponse.user?.imageUrl

                            //perform post session actions
                            userMeta.onPostSessionInit(
                                context,
                                userName,
                                uuid,
                                imageUrl
                            )
                        }
                    }
                } else {
                    error?.let { it(response.errorMessage) }
                }
            } else {
                showFeed(context, tokens?.first, tokens?.second, success, error)
            }
        }
    }

    fun showFeed(
        context: Context,
        accessToken: String?,
        refreshToken: String?,
        success: ((UserResponse) -> Unit)?,
        error: ((String?) -> Unit)?
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val userMeta = LMFeedUserMetaData.getInstance()
            val deviceId = userMeta.deviceId

            val lmFeedClient = LMFeedClient.getInstance()
            val tokens = if (accessToken.isNullOrEmpty() || refreshToken.isNullOrEmpty()) {
                lmFeedClient.getTokens().data
            } else {
                Pair(accessToken, refreshToken)
            } ?: Pair("", "")

            val validateUserRequest = ValidateUserRequest.Builder()
                .accessToken(tokens.first)
                .refreshToken(tokens.second)
                .deviceId(deviceId)
                .build()

            val response = lmFeedClient.validateUser(validateUserRequest)
            if (response.success) {
                success?.let { success ->
                    //return user response
                    response.data?.let { data ->
                        val userResponse = UserResponseConvertor.getUserResponse(data)
                        success(userResponse)
                    }

                    //perform post session actions
                    val user = response.data?.user
                    val userName = user?.name
                    val uuid = user?.sdkClientInfo?.uuid
                    val userImage = user?.imageUrl
                    userMeta.onPostSessionInit(
                        context,
                        userName,
                        uuid,
                        userImage
                    )
                }
            } else {
                error?.let { it(response.errorMessage) }
            }
        }
    }

    /**
     * Sets the provided LMFeedActivityFeedFragment instance
     *
     * @param activityFeedFragment The fragment instance of LMFeedActivityFeedFragment
     */
    fun setActivityFeedFragment(activityFeedFragment: LMFeedActivityFeedFragment) {
        LMFeedNavigationFragments.getInstance().setActivityFeedFragment(activityFeedFragment)
    }

    /**
     * Sets the provided LMFeedAdminDeleteDialogFragment instance
     *
     * @param adminDeleteDialogFragment The fragment instance of LMFeedAdminDeleteDialogFragment
     */
    fun setAdminDeleteDialogFragment(adminDeleteDialogFragment: LMFeedAdminDeleteDialogFragment) {
        LMFeedNavigationFragments.getInstance().setAdminDeleteDialogFragment(adminDeleteDialogFragment)
    }

    /**
     * Sets the provided LMFeedReasonChooseBottomSheetFragment instance
     *
     * @param reasonChooseBottomSheetFragment The fragment instance of LMFeedReasonChooseBottomSheetFragment
     */
    fun setReasonChooseBottomSheetFragment(reasonChooseBottomSheetFragment: LMFeedReasonChooseBottomSheetFragment) {
        LMFeedNavigationFragments.getInstance().setReasonChooseBottomSheetFragment(reasonChooseBottomSheetFragment)
    }

    /**
     * Sets the provided LMFeedSelfDeleteDialogFragment instance
     *
     * @param selfDeleteDialogFragment The fragment instance of LMFeedSelfDeleteDialogFragment
     */
    fun setSelfDeleteDialogFragment(selfDeleteDialogFragment: LMFeedSelfDeleteDialogFragment) {
        LMFeedNavigationFragments.getInstance().setSelfDeleteDialogFragment(selfDeleteDialogFragment)
    }

    /**
     * Sets the provided LMFeedLikesFragment instance
     *
     * @param likesFragment The fragment instance of LMFeedLikesFragment
     */
    fun setLikesFragment(likesFragment: LMFeedLikesFragment) {
        LMFeedNavigationFragments.getInstance().setLikesFragment(likesFragment)
    }

    /**
     * Sets the provided LMFeedCreatePollFragment instance
     *
     * @param createPollFragment The fragment instance of LMFeedCreatePollFragment
     */
    fun setCreatePollFragment(createPollFragment: LMFeedCreatePollFragment) {
        LMFeedNavigationFragments.getInstance().setCreatePollFragment(createPollFragment)
    }

    /**
     * Sets the provided LMFeedPollResultsFragment instance
     *
     * @param pollResultsFragment The fragment instance of LMFeedPollResultsFragment
     */
    fun setPollResultsFragment(pollResultsFragment: LMFeedPollResultsFragment) {
        LMFeedNavigationFragments.getInstance().setPollResultsFragment(pollResultsFragment)
    }

    /**
     * Sets the provided LMFeedPollResultsTabFragment instance
     *
     * @param pollResultsTabFragment The fragment instance of LMFeedPollResultsTabFragment
     */
    fun setPollResultsTabFragment(pollResultsTabFragment: LMFeedPollResultsTabFragment) {
        LMFeedNavigationFragments.getInstance().setPollResultsTabFragment(pollResultsTabFragment)
    }

    /**
     * Sets the provided LMFeedCreatePostFragment instance
     *
     * @param createPostFragment The fragment instance of LMFeedCreatePostFragment
     */
    fun setCreatePostFragment(createPostFragment: LMFeedCreatePostFragment) {
        LMFeedNavigationFragments.getInstance().setCreatePostFragment(createPostFragment)
    }

    /**
     * Sets the provided LMFeedPostDetailFragment instance
     *
     * @param postDetailFragment The fragment instance of LMFeedPostDetailFragment
     */
    fun setPostDetailFragment(postDetailFragment: LMFeedPostDetailFragment) {
        LMFeedNavigationFragments.getInstance().setPostDetailFragment(postDetailFragment)
    }

    /**
     * Sets the provided LMFeedEditPostDisabledTopicsDialogFragment instance
     *
     * @param editPostDisabledTopicsDialogFragment The fragment instance of LMFeedEditPostDisabledTopicsDialogFragment
     */
    fun setEditPostDisabledTopicsDialogFragment(editPostDisabledTopicsDialogFragment: LMFeedEditPostDisabledTopicsDialogFragment) {
        LMFeedNavigationFragments.getInstance().setEditPostDisabledTopicsDialogFragment(editPostDisabledTopicsDialogFragment)
    }

    /**
     * Sets the provided LMFeedEditPostFragment instance
     *
     * @param editPostFragment The fragment instance of LMFeedEditPostFragment
     */
    fun setEditPostFragment(editPostFragment: LMFeedEditPostFragment) {
        LMFeedNavigationFragments.getInstance().setEditPostFragment(editPostFragment)
    }

    /**
     * Sets the provided LMFeedPostMenuBottomSheetFragment instance
     *
     * @param postMenuBottomSheetFragment The fragment instance of LMFeedPostMenuBottomSheetFragment
     */
    fun setPostMenuBottomSheetListener(postMenuBottomSheetFragment: LMFeedPostMenuBottomSheetFragment) {
        LMFeedNavigationFragments.getInstance().setPostMenuBottomSheetListener(postMenuBottomSheetFragment)
    }

    /**
     * Sets the provided LMFeedQnAFeedFragment instance
     *
     * @param qnaFeedFragment The fragment instance of LMFeedQnAFeedFragment
     */
    fun setQnAFeedFragment(qnaFeedFragment: LMFeedQnAFeedFragment) {
        LMFeedNavigationFragments.getInstance().setQnAFeedFragment(qnaFeedFragment)
    }

    /**
     * Sets the provided LMFeedReportFragment instance
     *
     * @param reportFragment The fragment instance of LMFeedReportFragment
     */
    fun setReportFragment(reportFragment: LMFeedReportFragment) {
        LMFeedNavigationFragments.getInstance().setReportFragment(reportFragment)
    }

    /**
     * Sets the provided LMFeedReportSuccessDialogFragment instance
     *
     * @param reportSuccessDialogFragment The fragment instance of LMFeedReportSuccessDialogFragment
     */
    fun setReportSuccessDialogFragment(reportSuccessDialogFragment: LMFeedReportSuccessDialogFragment) {
        LMFeedNavigationFragments.getInstance().setReportSuccessDialogFragment(reportSuccessDialogFragment)
    }

    /**
     * Sets the provided LMFeedSearchFragment instance
     *
     * @param searchFragment The fragment instance of LMFeedSearchFragment
     */
    fun setSearchFragment(searchFragment: LMFeedSearchFragment) {
        LMFeedNavigationFragments.getInstance().setSearchFragment(searchFragment)
    }

    /**
     * Sets the provided LMFeedSocialFeedFragment instance
     *
     * @param socialFeedFragment The fragment instance of LMFeedSocialFeedFragment
     */
    fun setSocialFeedFragment(socialFeedFragment: LMFeedSocialFeedFragment) {
        LMFeedNavigationFragments.getInstance().setSocialFeedFragment(socialFeedFragment)
    }

    /**
     * Sets the provided LMFeedTopicSelectionFragment instance
     *
     * @param topicSelectionFragment The fragment instance of LMFeedTopicSelectionFragment
     */
    fun setTopicSelectionFragment(topicSelectionFragment: LMFeedTopicSelectionFragment) {
        LMFeedNavigationFragments.getInstance().setTopicSelectionFragment(topicSelectionFragment)
    }

    /**
     * Sets the provided LMFeedAddPollOptionBottomSheetFragment instance
     *
     * @param addPollOptionBottomSheetFragment The fragment instance of LMFeedAddPollOptionBottomSheetFragment
     */
    fun setAddPollOptionBottomSheetFragment(addPollOptionBottomSheetFragment: LMFeedAddPollOptionBottomSheetFragment) {
        LMFeedNavigationFragments.getInstance().setAddPollOptionBottomSheetFragment(addPollOptionBottomSheetFragment)
    }

    /**
     * Sets the provided LMFeedAnonymousPollDialogFragment instance
     *
     * @param anonymousPollDialogFragment The fragment instance of LMFeedAnonymousPollDialogFragment
     */
    fun setAnonymousPollDialogFragment(anonymousPollDialogFragment: LMFeedAnonymousPollDialogFragment) {
        LMFeedNavigationFragments.getInstance().setAnonymousPollDialogFragment(anonymousPollDialogFragment)
    }

    /**
     * Sets the provided LMFeedVideoFeedFragment instance
     *
     * @param videoFeedFragment The fragment instance of LMFeedVideoFeedFragment
     */
    fun setVideoFeedFragment(videoFeedFragment: LMFeedVideoFeedFragment) {
        LMFeedNavigationFragments.getInstance().setVideoFeedFragment(videoFeedFragment)
    }
}