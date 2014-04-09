/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.catalog.db.repo;

import com.junbo.catalog.db.BaseTest;
import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition;
import com.junbo.catalog.spec.model.entitlementdef.EntitlementType;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EntitlementDefinitionRepoTest extends BaseTest {
    @Autowired
    private EntitlementDefinitionRepository definitionRepository;

    @Test
    public void testCreate() {
        EntitlementDefinition definition = buildADefinition();
        Long id = definitionRepository.create(definition);
        EntitlementDefinition createdDefinition = definitionRepository.get(id);
        Assert.assertNotNull(createdDefinition);
    }

    private EntitlementDefinition buildADefinition() {
        EntitlementDefinition definition = new EntitlementDefinition();
        definition.setDeveloperId(generateId());
        definition.setTag("TEST");
        definition.setGroup("TEST");
        definition.setConsumable(true);
        definition.setType(EntitlementType.DEFAULT.toString());
        return definition;
    }
}
