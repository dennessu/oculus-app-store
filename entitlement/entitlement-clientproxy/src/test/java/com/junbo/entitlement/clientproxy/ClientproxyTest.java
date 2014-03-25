/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.clientproxy;

import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition;
import com.junbo.entitlement.clientproxy.catalog.EntitlementDefinitionFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Random;

/**
 * client proxy test.
 */
@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
public class ClientproxyTest extends AbstractTestNGSpringContextTests {
    @Autowired
    private EntitlementDefinitionFacade definitionFacade;

    @Test(enabled = false)
    public void testUserFacade() {
        EntitlementDefinition definition = definitionFacade.getDefinition(new Random().nextLong());
        Assert.assertNull(definition);
    }

}
