/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.dao.memcached

import com.junbo.oauth.common.JsonMarshaller
import com.junbo.oauth.db.dao.LoginStateDAO
import com.junbo.oauth.db.entity.LoginStateEntity
import groovy.transform.CompileStatic

/**
 * Javadoc.
 */
@CompileStatic
class MemcachedLoginStateDAOImpl extends MemcachedBaseDAO implements LoginStateDAO {
    @Override
    LoginStateEntity save(LoginStateEntity entity) {
        memcachedClient.add(namespace + entity.id, (int) (entity.expiredBy.time / MILLION),
                JsonMarshaller.marshall(entity))
        return entity
    }

    @Override
    LoginStateEntity get(String id) {
        Object entityString = memcachedClient.get(namespace + id)
        if (entityString == null) {
            return null
        }

        return JsonMarshaller.unmarshall(LoginStateEntity, entityString.toString())

    }

    @Override
    LoginStateEntity update(LoginStateEntity entity) {
        memcachedClient.add(namespace + entity.id, (int) (entity.expiredBy.time / MILLION),
                JsonMarshaller.marshall(entity))
        return entity
    }

    @Override
    void delete(String id) {
        memcachedClient.delete(namespace + id)
    }
}
