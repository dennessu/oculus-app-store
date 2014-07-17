/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.exception

import com.junbo.common.error.AppError
import com.junbo.common.error.ErrorDef
import com.junbo.common.error.ErrorProxy
import groovy.transform.CompileStatic

/**
 * AppExceptions.
 */
@CompileStatic
interface AppExceptions {
    static final AppExceptions INSTANCE = ErrorProxy.newProxyInstance(AppExceptions)

    @ErrorDef(httpStatusCode = 400, code = '20001', message = 'The client_id {0} is invalid', field = 'client_id')
    AppError invalidClientId(String clientId)

    @ErrorDef(httpStatusCode = 400, code = '20002', message = 'The client_id parameter is missing',
            field = 'client_id')
    AppError missingClientId()

    @ErrorDef(httpStatusCode = 400, code = '20003',
            message = 'The refresh token\'s client id {0} is different from the parameter client_id {1}',
            field = 'client_id')
    AppError differentClientId(String tokenClientId, String paramClientId)

    @ErrorDef(httpStatusCode = 400, code = '20004', message = 'The client_secret {0} is invalid',
            field = 'client_secret')
    AppError invalidClientSecret(String clientSecret)

    @ErrorDef(httpStatusCode = 400, code = '20005', message = 'The client_secret parameter is missing',
            field = 'client_secret')
    AppError missingClientSecret()

    @ErrorDef(httpStatusCode = 400, code = '20006', message = 'The scope {0} is invalid', field = 'scope')
    AppError invalidScope(String scopes)

    @ErrorDef(httpStatusCode = 400, code = '20007', message = 'The scope parameter is missing', field = 'scope')
    AppError missingScope()

    @ErrorDef(httpStatusCode = 400, code = '20008',
            message = 'The scope parameter must be a subset of the original access token\'s scopes',
            field = 'scope')
    AppError outboundScope()

    @ErrorDef(httpStatusCode = 400, code = '20009', message = 'The redirect_uri {0} is invalid',
            field = 'redirect_uri')
    AppError invalidRedirectUri(String redirectUri)

    @ErrorDef(httpStatusCode = 400, code = '20010', message = 'The redirect_uri parameter is missing',
            field = 'redirect_uri')
    AppError missingRedirectUri()

    @ErrorDef(httpStatusCode = 400, code = '20011', message = 'The prompt {0} is invalid', field = 'prompt')
    AppError invalidPrompt(String prompt)

    @ErrorDef(httpStatusCode = 400, code = '20012', message = 'The display {0} is invalid', field = 'display')
    AppError invalidDisplay(String display)

    @ErrorDef(httpStatusCode = 400, code = '20015', message = 'The response_type {0} is invalid',
            field = 'response_type')
    AppError invalidResponseType(String responseType)

    @ErrorDef(httpStatusCode = 400, code = '20016', message = 'The response_type parameter is missing',
            field = 'response_type')
    AppError missingResponseType()

    @ErrorDef(httpStatusCode = 400, code = '20017', message = 'The nonce parameter is missing',
            field = 'nonce')
    AppError missingNonce()

    @ErrorDef(httpStatusCode = 400, code = '20018', message = 'The grant_type {0} is invalid',
            field = 'grant_type')
    AppError invalidGrantType(String grantType)

    @ErrorDef(httpStatusCode = 400, code = '20019', message = 'The grant_type parameter is missing',
            field = 'grant_type')
    AppError missingGrantType()

    @ErrorDef(httpStatusCode = 400, code = '20020', message = 'The code {0} is invalid', field = 'code')
    AppError invalidCode(String code)

    @ErrorDef(httpStatusCode = 400, code = '20021', message = 'The code parameter is missing', field = 'code')
    AppError missingCode()

    @ErrorDef(httpStatusCode = 400, code = '20022', message = 'The login parameter is missing',
            field = 'login')
    AppError missingUsername()

    @ErrorDef(httpStatusCode = 400, code = '20023', message = 'The password parameter is missing',
            field = 'password')
    AppError missingPassword()

    @ErrorDef(httpStatusCode = 400, code = '20024', message = 'The user credential is invalid',
            field = 'username, password')
    AppError invalidCredential()

    @ErrorDef(httpStatusCode = 400, code = '20025', message = 'The refresh_token {0} is invalid',
            field = 'refresh_token')
    AppError invalidRefreshToken(String refreshToken)

    @ErrorDef(httpStatusCode = 400, code = '20026', message = 'The refresh_token parameter is missing',
            field = 'refresh_token')
    AppError missingRefreshToken()

    @ErrorDef(httpStatusCode = 400, code = '20027', message = 'The refresh_token {0} is already expired',
            field = 'refresh_token')
    AppError expiredRefreshToken(String refreshToken)

    @ErrorDef(httpStatusCode = 400, code = '20028', message = 'The issuer of the id token is invalid',
            field = 'id_token_hint')
    AppError invalidIdTokenIssuer()

    @ErrorDef(httpStatusCode = 400, code = '20029',
            message = 'The audience of the id token doesn not contain the client id {0}', field = 'id_token_hint')
    AppError invalidIdTokenAudience(String clientId)

    @ErrorDef(httpStatusCode = 400, code = '20030', message = 'The id token is already expired',
            field = 'id_token_hint')
    AppError expiredIdToken()

    @ErrorDef(httpStatusCode = 400, code = '20031', message = 'The id token is invalid', field = 'id_token_hint')
    AppError invalidIdToken()

    @ErrorDef(httpStatusCode = 400, code = '20032', message = 'The max_age {0} is invalid', field = 'max_age')
    AppError invalidMaxAge(String maxAge)

    @ErrorDef(httpStatusCode = 400, code = '20033', message = 'The access_token parameter is missing',
            field = 'access_token')
    AppError missingAccessToken()

    @ErrorDef(httpStatusCode = 400, code = '20034', message = 'The access_token {0} is invalid',
            field = 'access_token')
    AppError invalidAccessToken(accessToken)

    @ErrorDef(httpStatusCode = 401, code = '20035', message = 'The access_token {0} is already expired',
            field = 'access_token')
    AppError expiredAccessToken(accessToken)

    @ErrorDef(httpStatusCode = 400, code = '20036', message = 'The Authorization header is missing',
            field = 'authorization')
    AppError missingAuthorization()

    @ErrorDef(httpStatusCode = 400, code = '20037', message = 'The Authorization header is invalid',
            field = 'authorization')
    AppError invalidAuthorization()

    @ErrorDef(httpStatusCode = 400, code = '20038', message = 'The conversationId is missing',
            field = 'conversationId')
    AppError missingConversationId()

    @ErrorDef(httpStatusCode = 400, code = '20039', message = 'The post_logout_redirect_uri {0} is invalid',
            field = 'conversationId')
    AppError invalidPostLogoutRedirectUri(String postLogoutRedirectUri)

    @ErrorDef(httpStatusCode = 403, code = '20040',
            message = 'The access token does not have sufficient scope to make the request',
            field = 'access_token')
    AppError insufficientScope()

    @ErrorDef(httpStatusCode = 400, code = '20041',
            message = 'The default_redirect_uri {0} is invalid, reason: {1}',
            field = 'default_redirect_uri')
    AppError invalidDefaultRedirectUri(String defaultRedirectUri, String reason)

    @ErrorDef(httpStatusCode = 400, code = '20042', message = 'The default_scopes {0} is invalid',
            field = 'default_scopes')
    AppError invalidDefaultScope(String scopes)

    @ErrorDef(httpStatusCode = 400, code = '20043', message = 'The logout_uri {0} is invalid',
            field = 'logout_uri')
    AppError invalidLogoutRedirectUri(String logoutRedirectUri)

    @ErrorDef(httpStatusCode = 400, code = '20044',
            message = 'The default_logout_redirect_uri {0} is invalid, reason: {1}',
            field = 'default_logout_redirect_uri')
    AppError invalidDefaultLogoutRedirectUri(String defaultLogoutRedirectUri, String reason)

    @ErrorDef(httpStatusCode = 400, code = '20045', message = 'The logo_uri {0} is invalid',
            field = 'logo_uri')
    AppError invalidLogoUri(String logoUri)

    @ErrorDef(httpStatusCode = 400, code = '20046', message = 'The contact {0} is not a valid email address',
            field = 'contacts')
    AppError invalidContacts(String contact)

    @ErrorDef(httpStatusCode = 401, code = '20047', message = 'The user of access token is not the client owner',
            field = 'access_token')
    AppError notClientOwner()

    @ErrorDef(httpStatusCode = 401, code = '20048', message = 'The client id {0} does not exist',
            field = 'client_id')
    AppError notExistClient(String clientId)

    @ErrorDef(httpStatusCode = 403, code = '20048', message = 'The {0} cannot be updated',
            field = 'client')
    AppError cantUpdateFields(String field)

    @ErrorDef(httpStatusCode = 409, code = '20049',
            message = 'The client is updated by someone else, please re-get the client and try update again',
            field = 'client')
    AppError updateConflict()

    @ErrorDef(httpStatusCode = 400, code = '20050',
            message = 'The token is neither an access token nor a refresh token',
            field = 'token')
    AppError invalidTokenType()

    @ErrorDef(httpStatusCode = 403, code = '20050',
            message = 'The token\'s client does not match the authorization\'s client',
            field = 'token')
    AppError tokenClientNotMatch()

    @ErrorDef(httpStatusCode = 409, code = '20051',
            message = 'The {0} name {1} already exists',
            field = 'name')
    AppError duplicateEntityName(String entityName, String value)

    @ErrorDef(httpStatusCode = 400, code = '20052',
            message = 'The revision is not provided',
            field = 'revision')
    AppError missingRevision()

    @ErrorDef(httpStatusCode = 500, code = '20053', message = 'Error happened while calling the identity')
    AppError errorCallingIdentity()

    @ErrorDef(httpStatusCode = 400, code = '20054', message = 'The input name and entity name do not match')
    AppError mismatchEntityName()

    @ErrorDef(httpStatusCode = 400, code = '20055', message = 'cid is invalid', field = 'cid')
    AppError invalidCid()

    @ErrorDef(httpStatusCode = 400, code = '20057', message = 'The first name is missing', field = 'first_name')
    AppError missingFirstName()

    @ErrorDef(httpStatusCode = 400, code = '20058', message = 'The last name is missing', field = 'last_name')
    AppError missingLastName()

    @ErrorDef(httpStatusCode = 400, code = '20060', message = 'The gender is invalid', field = 'gender')
    AppError invalidGender()

    @ErrorDef(httpStatusCode = 400, code = '20062',
            message = 'The date of birth is invalid. Valid date format: YYYY-MM-DD', field = 'dob')
    AppError invalidDob()

    @ErrorDef(httpStatusCode = 400, code = '20063',
            message = 'The recaptcha_challenge_field is missing', field = 'recaptcha_challenge_field')
    AppError missingRecaptchaChallengeField()

    @ErrorDef(httpStatusCode = 400, code = '20064',
            message = 'The recaptcha_response_field is missing', field = 'recaptcha_response_field')
    AppError missingRecaptchaResponseField()

    @ErrorDef(httpStatusCode = 500, code = '20065',
            message = 'Error happened when calling recaptcha server', field = 'recaptcha')
    AppError errorCallingRecaptcha()

    @ErrorDef(httpStatusCode = 400, code = '20066',
            message = 'Invalid recaptcha, error message: {0}', field = 'recaptcha')
    AppError invalidRecaptcha(String message)

    @ErrorDef(httpStatusCode = 400, code = '20067',
            message = 'The email verify code is missing', field = 'evc')
    AppError missingEmailVerifyCode()

    @ErrorDef(httpStatusCode = 400, code = '20068',
            message = 'Invalid email verify code: {0}', field = 'code')
    AppError invalidEmailVerifyCode(String code)

    @ErrorDef(httpStatusCode = 500, code = '20065',
            message = 'Error happened when calling email server', field = 'email')
    AppError errorCallingEmail()

    @ErrorDef(httpStatusCode = 400, code = '20067',
            message = 'The email is missing', field = 'email')
    AppError missingEmail()

    @ErrorDef(httpStatusCode = 400, code = '20068',
            message = 'Invalid email format: {0}', field = 'email')
    AppError invalidEmail(String email)

    @ErrorDef(httpStatusCode = 400, code = '20069', message = 'Invalid locale: {0}', field = 'locale')
    AppError invalidLocale(String locale)

    @ErrorDef(httpStatusCode = 400, code = '20070',
            message = 'The facebookAuth is missing', field = 'facebookAuth')
    AppError missingFacebookAuth()

    @ErrorDef(httpStatusCode = 400, code = '20071',
            message = 'Invalid facebookAuth', field = 'facebookAuth')
    AppError errorCallingFacebook()

    @ErrorDef(httpStatusCode = 400, code = '20071',
            message = 'Invalid facebookAuth', field = 'facebookAuth', reason = "{0}")
    AppError errorCallingFacebook(String message)

    @ErrorDef(httpStatusCode = 400, code = '20072',
            message = 'The googleAuth is missing', field = 'googleAuth')
    AppError missingGoogleAuth()

    @ErrorDef(httpStatusCode = 400, code = '20073',
            message = 'Invalid googleAuth', field = 'googleAuth')
    AppError errorCallingGoogle()

    @ErrorDef(httpStatusCode = 400, code = '20073',
            message = 'Invalid googleAuth', field = 'googleAuth', reason = "{0}")
    AppError errorCallingGoogle(String message)

    @ErrorDef(httpStatusCode = 400, code = '20074', message = 'The state parameter is missing',
            field = 'state')
    AppError missingState()

    @ErrorDef(httpStatusCode = 400, code = '20075', message = 'Missing user id')
    AppError missingUserId()

    @ErrorDef(httpStatusCode = 400, code = '20071', message = 'Invalid verification code')
    AppError invalidVerificationCode()

    @ErrorDef(httpStatusCode = 400, code = '20072',
            message = 'The reset password code is missing', field = 'rpc')
    AppError missingResetPasswordCode()

    @ErrorDef(httpStatusCode = 400, code = '20073',
            message = 'No account found with that email address.', field = 'email')
    AppError noAccountFound()

    @ErrorDef(httpStatusCode = 400, code = '20074', message = 'The login or username parameter is missing',
            field = 'login or username')
    AppError missingLoginOrUsername()

    @ErrorDef(httpStatusCode = 400, code = '20075', message = 'There is no default email on user')
    AppError missingDefaultUserEmail()

    @ErrorDef(httpStatusCode = 404, code = '20076', message = 'Api definition {0} not found')
    AppError apiDefinitionNotFound(String apiName)

    @ErrorDef(httpStatusCode = 400, code = '20077', message = 'The pin parameter is missing',
            field = 'pin')
    AppError missingPin()

    @ErrorDef(httpStatusCode = 400, code = '20078', message = 'The pin parameter is invalid',
            field = 'pin')
    AppError invalidPin()

    @ErrorDef(httpStatusCode = 400, code = '20079', message = 'The {0} parameter is missing',
            field = '{0}')
    AppError missingParameter(String parameter)

    @ErrorDef(httpStatusCode = 400, code = '20080', message = 'The {0} parameter is invalid',
            field = '{0}')
    AppError invalidParameter(String parameter)

    @ErrorDef(httpStatusCode = 500, code = '20081', message = 'Error happened while calling the payment')
    AppError errorCallingPayment()

    @ErrorDef(httpStatusCode = 400, code = '20082', message = 'The country parameter is invalid',
            field = 'country')
    AppError invalidCountryCode()

    @ErrorDef(httpStatusCode = 400, code = '20083', message = 'Either username or user_email is required',
            field = 'username or user_email')
    AppError missingUsernameOrUserEmail()
}
