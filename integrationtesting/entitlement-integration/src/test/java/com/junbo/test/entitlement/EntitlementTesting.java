/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.entitlement;

import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition;
import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.identity.spec.v1.model.User;
import com.junbo.test.common.HttpclientHelper;
import com.junbo.test.common.Utility.TestClass;
import com.junbo.test.common.apihelper.catalog.EntitlementDefinitionService;
import com.junbo.test.common.apihelper.catalog.impl.EntitlementDefinitionServiceImpl;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.EnumHelper;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import com.junbo.test.identity.Identity;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Created by jiefeng on 14-4-1.
 */
public class EntitlementTesting extends TestClass {
    @BeforeMethod
    public void setup() {
        HttpclientHelper.CreateHttpClient();
    }

    @AfterMethod
    public void dispose() throws Exception {
        HttpclientHelper.CloseHttpClient();
    }
    private LogHelper logger = new LogHelper(EntitlementTesting.class);

    @Property(
            priority = Priority.Dailies,
            features = "Post /Entitlements",
            component = Component.Entitlement,
            owner = "JieFeng",
            status = Status.Enable,
            description = "Create a download entitlement",
            steps = {
                    "1. post a developer1" +
                            "/n 2. post an download entitlement definition for developer1" +
                            "/n 3. create an customer" +
                            "/n 4. post an entitlement with entitlement definition in step2 for customer"
            }
    )
    @Test
    public void testPostEntitlement() throws Exception {
        EntitlementDefinitionService eds = EntitlementDefinitionServiceImpl.instance();
        User dp = EntitlementService.createDeveloper();
        User us = Identity.DefaultPostUser();
        Entitlement entitlement= new Entitlement();
        EntitlementDefinition ed = new EntitlementDefinition();
        ed.setType(EnumHelper.EntitlementType.DOWNLOAD.getType());
        ed.setDeveloperId(dp.getId().getValue());
        String edsposted = eds.postEntitlementDefinition(ed);
        entitlement.setEntitlementDefinitionId(Master.getInstance().getEntitlementDefinition(edsposted).getEntitlementDefId());
        entitlement.setUserId(us.getId().getValue());
        logger.LogSample("post a DOWNLOAD type entitlement");
        EntitlementService.grantEntitlement(entitlement);
    }
}
