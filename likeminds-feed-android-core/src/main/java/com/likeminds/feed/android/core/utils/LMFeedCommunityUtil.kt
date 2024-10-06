package com.likeminds.feed.android.core.utils

object LMFeedCommunityUtil {

    private var postVariable: String = "post"
    private var likeVariable: String = "like"
    private var likePastTenseVariable: String = "liked"
    private var commentVariable: String = "comment"

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