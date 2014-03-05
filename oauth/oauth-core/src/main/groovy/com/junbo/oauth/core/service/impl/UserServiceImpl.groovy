/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.service.impl

import com.junbo.identity.spec.model.common.ResultList
import com.junbo.identity.spec.model.user.User
import com.junbo.identity.spec.model.user.UserProfile
import com.junbo.identity.spec.resource.UserProfileResource
import com.junbo.identity.spec.resource.UserResource
import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.core.context.ServiceContext
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.core.service.TokenGenerationService
import com.junbo.oauth.core.service.UserService
import com.junbo.oauth.core.util.ServiceContextUtil
import com.junbo.oauth.spec.model.AccessToken
import com.junbo.oauth.spec.model.TokenType
import com.junbo.oauth.spec.model.UserInfo
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

/**
 * Javadoc.
 */
@CompileStatic
class UserServiceImpl implements UserService {

    private TokenGenerationService tokenGenerationService

    private UserResource userResource

    private UserProfileResource userProfileResource

    @Required
    void setTokenGenerationService(TokenGenerationService tokenGenerationService) {
        this.tokenGenerationService = tokenGenerationService
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
    UserInfo getUserInfo(ServiceContext context) {
        def headerMap = ServiceContextUtil.getHeaderMap(context)
        String authorization = headerMap.getFirst(OAuthParameters.AUTHORIZATION)

        if (!StringUtils.hasText(authorization)) {
            throw AppExceptions.INSTANCE.missingAuthorization().exception()
        }

        String[] tokens = authorization.split(' ')
        if (tokens.length != 2 || !tokens[0].equalsIgnoreCase(TokenType.BEARER.name())) {
            throw AppExceptions.INSTANCE.invalidAuthorization().exception()
        }

        AccessToken accessToken = tokenGenerationService.getAccessToken(tokens[1])

        if (accessToken == null) {
            throw AppExceptions.INSTANCE.invalidAccessToken().exception()
        }

        if (accessToken.isExpired()) {
            throw AppExceptions.INSTANCE.expiredAccessToken().exception()
        }

        Promise<User> userPromise = userResource.getUser(accessToken.userId)

        User user = userPromise.wrapped().get()

        UserInfo userInfo = new UserInfo(
                sub: user.id.toString(),
                email: user.userName
        )

        Promise<ResultList<UserProfile>> userProfilePromise = userProfileResource.getUserProfiles(accessToken.userId,
                'PAYIN', 0, 1)

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
