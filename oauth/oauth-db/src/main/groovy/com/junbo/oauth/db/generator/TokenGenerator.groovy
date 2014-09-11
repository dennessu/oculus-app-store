/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.generator

import groovy.transform.CompileStatic

/**
 * Javadoc.
 */
@CompileStatic
interface TokenGenerator {

    String generateLoginStateId()

    String generateSessionStateId()

    String generateAuthorizationCode()

    String generateAccessToken(Long userId)

    String generateRefreshToken()

    String generateRefreshTokenSeries()

    String generateRememberMeToken()

    String generateRememberMeTokenSeries()

    String generateClientId()

    String generateClientSecret()

    String generateEmailVerifyCode()

    String generateResetPasswordCode()

    String generateSalt()

    boolean isValidAccessToken(String tokenValue)

    boolean isValidRefreshToken(String tokenValue)

    boolean isValidRememberMeToken(String tokenValue)

    boolean isValidEmailVerifyCode(String codeValue)

    boolean isValidResetPasswordCode(String codeValue)
}
