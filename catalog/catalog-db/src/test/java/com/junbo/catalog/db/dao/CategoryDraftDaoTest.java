/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao;

import com.junbo.catalog.db.BaseTest;
import com.junbo.catalog.db.entity.CategoryDraftEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CategoryDraftDaoTest extends BaseTest {
    @Autowired
    private CategoryDraftDao categoryDraftDao;

    @Test
    public void testCreate() {
        CategoryDraftEntity entity = buildCategoryEntity();
        Long id = categoryDraftDao.create(entity);
        Assert.assertNotNull(entity.getId(), "Entity id should not be null.");
        Assert.assertNotNull(id, "Entity id should not be null.");
    }

    @Test
    public void testGet() {
        CategoryDraftEntity entity = buildCategoryEntity();
        categoryDraftDao.create(entity);
        Assert.assertNotNull(categoryDraftDao.get(entity.getId()), "Entity should not be null.");
    }

    @Test
    public void testUpdate() {
        CategoryDraftEntity entity = buildCategoryEntity();
        categoryDraftDao.create(entity);
        CategoryDraftEntity retrieved = categoryDraftDao.get(entity.getId());
        retrieved.setStatus("PUBLISHED");
        categoryDraftDao.update(retrieved);
        Assert.assertEquals(retrieved.getStatus(), "PUBLISHED", "Status should have changed.");
    }

    private CategoryDraftEntity buildCategoryEntity() {
        CategoryDraftEntity entity = new CategoryDraftEntity();
        entity.setId(generateId());
        entity.setName("test");
        entity.setStatus("test");
        entity.setRevision(1);
        entity.setParentId(generateId());
        entity.setPayload("{\"name\": \"test\"}");

        return entity;
    }
}
