/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.dao.impl;

import com.junbo.order.db.dao.OrderItemFulfillmentHistoryDao;
import com.junbo.order.db.entity.OrderItemFulfillmentHistoryEntity;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by LinYi on 2/8/14.
 */
@Repository("orderItemFulfillmentDao")
public class OrderItemFulfillmentHistoryDaoImpl extends BaseDaoImpl<OrderItemFulfillmentHistoryEntity>
        implements OrderItemFulfillmentHistoryDao {
    public List<OrderItemFulfillmentHistoryEntity> readByOrderItemId(final Long orderItemId) {
        Criteria criteria = this.getSession(orderItemId).createCriteria(OrderItemFulfillmentHistoryEntity.class);
        criteria.add(Restrictions.eq("orderItemId", orderItemId));
        criteria.addOrder(Order.desc("createdTime"));
        return criteria.list();
    }
}
