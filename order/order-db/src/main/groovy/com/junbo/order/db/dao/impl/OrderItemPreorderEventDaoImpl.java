/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.dao.impl;

import com.junbo.order.db.dao.OrderItemPreorderEventDao;
import com.junbo.order.db.entity.OrderItemPreorderEventEntity;
import org.springframework.stereotype.Repository;

/**
 * Created by LinYi on 2/8/14.
 */
@Repository("orderItemPreorderEventDao")
public class OrderItemPreorderEventDaoImpl extends BaseDaoImpl<OrderItemPreorderEventEntity>
        implements OrderItemPreorderEventDao {
}
