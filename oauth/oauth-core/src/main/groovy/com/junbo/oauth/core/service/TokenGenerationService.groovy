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
    AccessToken generateAccessToken(AppClient appClient, Long userId, Set<String> scopes)

    AccessToken getAccessToken(String tokenValue)

    void removeAccessToken(String tokenValue)

    RefreshToken generateRefreshToken(AppClient client, AccessToken accessToken, String salt)

    RefreshToken generateRefreshToken(AppClient appClient, AccessToken accessToken, RefreshToken oldRefreshToken)

    RefreshToken getAndRemoveRefreshToken(String tokenValue)

    IdToken generateIdToken(AppClient client, String issuer, Long userId, String nonce, Date lastAuthDate,
                            AuthorizationCode code, AccessToken accessToken)

    IdToken parseIdToken(AppClient client, String tokenValue)
}