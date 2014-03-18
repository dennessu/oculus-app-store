/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.clientproxy;

import com.junbo.fulfilment.common.util.Utils;
import com.junbo.fulfilment.spec.fusion.Entitlement;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * EntitlementGatewayTest.
 */
public class EntitlementGatewayTest extends BaseTest {
    @Autowired
    private EntitlementGateway gateway;

    @Test(enabled = false)
    public void testBVT() {
        Entitlement entitlement = new Entitlement();
        entitlement.setUserId(12345L);
        entitlement.setOfferId(99999L);
        entitlement.setGrantDate(Utils.now());
        entitlement.setGroup("TEST_GROUP");
        entitlement.setType("DOWNLOAD");
        entitlement.setTag("TEST_TAG");
        entitlement.setEntitlementDefinitionId(12345L);
        entitlement.setDeveloperId(99999L);

        String entitlementId = gateway.grant(entitlement);
        Assert.assertNotNull(entitlementId, "entitlementId should not be null.");
    }
}
