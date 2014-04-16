/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db;

import com.junbo.common.id.PreorderId;
import com.junbo.order.db.common.TestHelper;
import com.junbo.order.db.dao.OrderItemPreorderInfoDao;
import com.junbo.order.db.entity.OrderItemPreorderInfoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by LinYi on 2/11/14.
 */
public class OrderItemPreorderInfoDaoTest extends BaseTest {
    @Autowired
    private OrderItemPreorderInfoDao orderItemPreorderInfoDao;

    @Test
    public void testCreateAndRead() {
        OrderItemPreorderInfoEntity entity = TestHelper.generateOrderItemPreorderInfoEntity();
        entity.setOrderItemPreorderInfoId(idGenerator.nextId(PreorderId.class));
        Long id = orderItemPreorderInfoDao.create(entity);
        OrderItemPreorderInfoEntity returnedEntity = orderItemPreorderInfoDao.read(id);

        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
        Assert.assertEquals(returnedEntity.getOrderItemPreorderInfoId(),
                entity.getOrderItemPreorderInfoId(),
                "The preorder info Id should not be different.");
    }

    @Test
    public void testUpdate() {
        OrderItemPreorderInfoEntity entity = TestHelper.generateOrderItemPreorderInfoEntity();
        entity.setOrderItemPreorderInfoId(idGenerator.nextId(PreorderId.class));
        Long id = orderItemPreorderInfoDao.create(entity);
        entity.setUpdatedBy("ANOTHER");
        orderItemPreorderInfoDao.update(entity);
        OrderItemPreorderInfoEntity returnedEntity = orderItemPreorderInfoDao.read(id);

        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
        Assert.assertEquals(returnedEntity.getUpdatedBy(), entity.getUpdatedBy(),
                "The updatedBy should not be different.");
    }
}
