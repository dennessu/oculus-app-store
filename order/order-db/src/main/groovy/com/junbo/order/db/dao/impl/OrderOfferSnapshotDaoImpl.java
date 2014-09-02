/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.dao.impl;

import com.junbo.order.db.dao.OrderOfferSnapshotDao;
import com.junbo.order.db.entity.OrderOfferSnapshotEntity;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by LinYi on 2014/8/28.
 */
@Repository("orderOfferSnapshotDao")
public class OrderOfferSnapshotDaoImpl extends BaseDaoImpl<OrderOfferSnapshotEntity> implements OrderOfferSnapshotDao {
    @Override
    public List<OrderOfferSnapshotEntity> readByOrderId(final Long orderId) {
        Criteria criteria = this.getSession(orderId).createCriteria(OrderOfferSnapshotEntity.class);
        criteria.add(Restrictions.eq("orderId", orderId));
        criteria.addOrder(Order.desc("createdTime"));
        return criteria.list();
    }
}
