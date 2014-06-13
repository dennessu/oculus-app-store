/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao;

import com.junbo.catalog.db.BaseTest;
import com.junbo.catalog.db.entity.ItemAttributeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AttributeDaoTest extends BaseTest {
    @Autowired
    private ItemAttributeDao attributeDao;

    @Test
    public void testCreateAndGet() {
        ItemAttributeEntity entity = buildAttributeEntity();
        String id = attributeDao.create(entity);
        Assert.assertNotNull(attributeDao.get(id), "Entity should not be null.");
    }

    private ItemAttributeEntity buildAttributeEntity() {
        ItemAttributeEntity entity = new ItemAttributeEntity();
        entity.setId(generateId());
        entity.setType("Color");
        entity.setPayload("{}");

        return entity;
    }
}
