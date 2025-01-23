package com.likeminds.feed.android.core.utils.feed

import android.util.Log
import com.likeminds.feed.android.core.socialfeed.model.LMFeedPostViewData
import com.likeminds.likemindsfeed.post.model.SeenPost

object LMFeedPostSeenUtil {
    private val seenPost = HashSet<SeenPost>()

    fun insertSeenPost(post: LMFeedPostViewData, seenAt: Long) {
        val postSeenByUser = SeenPost.Builder()
            .postId(post.id)
            .seenAt(seenAt)
            .build()

        seenPost.add(postSeenByUser)
    }

    fun getAllSeenPosts(): HashSet<SeenPost> {
        Log.d(
            "PUI", "seenPost: ${
                seenPost.map {
                    it
                }
            }"
        )
        return seenPost
    }

    fun clearSeenPost() {
        Log.d("PUI", "clearSeenPost called")
        seenPost.clear()
    }
}