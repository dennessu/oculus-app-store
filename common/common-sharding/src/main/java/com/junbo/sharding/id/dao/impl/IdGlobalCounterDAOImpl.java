/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.id.dao.impl;

import com.junbo.sharding.id.dao.IdGlobalCounterDAO;
import com.junbo.sharding.id.model.IdGlobalCounterEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Created by liangfu on 3/5/14.
 */
@Component
@Transactional("shardingTransactionManager")
public class IdGlobalCounterDAOImpl implements IdGlobalCounterDAO {
    private SessionFactory sessionFactory;

    @Required
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public IdGlobalCounterEntity get(Long optionMode, Long shardId) {
        String query = "select * from id_global_counter where shard_id = :shardId and option_mode = :optionMode";
        List<IdGlobalCounterEntity> entities = currentSession().createSQLQuery(query)
                .addEntity(IdGlobalCounterEntity.class).setParameter("optionMode", optionMode)
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
    public IdGlobalCounterEntity saveOrUpdate(IdGlobalCounterEntity entity) {
        IdGlobalCounterEntity entityInDB = get(entity.getOptionMode(), entity.getShardId());
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
    public IdGlobalCounterEntity update(IdGlobalCounterEntity entity) {
        currentSession().merge(entity);
        return get(entity.getOptionMode(), entity.getShardId());
    }
}
