/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db;

import com.junbo.order.db.common.TestHelper;
import com.junbo.order.db.dao.SubledgerDao;
import com.junbo.order.db.entity.SubledgerEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by LinYi on 2/11/14.
 */
public class SubledgerDaoTest extends BaseTest {
    @Autowired
    private SubledgerDao orderPaymentInfoDao;

    @Test
    public void testCreateAndRead() {
        SubledgerEntity orderPaymentInfoEntity = TestHelper.generateSubledgerEntity();
        Long id = orderPaymentInfoDao.create(orderPaymentInfoEntity);
        orderPaymentInfoDao.flush();
        SubledgerEntity returnedEntity = orderPaymentInfoDao.read(id);

        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
        Assert.assertEquals(returnedEntity.getSubledgerId(), orderPaymentInfoEntity.getSubledgerId(),
                "The subledger Id should not be different.");
    }

    @Test
    public void testUpdate() {
        SubledgerEntity orderPaymentInfoEntity = TestHelper.generateSubledgerEntity();
        Long id = orderPaymentInfoDao.create(orderPaymentInfoEntity);
        orderPaymentInfoDao.flush();
        orderPaymentInfoEntity.setUpdatedBy("ANOTHER");
        orderPaymentInfoDao.update(orderPaymentInfoEntity);
        orderPaymentInfoDao.flush();
        SubledgerEntity returnedEntity = orderPaymentInfoDao.read(id);

        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
        Assert.assertEquals(returnedEntity.getUpdatedBy(), orderPaymentInfoEntity.getUpdatedBy(),
                "The updatedBy should not be different.");
    }
}
