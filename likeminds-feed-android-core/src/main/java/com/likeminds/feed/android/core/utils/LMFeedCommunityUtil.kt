package com.likeminds.feed.android.core.utils

import com.likeminds.feed.android.core.*

object LMFeedCommunityUtil {

    private var postVariable: String = when (LMFeedCoreApplication.selectedTheme) {
        LMFeedTheme.SOCIAL_FEED -> "post"
        LMFeedTheme.VIDEO_FEED -> "reel"
        LMFeedTheme.QNA_FEED -> "question"
    }

    private var likeVariable: String = when (LMFeedCoreApplication.selectedTheme) {
        LMFeedTheme.SOCIAL_FEED, LMFeedTheme.VIDEO_FEED -> "like"
        LMFeedTheme.QNA_FEED -> "upvote"
    }

    private var likePastTenseVariable: String = "liked"

    private var commentVariable: String = when (LMFeedCoreApplication.selectedTheme) {
        LMFeedTheme.SOCIAL_FEED, LMFeedTheme.VIDEO_FEED -> "comment"
        LMFeedTheme.QNA_FEED -> "answer"
    }

    fun getPostVariable(): String {
        return postVariable
    }

    fun setPostVariable(postVariable: String) {
        this.postVariable = postVariable
    }

    fun getLikeVariable(): String {
        return likeVariable
    }

    fun setLikeVariable(likeVariable: String) {
        this.likeVariable = likeVariable
    }

    fun getLikePastTenseVariable(): String {
        return likePastTenseVariable
    }

    fun setLikePastTenseVariable(likePastTenseVariable: String) {
        this.likePastTenseVariable = likePastTenseVariable
    }

    fun getCommentVariable(): String {
        return commentVariable
    }

    fun setCommentVariable(commentVariable: String) {
        this.commentVariable = commentVariable
    }
}