/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.api.endpoint

import com.junbo.common.error.AppErrorException
import com.junbo.common.id.UserId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.identity.spec.v1.model.Email
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.identity.spec.v1.model.UserPersonalInfoLink
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.identity.spec.v1.option.model.UserPersonalInfoGetOptions
import com.junbo.identity.spec.v1.resource.UserPersonalInfoResource
import com.junbo.identity.spec.v1.resource.UserResource
import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.core.util.CookieUtil
import com.junbo.oauth.db.repo.EmailVerifyCodeRepository
import com.junbo.oauth.db.repo.LoginStateRepository
import com.junbo.oauth.spec.endpoint.EmailVerifyEndpoint
import com.junbo.oauth.spec.model.EmailVerifyCode
import com.junbo.oauth.spec.model.LoginState
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required
import org.springframework.context.annotation.Scope
import org.springframework.util.Assert
import org.springframework.util.StringUtils

import javax.ws.rs.core.Response
import javax.ws.rs.core.UriBuilder

/**
 * EmailVerifyEndpointImpl.
 */
@CompileStatic
@Scope('prototype')
class EmailVerifyEndpointImpl implements EmailVerifyEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailVerifyEndpointImpl)
    private EmailVerifyCodeRepository emailVerifyCodeRepository

    private UserResource userResource

    private UserPersonalInfoResource userPersonalInfoResource

    private String redirectUri

    private LoginStateRepository loginStateRepository

    @Required
    void setEmailVerifyCodeRepository(EmailVerifyCodeRepository emailVerifyCodeRepository) {
        this.emailVerifyCodeRepository = emailVerifyCodeRepository
    }

    @Required
    void setUserResource(UserResource userResource) {
        this.userResource = userResource
    }

    @Required
    void setUserPersonalInfoResource(UserPersonalInfoResource userPersonalInfoResource) {
        this.userPersonalInfoResource = userPersonalInfoResource
    }

    @Required
    void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri
    }

    @Required
    void setLoginStateRepository(LoginStateRepository loginStateRepository) {
        this.loginStateRepository = loginStateRepository
    }

    @Override
    Promise<Response> verifyEmail(String code, String locale) {
        if (StringUtils.isEmpty(code)) {
            throw AppExceptions.INSTANCE.missingEmailVerifyCode().exception()
        }

        EmailVerifyCode emailVerifyCode = emailVerifyCodeRepository.getAndRemove(code)

        if (emailVerifyCode == null) {
            throw AppExceptions.INSTANCE.invalidEmailVerifyCode(code).exception()
        }

        return userResource.get(new UserId(emailVerifyCode.userId), new UserGetOptions()).recover { Throwable e ->
            handleException(e)
            return Promise.pure(null)
        }.then { User user ->
            UserPersonalInfoLink emailLink = user.emails.find { UserPersonalInfoLink link -> link.isDefault }
            Assert.notNull(emailLink, 'emailLink is null')

            return userPersonalInfoResource.get(emailLink.value as UserPersonalInfoId,
                    new UserPersonalInfoGetOptions()).recover { Throwable e ->
                handleException(e)
                return Promise.pure(null)
            }.then { UserPersonalInfo emailPii ->
                Email email = ObjectMapperProvider.instance().treeToValue(emailPii.value, Email)

                if (email.info == emailVerifyCode.email) {
                    emailPii.lastValidateTime = new Date()
                    emailPii.value = ObjectMapperProvider.instance().valueToTree(email)
                    return userPersonalInfoResource.put(emailPii.id as UserPersonalInfoId, emailPii)
                            .recover { Throwable e ->
                        handleException(e)
                        return Promise.pure(null)
                    }.then { UserPersonalInfo updatedPersonalInfo ->
                        LoginState loginState = new LoginState(
                                userId: emailVerifyCode.userId,
                                lastAuthDate: new Date()
                        )

                        loginStateRepository.saveOrUpdate(loginState)


                        Response.ResponseBuilder responseBuilder = Response.status(Response.Status.FOUND)
                                .location(UriBuilder.fromUri(redirectUri).build())

                        CookieUtil.setCookie(responseBuilder, OAuthParameters.COOKIE_LOGIN_STATE, loginState.id, -1)
                        CookieUtil.setCookie(responseBuilder, OAuthParameters.COOKIE_SESSION_STATE,
                                loginState.sessionId, -1, false)

                        return Promise.pure(responseBuilder.build())
                    }
                }
            }
        }
    }

    private static void handleException(Throwable throwable) {
        LOGGER.error('Error calling the identity service', throwable)
        if (throwable instanceof AppErrorException) {
            throw throwable
        } else {
            throw AppExceptions.INSTANCE.errorCallingIdentity().exception()
        }
    }
}
