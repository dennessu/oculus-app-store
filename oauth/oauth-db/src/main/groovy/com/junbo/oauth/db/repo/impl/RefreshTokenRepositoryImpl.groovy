/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.repo.impl

import com.junbo.oauth.common.JsonMarshaller
import com.junbo.oauth.db.dao.RefreshTokenDAO
import com.junbo.oauth.db.entity.RefreshTokenEntity
import com.junbo.oauth.db.generator.TokenGenerator
import com.junbo.oauth.db.repo.RefreshTokenRepository
import com.junbo.oauth.spec.model.AccessToken
import com.junbo.oauth.spec.model.RefreshToken
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert

/**
 * Javadoc.
 */
@CompileStatic
class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private static final String DELIMITER = '.'

    private RefreshTokenDAO refreshTokenDAO

    private TokenGenerator tokenGenerator

    @Required
    void setRefreshTokenDAO(RefreshTokenDAO refreshTokenDAO) {
        this.refreshTokenDAO = refreshTokenDAO
    }

    @Required
    void setTokenGenerator(TokenGenerator tokenGenerator) {
        this.tokenGenerator = tokenGenerator
    }

    @Override
    RefreshToken save(RefreshToken refreshToken) {
        if (refreshToken.tokenValue == null) {
            refreshToken.tokenValue = tokenGenerator.generateRefreshToken() +
                    DELIMITER + tokenGenerator.generateRefreshTokenSeries()
        } else {
            String[] tokens = refreshToken.tokenValue.split('\\.')
            Assert.isTrue(tokens.length == 2)

            refreshToken.tokenValue = tokens[0] + DELIMITER + tokenGenerator.generateRefreshTokenSeries()
        }

        return wrap(refreshTokenDAO.save(unwrap(refreshToken)))
    }

    @Override
    RefreshToken get(String tokenValue) {
        return wrap(refreshTokenDAO.get(tokenValue))
    }

    @Override
    List<RefreshToken> findByUserIdClientId(Long userId, String clientId) {
        return refreshTokenDAO.findByUserIdClientId(userId, clientId).collect { RefreshTokenEntity entity ->
            return wrap(entity)
        }
    }

    @Override
    RefreshToken getAndRemove(String tokenValue) {
        def entity = refreshTokenDAO.get(tokenValue)

        if (entity != null) {
            refreshTokenDAO.delete(entity)
        }

        return wrap(entity)
    }

    @Override
    boolean isValidRefreshToken(String tokenValue) {
        return tokenGenerator.isValidRefreshToken(tokenValue)
    }

    private static RefreshTokenEntity unwrap(RefreshToken refreshToken) {
        if (refreshToken == null) {
            return null
        }

        return new RefreshTokenEntity(
                id: refreshToken.tokenValue,
                clientId: refreshToken.clientId,
                userId: refreshToken.userId,
                accessToken: JsonMarshaller.marshall(refreshToken.accessToken),
                expiredBy: refreshToken.expiredBy,
                salt: refreshToken.salt,
                revision: refreshToken.rev
        )
    }

    private static RefreshToken wrap(RefreshTokenEntity entity) {
        if (entity == null) {
            return null
        }

        return new RefreshToken(
                tokenValue: entity.id,
                clientId: entity.clientId,
                userId: entity.userId,
                accessToken: JsonMarshaller.unmarshall(entity.accessToken, AccessToken),
                expiredBy: entity.expiredBy,
                salt: entity.salt,
                rev: entity.revision
        )
    }
}
