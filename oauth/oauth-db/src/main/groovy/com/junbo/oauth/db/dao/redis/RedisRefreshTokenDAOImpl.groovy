/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.dao.redis

import com.junbo.oauth.common.JsonMarshaller
import com.junbo.oauth.db.dao.RefreshTokenDAO
import com.junbo.oauth.db.entity.RefreshTokenEntity

/**
 * Javadoc.
 */
class RedisRefreshTokenDAOImpl extends RedisBaseDAO implements RefreshTokenDAO {

    @Override
    RefreshTokenEntity save(RefreshTokenEntity entity) {
        jedis.set(namespace + entity.tokenValue, JsonMarshaller.marshall(entity))
        return entity
    }

    @Override
    RefreshTokenEntity get(String id) {
        String entityString = jedis.get(namespace + id)
        if (entityString != null) {
            return JsonMarshaller.unmarshall(RefreshTokenEntity, entityString)
        }

        return null
    }

    @Override
    RefreshTokenEntity update(RefreshTokenEntity entity) {
        jedis.set(namespace + entity.tokenValue, JsonMarshaller.marshall(entity))
        return entity
    }

    @Override
    void delete(String id) {
        jedis.del(namespace + id)
    }
}
