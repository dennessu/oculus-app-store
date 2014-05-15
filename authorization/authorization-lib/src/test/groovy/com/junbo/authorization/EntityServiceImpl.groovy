/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization

import com.junbo.authorization.service.AuthorizeService
import com.junbo.langur.core.promise.Promise
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
    Entity annotatedGet(Long id) {
        if (AuthorizeContext.hasRights('read')) {
            return new Entity(id: id, name: 'name', createdBy: 'system')
        }
        return null
    }

    @Override
    Entity get(Long id) {
        Entity entity = new Entity(id: id, name: 'name', createdBy: 'system')

        def callback = entityAuthorizeCallbackFactory.create('entity_get', entity)
        return authorizeService.authorizeAndThen(callback) {
            if (!AuthorizeContext.hasRights('owner') && !AuthorizeContext.hasRights('admin')) {
                entity.name = null
            }

            if (!AuthorizeContext.hasRights('admin')) {
                entity.createdBy = null
            }

            return Promise.pure(entity)
        }.wrapped().get()
    }
}
