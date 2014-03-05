/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.dao.redis

import com.junbo.oauth.common.JsonMarshaller
import com.junbo.oauth.db.dao.AppClientDAO
import com.junbo.oauth.db.entity.AppClientEntity
import groovy.transform.CompileStatic

/**
 * Javadoc.
 */
@CompileStatic
class RedisAppClientDAOImpl extends RedisBaseDAO implements AppClientDAO {

    @Override
    AppClientEntity save(AppClientEntity entity) {
        jedis.set(namespace + entity.clientId, JsonMarshaller.marshall(entity))
        return entity
    }

    @Override
    AppClientEntity get(String id) {
        String entityString = jedis.get(namespace + id)
        if (entityString != null) {
            return JsonMarshaller.unmarshall(AppClientEntity, entityString)
        }

        return null
    }

    @Override
    AppClientEntity update(AppClientEntity entity) {
        jedis.set(namespace + entity.clientId, JsonMarshaller.marshall(entity))
        return entity
    }

    @Override
    void delete(String id) {
        jedis.del(namespace + id)
    }
}
