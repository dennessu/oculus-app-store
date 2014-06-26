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
import com.junbo.identity.spec.v1.model.UserCredential
import com.junbo.identity.spec.v1.model.UserCredentialVerifyAttempt
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.identity.spec.v1.option.list.UserCredentialListOptions
import com.junbo.identity.spec.v1.option.list.UserPersonalInfoListOptions
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.identity.spec.v1.option.model.UserPersonalInfoGetOptions
import com.junbo.identity.spec.v1.resource.UserCredentialResource
import com.junbo.identity.spec.v1.resource.UserCredentialVerifyAttemptResource
import com.junbo.identity.spec.v1.resource.UserPersonalInfoResource
import com.junbo.identity.spec.v1.resource.UserResource
import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.core.service.TokenService
import com.junbo.oauth.core.service.UserService
import com.junbo.oauth.db.generator.TokenGenerator
import com.junbo.oauth.db.repo.EmailVerifyCodeRepository
import com.junbo.oauth.db.repo.ResetPasswordCodeRepository
import com.junbo.oauth.spec.model.AccessToken
import com.junbo.oauth.spec.model.EmailVerifyCode
import com.junbo.oauth.spec.model.ResetPasswordCode
import com.junbo.oauth.spec.model.UserInfo
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.glassfish.jersey.server.ContainerRequest
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

    private static final String EMAIL_SOURCE = 'Oculus'
    private static final String VERIFY_EMAIL_ACTION = 'EmailVerification'
    private static final String RESET_PASSWORD_ACTION = 'PasswordReset'
    private static final String EMAIL_VERIFY_PATH = 'oauth2/verify-email'
    private static final String EMAIL_RESET_PASSWORD_PATH = 'oauth2/reset-password'
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl)

    private TokenService tokenService

    private TokenGenerator tokenGenerator

    private UserPersonalInfoResource userPersonalInfoResource

    private UserResource userResource

    private UserCredentialResource userCredentialResource

    private UserCredentialVerifyAttemptResource userCredentialVerifyAttemptResource

    private EmailResource emailResource

    private EmailTemplateResource emailTemplateResource

    private ResetPasswordCodeRepository resetPasswordCodeRepository

    private EmailVerifyCodeRepository emailVerifyCodeRepository

    @Required
    void setTokenService(TokenService tokenService) {
        this.tokenService = tokenService
    }

    @Required
    void setTokenGenerator(TokenGenerator tokenGenerator) {
        this.tokenGenerator = tokenGenerator
    }

    @Required
    void setUserResource(UserResource userResource) {
        this.userResource = userResource
    }

    @Required
    void setUserCredentialResource(UserCredentialResource userCredentialResource) {
        this.userCredentialResource = userCredentialResource
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
    void setResetPasswordCodeRepository(ResetPasswordCodeRepository resetPasswordCodeRepository) {
        this.resetPasswordCodeRepository = resetPasswordCodeRepository
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
    Promise<UserId> getUserIdByUserEmail(String userEmail) {
        return userPersonalInfoResource.list(new UserPersonalInfoListOptions(email: userEmail)).then { Results<UserPersonalInfo> results ->
            if (results == null || results.items == null || results.items.isEmpty()) {
                throw AppExceptions.INSTANCE.noAccountFound().exception()
            }

            return Promise.pure(results.items.get(0).userId)
        }
    }

    @Override
    Promise<UserCredential> getUserCredential(UserId userId) {
        return this.userCredentialResource.list(userId, new UserCredentialListOptions(type: 'PASSWORD', active: true)).then{ Results<UserCredential> results ->
            if (results == null || results.items == null || results.items.isEmpty()) {
                return Promise.pure(null)
            }

            // there is only one active userCredential
            return Promise.pure(results.items.get(0))
        }
    }

    @Override
    Promise<UserInfo> getUserInfo(String authorization) {
        if (!StringUtils.hasText(authorization)) {
            throw AppExceptions.INSTANCE.missingAuthorization().exception()
        }

        AccessToken accessToken = tokenService.extractAccessToken(authorization)

        return userResource.get(new UserId(accessToken.userId), new UserGetOptions()).then { User user ->
            return this.getDefaultUserEmail(user).then { String email ->
                if (email == null) {
                    return Promise.pure(new UserInfo(sub: user.id.toString(), name: user.username, email: ''))
                }

                return Promise.pure(new UserInfo(sub: user.id.toString(), name: user.username, email: email))
            }
        }
    }

    @Override
    Promise<Void> sendVerifyEmail(UserId userId, ActionContextWrapper contextWrapper) {
        if (userId == null || userId.value == null) {
            throw AppExceptions.INSTANCE.missingUserId().exception()
        }

        def request = (ContainerRequest) contextWrapper.request

        return userResource.get(userId, new UserGetOptions()).then { User user ->
            if (user == null) {
                throw AppExceptions.INSTANCE.errorCallingIdentity().exception()
            }

            return this.getDefaultUserEmail(user).then { String email ->
                if (email == null) {
                    throw AppExceptions.INSTANCE.missingDefaultUserEmail().exception()
                }

                EmailVerifyCode code = new EmailVerifyCode(
                        userId: (user.id as UserId).value,
                        email: email)

                emailVerifyCodeRepository.save(code)

                UriBuilder uriBuilder = UriBuilder.fromUri(request.baseUri)
                uriBuilder.path(EMAIL_VERIFY_PATH)
                uriBuilder.queryParam(OAuthParameters.EMAIL_VERIFY_CODE, code.code)
                uriBuilder.queryParam(OAuthParameters.LOCALE, contextWrapper.viewLocale)
                uriBuilder.queryParam(OAuthParameters.COUNTRY, contextWrapper.viewCountry)

                QueryParam queryParam = new QueryParam(
                        source: EMAIL_SOURCE,
                        action: VERIFY_EMAIL_ACTION,
                        locale: contextWrapper.viewLocale
                )

                String link = uriBuilder.build().toString()
                contextWrapper.emailVerifyLink = link
                return this.sendEmail(queryParam, user, email, link)
            }
        }
    }

    @Override
    Promise<String> sendResetPassword(UserId userId, String locale, String country, URI baseUri) {
        if (userId == null || userId.value == null) {
            throw AppExceptions.INSTANCE.missingUserId().exception()
        }

        return userResource.get(userId, new UserGetOptions()).then { User user ->
            if (user == null) {
                throw AppExceptions.INSTANCE.errorCallingIdentity().exception()
            }

            return this.getDefaultUserEmail(user).then { String email ->
                if (email == null) {
                    throw AppExceptions.INSTANCE.missingDefaultUserEmail().exception()
                }

                ResetPasswordCode code = new ResetPasswordCode(
                        userId: userId.value,
                        email: email
                )

                resetPasswordCodeRepository.save(code)

                UriBuilder uriBuilder = UriBuilder.fromUri(baseUri)
                uriBuilder.path(EMAIL_RESET_PASSWORD_PATH)
                uriBuilder.queryParam(OAuthParameters.RESET_PASSWORD_CODE, code.code)
                if (!StringUtils.isEmpty(locale)) {
                    uriBuilder.queryParam(OAuthParameters.LOCALE, locale)
                }
                if (!StringUtils.isEmpty(country)) {
                    uriBuilder.queryParam(OAuthParameters.COUNTRY, country)
                }

                QueryParam queryParam = new QueryParam(
                        source: EMAIL_SOURCE,
                        action: RESET_PASSWORD_ACTION,
                        locale: locale
                )

                return this.sendEmail(queryParam, user, email, uriBuilder.build().toString())
            }
        }
    }

    @Override
    Promise<String> sendResetPassword(UserId userId, ActionContextWrapper contextWrapper) {
        String locale = contextWrapper.viewLocale
        String country = contextWrapper.viewCountry
        def request = (ContainerRequest) contextWrapper.request
        URI baseUri = request.baseUri

        return sendResetPassword(userId, locale, country, baseUri)
    }

    private Promise<String> getDefaultUserEmail(User user) {
        if (user == null) {
            throw AppExceptions.INSTANCE.errorCallingIdentity().exception()
        }

        for (int i = 0; i < user.emails.size(); i++) {
            def userPersonalInfoLink = user.emails.get(i)
            // use default email only, the email doesn't need to be verified at the moment
            // when sending verify-email email and reset-password email
            if (userPersonalInfoLink.isDefault) {
                return userPersonalInfoResource.get(userPersonalInfoLink.value, new UserPersonalInfoGetOptions()).then { UserPersonalInfo info ->
                    if (info == null) {
                        return Promise.pure(new UserInfo(sub: user.id.toString(), name: user.username, email: ''))
                    }

                    String userEmail
                    try {
                        com.junbo.identity.spec.v1.model.Email email = ObjectMapperProvider.instance().treeToValue(info.value, com.junbo.identity.spec.v1.model.Email)
                        userEmail = email.info
                    }
                    catch (Exception e) {
                        return Promise.pure(null)
                    }

                    return Promise.pure(userEmail)
                }
            }
        }

        return Promise.pure(null)
    }

    private Promise<String> sendEmail(QueryParam queryParam, User user, String email, String uri) {
        // todo: remove this hard coded after email template has been setup
        queryParam.locale = 'en_US'

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
                            'link': uri
                    ]
            )

            return emailResource.postEmail(emailToSend).then { Email emailSent ->
                if (emailSent == null) {
                    LOGGER.warn('Failed to send the email, skip the email send')
                    throw AppExceptions.INSTANCE.errorCallingEmail().exception()
                }

                // Return success no matter the email has been successfully sent.
                return Promise.pure(uri)
            }
        }
    }
}
