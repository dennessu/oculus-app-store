/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo;

import com.junbo.catalog.db.BaseTest;
import com.junbo.catalog.spec.model.attribute.ItemAttribute;
import com.junbo.catalog.spec.model.attribute.ItemAttributesGetOptions;
import com.junbo.catalog.spec.model.common.SimpleLocaleProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class ItemAttributeRepoTest extends BaseTest {
    @Autowired
    private ItemAttributeRepository attributeRepo;

    @Test
    public void testCreateAndGet() {
        ItemAttribute entity = buildAttributeEntity();
        Long id = attributeRepo.create(entity);
        Assert.assertNotNull(attributeRepo.get(id), "Entity should not be null.");
        List<ItemAttribute> attributeList = attributeRepo.getAttributes(new ItemAttributesGetOptions());
        System.out.println(attributeList.size());
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
