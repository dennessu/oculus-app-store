/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.entitlement;

import com.junbo.test.common.apihelper.identity.impl.UserServiceImpl;
import com.junbo.test.common.apihelper.oauth.impl.OAuthServiceImpl;
import com.junbo.test.entitlement.impl.EntitlementServiceImpl;
import com.junbo.test.common.apihelper.oauth.enums.GrantType;
import com.junbo.test.common.apihelper.identity.UserService;
import com.junbo.test.catalog.impl.ItemRevisionServiceImpl;
import com.junbo.test.common.apihelper.oauth.OAuthService;
import com.junbo.test.common.Entities.enums.ComponentType;
import com.junbo.test.catalog.enums.CatalogEntityStatus;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.test.catalog.enums.EntitlementType;
import com.junbo.test.catalog.impl.OfferServiceImpl;
import com.junbo.test.catalog.impl.ItemServiceImpl;
import com.junbo.test.catalog.ItemRevisionService;
import com.junbo.test.common.Utility.TestClass;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.identity.spec.v1.model.User;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.common.id.OfferRevisionId;
import com.junbo.test.catalog.OfferService;
import com.junbo.test.catalog.ItemService;
import com.junbo.common.id.EntitlementId;
import com.junbo.test.common.property.*;
import com.junbo.common.model.Results;

import static org.testng.AssertJUnit.*;
import org.testng.annotations.Test;
import org.testng.Assert;

/**
 * @author jifeng
 * Time: 4/1/2014
 * For testing entitlement API
 */
public class EntitlementTesting extends TestClass {

    private LogHelper logger = new LogHelper(EntitlementTesting.class);
    private String developerItemId = "-1";
    String defaultUserId;
    String developerUserId;
    User defaultUser;
    User developerUser;

    private void prepareTestData() throws Exception {
        UserService userService = UserServiceImpl.instance();
        defaultUserId = userService.PostUser();
        developerUserId = userService.PostUser();

        defaultUser = Master.getInstance().getUser(defaultUserId);
        developerUser = Master.getInstance().getUser(developerUserId);

        OAuthService oAuthService = OAuthServiceImpl.getInstance();
        oAuthService.postAccessToken(GrantType.CLIENT_CREDENTIALS, ComponentType.ENTITLEMENT);
    }

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
        this.prepareTestData();
        logger.LogSample("post a DOWNLOAD type entitlement");
        Entitlement etCreated = this.CreateEntitlement(defaultUser, EntitlementType.DOWNLOAD.getType());
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
        this.prepareTestData();
        Entitlement etCreated = this.CreateEntitlement(defaultUser, EntitlementType.DOWNLOAD.getType());
        logger.LogSample("get entitlement by entitlementId");
        EntitlementService entitlementService = EntitlementServiceImpl.instance();
        Master.getInstance().setCurrentUid(defaultUserId);
        Entitlement etGet = entitlementService.getEntitlement(IdConverter.idToHexString(new EntitlementId(etCreated.getId())));
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
        this.prepareTestData();
        Entitlement etCreated = this.CreateEntitlement(defaultUser, EntitlementType.DOWNLOAD.getType());
        String entitlementId = IdConverter.idToHexString(new EntitlementId(etCreated.getId()));
        EntitlementService entitlementService = EntitlementServiceImpl.instance();
        Master.getInstance().setCurrentUid(defaultUserId);
        Entitlement etGet = entitlementService.getEntitlement(entitlementId);
        assertTrue(!etGet.getIsBanned());
        etGet.setIsBanned(true);
        logger.LogSample("update Entitlement's banned status from false to true");
        Entitlement etupdated = entitlementService.updateEntitlement(entitlementId, etGet);
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
        this.prepareTestData();
        Entitlement etCreated = this.CreateEntitlement(defaultUser, EntitlementType.DOWNLOAD.getType());
        String entitlementId = IdConverter.idToHexString(new EntitlementId(etCreated.getId()));
        logger.LogSample("delete an entitlement");
        EntitlementService entitlementService = EntitlementServiceImpl.instance();
        entitlementService.deleteEntitlement(entitlementId);
        //verify entitlement not found returned after deletion
        logger.LogSample("get an non-existing entitlement");
        try {
            Entitlement etGet = entitlementService.getEntitlement(entitlementId, 404);
            Assert.fail("Shouldn't get items with wrong id");
        } catch (Exception ex) {
            logger.logInfo("Expected exception - could not find the deleted entitlement");
        }

        //delete again, ENTITLEMENT_NOT_FOUND should be returned
        logger.LogSample("delete a non-existing entitlement");
        entitlementService.deleteEntitlement(entitlementId, 404);
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
        this.prepareTestData();

        UserService userService = UserServiceImpl.instance();
        String developerId2 = userService.PostUser();
        User developer2 = Master.getInstance().getUser(developerId2);

        this.createDeveloperEntitlement(developerUser);
        this.createDeveloperEntitlement(developer2);

        this.CreateEntitlement(defaultUser, EntitlementType.DOWNLOAD.getType());
        this.CreateEntitlement(defaultUser, EntitlementType.DOWNLOAD.getType());
        String userId = IdConverter.idToHexString(defaultUser.getId());
        logger.LogSample("Get entitlement by userId");
        EntitlementService entitlementService = EntitlementServiceImpl.instance();
        Master.getInstance().setCurrentUid(developerId2);
        Results<Entitlement> etGets = entitlementService.getEntitlements(developerId2);
        assertEquals("One entitlement should be returned", 1, etGets.getItems().size());
    }

    //help function
    private void createDeveloperEntitlement(User user) throws Exception {
        Entitlement developerEntitlement = new Entitlement();
        developerEntitlement.setUserId(user.getId().getValue());
        if (this.developerItemId.equals("-1")) {
            postDeveloperItem();
        }
        developerEntitlement.setItemId(developerItemId);
        developerEntitlement.setType(EntitlementType.DEVELOPER.getType());

        EntitlementService entitlementService = EntitlementServiceImpl.instance();
        entitlementService.grantEntitlement(developerEntitlement);
    }

    private Entitlement CreateEntitlement(User user, String entitlementType) throws Exception {
        Entitlement entitlement = new Entitlement();
        entitlement.setUserId(user.getId().getValue());
        entitlement.setType(entitlementType);
        if (entitlementType.equalsIgnoreCase(EntitlementType.DOWNLOAD.getType()))
        {
            OfferService offerClient = OfferServiceImpl.instance();
            String offerId = offerClient.getOfferIdByName("testOffer_CartCheckout_digital1");
            String orId = IdConverter.idToUrlString(
                    OfferRevisionId.class, Master.getInstance().getOffer(offerId).getCurrentRevisionId());
            String itemId = Master.getInstance().getOfferRevision(orId).getItems().get(0).getItemId();
            entitlement.setItemId(itemId);
        }
        EntitlementService entitlementService = EntitlementServiceImpl.instance();
        return entitlementService.grantEntitlement(entitlement);
    }

    private void postDeveloperItem() throws Exception {
        ItemService itemService = ItemServiceImpl.instance();
        Item testItem = itemService.prepareItemEntity("developerItem");
        Item itemPosted = itemService.postItem(testItem);

        ItemRevisionService itemRevisionService = ItemRevisionServiceImpl.instance();
        ItemRevision itemRevision = itemRevisionService.prepareItemRevisionEntity("developerItemRevision");
        itemRevision.setItemId(itemPosted.getItemId());
        itemRevision.setOwnerId(itemPosted.getOwnerId());
        ItemRevision itemRevisionPosted = itemRevisionService.postItemRevision(itemRevision);

        //Approve the item revision
        itemRevisionPosted.setStatus(CatalogEntityStatus.APPROVED.getEntityStatus());
        ItemRevision itemRevisionPut = itemRevisionService.updateItemRevision(itemRevisionPosted.getRevisionId(),itemRevisionPosted);
        this.developerItemId = itemRevisionPut.getItemId();
    }

}
