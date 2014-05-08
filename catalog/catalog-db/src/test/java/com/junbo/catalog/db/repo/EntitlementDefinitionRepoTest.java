/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.catalog.db.repo;

import com.junbo.catalog.db.BaseTest;
import com.junbo.catalog.spec.model.common.PageableGetOptions;
import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

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

    @Test
    public void testSearch(){
        EntitlementDefinition definition = buildADefinition();
        definitionRepository.create(definition);
        List<EntitlementDefinition> definitions = definitionRepository.getByParams(definition.getDeveloperId(), definition.getInAppContext().get(0), null, null, null, true, new PageableGetOptions().ensurePagingValid());
        Assert.assertEquals(definitions.size(), 1);
    }

    private EntitlementDefinition buildADefinition() {
        EntitlementDefinition definition = new EntitlementDefinition();
        definition.setDeveloperId(generateId());
        definition.setTag("TEST");
        definition.setItemId(generateId());
        definition.setConsumable(true);
        List<String> inAppContext = new ArrayList<>();
        inAppContext.add("123");
        inAppContext.add("234");
        definition.setInAppContext(inAppContext);
        return definition;
    }
}
