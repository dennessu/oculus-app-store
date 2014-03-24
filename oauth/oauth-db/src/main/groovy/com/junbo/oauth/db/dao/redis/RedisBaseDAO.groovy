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

import java.lang.reflect.ParameterizedType

/**
 * Javadoc.
 */
@CompileStatic
abstract class RedisBaseDAO<T extends BaseEntity> implements BaseDAO<T, String> {
    protected final Class<T> entityClass
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

    protected RedisBaseDAO() {
        entityClass = (Class<T>) ((ParameterizedType) getClass().genericSuperclass).actualTypeArguments[0]
    }

    @Override
    T save(T entity) {
        jedis.set(namespace + entity.id, JsonMarshaller.marshall(entity))
        return entity
    }

    @Override
    T get(String id) {
        String entityString = jedis.get(namespace + id)
        if (entityString != null) {
            return (T) JsonMarshaller.unmarshall(entityString, entityClass)
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
