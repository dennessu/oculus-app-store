/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.dao.memcached

import com.junbo.oauth.common.JsonMarshaller
import com.junbo.oauth.db.dao.FlowStateDAO
import com.junbo.oauth.db.entity.FlowStateEntity
import groovy.transform.CompileStatic

/**
 * Javadoc.
 */
@CompileStatic
class MemcachedFlowStateDAOImpl extends MemcachedBaseDAO implements FlowStateDAO {
    @Override
    FlowStateEntity save(FlowStateEntity entity) {
        memcachedClient.add(namespace + entity.id, (int) (entity.expiredBy.time / MILLION),
                JsonMarshaller.marshall(entity))
        return entity
    }

    @Override
    FlowStateEntity get(String id) {
        Object entityString = memcachedClient.get(namespace + id)
        if (entityString == null) {
            return null
        }

        return JsonMarshaller.unmarshall(FlowStateEntity, entityString.toString())
    }

    @Override
    FlowStateEntity update(FlowStateEntity entity) {
        memcachedClient.set(namespace + entity.id, (int) (entity.expiredBy.time / MILLION),
                JsonMarshaller.marshall(entity))
        return entity
    }

    @Override
    void delete(String id) {
        memcachedClient.delete(namespace + id)
    }
}
