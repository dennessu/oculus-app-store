/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.dao.impl;

import com.junbo.order.db.dao.OrderPaymentInfoDao;
import com.junbo.order.db.entity.OrderPaymentInfoEntity;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by linyi on 14-2-8.
 */
@Repository("orderPaymentInfoDao")
public class OrderPaymentInfoDaoImpl extends BaseDaoImpl<OrderPaymentInfoEntity> implements OrderPaymentInfoDao {
    @Override
    public List<OrderPaymentInfoEntity> readByOrderId(Long orderId) {
        Criteria criteria = this.getSession().createCriteria(OrderPaymentInfoEntity.class);
        criteria.add(Restrictions.eq("orderId", orderId));
        criteria.add(Restrictions.eq("deleted", false));
        return criteria.list();
    }
}
