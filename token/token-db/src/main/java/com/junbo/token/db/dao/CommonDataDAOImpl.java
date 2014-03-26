/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.db.dao;

import com.junbo.token.db.entity.GenericEntity;
import com.junbo.sharding.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.Serializable;
import java.util.Date;

/**
 * common business data dao.
 * @param <T> the entity for this dao
 * @param <ID> the id for the entity
 */
public class CommonDataDAOImpl<T extends GenericEntity, ID extends Serializable> extends GenericDAOImpl<T, ID> {
    @Autowired
    @Qualifier("oculus48IdGenerator")
    protected IdGenerator idGenerator;

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
        if(entity.getUpdatedTime() == null){
            entity.setUpdatedTime(new Date());
        }
        T newt = (T) currentSession().merge(entity);
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
