/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db;

import com.junbo.order.db.common.TestHelper;
import com.junbo.order.db.dao.OrderItemFulfillmentEventDao;
import com.junbo.order.db.entity.OrderItemFulfillmentEventEntity;
import com.junbo.order.db.entity.enums.FulfillmentAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Created by LinYi on 2/11/14.
 */
public class OrderItemFulfillmentEventDaoTest extends BaseTest {
    @Autowired
    private OrderItemFulfillmentEventDao orderItemFulfillmentEventDao;

    @Test
    public void testCreateAndRead() {
        OrderItemFulfillmentEventEntity orderItemFulfillmentEventEntity =
                TestHelper.generateOrderItemFulfillmentEventEntity();
        Long id = orderItemFulfillmentEventDao.create(orderItemFulfillmentEventEntity);
        orderItemFulfillmentEventDao.flush();
        OrderItemFulfillmentEventEntity returnedEntity = orderItemFulfillmentEventDao.read(id);

        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
        Assert.assertEquals(returnedEntity.getEventId(), orderItemFulfillmentEventEntity.getEventId(),
                "The event Id should not be different.");
    }

    @Test
    public void testUpdate() {
        OrderItemFulfillmentEventEntity orderItemFulfillmentEventEntity =
                TestHelper.generateOrderItemFulfillmentEventEntity();
        Long id = orderItemFulfillmentEventDao.create(orderItemFulfillmentEventEntity);
        orderItemFulfillmentEventDao.flush();
        orderItemFulfillmentEventEntity.setAction(FulfillmentAction.FULFILL);
        orderItemFulfillmentEventDao.update(orderItemFulfillmentEventEntity);
        orderItemFulfillmentEventDao.flush();
        OrderItemFulfillmentEventEntity returnedEntity = orderItemFulfillmentEventDao.read(id);

        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
        Assert.assertEquals(returnedEntity.getAction(), orderItemFulfillmentEventEntity.getAction(),
                "The action Id should not be different.");
    }

    @Test
    public void testReadByOrderItemId() {
        OrderItemFulfillmentEventEntity fulfillmentEventEntity = TestHelper.generateOrderItemFulfillmentEventEntity();
        Long orderId = fulfillmentEventEntity.getOrderId();
        Long orderItemId = fulfillmentEventEntity.getOrderItemId();
        List<OrderItemFulfillmentEventEntity> resultBefore =
                orderItemFulfillmentEventDao.readByOrderItemId(orderId, orderItemId);
        orderItemFulfillmentEventDao.create(fulfillmentEventEntity);
        orderItemFulfillmentEventDao.flush();
        List<OrderItemFulfillmentEventEntity> resultAfter =
                orderItemFulfillmentEventDao.readByOrderItemId(orderId, orderItemId);

        Assert.assertEquals(resultBefore.size() + 1, resultAfter.size(), "Result size should increase.");
    }
}
