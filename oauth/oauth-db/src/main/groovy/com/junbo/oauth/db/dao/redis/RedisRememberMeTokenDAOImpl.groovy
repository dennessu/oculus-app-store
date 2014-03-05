/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.dao.redis

import com.junbo.oauth.common.JsonMarshaller
import com.junbo.oauth.db.dao.RememberMeTokenDAO
import com.junbo.oauth.db.entity.RememberMeTokenEntity

/**
 * Javadoc.
 */
class RedisRememberMeTokenDAOImpl extends RedisBaseDAO implements RememberMeTokenDAO {

    @Override
    RememberMeTokenEntity save(RememberMeTokenEntity entity) {
        jedis.set(namespace + entity.tokenValue, JsonMarshaller.marshall(entity))
        return entity
    }

    @Override
    RememberMeTokenEntity get(String id) {
        String entityString = jedis.get(namespace + id)
        if (entityString != null) {
            return JsonMarshaller.unmarshall(RememberMeTokenEntity, entityString)
        }

        return null
    }

    @Override
    RememberMeTokenEntity update(RememberMeTokenEntity entity) {
        jedis.set(namespace + entity.tokenValue, JsonMarshaller.marshall(entity))
        return entity
    }

    @Override
    void delete(String id) {
        jedis.del(namespace + id)
    }
}
