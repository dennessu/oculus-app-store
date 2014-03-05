/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db;

import com.junbo.order.db.dao.SubledgerItemPayinEventDao;
import com.junbo.order.db.entity.SubledgerItemPayinEventEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by LinYi on 2/11/14.
 */
public class SubledgerItemPayinEventDaoTest extends BaseTest {
    @Autowired
    private SubledgerItemPayinEventDao subledgerItemPayinEventDao;

    @Test(enabled = false)
    public void testCreateAndRead() {
        SubledgerItemPayinEventEntity subledgerItemPayinEventEntity = generateSubledgerItemPayinEventEntity();
        Long id = subledgerItemPayinEventDao.create(subledgerItemPayinEventEntity);
        subledgerItemPayinEventDao.flush();
        SubledgerItemPayinEventEntity returnedEntity = subledgerItemPayinEventDao.read(id);

        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
        Assert.assertEquals(returnedEntity.getEventId(), subledgerItemPayinEventEntity.getEventId(),
                "The event Id should not be different.");
    }

    @Test(enabled = false)
    public void testUpdate() {
        SubledgerItemPayinEventEntity subledgerItemPayinEventEntity = generateSubledgerItemPayinEventEntity();
        Long id = subledgerItemPayinEventDao.create(subledgerItemPayinEventEntity);
        subledgerItemPayinEventDao.flush();
        //subledgerItemPayinEventEntity.setAction(OrderAction.CHARGE);
        subledgerItemPayinEventDao.update(subledgerItemPayinEventEntity);
        subledgerItemPayinEventDao.flush();
        SubledgerItemPayinEventEntity returnedEntity = subledgerItemPayinEventDao.read(id);

        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
        //Assert.assertEquals(returnedEntity.getAction(), subledgerItemPayinEventEntity.getAction(),
        // "The action Id should not be different.");
    }

    private SubledgerItemPayinEventEntity generateSubledgerItemPayinEventEntity() {
        SubledgerItemPayinEventEntity entity = new SubledgerItemPayinEventEntity();
        entity.setEventId(generateId());
        //entity.setAction(OrderAction.FULFILL);
        //entity.setStatus(OrderStatus.COMPLETED);
        entity.setSubledgerItemId(generateLong());
        return entity;
    }
}
