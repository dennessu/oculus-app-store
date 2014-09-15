/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.repo.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.oauth.db.generator.TokenGenerator
import com.junbo.oauth.db.repo.RefreshTokenRepository
import com.junbo.oauth.spec.model.RefreshToken
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert
import org.springframework.util.StringUtils

/**
 * CloudantRefreshTokenRepositoryImpl.
 */
@CompileStatic
class CloudantRefreshTokenRepositoryImpl extends CloudantClient<RefreshToken>
        implements RefreshTokenRepository {
    private static final String DELIMITER = '.'

    private TokenGenerator tokenGenerator

    @Required
    void setTokenGenerator(TokenGenerator tokenGenerator) {
        this.tokenGenerator = tokenGenerator
    }

    @Override
    RefreshToken save(RefreshToken refreshToken) {
        if (refreshToken.tokenValue == null) {
            refreshToken.tokenValue = tokenGenerator.generateRefreshToken() +
                    DELIMITER + tokenGenerator.generateRefreshTokenSeries()
            refreshToken.hashedTokenValue = tokenGenerator.hashKey(refreshToken.tokenValue)
        } else {
            String[] tokens = refreshToken.tokenValue.split('\\.')
            Assert.isTrue(tokens.length == 2)

            refreshToken.tokenValue = tokens[0] + DELIMITER + tokenGenerator.generateRefreshTokenSeries()
            refreshToken.hashedTokenValue = tokenGenerator.hashKey(refreshToken.tokenValue)
        }

        return cloudantPostSync(refreshToken)
    }

    @Override
    RefreshToken get(String tokenValue) {
        if (StringUtils.isEmpty(tokenValue)) {
            return null
        }

        RefreshToken token = cloudantGetSync(tokenGenerator.hashKey(tokenValue))
        if (token != null) {
            token.tokenValue = tokenValue
        }

        return token
    }

    @Override
    List<RefreshToken> findByUserIdClientId(Long userId, String clientId) {
        return queryViewSync('by_user_id_client_id', "$userId:$clientId")
    }

    @Override
    RefreshToken getAndRemove(String tokenValue) {
        if (StringUtils.isEmpty(tokenValue)) {
            return null
        }

        String hashed = tokenGenerator.hashKey(tokenValue)
        RefreshToken entity = cloudantGetSync(hashed)
        if (entity != null) {
            entity.tokenValue = tokenValue
        }

        cloudantDeleteSync(hashed)
        return entity
    }

    @Override
    boolean isValidRefreshToken(String tokenValue) {
        return tokenGenerator.isValidRefreshToken(tokenValue)
    }
}
