/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.dao.redis

import com.junbo.oauth.common.JsonMarshaller
import com.junbo.oauth.db.dao.AccessTokenDAO
import com.junbo.oauth.db.entity.AccessTokenEntity
import groovy.transform.CompileStatic

/**
 * Javadoc.
 */
@CompileStatic
class RedisAccessTokenDAOImpl extends RedisBaseDAO implements AccessTokenDAO {

    @Override
    AccessTokenEntity save(AccessTokenEntity entity) {
        jedis.set(namespace + entity.tokenValue, JsonMarshaller.marshall(entity))
        return entity
    }

    @Override
    AccessTokenEntity get(String id) {
        String entityString = jedis.get(namespace + id)
        if (entityString != null) {
            return JsonMarshaller.unmarshall(AccessTokenEntity, entityString)
        }

        return null
    }

    @Override
    AccessTokenEntity update(AccessTokenEntity entity) {
        jedis.set(namespace + entity.tokenValue, JsonMarshaller.marshall(entity))
        return entity
    }

    @Override
    void delete(String id) {
        jedis.del(namespace + id)
    }
}
