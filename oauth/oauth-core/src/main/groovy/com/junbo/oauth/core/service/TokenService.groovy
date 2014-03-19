/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.service

import com.junbo.oauth.spec.model.*
import groovy.transform.CompileStatic

/**
 * TokenService.
 */
@CompileStatic
interface TokenService {
    AccessToken generateAccessToken(Client appClient, Long userId, Set<String> scopes)

    AccessToken getAccessToken(String tokenValue)

    AccessToken extractAccessToken(String authorization)

    AccessToken updateAccessToken(AccessToken accessToken)

    RefreshToken generateRefreshToken(Client client, AccessToken accessToken, String salt)

    RefreshToken generateRefreshToken(Client client, AccessToken accessToken, RefreshToken oldRefreshToken)

    RefreshToken getAndRemoveRefreshToken(String tokenValue)

    IdToken generateIdToken(Client client, String issuer, Long userId, String nonce, Date lastAuthDate,
                            AuthorizationCode code, AccessToken accessToken)

    IdToken parseIdToken(String tokenValue)

    void revokeAccessToken(String tokenValue, Client client)

    void revokeRefreshToken(String tokenValue, Client client)

    boolean isValidAccessToken(String tokenValue)

    boolean isValidRefreshToken(String tokenValue)
}