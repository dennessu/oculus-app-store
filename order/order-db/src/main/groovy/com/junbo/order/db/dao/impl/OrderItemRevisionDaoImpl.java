/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.db.dao.impl;

import com.junbo.order.db.dao.OrderItemRevisionDao;
import com.junbo.order.db.entity.OrderItemRevisionEntity;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by chriszhu on 6/11/14.
 */
@Repository("orderItemRevisionDao")
public class OrderItemRevisionDaoImpl extends BaseDaoImpl<OrderItemRevisionEntity> implements OrderItemRevisionDao {

    @Override
    public List<OrderItemRevisionEntity> readByOrderId(Long orderId) {
        Criteria criteria = this.getSession(orderId).createCriteria(OrderItemRevisionEntity.class);
        criteria.add(Restrictions.eq("orderId", orderId));
        return criteria.list();
    }

    @Override
    public List<OrderItemRevisionEntity> readByOrderItemId(Long orderId) {
        Criteria criteria = this.getSession(orderId).createCriteria(OrderItemRevisionEntity.class);
        criteria.add(Restrictions.eq("orderItemId", orderId));
        return criteria.list();
    }
}
