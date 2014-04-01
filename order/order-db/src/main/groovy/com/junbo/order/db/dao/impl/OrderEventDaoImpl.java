/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.dao.impl;

import com.junbo.order.db.dao.OrderEventDao;
import com.junbo.order.db.entity.OrderEventEntity;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Created by LinYi on 2/8/14.
 */
@Repository("orderEventDao")
public class OrderEventDaoImpl extends BaseDaoImpl<OrderEventEntity> implements OrderEventDao {

    @SuppressWarnings("unchecked")
    public List<OrderEventEntity> readByOrderId(final Long orderId, Integer start, Integer count) {
        Criteria criteria = this.getSession().createCriteria(OrderEventEntity.class);
        criteria.add(Restrictions.eq("orderId", orderId));

        if (start != null) {
            criteria.setFirstResult(start);
        }
        if (count != null) {
            criteria.setMaxResults(count);
        }

        criteria.addOrder(Order.desc("eventId"));
        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    public List<OrderEventEntity> readByTrackingUuid(final UUID trackingUuid) {
        Criteria criteria = this.getSession().createCriteria(OrderEventEntity.class);
        criteria.add(Restrictions.eq("trackingUuid", trackingUuid));
        return criteria.list();
    }
}
