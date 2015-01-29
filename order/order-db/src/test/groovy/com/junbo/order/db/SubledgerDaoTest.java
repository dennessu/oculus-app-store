/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db;

import com.junbo.common.id.SubledgerId;
import com.junbo.order.db.common.TestHelper;
import com.junbo.order.db.dao.SubledgerDao;
import com.junbo.order.db.entity.SubledgerEntity;
import com.junbo.order.spec.model.enums.PayoutStatus;
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
        SubledgerEntity entity = TestHelper.generateSubledgerEntity();
        entity.setSubledgerId(idGenerator.nextId(SubledgerId.class));
        Long id = subledgerDao.create(entity);
        SubledgerEntity returnedEntity = subledgerDao.read(id);

        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
        Assert.assertEquals(returnedEntity.getSubledgerId(), entity.getSubledgerId(),
                "The subledger Id should not be different.");
    }

    @Test
    public void testUpdate() {
        SubledgerEntity entity = TestHelper.generateSubledgerEntity();
        entity.setSubledgerId(idGenerator.nextId(SubledgerId.class));
        Long id = subledgerDao.create(entity);
        entity.setUpdatedBy(123L);
        subledgerDao.update(entity);
        SubledgerEntity returnedEntity = subledgerDao.read(id);

        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
        Assert.assertEquals(returnedEntity.getUpdatedBy(), entity.getUpdatedBy(),
                "The updatedBy should not be different.");
    }

    @Test
    public void testGetBySellorId() {
        SubledgerEntity entity = TestHelper.generateSubledgerEntity();
        entity.setSubledgerId(idGenerator.nextId(SubledgerId.class));
        entity.setSellerId(entity.getSubledgerId());
        entity.setPayoutStatus(PayoutStatus.PENDING);
        subledgerDao.create(entity);

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
        entity.setSubledgerId(idGenerator.nextId(SubledgerId.class));
        entity.setSellerId(entity.getSubledgerId());
        entity.setPayoutStatus(PayoutStatus.PENDING);
        subledgerDao.create(entity);

        Assert.assertNotNull(subledgerDao.find(entity.getSellerId(),
                PayoutStatus.PENDING, entity.getStartTime(), entity.getItemId(), entity.getKey(),
                entity.getCurrency(), entity.getCountry()
                ));

        Assert.assertNull(subledgerDao.find(entity.getSellerId(),
                PayoutStatus.COMPLETED, entity.getStartTime(), entity.getItemId(), entity.getCurrency(),
                entity.getKey(), entity.getCountry()
        ));

        Assert.assertNull(subledgerDao.find(entity.getSellerId() + 1,
                PayoutStatus.PENDING, entity.getStartTime(), entity.getItemId(), entity.getKey(),
                entity.getCurrency(), entity.getCountry()
        ));

        Assert.assertNull(subledgerDao.find(entity.getSellerId(),
                PayoutStatus.PENDING, entity.getStartTime(), entity.getItemId(), entity.getKey(),
                entity.getCurrency(), entity.getCurrency()
        ));
    }
}
