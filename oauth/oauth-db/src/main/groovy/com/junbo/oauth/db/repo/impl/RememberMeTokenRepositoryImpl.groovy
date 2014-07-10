/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.repo.impl

import com.junbo.oauth.db.dao.RememberMeTokenDAO
import com.junbo.oauth.db.entity.RememberMeTokenEntity
import com.junbo.oauth.db.generator.TokenGenerator
import com.junbo.oauth.db.repo.RememberMeTokenRepository
import com.junbo.oauth.spec.model.RememberMeToken
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert

/**
 * Javadoc.
 */
@CompileStatic
class RememberMeTokenRepositoryImpl implements RememberMeTokenRepository {

    private static final String DELIMITER = '.'

    private RememberMeTokenDAO rememberMeTokenDAO

    private TokenGenerator tokenGenerator

    @Required
    void setRememberMeTokenDAO(RememberMeTokenDAO rememberMeTokenDAO) {
        this.rememberMeTokenDAO = rememberMeTokenDAO
    }

    @Required
    void setTokenGenerator(TokenGenerator tokenGenerator) {
        this.tokenGenerator = tokenGenerator
    }

    @Override
    RememberMeToken save(RememberMeToken rememberMeToken) {
        if (rememberMeToken.tokenValue == null) {
            rememberMeToken.tokenValue = tokenGenerator.generateRememberMeToken() +
                    DELIMITER + tokenGenerator.generateRememberMeTokenSeries()
        } else {
            String[] tokens = rememberMeToken.tokenValue.split(DELIMITER)
            Assert.isTrue(tokens.length == 2)

            rememberMeToken.tokenValue = tokens[0] + DELIMITER + tokenGenerator.generateRememberMeTokenSeries()
        }

        return wrap(rememberMeTokenDAO.save(unwrap(rememberMeToken)))
    }

    @Override
    RememberMeToken get(String tokenValue) {
        return wrap(rememberMeTokenDAO.get(tokenValue))
    }

    @Override
    RememberMeToken getAndRemove(String tokenValue) {
        def entity = rememberMeTokenDAO.get(tokenValue)

        if (entity != null) {
            rememberMeTokenDAO.delete(entity)
        }

        return wrap(entity)
    }

    private static RememberMeTokenEntity unwrap(RememberMeToken rememberMeToken) {
        if (rememberMeToken == null) {
            return null
        }

        return new RememberMeTokenEntity(
                id: rememberMeToken.tokenValue,
                userId: rememberMeToken.userId,
                expiredBy: rememberMeToken.expiredBy,
                lastAuthDate: rememberMeToken.lastAuthDate,
                revision: rememberMeToken.rev
        )
    }

    private static RememberMeToken wrap(RememberMeTokenEntity entity) {
        if (entity == null) {
            return null
        }

        return new RememberMeToken(
                tokenValue: entity.id,
                userId: entity.userId,
                expiredBy: entity.expiredBy,
                lastAuthDate: entity.lastAuthDate,
                rev: entity.revision
        )
    }
}
