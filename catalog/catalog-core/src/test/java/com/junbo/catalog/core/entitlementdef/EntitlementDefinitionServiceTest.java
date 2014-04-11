/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.catalog.core.entitlementdef;

import com.junbo.catalog.core.EntitlementDefinitionService;
import com.junbo.catalog.core.BaseTest;
import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition;
import com.junbo.catalog.spec.model.entitlementdef.EntitlementType;
import junit.framework.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.util.Collections;

public class EntitlementDefinitionServiceTest extends BaseTest {
    @Autowired
    private EntitlementDefinitionService entitlementDefinitionService;

    @Test
    public void testCreate(){
        EntitlementDefinition definition = buildADefinition();
        Long id = entitlementDefinitionService.createEntitlementDefinition(definition);
        EntitlementDefinition createdDefinition = entitlementDefinitionService.getEntitlementDefinition(id);
        Assert.assertNotNull(createdDefinition);

    }

    private EntitlementDefinition buildADefinition() {
        EntitlementDefinition definition = new EntitlementDefinition();
        definition.setDeveloperId(generateId());
        definition.setInAppContext(Collections.singletonList(String.valueOf(generateId())));
        definition.setTag("TEST");
        definition.setGroup("TEST");
        definition.setConsumable(true);
        definition.setType(EntitlementType.DEFAULT.toString());
        return definition;
    }

}
