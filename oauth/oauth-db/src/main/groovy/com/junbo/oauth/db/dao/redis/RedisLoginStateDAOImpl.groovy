/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.dao.redis

import com.junbo.oauth.common.JsonMarshaller
import com.junbo.oauth.db.dao.LoginStateDAO
import com.junbo.oauth.db.entity.LoginStateEntity
import groovy.transform.CompileStatic

/**
 * Javadoc.
 */
@CompileStatic
class RedisLoginStateDAOImpl extends RedisBaseDAO implements LoginStateDAO {

    @Override
    LoginStateEntity save(LoginStateEntity entity) {
        jedis.set(namespace + entity.id, JsonMarshaller.marshall(entity))
        return entity
    }

    @Override
    LoginStateEntity get(String id) {
        String entityString = jedis.get(namespace + id)
        if (entityString != null) {
            return JsonMarshaller.unmarshall(LoginStateEntity, entityString)
        }

        return null
    }

    @Override
    LoginStateEntity update(LoginStateEntity entity) {
        jedis.set(namespace + entity.id, JsonMarshaller.marshall(entity))
        return entity
    }

    @Override
    void delete(String id) {
        jedis.del(namespace + id)
    }
}
