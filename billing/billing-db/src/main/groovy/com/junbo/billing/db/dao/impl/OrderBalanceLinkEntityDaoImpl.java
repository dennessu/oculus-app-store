/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.dao.impl;

import com.junbo.billing.db.BaseDaoImpl;
import com.junbo.billing.db.balance.OrderBalanceLinkEntity;
import com.junbo.billing.db.dao.OrderBalanceLinkEntityDao;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Created by xmchen on 14-1-21.
 */
@SuppressWarnings("unchecked")
public class OrderBalanceLinkEntityDaoImpl extends BaseDaoImpl<OrderBalanceLinkEntity, Long>
        implements OrderBalanceLinkEntityDao {
    public List<OrderBalanceLinkEntity> findByOrderId(Long orderId) {
        Criteria criteria = currentSession().createCriteria(OrderBalanceLinkEntity.class).
                add(Restrictions.eq("orderId", orderId));
        return criteria.list();
    }
}
