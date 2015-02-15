/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.dao.impl;

import com.junbo.order.db.dao.BaseDao;
import com.junbo.order.db.entity.CommonDbEntityDeletable;
import com.junbo.order.db.entity.CommonDbEntityWithDate;
import com.junbo.sharding.ShardAlgorithm;
import com.junbo.sharding.hibernate.ShardScope;
import com.junbo.sharding.view.ViewQueryFactory;
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
@Repository("orderBaseDao")
public class BaseDaoImpl<T extends CommonDbEntityWithDate> implements BaseDao<T> {
    @Autowired
    @Qualifier("orderSessionFactory")
    private SessionFactory sessionFactory;

    @Autowired
    @Qualifier("userShardAlgorithm")
    private ShardAlgorithm shardAlgorithm;

    @Autowired
    @Qualifier("orderViewQueryFactory")
    protected ViewQueryFactory viewQueryFactory;

    private Class<T> entityType;

    BaseDaoImpl() {
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

    protected Session getSessionByShardId(int dataCenterId, int shardId) {
        ShardScope shardScope = new ShardScope(dataCenterId, shardId);
        try {
            return sessionFactory.getCurrentSession();
        } finally {
            shardScope.close();
        }
    }

    public Long create(T t) {
        if(t instanceof CommonDbEntityDeletable) {
            ((CommonDbEntityDeletable) t).setDeleted(false);
        }
        Session session = this.getSession(t.getShardId());
        Long id = (Long) session.save(t);
        session.flush();
        return id;
    }

    public T read(Long id) {
        return (T) this.getSession(id).get(entityType, id);
    }

    public void update(T t) {
        Session session = this.getSession(t.getShardId());
        session.merge(t);
        session.flush();
    }

    public void markDelete(Long id) {
        if (!CommonDbEntityDeletable.class.isAssignableFrom(entityType)) {
            throw new UnsupportedOperationException(
                    String.format("name=MarkDelete_Not_Supported, type=%s", entityType.getCanonicalName()));
        }
        CommonDbEntityDeletable entity = (CommonDbEntityDeletable) read(id);
        if(entity != null && !Boolean.TRUE.equals(entity.isDeleted())) {
            entity.setDeleted(true);
        }
    }
}
