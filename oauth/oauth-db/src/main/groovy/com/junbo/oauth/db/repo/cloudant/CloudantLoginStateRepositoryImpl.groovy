/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.repo.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.oauth.db.generator.TokenGenerator
import com.junbo.oauth.db.repo.LoginStateRepository
import com.junbo.oauth.spec.model.LoginState
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * CloudantLoginStateRepositoryImpl.
 */
@CompileStatic
class CloudantLoginStateRepositoryImpl extends CloudantClient<LoginState> implements LoginStateRepository {
    private TokenGenerator tokenGenerator

    private long defaultLoginStateExpiration

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
        LoginState loginState = cloudantGetSync(id)
        if (loginState != null) {
            loginState.loginStateId = id
        }

        return loginState
    }

    @Override
    LoginState save(LoginState loginState) {
        if (loginState.id == null) {
            loginState.id = tokenGenerator.generateLoginStateId()
            loginState.hashedId = tokenGenerator.hashKey(loginState.id)
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
    void delete(String id) {
        cloudantDeleteSync(tokenGenerator.hashKey(id))
    }
}
