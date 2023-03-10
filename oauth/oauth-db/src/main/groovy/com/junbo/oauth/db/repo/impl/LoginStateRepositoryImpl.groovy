/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.repo.impl

import com.junbo.oauth.db.dao.LoginStateDAO
import com.junbo.oauth.db.entity.LoginStateEntity
import com.junbo.oauth.db.generator.TokenGenerator
import com.junbo.oauth.db.repo.LoginStateRepository
import com.junbo.oauth.spec.model.LoginState
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

import javax.ws.rs.NotSupportedException

/**
 * Javadoc.
 */
@CompileStatic
class LoginStateRepositoryImpl implements LoginStateRepository {
    private LoginStateDAO loginStateDAO

    private TokenGenerator tokenGenerator

    private long defaultLoginStateExpiration

    @Required
    void setLoginStateDAO(LoginStateDAO loginStateDAO) {
        this.loginStateDAO = loginStateDAO
    }

    @Required
    void setTokenGenerator(TokenGenerator tokenGenerator) {
        this.tokenGenerator = tokenGenerator
    }

    @Required
    void setDefaultLoginStateExpiration(long defaultLoginStateExpiration) {
        this.defaultLoginStateExpiration = defaultLoginStateExpiration
    }

    @Override
    LoginState get(String id) {
        return wrap(loginStateDAO.get(id))
    }

    @Override
    LoginState save(LoginState loginState) {
        if (loginState.id == null) {
            loginState.id = tokenGenerator.generateLoginStateId()
        }

        if (loginState.sessionId == null) {
            loginState.sessionId = tokenGenerator.generateSessionStateId()
        }

        if (loginState.expiredBy == null) {
            loginState.expiredBy = new Date(System.currentTimeMillis() + defaultLoginStateExpiration * 1000)
        }

        return wrap(loginStateDAO.update(unwrap(loginState)))
    }

    @Override
    void remove(String id) {
        def entity = loginStateDAO.get(id)
        if (entity != null) {
            loginStateDAO.delete(entity)
        }
    }

    @Override
    void removeByHash(String id) {
        throw new NotSupportedException()
    }

    @Override
    void removeByUserId(Long userId) {

    }

    private static LoginStateEntity unwrap(LoginState loginState) {
        if (loginState == null) {
            return null
        }

        return new LoginStateEntity(
                id: loginState.getId(),
                userId: loginState.userId,
                expiredBy: loginState.expiredBy,
                lastAuthDate: loginState.lastAuthDate,
                sessionId: loginState.sessionId,
                revision: loginState.rev
        )

    }

    private static LoginState wrap(LoginStateEntity entity) {
        if (entity == null) {
            return null
        }

        return new LoginState(
                id: entity.id,
                userId: entity.userId,
                expiredBy: entity.expiredBy,
                lastAuthDate: entity.lastAuthDate,
                sessionId: entity.sessionId,
                rev: entity.revision
        )
    }
}
