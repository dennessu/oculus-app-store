/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.service

import com.junbo.oauth.spec.model.*
import groovy.transform.CompileStatic

/**
 * TokenGenerationService.
 */
@CompileStatic
interface TokenGenerationService {
    AccessToken generateAccessToken(Client appClient, Long userId, Set<String> scopes)

    AccessToken getAccessToken(String tokenValue)

    void removeAccessToken(String tokenValue)

    RefreshToken generateRefreshToken(Client client, AccessToken accessToken, String salt)

    RefreshToken generateRefreshToken(Client client, AccessToken accessToken, RefreshToken oldRefreshToken)

    RefreshToken getAndRemoveRefreshToken(String tokenValue)

    IdToken generateIdToken(Client client, String issuer, Long userId, String nonce, Date lastAuthDate,
                            AuthorizationCode code, AccessToken accessToken)

    IdToken parseIdToken(Client client, String tokenValue)
}