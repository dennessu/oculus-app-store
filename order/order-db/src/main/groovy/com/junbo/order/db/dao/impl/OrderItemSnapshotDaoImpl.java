/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.dao.impl;

import com.junbo.order.db.dao.OrderItemSnapshotDao;
import com.junbo.order.db.entity.OrderOfferItemSnapshotEntity;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by LinYi on 2014/8/28.
 */
@Repository("orderItemSnapshotDao")
public class OrderItemSnapshotDaoImpl extends BaseDaoImpl<OrderOfferItemSnapshotEntity> implements OrderItemSnapshotDao {
    @Override
    public List<OrderOfferItemSnapshotEntity> readByOfferSnapshotId(Long offerSnapshotId) {
        Criteria criteria = this.getSession(offerSnapshotId).createCriteria(OrderOfferItemSnapshotEntity.class);
        criteria.add(Restrictions.eq("offerSnapshotId", offerSnapshotId));
        criteria.addOrder(Order.desc("createdTime"));
        return criteria.list();
    }
}
