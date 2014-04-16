/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db;

import com.junbo.common.id.OrderEventId;
import com.junbo.order.db.dao.SubledgerEventDao;
import com.junbo.order.db.entity.SubledgerEventEntity;
import com.junbo.order.spec.model.OrderEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by LinYi on 2/11/14.
 */
public class SubledgerEventDaoTest extends BaseTest {
    @Autowired
    private SubledgerEventDao subledgerEventDao;

    @Test(enabled = false)
    public void testCreateAndRead() {
        SubledgerEventEntity entity = generateSubledgerEventEntity();
        entity.setEventId(idGenerator.nextId(OrderEventId.class));
        Long id = subledgerEventDao.create(entity);
        SubledgerEventEntity returnedEntity = subledgerEventDao.read(id);

        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
        Assert.assertEquals(returnedEntity.getEventId(), entity.getEventId(),
                "The event Id should not be different.");
    }

    @Test(enabled = false)
    public void testUpdate() {
        SubledgerEventEntity entity = generateSubledgerEventEntity();
        entity.setEventId(idGenerator.nextId(OrderEventId.class));
        Long id = subledgerEventDao.create(entity);
        //entity.setAction(OrderAction.CHARGE);
        subledgerEventDao.update(entity);
        SubledgerEventEntity returnedEntity = subledgerEventDao.read(id);

        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
        Assert.assertEquals(returnedEntity.getEventId(), entity.getEventId(),
                "The action Id should not be different.");
    }

    private SubledgerEventEntity generateSubledgerEventEntity() {
        SubledgerEventEntity entity = new SubledgerEventEntity();
        entity.setEventId(generateId());
        //entity.setAction(OrderAction.FULFILL);
        //entity.setPayoutStatus(OrderStatus.COMPLETED);
        entity.setSubledgerId(generateLong());
        return entity;
    }
}
