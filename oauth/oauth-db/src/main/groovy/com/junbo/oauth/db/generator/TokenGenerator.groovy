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

    String generateAccessToken()

    String generateRefreshToken()

    String generateRefreshTokenSeries()

    String generateRememberMeToken()

    String generateRememberMeTokenSeries()

    String generateClientId()

    String generateClientSecret()

    String generateEmailVerifyCode()

    boolean isValidAccessToken(String tokenValue)

    boolean isValidRefreshToken(String tokenValue)

    boolean isValidRememberMeToken(String tokenValue)
}
