/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.db.dao.hibernate;

import com.junbo.common.id.WalletId;
import com.junbo.ewallet.db.entity.hibernate.EntityWithCreated;
import com.junbo.sharding.IdGeneratorFacade;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * Base Dao for transactionEntity.
 *
 * @param <T> Transaction entity type.
 */
public class TransactionBaseDao<T extends EntityWithCreated> {
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private IdGeneratorFacade idGenerator;

    private Class<T> entityType;

    protected Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    public T get(Long id) {
        return (T) currentSession().get(entityType, id);
    }

    public Long insert(T t) {
        Date now = new Date();
        t.setId(generateId(t.getShardMasterId()));
        t.setCreatedBy("DEFAULT"); //TODO
        t.setCreatedTime(now);
        return (Long) currentSession().save(t);
    }

    protected Long generateId(Long shardId) {
        return idGenerator.nextId(WalletId.class, shardId);
    }

    public Class<T> getEntityType() {
        return entityType;
    }

    public void setEntityType(Class<T> entityType) {
        this.entityType = entityType;
    }
}
