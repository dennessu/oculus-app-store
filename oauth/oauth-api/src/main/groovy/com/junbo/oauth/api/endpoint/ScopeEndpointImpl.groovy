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
 * Default {@link com.junbo.oauth.spec.endpoint.ScopeEndpoint} implementation.
 * @author Zhanxin Yang
 * @see com.junbo.oauth.spec.endpoint.ScopeEndpoint
 */
@CompileStatic
@org.springframework.context.annotation.Scope('prototype')
class ScopeEndpointImpl implements ScopeEndpoint {
    /**
     * The ScopeService to handle the scope related logic.
     */
    private ScopeService scopeService

    @Required
    void setScopeService(ScopeService scopeService) {
        this.scopeService = scopeService
    }

    /**
     * Endpoint to create a scope.
     * The requester should provide an access token with scope of 'scope.manage'.
     * @param scope The request body of the to-be-created scope.
     * @return The created scope.
     */
    @Override
    Promise<Scope> postScope(Scope scope) {
        return Promise.pure(scopeService.saveScope(scope))
    }

    /**
     * Endpoint to retrieve a scope's information.
     * The requester should provide an access token with scope of 'scope.info'.
     * @param scopeName The scope name of the scope to be retrieved.
     * @return The scope information with the given scope name.
     */
    @Override
    Promise<Scope> getScope(String scopeName) {
        return Promise.pure(scopeService.getScope(scopeName))
    }

    /**
     * Endpoint to retrieve a list of scopes' information with given scope names list.
     * The requester should provide an access token with scope of 'scope.info'.
     * @param scopeNames The scope names of the scope to be retrieved, delimited by comma
     * @return The scopes information with the given scope names.
     */
    @Override
    Promise<List<Scope>> getByScopeNames(String scopeNames) {
        return Promise.pure(scopeService.getScopes(scopeNames))
    }

    /**
     * Endpoint to update a scope's information.
     * The requester should provide an access token with scope of 'scope.manage'.
     * @param scopeName The scope name of the scope to be updated.
     * @param scope The request body of the scope to be updated.
     * @return The full scope information (updated version).
     */
    @Override
    Promise<Scope> putScope(String scopeName, Scope scope) {
        return Promise.pure(scopeService.updateScope(scopeName, scope))
    }
}
