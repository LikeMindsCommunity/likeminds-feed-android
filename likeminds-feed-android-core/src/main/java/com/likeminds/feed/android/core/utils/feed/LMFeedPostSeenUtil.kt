package com.likeminds.feed.android.core.utils.feed

import android.util.Log
import com.likeminds.feed.android.core.socialfeed.model.LMFeedPostViewData
import com.likeminds.likemindsfeed.post.model.SeenPost

object LMFeedPostSeenUtil {
    private val seenPost = HashMap<String, SeenPost>()

    // insert post seen by user to the hashmap
    fun insertSeenPost(post: LMFeedPostViewData, seenAt: Long) {
        val postId = post.id

        val postSeenByUser = SeenPost.Builder()
            .postId(postId)
            .seenAt(seenAt)
            .build()

        seenPost[postId] = postSeenByUser
    }

    // get all seen posts by user from hashmap
    fun getAllSeenPosts(): List<SeenPost> {
        return seenPost.map {
            it.value
        }
    }

    //clear the hashmap
    fun clearSeenPost() {
        seenPost.clear()
    }
}