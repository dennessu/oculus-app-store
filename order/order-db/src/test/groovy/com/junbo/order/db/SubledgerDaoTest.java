/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db;

import com.junbo.order.db.common.TestHelper;
import com.junbo.order.db.dao.SubledgerDao;
import com.junbo.order.db.entity.SubledgerEntity;
import com.junbo.order.db.entity.enums.PayoutStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Date;

/**
 * Created by LinYi on 2/11/14.
 */
public class SubledgerDaoTest extends BaseTest {
    @Autowired
    private SubledgerDao subledgerDao;

    @Test
    public void testCreateAndRead() {
        SubledgerEntity orderPaymentInfoEntity = TestHelper.generateSubledgerEntity();
        Long id = subledgerDao.create(orderPaymentInfoEntity);
        subledgerDao.flush();
        SubledgerEntity returnedEntity = subledgerDao.read(id);

        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
        Assert.assertEquals(returnedEntity.getSubledgerId(), orderPaymentInfoEntity.getSubledgerId(),
                "The subledger Id should not be different.");
    }

    @Test
    public void testUpdate() {
        SubledgerEntity orderPaymentInfoEntity = TestHelper.generateSubledgerEntity();
        Long id = subledgerDao.create(orderPaymentInfoEntity);
        subledgerDao.flush();
        orderPaymentInfoEntity.setUpdatedBy("ANOTHER");
        subledgerDao.update(orderPaymentInfoEntity);
        subledgerDao.flush();
        SubledgerEntity returnedEntity = subledgerDao.read(id);

        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
        Assert.assertEquals(returnedEntity.getUpdatedBy(), orderPaymentInfoEntity.getUpdatedBy(),
                "The updatedBy should not be different.");
    }

    @Test
    public void testGetBySellorId() {
        SubledgerEntity entity = TestHelper.generateSubledgerEntity();
        entity.setPayoutStatus(PayoutStatus.PENDING);
        subledgerDao.create(entity);
        subledgerDao.flush();

        Assert.assertEquals(subledgerDao.getBySellerId(entity.getSellerId(),
                PayoutStatus.PENDING, entity.getStartTime(),
                new Date(entity.getStartTime().getTime() + 1000), 0, 10).size(), 1);

        Assert.assertEquals(subledgerDao.getBySellerId(entity.getSellerId(),
                PayoutStatus.PENDING, entity.getStartTime(),
                new Date(entity.getStartTime().getTime() + 1000), 1, 10).size(), 0);

        Assert.assertEquals(subledgerDao.getBySellerId(entity.getSellerId(),
                PayoutStatus.COMPLETED, entity.getStartTime(),
                new Date(entity.getStartTime().getTime() + 1000), 0, 10).size(), 0);

        Assert.assertEquals(subledgerDao.getBySellerId(entity.getSellerId(),
                PayoutStatus.PENDING, new Date(entity.getStartTime().getTime() - 1000),
                new Date(entity.getStartTime().getTime() + 1000), 0, 10).size(), 1);

        Assert.assertEquals(subledgerDao.getBySellerId(entity.getSellerId(),
                PayoutStatus.PENDING, entity.getStartTime(),
                new Date(entity.getStartTime().getTime()), 0, 10).size(), 0);

    }

    @Test
    public void testFind() {
        SubledgerEntity entity = TestHelper.generateSubledgerEntity();
        entity.setPayoutStatus(PayoutStatus.PENDING);
        subledgerDao.create(entity);
        subledgerDao.flush();

        Assert.assertNotNull(subledgerDao.find(entity.getSellerId(),
                PayoutStatus.PENDING, entity.getStartTime(), entity.getProductItemId(), entity.getCountry(),
                entity.getCurrency()
                ));

        Assert.assertNull(subledgerDao.find(entity.getSellerId(),
                PayoutStatus.COMPLETED, entity.getStartTime(), entity.getProductItemId(), entity.getCountry(),
                entity.getCurrency()
        ));

        Assert.assertNull(subledgerDao.find(entity.getSellerId() + 1,
                PayoutStatus.PENDING, entity.getStartTime(), entity.getProductItemId(), entity.getCountry(),
                entity.getCurrency()
        ));

        Assert.assertNull(subledgerDao.find(entity.getSellerId(),
                PayoutStatus.PENDING, entity.getStartTime(), entity.getProductItemId(), entity.getCurrency(),
                entity.getCurrency()
        ));
    }
}
