/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db;

import com.junbo.common.id.OrderItemId;
import com.junbo.order.db.dao.OrderItemTaxInfoDao;
import com.junbo.order.db.entity.OrderItemTaxInfoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Random;

/**
 * Created by LinYi on 2/11/14.
 */
public class OrderItemTaxInfoDaoTest extends BaseTest {
    @Autowired
    private OrderItemTaxInfoDao orderItemTaxInfoDao;

    @Test
    public void testCreateAndRead() {
        OrderItemTaxInfoEntity entity = generateOrderItemTaxInfoEntity();
        entity.setOrderItemId(idGenerator.nextId(OrderItemId.class));
        Long id = orderItemTaxInfoDao.create(entity);
        OrderItemTaxInfoEntity returnedEntity = orderItemTaxInfoDao.read(id);

        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
        Assert.assertEquals(returnedEntity.getOrderItemTaxInfoId(), entity.getOrderItemTaxInfoId(),
                "The tax info Id should not be different.");
    }

    @Test
    public void testUpdate() {
        OrderItemTaxInfoEntity entity = generateOrderItemTaxInfoEntity();
        entity.setOrderItemId(idGenerator.nextId(OrderItemId.class));
        Long id = orderItemTaxInfoDao.create(entity);
        entity.setUpdatedBy("ANOTHER");
        orderItemTaxInfoDao.update(entity);
        OrderItemTaxInfoEntity returnedEntity = orderItemTaxInfoDao.read(id);

        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
        Assert.assertEquals(returnedEntity.getUpdatedBy(), entity.getUpdatedBy(),
                "The UpdatedBy field should not be different.");
    }

    private OrderItemTaxInfoEntity generateOrderItemTaxInfoEntity() {
        OrderItemTaxInfoEntity entity = new OrderItemTaxInfoEntity();
        Random rand = new Random();
        entity.setOrderItemTaxInfoId(generateId());
        entity.setOrderItemId(generateLong());
        entity.setTotalTax(BigDecimal.valueOf(rand.nextInt(100)));
        entity.setIsTaxExempt(false);
        entity.setIsTaxInclusive(false);
        entity.setTaxCode("TEST_TAX_CODE");
        entity.setCreatedTime(new Date());
        entity.setCreatedBy("TESTER");
        entity.setUpdatedTime(new Date());
        entity.setUpdatedBy("Tester");
        return entity;
    }
}
