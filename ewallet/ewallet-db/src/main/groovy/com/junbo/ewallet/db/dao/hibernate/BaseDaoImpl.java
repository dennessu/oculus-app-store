/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.db.dao.hibernate;

import com.junbo.ewallet.db.dao.BaseDao;
import com.junbo.ewallet.db.entity.BaseEntity;
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
public class BaseDaoImpl<T extends BaseEntity> implements BaseDao<T> {
    @Autowired
    @Qualifier("ewalletSessionFactory")
    private SessionFactory sessionFactory;
    @Autowired
    @Qualifier("userShardAlgorithm")
    private ShardAlgorithm shardAlgorithm;
    @Autowired
    @Qualifier("oculus48IdGenerator")
    protected IdGenerator idGenerator;

    private Class<T> entityType;

    protected Session currentSession(Object key) {
        ShardScope shardScope = new ShardScope(shardAlgorithm.dataCenterId(key), shardAlgorithm.shardId(key));
        try {
            return sessionFactory.getCurrentSession();
        } finally {
            shardScope.close();
        }
    }

    @Override
    public T get(Long id) {
        return (T) currentSession(id).get(entityType, id);
    }

    @Override
    public Long insert(T t) {
        if(t.getId() == null){
            t.setId(idGenerator.nextId(t.getShardMasterId()));
        }
        t.setCreatedTime(new Date());
        if (t.getCreatedBy() == null) {
            t.setCreatedBy("0");
        }
        Session session = currentSession(t.getShardMasterId());
        Long id = (Long)session.save(t);
        session.flush();
        return id;
    }

    @Override
    public void update(T t) {
        t.setUpdatedTime(new Date());
        if (t.getUpdatedBy() == null) {
            t.setUpdatedBy("0");
        }
        Session session = currentSession(t.getShardMasterId());
        session.merge(t);
        session.flush();
    }

    @Override
    public void delete(Long id) {
        throw new UnsupportedOperationException("Delete action not support on " + entityType.getCanonicalName());
    }

    public Class<T> getEntityType() {
        return entityType;
    }

    public void setEntityType(Class<T> entityType) {
        this.entityType = entityType;
    }
}
