/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao.impl;

import com.junbo.catalog.common.util.Action;
import com.junbo.catalog.common.util.Constants;
import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.db.dao.BaseDao;
import com.junbo.catalog.db.entity.BaseEntity;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Base DAO implementation.
 * @param <T> entity to operate.
 */
public abstract class BaseDaoImpl<T extends BaseEntity> implements BaseDao<T> {
    @Autowired
    private SessionFactory sessionFactory;

    private Class<T> entityType;

    protected Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    public Long create(T entity) {
        // remove it later
        entity.setId(genSimpleId());

        entity.setTimestamp(Utils.currentTimestamp());
        entity.setCreatedTime(Utils.now());
        entity.setCreatedBy(Constants.SYSTEM_INTERNAL);
        entity.setUpdatedTime(Utils.now());
        entity.setUpdatedBy(Constants.SYSTEM_INTERNAL);

        return (Long) currentSession().save(entity);
    }

    public T get(Long id) {
        return (T) currentSession().get(entityType, id);
    }

    public Long update(T entity) {
        entity.setTimestamp(Utils.currentTimestamp());
        entity.setUpdatedTime(Utils.now());
        entity.setUpdatedBy(Constants.SYSTEM_INTERNAL);

        currentSession().update(entity);
        return entity.getId();
    }

    public Boolean exists(Long id) {
        return get(id) != null;
    }

    /*public void flush() {
        currentSession().flush();
    }*/

    protected T findBy(Action<Criteria> filter) {
        Criteria criteria = currentSession().createCriteria(entityType);
        filter.apply(criteria);

        return (T) criteria.uniqueResult();
    }

    protected List<T> findAllBy(Action<Criteria> filter) {
        Criteria criteria = currentSession().createCriteria(entityType);
        filter.apply(criteria);
        return criteria.list();
    }

    public Class<T> getEntityType() {
        return entityType;
    }

    public void setEntityType(Class<T> entityType) {
        this.entityType = entityType;
    }


    /**
     * Generate simple id.
     * @deprecated deprecated.
     * @return simple id
     */
    @Deprecated
    private long genSimpleId() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            //ignore
        }

        return System.currentTimeMillis();
    }
}
