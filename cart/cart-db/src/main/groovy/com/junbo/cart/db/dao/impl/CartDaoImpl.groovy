/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.db.dao.impl
import com.junbo.cart.db.dao.CartDao
import com.junbo.cart.db.entity.CartEntity
import groovy.transform.CompileStatic
import org.hibernate.criterion.Restrictions
import org.springframework.util.CollectionUtils
/**
 * Created by fzhang@wan-san.com on 14-1-21.
 */
@CompileStatic
class CartDaoImpl extends AbstractDao implements CartDao {

    @Override
    void insert(CartEntity cartEntity) {
        def session = getSession(cartEntity.id)
        session.save(cartEntity)
        session.flush()
    }

    @Override
    CartEntity update(CartEntity cartEntity) {
        def session = getSession(cartEntity.id)
        session.merge(cartEntity)
        session.flush()
        return get(cartEntity.id)
    }

    @Override
    CartEntity get(long id) {
        return (CartEntity) getSession(id).get(CartEntity, id)
    }

    @Override
    CartEntity get(String clientId, String cartName, long userId) {
        def criteria = getSession(userId).createCriteria(CartEntity).
                add(Restrictions.eq('userId', userId)).
                add(Restrictions.eq('cartName', cartName)).
                add(Restrictions.eq('clientId', clientId))

        List<CartEntity> result = criteria.list()
        if (CollectionUtils.isEmpty(result)) {
            return null
        }

        assert result.size() == 1, 'Only one entry should be found'
        return result.iterator().next()
    }
}
