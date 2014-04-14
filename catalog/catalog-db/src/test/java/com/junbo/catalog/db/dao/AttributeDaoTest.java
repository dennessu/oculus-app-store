/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao;

import com.junbo.catalog.db.BaseTest;
import com.junbo.catalog.db.entity.AttributeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AttributeDaoTest extends BaseTest {
    @Autowired
    private AttributeDao attributeDao;

    @Test
    public void testCreateAndGet() {
        AttributeEntity entity = buildAttributeEntity();
        Long id = attributeDao.create(entity);
        Assert.assertNotNull(attributeDao.get(id), "Entity should not be null.");
    }

    private AttributeEntity buildAttributeEntity() {
        AttributeEntity entity = new AttributeEntity();
        entity.setId(generateId());
        entity.setName("{\"locales\":{\"DEFAULT\":\"test\"}}");
        entity.setType("Color");
        entity.setPayload("{}");

        return entity;
    }
}
