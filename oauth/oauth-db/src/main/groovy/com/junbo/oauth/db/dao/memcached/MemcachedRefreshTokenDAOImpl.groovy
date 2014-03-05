/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.dao.memcached

import com.junbo.oauth.common.JsonMarshaller
import com.junbo.oauth.db.dao.RefreshTokenDAO
import com.junbo.oauth.db.entity.RefreshTokenEntity
import groovy.transform.CompileStatic

/**
 * Javadoc.
 */
@CompileStatic
class MemcachedRefreshTokenDAOImpl extends MemcachedBaseDAO implements RefreshTokenDAO {
    @Override
    RefreshTokenEntity save(RefreshTokenEntity entity) {
        memcachedClient.add(namespace + entity.tokenValue, (int) (entity.expiredBy.time / MILLION),
                JsonMarshaller.marshall(entity))
        return entity
    }

    @Override
    RefreshTokenEntity get(String id) {
        Object entityString = memcachedClient.get(namespace + id)
        if (entityString == null) {
            return null
        }

        return JsonMarshaller.unmarshall(RefreshTokenEntity, entityString.toString())
    }

    @Override
    RefreshTokenEntity update(RefreshTokenEntity entity) {
        memcachedClient.set(namespace + entity.tokenValue, (int) (entity.expiredBy.time / MILLION),
                JsonMarshaller.marshall(entity))
        return entity
    }

    @Override
    void delete(String id) {
        memcachedClient.delete(namespace + id)
    }
}
