/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.drm.db.dao;

import com.junbo.drm.db.Entity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * drm.
 * @param <T> entity
 */
public class BaseDao<T extends Entity> {
    private SessionFactory sessionFactory;

    private Class<T> entityType;

    protected Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    public Long insert(T t) {
        return (Long) currentSession().save(t);
    }

    public T get(Long id) {
        return (T) currentSession().get(entityType, id);
    }

    public Long update(T t) {
        currentSession().update(t);
        return t.getId();
    }

    public Class<T> getEntityType() {
        return entityType;
    }

    public void setEntityType(Class<T> entityType) {
        this.entityType = entityType;
    }
}
