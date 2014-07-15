/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.dao.impl;

import com.junbo.order.db.dao.BaseEventDao;
import com.junbo.order.db.entity.CommonEventEntity;
import com.junbo.sharding.ShardAlgorithm;
import com.junbo.sharding.hibernate.ShardScope;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by linyi on 14-2-7.
 *
 * @param <T> the type of entity to work with
 */
@Repository("orderBaseEventDao")
public class BaseEventDaoImpl<T extends CommonEventEntity> implements BaseEventDao<T> {
    @Autowired
    @Qualifier("orderSessionFactory")
    private SessionFactory sessionFactory;

    @Autowired
    @Qualifier("userShardAlgorithm")
    private ShardAlgorithm shardAlgorithm;

    private Class<T> entityType;

    BaseEventDaoImpl() {
        this.entityType = null;
        Class c = getClass();
        Type t = c.getGenericSuperclass();
        if (t instanceof ParameterizedType) {
            Type[] p = ((ParameterizedType) t).getActualTypeArguments();
            this.entityType = (Class<T>) p[0];
        }
    }

    protected Session getSession(Object key) {
        ShardScope shardScope = new ShardScope(shardAlgorithm.dataCenterId(key), shardAlgorithm.shardId(key));
        try {
            return sessionFactory.getCurrentSession();
        } finally {
            shardScope.close();
        }
    }

    public Long create(T t) {
        Session session = this.getSession(t.getShardId());
        Long id = (Long) session.save(t);
        session.flush();
        return id;
    }

    public T read(long id) {
        return (T) this.getSession(id).get(entityType, id);
    }

    public void update(T t) {
        Session session = this.getSession(t.getShardId());
        session.merge(t);
        session.flush();
    }

    public Class<T> getEntityType() {
        return entityType;
    }

    public void setEntityType(Class<T> entityType) {
        this.entityType = entityType;
    }
}
