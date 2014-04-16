/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db;

import com.junbo.order.db.common.TestHelper;
import com.junbo.order.db.dao.OrderEventDao;
import com.junbo.order.db.entity.OrderEventEntity;
import com.junbo.order.db.entity.enums.OrderActionType;
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
        OrderEventEntity returnedEntity = orderEventDao.read(id);

        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
        Assert.assertEquals(returnedEntity.getEventId(), orderEventEntity.getEventId(),
                "The event Id should not be different.");
    }

    @Test
    public void testUpdate() {
        OrderEventEntity orderEventEntity = TestHelper.generateOrderEventEntity();
        Long id = orderEventDao.create(orderEventEntity);
        orderEventEntity.setActionId(OrderActionType.CHARGE);
        orderEventDao.update(orderEventEntity);
        OrderEventEntity returnedEntity = orderEventDao.read(id);

        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
        Assert.assertEquals(returnedEntity.getActionId(), orderEventEntity.getActionId(),
                "The action Id should not be different.");
    }

    @Test
    public void testReadByOrderId() {
        OrderEventEntity entity = TestHelper.generateOrderEventEntity();
        Long orderId = entity.getOrderId();
        List<OrderEventEntity> resultBefore = orderEventDao.readByOrderId(orderId, null, null);
        orderEventDao.create(entity);
        List<OrderEventEntity> resultAfter = orderEventDao.readByOrderId(orderId, null, null);

        Assert.assertEquals(resultAfter.size(), resultBefore.size() + 1, "Result size should increase.");
        Assert.assertEquals(orderEventDao.readByOrderId(orderId, resultBefore.size(), 1).size(), 1);
        Assert.assertEquals(orderEventDao.readByOrderId(orderId, 0, 1).size(), 1);
        Assert.assertEquals(orderEventDao.readByOrderId(orderId, resultBefore.size() + 1, 1).size(), 0);
    }

    @Test
    public void testReadByOrderIdWithPage() {
        Long orderId = TestHelper.generateId();
        for (int i = 0;i < 3;++i) {
            OrderEventEntity entity = TestHelper.generateOrderEventEntity();
            entity.setOrderId(orderId);
            orderEventDao.create(entity);
        }

        Assert.assertEquals(orderEventDao.readByOrderId(orderId, null, null).size(), 3);
        Assert.assertEquals(orderEventDao.readByOrderId(orderId, 1, 2).size(), 2);
        Assert.assertEquals(orderEventDao.readByOrderId(orderId, null, 2).size(), 2);
        Assert.assertEquals(orderEventDao.readByOrderId(orderId, 1, null).size(), 2);
        Assert.assertEquals(orderEventDao.readByOrderId(orderId, 1, 4).size(), 2);
        Assert.assertEquals(orderEventDao.readByOrderId(orderId, 1, 1).size(), 1);
    }
}
