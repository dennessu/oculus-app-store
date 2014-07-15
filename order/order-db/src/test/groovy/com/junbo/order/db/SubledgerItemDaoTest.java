/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db;

import com.junbo.common.id.SubledgerId;
import com.junbo.common.id.SubledgerItemId;
import com.junbo.order.db.common.TestHelper;
import com.junbo.order.db.dao.SubledgerItemDao;
import com.junbo.order.db.entity.SubledgerItemEntity;
import com.junbo.sharding.ShardAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by LinYi on 2/11/14.
 */
public class SubledgerItemDaoTest extends BaseTest {
    @Autowired
    private SubledgerItemDao subledgerItemDao;

    @Autowired
    @Qualifier("userShardAlgorithm")
    private ShardAlgorithm shardAlgorithm;

    @Test
    public void testCreateAndRead() {
        SubledgerItemEntity entity = TestHelper.generateSubledgerItemEntity();
        entity.setSubledgerId(idGenerator.nextId(SubledgerId.class));
        entity.setSubledgerItemId(idGenerator.nextId(SubledgerItemId.class, entity.getSubledgerId()));
        Long id = subledgerItemDao.create(entity);
        SubledgerItemEntity returnedEntity = subledgerItemDao.read(id);

        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
        Assert.assertEquals(returnedEntity.getStatus(), entity.getStatus());
        Assert.assertEquals(returnedEntity.getSubledgerItemId(), entity.getSubledgerItemId(),
                "The subledger item Id should not be different.");
    }

    @Test
    public void testUpdate() {
        SubledgerItemEntity entity = TestHelper.generateSubledgerItemEntity();
        entity.setSubledgerId(idGenerator.nextId(SubledgerId.class));
        entity.setSubledgerItemId(idGenerator.nextId(SubledgerItemId.class, entity.getSubledgerId()));
        Long id = subledgerItemDao.create(entity);
        entity.setUpdatedBy(123L);
        subledgerItemDao.update(entity);
        SubledgerItemEntity returnedEntity = subledgerItemDao.read(id);

        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
        Assert.assertEquals(returnedEntity.getUpdatedBy(), entity.getUpdatedBy(),
                "The UpdatedBy field should not be different.");
    }

    @Test
    public void testGetByStatus() {
        SubledgerItemEntity entity = TestHelper.generateSubledgerItemEntity();
        entity.setSubledgerId(idGenerator.nextId(SubledgerId.class));
        entity.setSubledgerItemId(idGenerator.nextId(SubledgerItemId.class, entity.getSubledgerId()));
        subledgerItemDao.create(entity);
        Assert.assertEquals(subledgerItemDao.getByStatus(shardAlgorithm.dataCenterId(entity.getId()), shardAlgorithm.shardId(entity.getId()),
                entity.getStatus(), 0, 1).size(), 1);
        for (SubledgerItemEntity itemEntity : subledgerItemDao.getByStatus(shardAlgorithm.dataCenterId(entity.getId()), shardAlgorithm.shardId(entity.getId()),
                entity.getStatus(), 0, 1)) {
            Assert.assertEquals(itemEntity.getStatus(), entity.getStatus());
        }
    }
}
