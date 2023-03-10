/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.generator.impl;

//import com.junbo.common.error.AppCommonErrors;

import com.junbo.common.error.AppCommonErrors;
import com.junbo.common.util.UUIDUtils;
import com.junbo.configuration.topo.DataCenters;
import com.junbo.langur.core.HashUtil;
import com.junbo.oauth.db.generator.TokenGenerator;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.security.SecureRandom;
import java.util.Random;
import java.util.regex.Pattern;

//import org.apache.commons.codec.binary.Hex;
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;

/**
 * Javadoc.
 */
public class SecureRandomTokenGenerator implements TokenGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecureRandomTokenGenerator.class);

    private static final Pattern DEFAULT_CODEC_PATTERN = Pattern.compile("[0-9A-Za-z=\\-~]+");

    private final Random random = new SecureRandom();

    private int authorizationCodeLength;

    private int accessTokenLength;

    private int refreshTokenLength;
    private int refreshTokenSeriesLength;

    private int rememberMeTokenLength;
    private int rememberMeTokenSeriesLength;

    private int clientIdLength;
    private int clientSecretLength;

    private int saltLength;

    private int emailVerifyCodeLength;
    private int resetPasswordCodeLength;

    private int currentDc = DataCenters.instance().currentDataCenterId();

    @Required
    public void setAuthorizationCodeLength(int authorizationCodeLength) {
        this.authorizationCodeLength = authorizationCodeLength;
    }

    @Required
    public void setAccessTokenLength(int accessTokenLength) {
        this.accessTokenLength = accessTokenLength;
    }

    @Required
    public void setRefreshTokenLength(int refreshTokenLength) {
        this.refreshTokenLength = refreshTokenLength;
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
    public void setRememberMeTokenSeriesLength(int rememberMeTokenSeriesLength) {
        this.rememberMeTokenSeriesLength = rememberMeTokenSeriesLength;
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

    @Required
    public void setEmailVerifyCodeLength(int emailVerifyCodeLength) {
        this.emailVerifyCodeLength = emailVerifyCodeLength;
    }

    @Required
    public void setResetPasswordCodeLength(int resetPasswordCodeLength) {
        this.resetPasswordCodeLength = resetPasswordCodeLength;
    }

    private String generate(int length) {
        return generateWithDc(length, null);
    }

    private String generateWithDc(int length, Long userId) {
        byte currentDcByte = (byte)currentDc;
        byte userDcByte;

        if (userId == null || userId == 0L) {
            userDcByte = currentDcByte;
        } else {
            userDcByte = (byte)((userId >> 2) & 0xF);
        }

        byte dcByte = (byte)((currentDcByte << 4) + userDcByte);

        byte[] bytes = new byte[length];
        random.nextBytes(bytes);

        byte[] bytesWithDc = new byte[length + 1];
        System.arraycopy(bytes, 0, bytesWithDc, 0, length);

        bytesWithDc[length] = dcByte;
        return Base64.encodeBase64URLSafeString(bytesWithDc).replace('_', '~');
    }

    @Override
    public String generateLoginStateId() {
        return UUIDUtils.randomUUIDwithDC().toString();
    }

    @Override
    public String generateSessionStateId() {
        return UUIDUtils.randomUUIDwithDC().toString();
    }

    @Override
    public String generateAuthorizationCode(Long userId) {
        return generateWithDc(authorizationCodeLength, userId);
    }

    @Override
    public String generateAccessToken(Long userId) {
        return generateWithDc(accessTokenLength, userId);
    }

    @Override
    public String generateRefreshToken() {
        return generate(refreshTokenLength);
    }

    @Override
    public String generateRefreshTokenSeries() {
        return generate(refreshTokenSeriesLength);
    }

    @Override
    public String generateRememberMeToken() {
        return generate(rememberMeTokenLength);
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
    public String hashKey(String key) {
        return HashUtil.hash(key);
    }

    @Override
    public String generateEmailVerifyCode() {
        return generate(emailVerifyCodeLength);
    }

    @Override
    public String generateResetPasswordCode() {
        return generate(resetPasswordCodeLength);
    }

    @Override
    public boolean isValidAccessToken(String tokenValue) {
        if (tokenValue == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired("code").exception();
        }

        return DEFAULT_CODEC_PATTERN.matcher(tokenValue).matches();
    }

    @Override
    public boolean isValidRefreshToken(String tokenValue) {
        if (tokenValue == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired("code").exception();
        }

        return DEFAULT_CODEC_PATTERN.matcher(tokenValue).matches();
    }

    @Override
    public boolean isValidRememberMeToken(String tokenValue) {
        if (tokenValue == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired("code").exception();
        }

        return DEFAULT_CODEC_PATTERN.matcher(tokenValue).matches();
    }

    @Override
    public boolean isValidEmailVerifyCode(String codeValue) {
        if (codeValue == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired("code").exception();
        }

        return DEFAULT_CODEC_PATTERN.matcher(codeValue).matches();
    }

    @Override
    public boolean isValidResetPasswordCode(String codeValue) {
        if (codeValue == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired("code").exception();
        }

        return DEFAULT_CODEC_PATTERN.matcher(codeValue).matches();
    }

    @Override
    public int getTokenDc(String token) {
        try {
            byte[] originalBytes = Base64.decodeBase64(token.replace("~", "_"));
            byte dcByte = originalBytes[originalBytes.length - 1];
            return dcByte >> 4;
        } catch (Exception ex) {
            LOGGER.warn("Failed to get DC from token: {}", token);
            return currentDc;
        }
    }

    @Override
    public int getTokenUserDc(String token) {
        try {
            byte[] originalBytes = Base64.decodeBase64(token.replace("~", "_"));
            byte dcByte = originalBytes[originalBytes.length - 1];
            return dcByte & 0xF;
        } catch (Exception ex) {
            LOGGER.warn("Failed to get user DC from token: {}", token);
            return currentDc;
        }
    }
}
