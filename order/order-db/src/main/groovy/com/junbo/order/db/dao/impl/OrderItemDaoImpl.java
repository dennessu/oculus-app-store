/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.dao.impl;

import com.junbo.order.db.dao.OrderItemDao;
import com.junbo.order.db.entity.OrderItemEntity;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by linyi on 14-2-8.
 */
@Component("orderItemDao")
public class OrderItemDaoImpl extends BaseDaoImpl<OrderItemEntity> implements OrderItemDao {
    public List<OrderItemEntity> readByOrderId(final Long orderId) {
        Criteria criteria = this.getSession().createCriteria(OrderItemEntity.class);
        criteria.add(Restrictions.eq("orderId", orderId));
        return criteria.list();
    }
}
