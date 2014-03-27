/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.db.dao.impl;

import com.junbo.email.common.util.Action;
import com.junbo.email.db.dao.BaseDao;
import com.junbo.email.db.entity.BaseEntity;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.Date;
import java.util.List;

/**
 * Impl of BaseDao.
 * @param <T> Entity Type
 */
public abstract class BaseDaoImpl<T extends BaseEntity> implements BaseDao<T> {

    private SessionFactory sessionFactory;

    private Class<T> entityType;

    protected Session currentSession() {
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

    public Long save(T entity) {
        return (Long)currentSession().save(entity);
    }

    public T get(Long id) {
        return (T) currentSession().get(entityType, id);
    }

    public Long update(T entity) {
        T merge = (T) currentSession().merge(entity);
        if (merge.getUpdatedTime() == null) {
            merge.setUpdatedTime(new Date());
        }
        currentSession().update(merge);
        currentSession().flush();
        return entity.getId();
    }

    public void delete(T entity) {
        currentSession().delete(entity);
    }

    public void flush() {
        currentSession().flush();
    }

    protected  T findBy(Action<Criteria> filter) {
        Criteria criteria = currentSession().createCriteria(entityType);
        filter.apply(criteria);

        return (T) criteria.uniqueResult();
    }

    protected List<T> findAllBy(Action<Criteria> filter) {
        Criteria criteria = currentSession().createCriteria(entityType);
        filter.apply(criteria);

        return criteria.list();
    }

}
