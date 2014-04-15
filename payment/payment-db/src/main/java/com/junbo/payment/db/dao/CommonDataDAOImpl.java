/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.dao;

import com.junbo.payment.db.entity.GenericEntity;
import com.junbo.sharding.IdGenerator;
import com.junbo.sharding.ShardAlgorithm;
import com.junbo.sharding.hibernate.ShardScope;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.Serializable;
import java.util.Date;

/**
 * common business data dao.
 * @param <T> the entity for this dao
 * @param <ID> the id for the entity
 */
public class CommonDataDAOImpl<T extends GenericEntity, ID extends Serializable> {
    @Autowired
    @Qualifier("userShardAlgorithm")
    private ShardAlgorithm shardAlgorithm;
    @Autowired
    @Qualifier("oculus48IdGenerator")
    protected IdGenerator idGenerator;
    private Class<T> persistentClass;
    @Autowired
    @Qualifier("paymentSessionFactory")
    protected SessionFactory sessionFactory;

    public CommonDataDAOImpl(Class<T> persistentClass) {
        this.persistentClass = persistentClass;
    }

    public Session currentSession(Object key) {
        ShardScope shardScope = new ShardScope(shardAlgorithm.shardId(key));
        try {
            return sessionFactory.getCurrentSession();
        } finally {
            shardScope.close();
        }
    }
    public T get(ID id) {
        return (T) currentSession(id).get(persistentClass, id);
    }

    public ID save(T entity) {
        if(entity.getId() == null){
            entity.setId(idGenerator.nextId(entity.getShardMasterId()));
        }
        if(entity.getCreatedTime() == null){
            entity.setCreatedTime(new Date());
        }
        if(entity.getUpdatedTime() == null){
            entity.setUpdatedTime(new Date());
        }
        return  (ID) currentSession(entity.getShardMasterId()).save(entity);
    }

    public T update(T entity) {
        if(entity.getUpdatedTime() == null){
            entity.setUpdatedTime(new Date());
        }
        T newt = (T) currentSession(entity.getShardMasterId()).merge(entity);
        //currentSession().update(newt);
        //currentSession().evict(newt);
        return newt;
    }

    public void delete(T entity) {
        currentSession(entity.getShardMasterId()).delete(entity);
    }

    public Class<T> getPersistentClass() {
        return persistentClass;
    }

    public void setPersistentClass(Class<T> persistentClass) {
        this.persistentClass = persistentClass;
    }
}
