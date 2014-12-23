/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.clientproxy;

import com.junbo.catalog.spec.enums.EntitlementType;
import com.junbo.fulfilment.spec.fusion.Entitlement;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

/**
 * EntitlementGatewayTest.
 */
public class EntitlementGatewayTest extends BaseTest {
    @Autowired
    private EntitlementGateway gateway;

    @Test(enabled = false)
    public void testBVT() {
        Entitlement entitlement = new Entitlement();
        entitlement.setType(EntitlementType.DOWNLOAD.toString());
        entitlement.setItemId("33570816L");
        entitlement.setUserId(33570816L);

        List<Entitlement> entitlements = Collections.singletonList(entitlement);

        String entitlementId = gateway.grant(entitlements).get(0);
        Assert.assertNotNull(entitlementId, "entitlementId should not be null.");
    }
}
