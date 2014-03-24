/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.service.impl

import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.identity.spec.model.user.User
import com.junbo.identity.spec.model.user.UserProfile
import com.junbo.identity.spec.resource.UserProfileResource
import com.junbo.identity.spec.resource.UserResource
import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.core.service.TokenService
import com.junbo.oauth.core.service.UserService
import com.junbo.oauth.spec.model.AccessToken
import com.junbo.oauth.spec.model.UserInfo
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

/**
 * Javadoc.
 */
@CompileStatic
class UserServiceImpl implements UserService {

    private TokenService tokenService

    private UserResource userResource

    private UserProfileResource userProfileResource

    @Required
    void setTokenService(TokenService tokenService) {
        this.tokenService = tokenService
    }

    @Required
    void setUserResource(UserResource userResource) {
        this.userResource = userResource
    }

    @Required
    void setUserProfileResource(UserProfileResource userProfileResource) {
        this.userProfileResource = userProfileResource
    }

    @Override
    Promise<User> authenticateUser(String username, String password) {
        return userResource.authenticateUser(username, password)
    }

    @Override
    UserInfo getUserInfo(String authorization) {
        if (!StringUtils.hasText(authorization)) {
            throw AppExceptions.INSTANCE.missingAuthorization().exception()
        }

        AccessToken accessToken = tokenService.extractAccessToken(authorization)

        if (accessToken == null) {
            throw AppExceptions.INSTANCE.invalidAccessToken().exception()
        }

        if (accessToken.isExpired()) {
            throw AppExceptions.INSTANCE.expiredAccessToken().exception()
        }

        Promise<User> userPromise = userResource.getUser(new UserId(accessToken.userId))

        User user = userPromise.wrapped().get()

        UserInfo userInfo = new UserInfo(
                sub: user.id.toString(),
                email: user.userName
        )

        Promise<Results<UserProfile>> userProfilePromise = userProfileResource.
                getUserProfiles(new UserId(accessToken.userId), 'PAYIN', 0, 1)

        if (userProfileResource != null && userProfilePromise.wrapped().get() != null
                && !userProfilePromise.wrapped().get().items.isEmpty()) {
            UserProfile profile = userProfilePromise.wrapped().get().items.get(0)
            userInfo.givenName = profile.firstName
            userInfo.familyName = profile.lastName
            userInfo.middleName = profile.middleName
        }

        return userInfo
    }
}
