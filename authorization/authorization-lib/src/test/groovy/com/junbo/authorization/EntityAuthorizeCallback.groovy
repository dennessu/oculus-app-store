/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization

import com.junbo.authorization.model.AuthorizeContext
import com.junbo.oauth.spec.model.TokenInfo
import groovy.transform.CompileStatic

/**
 * EntityAuthorizeCallback.
 */
@CompileStatic
class EntityAuthorizeCallback implements AuthorizeCallback<Entity> {
    String apiName
    Long resourceId
    Entity entity
    TokenInfo tokenInfo

    EntityAuthorizeCallback(String apiName, Long id) {
        this.apiName = apiName
        this.resourceId = id
    }

    EntityAuthorizeCallback(String apiName, Entity entity) {
        this.apiName = apiName
        this.entity = entity
        this.resourceId = entity.id
    }

    EntityAuthorizeCallback(Map<String, Object> context) {
        this.apiName = context['apiName']
        this.resourceId = (Long) context['id']
        this.entity = (Entity) context['entity']
        if (resourceId == null && entity != null) {
            resourceId = entity.id
        }
    }

    Boolean isOwner() {
        return resourceId == tokenInfo.sub.value
    }

    Boolean isAdmin() {
        return tokenInfo.sub.value == 123L
    }

    Boolean isGuest() {
        return !isOwner() && !isAdmin()
    }

    @Override
    String getApiName() {
        return apiName
    }

    @Override
    void setTokenInfo(TokenInfo tokenInfo) {
        this.tokenInfo = tokenInfo
    }

    @Override
    Entity postFilter() {
        Set<String> claims = AuthorizeContext.CLAIMS.get()
        if (!claims.contains('read')) {
            return null
        }

        if (!claims.contains('owner') && !claims.contains('admin')) {
            entity.name = null
        }

        if (!claims.contains('admin')) {
            entity.createdBy = null
        }
        return entity
    }
}
