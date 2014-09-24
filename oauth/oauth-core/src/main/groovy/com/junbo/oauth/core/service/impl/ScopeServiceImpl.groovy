/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.service.impl

import com.junbo.authorization.AuthorizeContext
import com.junbo.common.error.AppCommonErrors
import com.junbo.oauth.spec.error.AppErrors
import com.junbo.oauth.core.service.OAuthTokenService
import com.junbo.oauth.core.service.ScopeService
import com.junbo.oauth.core.util.UriUtil
import com.junbo.oauth.db.exception.DBUpdateConflictException
import com.junbo.oauth.db.repo.ScopeRepository
import com.junbo.oauth.spec.model.Scope
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

/**
 * ScopeServiceImpl.
 */
@CompileStatic
class ScopeServiceImpl implements ScopeService {
    private static final String SCOPE_INFO_SCOPE = 'scope.info'
    private static final String SCOPE_MANAGE_SCOPE = 'scope.manage'

    private ScopeRepository scopeRepository
    private OAuthTokenService tokenService

    @Required
    void setScopeRepository(ScopeRepository scopeRepository) {
        this.scopeRepository = scopeRepository
    }

    @Required
    void setTokenService(OAuthTokenService tokenService) {
        this.tokenService = tokenService
    }

    @Override
    Scope saveScope(Scope scope) {
        if (!AuthorizeContext.hasScopes(SCOPE_MANAGE_SCOPE)) {
            throw AppErrors.INSTANCE.insufficientScope().exception()
        }

        Scope existingScope = scopeRepository.getScope(scope.name)

        if (existingScope != null) {
            throw AppErrors.INSTANCE.duplicateEntityName('scope', scope.name).exception()
        }

        if (scope.logoUri != null && !UriUtil.isValidUri(scope.logoUri)) {
            throw AppErrors.INSTANCE.invalidLogoUri(scope.logoUri).exception()
        }

        return scopeRepository.saveScope(scope)
    }

    @Override
    Scope getScope(String scopeName) {
        Scope scope = scopeRepository.getScope(scopeName)
        if (scope == null) {
            throw AppCommonErrors.INSTANCE.resourceNotFound('scope', scopeName).exception()
        }

        return scope
    }

    @Override
    List<Scope> getScopes(String scopeNames) {
        if (!AuthorizeContext.hasScopes(SCOPE_INFO_SCOPE)) {
            throw AppErrors.INSTANCE.insufficientScope().exception()
        }

        String[] names = scopeNames == null ? new String[0] : scopeNames.split(',')

        List<Scope> scopes = []
        names.each { String name ->
            Scope scope = scopeRepository.getScope(name)
            if (scope != null) {
                scopes.add(scope)
            }
        }

        return scopes
    }

    @Override
    Scope updateScope(String scopeName, Scope scope) {
        if (StringUtils.isEmpty(scope.rev)) {
            throw AppCommonErrors.INSTANCE.fieldRequired('revision').exception()
        }

        if (!AuthorizeContext.hasScopes(SCOPE_MANAGE_SCOPE)) {
            throw AppErrors.INSTANCE.insufficientScope().exception()
        }

        Scope existingScope = scopeRepository.getScope(scopeName)

        if (scope.rev != existingScope.rev) {
            throw AppErrors.INSTANCE.updateConflict().exception()
        }

        if (scope.name != existingScope.name) {
            throw AppErrors.INSTANCE.cantUpdateFields('scope_name').exception()
        }

        if (scope.logoUri != null && !UriUtil.isValidUri(scope.logoUri)) {
            throw AppErrors.INSTANCE.invalidLogoUri(scope.logoUri).exception()
        }

        try {
            return scopeRepository.updateScope(scope, existingScope)
        } catch (DBUpdateConflictException e) {
            throw AppErrors.INSTANCE.updateConflict().exception()
        }
    }
}
