/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.db.dao;

import com.junbo.sharding.IdGenerator;
import com.junbo.sharding.ShardAlgorithm;
import com.junbo.sharding.hibernate.ShardScope;
import com.junbo.sharding.view.ViewQueryFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Date;


/**
 * base dao.
 *
 * @param <T> the entity for this dao
 */
public class BaseDao<T extends com.junbo.subscription.db.entity.Entity> {
    @Autowired
    @Qualifier("userShardAlgorithm")
    private ShardAlgorithm shardAlgorithm;

    @Autowired
    @Qualifier("subscriptionSessionFactory")
    private SessionFactory sessionFactory;

    @Autowired
    @Qualifier("oculus48IdGenerator")
    private IdGenerator idGenerator;

    @Autowired
    @Qualifier("subscriptionViewQueryFactory")
    protected ViewQueryFactory viewQueryFactory;

    private Class<T> classType;

    public Session currentSession(Object key) {
        ShardScope shardScope = new ShardScope(shardAlgorithm.dataCenterId(key), shardAlgorithm.shardId(key));
        try {
            return sessionFactory.getCurrentSession();
        } finally {
            shardScope.close();
        }
    }

    public Long insert(T t) {
        t.setId(generateId(t.getShardMasterId()));
        if(t.getCreatedTime() == null){
            t.setCreatedTime(new Date());
            t.setCreatedBy("INTERNAL");
        }
        Session session = currentSession(t.getShardMasterId());
        Long id = (Long)session.save(t);
        session.flush();
        return id;
    }

    public T get(Long id) {
        return (T) currentSession(id).get(classType, id);
    }

    public Long update(T t) {
        T existed = (T) currentSession(t.getShardMasterId()).get(classType, t.getId());
        t.setCreatedBy(existed.getCreatedBy());
        t.setCreatedTime(existed.getCreatedTime());
        if(t.getModifiedTime() == null){
            t.setModifiedTime(new Date());
            t.setModifiedBy("INTERNAL");
        }
        Session session = currentSession(t.getShardMasterId());
        session.merge(t);
        session.flush();
        return t.getId();
    }

    public void delete(T t) {
        Session session = currentSession(t.getShardMasterId());
        session.delete(t);
        session.flush();
    }

    public Class<T> getClassType() {
        return classType;
    }

    public void setClassType(Class<T> classType) {
        this.classType = classType;
    }

    protected Long generateId(Long shardId) {
        Long id = 0L;
        try {
            id =  idGenerator.nextId(shardId);
        }
        catch (Exception e){
            throw e;
        }
        return id;
    }
}
