/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.db.dao.impl
import com.junbo.cart.db.dao.CartItemDao
import com.junbo.cart.db.entity.CartItemEntity
import com.junbo.cart.db.entity.ItemStatus
import groovy.transform.CompileStatic
import org.hibernate.Criteria
import org.hibernate.criterion.Restrictions
import org.springframework.beans.factory.InitializingBean
/**
 * Created by fzhang@wan-san.com on 14-1-22.
 * @param <T> the type of cart item entity
 */
@CompileStatic
class CartItemDaoImpl<T extends CartItemEntity> extends AbstractDao implements CartItemDao<T>, InitializingBean {

    private Class entityClass

    @Override
    void insert(T item) {
        if (item != null) {
            assert item.getClass() == entityClass
        }
        getSession(item.cartItemId).save(item)
    }

    @Override
    void update(T item) {
        if (item != null) {
            assert item.getClass() == entityClass
        }
        getSession(item.cartItemId).merge(item)
    }

    @Override
    boolean markDelete(long id, Date time) {
        T entity = get(id)
        if (entity != null && entity.status != ItemStatus.DELETED) {
            entity.updatedTime = time
            entity.status = ItemStatus.DELETED
            update(entity)
            return true
        }
        return false
    }

    @Override
    T get(long id) {
        return (T) getSession(id).get(entityClass, id)
    }

    @Override
    List<T> getItems(long cartId, ItemStatus itemStatus) {
        Criteria criteria = getSession(cartId).createCriteria(entityClass).
                add(Restrictions.eq('cartId', cartId))
        if (itemStatus != null) {
            criteria.add(Restrictions.eq('status', itemStatus))
        }
        return criteria.list()
    }

    @Override
    Class getEntityClass() {
        return entityClass
    }

    void setEntityClass(Class entityClass) {
        this.entityClass = entityClass
    }

    @Override
    void afterPropertiesSet() throws Exception {
        assert entityClass != null, 'entityClass should not be null'
    }
}
