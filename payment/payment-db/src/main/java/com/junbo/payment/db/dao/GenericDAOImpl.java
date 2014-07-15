/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.dao;

import java.io.Serializable;
import java.util.List;

import com.junbo.sharding.ShardAlgorithm;
import com.junbo.sharding.hibernate.ShardScope;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * generic dao.
 * @param <T> the entity for this dao
 * @param <ID> the id for the entity
 */
@SuppressWarnings("unchecked")
public class GenericDAOImpl<T, ID extends Serializable> {
    @Autowired
    @Qualifier("userShardAlgorithm")
    private ShardAlgorithm shardAlgorithm;
    @Autowired
    @Qualifier("paymentSessionFactory")
    protected SessionFactory sessionFactory;
    private Class<T> persistentClass;

    public GenericDAOImpl(Class<T> persistentClass) {
        this.persistentClass = persistentClass;
    }

    //TODO: move all the configuration data to config DB and remove the generic DAO here
    private Session currentSession() {
        ShardScope shardScope = new ShardScope(0, 0);
        try {
            return sessionFactory.getCurrentSession();
        } finally {
            shardScope.close();
        }
    }

    public T get(ID id) {
        return (T) currentSession().get(persistentClass, id);
    }

    public ID save(T entity) {
        return  (ID) currentSession().save(entity);
    }

    public T update(T entity) {
        currentSession().update(entity);
        return entity;
    }

    public T saveOrUpdate(T entity) {
        currentSession().saveOrUpdate(entity);
        return entity;
    }

    public void delete(T entity) {
        currentSession().delete(entity);
    }

    public Boolean exists(ID id) {
        return get(id) != null;
    }

    public void flush() {
        currentSession().flush();
    }

    public void merge(T entity){
        currentSession().merge(entity);
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
