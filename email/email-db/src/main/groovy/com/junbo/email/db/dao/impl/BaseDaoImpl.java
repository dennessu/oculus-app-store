/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.db.dao.impl;

import com.junbo.email.db.dao.BaseDao;
import com.junbo.email.db.entity.BaseEntity;
import com.junbo.sharding.ShardAlgorithm;
import com.junbo.sharding.hibernate.ShardScope;
import groovy.lang.Closure;
import org.codehaus.groovy.runtime.MethodClosure;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.Date;

/**
 * Impl of BaseDao.
 * @param <T> Entity Type
 */
public abstract class BaseDaoImpl<T extends BaseEntity> implements BaseDao<T> {

    private SessionFactory sessionFactory;

    private Class<T> entityType;

    private ShardAlgorithm shardAlgorithm;

    protected Session currentSession(Object id) {
        Closure mc = new MethodClosure(this,"getCurrentSession");
        return (Session) ShardScope.with(shardAlgorithm.shardId(id), mc);
    }
    protected Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Class<T> getEntityType() {
        return entityType;
    }

    public void setEntityType(Class<T> entityType) {
        this.entityType = entityType;
    }

    public ShardAlgorithm getShardAlgorithm() {
        return shardAlgorithm;
    }

    public void setShardAlgorithm(ShardAlgorithm shardAlgorithm) {
        this.shardAlgorithm = shardAlgorithm;
    }


    public Long save(T entity) {
        return (Long)currentSession(entity.getId()).save(entity);
    }

    public T get(Long id) {
        return (T) currentSession(id).get(entityType, id);
    }

    public Long update(T entity) {
        T merge = (T) currentSession(entity.getId()).merge(entity);
        if (merge.getUpdatedTime() == null) {
            merge.setUpdatedTime(new Date());
        }
        currentSession(entity.getId()).update(merge);
        currentSession(entity.getId()).flush();
        return entity.getId();
    }

    public void delete(T entity) {
        currentSession(entity.getId()).delete(entity);
    }

    public void flush(Long id) {
        currentSession(id).flush();
    }
}
