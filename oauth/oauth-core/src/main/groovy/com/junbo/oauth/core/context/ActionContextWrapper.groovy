/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.context

import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.oauth.spec.model.*
import groovy.transform.CompileStatic
import org.springframework.web.util.UriComponentsBuilder

import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.core.Cookie
import javax.ws.rs.core.MultivaluedMap
import javax.ws.rs.core.NewCookie
import javax.ws.rs.core.Response

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
    public static final String CLIENT_ID = 'client_id'
    public static final String OAUTH_INFO = 'oauth_info'
    public static final String FLOW_STATE = 'flow_state'
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

    @Delegate
    private final ActionContext actionContext

    ActionContextWrapper(ActionContext actionContext) {
        this.actionContext = actionContext
    }

    ContainerRequestContext getRequest() {
        return (ContainerRequestContext) actionContext.requestScope[REQUEST]
    }

    void setRequest(ContainerRequestContext request) {
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
        actionContext.requestScope[RESPONSE_COOKIE_LIST] = headerMap
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

    String getClientId() {
        return (String) actionContext.requestScope[CLIENT_ID]
    }

    void setClientId(String clientId) {
        actionContext.requestScope[CLIENT_ID] = clientId
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

    FlowState getFlowState() {
        return (FlowState) actionContext.requestScope[FLOW_STATE]
    }

    void setFlowState(FlowState flowState) {
        actionContext.requestScope[FLOW_STATE] = flowState
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
}
