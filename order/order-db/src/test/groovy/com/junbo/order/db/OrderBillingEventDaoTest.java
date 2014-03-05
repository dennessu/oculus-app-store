/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db;

import com.junbo.order.db.dao.OrderBillingEventDao;
import com.junbo.order.db.entity.OrderBillingEventEntity;
import com.junbo.order.spec.model.BillingAction;
import com.junbo.order.spec.model.EventStatus;
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
        OrderBillingEventEntity orderBillingEventEntity = generateOrderBillingEventEntity();
        Long id = orderBillingEventDao.create(orderBillingEventEntity);
        orderBillingEventDao.flush();
        OrderBillingEventEntity returnedEntity = orderBillingEventDao.read(id);

        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
        Assert.assertEquals(returnedEntity.getEventId(), orderBillingEventEntity.getEventId(),
                "The event Id should not be different.");
    }

    @Test
    public void testUpdate() {
        OrderBillingEventEntity orderBillingEventEntity = generateOrderBillingEventEntity();
        Long id = orderBillingEventDao.create(orderBillingEventEntity);
        orderBillingEventDao.flush();
        orderBillingEventEntity.setAction(BillingAction.CHARGE);
        orderBillingEventDao.update(orderBillingEventEntity);
        orderBillingEventDao.flush();
        OrderBillingEventEntity returnedEntity = orderBillingEventDao.read(id);

        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
        Assert.assertEquals(returnedEntity.getAction(), orderBillingEventEntity.getAction(),
                "The action Id should not be different.");
    }

    private OrderBillingEventEntity generateOrderBillingEventEntity() {
        OrderBillingEventEntity entity = new OrderBillingEventEntity();
        Random rand = new Random();
        entity.setEventId(generateId());
        entity.setAction(BillingAction.CHARGE);
        entity.setStatus(EventStatus.COMPLETED);
        entity.setOrderId(generateLong());
        entity.setBalanceId("TEST_BALANCE_ID");
        return entity;
    }
}
