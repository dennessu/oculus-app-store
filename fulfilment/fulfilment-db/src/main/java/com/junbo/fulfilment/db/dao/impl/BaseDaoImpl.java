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
import com.junbo.sharding.ShardAlgorithm;
import com.junbo.sharding.hibernate.ShardScope;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

/**
 * BaseDaoImpl.
 *
 * @param <T> entity type
 */
public abstract class BaseDaoImpl<T extends BaseEntity> implements BaseDao<T> {
    @Autowired
    @Qualifier("userShardAlgorithm")
    private ShardAlgorithm shardAlgorithm;

    @Autowired
    @Qualifier("fulfilmentSessionFactory")
    private SessionFactory sessionFactory;

    @Autowired
    @Qualifier("oculus48IdGenerator")
    private IdGenerator idGenerator;

    private Class<T> entityType;

    public Session currentSession(Object key) {
        ShardScope shardScope = new ShardScope(shardAlgorithm.dataCenterId(key), shardAlgorithm.shardId(key));
        try {
            return sessionFactory.getCurrentSession();
        } finally {
            shardScope.close();
        }
    }

    public Long create(T entity) {
        entity.setId(idGenerator.nextId(entity.getShardMasterId()));

        entity.setCreatedTime(Utils.now());
        entity.setCreatedBy(Constant.SYSTEM_INTERNAL);

        Session session = currentSession(entity.getShardMasterId());
        Long id = (Long) session.save(entity);
        session.flush();
        return id;
    }

    public T get(Long id) {
        return (T) currentSession(id).get(entityType, id);
    }

    public Long update(T entity) {
        entity.setUpdatedTime(Utils.now());
        entity.setUpdatedBy(Constant.SYSTEM_INTERNAL);

        Session session = currentSession(entity.getShardMasterId());
        session.update(entity);
        session.flush();
        return entity.getId();
    }

    public Boolean exists(Long id) {
        return get(id) != null;
    }

    protected T findBy(Long shardKey, Action<Criteria> filter) {
        Criteria criteria = currentSession(shardKey).createCriteria(entityType);
        filter.apply(criteria);

        return (T) criteria.uniqueResult();
    }

    protected List<T> findAllBy(Long shardKey, Action<Criteria> filter) {
        Criteria criteria = currentSession(shardKey).createCriteria(entityType);
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
