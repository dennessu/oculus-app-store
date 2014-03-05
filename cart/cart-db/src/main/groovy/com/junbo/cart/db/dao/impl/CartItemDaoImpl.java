/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.db.dao.impl;

import com.junbo.cart.db.dao.CartItemDao;
import com.junbo.cart.db.entity.CartItemEntity;
import com.junbo.cart.db.entity.ItemStatus;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Created by fzhang@wan-san.com on 14-1-22.
 * @param <T> the type of cart item entity
 */
public class CartItemDaoImpl<T extends CartItemEntity> extends AbstractDao implements CartItemDao<T>, InitializingBean {

    private Class entityClass;

    @Override
    public void insert(T item) {
        if(item != null) {
            Assert.isTrue(item.getClass() == entityClass);
        }
        getSession().save(item);
    }

    @Override
    public void update(T item) {
        if(item != null) {
            Assert.isTrue(item.getClass() == entityClass);
        }
        getSession().merge(item);
    }

    @Override
    public T get(long id) {
        return (T) getSession().get(entityClass, id);
    }

    @Override
    public List<T> getItems(long cartId, ItemStatus itemStatus) {
        Criteria criteria = getSession().createCriteria(entityClass).
                add(Restrictions.eq("cartId", cartId));
        if (itemStatus != null) {
            criteria.add(Restrictions.eq("status", itemStatus));
        }
        return criteria.list();
    }

    @Override
    public Class getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(entityClass, "entityClass should not be null");
    }
}
