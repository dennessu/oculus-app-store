/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.catalog.db.dao;

import com.junbo.catalog.db.BaseTest;
import com.junbo.catalog.db.entity.EntitlementDefinitionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collections;

public class EntitlementDefinitionDaoTest extends BaseTest {
    @Autowired
    private EntitlementDefinitionDao entitlementDefinitionDao;

    @Test
    public void testCreate() {
        EntitlementDefinitionEntity definitionEntity = buildADefinitionEntity();
        Long id = entitlementDefinitionDao.create(definitionEntity);
        EntitlementDefinitionEntity createdDefinition = entitlementDefinitionDao.get(id);
        Assert.assertNotNull(createdDefinition);
    }

    private EntitlementDefinitionEntity buildADefinitionEntity() {
        EntitlementDefinitionEntity definitionEntity = new EntitlementDefinitionEntity();
        definitionEntity.setDeveloperId(generateId());
        definitionEntity.setTag("TEST");
        definitionEntity.setGroup("TEST");
        definitionEntity.setConsumable(true);
        definitionEntity.setInAppContext(Collections.singletonList(String.valueOf(generateId())));
        return definitionEntity;
    }
}
