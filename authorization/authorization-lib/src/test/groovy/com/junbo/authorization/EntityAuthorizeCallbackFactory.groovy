/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization

import groovy.transform.CompileStatic

/**
 * EntityAuthorizeCallbackFactory.
 */
@CompileStatic
class EntityAuthorizeCallbackFactory extends AbstractAuthorizeCallbackFactory<Entity> {

    @Override
    AuthorizeCallback<Entity> create(String apiName, Entity entity) {
        return new EntityAuthorizeCallback(this, apiName, entity)
    }

    private class EntityAuthorizeCallback extends AbstractAuthorizeCallback<Entity> {

        Long resourceId

        EntityAuthorizeCallback(EntityAuthorizeCallbackFactory factory, String apiName, Entity entity) {
            super(factory, apiName, entity)

            if (resourceId == null && entity != null) {
                resourceId = entity.id
            }
        }

        Boolean isOwner() {

            return resourceId == AuthorizeContext.currentUserId.value
        }

        Boolean isAdmin() {
            return AuthorizeContext.currentUserId.value == 123L
        }

        Boolean isGuest() {
            return !isOwner() && !isAdmin()
        }

        @Override
        String getApiName() {
            return apiName
        }
    }
}
