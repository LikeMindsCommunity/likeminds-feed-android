package com.likeminds.feed.android.core.utils.feed

import android.util.Log
import com.likeminds.feed.android.core.socialfeed.model.LMFeedPostViewData
import com.likeminds.likemindsfeed.post.model.SeenPost

object LMFeedPostSeenUtil {
    private val seenPost = HashMap<String, SeenPost>()

    fun insertSeenPost(post: LMFeedPostViewData, seenAt: Long) {
        val postId = post.id

        val postSeenByUser = SeenPost.Builder()
            .postId(postId)
            .seenAt(seenAt)
            .build()

        seenPost[postId] = postSeenByUser
    }

    fun getAllSeenPosts(): List<SeenPost> {
        Log.d(
            "PUI", "static getAllSeenPosts: ${
                seenPost.map {
                    it
                }
            }"
        )
        return seenPost.map {
            it.value
        }
    }

    fun clearSeenPost() {
        Log.d("PUI", "clearSeenPost static called")
        seenPost.clear()
    }
}