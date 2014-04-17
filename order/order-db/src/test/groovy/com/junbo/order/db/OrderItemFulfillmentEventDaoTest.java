/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db;

import com.junbo.common.id.FulfillmentEventId;
import com.junbo.common.id.OrderEventId;
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
        OrderItemFulfillmentEventEntity entity = TestHelper.generateOrderItemFulfillmentEventEntity();
        entity.setEventId(idGenerator.nextId(OrderEventId.class));
        Long id = orderItemFulfillmentEventDao.create(entity);
        OrderItemFulfillmentEventEntity returnedEntity = orderItemFulfillmentEventDao.read(id);

        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
        Assert.assertEquals(returnedEntity.getEventId(), entity.getEventId(),
                "The event Id should not be different.");
    }

    @Test
    public void testUpdate() {
        OrderItemFulfillmentEventEntity entity = TestHelper.generateOrderItemFulfillmentEventEntity();
        entity.setEventId(idGenerator.nextId(OrderEventId.class));
        Long id = orderItemFulfillmentEventDao.create(entity);
        entity.setAction(FulfillmentAction.FULFILL);
        orderItemFulfillmentEventDao.update(entity);
        OrderItemFulfillmentEventEntity returnedEntity = orderItemFulfillmentEventDao.read(id);

        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
        Assert.assertEquals(returnedEntity.getAction(), entity.getAction(),
                "The action Id should not be different.");
    }
}
