/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db;

import com.junbo.common.id.OrderId;
import com.junbo.common.id.UserId;
import com.junbo.order.db.common.TestHelper;
import com.junbo.order.db.dao.OrderDao;
import com.junbo.order.db.entity.OrderEntity;
import com.junbo.sharding.IdGenerator;
import com.junbo.sharding.IdGeneratorFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Created by LinYi on 2/11/14.
 */
public class OrderDaoTest extends BaseTest {
    @Autowired
    private OrderDao orderDao;

    @Autowired
    protected IdGeneratorFacade idGenerator;

    @Test
    public void testCreateAndRead() {
        OrderEntity orderEntity = TestHelper.generateOrder();
        orderEntity.setUserId(idGenerator.nextId(UserId.class));
        orderEntity.setOrderId(idGenerator.nextId(OrderId.class, orderEntity.getUserId()));
        Long id = orderDao.create(orderEntity);
        OrderEntity returnedEntity = orderDao.read(id);

        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
        Assert.assertEquals(returnedEntity.getOrderId(), orderEntity.getOrderId(),
                "The order Id should not be different.");
    }

    @Test
    public void testUpdate() {
        OrderEntity orderEntity = TestHelper.generateOrder();
        orderEntity.setUserId(idGenerator.nextId(UserId.class));
        orderEntity.setOrderId(idGenerator.nextId(OrderId.class, orderEntity.getUserId()));
        Long id = orderDao.create(orderEntity);
        orderEntity.setCountry("CN");
        orderDao.update(orderEntity);

        OrderEntity returnedEntity = orderDao.read(id);

        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
        Assert.assertEquals(returnedEntity.getCountry(), "CN", "Fail to update entity.");
    }

    @Test
    public void testReadByUserId() {
        OrderEntity orderEntity = TestHelper.generateOrder();
        orderEntity.setUserId(idGenerator.nextId(UserId.class));
        orderEntity.setOrderId(idGenerator.nextId(OrderId.class, orderEntity.getUserId()));
        Long userId = orderEntity.getUserId();
        List<OrderEntity> resultBefore = orderDao.readByUserId(userId, null, null, null);
        orderDao.create(orderEntity);
        List<OrderEntity> resultAfter = orderDao.readByUserId(userId, null, null, null);
        Assert.assertEquals(resultAfter.size(), resultBefore.size() + 1, "Result size should increase.");
    }

    @Test
    public void testReadByUserIdWithPage() {
        Long userId = idGenerator.nextId(UserId.class);
        for (int i = 0;i < 3;++i) {
            OrderEntity entity = TestHelper.generateOrder();
            entity.setOrderId(idGenerator.nextId(OrderId.class, userId));
            entity.setUserId(userId);
            orderDao.create(entity);
        }
        Assert.assertEquals(orderDao.readByUserId(userId, null, null, null).size(), 3);
        Assert.assertEquals(orderDao.readByUserId(userId, null, 1, 2).size(), 2);
        Assert.assertEquals(orderDao.readByUserId(userId, null, null, 2).size(), 2);
        Assert.assertEquals(orderDao.readByUserId(userId, null, 1, null).size(), 2);
        Assert.assertEquals(orderDao.readByUserId(userId, null, 1, 4).size(), 2);
        Assert.assertEquals(orderDao.readByUserId(userId, null, 1, 1).size(), 1);
    }

    @Test
    public void testReadByUserIdOnlyTentative() {
        Long userId = TestHelper.generateId();
        for (int i = 0;i < 2;++i) {
            OrderEntity entity = TestHelper.generateOrder();
            entity.setUserId(userId);
            orderDao.create(entity);
            entity.setTentative((i % 2) == 0);
        }
        int totalSize = orderDao.readByUserId(userId, null, null, null).size();
        Assert.assertTrue(orderDao.readByUserId(userId, true, null, null).size() < totalSize);
        Assert.assertTrue(orderDao.readByUserId(userId, false, null, null).size() < totalSize);
    }
}
