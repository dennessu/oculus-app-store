/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.dao.impl;

import com.junbo.order.db.dao.OrderDao;
import com.junbo.order.db.entity.OrderEntity;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by linyi on 14-2-7.
 */
@Repository("orderDao")
public class OrderDaoImpl extends BaseDaoImpl<OrderEntity> implements OrderDao {

    @SuppressWarnings("unchecked")
    public List<OrderEntity> readByUserId(final Long userId, Boolean tentative,
                                          Integer start, Integer count) {
        Criteria criteria = this.getSession(userId).createCriteria(OrderEntity.class);

        criteria.add(Restrictions.eq("userId", userId));
        if (tentative != null) {
            criteria.add(Restrictions.eq("tentative", tentative));
        }

        if (start != null) {
            criteria.setFirstResult(start);
        }
        if (count != null) {
            criteria.setMaxResults(count);
        }

        criteria.addOrder(Order.desc("orderId"));
        return criteria.list();
    }
}
