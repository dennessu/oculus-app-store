/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.dao.impl;

import com.junbo.order.db.dao.OrderBillingEventDao;
import com.junbo.order.db.entity.OrderBillingEventEntity;
import org.springframework.stereotype.Repository;

/**
 * Created by LinYi on 2/8/14.
 */
@Repository("orderBillingEventDao")
public class OrderBillingEventDaoImpl extends BaseDaoImpl<OrderBillingEventEntity> implements OrderBillingEventDao {
}
