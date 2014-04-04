/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.id.dao.impl

import com.junbo.sharding.ShardAlgorithm
import com.junbo.sharding.hibernate.ShardScope
import com.junbo.sharding.id.dao.IdGlobalCounterDAO
import com.junbo.sharding.id.model.IdGlobalCounterEntity
import groovy.transform.CompileStatic
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Required
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.CollectionUtils

/**
 * Created by liangfu on 3/5/14.
 */
@Component
@Transactional
@CompileStatic
class IdGlobalCounterDAOImpl implements IdGlobalCounterDAO {
    private SessionFactory sessionFactory
    protected ShardAlgorithm shardAlgorithm

    @Required
    void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory
    }

    @Required
    void setShardAlgorithm(ShardAlgorithm shardAlgorithm) {
        this.shardAlgorithm = shardAlgorithm
    }

    @Override
    IdGlobalCounterEntity get(Long optionMode, Long shardId) {
        Session session = ShardScope.with(shardAlgorithm.shardId()) { sessionFactory.currentSession }
        String query = 'select * from id_global_counter where shard_id = :shardId and option_mode = :optionMode'
        List<IdGlobalCounterEntity> entities = session.createSQLQuery(query)
                .addEntity(IdGlobalCounterEntity).setParameter('optionMode', optionMode)
                .setParameter('shardId', shardId).list()

        if (CollectionUtils.isEmpty(entities)) {
            return null
        }
        else if (entities.size() > 2) {
            throw new RuntimeException('Internal error for shard.')
        }
        else {
            return entities.get(0)
        }
    }

    @Override
    IdGlobalCounterEntity saveOrUpdate(IdGlobalCounterEntity entity) {
        IdGlobalCounterEntity entityInDB = get(entity.optionMode, entity.shardId)
        Session session = ShardScope.with(shardAlgorithm.shardId(entity.id)) { sessionFactory.currentSession }
        if (entityInDB == null) {
            session.save(entity)
        }
        else {
            if (entity.id != entity.id) {
                throw new RuntimeException('idGeneration error.')
            }
            update(entity)
        }

        return get(entity.optionMode, entity.shardId)
    }

    @Override
    IdGlobalCounterEntity update(IdGlobalCounterEntity entity) {
        Session session = ShardScope.with(shardAlgorithm.shardId(entity.id)) { sessionFactory.currentSession }
        session.merge(entity)
        return get(entity.optionMode, entity.shardId)
    }
}
