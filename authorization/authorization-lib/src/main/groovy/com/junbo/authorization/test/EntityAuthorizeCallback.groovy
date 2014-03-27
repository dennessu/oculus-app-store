/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.test

import com.junbo.authorization.AuthorizeCallback
import com.junbo.authorization.model.AccessToken
import com.junbo.authorization.model.AuthorizeContext
import com.junbo.authorization.model.Role
import groovy.transform.CompileStatic

/**
 * EntityAuthorizeCallback.
 */
@CompileStatic
class EntityAuthorizeCallback implements AuthorizeCallback<Entity> {

    @Override
    AuthorizeContext newContext(Map<String, Object> map) {
        return new AuthorizeContext(context: map)
    }

    @Override
    AuthorizeContext newContext(Entity resourceEntity) {
        Map<String, Object> map = [:]
        map['entity'] = resourceEntity
        return newContext(map)
    }

    @Override
    Boolean hasRole(Role role, AuthorizeContext context) {
        switch (role) {
            case Role.OWNER:
                Long id = (Long) context.context['id']

                if (id == null) {
                    Entity entity = (Entity) context.context['entity']
                    id = entity.id
                }

                AccessToken accessToken = (AccessToken) context.context['accessToken']

                return id == accessToken.userId
            case Role.ADMIN:
                AccessToken accessToken = (AccessToken) context.context['accessToken']

                return accessToken.userId == 123L
            case Role.GUEST:
                Long id = (Long) context.context['id']

                if (id == null) {
                    Entity entity = (Entity) context.context['entity']
                    id = entity.id
                }

                AccessToken accessToken = (AccessToken) context.context['accessToken']

                return id != accessToken.userId && accessToken.userId != 123L
            default:
                return false
        }
    }

    @Override
    Entity postFilter(Entity resourceEntity, AuthorizeContext context) {
        Set<String> claims = context.claims
        if (!claims.contains('read')) {
            return null
        }

        if (!claims.contains('owner') && !claims.contains('admin')) {
            resourceEntity.name = null
        }

        if (!claims.contains('admin')) {
            resourceEntity.createdBy = null
        }
        return resourceEntity
    }
}
