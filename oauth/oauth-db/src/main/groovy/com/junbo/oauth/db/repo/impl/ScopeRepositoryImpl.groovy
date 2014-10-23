/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.repo.impl

import com.junbo.oauth.db.dao.ScopeDAO
import com.junbo.oauth.db.entity.ScopeEntity
import com.junbo.oauth.db.repo.ScopeRepository
import com.junbo.oauth.spec.model.Scope
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

import javax.ws.rs.NotSupportedException

/**
 * ScopeRepositoryImpl.
 */
@CompileStatic
class ScopeRepositoryImpl implements ScopeRepository {
    private ScopeDAO scopeDAO

    @Required
    void setScopeDAO(ScopeDAO scopeDAO) {
        this.scopeDAO = scopeDAO
    }

    @Override
    List<Scope> getScopes() {
        throw new NotSupportedException()
    }

    @Override
    Scope getScope(String name) {
        return wrap(scopeDAO.get(name))
    }

    @Override
    Scope saveScope(Scope scope) {
        return wrap(scopeDAO.save(unwrap(scope)))
    }

    @Override
    Scope updateScope(Scope scope, Scope oldScope) {
        return wrap(scopeDAO.update(unwrap(scope)))
    }

    @Override
    void deleteScope(Scope scope) {
        scopeDAO.delete(unwrap(scope))
    }

    private static ScopeEntity unwrap(Scope scope) {
        if (scope == null) {
            return null
        }

        return new ScopeEntity(
                id: scope.name,
                description: scope.description,
                logoUri: scope.logoUri,
                revision: scope.rev
        )
    }

    private static Scope wrap(ScopeEntity entity) {
        if (entity == null) {
            return null
        }

        return new Scope(
                name: entity.id,
                description: entity.description,
                logoUri: entity.logoUri,
                rev: entity.revision
        )
    }
}
