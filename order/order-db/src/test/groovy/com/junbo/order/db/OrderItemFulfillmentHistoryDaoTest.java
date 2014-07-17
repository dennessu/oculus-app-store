/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db;

import com.junbo.common.id.OrderEventId;
import com.junbo.order.db.common.TestHelper;
import com.junbo.order.db.dao.OrderItemFulfillmentHistoryDao;
import com.junbo.order.db.entity.OrderItemFulfillmentHistoryEntity;
import com.junbo.order.spec.model.enums.FulfillmentEventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by LinYi on 2/11/14.
 */
public class OrderItemFulfillmentHistoryDaoTest extends BaseTest {
    @Autowired
    private OrderItemFulfillmentHistoryDao orderItemFulfillmentHistoryDao;

    @Test
    public void testCreateAndRead() {
        OrderItemFulfillmentHistoryEntity entity = TestHelper.generateOrderItemFulfillmentHistoryEntity();
        entity.setHistoryId(idGenerator.nextId(OrderEventId.class));
        Long id = orderItemFulfillmentHistoryDao.create(entity);
        OrderItemFulfillmentHistoryEntity returnedEntity = orderItemFulfillmentHistoryDao.read(id);

        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
        Assert.assertEquals(returnedEntity.getHistoryId(), entity.getHistoryId(),
                "The event Id should not be different.");
    }

    @Test
    public void testUpdate() {
        OrderItemFulfillmentHistoryEntity entity = TestHelper.generateOrderItemFulfillmentHistoryEntity();
        entity.setHistoryId(idGenerator.nextId(OrderEventId.class));
        Long id = orderItemFulfillmentHistoryDao.create(entity);
        entity.setFulfillmentEventId(FulfillmentEventType.FULFILL);
        orderItemFulfillmentHistoryDao.update(entity);
        OrderItemFulfillmentHistoryEntity returnedEntity = orderItemFulfillmentHistoryDao.read(id);

        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
        Assert.assertEquals(returnedEntity.getFulfillmentEventId(), entity.getFulfillmentEventId(),
                "The action Id should not be different.");
    }
}
