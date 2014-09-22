/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.repo.cloudant

import com.junbo.oauth.db.repo.AccessTokenRepository
import com.junbo.oauth.spec.model.AccessToken
import groovy.transform.CompileStatic
import org.springframework.util.StringUtils
/**
 * CloudantAccessTokenRepositoryImpl.
 */
@CompileStatic
class CloudantAccessTokenRepositoryImpl
        extends CloudantTokenRepositoryBase<AccessToken> implements AccessTokenRepository {

    @Override
    AccessToken save(AccessToken accessToken) {
        if (accessToken.tokenValue == null) {
            accessToken.tokenValue = tokenGenerator.generateAccessToken(accessToken.userId)
            accessToken.hashedTokenValue = tokenGenerator.hashKey(accessToken.tokenValue)
        }

        return cloudantPostSync(accessToken)
    }

    @Override
    AccessToken get(String tokenValue) {
        if (StringUtils.isEmpty(tokenValue)) {
            return null
        }

        String tokenHash = tokenGenerator.hashKey(tokenValue)
        AccessToken token = cloudantGetSyncWithFallback(tokenValue, tokenHash)
        if (token != null) {
            token.tokenValue = tokenValue
        }

        return token
    }

    @Override
    List<AccessToken> findByRefreshToken(String refreshTokenValue) {
        return queryViewSync('by_refresh_token', refreshTokenValue)
    }

    @Override
    List<AccessToken> findByUserIdClientId(Long userId, String clientId) {
        return queryViewSync('by_user_id_client_id', "$userId:$clientId")
    }

    @Override
    AccessToken update(AccessToken accessToken, AccessToken oldAccessToken) {
        return cloudantPutSync(accessToken, oldAccessToken)
    }

    @Override
    void remove(String tokenValue) {
        if (StringUtils.hasText(tokenValue)) {
            cloudantDeleteSync(tokenGenerator.hashKey(tokenValue))
        }
    }

    @Override
    void removeByHash(String hash) {
        cloudantDeleteSync(hash)
    }

    @Override
    boolean isValidAccessToken(String tokenValue) {
        return tokenGenerator.isValidAccessToken(tokenValue)
    }
}
