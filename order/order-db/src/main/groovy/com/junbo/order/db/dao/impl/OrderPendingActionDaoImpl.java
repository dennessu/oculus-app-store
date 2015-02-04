/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.db.dao.impl;

import com.junbo.order.db.dao.OrderPendingActionDao;
import com.junbo.order.db.entity.OrderPendingActionEntity;
import com.junbo.order.spec.model.enums.OrderPendingActionType;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by fzhang on 2015/2/2.
 */
@Repository("orderPendingActionDao")
public class OrderPendingActionDaoImpl extends BaseDaoImpl<OrderPendingActionEntity> implements OrderPendingActionDao {

    @Override
    @SuppressWarnings("unchecked")
    public List<OrderPendingActionEntity> list(int dataCenterId, int shardId, OrderPendingActionType actionType, Date startTime, Date endTime, int start, int count) {
        Session session = this.getSessionByShardId(dataCenterId, shardId);
        Criteria criteria = session.createCriteria(OrderPendingActionEntity.class);

        criteria.add(Restrictions.eq("completed", false));
        criteria.add(Restrictions.eq("actionType", actionType));
        criteria.add(Restrictions.lt("createdTime", endTime));
        if (startTime != null) {
            criteria.add(Restrictions.ge("createdTime", startTime));
        }
        criteria.addOrder(Order.asc("createdTime"));

        criteria.setFirstResult(start);
        criteria.setMaxResults(count);
        return criteria.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<OrderPendingActionEntity> getByOrderId(long orderId, OrderPendingActionType actionType) {
        Criteria criteria = this.getSession(orderId).createCriteria(OrderPendingActionEntity.class);

        criteria.add(Restrictions.eq("actionType", actionType));
        criteria.add(Restrictions.eq("orderId", orderId));
        criteria.add(Restrictions.eq("completed", false));

        return criteria.list();
    }
}
