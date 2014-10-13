/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.api.endpoint

import com.junbo.authorization.AuthorizeContext
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.error.AppError
import com.junbo.common.id.UserId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.csr.spec.def.CsrLogActionType
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
import com.junbo.langur.core.context.JunboHttpContext
import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.spec.error.AppErrors
import com.junbo.oauth.core.service.UserService
import com.junbo.oauth.core.util.CookieUtil
import com.junbo.oauth.core.util.ValidatorUtil
import com.junbo.oauth.db.repo.EmailVerifyCodeRepository
import com.junbo.oauth.db.repo.LoginStateRepository
import com.junbo.oauth.spec.endpoint.EmailVerifyEndpoint
import com.junbo.oauth.spec.model.EmailVerifyCode
import com.junbo.oauth.spec.model.LoginState
import com.junbo.oauth.spec.model.ViewModel
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.apache.commons.collections.CollectionUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert
import org.springframework.util.StringUtils

import javax.ws.rs.core.MediaType
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
        if (StringUtils.isEmpty(locale) || !ValidatorUtil.isValidLocale(locale)) {
            locale = 'en-US'
        }

        String failedUri = this.failedRedirectUri.replaceFirst('/locale', '/' + locale)
        String successUri = this.successRedirectUri.replaceFirst('/locale', '/' + locale)

        locale = locale.replace('-', '_')
        String[] parts = locale.split('_')
        assert parts.length == 2 : 'locale should consist of 2 parts'
        failedUri = failedUri.replaceFirst('/country', '/' + parts[1])
        successUri = successUri.replaceFirst('/country', '/' + parts[1])

        if (StringUtils.isEmpty(code)) {
            return Promise.pure(response(failedUri, false, locale, null, AppCommonErrors.INSTANCE.fieldRequired('evc')).build())
        }

        EmailVerifyCode emailVerifyCode = emailVerifyCodeRepository.getAndRemove(code)

        if (emailVerifyCode == null) {
            return Promise.pure(response(failedUri, false, locale, null, AppCommonErrors.INSTANCE.fieldInvalid('evc')).build())
        }

        emailVerifyCodeRepository.removeByUserIdEmail(emailVerifyCode.userId, emailVerifyCode.email)

        try {
            User user = userResource.get(new UserId(emailVerifyCode.userId), new UserGetOptions()).get()

            UserPersonalInfo emailPii = getEmailPii(user, emailVerifyCode)
            Email email = ObjectMapperProvider.instance().treeToValue(emailPii.value, Email)

            emailPii.lastValidateTime = new Date()
            emailPii.value = ObjectMapperProvider.instance().valueToTree(email)
            userPersonalInfoResource.put(emailPii.getId(), emailPii).get()

            if (isUpdatePrimaryMailRequired(user, emailVerifyCode)) {
                User existing = userResource.get(user.getId(), new UserGetOptions()).get()
                UserPersonalInfoLink defaultLink = existing.emails.find { UserPersonalInfoLink link ->
                    return link.isDefault
                }
                if (defaultLink == null) {
                    defaultLink = new UserPersonalInfoLink()
                    defaultLink.isDefault = true
                    defaultLink.value = new UserPersonalInfoId(emailVerifyCode.targetMailId)
                    existing.emails.add(defaultLink)
                } else {
                    defaultLink.value = new UserPersonalInfoId(emailVerifyCode.targetMailId)
                }

                userResource.put(user.getId(), existing).get()
            }
            
            LoginState loginState = new LoginState(
                    userId: emailVerifyCode.userId,
                    lastAuthDate: new Date()
            )

            loginStateRepository.save(loginState)
            def responseBuilder = response(successUri, true, locale, email, null)
            CookieUtil.setCookie(responseBuilder, OAuthParameters.COOKIE_LOGIN_STATE, loginState.loginStateId, -1)
            CookieUtil.setCookie(responseBuilder, OAuthParameters.COOKIE_SESSION_STATE,
                    loginState.sessionId, -1, false)

            return Promise.pure(responseBuilder.build())
        } catch (Exception e) {
            LOGGER.error('Error calling the identity service', e)
            return Promise.pure(response(failedUri, false, locale, null, AppErrors.INSTANCE.errorCallingIdentity()).build())
        }
    }

    @Override
    Promise<List<String>> getVerifyEmailLink(UserId userId, String locale, String email) {
        List<String> links = new ArrayList<>();
        List<EmailVerifyCode> verifyCodeList = emailVerifyCodeRepository.getByUserIdEmail(userId.value, email)
        if (CollectionUtils.isEmpty(verifyCodeList)) {
            return Promise.pure(links)
        }

        return Promise.each(verifyCodeList) { EmailVerifyCode code ->
            return userService.buildResponseLink(code).then { String link ->
                if (!StringUtils.isEmpty(link)) {
                    links.add(link)
                }

                return Promise.pure(null)
            }
        }.then {
            return Promise.pure(links)
        }
    }

    @Override
    Promise<Response> sendVerifyEmail(String locale, String country, UserId userId, UserPersonalInfoId targetMail) {
        return userService.sendVerifyEmail(userId, locale, country, targetMail, null).then {
            // audit csr action on success
            csrActionAudit(userId)
            return Promise.pure(Response.noContent().build())
        }
    }

    @Override
    Promise<Response> sendWelcomeEmail(String locale, String country, UserId userId) {
        return userService.sendVerifyEmail(userId, locale, country, null, true).then {
            // CSR shouldn't invoke send welcome email
            return Promise.pure(Response.noContent().build())
        }
    }

    private void csrActionAudit(UserId userId) {
        if (AuthorizeContext.hasScopes('csr') && AuthorizeContext.currentUserId != null) {
            String email = userService.getUserEmailByUserId(userId).get()
            csrLogResource.create(new CsrLog(userId: AuthorizeContext.currentUserId, regarding: 'Account', action: CsrLogActionType.VerificationEmailSent, property: email)).get()
        }
    }

    private Boolean isUpdatePrimaryMailRequired(User user, EmailVerifyCode verifyCode) {
        if (verifyCode.targetMailId == null) {
            return false
        }

        if (CollectionUtils.isEmpty(user.emails)) {
            return false
        }

        UserPersonalInfoLink defaultLink = user.emails.find { UserPersonalInfoLink link ->
            return link.isDefault
        }

        if (defaultLink != null && defaultLink.value.value != verifyCode.targetMailId) {
            return true
        }

        return false
    }

    private UserPersonalInfo getEmailPii(User user, EmailVerifyCode verifyCode) {
        if (verifyCode.targetMailId == null) {
            UserPersonalInfoLink emailLink = user.emails.find { UserPersonalInfoLink link -> link.isDefault }
            Assert.notNull(emailLink, 'emailLink is null')
            verifyCode.targetMailId = emailLink.getValue().getValue()
        }

        UserPersonalInfo userPersonalInfo = userPersonalInfoResource.get(new UserPersonalInfoId(verifyCode.targetMailId),
                new UserPersonalInfoGetOptions()).get()
        checkMailValid(userPersonalInfo, verifyCode.email)
        return userPersonalInfo
    }

    private void checkMailValid(UserPersonalInfo mailPII, String email) {
        Assert.notNull(mailPII, 'default email is null')

        Email emailObj = ObjectMapperProvider.instance().treeToValue(mailPII.value, Email)

        if (emailObj.info != email) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('userId').exception()
        }
    }

    private
    static Response.ResponseBuilder response(String redirectUri, boolean success, String locale, Email email, AppError error) {
        String accept = JunboHttpContext.requestHeaders.getFirst('Accept')
        if (!StringUtils.isEmpty(accept) && accept.contains(MediaType.APPLICATION_JSON)) {
            ViewModel response = new ViewModel(
                    view: 'emailVerify',
                    model: ['verifyResult': success, 'locale': locale] as Map,
                    errors: []
            )

            if (email != null) {
                response.model['email'] = email.info
            }

            if (error != null) {
                response.errors << error.error()
            }

            return Response.ok(response)
        }

        return Response.status(Response.Status.FOUND).location(UriBuilder.fromUri(redirectUri).build())
    }
}
