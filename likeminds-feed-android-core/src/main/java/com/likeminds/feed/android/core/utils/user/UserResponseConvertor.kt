package com.likeminds.feed.android.core.utils.user

import com.likeminds.likemindsfeed.user.model.InitiateUserResponse
import com.likeminds.likemindsfeed.user.model.ValidateUserResponse

object UserResponseConvertor {

    //convert initiate user response to user response
    fun getUserResponse(initiateUserResponse: InitiateUserResponse): UserResponse {
        return UserResponse(
            user = initiateUserResponse.user,
            community = initiateUserResponse.community,
            appAccess = initiateUserResponse.appAccess,
            accessToken = initiateUserResponse.accessToken,
            refreshToken = initiateUserResponse.refreshToken
        )
    }

    //convert validate user response to user response
    fun getUserResponse(validateUserResponse: ValidateUserResponse): UserResponse {
        return UserResponse(
            user = validateUserResponse.user,
            community = validateUserResponse.community,
            appAccess = validateUserResponse.appAccess
        )
    }
}