/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.util

import com.junbo.oauth.core.context.ServiceContext
import com.junbo.oauth.spec.model.*
import groovy.transform.CompileStatic
import org.springframework.web.util.UriComponentsBuilder

import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.core.Cookie
import javax.ws.rs.core.MultivaluedMap
import javax.ws.rs.core.NewCookie
import javax.ws.rs.core.Response

/**
 * ServiceContextUtil.
 */
@CompileStatic
class ServiceContextUtil {
    private static final String REQUEST = 'request'
    private static final String RESPONSE_BUILDER = 'response_builder'
    private static final String PARAMETER_MAP = 'parameter_map'
    private static final String HEADER_MAP = 'header_map'
    private static final String COOKIE_MAP = 'cookie_map'
    private static final String RESPONSE_HEADER_MAP = 'response_headers_map'
    private static final String RESPONSE_COOKIE_LIST = 'response_cookies_map'
    private static final String APP_CLIENT = 'app_client'
    private static final String CLIENT_ID = 'client_id'
    private static final String OAUTH_INFO = 'oauth_info'
    private static final String FLOW_STATE = 'flow_state'
    private static final String LOGIN_STATE = 'login_state'
    private static final String AUTHORIZATION_CODE = 'authorization_code'
    private static final String ACCESS_TOKEN = 'access_token'
    private static final String ACCESS_TOKEN_RESPONSE = 'access_token_response'
    private static final String REFRESH_TOKEN = 'refresh_token'
    private static final String REDIRECT_URI_BUILDER = 'redirect_uri_builder'
    private static final String ID_TOKEN = 'id_token'
    private static final String NEED_REMEMBER_ME = 'need_remember_me'
    private static final String REMEMBER_ME_TOKEN = 'remember_me_token'
    private static final String TOKEN_INFO = 'token_info'

    static ContainerRequestContext getRequest(ServiceContext context) {
        return (ContainerRequestContext) context.getAttribute(REQUEST)
    }

    static void setRequest(ServiceContext context, ContainerRequestContext request) {
        context.setAttribute(REQUEST, request)
    }

    static Response.ResponseBuilder getResponseBuilder(ServiceContext context) {
        return (Response.ResponseBuilder) context.getAttribute(RESPONSE_BUILDER)
    }

    static void setResponseBuilder(ServiceContext context, Response.ResponseBuilder responseBuilder) {
        context.setAttribute(RESPONSE_BUILDER, responseBuilder)
    }

    static MultivaluedMap<String, String> getParameterMap(ServiceContext context) {
        return (MultivaluedMap<String, String>) context.getAttribute(PARAMETER_MAP)
    }

    static void setParameterMap(ServiceContext context, MultivaluedMap<String, String> parameterMap) {
        context.setAttribute(PARAMETER_MAP, parameterMap)
    }

    static MultivaluedMap<String, String> getHeaderMap(ServiceContext context) {
        return (MultivaluedMap<String, String>) context.getAttribute(HEADER_MAP)
    }

    static void setHeaderMap(ServiceContext context, MultivaluedMap<String, String> headerMap) {
        context.setAttribute(HEADER_MAP, headerMap)
    }

    static Map<String, Cookie> getCookieMap(ServiceContext context) {
        return (Map<String, Cookie>) context.getAttribute(COOKIE_MAP)
    }

    static void setCookieMap(ServiceContext context, Map<String, Cookie> cookieMap) {
        context.setAttribute(COOKIE_MAP, cookieMap)
    }

    static Map<String, String> getResponseHeaderMap(ServiceContext context) {
        if (context.getAttribute(RESPONSE_HEADER_MAP) == null) {
            context.setAttribute(RESPONSE_HEADER_MAP, new HashMap<String, String>())
        }
        return (Map<String, String>) context.getAttribute(RESPONSE_HEADER_MAP)
    }

    static void setResponseHeaderMap(ServiceContext context, Map<String, String> headerMap) {
        context.setAttribute(RESPONSE_COOKIE_LIST, headerMap)
    }

    static List<NewCookie> getResponseCookieList(ServiceContext context) {
        if (context.getAttribute(RESPONSE_COOKIE_LIST) == null) {
            context.setAttribute(RESPONSE_COOKIE_LIST, new ArrayList<NewCookie>())
        }
        return (List<NewCookie>) context.getAttribute(RESPONSE_COOKIE_LIST)
    }

    static void setResponseCookieList(ServiceContext context, List<NewCookie> cookieList) {
        context.setAttribute(RESPONSE_COOKIE_LIST, cookieList)
    }

    static AppClient getAppClient(ServiceContext context) {
        return (AppClient) context.getAttribute(APP_CLIENT)
    }

    static void setAppClient(ServiceContext context, AppClient appClient) {
        context.setAttribute(APP_CLIENT, appClient)
    }

    static String getClientId(ServiceContext context) {
        return (String) context.getAttribute(CLIENT_ID)
    }

    static void setClientId(ServiceContext context, String clientId) {
        context.setAttribute(CLIENT_ID, clientId)
    }

    static OAuthInfo getOAuthInfo(ServiceContext context) {
        if (context.getAttribute(OAUTH_INFO) == null) {
            context.setAttribute(OAUTH_INFO, new OAuthInfo())
        }
        return (OAuthInfo) context.getAttribute(OAUTH_INFO)
    }

    static void setOAuthInfo(ServiceContext context, OAuthInfo oauthInfo) {
        context.setAttribute(OAUTH_INFO, oauthInfo)
    }

    static FlowState getFlowState(ServiceContext context) {
        return (FlowState) context.getAttribute(FLOW_STATE)
    }

    static void setFlowState(ServiceContext context, FlowState flowState) {
        context.setAttribute(FLOW_STATE, flowState)
    }

    static LoginState getLoginState(ServiceContext context) {
        return (LoginState) context.getAttribute(LOGIN_STATE)
    }

    static void setLoginState(ServiceContext context, LoginState loginState) {
        context.setAttribute(LOGIN_STATE, loginState)
    }

    static AuthorizationCode getAuthorizationCode(ServiceContext context) {
        return (AuthorizationCode) context.getAttribute(AUTHORIZATION_CODE)
    }

    static void setAuthorizationCode(ServiceContext context, AuthorizationCode authorizationCode) {
        context.setAttribute(AUTHORIZATION_CODE, authorizationCode)
    }

    static AccessToken getAccessToken(ServiceContext context) {
        return (AccessToken) context.getAttribute(ACCESS_TOKEN)
    }

    static void setAccessToken(ServiceContext context, AccessToken accessToken) {
        context.setAttribute(ACCESS_TOKEN, accessToken)
    }

    static AccessTokenResponse getAccessTokenResponse(ServiceContext context) {
        return (AccessTokenResponse) context.getAttribute(ACCESS_TOKEN_RESPONSE)
    }

    static void setAccessTokenResponse(ServiceContext context, AccessTokenResponse accessTokenResponse) {
        context.setAttribute(ACCESS_TOKEN_RESPONSE, accessTokenResponse)
    }

    static RefreshToken getRefreshToken(ServiceContext context) {
        return (RefreshToken) context.getAttribute(REFRESH_TOKEN)
    }

    static void setRefreshToken(ServiceContext context, RefreshToken refreshToken) {
        context.setAttribute(REFRESH_TOKEN, refreshToken)
    }

    static IdToken getIdToken(ServiceContext context) {
        return (IdToken) context.getAttribute(ID_TOKEN)
    }

    static void setIdToken(ServiceContext context, IdToken idToken) {
        context.setAttribute(ID_TOKEN, idToken)
    }

    static UriComponentsBuilder getRedirectUriBuilder(ServiceContext context) {
        return (UriComponentsBuilder) context.getAttribute(REDIRECT_URI_BUILDER)
    }

    static void setRedirectUriBuilder(ServiceContext context, UriComponentsBuilder builder) {
        context.setAttribute(REDIRECT_URI_BUILDER, builder)
    }

    static Boolean getNeedRememberMe(ServiceContext context) {
        return (Boolean) context.getAttribute(NEED_REMEMBER_ME)
    }

    static void setNeedRememberMe(ServiceContext context, Boolean needRememberMe) {
        context.setAttribute(NEED_REMEMBER_ME, needRememberMe)
    }

    static RememberMeToken getRememberMeToken(ServiceContext context) {
        return (RememberMeToken) context.getAttribute(REMEMBER_ME_TOKEN)
    }

    static void setRememberMeToken(ServiceContext context, RememberMeToken rememberMeToken) {
        context.setAttribute(REMEMBER_ME_TOKEN, rememberMeToken)
    }

    static TokenInfo getTokenInfo(ServiceContext context) {
        return (TokenInfo) context.getAttribute(TOKEN_INFO)
    }

    static void setTokenInfo(ServiceContext context, TokenInfo tokenInfo) {
        context.setAttribute(TOKEN_INFO, tokenInfo)
    }
}
