/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.db.dao.hibernate;

import com.junbo.ewallet.db.entity.Entity;
import com.junbo.sharding.IdGenerator;
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

    private Class<T> entityType;

    protected Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    public T get(Long id) {
        return (T) currentSession().get(entityType, id);
    }

    public T insert(T t) {
        Date now = new Date();
        t.setId(generateId(t.getShardMasterId()));
        t.setCreatedBy("DEFAULT"); //TODO
        t.setCreatedTime(now);
        t.setModifiedBy("DEFAULT");   //TODO
        t.setModifiedTime(now);
        return get((Long) currentSession().save(t));
    }

    public T update(T t) {
        T existed = (T) currentSession().load(entityType, t.getId());
        t.setCreatedTime(existed.getCreatedTime());
        t.setCreatedBy(existed.getCreatedBy());
        Date now = new Date();
        t.setModifiedBy("DEFAULT"); //TODO
        t.setModifiedTime(now);
        return (T) currentSession().merge(t);
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
