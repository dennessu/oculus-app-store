/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db;

import com.junbo.common.id.OrderId;
import com.junbo.common.id.OrderItemId;
import com.junbo.order.db.common.TestHelper;
import com.junbo.order.db.dao.OrderItemDao;
import com.junbo.order.db.entity.OrderItemEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Created by LinYi on 2/10/14.
 */

public class OrderItemDaoTest extends BaseTest {
    @Autowired
    private OrderItemDao orderItemDao;

    @Test
    public void testCreateAndRead() {
        OrderItemEntity orderItemEntity = TestHelper.generateOrderItem();
        orderItemEntity.setOrderId(idGenerator.nextId(OrderId.class));
        orderItemEntity.setOrderItemId(idGenerator.nextId(OrderItemId.class, orderItemEntity.getOrderId()));
        Long orderItemId = orderItemDao.create(orderItemEntity);

        OrderItemEntity returnedEntity = orderItemDao.read(orderItemId);
        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
        Assert.assertEquals(returnedEntity.getOrderItemId(), orderItemEntity.getOrderItemId(),
                "The order item Id should not be different.");
        Assert.assertEquals(returnedEntity.getDeveloperRevenue(), orderItemEntity.getDeveloperRevenue(),
                "The order developerRevenue should not be different.");
    }

    @Test
    public void testUpdate() {
        OrderItemEntity orderItemEntity = TestHelper.generateOrderItem();
        orderItemEntity.setOrderId(idGenerator.nextId(OrderId.class));
        orderItemEntity.setOrderItemId(idGenerator.nextId(OrderItemId.class, orderItemEntity.getOrderId()));
        Long orderItemId = orderItemDao.create(orderItemEntity);
        orderItemDao.update(orderItemEntity);

        OrderItemEntity returnedEntity = orderItemDao.read(orderItemId);
        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
    }

    @Test
    public void testReadByOrderId() {
        OrderItemEntity orderItemEntity = TestHelper.generateOrderItem();
        orderItemEntity.setOrderId(idGenerator.nextId(OrderId.class));
        orderItemEntity.setOrderItemId(idGenerator.nextId(OrderItemId.class, orderItemEntity.getOrderId()));
        Long orderId = orderItemEntity.getOrderId();
        List<OrderItemEntity> resultBefore = orderItemDao.readByOrderId(orderId);
        orderItemDao.create(orderItemEntity);
        List<OrderItemEntity> resultAfter = orderItemDao.readByOrderId(orderId);
        Assert.assertEquals(resultAfter.size(), resultBefore.size() + 1, "Result size should increase.");
    }

    @Test
    public void testMarkDelete() {
        OrderItemEntity orderItemEntity = TestHelper.generateOrderItem();
        orderItemEntity.setOrderId(idGenerator.nextId(OrderId.class));
        orderItemEntity.setOrderItemId(idGenerator.nextId(OrderItemId.class, orderItemEntity.getOrderId()));
        Long orderId = orderItemEntity.getOrderId();
        List<OrderItemEntity> resultBefore = orderItemDao.readByOrderId(orderId);
        orderItemDao.create(orderItemEntity);
        orderItemDao.markDelete(orderItemEntity.getOrderItemId());
        List<OrderItemEntity> resultAfter = orderItemDao.readByOrderId(orderId);
        Assert.assertEquals(resultAfter.size(), resultBefore.size(), "Result size should not increase.");
    }
}
