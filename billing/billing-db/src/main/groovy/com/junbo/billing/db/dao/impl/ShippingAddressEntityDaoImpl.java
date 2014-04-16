/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.dao.impl;

import com.junbo.billing.db.BaseDao;
import com.junbo.billing.db.entity.ShippingAddressEntity;
import com.junbo.billing.db.dao.ShippingAddressEntityDao;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Created by xmchen on 14-1-21.
 */
@SuppressWarnings("unchecked")
public class ShippingAddressEntityDaoImpl extends BaseDao implements ShippingAddressEntityDao {
    @Override
    public ShippingAddressEntity get(Long shippingAddressId) {
        return (ShippingAddressEntity)currentSession(shippingAddressId).get(
                ShippingAddressEntity.class, shippingAddressId);
    }

    @Override
    public ShippingAddressEntity save(ShippingAddressEntity shippingAddress) {

        shippingAddress.setAddressId(idGenerator.nextId(shippingAddress.getUserId()));

        Session session = currentSession(shippingAddress.getAddressId());
        session.save(shippingAddress);
        session.flush();
        return get(shippingAddress.getAddressId());
    }

    @Override
    public ShippingAddressEntity update(ShippingAddressEntity shippingAddress) {

        Session session = currentSession(shippingAddress.getAddressId());
        session.merge(shippingAddress);
        session.flush();

        return get(shippingAddress.getAddressId());
    }

    @Override
    public List<ShippingAddressEntity> findByUserId(Long userId) {

        Criteria criteria = currentSession(userId).createCriteria(ShippingAddressEntity.class).
                add(Restrictions.eq("userId", userId));
        criteria.add(Restrictions.eq("deleted", Boolean.FALSE));
        return criteria.list();
    }

    @Override
    public void softDelete(Long addressId) {
        ShippingAddressEntity entity = this.get(addressId);
        entity.setDeleted(Boolean.TRUE);
        this.update(entity);
    }
}
