/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.db.dao;

import java.io.Serializable;
import java.util.List;

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
    @Qualifier("tokenSessionFactory")
    private SessionFactory sessionFactory;
    private Class<T> persistentClass;

    public GenericDAOImpl(Class<T> persistentClass) {
        this.persistentClass = persistentClass;
    }

    protected Session currentSession() {
        return sessionFactory.getCurrentSession();
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
