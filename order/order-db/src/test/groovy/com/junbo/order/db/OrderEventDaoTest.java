/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db;

import com.junbo.order.db.common.TestHelper;
import com.junbo.order.db.dao.OrderEventDao;
import com.junbo.order.db.entity.OrderEventEntity;
import com.junbo.order.spec.model.OrderAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Created by LinYi on 2/8/14.
 */

public class OrderEventDaoTest extends BaseTest {
    @Autowired
    private OrderEventDao orderEventDao;

    @Test
    public void testCreateAndRead() {
        OrderEventEntity orderEventEntity = TestHelper.generateOrderEventEntity();
        Long id = orderEventDao.create(orderEventEntity);
        orderEventDao.flush();
        OrderEventEntity returnedEntity = orderEventDao.read(id);

        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
        Assert.assertEquals(returnedEntity.getEventId(), orderEventEntity.getEventId(),
                "The event Id should not be different.");
    }

    @Test
    public void testUpdate() {
        OrderEventEntity orderEventEntity = TestHelper.generateOrderEventEntity();
        Long id = orderEventDao.create(orderEventEntity);
        orderEventDao.flush();
        orderEventEntity.setActionId(OrderAction.CHARGE);
        orderEventDao.update(orderEventEntity);
        orderEventDao.flush();
        OrderEventEntity returnedEntity = orderEventDao.read(id);

        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
        Assert.assertEquals(returnedEntity.getActionId(), orderEventEntity.getActionId(),
                "The action Id should not be different.");
    }

    @Test
    public void testReadByOrderId() {
        OrderEventEntity entity = TestHelper.generateOrderEventEntity();
        Long orderId = entity.getOrderId();
        List<OrderEventEntity> resultBefore = orderEventDao.readByOrderId(orderId);
        orderEventDao.create(entity);
        orderEventDao.flush();
        List<OrderEventEntity> resultAfter = orderEventDao.readByOrderId(orderId);

        Assert.assertEquals(resultBefore.size() + 1, resultAfter.size(), "Result size should increase.");
    }
}
