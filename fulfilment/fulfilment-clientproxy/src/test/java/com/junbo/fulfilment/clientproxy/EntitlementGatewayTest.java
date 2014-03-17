/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.clientproxy;

import com.junbo.fulfilment.spec.fusion.Entitlement;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Date;

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
        entitlement.setGrantDate(new Date());
        entitlement.setGroup("TEST_GROUP");
        entitlement.setType("TEST_TYPE");
        entitlement.setTag("TEST_TAG");

        String entitlementId = gateway.grant(entitlement);
        Assert.assertNotNull(entitlementId, "entitlementId should not be null.");
    }
}
