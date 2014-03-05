/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.db.dao.hibernate;

import com.junbo.ewallet.db.entity.hibernate.Entity;
import com.junbo.sharding.IdGenerator;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * Base dao for Entity.
 * @param <T> entity type.
 */
public class BaseDao<T extends Entity> {
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private IdGenerator idGenerator;

    private Class<T> entityType;

    protected Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    public T get(Long id) {
        return (T) currentSession().get(entityType, id);
    }

    public Long insert(T t) {
        Date now = new Date();
        t.setId(generateId(t.getShardId()));
        t.setCreatedBy("DEFAULT"); //TODO
        t.setCreatedTime(now);
        t.setModifiedBy("DEFAULT");   //TODO
        t.setModifiedTime(now);
        return (Long) currentSession().save(t);
    }

    public Long update(T t) {
        Date now = new Date();
        T newt = (T) currentSession().merge(t);
        newt.setModifiedBy("DEFAULT"); //TODO
        newt.setModifiedTime(now);
        currentSession().update(newt);
        return newt.getId();
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
