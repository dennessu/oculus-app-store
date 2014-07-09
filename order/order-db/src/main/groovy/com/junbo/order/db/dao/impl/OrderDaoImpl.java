/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.dao.impl;

import com.junbo.order.db.dao.OrderDao;
import com.junbo.order.db.entity.OrderEntity;
import com.junbo.order.spec.model.enums.OrderStatus;
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

    @Override
    @SuppressWarnings("unchecked")
    public List<OrderEntity> readByUserId(final Long userId, Boolean tentative,
                                          Integer start, Integer count) {
        Criteria criteria = this.getSession(userId).createCriteria(OrderEntity.class);

        criteria.add(Restrictions.eq("userId", userId));
        if (tentative != null) {
            criteria.add(Restrictions.eq("tentative", tentative));
        }

        pageCriteria(criteria, start, count);
        criteria.addOrder(Order.desc("orderId"));
        return criteria.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<OrderEntity> readByStatus(Integer dataCenterId, Integer shardId, List<OrderStatus> statusList, boolean updatedByAscending,
                                          Integer start, Integer count) {
        Criteria criteria = this.getSessionByShardId(dataCenterId, shardId).createCriteria(OrderEntity.class);

        criteria.add(Restrictions.in("orderStatusId", statusList));
        pageCriteria(criteria, start, count);

        if (updatedByAscending) {
            criteria.addOrder(Order.asc("updatedTime"));
        } else {
            criteria.addOrder(Order.desc("updatedTime"));
        }
        return criteria.list();
    }

    private void pageCriteria(Criteria criteria, Integer start, Integer count) {
        if (start != null) {
            criteria.setFirstResult(start);
        }
        if (count != null) {
            criteria.setMaxResults(count);
        }
    }
}
