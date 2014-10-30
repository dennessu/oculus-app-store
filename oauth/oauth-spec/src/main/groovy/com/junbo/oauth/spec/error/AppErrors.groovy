/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.spec.error

import com.junbo.common.error.AppError
import com.junbo.common.error.ErrorDef
import com.junbo.common.error.ErrorProxy
import groovy.transform.CompileStatic

/**
 * AppErrors.
 */
@CompileStatic
interface AppErrors {
    static final AppErrors INSTANCE = ErrorProxy.newProxyInstance(AppErrors)

    @ErrorDef(httpStatusCode = 412, code = '101', message = 'Different ClientId.',
            reason = 'The refresh token\'s client id {0} is different from the parameter client_id {1}',
            field = 'client_id')
    AppError differentClientId(String tokenClientId, String paramClientId)

    @ErrorDef(httpStatusCode = 400, code = '102', message = 'Outbound Scope.',
            reason = 'The scope parameter must be a subset of the original access token\'s scopes',
            field = 'scope')
    AppError outboundScope()

    @ErrorDef(httpStatusCode = 412, code = '103', message = 'Invalid Credential', field = 'email, password', reason = 'email and credential doesn\'t match')
    AppError invalidCredential()

    @ErrorDef(httpStatusCode = 412, code = '104', message='Expired Refresh Token.', reason = 'The refresh_token {0} is already expired',
            field = 'refresh_token')
    AppError expiredRefreshToken(String refreshToken)

    @ErrorDef(httpStatusCode = 412, code = '105', message = 'Invalid IdToken Audience',
            reason = 'The audience of the id token doesn not contain the client id {0}', field = 'id_token_hint')
    AppError invalidIdTokenAudience(String clientId)

    @ErrorDef(httpStatusCode = 412, code = '106', message = 'Expired IdToken', reason = 'The id token is already expired',
            field = 'id_token_hint')
    AppError expiredIdToken()

    @ErrorDef(httpStatusCode = 412, code = '107', message = 'Invalid Access Token', reason = 'The access_token {0} is invalid',
            field = 'access_token')
    AppError invalidAccessToken(accessToken)

    @ErrorDef(httpStatusCode = 412, code = '108', message = 'Expired Access Token', reason = 'The access_token {0} is already expired',
            field = 'access_token')
    AppError expiredAccessToken(accessToken)

    @ErrorDef(httpStatusCode = 400, code = '109', message = 'Missing Authorization', reason = 'The Authorization header is missing',
            field = 'authorization')
    AppError missingAuthorization()

    @ErrorDef(httpStatusCode = 400, code = '110', message = 'Invalid Authorization', reason = 'The Authorization header is invalid',
            field = 'authorization')
    AppError invalidAuthorization()

    @ErrorDef(httpStatusCode = 400, code = '111', message = 'Invalid Post Logout RedirectUri', reason = 'The post_logout_redirect_uri {0} is invalid',
            field = 'post_logout_redirect_uri')
    AppError invalidPostLogoutRedirectUri(String postLogoutRedirectUri)

    @ErrorDef(httpStatusCode = 412, code = '112', message = 'Insufficient Scope',
            reason = 'The access token does not have sufficient scope to make the request',
            field = 'access_token')
    AppError insufficientScope()

    @ErrorDef(httpStatusCode = 412, code = '113', message = 'Invalid Default Redirect Uri',
            reason = 'The default_redirect_uri {0} is invalid, reason: {1}',
            field = 'default_redirect_uri')
    AppError invalidDefaultRedirectUri(String defaultRedirectUri, String reason)

    @ErrorDef(httpStatusCode = 412, code = '114', message = 'Invalid Default Scope', reason = 'The default_scopes {0} is invalid',
            field = 'default_scopes')
    AppError invalidDefaultScope(String scopes)

    @ErrorDef(httpStatusCode = 412, code = '115', message = 'Invalid Logout RedirectUri', reason = 'The logout_uri {0} is invalid',
            field = 'logout_uri')
    AppError invalidLogoutRedirectUri(String logoutRedirectUri)

    @ErrorDef(httpStatusCode = 412, code = '116', message = 'Invalid Default Logout RedirectUri',
            reason = 'The default_logout_redirect_uri {0} is invalid, reason: {1}',
            field = 'default_logout_redirect_uri')
    AppError invalidDefaultLogoutRedirectUri(String defaultLogoutRedirectUri, String reason)

    @ErrorDef(httpStatusCode = 412, code = '117', message = 'Invalid LogoUri', reason = 'The logo_uri {0} is invalid',
            field = 'logo_uri')
    AppError invalidLogoUri(String logoUri)

    @ErrorDef(httpStatusCode = 412, code = '118', message = 'Invalid Contacts', reason = 'The contact {0} is not a valid email address',
            field = 'contacts')
    AppError invalidContacts(String contact)

    @ErrorDef(httpStatusCode = 412, code = '119', message = 'Not Client Owner', reason = 'The user of access token is not the client owner',
            field = 'access_token')
    AppError notClientOwner()

    @ErrorDef(httpStatusCode = 412, code = '120', message = 'Not Existing Client', reason = 'The client id {0} does not exist',
            field = 'client_id')
    AppError notExistClient(String clientId)

    @ErrorDef(httpStatusCode = 400, code = '121', message = 'Cant Update Fields', reason = 'The {0} cannot be updated',
            field = 'client')
    AppError cantUpdateFields(String field)

    @ErrorDef(httpStatusCode = 412, code = '122', message = 'Update Conflict',
            reason = 'The client is updated by someone else, please re-get the client and try update again',
            field = 'client')
    AppError updateConflict()

    @ErrorDef(httpStatusCode = 412, code = '123', message = 'Invalid Token Type',
            reason = 'The token is neither an access token nor a refresh token',
            field = 'token')
    AppError invalidTokenType()

    @ErrorDef(httpStatusCode = 412, code = '124', message = 'Token Client Not Match',
            reason = 'The token\'s client does not match the authorization\'s client',
            field = 'token')
    AppError tokenClientNotMatch()

    @ErrorDef(httpStatusCode = 409, code = '125', message = 'Duplicate Entity Name',
            reason = 'The {0} name {1} already exists',
            field = 'name')
    AppError duplicateEntityName(String entityName, String value)

    @ErrorDef(httpStatusCode = 500, code = '126', message = 'Error Calling Identity', reason = 'Error happened while calling the identity')
    AppError errorCallingIdentity()

    @ErrorDef(httpStatusCode = 412, code = '127', message = 'Invalid Recaptcha',
            reason = 'Invalid recaptcha, error message: {0}', field = 'recaptcha')
    AppError invalidRecaptcha(String message)

    @ErrorDef(httpStatusCode = 412, code = '128', message = 'Invalid Email Verify Code',
            reason = 'Invalid email verify code', field = 'code')
    AppError invalidEmailVerifyCode()

    @ErrorDef(httpStatusCode = 500, code = '129', message = 'Error Calling Email',
            reason = 'Error happened when calling email server', field = 'email')
    AppError errorCallingEmail()

    @ErrorDef(httpStatusCode = 412, code = '130', message = 'Error Calling Facebook',
            reason  = 'Invalid facebookAuth', field = 'facebookAuth')
    AppError errorCallingFacebook()

    @ErrorDef(httpStatusCode = 412, code = '130', message = 'Error Calling Facebook',
            field = 'facebookAuth', reason = "{0}")
    AppError errorCallingFacebook(String message)

    @ErrorDef(httpStatusCode = 412, code = '131', message = 'Error Calling Google',
            reason = 'Invalid googleAuth', field = 'googleAuth')
    AppError errorCallingGoogle()

    @ErrorDef(httpStatusCode = 412, code = '131', message = 'Error Calling Google',
            field = 'googleAuth', reason = "{0}")
    AppError errorCallingGoogle(String message)

    @ErrorDef(httpStatusCode = 400, code = '132', message = 'Missing User Id')
    AppError missingUserId()

    @ErrorDef(httpStatusCode = 412, code = '133', message = 'Invalid Reset Password Code')
    AppError invalidResetPasswordCode()

    @ErrorDef(httpStatusCode = 412, code = '134', message = 'No Account Found',
            reason = 'No account found with that email address.', field = 'email')
    AppError noAccountFound()

    @ErrorDef(httpStatusCode = 412, code = '135', message = 'Missing Default User Email',
            reason = 'There is no default email on user')
    AppError missingDefaultUserEmail()

    @ErrorDef(httpStatusCode = 500, code = '136', message = 'Error Calling Payment', reason = 'Error happened while calling the payment')
    AppError errorCallingPayment()

    @ErrorDef(httpStatusCode = 400, code = '137', message = 'Method Not Allowed', reason = 'The GET method is not allowed during flow state change',
            field = 'event')
    AppError methodNotAllowed()

    @ErrorDef(httpStatusCode = 403, code = '138', message = 'Ip Not In Whitelist',
            reason = 'The request IP {0} is not in the client\'s ip whitelist', field = 'ip_address')
    AppError notIpWhitelist(String ip)

    @ErrorDef(httpStatusCode = 500, code = '139', message = 'Error Calling Recaptcha',
            reason = 'Error happened when calling recaptcha server', field = 'recaptcha')
    AppError errorCallingRecaptcha()

    @ErrorDef(httpStatusCode = 412, code = '140', message = 'Reset Password Code already used')
    AppError resetPasswordCodeAlreadyUsed()

    @ErrorDef(httpStatusCode = 412, code = '141', message = 'User does not have phone in his/her profile')
    AppError phoneNotFound()

    @ErrorDef(httpStatusCode = 412, code = '142', message = 'Username and email are occupied')
    AppError usernameAndEmailOccupied()

    @ErrorDef(httpStatusCode = 412, code = '143', message = 'Sentry block register access')
    AppError sentryBlockRegisterAccess()

    @ErrorDef(httpStatusCode = 412, code = '144', message = 'Sentry block login access')
    AppError sentryBlockLoginAccess()
}
