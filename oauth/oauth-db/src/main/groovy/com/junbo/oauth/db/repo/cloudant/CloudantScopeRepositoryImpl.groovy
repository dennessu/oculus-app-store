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

/**
 * CloudantScopeRepositoryImpl.
 */
@CompileStatic
class CloudantScopeRepositoryImpl extends CloudantClient<Scope> implements ScopeRepository {
    @Override
    Scope getScope(String name) {
        return cloudantGet(name).get()
    }

    @Override
    Scope saveScope(Scope scope) {
        return cloudantPost(scope).get()
    }

    @Override
    Scope updateScope(Scope scope) {
        return cloudantPut(scope).get()
    }

    @Override
    void deleteScope(Scope scope) {
        cloudantDelete(scope).get()
    }
}