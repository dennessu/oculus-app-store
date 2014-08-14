/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.entitlement;

import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.common.id.EntitlementId;
import com.junbo.common.id.ItemId;
import com.junbo.common.id.OfferRevisionId;
import com.junbo.common.id.UserId;
import com.junbo.common.model.Results;
import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.entitlement.spec.model.EntitlementSearchParam;
import com.junbo.identity.spec.v1.model.User;
import com.junbo.test.catalog.ItemRevisionService;
import com.junbo.test.catalog.ItemService;
import com.junbo.test.catalog.OfferService;
import com.junbo.test.catalog.enums.CatalogEntityStatus;
import com.junbo.test.catalog.enums.EntitlementType;
import com.junbo.test.catalog.impl.ItemRevisionServiceImpl;
import com.junbo.test.catalog.impl.ItemServiceImpl;
import com.junbo.test.catalog.impl.OfferServiceImpl;
import com.junbo.test.common.Entities.enums.ComponentType;
import com.junbo.test.common.Utility.TestClass;
import com.junbo.test.common.apihelper.identity.UserService;
import com.junbo.test.common.apihelper.identity.impl.UserServiceImpl;
import com.junbo.test.common.apihelper.oauth.OAuthService;
import com.junbo.test.common.apihelper.oauth.enums.GrantType;
import com.junbo.test.common.apihelper.oauth.impl.OAuthServiceImpl;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import com.junbo.test.entitlement.impl.EntitlementServiceImpl;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.testng.AssertJUnit.*;

/**
 * @author jifeng
 *         Time: 4/1/2014
 *         For testing entitlement API
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

    @Property(
            priority = Priority.Dailies,
            features = "GET entitlements?userId={userId}...",
            component = Component.Entitlement,
            owner = "NanXin",
            status = Status.Enable,
            description = "get entitlement by userId and other params",
            steps = {
                    "1. post 4 entitlements to one user with different params" +
                            "/n 2. get entitlement by userId and other params" +
                            "/n 3. verification"
            }
    )
    @Test
    public void testSearchEntitlement() throws Exception {
        this.prepareTestData();

        UserService userService = UserServiceImpl.instance();
        User user = Master.getInstance().getUser(userService.PostUser());

        ItemId itemId = new ItemId(this.CreateEntitlement(user, EntitlementType.DOWNLOAD.getType()).getItemId());
        this.CreateEntitlement(user, EntitlementType.DOWNLOAD.getType());
        this.CreateEntitlement(user, EntitlementType.RUN.getType());
        this.CreateEntitlement(user, EntitlementType.RUN.getType());
        this.CreateEntitlement(user, EntitlementType.RUN.getType());
        UserId userId = user.getId();

        EntitlementSearchParam param = new EntitlementSearchParam();
        EntitlementService entitlementService = EntitlementServiceImpl.instance();

        logger.LogSample("Get entitlement by userId");
        param.setUserId(userId);
        Results<Entitlement> etGets = entitlementService.searchEntitlements(param, null, null);
        assertEquals("5 entitlement should be returned", 5, etGets.getItems().size());

        logger.LogSample("Get entitlement by userId and isActive");
        param.setIsActive(Boolean.TRUE);
        etGets = entitlementService.searchEntitlements(param, null, null);
        assertEquals("5 entitlement should be returned", 5, etGets.getItems().size());

        logger.LogSample("Get entitlement by userId and isSuspended");
        param.setIsBanned(Boolean.TRUE);
        etGets = entitlementService.searchEntitlements(param, null, null, 400);
        param.setIsActive(null);
        etGets = entitlementService.searchEntitlements(param, null, null);
        assertEquals("no entitlement should be returned", 0, etGets.getItems().size());

        logger.LogSample("Get entitlement by userId and type");
        param.setIsBanned(null);
        param.setType(EntitlementType.DOWNLOAD.toString());
        etGets = entitlementService.searchEntitlements(param, null, null);
        assertEquals("2 entitlement should be returned", 2, etGets.getItems().size());

        logger.LogSample("Get entitlement by userId and itemId");
        param.setType(null);
        param.setItemIds(Collections.singleton(itemId));
        etGets = entitlementService.searchEntitlements(param, null, null);
        assertEquals("5 entitlement should be returned", 5, etGets.getItems().size());

        logger.LogSample("Get entitlement by userId, startGrantTime and endGrantTime");
        param.setType(null);
        param.setStartGrantTime("2013-07-07T23:24:21Z");
        param.setEndGrantTime("2100-07-07T23:24:21Z");
        etGets = entitlementService.searchEntitlements(param, null, null);
        assertEquals("5 entitlement should be returned", 5, etGets.getItems().size());
        param.setEndGrantTime("2013-08-07T23:24:21Z");
        etGets = entitlementService.searchEntitlements(param, null, null);
        assertEquals("no entitlement should be returned", 0, etGets.getItems().size());

        logger.LogSample("Get entitlement by userId, startExpirationTime and endExpirationTime");
        param.setEndGrantTime("2100-07-07T23:24:21Z");
        param.setStartExpirationTime("2100-07-07T23:24:21Z");
        etGets = entitlementService.searchEntitlements(param, null, null);
        assertEquals("5 entitlement should be returned", 5, etGets.getItems().size());
        param.setEndExpirationTime("2102-07-07T23:24:21Z");
        etGets = entitlementService.searchEntitlements(param, null, null);
        assertEquals("no entitlement should be returned", 0, etGets.getItems().size());

        logger.LogSample("Get entitlement by userId and lastModifiedTime");
        param.setEndExpirationTime(null);
        param.setLastModifiedTime("2013-07-07T23:24:21Z");
        etGets = entitlementService.searchEntitlements(param, null, null);
        assertEquals("5 entitlement should be returned", 5, etGets.getItems().size());
        param.setLastModifiedTime("2100-07-07T23:24:21Z");
        etGets = entitlementService.searchEntitlements(param, null, null);
        assertEquals("no entitlement should be returned", 0, etGets.getItems().size());

        logger.LogSample("Get entitlement by userId with page params");
        param = new EntitlementSearchParam();
        param.setUserId(userId);
        etGets = entitlementService.searchEntitlements(param, null, 2);
        assertEquals("2 entitlement should be returned", 2, etGets.getItems().size());
        String bookmark = parseBookmark(etGets.getNext().getHref());
        etGets = entitlementService.searchEntitlements(param, bookmark, 2);
        assertEquals("2 entitlement should be returned", 2, etGets.getItems().size());
        bookmark = parseBookmark(etGets.getNext().getHref());
        etGets = entitlementService.searchEntitlements(param, bookmark, 2);
        assertEquals("1 entitlement should be returned", 1, etGets.getItems().size());
    }

    private String parseBookmark(String href) {
        Matcher matcher = Pattern.compile(".*bookmark=(.*)&.*").matcher(href);
        if(matcher.matches()){
            return matcher.group(1);
        }
        return null;
    }

    @Property(
            priority = Priority.Dailies,
            features = "GET item-binary/{itemId}?entitlementId={entitlementId}&itemRevisionId={itemRevisionId}&platform={platform}",
            component = Component.Entitlement,
            owner = "NanXin",
            status = Status.Enable,
            description = "get downloadUrl",
            steps = {
                    "1. post an entitlement" +
                            "/n 2. check the downloadUrl for every platform with entitlementId" +
                            "/n 3. check the downloadUrl for PC with itemRevisionId"
            }
    )
    @Test
    public void testGetDownloadUrl() throws Exception {
        this.prepareTestData();
        Entitlement etCreated = this.CreateEntitlement(defaultUser, EntitlementType.DOWNLOAD.getType());
        EntitlementService entitlementService = EntitlementServiceImpl.instance();
        Master.getInstance().setCurrentUid(defaultUserId);

        logger.LogSample("get downloadUrl for http://d1aifagf6hhneo.cloudfront.net/binaries/sr51r1VTfeqZFaFF0ZXy_SpotifyInstaller.zip");
        String downloadUrl = entitlementService.getDownloadUrl(etCreated.getId(), etCreated.getItemId(), "PC");
        assertTrue("return downloadUrl should contain the signature part", downloadUrl.indexOf("Signature=") != -1);

        logger.LogSample("get downloadUrl for http://static.oculusvr.com/apk-tests/VRArcade.apk");
        downloadUrl = entitlementService.getDownloadUrl(etCreated.getId(), etCreated.getItemId(), "ANDROID");
        assertTrue("return downloadUrl should contain the signature part", downloadUrl.indexOf("Signature=") != -1);

        logger.LogSample("get downloadUrl for http://www.google.com/download/angrybird1_1mac.dmg");
        downloadUrl = entitlementService.getDownloadUrl(etCreated.getId(), etCreated.getItemId(), "MAC");
        assertTrue("return downloadUrl should not contain the signature part", downloadUrl.indexOf("Signature=") == -1);

        logger.LogSample("get downloadUrl for non-existing platform");
        entitlementService.getDownloadUrl(etCreated.getId(), etCreated.getItemId(), "LINUX", 400);

        ItemService itemClient = ItemServiceImpl.instance();
        String itemRevisionId = itemClient.getItem(etCreated.getItemId()).getCurrentRevisionId();
        logger.LogSample("get downloadUrl with specific itemRevisionId");
        downloadUrl = entitlementService.getDownloadUrlForItemRevision(itemRevisionId, etCreated.getItemId(), "PC");
        assertNotNull("return downloadUrl should not be null", downloadUrl);
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
        OfferService offerClient = OfferServiceImpl.instance();
        String offerId = offerClient.getOfferIdByName("testOffer_CartCheckout_digital1");
        String orId = IdConverter.idToUrlString(
                OfferRevisionId.class, Master.getInstance().getOffer(offerId).getCurrentRevisionId());
        String itemId = Master.getInstance().getOfferRevision(orId).getItems().get(0).getItemId();
        entitlement.setItemId(itemId);
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
        OAuthServiceImpl.getInstance().postAccessToken(GrantType.CLIENT_CREDENTIALS, ComponentType.CATALOGADMIN);
        ItemRevision itemRevisionPut = itemRevisionService.updateItemRevision(itemRevisionPosted.getRevisionId(), itemRevisionPosted);
        this.developerItemId = itemRevisionPut.getItemId();
    }

}
