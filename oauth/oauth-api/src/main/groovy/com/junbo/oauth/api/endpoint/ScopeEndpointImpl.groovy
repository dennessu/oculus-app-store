/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.api.endpoint

import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.core.service.ScopeService
import com.junbo.oauth.spec.endpoint.ScopeEndpoint
import com.junbo.oauth.spec.model.Scope
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * ScopeEndpointImpl.
 */
@CompileStatic
class ScopeEndpointImpl implements ScopeEndpoint {
    private ScopeService scopeService

    @Required
    void setScopeService(ScopeService scopeService) {
        this.scopeService = scopeService
    }

    @Override
    Promise<Scope> postScope(String authorization, Scope scope) {
        return Promise.pure(scopeService.saveScope(authorization, scope))
    }

    @Override
    Promise<Scope> getScope(String authorization, String scopeName) {
        return Promise.pure(scopeService.getScope(authorization, scopeName))
    }

    @Override
    Promise<List<Scope>> getByScopeNames(String authorization, String scopeNames) {
        return Promise.pure(scopeService.getScopes(authorization, scopeNames))
    }

    @Override
    Promise<Scope> putScope(String authorization, String scopeName, Scope scope) {
        return Promise.pure(scopeService.updateScope(authorization, scopeName, scope))
    }
}
