/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.api.endpoint

import com.junbo.authorization.AuthorizeContext
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.csr.spec.model.CsrLog
import com.junbo.csr.spec.resource.CsrLogResource
import com.junbo.identity.spec.v1.model.Email
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.identity.spec.v1.model.UserPersonalInfoLink
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.identity.spec.v1.option.model.UserPersonalInfoGetOptions
import com.junbo.identity.spec.v1.resource.UserPersonalInfoResource
import com.junbo.identity.spec.v1.resource.UserResource
import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.core.exception.AppErrors
import com.junbo.oauth.core.service.UserService
import com.junbo.oauth.core.util.CookieUtil
import com.junbo.oauth.core.util.ValidatorUtil
import com.junbo.oauth.db.repo.EmailVerifyCodeRepository
import com.junbo.oauth.db.repo.LoginStateRepository
import com.junbo.oauth.spec.endpoint.EmailVerifyEndpoint
import com.junbo.oauth.spec.model.EmailVerifyCode
import com.junbo.oauth.spec.model.LoginState
import com.junbo.oauth.spec.param.OAuthParameters
import com.junbo.csr.spec.def.CsrLogActionType
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert
import org.springframework.util.StringUtils

import javax.ws.rs.core.Response
import javax.ws.rs.core.UriBuilder
/**
 * EmailVerifyEndpointImpl.
 */
@CompileStatic
class EmailVerifyEndpointImpl implements EmailVerifyEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailVerifyEndpointImpl)
    private EmailVerifyCodeRepository emailVerifyCodeRepository
    private UserResource userResource
    private UserPersonalInfoResource userPersonalInfoResource
    private UserService userService
    private CsrLogResource csrLogResource

    private String successRedirectUri
    private String failedRedirectUri
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
    void setCsrLogResource(CsrLogResource csrLogResource) {
        this.csrLogResource = csrLogResource
    }

    @Required
    void setSuccessRedirectUri(String successRedirectUri) {
        this.successRedirectUri = successRedirectUri
    }

    @Required
    void setFailedRedirectUri(String failedRedirectUri) {
        this.failedRedirectUri = failedRedirectUri
    }

    @Required
    void setLoginStateRepository(LoginStateRepository loginStateRepository) {
        this.loginStateRepository = loginStateRepository
    }

    @Required
    void setUserService(UserService userService) {
        this.userService = userService
    }

    @Override
    Promise<Response> verifyEmail(String code, String locale) {
        if (StringUtils.isEmpty(locale)) {
            locale = 'en-US'
        }

        if (!StringUtils.isEmpty(locale)) {
            if (ValidatorUtil.isValidLocale(locale)) {
                this.failedRedirectUri = this.failedRedirectUri.replaceFirst('/locale', '/' + locale)
                this.successRedirectUri = this.successRedirectUri.replaceFirst('/locale', '/' + locale)
            }

            locale = locale.replace('-', '_')
            String[] parts = locale.split('_')
            switch (parts.length) {
                case 3: case 2:
                    this.failedRedirectUri = this.failedRedirectUri.replaceFirst('/country', '/' + parts[1])
                    this.successRedirectUri = this.successRedirectUri.replaceFirst('/country', '/' + parts[1])
                default:
                    break
            }
        }

        if (StringUtils.isEmpty(code)) {
            LOGGER.warn(AppCommonErrors.INSTANCE.fieldRequired('evc').toString())
            Response.ResponseBuilder responseBuilder = Response.status(Response.Status.FOUND)
                    .location(UriBuilder.fromUri(failedRedirectUri).build())
            return Promise.pure(responseBuilder.build())
        }

        EmailVerifyCode emailVerifyCode = emailVerifyCodeRepository.getAndRemove(code)

        if (emailVerifyCode == null) {
            LOGGER.warn(AppErrors.INSTANCE.invalidEmailVerifyCode().toString())
            Response.ResponseBuilder responseBuilder = Response.status(Response.Status.FOUND)
                    .location(UriBuilder.fromUri(failedRedirectUri).build())
            return Promise.pure(responseBuilder.build())
        }

        emailVerifyCodeRepository.removeByUserIdEmail(emailVerifyCode.userId, emailVerifyCode.email)

        return userResource.get(new UserId(emailVerifyCode.userId), new UserGetOptions()).recover { Throwable e ->
            return handleException(e)
        }.then { User user ->
            UserPersonalInfoLink emailLink = user.emails.find { UserPersonalInfoLink link -> link.isDefault }
            Assert.notNull(emailLink, 'emailLink is null')

            return userPersonalInfoResource.get(emailLink.value as UserPersonalInfoId,
                    new UserPersonalInfoGetOptions()).recover { Throwable e ->
                return handleException(e)
            }.then { UserPersonalInfo emailPii ->
                Email email = ObjectMapperProvider.instance().treeToValue(emailPii.value, Email)

                if (email.info == emailVerifyCode.email) {
                    emailPii.lastValidateTime = new Date()
                    emailPii.value = ObjectMapperProvider.instance().valueToTree(email)
                    return userPersonalInfoResource.put(emailPii.id as UserPersonalInfoId, emailPii)
                            .recover { Throwable e ->
                        return handleException(e)
                    }.then { UserPersonalInfo updatedPersonalInfo ->
                        LoginState loginState = new LoginState(
                                userId: emailVerifyCode.userId,
                                lastAuthDate: new Date()
                        )

                        loginStateRepository.save(loginState)

                        Response.ResponseBuilder responseBuilder = Response.status(Response.Status.FOUND)
                                .location(UriBuilder.fromUri(successRedirectUri).build())

                        CookieUtil.setCookie(responseBuilder, OAuthParameters.COOKIE_LOGIN_STATE, loginState.getId(), -1)
                        CookieUtil.setCookie(responseBuilder, OAuthParameters.COOKIE_SESSION_STATE,
                                loginState.sessionId, -1, false)

                        return Promise.pure(responseBuilder.build())
                    }
                }
            }
        }
    }

    @Override
    Promise<Response> sendVerifyEmail(String locale, String country, UserId userId) {
        return userService.sendVerifyEmail(userId, locale, country, null).then {
            // audit csr action on success
            csrActionAudit(userId)
            return Promise.pure(Response.noContent().build())
        }
    }

    @Override
    Promise<Response> sendVerifyEmail(String locale, String country, UserId userId, String targetMail) {
        // todo:    This should be only service api can access
        return userService.sendVerifyEmail(userId, locale, country, targetMail, null).then {
            // audit csr action on success
            csrActionAudit(userId)
            return Promise.pure(Response.noContent().build())
        }
    }

    private Promise<Response> handleException(Throwable throwable) {
        LOGGER.error('Error calling the identity service', throwable)
        Response.ResponseBuilder responseBuilder = Response.status(Response.Status.FOUND)
                .location(UriBuilder.fromUri(failedRedirectUri).build())
        return Promise.pure(responseBuilder.build())
    }

    private void csrActionAudit(UserId userId) {
        if (AuthorizeContext.hasScopes('csr') && AuthorizeContext.currentUserId != null) {
            String email = userService.getUserEmailByUserId(userId).get()
            csrLogResource.create(new CsrLog(userId: AuthorizeContext.currentUserId, regarding: 'Account', action: CsrLogActionType.VerificationEmailSent, property: email)).get()
        }
    }
}
