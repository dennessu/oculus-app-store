/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.service.impl

import com.junbo.common.id.ClientId
import com.junbo.common.id.EmailTemplateId
import com.junbo.common.id.UserId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.common.model.Results
import com.junbo.email.spec.model.Email
import com.junbo.email.spec.model.EmailTemplate
import com.junbo.email.spec.model.QueryParam
import com.junbo.email.spec.resource.EmailResource
import com.junbo.email.spec.resource.EmailTemplateResource
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserCredentialVerifyAttempt
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.identity.spec.v1.option.model.UserPersonalInfoGetOptions
import com.junbo.identity.spec.v1.resource.UserCredentialVerifyAttemptResource
import com.junbo.identity.spec.v1.resource.UserPersonalInfoResource
import com.junbo.identity.spec.v1.resource.UserResource
import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.core.service.TokenService
import com.junbo.oauth.core.service.UserService
import com.junbo.oauth.db.repo.EmailVerifyCodeRepository
import com.junbo.oauth.spec.model.AccessToken
import com.junbo.oauth.spec.model.EmailVerifyCode
import com.junbo.oauth.spec.model.UserInfo
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

import javax.ws.rs.core.UriBuilder

/**
 * Javadoc.
 */
@CompileStatic
class UserServiceImpl implements UserService {

    private static final String EMAIL_SOURCE = 'SilkCloud'
    private static final String EMAIL_ACTION = 'EmailVerification'
    private static final String EMAIL_VERIFY_PATH = 'oauth2/verify-email'
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl)

    private TokenService tokenService

    private UserPersonalInfoResource userPersonalInfoResource

    private UserResource userResource

    private UserCredentialVerifyAttemptResource userCredentialVerifyAttemptResource

    private EmailResource emailResource

    private EmailTemplateResource emailTemplateResource

    private EmailVerifyCodeRepository emailVerifyCodeRepository

    @Required
    void setTokenService(TokenService tokenService) {
        this.tokenService = tokenService
    }

    @Required
    void setUserResource(UserResource userResource) {
        this.userResource = userResource
    }

    @Required
    void setUserCredentialVerifyAttemptResource(UserCredentialVerifyAttemptResource
                                                        userCredentialVerifyAttemptResource) {
        this.userCredentialVerifyAttemptResource = userCredentialVerifyAttemptResource
    }

    @Required
    void setEmailResource(EmailResource emailResource) {
        this.emailResource = emailResource
    }

    @Required
    void setUserPersonalInfoResource(UserPersonalInfoResource userPersonalInfoResource) {
        this.userPersonalInfoResource = userPersonalInfoResource
    }

    @Required
    void setEmailTemplateResource(EmailTemplateResource emailTemplateResource) {
        this.emailTemplateResource = emailTemplateResource
    }

    @Required
    void setEmailVerifyCodeRepository(EmailVerifyCodeRepository emailVerifyCodeRepository) {
        this.emailVerifyCodeRepository = emailVerifyCodeRepository
    }

    @Override
    Promise<UserCredentialVerifyAttempt> authenticateUser(String username, String password,
                                                          String clientId, String ipAddress, String userAgent) {
        UserCredentialVerifyAttempt loginAttempt = new UserCredentialVerifyAttempt(
                type: 'PASSWORD',
                username: username,
                value: password,
                //TODO: remove the hard coded client id
                clientId: new ClientId(1L),
                ipAddress: ipAddress,
                userAgent: userAgent
        )

        return userCredentialVerifyAttemptResource.create(loginAttempt)
    }

    @Override
    Promise<UserInfo> getUserInfo(String authorization) {
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

        return userResource.get(new UserId(accessToken.userId), new UserGetOptions()).then { User user ->
            return this.getUserEmail(user).then { String email ->
                if (email == null) {
                    return Promise.pure(new UserInfo(sub: user.id.toString(), name: user.username, email: ''))
                }

                return Promise.pure(new UserInfo(sub: user.id.toString(), name: user.username, email: email))
            }
        }
    }

    @Override
    Promise<Void> verifyEmailByAuthHeader(String authorization, String locale, URI baseUri) {
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

        return this.verifyEmailByUserId(new UserId(accessToken.userId), locale, baseUri)
    }

    @Override
    Promise<Void> verifyEmailByUserId(UserId userId, String locale, URI baseUri) {
        if (userId == null || userId.value == null) {
            throw AppExceptions.INSTANCE.missingUserId().exception()
        }

        return userResource.get(userId, new UserGetOptions()).then { User user ->
            if (user == null) {
                throw AppExceptions.INSTANCE.errorCallingIdentity().exception()
            }

            return this.getUserEmail(user).then { String email ->
                if (email == null) {
                    throw AppExceptions.INSTANCE.errorCallingIdentity().exception()
                }

                EmailVerifyCode code = new EmailVerifyCode(
                        userId: (user.id as UserId).value,
                        email: email)

                emailVerifyCodeRepository.save(code)

                UriBuilder uriBuilder = UriBuilder.fromUri(baseUri)
                uriBuilder.path(EMAIL_VERIFY_PATH)
                uriBuilder.queryParam(OAuthParameters.CODE, code.code)
                uriBuilder.queryParam(OAuthParameters.LOCALE, locale)

                QueryParam queryParam = new QueryParam(
                        source: EMAIL_SOURCE,
                        action: EMAIL_ACTION,
                        // todo: remove hard coded locale
                        locale: 'en_US'
                )

                // TODO: cache the email template for each locale.
                return emailTemplateResource.getEmailTemplates(queryParam).then { Results<EmailTemplate> results ->
                    if (results == null || results.items == null || results.items.isEmpty()) {
                        LOGGER.warn('Failed to get the email template, skip the email send')
                        throw AppExceptions.INSTANCE.errorCallingEmail().exception()
                    }

                    EmailTemplate template = results.items.get(0)

                    Email emailToSend = new Email(
                            userId: user.id as UserId,
                            templateId: template.id as EmailTemplateId,
                            recipients: [email].asList(),
                            replacements: [
                                    'name': user.username,
                                    'verify_link': uriBuilder.build().toString()
                            ]
                    )

                    return emailResource.postEmail(emailToSend).then { Email emailSent ->
                        if (emailSent == null) {
                            LOGGER.warn('Failed to send the email, skip the email send')
                            throw AppExceptions.INSTANCE.errorCallingEmail().exception()
                        }

                        // Return success no matter the email has been successfully sent.
                        return Promise.pure(null)
                    }
                }
            }
        }
    }

    private Promise<String> getUserEmail(User user) {
        if (user == null) {
            throw AppExceptions.INSTANCE.errorCallingIdentity().exception()
        }

        for (int i = 0; i < user.emails.size(); i++) {
            def userPersonalInfoLink = user.emails.get(i)
            // if no default email, pick the last email as possible
            if (userPersonalInfoLink.isDefault || i == (user.emails.size() - 1)) {
                return userPersonalInfoResource.get(userPersonalInfoLink.value, new UserPersonalInfoGetOptions()).then { UserPersonalInfo info ->
                    if (info == null) {
                        return Promise.pure(new UserInfo(sub: user.id.toString(), name: user.username, email: ''))
                    }

                    String userEmail
                    try {
                        com.junbo.identity.spec.v1.model.Email email = ObjectMapperProvider.instance().treeToValue(info.value, com.junbo.identity.spec.v1.model.Email)
                        userEmail = email.value
                    }
                    catch (Exception e) {
                        userEmail = ''
                    }

                    return Promise.pure(userEmail)
                }
            }
        }

        return Promise.pure(null)
    }
}
