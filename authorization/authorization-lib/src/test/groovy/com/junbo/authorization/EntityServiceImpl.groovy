/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization

import com.junbo.authorization.annotation.AuthorizeRequired
import com.junbo.authorization.annotation.ContextParam
import com.junbo.authorization.model.AuthorizeContext
import com.junbo.authorization.service.AuthorizeService
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * EntityServiceImpl.
 */
@CompileStatic
class EntityServiceImpl implements EntityService {

    private AuthorizeService authorizeService

    @Required
    void setAuthorizeService(AuthorizeService authorizeService) {
        this.authorizeService = authorizeService
    }

    @Override
    @AuthorizeRequired(authCallBack = EntityAuthorizeCallback, apiName = 'entity_get')
    Entity annotatedGet(@ContextParam('id') Long id) {
        Set<String> claims = AuthorizeContext.CLAIMS.get()
        if (claims.contains('read')) {
            return new Entity(id: id, name: 'name', createdBy: 'system')
        }
        return null
    }

    @Override
    Entity get(Long id) {
        EntityAuthorizeCallback callback = new EntityAuthorizeCallback('entity_get', id)
        Set<String> claims = authorizeService.getClaims(callback)

        if (!claims.contains('read')) {
            return null
        }

        Entity entity = new Entity(id: id, name: 'name', createdBy: 'system')

        callback = new EntityAuthorizeCallback('entity_get', entity)
        claims = authorizeService.getClaims(callback)

        if (!claims.contains('owner') && !claims.contains('admin')) {
            entity.name = null
        }

        if (!claims.contains('admin')) {
            entity.createdBy = null
        }
        return entity
    }
}
