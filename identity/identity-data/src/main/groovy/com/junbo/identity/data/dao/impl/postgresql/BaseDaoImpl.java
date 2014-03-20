/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.identity.data.dao.BaseDao;
import com.junbo.sharding.core.hibernate.SessionFactoryWrapper;
import com.junbo.sharding.util.Helper;
import org.hibernate.Session;

import java.io.Serializable;

/**
 * Created by haomin on 14-3-20.
 *
 * @param <T> Entity type
 * @param <ID> ID type
 */
@SuppressWarnings("unchecked")
public abstract class BaseDaoImpl<T, ID extends Serializable> implements BaseDao<T, ID> {
    private SessionFactoryWrapper sessionFactoryWrapper;

    public void setSessionFactoryWrapper(SessionFactoryWrapper sessionFactoryWrapper) {
        this.sessionFactoryWrapper = sessionFactoryWrapper;
    }

    protected Session currentSession() {
        return sessionFactoryWrapper.resolve(Helper.getCurrentThreadLocalShardId()).getCurrentSession();
    }

    private Class<T> entityType;

    public ID save(T entity) {
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
