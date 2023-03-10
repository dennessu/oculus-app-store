/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db;

import com.junbo.order.db.dao.OrderPaymentInfoDao;
import com.junbo.order.db.entity.OrderPaymentInfoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.List;

/**
 * Created by LinYi on 2/11/14.
 */
public class OrderPaymentInfoDaoTest extends BaseTest {
    @Autowired
    private OrderPaymentInfoDao orderPaymentInfoDao;

    @Test
    public void testCreateAndRead() {
        OrderPaymentInfoEntity orderPaymentInfoEntity = generateOrderPaymentInfoEntity();
        Long id = orderPaymentInfoDao.create(orderPaymentInfoEntity);
        OrderPaymentInfoEntity returnedEntity = orderPaymentInfoDao.read(id);

        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
        Assert.assertEquals(returnedEntity.getOrderPaymentId(), orderPaymentInfoEntity.getOrderPaymentId(),
                "The order payment Id should not be different.");
    }

    @Test
    public void testUpdate() {
        OrderPaymentInfoEntity orderPaymentInfoEntity = generateOrderPaymentInfoEntity();
        Long id = orderPaymentInfoDao.create(orderPaymentInfoEntity);
        orderPaymentInfoEntity.setUpdatedBy(123L);
        orderPaymentInfoDao.update(orderPaymentInfoEntity);
        OrderPaymentInfoEntity returnedEntity = orderPaymentInfoDao.read(id);

        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
        Assert.assertEquals(returnedEntity.getUpdatedBy(), orderPaymentInfoEntity.getUpdatedBy(),
                "The updatedBy should not be different.");
    }

    @Test(enabled = false)
    public void testReadByOrderId() {
        OrderPaymentInfoEntity orderPaymentInfoEntity = generateOrderPaymentInfoEntity();
        Long orderId = orderPaymentInfoEntity.getOrderId();
        List<OrderPaymentInfoEntity> resultBefore = orderPaymentInfoDao.readByOrderId(orderId);
        orderPaymentInfoDao.create(orderPaymentInfoEntity);
        List<OrderPaymentInfoEntity> resultAfter = orderPaymentInfoDao.readByOrderId(orderId);

        Assert.assertEquals(resultAfter.size(), resultBefore.size() + 1, "Result size should increase.");
    }

    @Test(enabled = false)
    public void testMarkDelete() {
        OrderPaymentInfoEntity orderPaymentInfoEntity = generateOrderPaymentInfoEntity();
        Long paymentInfoId = orderPaymentInfoEntity.getOrderPaymentId();
        List<OrderPaymentInfoEntity> resultBefore = orderPaymentInfoDao.readByOrderId(paymentInfoId);
        orderPaymentInfoDao.create(orderPaymentInfoEntity);
        List<OrderPaymentInfoEntity> resultAfter = orderPaymentInfoDao.readByOrderId(paymentInfoId);

        Assert.assertEquals(resultAfter.size(), resultBefore.size() + 1, "Result size should increase.");
        orderPaymentInfoDao.markDelete(orderPaymentInfoEntity.getOrderPaymentId());
        Assert.assertEquals(orderPaymentInfoDao.readByOrderId(paymentInfoId).size(),
                resultBefore.size(), "Result size should decrease.");
    }

    private OrderPaymentInfoEntity generateOrderPaymentInfoEntity() {
        OrderPaymentInfoEntity entity = new OrderPaymentInfoEntity();
        entity.setOrderPaymentId(generateId());
        entity.setOrderId(generateLong());
        entity.setPaymentInstrumentId("TEST_PAYMENT_METHOD_ID");
        entity.setPaymentInstrumentType("TEST_PAYMENT_METHOD_TYPE");
        entity.setCreatedTime(new Date());
        entity.setCreatedBy(123L);
        entity.setUpdatedTime(new Date());
        entity.setUpdatedBy(456L);
        return entity;
    }
}
