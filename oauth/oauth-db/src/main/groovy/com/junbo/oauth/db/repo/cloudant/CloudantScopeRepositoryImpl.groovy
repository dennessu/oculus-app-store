/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.repo.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.oauth.db.repo.ScopeRepository
import com.junbo.oauth.spec.model.Scope
import groovy.transform.CompileStatic

import javax.ws.rs.NotSupportedException

/**
 * CloudantScopeRepositoryImpl.
 */
@CompileStatic
class CloudantScopeRepositoryImpl extends CloudantClient<Scope> implements ScopeRepository {
    @Override
    List<Scope> getScopes() {
        throw new NotSupportedException()
    }

    @Override
    Scope getScope(String name) {
        return cloudantGetSync(name)
    }

    @Override
    Scope saveScope(Scope scope) {
        return cloudantPostSync(scope)
    }

    @Override
    Scope updateScope(Scope scope, Scope oldScope) {
        return cloudantPutSync(scope, oldScope)
    }

    @Override
    void deleteScope(Scope scope) {
        cloudantDeleteSync(scope)
    }
}
