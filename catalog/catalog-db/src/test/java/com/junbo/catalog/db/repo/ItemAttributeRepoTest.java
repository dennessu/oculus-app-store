/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo;

import com.junbo.catalog.db.BaseTest;
import com.junbo.catalog.spec.model.attribute.ItemAttribute;
import com.junbo.catalog.spec.model.common.SimpleLocaleProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ItemAttributeRepoTest extends BaseTest {
    @Autowired
    @Qualifier("itemAttributeCloudantRepo")
    private ItemAttributeRepository attributeRepo;

    @Test(enabled = false)
    public void testCreateAndGet() {
        ItemAttribute entity = buildAttributeEntity();
        ItemAttribute attribute = attributeRepo.create(entity);
        Assert.assertNotNull(attribute, "Entity should not be null.");
        //List<ItemAttribute> attributeList = attributeRepo.getAttributes(new ItemAttributesGetOptions());
        //System.out.println(attributeList.size());
    }

    private ItemAttribute buildAttributeEntity() {
        ItemAttribute entity = new ItemAttribute();
        entity.setId(generateId());
        entity.setType("Color");
        SimpleLocaleProperties localeProperties = new SimpleLocaleProperties();
        localeProperties.setName("name");
        localeProperties.setDescription("description");

        return entity;
    }
}
