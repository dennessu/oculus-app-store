/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.spec.model;

import groovy.transform.CompileStatic;

import javax.ws.rs.FormParam;

/**
 * Javadoc.
 */
@CompileStatic
public class AccessTokenRequest {
    @FormParam("client_id")
    private String clientId;

    @FormParam("client_secret")
    private String clientSecret;

    @FormParam("grant_type")
    private String grantType;

    @FormParam("code")
    private String code;

    @FormParam("scope")
    private String scope;

    @FormParam("redirect_uri")
    private String redirectUri;

    @FormParam("username")
    private String username;

    @FormParam("password")
    private String password;

    @FormParam("refresh_token")
    private String refreshToken;

    @FormParam("nonce")
    private String nonce;

    @FormParam("ip_restriction")
    private String ipRestriction;

    @FormParam("user_id")
    private String userId;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getIpRestriction() {
        return ipRestriction;
    }

    public void setIpRestriction(String ipRestriction) {
        this.ipRestriction = ipRestriction;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
