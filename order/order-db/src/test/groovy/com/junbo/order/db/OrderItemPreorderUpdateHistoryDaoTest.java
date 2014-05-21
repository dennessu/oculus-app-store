/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db;

import com.junbo.order.db.common.TestHelper;
import com.junbo.order.db.dao.OrderItemPreorderUpdateHistoryDao;
import com.junbo.order.db.entity.OrderItemPreorderUpdateHistoryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by LinYi on 2/11/14.
 */
public class OrderItemPreorderUpdateHistoryDaoTest extends BaseTest {
    @Autowired
    private OrderItemPreorderUpdateHistoryDao orderItemPreorderUpdateHistoryDao;

    @Test
    public void testCreateAndRead() {
        OrderItemPreorderUpdateHistoryEntity entity = TestHelper.generateOrderItemPreorderUpdateHistoryEntity();
        Long id = orderItemPreorderUpdateHistoryDao.create(entity);
        OrderItemPreorderUpdateHistoryEntity returnedEntity = orderItemPreorderUpdateHistoryDao.read(id);

        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
        Assert.assertEquals(returnedEntity.getOrderItemPreorderUpdateHistoryId(),
                entity.getOrderItemPreorderUpdateHistoryId(),
                "The update history Id should not be different.");
    }

    @Test
    public void testUpdate() {
        OrderItemPreorderUpdateHistoryEntity entity = TestHelper.generateOrderItemPreorderUpdateHistoryEntity();
        Long id = orderItemPreorderUpdateHistoryDao.create(entity);
        entity.setUpdatedBy(456L);
        orderItemPreorderUpdateHistoryDao.update(entity);
        OrderItemPreorderUpdateHistoryEntity returnedEntity = orderItemPreorderUpdateHistoryDao.read(id);

        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
        Assert.assertEquals(returnedEntity.getUpdatedBy(), entity.getUpdatedBy(),
                "The updatedBy should not be different.");
    }
}
