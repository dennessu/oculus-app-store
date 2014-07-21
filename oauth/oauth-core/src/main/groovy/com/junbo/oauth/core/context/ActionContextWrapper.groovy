/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.context

import com.junbo.identity.spec.v1.model.User
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.oauth.common.Utils
import com.junbo.oauth.spec.model.*
import groovy.transform.CompileStatic
import org.springframework.web.util.UriComponentsBuilder

import javax.ws.rs.core.*

/**
 * A wrapper for ActionContext, provide some util functions.
 */
@CompileStatic
class ActionContextWrapper {
    public static final String REQUEST = 'request'
    public static final String RESPONSE_BUILDER = 'response_builder'
    public static final String PARAMETER_MAP = 'parameter_map'
    public static final String HEADER_MAP = 'header_map'
    public static final String COOKIE_MAP = 'cookie_map'
    public static final String RESPONSE_HEADER_MAP = 'response_headers_map'
    public static final String RESPONSE_COOKIE_LIST = 'response_cookies_map'
    public static final String CLIENT = 'client'
    public static final String OAUTH_INFO = 'oauth_info'
    public static final String LOGIN_STATE = 'login_state'
    public static final String AUTHORIZATION_CODE = 'authorization_code'
    public static final String ACCESS_TOKEN = 'access_token'
    public static final String ACCESS_TOKEN_RESPONSE = 'access_token_response'
    public static final String REFRESH_TOKEN = 'refresh_token'
    public static final String REDIRECT_URI_BUILDER = 'redirect_uri_builder'
    public static final String ID_TOKEN = 'id_token'
    public static final String NEED_REMEMBER_ME = 'need_remember_me'
    public static final String REMEMBER_ME_TOKEN = 'remember_me_token'
    public static final String TOKEN_INFO = 'token_info'
    public static final String CONSENT = 'consent'
    public static final String ERRORS = 'errors'
    public static final String USER = 'user'
    public static final String USER_CREDENTIAL = 'user_credential'
    public static final String DOB = 'dob'
    public static final String GENDER = 'gender'
    public static final String FORGET_PASSWORD_EMAIL = 'forget_password_email'
    public static final String REMOTE_ADDRESS = 'remote_address'
    public static final String CAPTCHA_REQUIRED = 'captcha_required'
    public static final String CAPTCHA_SUCCEED = 'captcha_succeed'
    public static final String VIEW_LOCALE = 'view_locale'
    public static final String VIEW_COUNTRY = 'view_country'
    public static final String EXTRA_PARAM_MAP = 'extra_param_map'
    public static final String EMAIL_VERIFY_CODE = 'email_verify_code'
    public static final String RESET_PASSWORD_CODE = 'reset_password_code'
    public static final String THIRD_PARTY_ACCOUNT = 'third_party_account'
    public static final String SUB_FLOW_NAME = 'sub_flow_name'
    public static final String USER_DEFAULT_EMAIL = 'default_email'
    public static final String EMAIL_VERIFY_LINK = 'email_verify_link'

    @Delegate
    private final ActionContext actionContext

    ActionContextWrapper(ActionContext actionContext) {
        this.actionContext = actionContext
    }

    Request getRequest() {
        return (Request) actionContext.requestScope[REQUEST]
    }

    void setRequest(Request request) {
        actionContext.requestScope[REQUEST] = request
    }

    Response.ResponseBuilder getResponseBuilder() {
        return (Response.ResponseBuilder) actionContext.requestScope[RESPONSE_BUILDER]
    }

    void setResponseBuilder(Response.ResponseBuilder responseBuilder) {
        actionContext.requestScope[RESPONSE_BUILDER] = responseBuilder
    }

    MultivaluedMap<String, String> getParameterMap() {
        return (MultivaluedMap<String, String>) actionContext.requestScope[PARAMETER_MAP]
    }

    void setParameterMap(MultivaluedMap<String, String> parameterMap) {
        actionContext.requestScope[PARAMETER_MAP] = parameterMap
    }

    MultivaluedMap<String, String> getHeaderMap() {
        return (MultivaluedMap<String, String>) actionContext.requestScope[HEADER_MAP]
    }

    void setHeaderMap(MultivaluedMap<String, String> headerMap) {
        actionContext.requestScope[HEADER_MAP] = headerMap
    }

    Map<String, Cookie> getCookieMap() {
        return (Map<String, Cookie>) actionContext.requestScope[COOKIE_MAP]
    }

    void setCookieMap(Map<String, Cookie> cookieMap) {
        actionContext.requestScope[COOKIE_MAP] = cookieMap
    }

    Map<String, String> getResponseHeaderMap() {
        if (actionContext.requestScope[RESPONSE_HEADER_MAP] == null) {
            actionContext.requestScope[RESPONSE_HEADER_MAP] = new HashMap<String, String>()
        }
        return (Map<String, String>) actionContext.requestScope[RESPONSE_HEADER_MAP]
    }

    void setResponseHeaderMap(Map<String, String> headerMap) {
        actionContext.requestScope[RESPONSE_HEADER_MAP] = headerMap
    }

    List<NewCookie> getResponseCookieList() {
        if (actionContext.requestScope[RESPONSE_COOKIE_LIST] == null) {
            actionContext.requestScope[RESPONSE_COOKIE_LIST] = new ArrayList<NewCookie>()
        }
        return (List<NewCookie>) actionContext.requestScope[RESPONSE_COOKIE_LIST]
    }

    void setResponseCookieList(List<NewCookie> cookieList) {
        actionContext.requestScope[RESPONSE_COOKIE_LIST] = cookieList
    }

    Client getClient() {
        return (Client) actionContext.flowScope[CLIENT]
    }

    void setClient(Client appClient) {
        actionContext.flowScope[CLIENT] = appClient
    }

    OAuthInfo getOauthInfo() {
        if (actionContext.flowScope[OAUTH_INFO] == null) {
            actionContext.flowScope[OAUTH_INFO] = new OAuthInfo()
        }
        return (OAuthInfo) actionContext.flowScope[OAUTH_INFO]
    }

    void setOauthInfo(OAuthInfo oauthInfo) {
        actionContext.flowScope[OAUTH_INFO] = oauthInfo
    }

    LoginState getLoginState() {
        return (LoginState) actionContext.flowScope[LOGIN_STATE]
    }

    void setLoginState(LoginState loginState) {
        actionContext.flowScope[LOGIN_STATE] = loginState
    }

    AuthorizationCode getAuthorizationCode() {
        return (AuthorizationCode) actionContext.requestScope[AUTHORIZATION_CODE]
    }

    void setAuthorizationCode(AuthorizationCode authorizationCode) {
        actionContext.requestScope[AUTHORIZATION_CODE] = authorizationCode
    }

    AccessToken getAccessToken() {
        return (AccessToken) actionContext.requestScope[ACCESS_TOKEN]
    }

    void setAccessToken(AccessToken accessToken) {
        actionContext.requestScope[ACCESS_TOKEN] = accessToken
    }

    AccessTokenResponse getAccessTokenResponse() {
        return (AccessTokenResponse) actionContext.requestScope[ACCESS_TOKEN_RESPONSE]
    }

    void setAccessTokenResponse(AccessTokenResponse accessTokenResponse) {
        actionContext.requestScope[ACCESS_TOKEN_RESPONSE] = accessTokenResponse
    }

    RefreshToken getRefreshToken() {
        return (RefreshToken) actionContext.requestScope[REFRESH_TOKEN]
    }

    void setRefreshToken(RefreshToken refreshToken) {
        actionContext.requestScope[REFRESH_TOKEN] = refreshToken
    }

    IdToken getIdToken() {
        return (IdToken) actionContext.requestScope[ID_TOKEN]
    }

    void setIdToken(IdToken idToken) {
        actionContext.requestScope[ID_TOKEN] = idToken
    }

    UriComponentsBuilder getRedirectUriBuilder() {
        return (UriComponentsBuilder) actionContext.requestScope[REDIRECT_URI_BUILDER]
    }

    void setRedirectUriBuilder(UriComponentsBuilder builder) {
        actionContext.requestScope[REDIRECT_URI_BUILDER] = builder
    }

    Boolean getNeedRememberMe() {
        return (Boolean) actionContext.requestScope[NEED_REMEMBER_ME]
    }

    void setNeedRememberMe(Boolean needRememberMe) {
        actionContext.requestScope[NEED_REMEMBER_ME] = needRememberMe
    }

    RememberMeToken getRememberMeToken() {
        return (RememberMeToken) actionContext.requestScope[REMEMBER_ME_TOKEN]
    }

    void setRememberMeToken(RememberMeToken rememberMeToken) {
        actionContext.requestScope[REMEMBER_ME_TOKEN] = rememberMeToken
    }

    TokenInfo getTokenInfo() {
        return (TokenInfo) actionContext.requestScope[TOKEN_INFO]
    }

    void setTokenInfo(TokenInfo tokenInfo) {
        actionContext.requestScope[TOKEN_INFO] = tokenInfo
    }

    Consent getConsent() {
        return (Consent) actionContext.flowScope[CONSENT]
    }

    void setConsent(Consent consent) {
        actionContext.flowScope[CONSENT] = consent
    }

    List<com.junbo.common.error.Error> getErrors() {
        if (actionContext.flashScope[ERRORS] == null) {
            actionContext.flashScope[ERRORS] = new ArrayList<com.junbo.common.error.Error>()
        }
        return (List<com.junbo.common.error.Error>) actionContext.flashScope[ERRORS]
    }

    void setErrors(List<com.junbo.common.error.Error> errors) {
        actionContext.flashScope[ERRORS] = errors
    }

    User getUser() {
        return (User) actionContext.flowScope[USER]
    }

    void setUser(User user) {
        actionContext.flowScope[USER] = user
    }

    Date getDob() {
        return (Date) actionContext.flowScope[DOB]
    }

    void setDob(Date dob) {
        actionContext.flowScope[DOB] = dob
    }

    Gender getGender() {
        return (Gender) actionContext.requestScope[GENDER]
    }

    void setGender(Gender gender) {
        actionContext.requestScope[GENDER] = gender
    }

    String getForgetPasswordEmail() {
        return (String) actionContext.requestScope[FORGET_PASSWORD_EMAIL]
    }

    void setForgetPasswordEmail(String email) {
        actionContext.requestScope[FORGET_PASSWORD_EMAIL] = email
    }

    String getRemoteAddress() {
        return (String) actionContext.requestScope[REMOTE_ADDRESS]
    }

    void setRemoteAddress(String remoteAddress) {
        actionContext.requestScope[REMOTE_ADDRESS] = remoteAddress
    }

    Boolean getCaptchaRequired() {
        if (actionContext.flowScope[CAPTCHA_REQUIRED] == null) {
            actionContext.flowScope[CAPTCHA_REQUIRED] = false
        }

        return (Boolean) actionContext.flowScope[CAPTCHA_REQUIRED]
    }

    void setCaptchaRequired(Boolean captchaRequired) {
        actionContext.flowScope[CAPTCHA_REQUIRED] = captchaRequired
    }

    Boolean getCaptchaSucceed() {
        if (actionContext.requestScope[CAPTCHA_SUCCEED] == null) {
            actionContext.requestScope[CAPTCHA_SUCCEED] = false
        }

        return (Boolean) actionContext.requestScope[CAPTCHA_SUCCEED]
    }

    void setCaptchaSucceed(Boolean captchaRequired) {
        actionContext.requestScope[CAPTCHA_SUCCEED] = captchaRequired
    }

    String getViewLocale() {
        return (String) actionContext.flowScope[VIEW_LOCALE]
    }

    void setViewLocale(String locale) {
        actionContext.flowScope[VIEW_LOCALE] = locale
    }

    String getViewCountry() {
        return (String) actionContext.flowScope[VIEW_COUNTRY]
    }

    void setViewCountry(String country) {
        actionContext.flowScope[VIEW_COUNTRY] = country
    }

    EmailVerifyCode getEmailVerifyCode() {
        return (EmailVerifyCode) actionContext.flowScope[EMAIL_VERIFY_CODE]
    }

    void setEmailVerifyCode(EmailVerifyCode code) {
        actionContext.flowScope[EMAIL_VERIFY_CODE] = code
    }

    String getResetPasswordCode() {
        return (String) actionContext.flowScope[RESET_PASSWORD_CODE]
    }

    void setResetPasswordCode(String code) {
        actionContext.flowScope[RESET_PASSWORD_CODE] = code
    }

    Map<String, String> getExtraParameterMap() {
        if (actionContext.flowScope[EXTRA_PARAM_MAP] == null) {
            actionContext.flowScope[EXTRA_PARAM_MAP] = new HashMap<String, String>()
        }
        return (Map<String, String>) actionContext.flowScope[EXTRA_PARAM_MAP]
    }

    void setExtraParameterMap(Map<String, String> extraMap) {
        actionContext.flowScope[EXTRA_PARAM_MAP] = extraMap
    }

    ThirdPartyAccount getThirdPartyAccount() {
        return (ThirdPartyAccount) actionContext.flowScope[THIRD_PARTY_ACCOUNT]
    }

    void setThirdPartyAccount(ThirdPartyAccount thirdPartyAccount) {
        actionContext.flowScope[THIRD_PARTY_ACCOUNT] = thirdPartyAccount
    }

    String getUserDefaultEmail() {
        return Utils.maskEmail(actionContext.flowScope[USER_DEFAULT_EMAIL] as String)
    }

    void setUserDefaultEmail(String userDefaultEmail) {
        actionContext.flowScope[USER_DEFAULT_EMAIL] = userDefaultEmail
    }

    String getSubFlowName() {
        return actionContext.flowScope[SUB_FLOW_NAME] as String
    }

    void setSubFlowName(String subFlowName) {
        actionContext.flowScope[SUB_FLOW_NAME] = subFlowName
    }

    String getEmailVerifyLink() {
        return actionContext.flowScope[EMAIL_VERIFY_LINK] as String
    }

    void setEmailVerifyLink(String emailVerifyLink) {
        actionContext.flowScope[EMAIL_VERIFY_LINK] = emailVerifyLink
    }
}
