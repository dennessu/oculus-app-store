/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.service.impl

import com.junbo.common.id.UserId
import com.junbo.identity.spec.model.users.User
import com.junbo.identity.spec.model.users.UserLoginAttempt
import com.junbo.identity.spec.options.entity.UserGetOptions
import com.junbo.identity.spec.resource.UserLoginAttemptResource
import com.junbo.identity.spec.resource.UserResource
import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.core.service.TokenService
import com.junbo.oauth.core.service.UserService
import com.junbo.oauth.spec.model.AccessToken
import com.junbo.oauth.spec.model.UserInfo
import groovy.transform.CompileStatic
import org.apache.commons.codec.binary.Base64
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

/**
 * Javadoc.
 */
@CompileStatic
class UserServiceImpl implements UserService {

    private TokenService tokenService

    private UserResource userResource

    private UserLoginAttemptResource userLoginAttemptResource

    @Required
    void setTokenService(TokenService tokenService) {
        this.tokenService = tokenService
    }

    @Required
    void setUserResource(UserResource userResource) {
        this.userResource = userResource
    }

    @Required
    void setUserLoginAttemptResource(UserLoginAttemptResource userLoginAttemptResource) {
        this.userLoginAttemptResource = userLoginAttemptResource
    }

    @Override
    Promise<UserLoginAttempt> authenticateUser(String username, String password, String clientId, String ipAddress) {
        UserLoginAttempt loginAttempt = new UserLoginAttempt(
                type: 'password',
                value: new String(Base64.encodeBase64("$username:$password".bytes)),
                clientId: clientId,
                ipAddress: ipAddress
        )

        return userLoginAttemptResource.create(loginAttempt)
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

        Promise<User> userPromise = userResource.get(new UserId(accessToken.userId), new UserGetOptions())

        User user = userPromise.wrapped().get()

        UserInfo userInfo = new UserInfo(
                sub: user.id.toString(),
                email: user.username,
                givenName: user.name.firstName,
                middleName: user.name.middleName,
                familyName: user.name.lastName
        )

        return userInfo
    }
}
