/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db;

import com.junbo.order.db.common.TestHelper;
import com.junbo.order.db.dao.OrderDao;
import com.junbo.order.db.entity.OrderEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

/**
 * Created by LinYi on 2/11/14.
 */
public class OrderDaoTest extends BaseTest {
    @Autowired
    private OrderDao orderDao;

    @Test
    public void testCreateAndRead() {
        OrderEntity orderEntity = TestHelper.generateOrder();
        Long id = orderDao.create(orderEntity);
        orderDao.flush();
        OrderEntity returnedEntity = orderDao.read(id);

        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
        Assert.assertEquals(returnedEntity.getOrderId(), orderEntity.getOrderId(),
                "The order Id should not be different.");
    }

    @Test
    public void testUpdate() {
        OrderEntity orderEntity = TestHelper.generateOrder();
        Long id = orderDao.create(orderEntity);
        orderDao.flush();
        orderEntity.setCountry("CN");
        orderDao.update(orderEntity);
        orderDao.flush();

        OrderEntity returnedEntity = orderDao.read(id);

        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
        Assert.assertEquals("CN", returnedEntity.getCountry(), "Fail to update entity.");
    }

    @Test
    public void testReadByUserId() {
        OrderEntity orderEntity = TestHelper.generateOrder();
        Long userId = orderEntity.getUserId();
        List<OrderEntity> resultBefore = orderDao.readByUserId(userId);
        orderDao.create(orderEntity);
        orderDao.flush();
        List<OrderEntity> resultAfter = orderDao.readByUserId(userId);
        Assert.assertEquals(resultBefore.size() + 1, resultAfter.size(), "Result size should increase.");
    }

    @Test
    public void testReadByUuid() {
        OrderEntity orderEntity = TestHelper.generateOrder();
        UUID trackingUuid = orderEntity.getTrackingUuid();
        List<OrderEntity> resultBefore = orderDao.readByTrackingUuid(trackingUuid);
        orderDao.create(orderEntity);
        orderDao.flush();
        List<OrderEntity> resultAfter = orderDao.readByTrackingUuid(trackingUuid);

        Assert.assertEquals(resultBefore.size() + 1, resultAfter.size(), "Result size should increase.");
    }
}
