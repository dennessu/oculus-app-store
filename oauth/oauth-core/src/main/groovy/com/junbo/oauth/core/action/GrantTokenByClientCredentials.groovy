/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.common.error.AppCommonErrors
import com.junbo.common.error.AppErrorException
import com.junbo.common.id.UserId
import com.junbo.common.util.IdFormatter
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.identity.spec.v1.resource.UserResource
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.exception.AppErrors
import com.junbo.oauth.core.service.OAuthTokenService
import com.junbo.oauth.spec.model.AccessToken
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required
import org.springframework.http.HttpStatus
import org.springframework.util.Assert
import org.springframework.util.StringUtils

/**
 * GrantTokenByClientCredentials.
 */
@CompileStatic
class GrantTokenByClientCredentials implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(GrantTokenByClientCredentials)

    private OAuthTokenService tokenService

    private UserResource userResource

    @Required
    void setTokenService(OAuthTokenService tokenService) {
        this.tokenService = tokenService
    }

    @Required
    void setUserResource(UserResource userResource) {
        this.userResource = userResource
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)

        def oauthInfo = contextWrapper.oauthInfo
        def client = contextWrapper.client
        def parameterMap = contextWrapper.parameterMap

        Assert.notNull(oauthInfo, 'oauthInfo is null')
        Assert.notNull(client, 'client is null')

        String ipRestriction = parameterMap.getFirst(OAuthParameters.IP_RESTRICTION)
        Boolean ipRestrictionRequired = Boolean.parseBoolean(ipRestriction);

        String userIdStr = parameterMap.getFirst(OAuthParameters.USER_ID)
        if (StringUtils.hasText(userIdStr)) {
            Long userIdLong = IdFormatter.decodeId(UserId, userIdStr)
            return userResource.get(new UserId(userIdLong), new UserGetOptions()).recover { Throwable e ->
                if (e instanceof AppErrorException) {
                    AppErrorException appError = (AppErrorException) e
                    if (appError.error.httpStatusCode == HttpStatus.NOT_FOUND.value()) {
                        throw AppCommonErrors.INSTANCE.fieldInvalid('user_id').exception()
                    } else {
                        LOGGER.error('Error calling the identity service.', e)
                        throw AppErrors.INSTANCE.errorCallingIdentity().exception()
                    }
                } else {
                    LOGGER.error('Error calling the identity service.', e)
                    throw AppErrors.INSTANCE.errorCallingIdentity().exception()
                }
            }.then { User user ->
                AccessToken accessToken = tokenService.generateAccessToken(client, user.getId().value, oauthInfo.scopes,
                        ipRestrictionRequired)

                contextWrapper.accessToken = accessToken

                return Promise.pure(null)
            }
        } else {
            AccessToken accessToken = tokenService.generateAccessToken(client, 0L, oauthInfo.scopes, ipRestrictionRequired)

            contextWrapper.accessToken = accessToken

            return Promise.pure(null)
        }
    }
}
