/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.dao.memcached

import com.junbo.oauth.common.JsonMarshaller
import com.junbo.oauth.db.dao.AuthorizationCodeDAO
import com.junbo.oauth.db.entity.AuthorizationCodeEntity
import groovy.transform.CompileStatic

/**
 * Javadoc.
 */
@CompileStatic
class MemcachedAuthorizationCodeDAOImpl extends MemcachedBaseDAO implements AuthorizationCodeDAO {
    @Override
    AuthorizationCodeEntity save(AuthorizationCodeEntity entity) {
        memcachedClient.add(namespace + entity.code, (int) (entity.expiredBy.time / MILLION),
                JsonMarshaller.marshall(entity))
        return entity
    }

    @Override
    AuthorizationCodeEntity get(String id) {
        Object entityString = memcachedClient.get(namespace + id)
        if (entityString == null) {
            return null
        }

        return JsonMarshaller.unmarshall(AuthorizationCodeEntity, entityString.toString())

    }

    @Override
    AuthorizationCodeEntity update(AuthorizationCodeEntity entity) {
        memcachedClient.set(namespace + entity.code, (int) (entity.expiredBy.time / MILLION),
                JsonMarshaller.marshall(entity))
        return entity
    }

    @Override
    void delete(String id) {
        memcachedClient.delete(namespace + id)
    }
}
