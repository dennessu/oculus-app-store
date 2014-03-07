/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.dao.redis

import com.junbo.oauth.common.JsonMarshaller
import com.junbo.oauth.db.dao.ClientDAO
import com.junbo.oauth.db.entity.ClientEntity
import groovy.transform.CompileStatic

/**
 * Javadoc.
 */
@CompileStatic
class RedisClientDAOImpl extends RedisBaseDAO implements ClientDAO {

    @Override
    ClientEntity save(ClientEntity entity) {
        jedis.set(namespace + entity.clientId, JsonMarshaller.marshall(entity))
        return entity
    }

    @Override
    ClientEntity get(String id) {
        String entityString = jedis.get(namespace + id)
        if (entityString != null) {
            return JsonMarshaller.unmarshall(ClientEntity, entityString)
        }

        return null
    }

    @Override
    ClientEntity update(ClientEntity entity) {
        jedis.set(namespace + entity.clientId, JsonMarshaller.marshall(entity))
        return entity
    }

    @Override
    void delete(String id) {
        jedis.del(namespace + id)
    }
}
