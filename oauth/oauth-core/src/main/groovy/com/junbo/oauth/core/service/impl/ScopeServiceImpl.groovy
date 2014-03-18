/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.service.impl

import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.core.service.ScopeService
import com.junbo.oauth.core.service.TokenService
import com.junbo.oauth.core.util.UriUtil
import com.junbo.oauth.db.exception.DBUpdateConflictException
import com.junbo.oauth.db.repo.ScopeRepository
import com.junbo.oauth.spec.model.AccessToken
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
    private TokenService tokenService

    @Required
    void setScopeRepository(ScopeRepository scopeRepository) {
        this.scopeRepository = scopeRepository
    }

    @Required
    void setTokenService(TokenService tokenService) {
        this.tokenService = tokenService
    }

    @Override
    Scope saveScope(String authorization, Scope scope) {
        AccessToken accessToken = tokenService.extractAccessToken(authorization)

        if (!accessToken.scopes.contains(SCOPE_MANAGE_SCOPE)) {
            throw AppExceptions.INSTANCE.insufficientScope().exception()
        }

        Scope existingScope = scopeRepository.getScope(scope.name)

        if (existingScope != null) {
            throw AppExceptions.INSTANCE.duplicateScopeName().exception()
        }

        if (scope.logoUri != null && !UriUtil.isValidUri(scope.logoUri)) {
            throw AppExceptions.INSTANCE.invalidLogoUri(scope.logoUri).exception()
        }

        return scopeRepository.saveScope(scope)
    }

    @Override
    Scope getScope(String authorization, String scopeName) {
        AccessToken accessToken = tokenService.extractAccessToken(authorization)

        if (!accessToken.scopes.contains(SCOPE_INFO_SCOPE)) {
            throw AppExceptions.INSTANCE.insufficientScope().exception()
        }

        return scopeRepository.getScope(scopeName)
    }

    @Override
    Scope getScope(String scopeName) {
        return scopeRepository.getScope(scopeName)
    }

    @Override
    List<Scope> getScopes(String authorization, String scopeNames) {
        AccessToken accessToken = tokenService.extractAccessToken(authorization)

        if (!accessToken.scopes.contains(SCOPE_INFO_SCOPE)) {
            throw AppExceptions.INSTANCE.insufficientScope().exception()
        }

        String[] names = scopeNames.split(',')

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
    Scope updateScope(String authorization, String scopeName, Scope scope) {
        if (StringUtils.isEmpty(scope.revision)) {
            throw AppExceptions.INSTANCE.missingRevision().exception()
        }

        AccessToken accessToken = tokenService.extractAccessToken(authorization)

        if (!accessToken.scopes.contains(SCOPE_MANAGE_SCOPE)) {
            throw AppExceptions.INSTANCE.insufficientScope().exception()
        }

        Scope existingScope = scopeRepository.getScope(scopeName)

        if (scope.revision != existingScope.revision) {
            throw AppExceptions.INSTANCE.updateConflict().exception()
        }

        if (scope.name != existingScope.name) {
            throw AppExceptions.INSTANCE.cantUpdateFields('scope_name').exception()
        }

        if (scope.logoUri != null && !UriUtil.isValidUri(scope.logoUri)) {
            throw AppExceptions.INSTANCE.invalidLogoUri(scope.logoUri).exception()
        }

        try {
            return scopeRepository.updateScope(scope)
        } catch (DBUpdateConflictException e) {
            throw AppExceptions.INSTANCE.updateConflict().exception()
        }
    }
}
