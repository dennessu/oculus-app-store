/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.dao.impl;

import com.junbo.order.db.dao.OrderItemFulfillmentEventDao;
import com.junbo.order.db.entity.OrderItemFulfillmentEventEntity;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by LinYi on 2/8/14.
 */
@Repository("orderItemFulfillmentDao")
public class OrderItemFulfillmentEventDaoImpl extends BaseDaoImpl<OrderItemFulfillmentEventEntity>
        implements OrderItemFulfillmentEventDao {
    @Override
    public List<OrderItemFulfillmentEventEntity> readByOrderItemId(Long orderId, Long orderItemId) {
        Criteria criteria = this.getSession().createCriteria(OrderItemFulfillmentEventEntity.class);
        criteria.add(Restrictions.eq("orderId", orderId));
        criteria.add(Restrictions.eq("orderItemId", orderItemId));
        return criteria.list();
    }
}
