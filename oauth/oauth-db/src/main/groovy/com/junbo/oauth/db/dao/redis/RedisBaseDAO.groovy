/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.dao.redis

import com.junbo.oauth.common.JsonMarshaller
import com.junbo.oauth.db.dao.BaseDAO
import com.junbo.oauth.db.entity.BaseEntity
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import redis.clients.jedis.Jedis

/**
 * Javadoc.
 */
@CompileStatic
abstract class RedisBaseDAO<T extends BaseEntity> implements BaseDAO<T, String> {
    protected Jedis jedis
    protected String namespace

    @Required
    void setJedis(Jedis jedis) {
        this.jedis = jedis
    }

    @Required
    void setNamespace(String namespace) {
        this.namespace = namespace
    }

    @Override
    T save(T entity) {
        jedis.set(namespace + entity.id, JsonMarshaller.marshall(entity))
        return entity
    }

    protected T internalGet(String id, Class<T> clazz) {
        String entityString = jedis.get(namespace + id)
        if (entityString != null) {
            return (T) JsonMarshaller.unmarshall(clazz, entityString)
        }

        return null
    }

    @Override
    T update(T entity) {
        jedis.set(namespace + entity.id, JsonMarshaller.marshall(entity))
        return entity
    }

    @Override
    void delete(T entity) {
        jedis.del(namespace + entity.id)
    }
}
