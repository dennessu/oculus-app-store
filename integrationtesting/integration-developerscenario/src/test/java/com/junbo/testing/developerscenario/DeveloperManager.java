/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.testing.developerscenario;

import com.junbo.testing.common.apihelper.catalog.OfferService;
import com.junbo.testing.common.apihelper.catalog.impl.OfferServiceImpl;
import com.junbo.testing.common.apihelper.entitlement.EntitlementService;
import com.junbo.testing.common.apihelper.entitlement.impl.EntitlementServiceImpl;
import com.junbo.testing.common.apihelper.identity.UserService;
import com.junbo.testing.common.apihelper.identity.impl.UserServiceImpl;
import com.junbo.testing.common.libs.LogHelper;
import com.junbo.testing.common.property.Component;
import com.junbo.testing.common.property.Priority;
import com.junbo.testing.common.property.Property;
import com.junbo.testing.common.property.Status;
import org.junit.Test;

/**
 * Created by JieFeng on 3/7/14.
 */
public class DeveloperManager {

    private LogHelper logger = new LogHelper(DeveloperManager.class);

    @Property(
            priority = Priority.BVT,
            features = "developerscenario",
            component = Component.Identity,
            owner = "JieFeng",
            status = Status.Incomplete,
            description = "developer registration and offer uploading",
            steps = {

            }
    )
    @Test
    public void testDeveloperRegistrationAndOfferLoading() throws Exception {
        UserService us = UserServiceImpl.instance();
        EntitlementService es = EntitlementServiceImpl.getInstance();
        OfferService os = OfferServiceImpl.instance();

        //create an user1

        //create another user2 to similate partner user(entitlement's developerId)

        //registraer user1 as a developer of user2 by granting entitlement

        //check user1's entitlement to verify developer registration

        //user1 upload an offer as a developer

        //check user1's offer

    }
}
