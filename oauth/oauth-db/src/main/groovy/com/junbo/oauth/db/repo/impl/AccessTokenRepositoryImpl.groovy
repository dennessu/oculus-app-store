/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.repo.impl

import com.junbo.oauth.db.dao.AccessTokenDAO
import com.junbo.oauth.db.entity.AccessTokenEntity
import com.junbo.oauth.db.generator.TokenGenerator
import com.junbo.oauth.db.repo.AccessTokenRepository
import com.junbo.oauth.spec.model.AccessToken
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Javadoc.
 */
@CompileStatic
class AccessTokenRepositoryImpl implements AccessTokenRepository {
    private AccessTokenDAO accessTokenDAO

    private TokenGenerator tokenGenerator

    @Required
    void setAccessTokenDAO(AccessTokenDAO accessTokenDAO) {
        this.accessTokenDAO = accessTokenDAO
    }

    @Required
    void setTokenGenerator(TokenGenerator tokenGenerator) {
        this.tokenGenerator = tokenGenerator
    }

    @Override
    void save(AccessToken accessToken) {
        if (accessToken.tokenValue == null) {
            accessToken.tokenValue = tokenGenerator.generateAccessToken()
        }

        accessTokenDAO.save(unwrap(accessToken))
    }

    @Override
    AccessToken get(String tokenValue) {
        return wrap(accessTokenDAO.get(tokenValue))
    }

    @Override
    void remove(String tokenValue) {
        def entity = accessTokenDAO.get(tokenValue)
        if (entity != null) {
            accessTokenDAO.delete(entity)
        }
    }

    private static AccessTokenEntity unwrap(AccessToken accessToken) {
        if (accessToken == null) {
            return null
        }

        return new AccessTokenEntity(
                id: accessToken.tokenValue,
                clientId: accessToken.clientId,
                userId: accessToken.userId,
                scopes: accessToken.scopes,
                expiredBy: accessToken.expiredBy,
                refreshTokenValue: accessToken.refreshTokenValue
        )
    }

    private static AccessToken wrap(AccessTokenEntity entity) {
        if (entity == null) {
            return null
        }
        return new AccessToken(
                tokenValue: entity.id,
                clientId: entity.clientId,
                userId: entity.userId,
                scopes: entity.scopes,
                expiredBy: entity.expiredBy,
                refreshTokenValue: entity.refreshTokenValue
        )
    }
}
