/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.entitlement;

import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.common.id.EntitlementId;
import com.junbo.common.id.OfferRevisionId;
import com.junbo.common.model.Results;
import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.identity.spec.v1.model.User;
import com.junbo.test.catalog.ItemRevisionService;
import com.junbo.test.catalog.ItemService;
import com.junbo.test.catalog.OfferService;
import com.junbo.test.catalog.enums.CatalogEntityStatus;
import com.junbo.test.catalog.enums.EntitlementType;
import com.junbo.test.catalog.impl.ItemRevisionServiceImpl;
import com.junbo.test.catalog.impl.ItemServiceImpl;
import com.junbo.test.catalog.impl.OfferServiceImpl;
import com.junbo.test.common.HttpclientHelper;
import com.junbo.test.common.Utility.TestClass;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import com.junbo.test.identity.Identity;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.*;

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
    private String developerItemId = "-1";

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
        User us = Identity.UserPostDefault();
        User dp = this.createDeveloper();
        logger.LogSample("post a DOWNLOAD type entitlement");
        Entitlement etCreated = this.CreateEntitlement(us, dp, EntitlementType.DOWNLOAD.getType());
        assertNotNull("return entitlement should not be null", etCreated);
        assertTrue(etCreated.getIsActive());
    }

    @Property(
            priority = Priority.Dailies,
            features = "Get /Entitlements",
            component = Component.Entitlement,
            owner = "JieFeng",
            status = Status.Enable,
            description = "get entitlement",
            steps = {
                    "1. post a developer1" +
                            "/n 2. post an download entitlement definition for developer1" +
                            "/n 3. create an customer" +
                            "/n 4. post an entitlement with entitlement definition in step2 for customer" +
                            "/n 5. Get  entitlement"
            }
    )
    @Test
    public void testGetEntitlementById() throws Exception {
        User us = Identity.UserPostDefault();
        User dp = this.createDeveloper();
        Entitlement etCreated = this.CreateEntitlement(us, dp, EntitlementType.DOWNLOAD.getType());
        logger.LogSample("get entitlement by entitlementId");
        //Entitlement etGet = EntitlementService.getEntitlement(IdConverter.idToHexString(new EntitlementId(etCreated.getEntitlementId())));
        Entitlement etGet = EntitlementService.getEntitlement(IdConverter.idToHexString(new EntitlementId(etCreated.getId())));
        assertEquals("validate userId in entitlement is correct",
                etCreated.getUserId(), etGet.getUserId());
        // assertEquals("validate userId in entitlement definition is correct",
        // etCreated., etGet.getEntitlementDefinitionId());
    }

    @Property(
            priority = Priority.Dailies,
            features = "Put /Entitlements",
            component = Component.Entitlement,
            owner = "JieFeng",
            status = Status.Enable,
            description = "update entitlement's banned status from false to true",
            steps = {
                    "1. post an entitlement" +
                            "/n 2. check entitlement's banned status = false" +
                            "/n 3. update entitlement's banned status from false to true"
            }
    )
    @Test
    public void testUpdateEntitlementStatus() throws Exception {
        User us = Identity.UserPostDefault();
        User dp = this.createDeveloper();
        Entitlement etCreated = this.CreateEntitlement(us, dp, EntitlementType.DOWNLOAD.getType());
        String entitlementId = IdConverter.idToHexString(new EntitlementId(etCreated.getId()));
        Entitlement etGet = EntitlementService.getEntitlement(entitlementId);
        assertTrue(!etGet.getIsBanned());
        etGet.setIsBanned(true);
        logger.LogSample("update Entitlement's banned status from false to true");
        Entitlement etupdated = EntitlementService.updateEntitlement(entitlementId, etGet);
        assertTrue(etupdated.getIsBanned());
    }

    @Property(
            priority = Priority.Dailies,
            features = "Delete /Entitlements",
            component = Component.Entitlement,
            owner = "JieFeng",
            status = Status.Enable,
            description = "delete an entitlement",
            steps = {
                    "1. post an entitlement" +
                            "/n 2. delete entitlement by entitlement Id" +
                            "/n 3. check entitlement was deleted already"
            }
    )
    @Test
    public void testDeleteEntitlement() throws Exception {
        User us = Identity.UserPostDefault();
        User dp = this.createDeveloper();
        Entitlement etCreated = this.CreateEntitlement(us, dp, EntitlementType.DOWNLOAD.getType());
        String entitlementId = IdConverter.idToHexString(new EntitlementId(etCreated.getId()));
        logger.LogSample("delete an entitlement");
        EntitlementService.deleteEntitlement(entitlementId);
        //verify entitlement not found returned after deletion
        logger.LogSample("get an non-existing entitlement");
        Entitlement etGet = EntitlementService.getEntitlement(entitlementId, 404);
        assertNull(etGet);
        //delete again, ENTITLEMENT_NOT_FOUND should be returned
        logger.LogSample("delete an non-existing entitlement");
        EntitlementService.deleteEntitlement(entitlementId, 404);
    }

    @Property(
            priority = Priority.Dailies,
            features = "GET /users/{userId}/entitlements?developerId={developerId}...",
            component = Component.Entitlement,
            owner = "JieFeng",
            status = Status.Enable,
            description = "get entitlement by userId and developerId",
            steps = {
                    "1. post two entitlements to one user with different developers" +
                            "/n 2. get entitlement by userId and developerId" +
                            "/n 3. verification"
            }
    )
    @Test
    public void testGetEntitlementByUserId() throws Exception {
        User us = Identity.UserPostDefault();
        User dp1 = this.createDeveloper();
        User dp2 = this.createDeveloper();
        this.CreateEntitlement(us, dp1, EntitlementType.DOWNLOAD.getType());
        this.CreateEntitlement(us, dp2, EntitlementType.DOWNLOAD.getType());
        String userId = IdConverter.idToHexString(us.getId());
        logger.LogSample("Get entitlement by userId");
        Results<Entitlement> etGets = EntitlementService.getEntitlements(userId);
        assertEquals("Two entitlements should be returned", 2, etGets.getItems().size());
    }

    //help function
    private User createDeveloper() throws Exception {
        User developerUser = Identity.UserPostDefault(); // create an developer
        Entitlement developerEntitlement = new Entitlement();
        developerEntitlement.setUserId(developerUser.getId().getValue());
        if (this.developerItemId.equals("-1")) {
            postDeveloperItem();
        }
        developerEntitlement.setItemId(developerItemId);
        developerEntitlement.setType(EntitlementType.DEVELOPER.getType());
        EntitlementService.grantEntitlement(developerEntitlement);
        return developerUser;
    }

    private Entitlement CreateEntitlement(User user, User developer, String entitlementType) throws Exception {
        Entitlement entitlement = new Entitlement();
        entitlement.setUserId(user.getId().getValue());
        entitlement.setType(entitlementType);
        if (entitlementType == EntitlementType.DOWNLOAD.getType()) ;
        {
            OfferService offerClient = OfferServiceImpl.instance();
            String offerId=offerClient.getOfferIdByName("testOffer_CartCheckout_digital1");
            String orId = IdConverter.idToUrlString(
                    OfferRevisionId.class, Master.getInstance().getOffer(offerId).getCurrentRevisionId());
            String itemId = Master.getInstance().getOfferRevision(orId).getItems().get(0).getItemId();
            entitlement.setItemId(itemId);
        }
        return EntitlementService.grantEntitlement(entitlement);
    }

    private void postDeveloperItem() throws Exception {
        ItemService itemService = ItemServiceImpl.instance();
        Item testItem = itemService.prepareItemEntity("developerItem");
        Item itemPosted = itemService.postItem(testItem);

        ItemRevisionService itemRevisionService = ItemRevisionServiceImpl.instance();
        ItemRevision itemRevision = itemRevisionService.prepareItemRevisionEntity("developerItemRevision");
        itemRevision.setItemId(itemPosted.getItemId());
        ItemRevision itemRevisionPosted = itemRevisionService.postItemRevision(itemRevision);

        //Approve the item revision
        itemRevisionPosted.setStatus(CatalogEntityStatus.APPROVED.getEntityStatus());
        ItemRevision itemRevisionPut = itemRevisionService.updateItemRevision(itemRevisionPosted.getRevisionId(),itemRevisionPosted);
        this.developerItemId = itemRevisionPut.getItemId();
    }

}
