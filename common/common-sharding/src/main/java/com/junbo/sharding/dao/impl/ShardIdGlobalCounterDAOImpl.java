/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.dao.impl;

import com.junbo.sharding.dao.ShardIdGlobalCounterDAO;
import com.junbo.sharding.model.ShardIdGlobalCounterEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Created by liangfu on 3/5/14.
 */
@Component
public class ShardIdGlobalCounterDAOImpl implements ShardIdGlobalCounterDAO {
    @Autowired
    private SessionFactory sessionFactory;

    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public ShardIdGlobalCounterEntity get(Long optionMode, Long shardId) {
        String query = "select * from shard_id_global_counter where shard_id = :shardId and option_mode = :optionMode";
        List<ShardIdGlobalCounterEntity> entities = currentSession().createSQLQuery(query)
                .addEntity(ShardIdGlobalCounterEntity.class).setParameter("optionMode", optionMode)
                .setParameter("shardId", shardId).list();

        if(CollectionUtils.isEmpty(entities)) {
            return null;
        }
        else if(entities.size() > 2) {
            throw new RuntimeException("Internal error for shard.");
        }
        else {
            return entities.get(0);
        }
    }

    @Override
    public ShardIdGlobalCounterEntity saveOrUpdate(ShardIdGlobalCounterEntity entity) {
        ShardIdGlobalCounterEntity entityInDB = get(entity.getOptionMode(), entity.getShardId());
        if(entityInDB == null) {
            currentSession().save(entity);
        }
        else {
            if(!entity.getId().equals(entity.getId())) {
                throw new RuntimeException("idGeneration error.");
            }
            update(entity);
        }

        return get(entity.getOptionMode(), entity.getShardId());
    }

    @Override
    public ShardIdGlobalCounterEntity update(ShardIdGlobalCounterEntity entity) {
        currentSession().update(entity);
        return get(entity.getOptionMode(), entity.getShardId());
    }
}
