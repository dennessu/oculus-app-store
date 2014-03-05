/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.dao;

import com.junbo.payment.db.entity.GenericEntity;

import java.io.Serializable;
import java.util.Date;

/**
 * common business data dao.
 * @param <T> the entity for this dao
 * @param <ID> the id for the entity
 */
public class CommonDataDAOImpl<T extends GenericEntity, ID extends Serializable> extends GenericDAOImpl<T, ID> {
    public CommonDataDAOImpl(Class<T> persistentClass) {
        super(persistentClass);
    }
    @Override
    public ID save(T entity) {
        if(entity.getId() == null){
            entity.setId(generateId());
        }
        if(entity.getCreatedTime() == null){
            entity.setCreatedTime(new Date());
        }
        if(entity.getUpdatedTime() == null){
            entity.setUpdatedTime(new Date());
        }
        return  (ID) currentSession().save(entity);
    }

    @Override
    public T update(T entity) {
        T newt = (T) currentSession().merge(entity);
        if(newt.getUpdatedTime() == null){
            newt.setUpdatedTime(new Date());
        }
        currentSession().update(newt);
        return newt;
    }

    protected long generateId() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            //ignore
        }

        return System.currentTimeMillis();
    }
}
