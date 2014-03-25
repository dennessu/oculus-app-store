/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.dao.impl;

import com.junbo.order.db.dao.OrderEventDao;
import com.junbo.order.db.entity.OrderEventEntity;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Created by LinYi on 2/8/14.
 */
@Repository("orderEventDao")
public class OrderEventDaoImpl extends BaseDaoImpl<OrderEventEntity> implements OrderEventDao {
    public List<OrderEventEntity> readByOrderId(final Long orderId) {
        Criteria criteria = this.getSession().createCriteria(OrderEventEntity.class);
        criteria.add(Restrictions.eq("orderId", orderId));
        return criteria.list();
    }
    public List<OrderEventEntity> readByTrackingUuid(final UUID trackingUuid) {
        Criteria criteria = this.getSession().createCriteria(OrderEventEntity.class);
        criteria.add(Restrictions.eq("trackingUuid", trackingUuid));
        return criteria.list();
    }
}
