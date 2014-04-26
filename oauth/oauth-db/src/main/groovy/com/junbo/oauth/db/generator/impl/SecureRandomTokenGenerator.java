/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.generator.impl;

import com.junbo.oauth.db.generator.TokenGenerator;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Javadoc.
 */
public class
        SecureRandomTokenGenerator implements TokenGenerator {

    private static final char[] DEFAULT_CODEC =
            "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final String TOKEN_DELIMITER = ";";

    private final Random random = new SecureRandom();

    private int loginStateLength;
    private String loginStatePostfix;

    private int sessionStateLength;
    private String sessionStatePostfix;

    private int flowStateLength;
    private String flowStatePostfix;

    private int authorizationCodeLength;
    private String authorizationCodePostfix;

    private int accessTokenLength;
    private String accessTokenPostfix;

    private int refreshTokenLength;
    private String refreshTokenPostfix;
    private int refreshTokenSeriesLength;

    private int rememberMeTokenLength;
    private String rememberMeTokenPostfix;
    private int rememberMeTokenSeriesLength;

    private int clientIdLength;
    private int clientSecretLength;

    private int saltLength;

    @Required
    public void setLoginStateLength(int loginStateLength) {
        this.loginStateLength = loginStateLength;
    }

    @Required
    public void setLoginStatePostfix(String loginStatePostfix) {
        this.loginStatePostfix = loginStatePostfix;
    }

    @Required
    public void setFlowStateLength(int flowStateLength) {
        this.flowStateLength = flowStateLength;
    }

    @Required
    public void setFlowStatePostfix(String flowStatePostfix) {
        this.flowStatePostfix = flowStatePostfix;
    }

    @Required
    public void setAuthorizationCodeLength(int authorizationCodeLength) {
        this.authorizationCodeLength = authorizationCodeLength;
    }

    @Required
    public void setAuthorizationCodePostfix(String authorizationCodePostfix) {
        this.authorizationCodePostfix = authorizationCodePostfix;
    }

    @Required
    public void setAccessTokenLength(int accessTokenLength) {
        this.accessTokenLength = accessTokenLength;
    }

    @Required
    public void setAccessTokenPostfix(String accessTokenPostfix) {
        this.accessTokenPostfix = accessTokenPostfix;
    }

    @Required
    public void setRefreshTokenLength(int refreshTokenLength) {
        this.refreshTokenLength = refreshTokenLength;
    }

    @Required
    public void setRefreshTokenPostfix(String refreshTokenPostfix) {
        this.refreshTokenPostfix = refreshTokenPostfix;
    }

    @Required
    public void setRefreshTokenSeriesLength(int refreshTokenSeriesLength) {
        this.refreshTokenSeriesLength = refreshTokenSeriesLength;
    }

    @Required
    public void setRememberMeTokenLength(int rememberMeTokenLength) {
        this.rememberMeTokenLength = rememberMeTokenLength;
    }

    @Required
    public void setRememberMeTokenPostfix(String rememberMeTokenPostfix) {
        this.rememberMeTokenPostfix = rememberMeTokenPostfix;
    }

    @Required
    public void setRememberMeTokenSeriesLength(int rememberMeTokenSeriesLength) {
        this.rememberMeTokenSeriesLength = rememberMeTokenSeriesLength;
    }

    @Required
    public void setSessionStateLength(int sessionStateLength) {
        this.sessionStateLength = sessionStateLength;
    }

    @Required
    public void setSessionStatePostfix(String sessionStatePostfix) {
        this.sessionStatePostfix = sessionStatePostfix;
    }

    @Required
    public void setClientIdLength(int clientIdLength) {
        this.clientIdLength = clientIdLength;
    }

    @Required
    public void setClientSecretLength(int clientSecretLength) {
        this.clientSecretLength = clientSecretLength;
    }

    @Required
    public void setSaltLength(int saltLength) {
        this.saltLength = saltLength;
    }

    private String generate(int length) {
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);

        return getString(bytes);
    }

    private String getString(byte[] bytes) {
        char[] chars = new char[bytes.length];

        for (int i = 0; i < bytes.length; i++) {
            chars[i] = DEFAULT_CODEC[((bytes[i] & 0xFF) % DEFAULT_CODEC.length)];
        }

        return new String(chars);
    }

    @Override
    public String generateFlowStateId() {
        return generate(flowStateLength) + flowStatePostfix;
    }

    @Override
    public String generateLoginStateId() {
        return generate(loginStateLength) + loginStatePostfix;
    }

    @Override
    public String generateSessionStateId() {
        return generate(sessionStateLength) + sessionStatePostfix;
    }

    @Override
    public String generateAuthorizationCode() {
        return generate(authorizationCodeLength) + authorizationCodePostfix;
    }

    @Override
    public String generateAccessToken() {
        String tokenValue = generate(accessTokenLength) + TOKEN_DELIMITER + accessTokenPostfix;
        return Base64.encodeBase64URLSafeString(tokenValue.getBytes());
    }

    @Override
    public String generateRefreshToken() {
        String tokenValue = generate(refreshTokenLength) + TOKEN_DELIMITER + refreshTokenPostfix;
        return Base64.encodeBase64URLSafeString(tokenValue.getBytes());
    }

    @Override
    public String generateRefreshTokenSeries() {
        return generate(refreshTokenSeriesLength);
    }

    @Override
    public String generateRememberMeToken() {
        String tokenValue = generate(rememberMeTokenLength) + TOKEN_DELIMITER + rememberMeTokenPostfix;
        return Base64.encodeBase64URLSafeString(tokenValue.getBytes());
    }

    @Override
    public String generateRememberMeTokenSeries() {
        return generate(rememberMeTokenSeriesLength);
    }

    @Override
    public String generateClientId() {
        return generate(clientIdLength);
    }

    @Override
    public String generateClientSecret() {
        return generate(clientSecretLength);
    }

    @Override
    public String generateSalt() {
        return generate(saltLength);
    }

    @Override
    public boolean isValidAccessToken(String tokenValue) {
        Assert.notNull(tokenValue);

        try {
            String token = new String(Base64.decodeBase64(tokenValue));
            String[] tokens = token.split(TOKEN_DELIMITER);

            if (tokens.length != 2 || !tokens[1].equals(accessTokenPostfix)) {
                return false;
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isValidRefreshToken(String tokenValue) {
        Assert.notNull(tokenValue);

        try {
            String token = new String(Base64.decodeBase64(tokenValue));
            String[] tokens = token.split(TOKEN_DELIMITER);

            if (tokens.length != 2 || !tokens[1].equals(refreshTokenPostfix)) {
                return false;
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isValidRememberMeToken(String tokenValue) {
        Assert.notNull(tokenValue);

        try {
            String token = new String(Base64.decodeBase64(tokenValue));
            String[] tokens = token.split(TOKEN_DELIMITER);

            if (tokens.length != 2 || !tokens[1].equals(rememberMeTokenPostfix)) {
                return false;
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
