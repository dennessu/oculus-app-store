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
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Date;


/**
 * base dao.
 *
 * @param <T> the entity for this dao
 */
public class BaseDao<T extends com.junbo.subscription.db.entity.Entity> {
    @Autowired
    @Qualifier("subscriptionSessionFactory")
    private SessionFactory sessionFactory;

    @Autowired
    @Qualifier("oculus48IdGenerator")
    private IdGenerator idGenerator;

    private Class<T> classType;

    protected Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    public Long insert(T t) {
        t.setId(generateId(t.getShardMasterId()));
        if(t.getCreatedTime() == null){
            t.setCreatedTime(new Date());
            t.setCreatedBy("INTERNAL");
        }
        return (Long) currentSession().save(t);
    }

    public T get(Long id) {
        return (T) currentSession().get(classType, id);
    }

    public Long update(T t) {
        currentSession().update(t);
        if(t.getModifiedTime() == null){
            t.setModifiedTime(new Date());
            t.setModifiedBy("INTERNAL");
        }
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

    protected Long generateId(Long shardId) {
        return idGenerator.nextId(shardId);
    }
}
