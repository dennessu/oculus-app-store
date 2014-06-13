/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.db.dao.impl;

import com.junbo.order.db.dao.OrderRevisionDao;
import com.junbo.order.db.entity.OrderRevisionEntity;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by chriszhu on 6/11/14.
 */
@Repository("orderRevisionDao")
public class OrderRevisionDaoImpl extends BaseDaoImpl<OrderRevisionEntity> implements OrderRevisionDao {
    @Override
    public List<OrderRevisionEntity> readByOrderId(Long orderId) {
        Criteria criteria = this.getSession(orderId).createCriteria(OrderRevisionEntity.class);
        criteria.add(Restrictions.eq("orderId", orderId));
        return criteria.list();
    }
}
