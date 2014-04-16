/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db;

import com.junbo.common.id.OrderEventId;
import com.junbo.order.db.common.TestHelper;
import com.junbo.order.db.dao.OrderBillingEventDao;
import com.junbo.order.db.entity.OrderBillingEventEntity;
import com.junbo.order.db.entity.enums.BillingAction;
import com.junbo.order.db.entity.enums.EventStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Random;

/**
 * Created by LinYi on 2/11/14.
 */
public class OrderBillingEventDaoTest extends BaseTest {
    @Autowired
    private OrderBillingEventDao orderBillingEventDao;

    @Test
    public void testCreateAndRead() {
        OrderBillingEventEntity orderBillingEventEntity = TestHelper.generateOrderBillingEventEntity();
        orderBillingEventEntity.setOrderId(idGenerator.nextId(OrderEventId.class));
        Long id = orderBillingEventDao.create(orderBillingEventEntity);
        OrderBillingEventEntity returnedEntity = orderBillingEventDao.read(id);

        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
        Assert.assertEquals(returnedEntity.getEventId(), orderBillingEventEntity.getEventId(),
                "The event Id should not be different.");
    }

    @Test
    public void testUpdate() {
        OrderBillingEventEntity orderBillingEventEntity = TestHelper.generateOrderBillingEventEntity();
        orderBillingEventEntity.setOrderId(idGenerator.nextId(OrderEventId.class));
        Long id = orderBillingEventDao.create(orderBillingEventEntity);
        orderBillingEventEntity.setAction(BillingAction.CHARGE);
        orderBillingEventDao.update(orderBillingEventEntity);
        OrderBillingEventEntity returnedEntity = orderBillingEventDao.read(id);

        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
        Assert.assertEquals(returnedEntity.getAction(), orderBillingEventEntity.getAction(),
                "The action Id should not be different.");
    }
}
