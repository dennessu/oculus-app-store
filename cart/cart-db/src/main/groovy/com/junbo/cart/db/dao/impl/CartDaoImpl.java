/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.db.dao.impl;

import com.junbo.cart.db.dao.CartDao;
import com.junbo.cart.db.entity.CartEntity;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Created by fzhang@wan-san.com on 14-1-21.
 */
public class CartDaoImpl extends AbstractDao implements CartDao {

    @Override
    public void insert(CartEntity cartEntity) {
        getSession().save(cartEntity);
        getSession().flush();
    }

    @Override
    public CartEntity update(CartEntity cartEntity) {
        getSession().merge(cartEntity);
        getSession().flush();
        return get(cartEntity.getId());
    }

    @Override
    public CartEntity get(long id) {
        return (CartEntity) getSession().get(CartEntity.class, id);
    }

    @Override
    public CartEntity get(String clientId, String cartName, long userId) {
        Criteria criteria = getSession().createCriteria(CartEntity.class).
                add(Restrictions.eq("userId", userId)).
                add(Restrictions.eq("cartName", cartName)).
                add(Restrictions.eq("clientId", clientId));
        List<CartEntity> result = criteria.list();
        if (CollectionUtils.isEmpty(result)) {
            return null;
        }
        Assert.isTrue(result.size() == 1, "Only one entry should be found");
        return result.iterator().next();
    }
}
