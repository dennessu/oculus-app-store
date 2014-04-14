/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization

import com.junbo.authorization.annotation.AuthContextParam
import com.junbo.authorization.annotation.AuthorizeRequired
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

    private EntityAuthorizeCallbackFactory entityAuthorizeCallbackFactory

    @Required
    void setEntityAuthorizeCallbackFactory(EntityAuthorizeCallbackFactory entityAuthorizeCallbackFactory) {
        this.entityAuthorizeCallbackFactory = entityAuthorizeCallbackFactory
    }

    @Required
    void setAuthorizeService(AuthorizeService authorizeService) {
        this.authorizeService = authorizeService
    }

    @Override
    @AuthorizeRequired(authCallBackFactoryBean = 'entityAuthorizeCallbackFactory', apiName = 'entity_get')
    Entity annotatedGet(@AuthContextParam('id') Long id) {
        if (AuthorizeContext.hasRight('read')) {
            return new Entity(id: id, name: 'name', createdBy: 'system')
        }
        return null
    }

    @Override
    Entity get(Long id) {
        Map<String, Object> map = [:]
        map['apiName'] = 'entity_get'
        map['id'] = id
        AuthorizeCallback callback = entityAuthorizeCallbackFactory.create(map)
        authorizeService.authorize(callback)

        if (!AuthorizeContext.hasRight('read')) {
            return null
        }

        Entity entity = new Entity(id: id, name: 'name', createdBy: 'system')

        map.clear()
        map['apiName'] = 'entity_get'
        map['entity'] = entity
        callback = entityAuthorizeCallbackFactory.create(map)
        authorizeService.authorize(callback)

        if (!AuthorizeContext.hasRight('owner') && !AuthorizeContext.hasRight('admin')) {
            entity.name = null
        }

        if (!AuthorizeContext.hasRight('admin')) {
            entity.createdBy = null
        }
        return entity
    }
}
