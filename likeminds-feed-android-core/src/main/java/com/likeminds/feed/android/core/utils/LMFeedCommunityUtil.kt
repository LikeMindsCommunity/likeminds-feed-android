package com.likeminds.feed.android.core.utils

object LMFeedCommunityUtil {

    private var postVariable: String = "post"

    fun getPostVariable(): String {
        return postVariable
    }

    fun setPostVariable(postVariable: String) {
        this.postVariable = postVariable
    }
}