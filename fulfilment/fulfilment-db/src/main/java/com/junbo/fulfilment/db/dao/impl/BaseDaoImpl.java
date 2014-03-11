/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.db.dao.impl;

import com.junbo.fulfilment.common.util.Action;
import com.junbo.fulfilment.common.util.Constant;
import com.junbo.fulfilment.common.util.Utils;
import com.junbo.fulfilment.db.dao.BaseDao;
import com.junbo.fulfilment.db.entity.BaseEntity;
import com.junbo.sharding.IdGenerator;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * BaseDaoImpl.
 *
 * @param <T> entity type
 */
public abstract class BaseDaoImpl<T extends BaseEntity> implements BaseDao<T> {
    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private IdGenerator idGenerator;

    private Class<T> entityType;

    protected Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    public Long create(T entity) {
        entity.setId(idGenerator.nextId(entity.getShardMasterId()));

        entity.setCreatedTime(Utils.now());
        entity.setCreatedBy(Constant.SYSTEM_INTERNAL);

        return (Long) currentSession().save(entity);
    }

    public T get(Long id) {
        return (T) currentSession().get(entityType, id);
    }

    public Long update(T entity) {
        entity.setUpdatedTime(Utils.now());
        entity.setUpdatedBy(Constant.SYSTEM_INTERNAL);

        currentSession().update(entity);
        return entity.getId();
    }

    public Boolean exists(Long id) {
        return get(id) != null;
    }

    public void flush() {
        currentSession().flush();
    }

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
}
