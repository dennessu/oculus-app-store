/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.db.dao;

import com.junbo.sharding.ShardAlgorithm;
import com.junbo.sharding.hibernate.ShardScope;
import com.junbo.token.db.entity.GenericEntity;
import com.junbo.sharding.IdGenerator;
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
    @Qualifier("tokenSessionFactory")
    protected SessionFactory sessionFactory;

    public CommonDataDAOImpl(Class<T> persistentClass) {
        this.persistentClass = persistentClass;
    }

    public Session currentSession(Object key) {
        //TODO: hashValue is not a partionable-id, use partition 0 first
        ShardScope shardScope = new ShardScope(0);
        //ShardScope shardScope = new ShardScope(shardAlgorithm.shardId(key));
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
            //TODO: hashValue is not a partionable-id, so cannot calculate right partition later
            entity.setId(idGenerator.nextId(entity.getShardMasterId()));
        }
        entity.setCreatedTime(new Date());
        if (entity.getCreatedBy() == null) {
            entity.setCreatedBy("0");
        }
        return  (ID) currentSession(entity.getId()).save(entity);
    }

    public T update(T entity) {
        entity.setUpdatedTime(new Date());
        if (entity.getUpdatedBy() == null) {
            entity.setUpdatedBy("0");
        }
        T newt = (T) currentSession(entity.getShardMasterId()).merge(entity);
        return newt;
    }

    public void delete(ID id) {
        throw new UnsupportedOperationException("Delete action not support on token class: " + persistentClass.getCanonicalName());
    }

    public Class<T> getPersistentClass() {
        return persistentClass;
    }

    public void setPersistentClass(Class<T> persistentClass) {
        this.persistentClass = persistentClass;
    }
}
