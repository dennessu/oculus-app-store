/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.repo.impl

import com.junbo.oauth.db.dao.AuthorizationCodeDAO
import com.junbo.oauth.db.entity.AuthorizationCodeEntity
import com.junbo.oauth.db.generator.TokenGenerator
import com.junbo.oauth.db.repo.AuthorizationCodeRepository
import com.junbo.oauth.spec.model.AuthorizationCode
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Javadoc.
 */
@CompileStatic
class AuthorizationCodeRepositoryImpl implements AuthorizationCodeRepository {
    private AuthorizationCodeDAO authorizationCodeDAO

    private TokenGenerator tokenGenerator

    @Required
    void setAuthorizationCodeDAO(AuthorizationCodeDAO authorizationCodeDAO) {
        this.authorizationCodeDAO = authorizationCodeDAO
    }

    @Required
    void setTokenGenerator(TokenGenerator tokenGenerator) {
        this.tokenGenerator = tokenGenerator
    }

    @Override
    void save(AuthorizationCode code) {
        if (code.code == null) {
            code.code = tokenGenerator.generateAuthorizationCode()
        }

        authorizationCodeDAO.save(unwrap(code))
    }

    @Override
    AuthorizationCode getAndRemove(String code) {
        AuthorizationCodeEntity entity = authorizationCodeDAO.get(code)
        if (entity != null) {
            authorizationCodeDAO.delete(entity)
        }
        return wrap(entity)
    }

    private static AuthorizationCodeEntity unwrap(AuthorizationCode code) {
        if (code == null) {
            return null
        }

        return new AuthorizationCodeEntity(
                id: code.code,
                userId: code.userId,
                clientId: code.clientId,
                scopes: code.scopes,
                nonce: code.nonce,
                redirectUri: code.redirectUri,
                expiredBy: code.expiredBy,
                lastAuthDate: code.lastAuthDate,
                revision: code.rev
        )
    }

    private static AuthorizationCode wrap(AuthorizationCodeEntity entity) {
        if (entity == null) {
            return null
        }

        return new AuthorizationCode(
                code: entity.id,
                userId: entity.userId,
                clientId: entity.clientId,
                scopes: entity.scopes,
                nonce: entity.nonce,
                redirectUri: entity.redirectUri,
                expiredBy: entity.expiredBy,
                lastAuthDate: entity.lastAuthDate,
                rev: entity.revision
        )

    }
}
