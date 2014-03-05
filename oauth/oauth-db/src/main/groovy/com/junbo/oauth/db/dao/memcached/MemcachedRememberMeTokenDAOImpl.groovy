/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.dao.memcached

import com.junbo.oauth.common.JsonMarshaller
import com.junbo.oauth.db.dao.RememberMeTokenDAO
import com.junbo.oauth.db.entity.RememberMeTokenEntity
import groovy.transform.CompileStatic

/**
 * Javadoc.
 */
@CompileStatic
class MemcachedRememberMeTokenDAOImpl extends MemcachedBaseDAO implements RememberMeTokenDAO {
    @Override
    RememberMeTokenEntity save(RememberMeTokenEntity entity) {
        memcachedClient.add(namespace + entity.tokenValue, (int) (entity.expiredBy.time / MILLION),
                JsonMarshaller.marshall(entity))
        return entity
    }

    @Override
    RememberMeTokenEntity get(String id) {
        Object entityString = memcachedClient.get(namespace + id)
        if (entityString == null) {
            return null
        }

        return JsonMarshaller.unmarshall(RememberMeTokenEntity, entityString.toString())
    }

    @Override
    RememberMeTokenEntity update(RememberMeTokenEntity entity) {
        memcachedClient.set(namespace + entity.tokenValue, (int) (entity.expiredBy.time / MILLION),
                JsonMarshaller.marshall(entity))
        return entity
    }

    @Override
    void delete(String id) {
        memcachedClient.delete(namespace + id)
    }
}
