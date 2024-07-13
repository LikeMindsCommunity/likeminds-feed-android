package com.likeminds.feed.android.core.utils.user

import com.likeminds.likemindsfeed.sdk.model.Community
import com.likeminds.likemindsfeed.sdk.model.User

data class UserResponse(
    val user: User?, //user data
    val community: Community?, //community data
    val appAccess: Boolean?,
    val accessToken: String? = null,
    val refreshToken: String? = null
)
