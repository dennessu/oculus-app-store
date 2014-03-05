/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.dao.redis

import com.junbo.oauth.common.JsonMarshaller
import com.junbo.oauth.db.dao.FlowStateDAO
import com.junbo.oauth.db.entity.FlowStateEntity
import groovy.transform.CompileStatic

/**
 * Javadoc.
 */
@CompileStatic
class RedisFlowStateDAOImpl extends RedisBaseDAO implements FlowStateDAO {

    @Override
    FlowStateEntity save(FlowStateEntity entity) {
        jedis.set(namespace + entity.id, JsonMarshaller.marshall(entity))
        return entity
    }

    @Override
    FlowStateEntity get(String id) {
        String entityString = jedis.get(namespace + id)
        if (entityString != null) {
            return JsonMarshaller.unmarshall(FlowStateEntity, entityString)
        }

        return null
    }

    @Override
    FlowStateEntity update(FlowStateEntity entity) {
        jedis.set(namespace + entity.id, JsonMarshaller.marshall(entity))
        return entity
    }

    @Override
    void delete(String id) {
        jedis.del(namespace + id)
    }
}
