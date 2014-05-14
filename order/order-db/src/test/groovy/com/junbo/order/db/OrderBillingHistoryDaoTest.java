/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db;

import com.junbo.common.id.OrderEventId;
import com.junbo.order.db.common.TestHelper;
import com.junbo.order.db.dao.OrderBillingHistoryDao;
import com.junbo.order.db.entity.OrderBillingHistoryEntity;
import com.junbo.order.db.entity.enums.BillingAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by LinYi on 2/11/14.
 */
public class OrderBillingHistoryDaoTest extends BaseTest {
    @Autowired
    private OrderBillingHistoryDao orderBillingHistoryDao;

    @Test
    public void testCreateAndRead() {
        OrderBillingHistoryEntity orderBillingHistoryEntity = TestHelper.generateOrderBillingHistoryEntity();
        orderBillingHistoryEntity.setOrderId(idGenerator.nextId(OrderEventId.class));
        Long id = orderBillingHistoryDao.create(orderBillingHistoryEntity);
        OrderBillingHistoryEntity returnedEntity = orderBillingHistoryDao.read(id);

        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
        Assert.assertEquals(returnedEntity.getHistoryId(), orderBillingHistoryEntity.getHistoryId(),
                "The event Id should not be different.");
    }

    @Test
    public void testUpdate() {
        OrderBillingHistoryEntity orderBillingHistoryEntity = TestHelper.generateOrderBillingHistoryEntity();
        orderBillingHistoryEntity.setOrderId(idGenerator.nextId(OrderEventId.class));
        Long id = orderBillingHistoryDao.create(orderBillingHistoryEntity);
        orderBillingHistoryEntity.setBillingEventId(BillingAction.CHARGE);
        orderBillingHistoryDao.update(orderBillingHistoryEntity);
        OrderBillingHistoryEntity returnedEntity = orderBillingHistoryDao.read(id);

        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
        Assert.assertEquals(returnedEntity.getBillingEventId(), orderBillingHistoryEntity.getBillingEventId(),
                "The action Id should not be different.");
    }
}
