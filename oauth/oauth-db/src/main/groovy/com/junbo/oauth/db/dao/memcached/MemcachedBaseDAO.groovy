/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.dao.memcached

import com.junbo.oauth.common.JsonMarshaller
import com.junbo.oauth.db.dao.BaseDAO
import com.junbo.oauth.db.entity.BaseEntity
import groovy.transform.CompileStatic
import net.spy.memcached.MemcachedClientIF
import org.springframework.beans.factory.annotation.Required

/**
 * Javadoc.
 */
@CompileStatic
abstract class MemcachedBaseDAO<T extends BaseEntity> implements BaseDAO<T, String> {
    protected final static Long MILLION = 1000000L
    protected MemcachedClientIF memcachedClient
    protected String namespace

    @Required
    void setMemcachedClient(MemcachedClientIF memcachedClient) {
        this.memcachedClient = memcachedClient
    }

    @Required
    void setNamespace(String namespace) {
        this.namespace = namespace
    }

    @Override
    T save(T entity) {
        memcachedClient.add(namespace + entity.id, (int) (entity.expiredBy.time / MILLION),
                JsonMarshaller.marshall(entity))
        return entity
    }

    protected T internalGet(String id, Class<T> clazz) {
        Object entityString = memcachedClient.get(namespace + id)
        if (entityString == null) {
            return null
        }

        return (T) JsonMarshaller.unmarshall(clazz, entityString.toString())
    }

    @Override
    T update(T entity) {
        memcachedClient.set(namespace + entity.id, (int) (entity.expiredBy.time / MILLION),
                JsonMarshaller.marshall(entity))
        return entity
    }

    @Override
    void delete(T entity) {
        memcachedClient.delete(namespace + entity.id)
    }
}
