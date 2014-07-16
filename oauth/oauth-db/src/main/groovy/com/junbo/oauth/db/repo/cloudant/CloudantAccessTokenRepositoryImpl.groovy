/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.repo.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.oauth.db.generator.TokenGenerator
import com.junbo.oauth.db.repo.AccessTokenRepository
import com.junbo.oauth.spec.model.AccessToken
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
/**
 * CloudantAccessTokenRepositoryImpl.
 */
@CompileStatic
class CloudantAccessTokenRepositoryImpl extends CloudantClient<AccessToken> implements AccessTokenRepository {
    private TokenGenerator tokenGenerator

    @Required
    void setTokenGenerator(TokenGenerator tokenGenerator) {
        this.tokenGenerator = tokenGenerator
    }

    @Override
    AccessToken save(AccessToken accessToken) {
        if (accessToken.tokenValue == null) {
            accessToken.tokenValue = tokenGenerator.generateAccessToken()
        }

        return cloudantPostSync(accessToken)
    }

    @Override
    AccessToken get(String tokenValue) {
        return cloudantGetSync(tokenValue)
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
        cloudantDeleteSync(tokenValue)
    }

    @Override
    boolean isValidAccessToken(String tokenValue) {
        return tokenGenerator.isValidAccessToken(tokenValue)
    }
}
