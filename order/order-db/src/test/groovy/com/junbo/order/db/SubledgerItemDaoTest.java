/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db;

import com.junbo.order.db.common.TestHelper;
import com.junbo.order.db.dao.SubledgerItemDao;
import com.junbo.order.db.entity.SubledgerItemEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by LinYi on 2/11/14.
 */
public class SubledgerItemDaoTest extends BaseTest {
    @Autowired
    private SubledgerItemDao subledgerItemDao;

    @Test
    public void testCreateAndRead() {
        SubledgerItemEntity subledgerItemEntity = TestHelper.generateSubledgerItemEntity();
        Long id = subledgerItemDao.create(subledgerItemEntity);
        subledgerItemDao.flush();
        SubledgerItemEntity returnedEntity = subledgerItemDao.read(id);

        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
        Assert.assertEquals(returnedEntity.getSubledgerItemId(), subledgerItemEntity.getSubledgerItemId(),
                "The subledger item Id should not be different.");
    }

    @Test
    public void testUpdate() {
        SubledgerItemEntity subledgerItemEntity = TestHelper.generateSubledgerItemEntity();
        Long id = subledgerItemDao.create(subledgerItemEntity);
        subledgerItemDao.flush();
        subledgerItemEntity.setUpdatedBy("ANOTHER");
        subledgerItemDao.update(subledgerItemEntity);
        subledgerItemDao.flush();
        SubledgerItemEntity returnedEntity = subledgerItemDao.read(id);

        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
        Assert.assertEquals(returnedEntity.getUpdatedBy(), subledgerItemEntity.getUpdatedBy(),
                "The UpdatedBy field should not be different.");
    }

    @Test
    public void testGetByStatus() {
        SubledgerItemEntity subledgerItemEntity = TestHelper.generateSubledgerItemEntity();
        subledgerItemDao.create(subledgerItemEntity);
        subledgerItemDao.flush();
        Assert.assertEquals(subledgerItemDao.getByStatus(subledgerItemEntity.getStatus(), 0, 1).size(), 1);
    }
}
