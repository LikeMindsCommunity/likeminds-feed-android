package com.likeminds.feed.android.core.socialfeed.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class LMFeedSDKClientInfoViewData private constructor(
    val community: Int,
    val user: Int,
    val userUniqueId: String,
    val uuid: String
) : Parcelable {

    class Builder {
        private var community: Int = 0
        private var user: Int = 0
        private var userUniqueId: String = ""
        private var uuid: String = ""

        fun community(community: Int) = apply { this.community = community }
        fun user(user: Int) = apply { this.user = user }
        fun userUniqueId(userUniqueId: String) = apply { this.userUniqueId = userUniqueId }
        fun uuid(uuid: String) = apply { this.uuid = uuid }

        fun build() = LMFeedSDKClientInfoViewData(community, user, userUniqueId, uuid)
    }

    fun toBuilder(): Builder {
        return Builder().user(user)
            .community(community)
            .uuid(uuid)
            .userUniqueId(userUniqueId)
    }
}