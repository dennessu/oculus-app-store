/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.dao.impl;

import com.junbo.order.db.dao.BaseDao;
import com.junbo.order.db.entity.CommonDbEntityDeletable;
import com.junbo.order.db.entity.CommonDbEntityWithDate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;

/**
 * Created by linyi on 14-2-7.
 *
 * @param <T> the type of entity to work with
 */
@Repository("baseDao")
public class BaseDaoImpl<T extends CommonDbEntityWithDate> implements BaseDao<T> {
    @Autowired
    @Qualifier("orderSessionFactory")
    private SessionFactory sessionFactory;

    private Class<T> entityType;

    BaseDaoImpl() {
        this.entityType = null;
        Class c = getClass();
        Type t = c.getGenericSuperclass();
        if (t instanceof ParameterizedType) {
            Type[] p = ((ParameterizedType) t).getActualTypeArguments();
            this.entityType = (Class<T>) p[0];
        }
    }

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public Long create(T t) {
        // TODO Honor passed-in clientId
        t.setUpdatedBy("dev");
        t.setCreatedBy("dev");
        Date now = new Date();
        t.setCreatedTime(now);
        t.setUpdatedTime(now);
        //t.setRev(0);
        if(t instanceof CommonDbEntityDeletable) {
            ((CommonDbEntityDeletable) t).setDeleted(false);
        }
        return (Long) this.getSession().save(t);
    }

    public T read(long id) {
        return (T) this.getSession().get(entityType, id);
    }

    public void update(T t) {
        t.setUpdatedBy("dev");
        Date now = new Date();
        t.setUpdatedTime(now);
        this.getSession().merge(t);
    }

    public void flush() {
        this.getSession().flush();
    }

    public void markDelete(long id) {
        if (!CommonDbEntityDeletable.class.isAssignableFrom(entityType)) {
            throw new UnsupportedOperationException(
                    String.format("name=MarkDelete_Not_Supported, type=%s", entityType.getCanonicalName()));
        }
        CommonDbEntityDeletable entity = (CommonDbEntityDeletable) read(id);
        if(entity != null && !entity.isDeleted()) {
            entity.setDeleted(true);
        }
    }

    public Class<T> getEntityType() {
        return entityType;
    }

    public void setEntityType(Class<T> entityType) {
        this.entityType = entityType;
    }
}
