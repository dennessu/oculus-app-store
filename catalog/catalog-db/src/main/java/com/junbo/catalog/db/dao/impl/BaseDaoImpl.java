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
import com.junbo.common.id.Id;
import com.junbo.sharding.IdGenerator;
import com.junbo.sharding.ShardAlgorithm;
import com.junbo.sharding.hibernate.ShardScope;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Base DAO implementation.
 * @param <T> entity to operate.
 */
public abstract class BaseDaoImpl<T extends BaseEntity> implements BaseDao<T> {
    @Autowired
    @Qualifier("userShardAlgorithm")
    private ShardAlgorithm shardAlgorithm;

    @Autowired
    @Qualifier("catalogSessionFactory")
    private SessionFactory sessionFactory;

    private Class<T> entityType;

    private IdGenerator idGenerator;

    protected Session currentSession() {
        ShardScope shardScope = new ShardScope(shardAlgorithm.shardId(0));
        try {
            return sessionFactory.getCurrentSession();
        } finally {
            shardScope.close();
        }
    }

    public Long create(T entity) {
        entity.setId(idGenerator.nextId());
        entity.setCreatedTime(Utils.now());
        entity.setCreatedBy(Constants.SYSTEM_INTERNAL);
        entity.setUpdatedTime(Utils.now());
        entity.setUpdatedBy(Constants.SYSTEM_INTERNAL);
        entity.setRev(1);

        return (Long) currentSession().save(entity);
    }

    public T get(Long id) {
        T entity = (T) currentSession().get(entityType, id);
        return (entity == null || entity.isDeleted()) ? null : entity;
    }

    public Long update(T entity) {
        entity.setUpdatedTime(Utils.now());
        entity.setUpdatedBy(Constants.SYSTEM_INTERNAL);
        entity.setRev(entity.getRev()==null ? 1 : entity.getRev() + 1);
        currentSession().update(entity);
        return entity.getId();
    }

    public Boolean exists(Long id) {
        return get(id) != null;
    }

    protected T findBy(Action<Criteria> filter) {
        Criteria criteria = currentSession().createCriteria(entityType);
        filter.apply(criteria);
        criteria.add(Restrictions.ne("deleted", Boolean.TRUE));
        return (T) criteria.uniqueResult();
    }

    protected List<T> findAllBy(Action<Criteria> filter) {
        Criteria criteria = currentSession().createCriteria(entityType);
        filter.apply(criteria);
        criteria.add(Restrictions.ne("deleted", Boolean.TRUE));
        return criteria.list();
    }

    protected <E extends Id> void addIdRestriction(String fieldName, Collection<E> ids, Criteria criteria) {
        if (!CollectionUtils.isEmpty(ids)) {
            List<Long> idValues = new ArrayList<>();
            for (E id : ids) {
                idValues.add(id.getValue());
            }
            criteria.add(Restrictions.in(fieldName, idValues));
        }
    }

    public Class<T> getEntityType() {
        return entityType;
    }

    public void setEntityType(Class<T> entityType) {
        this.entityType = entityType;
    }

    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }
}
