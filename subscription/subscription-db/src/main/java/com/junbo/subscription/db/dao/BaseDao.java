/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.db.dao;

import com.junbo.sharding.IdGenerator;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * base dao.
 * @param <T> the entity for this dao
 */
public class BaseDao<T extends com.junbo.subscription.db.entity.Entity> {
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private IdGenerator idGenerator;

    private Class<T> classType;

    protected Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    public Long insert(T t) {
        t.setId(generateId(t.getShardMasterId()));
        return (Long) currentSession().save(t);
    }

    public T get(Long id) {
        return (T) currentSession().get(classType, id);
    }

    public Long update(T t) {
        currentSession().update(t);
        return t.getId();
    }

    public void delete(T entity) {
        currentSession().delete(entity);
    }

    public Class<T> getClassType() {
        return classType;
    }

    public void setClassType(Class<T> classType) {
        this.classType = classType;
    }

    protected Long generateId(Long shardId){
        return idGenerator.nextId(shardId);
    }
}
