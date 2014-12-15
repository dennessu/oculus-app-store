/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.service

import com.junbo.oauth.spec.model.*
import groovy.transform.CompileStatic

/**
 * OAuthTokenService.
 */
@CompileStatic
interface OAuthTokenService {
    AccessToken generateAccessToken(Client appClient, Long userId, Set<String> scopes, Long overrideExpiration)

    AccessToken generateAccessToken(Client appClient, Long userId, Set<String> scopes, Boolean ipRestriction, Long overrideExpiration)

    AccessToken generateAccessToken(Client client, Long userId, Set<String> scopes, Long overrideExpiration, String loginStateHash)

    AccessToken generateAccessToken(Client appClient, Long userId, Set<String> scopes, Boolean ipRestriction, Long overrideExpiration, String loginStateHash)

    AccessToken getAccessToken(String tokenValue)

    AccessToken extractAccessToken(String authorization)

    List<AccessToken> getAccessTokenByUserIdClientId(Long userId, String clientId)

    AccessToken updateAccessToken(AccessToken accessToken)

    RefreshToken generateRefreshToken(Client client, AccessToken accessToken, String salt)

    RefreshToken generateRefreshToken(Client client, AccessToken accessToken, RefreshToken oldRefreshToken)

    List<RefreshToken> getRefreshTokenByUserIdClientId(Long userId, String clientId)

    RefreshToken getRefreshToken(String tokenValue)

    RefreshToken getAndRemoveRefreshToken(String tokenValue)

    IdToken generateIdToken(Client client, String issuer, Long userId, String nonce, Date lastAuthDate,
                            AuthorizationCode code, AccessToken accessToken)

    IdToken parseIdToken(String tokenValue)

    void revokeAccessToken(String tokenValue, Client client)

    void revokeRefreshToken(String tokenValue, Client client)

    void revokeAccessTokenByUserId(Long userId, Client client)

    void revokeAccessToken(Long userId)

    void revokeAccessTokenByLoginStateHash(String loginState)

    void revokeRefreshToken(Long userId)

    boolean isValidAccessToken(String tokenValue)

    boolean isValidRefreshToken(String tokenValue)

    String generateSessionStatePerClient(String sessionId, String clientId, String redirectUri)
}
