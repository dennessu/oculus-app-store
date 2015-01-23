/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.dao;

import com.junbo.configuration.topo.DataCenters;
import com.junbo.sharding.ShardAlgorithm;
import com.junbo.sharding.hibernate.ShardScope;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * domain data dao, reads goes to shard 0 of local region, writes to shard 0 of all regions.
 * @param <T> the entity for this dao
 * @param <ID> the id for the entity
 */
@SuppressWarnings("unchecked")
public class DomainDataDAOImpl<T, ID extends Serializable> {
    @Autowired
    @Qualifier("userShardAlgorithm")
    private ShardAlgorithm shardAlgorithm;
    @Autowired
    @Qualifier("paymentSessionFactory")
    protected SessionFactory sessionFactory;
    private Class<T> persistentClass;

    public DomainDataDAOImpl(Class<T> persistentClass) {
        this.persistentClass = persistentClass;
    }

    public Session currentSession() {
        ShardScope shardScope = new ShardScope(DataCenters.instance().currentDataCenterId(), 0);
        try {
            return sessionFactory.getCurrentSession();
        } finally {
            shardScope.close();
        }
    }

    public List<Session> allSessions() {
        List<Session> allSessions = new ArrayList<>();
        for (Integer dcid : DataCenters.instance().getDataCenterIds()) {
            // shard id is always 0.
            ShardScope shardScope = new ShardScope(dcid, 0);
            try {
                allSessions.add(sessionFactory.getCurrentSession());
            } finally {
                shardScope.close();
            }
        }
        return allSessions;
    }

    public T get(ID id) {
        return (T) currentSession().get(persistentClass, id);
    }

    public void save(T entity) {
        for (Session session : allSessions()) {
            session.save(entity);
            session.flush();
        }
    }

    public void update(T entity) {
        for (Session session : allSessions()) {
            session.update(entity);
            session.flush();
        }
    }

    public void delete(T entity) {
        for (Session session : allSessions()) {
            session.delete(entity);
            session.flush();
        }
    }

    @SuppressWarnings("unchecked")
    public List<T> getAll() {
        return currentSession().createQuery("from " + persistentClass.getSimpleName()).list() ;
    }

    public Class<T> getPersistentClass() {
        return persistentClass;
    }

    public void setPersistentClass(Class<T> persistentClass) {
        this.persistentClass = persistentClass;
    }

}
