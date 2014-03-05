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
    void save(RefreshToken refreshToken) {
        if (refreshToken.tokenValue == null) {
            refreshToken.tokenValue = tokenGenerator.generateRefreshToken() +
                    DELIMITER + tokenGenerator.generateRefreshTokenSeries()
        } else {
            String[] tokens = refreshToken.tokenValue.split(DELIMITER)
            Assert.isTrue(tokens.length == 2)

            refreshToken.tokenValue = tokens[0] + DELIMITER + tokenGenerator.generateRefreshTokenSeries()
        }

        refreshTokenDAO.save(unwrap(refreshToken))
    }

    @Override
    RefreshToken getAndRemove(String tokenValue) {
        RefreshToken refreshToken = wrap(refreshTokenDAO.get(tokenValue))
        refreshTokenDAO.delete(tokenValue)
        return refreshToken
    }

    private static RefreshTokenEntity unwrap(RefreshToken refreshToken) {
        if (refreshToken == null) {
            return null
        }

        return new RefreshTokenEntity(
                tokenValue: refreshToken.tokenValue,
                clientId: refreshToken.clientId,
                userId: refreshToken.userId,
                accessToken: JsonMarshaller.marshall(refreshToken.accessToken),
                expiredBy: refreshToken.expiredBy,
                salt: refreshToken.salt
        )
    }

    private static RefreshToken wrap(RefreshTokenEntity entity) {
        if (entity == null) {
            return null
        }

        return new RefreshToken(
                tokenValue: entity.tokenValue,
                clientId: entity.clientId,
                userId: entity.userId,
                accessToken: JsonMarshaller.unmarshall(AccessToken, entity.accessToken),
                expiredBy: entity.expiredBy,
                salt: entity.salt
        )
    }
}
