/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.dao.impl;

import com.junbo.order.db.dao.OrderItemPreorderInfoDao;
import com.junbo.order.db.entity.OrderItemPreorderInfoEntity;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by linyi on 14-2-8.
 */
@Repository("orderItemPreorderInfoDao")
public class OrderItemPreorderInfoDaoImpl
        extends BaseDaoImpl<OrderItemPreorderInfoEntity> implements OrderItemPreorderInfoDao {
    @Override
    public List<OrderItemPreorderInfoEntity> readByOrderItemId(final Long orderItemId) {
        Criteria criteria = this.getSession(orderItemId).createCriteria(OrderItemPreorderInfoEntity.class);
        criteria.add(Restrictions.eq("orderItemId", orderItemId));
        return criteria.list();
    }
}
