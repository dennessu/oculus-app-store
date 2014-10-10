/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.service.impl

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.ClientId
import com.junbo.common.id.EmailTemplateId
import com.junbo.common.id.UserId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.common.model.Results
import com.junbo.email.spec.model.Email
import com.junbo.email.spec.model.EmailTemplate
import com.junbo.email.spec.model.QueryParam
import com.junbo.email.spec.resource.EmailResource
import com.junbo.email.spec.resource.EmailTemplateResource
import com.junbo.identity.spec.v1.model.*
import com.junbo.identity.spec.v1.option.list.UserCredentialListOptions
import com.junbo.identity.spec.v1.option.list.UserListOptions
import com.junbo.identity.spec.v1.option.list.UserPersonalInfoListOptions
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.identity.spec.v1.option.model.UserPersonalInfoGetOptions
import com.junbo.identity.spec.v1.resource.UserCredentialResource
import com.junbo.identity.spec.v1.resource.UserCredentialVerifyAttemptResource
import com.junbo.identity.spec.v1.resource.UserPersonalInfoResource
import com.junbo.identity.spec.v1.resource.UserResource
import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.service.OAuthTokenService
import com.junbo.oauth.core.service.UserService
import com.junbo.oauth.db.generator.TokenGenerator
import com.junbo.oauth.db.repo.EmailVerifyCodeRepository
import com.junbo.oauth.db.repo.ResetPasswordCodeRepository
import com.junbo.oauth.spec.error.AppErrors
import com.junbo.oauth.spec.model.AccessToken
import com.junbo.oauth.spec.model.EmailVerifyCode
import com.junbo.oauth.spec.model.ResetPasswordCode
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

    private static final String EMAIL_SOURCE = 'Oculus'
    private static final String VERIFY_EMAIL_ACTION = 'EmailVerification_V1'
    private static final String WELCOME_ACTION = 'Welcome_V1'
    private static final String RESET_PASSWORD_ACTION = 'PasswordReset_V1'
    private static final String EMAIL_VERIFY_PATH = 'oauth2/verify-email'
    private static final String EMAIL_RESET_PASSWORD_PATH = 'oauth2/reset-password'
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl)

    private OAuthTokenService tokenService

    private TokenGenerator tokenGenerator

    private UserPersonalInfoResource userPersonalInfoResource

    private UserResource userResource

    private UserCredentialResource userCredentialResource

    private UserCredentialVerifyAttemptResource userCredentialVerifyAttemptResource

    private EmailResource emailResource

    private EmailTemplateResource emailTemplateResource

    private ResetPasswordCodeRepository resetPasswordCodeRepository

    private EmailVerifyCodeRepository emailVerifyCodeRepository

    private URI emailLinkBaseUri

    @Required
    void setTokenService(OAuthTokenService tokenService) {
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

    @Required
    void setEmailLinkBaseUri(String emailLinkBaseUri) {
        this.emailLinkBaseUri = new URI(emailLinkBaseUri)
    }

    @Override
    Promise<UserCredentialVerifyAttempt> authenticateUser(String username, String password,
                                                          String clientId, String ipAddress, String userAgent) {
        UserCredentialVerifyAttempt loginAttempt = new UserCredentialVerifyAttempt(
                type: 'PASSWORD',
                username: username,
                value: password,
                clientId: new ClientId(clientId),
                ipAddress: ipAddress,
                userAgent: userAgent
        )

        return userCredentialVerifyAttemptResource.create(loginAttempt)
    }

    @Override
    Promise<UserId> getUserIdByUserEmail(String userEmail) {
        return userPersonalInfoResource.list(new UserPersonalInfoListOptions(email: userEmail)).then { Results<UserPersonalInfo> results ->
            if (results == null || results.items == null || results.items.isEmpty()) {
                throw AppErrors.INSTANCE.noAccountFound().exception()
            }

            return Promise.pure(results.items.get(0).userId)
        }
    }

    @Override
    Promise<UserId> getUserIdByUsername(String username) {
        return userResource.list(new UserListOptions(username: username)).then { Results<User> userResults ->
            if (userResults == null || userResults.items == null || userResults.items.isEmpty()) {
                throw AppErrors.INSTANCE.noAccountFound().exception()
            }

            return Promise.pure(userResults.items.get(0).id as UserId)
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
            throw AppErrors.INSTANCE.missingAuthorization().exception()
        }

        AccessToken accessToken = tokenService.extractAccessToken(authorization)

        return userResource.get(new UserId(accessToken.userId), new UserGetOptions()).then { User user ->
            return this.getDefaultUserEmail(user).then { String email ->
                if (email == null) {
                    return Promise.pure(new UserInfo(sub: user.id.toString(), name: getName(user).get(), email: ''))
                }

                return Promise.pure(new UserInfo(sub: user.id.toString(), name: getName(user).get(), email: email))
            }
        }
    }

    @Override
    Promise<String> sendWelcomeEmail(UserId userId, ActionContextWrapper contextWrapper) {
        String locale = contextWrapper.viewLocale
        String country = contextWrapper.viewCountry

        return sendVerifyEmail(userId, locale, country, true).then { String link ->
            contextWrapper.emailVerifyLink = link
            return Promise.pure(link)
        }
    }

    @Override
    Promise<String> sendVerifyEmail(UserId userId, ActionContextWrapper contextWrapper) {
        String locale = contextWrapper.viewLocale
        String country = contextWrapper.viewCountry

        return sendVerifyEmail(userId, locale, country, null).then { String link ->
            contextWrapper.emailVerifyLink = link
            return Promise.pure(link)
        }
    }

    @Override
    Promise<String> getUserEmailByUserId(UserId userId) {
        if (userId == null || userId.value == null) {
            throw AppErrors.INSTANCE.missingUserId().exception()
        }

        return userResource.get(userId, new UserGetOptions()).then { User user ->
            if (user == null) {
                throw AppErrors.INSTANCE.errorCallingIdentity().exception()
            }

            return this.getDefaultUserEmail(user).then { String email ->
                if (email == null) {
                    throw AppErrors.INSTANCE.missingDefaultUserEmail().exception()
                }

                return Promise.pure(email)
            }
        }
    }

    @Override
    Promise<String> buildResponseLink(EmailVerifyCode emailVerifyCode) {
        UriBuilder uriBuilder = UriBuilder.fromUri(emailLinkBaseUri)
        uriBuilder.path(EMAIL_VERIFY_PATH)
        uriBuilder.queryParam(OAuthParameters.EMAIL_VERIFY_CODE, emailVerifyCode.code)
        uriBuilder.queryParam(OAuthParameters.LOCALE, 'en_US')
        String link = uriBuilder.build().toString()

        return Promise.pure(link)
    }

    @Override
    Promise<String> sendVerifyEmail(UserId userId, String locale, String country, UserPersonalInfoId emailId, Boolean welcome) {
        return userResource.get(userId, new UserGetOptions()).then { User user ->
            if (user == null) {
                throw AppErrors.INSTANCE.errorCallingIdentity().exception()
            }
            return getMail(user, emailId).then { String email ->
                EmailVerifyCode code = new EmailVerifyCode(
                        userId: userId.value,
                        email: email,
                        targetMailId: emailId == null ? null : emailId.value
                )

                emailVerifyCodeRepository.save(code)

                UriBuilder uriBuilder = UriBuilder.fromUri(emailLinkBaseUri)
                uriBuilder.path(EMAIL_VERIFY_PATH)
                uriBuilder.queryParam(OAuthParameters.EMAIL_VERIFY_CODE, code.code)
                uriBuilder.queryParam(OAuthParameters.LOCALE, locale)

                QueryParam queryParam
                if (welcome == null || !welcome) {
                    queryParam = new QueryParam(
                            source: EMAIL_SOURCE,
                            action: VERIFY_EMAIL_ACTION,
                            locale: locale
                    )
                }
                else {
                    queryParam = new QueryParam(
                            source: EMAIL_SOURCE,
                            action: WELCOME_ACTION,
                            locale: locale
                    )
                }

                String link = uriBuilder.build().toString()
                return this.sendEmail(queryParam, user, email, link, buildReplacements(user, queryParam.action, link, email))
            }
        }
    }


    @Override
    Promise<String> sendVerifyEmail(UserId userId, String locale, String country, Boolean welcome) {
        if (userId == null || userId.value == null) {
            throw AppErrors.INSTANCE.missingUserId().exception()
        }

        return userResource.get(userId, new UserGetOptions()).then { User user ->
            if (user == null) {
                throw AppErrors.INSTANCE.errorCallingIdentity().exception()
            }

            return sendVerifyEmail(userId, locale, country, null, welcome)
        }
    }

    @Override
    Promise<String> sendResetPassword(UserId userId, String locale, String country) {
        if (userId == null || userId.value == null) {
            throw AppErrors.INSTANCE.missingUserId().exception()
        }

        return userResource.get(userId, new UserGetOptions()).then { User user ->
            if (user == null) {
                throw AppErrors.INSTANCE.errorCallingIdentity().exception()
            }

            return this.getDefaultUserEmail(user).then { String email ->
                if (email == null) {
                    throw AppErrors.INSTANCE.missingDefaultUserEmail().exception()
                }

                ResetPasswordCode code = new ResetPasswordCode(
                        userId: userId.value,
                        email: email
                )

                resetPasswordCodeRepository.save(code)

                UriBuilder uriBuilder = UriBuilder.fromUri(emailLinkBaseUri)
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

                String uri = uriBuilder.build().toString()
                return this.sendEmail(queryParam, user, email, uri, buildReplacements(user, queryParam.action, uri, email))
            }
        }
    }

    @Override
    Promise<String> sendResetPassword(UserId userId, ActionContextWrapper contextWrapper) {
        String locale = contextWrapper.viewLocale
        String country = contextWrapper.viewCountry

        return sendResetPassword(userId, locale, country)
    }

    @Override
    Promise<List<String>> getResetPasswordLinks(String username, String email, String locale, String country) {
        List<String> results = new ArrayList<>()
        return userResource.list(new UserListOptions(username: username)).then { Results<User> userResults ->
            userResults.items.each { User user ->
                List<ResetPasswordCode> codes = resetPasswordCodeRepository.getByUserIdEmail(user.getId().value, email)
                codes.each { ResetPasswordCode code ->
                    UriBuilder uriBuilder = UriBuilder.fromUri(emailLinkBaseUri)
                    uriBuilder.path(EMAIL_RESET_PASSWORD_PATH)
                    uriBuilder.queryParam(OAuthParameters.RESET_PASSWORD_CODE, code.code)
                    if (!StringUtils.isEmpty(locale)) {
                        uriBuilder.queryParam(OAuthParameters.LOCALE, locale)
                    }
                    if (!StringUtils.isEmpty(country)) {
                        uriBuilder.queryParam(OAuthParameters.COUNTRY, country)
                    }

                    results.add(uriBuilder.build().toString())
                }
            }

            return Promise.pure()
        }.then {
            return Promise.pure(results)
        }
    }

    @Override
    Promise<Boolean> checkUsernameAndEmailBlocker(String username, String email) {
        return userResource.checkUsernameEmailBlocker(username, email)
    }

    private Promise<String> getDefaultUserEmail(User user) {
        if (user == null) {
            throw AppErrors.INSTANCE.errorCallingIdentity().exception()
        }

        for (int i = 0; i < user.emails.size(); i++) {
            def userPersonalInfoLink = user.emails.get(i)
            // use default email only, the email doesn't need to be verified at the moment
            // when sending verify-email email and reset-password email
            if (userPersonalInfoLink.isDefault) {
                return userPersonalInfoResource.get(userPersonalInfoLink.value, new UserPersonalInfoGetOptions()).then { UserPersonalInfo info ->
                    if (info == null) {
                        return Promise.pure(new UserInfo(sub: user.id.toString(), name: getName(user).get(), email: ''))
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

    private Promise<String> getMail(User user, UserPersonalInfoId mail) {
        if (mail == null) {
            return getDefaultUserEmail(user)
        } else {
            return userPersonalInfoResource.get(mail, new UserPersonalInfoGetOptions()).then { UserPersonalInfo info ->
                if (info == null) {
                    return Promise.pure(new UserInfo(sub: user.id.toString(), name: getName(user).get(), email: ''))
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

    private Promise<String> sendEmail(QueryParam queryParam, User user, String email, String uri, Map<String, String> replacements) {
        // todo: remove this hard coded after email template has been setup
        queryParam.locale = 'en_US'

        // TODO: cache the email template for each locale.
        return emailTemplateResource.getEmailTemplates(queryParam).then { Results<EmailTemplate> results ->
            if (results == null || results.items == null || results.items.isEmpty()) {
                LOGGER.warn('Failed to get the email template, skip the email send')
                throw AppErrors.INSTANCE.errorCallingEmail().exception()
            }

            EmailTemplate template = results.items.get(0)

            Email emailToSend = new Email(
                    userId: user.id as UserId,
                    templateId: template.id as EmailTemplateId,
                    recipients: [email].asList(),
                    replacements: replacements
            )

            return emailResource.postEmail(emailToSend).then { Email emailSent ->
                if (emailSent == null) {
                    LOGGER.warn('Failed to send the email, skip the email send')
                    throw AppErrors.INSTANCE.errorCallingEmail().exception()
                }

                // Return success no matter the email has been successfully sent.
                return Promise.pure(uri)
            }
        }
    }

    private Promise<String> getName(User user) {
        if (user.name == null) {
            return Promise.pure('')
        } else {
            return userPersonalInfoResource.get(user.name, new UserPersonalInfoGetOptions()).then { UserPersonalInfo userPersonalInfo ->
                if (userPersonalInfo == null) {
                    return Promise.pure('')
                }

                UserName userName = (UserName)jsonNodeToObj(userPersonalInfo.value, UserName)
                String firstName = StringUtils.isEmpty(userName.givenName) ? "" : userName.givenName
                String lastName = StringUtils.isEmpty(userName.familyName) ? "" : userName.familyName
                return Promise.pure(firstName + ' ' + lastName)
            }
        }
    }

    private Promise<String> getFirstName(User user) {
        if (user.name == null) {
            return Promise.pure('')
        } else {
            return userPersonalInfoResource.get(user.name, new UserPersonalInfoGetOptions()).then { UserPersonalInfo userPersonalInfo ->
                if (userPersonalInfo == null) {
                    return Promise.pure('')
                }

                UserName userName = (UserName)jsonNodeToObj(userPersonalInfo.value, UserName)
                return Promise.pure(userName.givenName)
            }
        }
    }

    public static Object jsonNodeToObj(JsonNode jsonNode, Class cls) {
        try {
            return ObjectMapperProvider.instance().treeToValue(jsonNode, cls);
        } catch (Exception e) {
            throw AppCommonErrors.INSTANCE.internalServerError(new Exception('Cannot convert JsonObject to ' + cls.toString() + ' e: ' + e.getMessage())).exception()
        }
    }

    // todo:    We may need to refine the code here to use interface...
    private Map<String, String> buildReplacements(User user, String action, String link, String email) {
        if (action == VERIFY_EMAIL_ACTION) {
            return [
                'firstName': getFirstName(user).get(),
                'link': link
            ]
        } else if (action == WELCOME_ACTION) {
            return [
                'firstName': getFirstName(user).get(),
                'accountName': email,
                'link': link
            ]
        } else if (action == RESET_PASSWORD_ACTION) {
            return [
                'name': getName(user).get(),
                'link': link
            ]
        } else {
            throw AppCommonErrors.INSTANCE.invalidOperation('Unsupported operation').exception()
        }
    }
}
