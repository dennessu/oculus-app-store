/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.dao.impl;

import com.junbo.order.db.dao.OrderBillingHistoryDao;
import com.junbo.order.db.entity.OrderBillingHistoryEntity;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by LinYi on 2/8/14.
 */
@Repository("orderBillingHistoryDao")
public class OrderBillingHistoryDaoImpl extends BaseDaoImpl<OrderBillingHistoryEntity> implements OrderBillingHistoryDao {
    public List<OrderBillingHistoryEntity> readByOrderId(final Long orderId) {
        Criteria criteria = this.getSession(orderId).createCriteria(OrderBillingHistoryEntity.class);
        criteria.add(Restrictions.eq("orderId", orderId));
        criteria.addOrder(Order.desc("createdTime"));
        return criteria.list();
    }
}
