/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.dao.impl;

import com.junbo.billing.db.BaseDaoImpl;
import com.junbo.billing.db.entity.ShippingAddressEntity;
import com.junbo.billing.db.dao.ShippingAddressEntityDao;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Created by xmchen on 14-1-21.
 */
@SuppressWarnings("unchecked")
public class ShippingAddressEntityDaoImpl extends BaseDaoImpl<ShippingAddressEntity, Long>
        implements ShippingAddressEntityDao {

    @Override
    public List<ShippingAddressEntity> findByUserId(Long userId) {

        Criteria criteria = currentSession().createCriteria(ShippingAddressEntity.class).
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
