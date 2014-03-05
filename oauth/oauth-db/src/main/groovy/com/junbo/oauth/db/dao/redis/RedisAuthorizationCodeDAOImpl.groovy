/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.dao.redis

import com.junbo.oauth.common.JsonMarshaller
import com.junbo.oauth.db.dao.AuthorizationCodeDAO
import com.junbo.oauth.db.entity.AuthorizationCodeEntity
import groovy.transform.CompileStatic

/**
 * Javadoc.
 */
@CompileStatic
class RedisAuthorizationCodeDAOImpl extends RedisBaseDAO implements AuthorizationCodeDAO {

    @Override
    AuthorizationCodeEntity save(AuthorizationCodeEntity entity) {
        jedis.set(namespace + entity.code, JsonMarshaller.marshall(entity))
        return entity
    }

    @Override
    AuthorizationCodeEntity get(String id) {
        String entityString = jedis.get(namespace + id)
        if (entityString != null) {
            return JsonMarshaller.unmarshall(AuthorizationCodeEntity, entityString)
        }

        return null
    }

    @Override
    AuthorizationCodeEntity update(AuthorizationCodeEntity entity) {
        jedis.set(namespace + entity.code, JsonMarshaller.marshall(entity))
        return entity
    }

    @Override
    void delete(String id) {
        jedis.del(namespace + id)
    }
}
