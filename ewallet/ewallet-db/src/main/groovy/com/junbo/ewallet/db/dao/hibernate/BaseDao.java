/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.db.dao.hibernate;

import com.junbo.ewallet.db.entity.Entity;
import com.junbo.sharding.IdGenerator;
import com.junbo.sharding.ShardAlgorithm;
import com.junbo.sharding.hibernate.ShardScope;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Date;

/**
 * Base dao for Entity.
 *
 * @param <T> entity type.
 */
public class BaseDao<T extends Entity> {
    @Autowired
    @Qualifier("ewalletSessionFactory")
    private SessionFactory sessionFactory;
    @Autowired
    @Qualifier("oculus48IdGenerator")
    private IdGenerator idGenerator;
    @Autowired
    @Qualifier("userShardAlgorithm")
    private ShardAlgorithm shardAlgorithm;

    private Class<T> entityType;

    protected Session currentSession(Object key) {
        ShardScope shardScope = new ShardScope(shardAlgorithm.shardId(key));
        try {
            return sessionFactory.getCurrentSession();
        } finally {
            shardScope.close();
        }
    }

    public T get(Long id) {
        return (T) currentSession(id).get(entityType, id);
    }

    public T insert(T t) {
        Date now = new Date();
        t.setpId(generateId(t.getShardMasterId()));
        t.setCreatedBy(123L); //TODO
        t.setCreatedTime(now);
        t.setUpdatedBy(123L);   //TODO
        t.setUpdatedTime(now);
        return get((Long) currentSession(t.getShardMasterId()).save(t));
    }

    public T update(T t) {
        T existed = (T) currentSession(t.getShardMasterId()).get(entityType, t.getpId());
        t.setCreatedTime(existed.getCreatedTime());
        t.setCreatedBy(existed.getCreatedBy());
        Date now = new Date();
        t.setUpdatedBy(123L); //TODO
        t.setUpdatedTime(now);
        return (T) currentSession(t.getShardMasterId()).merge(t);
    }

    protected Long generateId(Long shardId) {
        return idGenerator.nextId(shardId);
    }

    public Class<T> getEntityType() {
        return entityType;
    }

    public void setEntityType(Class<T> entityType) {
        this.entityType = entityType;
    }
}
