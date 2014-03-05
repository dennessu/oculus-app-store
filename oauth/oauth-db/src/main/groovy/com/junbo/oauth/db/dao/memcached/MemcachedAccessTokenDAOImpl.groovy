/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.dao.memcached

import com.junbo.oauth.common.JsonMarshaller
import com.junbo.oauth.db.dao.AccessTokenDAO
import com.junbo.oauth.db.entity.AccessTokenEntity
import groovy.transform.CompileStatic

/**
 * Javadoc.
 */
@CompileStatic
class MemcachedAccessTokenDAOImpl extends MemcachedBaseDAO implements AccessTokenDAO {

    @Override
    AccessTokenEntity save(AccessTokenEntity entity) {
        memcachedClient.add(namespace + entity.tokenValue, (int) (entity.expiredBy.time / MILLION),
                JsonMarshaller.marshall(entity))
        return entity
    }

    @Override
    AccessTokenEntity get(String id) {
        Object entityString = memcachedClient.get(namespace + id)
        if (entityString == null) {
            return null
        }

        return JsonMarshaller.unmarshall(AccessTokenEntity, entityString.toString())
    }

    @Override
    AccessTokenEntity update(AccessTokenEntity entity) {
        memcachedClient.set(namespace + entity.tokenValue, (int) (entity.expiredBy.time / MILLION),
                JsonMarshaller.marshall(entity))
        return entity
    }

    @Override
    void delete(String id) {
        memcachedClient.delete(namespace + id)
    }
}
