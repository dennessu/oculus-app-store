/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

/**
 * Created by xmchen on 14-1-26.
 * @param <T> Entity type
 * @param <ID> ID type
 */
@SuppressWarnings("unchecked")
public abstract class BaseDaoImpl<T extends Serializable, ID extends Serializable> implements BaseDao<T, ID> {
    @Autowired
    private SessionFactory sessionFactory;

    private Class<T> entityType;

    protected Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    public ID insert(T entity) {
        return (ID) currentSession().save(entity);
    }

    public T get(ID id) {
        return (T) currentSession().get(entityType, id);
    }

    public T update(T t) {
        currentSession().update(t);
        return t;
    }

    public Boolean exists(ID id) {
        return get(id) != null;
    }

    public void flush() {
        currentSession().flush();
    }

    public Class<T> getEntityType() {
        return entityType;
    }

    public void setEntityType(Class<T> entityType) {
        this.entityType = entityType;
    }
}
