/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.developerscenario;

import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.common.id.UserId;
import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.test.common.Utility.TestClass;
import com.junbo.test.common.apihelper.catalog.OfferService;
import com.junbo.test.common.apihelper.catalog.impl.OfferServiceImpl;
import com.junbo.test.common.apihelper.entitlement.EntitlementService;
import com.junbo.test.common.apihelper.entitlement.impl.EntitlementServiceImpl;
import com.junbo.test.common.apihelper.identity.UserService;
import com.junbo.test.common.apihelper.identity.impl.UserServiceImpl;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.EnumHelper;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Created by JieFeng on 3/7/14.
 */
public class DeveloperManager extends TestClass {

    private LogHelper logger = new LogHelper(DeveloperManager.class);

    @Property(
            priority = Priority.BVT,
            features = "developerscenario",
            component = Component.Entitlement,
            owner = "JieFeng",
            status = Status.Enable,
            description = "developer registration and offer uploading",
            steps = {
                    "1. Create user1 " +
                            "/n 2. Create User2 to simular partner user" +
                            "/n 3. Register user1 as a developer user of user2 by grant entitlement" +
                            "/n 4. Validate entitlement's userId, developerId and entitlementType" +
                            "/n 5. Post an offer for developer user1 by setting corresponding ownerId and check offer"
            }
    )
    @Test
    public void testDeveloperRegistrationAndOfferLoading() throws Exception {

        UserService us = UserServiceImpl.instance();
        EntitlementService es = EntitlementServiceImpl.getInstance();
        OfferService os = OfferServiceImpl.instance();

        String entitlementType = "DEVELOPER";

        //create an user1
        String developerUser = us.PostUser();

        //create another user2 to simulate partner user(entitlement's developerId)
        String partnerUser = us.PostUser();

        //register user1 as a developer of user2 by granting entitlement
        Entitlement developerEntitlement = new Entitlement();
        developerEntitlement.setUserId(Master.getInstance().getUser(developerUser).getId().getValue());
        developerEntitlement.setDeveloperId(Master.getInstance().getUser(partnerUser).getId().getValue());
        developerEntitlement.setType(entitlementType);

        logger.LogSample("grant Developer entitlement for user");
        String entitlementId = es.grantEntitlement(developerEntitlement);

        //check user1's entitlement to verify developer registration
        List<String> rtnEntitlements = es.getEntitlements(developerUser, partnerUser);
        Assert.assertTrue(rtnEntitlements.size()==1);
        Entitlement rtnEntitlement = Master.getInstance().getEntitlement(rtnEntitlements.get(0));

        Assert.assertTrue(rtnEntitlement.getUserId().equals(Master.getInstance().getUser(developerUser).getId().getValue()));
        Assert.assertTrue(rtnEntitlement.getDeveloperId().equals(Master.getInstance().getUser(partnerUser).getId().getValue()));
        Assert.assertEquals(rtnEntitlement.getType(), entitlementType);
        //user1 upload an offer as a developer

        String offerName = "defaultOffer";
        Offer offerToPost = os.prepareOfferEntity(offerName, EnumHelper.CatalogItemType.APP);
        offerToPost.setOwnerId(Master.getInstance().getUser(developerUser).getId().getValue());
        logger.LogSample("post an offer for a developer");
        String offerId = os.postOffer(offerToPost);

        //check user1's offer ownerId
        Assert.assertEquals(IdConverter.idToHexString(new UserId(Master.getInstance().getOffer(offerId).getOwnerId())), developerUser);
    }
}
