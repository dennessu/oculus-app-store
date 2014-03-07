/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db;

import com.junbo.order.db.dao.OrderItemPreorderEventDao;
import com.junbo.order.db.entity.OrderItemPreorderEventEntity;
import com.junbo.order.db.entity.enums.EventStatus;
import com.junbo.order.db.entity.enums.PreorderAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Random;

/**
 * Created by LinYi on 2/11/14.
 */
public class OrderItemPreorderEventDaoTest extends BaseTest {
    @Autowired
    private OrderItemPreorderEventDao orderItemPreorderEventDao;

    @Test
    public void testCreateAndRead() {
        OrderItemPreorderEventEntity orderItemPreorderEventEntity = generateOrderItemPreorderEventEntity();
        Long id = orderItemPreorderEventDao.create(orderItemPreorderEventEntity);
        orderItemPreorderEventDao.flush();
        OrderItemPreorderEventEntity returnedEntity = orderItemPreorderEventDao.read(id);

        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
        Assert.assertEquals(returnedEntity.getEventId(), orderItemPreorderEventEntity.getEventId(),
                "The event Id should not be different.");
    }

    @Test
    public void testUpdate() {
        OrderItemPreorderEventEntity orderItemPreorderEventEntity = generateOrderItemPreorderEventEntity();
        Long id = orderItemPreorderEventDao.create(orderItemPreorderEventEntity);
        orderItemPreorderEventDao.flush();
        orderItemPreorderEventEntity.setAction(PreorderAction.CHARGE);
        orderItemPreorderEventDao.update(orderItemPreorderEventEntity);
        orderItemPreorderEventDao.flush();
        OrderItemPreorderEventEntity returnedEntity = orderItemPreorderEventDao.read(id);

        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
        Assert.assertEquals(returnedEntity.getAction(), orderItemPreorderEventEntity.getAction(),
                "The action Id should not be different.");
    }

    private OrderItemPreorderEventEntity generateOrderItemPreorderEventEntity() {
        OrderItemPreorderEventEntity entity = new OrderItemPreorderEventEntity();
        Random rand = new Random();
        entity.setEventId(generateId());
        entity.setAction(PreorderAction.CHARGE);
        entity.setStatus(EventStatus.COMPLETED);
        entity.setOrderItemId(generateLong());
        return entity;
    }
}
