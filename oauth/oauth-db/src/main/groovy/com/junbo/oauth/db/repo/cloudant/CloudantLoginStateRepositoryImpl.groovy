/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.repo.cloudant

import com.junbo.oauth.db.repo.LoginStateRepository
import com.junbo.oauth.spec.model.LoginState
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils
/**
 * CloudantLoginStateRepositoryImpl.
 */
@CompileStatic
class CloudantLoginStateRepositoryImpl extends CloudantTokenRepositoryBase<LoginState> implements LoginStateRepository {

    private long defaultLoginStateExpiration

    @Required
    void setDefaultLoginStateExpiration(long defaultLoginStateExpiration) {
        this.defaultLoginStateExpiration = defaultLoginStateExpiration
    }

    @Override
    LoginState get(String id) {
        if (StringUtils.isEmpty(id)) {
            return null
        }

        LoginState loginState = cloudantGetSyncUuidWithFallback(id, tokenGenerator.hashKey(id))
        if (loginState != null) {
            loginState.loginStateId = id
        }

        return loginState
    }

    @Override
    LoginState save(LoginState loginState) {
        if (loginState.loginStateId == null) {
            loginState.loginStateId = tokenGenerator.generateLoginStateId()
            loginState.hashedId = tokenGenerator.hashKey(loginState.loginStateId)
        }

        if (loginState.sessionId == null) {
            loginState.sessionId = tokenGenerator.generateSessionStateId()
        }

        if (loginState.expiredBy == null) {
            loginState.expiredBy = new Date(System.currentTimeMillis() + defaultLoginStateExpiration * 1000)
        }

        return cloudantPostSync(loginState)
    }

    @Override
    void remove(String id) {
        if (StringUtils.hasText(id)) {
            cloudantDeleteSync(tokenGenerator.hashKey(id))
        }
    }

    @Override
    void removeByHash(String hash) {
        cloudantDeleteSync(hash)
    }
}
