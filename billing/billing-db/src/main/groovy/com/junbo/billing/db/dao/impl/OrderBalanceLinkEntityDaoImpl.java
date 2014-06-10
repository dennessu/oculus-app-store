/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.dao.impl;

import com.junbo.billing.db.BaseDao;
import com.junbo.billing.db.entity.OrderBalanceLinkEntity;
import com.junbo.billing.db.dao.OrderBalanceLinkEntityDao;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Created by xmchen on 14-1-21.
 */
@SuppressWarnings("unchecked")
public class OrderBalanceLinkEntityDaoImpl extends BaseDao implements OrderBalanceLinkEntityDao {
    @Override
    public OrderBalanceLinkEntity get(Long orderBalanceLinkId) {
        return (OrderBalanceLinkEntity)currentSession(orderBalanceLinkId).get(
                OrderBalanceLinkEntity.class, orderBalanceLinkId);
    }

    @Override
    public OrderBalanceLinkEntity save(OrderBalanceLinkEntity orderBalanceLink) {
        orderBalanceLink.setLinkId(idGenerator.nextId(orderBalanceLink.getBalanceId()));

        Session session = currentSession(orderBalanceLink.getLinkId());
        session.save(orderBalanceLink);
        session.flush();
        return get(orderBalanceLink.getLinkId());
    }

    @Override
    public OrderBalanceLinkEntity update(OrderBalanceLinkEntity orderBalanceLink) {

        Session session = currentSession(orderBalanceLink.getLinkId());
        session.merge(orderBalanceLink);
        session.flush();

        return get(orderBalanceLink.getLinkId());
    }

    public List<OrderBalanceLinkEntity> findByOrderId(Long orderId) {
        Criteria criteria = currentSession(orderId).createCriteria(OrderBalanceLinkEntity.class).
                add(Restrictions.eq("orderId", orderId));
        return criteria.list();
    }

    public List<OrderBalanceLinkEntity> findByBalanceId(Long balanceId) {
        Criteria criteria = currentSession(balanceId).createCriteria(OrderBalanceLinkEntity.class).
                add(Restrictions.eq("balanceId", balanceId));
        return criteria.list();
    }
}
