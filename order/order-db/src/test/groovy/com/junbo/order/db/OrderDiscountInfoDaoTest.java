/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db;

import com.junbo.order.db.common.TestHelper;
import com.junbo.order.db.dao.OrderDiscountInfoDao;
import com.junbo.order.db.entity.OrderDiscountInfoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by LinYi on 2/11/14.
 */
public class OrderDiscountInfoDaoTest extends BaseTest {
    @Autowired
    private OrderDiscountInfoDao orderDiscountInfoDao;

    @Test
    public void testCreateAndRead() {
        OrderDiscountInfoEntity orderDiscountInfoEntity = TestHelper.generateOrderDiscountInfoEntity();
        Long id = orderDiscountInfoDao.create(orderDiscountInfoEntity);
        orderDiscountInfoDao.flush();
        OrderDiscountInfoEntity returnedEntity = orderDiscountInfoDao.read(id);

        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
        Assert.assertEquals(returnedEntity.getDiscountInfoId(), orderDiscountInfoEntity.getDiscountInfoId(),
                "The event Id should not be different.");
    }

    @Test
    public void testUpdate() {
        OrderDiscountInfoEntity orderDiscountInfoEntity = TestHelper.generateOrderDiscountInfoEntity();
        Long id = orderDiscountInfoDao.create(orderDiscountInfoEntity);
        orderDiscountInfoDao.flush();
        BigDecimal newDiscountRate = orderDiscountInfoEntity.getDiscountRate().add(BigDecimal.ONE);
        orderDiscountInfoEntity.setDiscountRate(newDiscountRate);
        orderDiscountInfoDao.update(orderDiscountInfoEntity);
        orderDiscountInfoDao.flush();
        OrderDiscountInfoEntity returnedEntity = orderDiscountInfoDao.read(id);

        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
        Assert.assertEquals(returnedEntity.getDiscountType(), orderDiscountInfoEntity.getDiscountType(),
                "The Discount type should not be different.");
    }

    @Test
    public void testReadByOrderId() {
        OrderDiscountInfoEntity entity = TestHelper.generateOrderDiscountInfoEntity();
        Long orderId = entity.getOrderId();
        List<OrderDiscountInfoEntity> resultBefore = orderDiscountInfoDao.readByOrderId(orderId);
        orderDiscountInfoDao.create(entity);
        orderDiscountInfoDao.flush();
        List<OrderDiscountInfoEntity> resultAfter = orderDiscountInfoDao.readByOrderId(orderId);

        Assert.assertEquals(resultBefore.size() + 1, resultAfter.size(), "Result size should increase.");
    }
}
