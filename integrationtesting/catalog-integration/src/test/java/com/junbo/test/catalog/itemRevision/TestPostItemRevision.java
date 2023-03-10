/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.itemRevision;

import com.junbo.common.model.Results;
import com.junbo.test.common.apihelper.identity.impl.OrganizationServiceImpl;
import com.junbo.catalog.spec.model.item.ItemRevisionLocaleProperties;
import com.junbo.test.common.apihelper.identity.OrganizationService;
import com.junbo.test.catalog.impl.ItemRevisionServiceImpl;
import com.junbo.test.catalog.enums.CatalogEntityStatus;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.test.catalog.enums.CatalogItemType;
import com.junbo.test.catalog.impl.ItemServiceImpl;
import com.junbo.test.catalog.ItemRevisionService;
import com.junbo.test.catalog.util.BaseTestClass;
import com.junbo.catalog.spec.model.common.Price;
import com.junbo.catalog.spec.model.item.Binary;
import com.junbo.test.common.libs.RandomFactory;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.common.id.OrganizationId;
import com.junbo.test.catalog.ItemService;
import com.junbo.test.common.property.*;

import org.testng.annotations.Test;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jason
 * Time: 7/4/2014
 * For testing catalog post item revision API
 */
public class TestPostItemRevision extends BaseTestClass {

    private LogHelper logger = new LogHelper(TestPostItemRevision.class);

    private Item item1;
    private Item item2;
    private OrganizationId organizationId;
    private final String defaultLocale = "en_US";
    private final String fullItemRevisionFileName = "fullItemRevision";

    private ItemService itemService = ItemServiceImpl.instance();
    private ItemRevisionService itemRevisionService = ItemRevisionServiceImpl.instance();

    @Property(
            priority = Priority.BVT,
            features = "Post v1/item-revisions",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Post Item Revisions",
            steps = {
                    "1. Post test item revisions only with required fields",
                    "2. Verify the returned values are the same with prepared",
                    "3. Post test item revisions with optional fields",
                    "4. Verify the returned values are the same with prepared"
            }
    )
    @Test
    public void testPostItemRevision() throws Exception {
        this.prepareTestData();

        //Post an item revision only with required fields
        ItemRevision itemRevisionPrepared = new ItemRevision();
        Map<String, ItemRevisionLocaleProperties> locales = new HashMap<>();
        ItemRevisionLocaleProperties itemRevisionLocaleProperties = new ItemRevisionLocaleProperties();

        itemRevisionPrepared.setItemId(item1.getItemId());
        itemRevisionPrepared.setOwnerId(organizationId);
        itemRevisionPrepared.setStatus(CatalogEntityStatus.DRAFT.name());

        itemRevisionLocaleProperties.setName("testItemRevision_" + RandomFactory.getRandomStringOfAlphabetOrNumeric(10));
        locales.put(defaultLocale, itemRevisionLocaleProperties);
        itemRevisionPrepared.setLocales(locales);

        List<String> distributionChannels = new ArrayList<>();
        distributionChannels.add("INAPP");
        distributionChannels.add("STORE");
        itemRevisionPrepared.setDistributionChannels(distributionChannels);

        ItemRevision itemRevisionRtn = itemRevisionService.postItemRevision(itemRevisionPrepared);

        checkItemRevisionRequiredFields(itemRevisionRtn, itemRevisionPrepared);

        //Post an item revision with optional fields
        ItemRevision testItemRevisionFull = itemRevisionService.prepareItemRevisionEntity(fullItemRevisionFileName);

        testItemRevisionFull.setItemId(item1.getItemId());
        testItemRevisionFull.setOwnerId(organizationId);
        testItemRevisionFull.setPackageName("packageName_" + RandomFactory.getRandomStringOfAlphabetOrNumeric(10));
        List<String> hostItemIds = new ArrayList<>();
        hostItemIds.add(item2.getItemId());
        testItemRevisionFull.setIapHostItemIds(hostItemIds);

        ItemRevision testItemRevisionRtn = itemRevisionService.postItemRevision(testItemRevisionFull);

        checkItemRevisionRequiredFields(testItemRevisionRtn, testItemRevisionFull);
        checkItemRevisionOptionalFields(testItemRevisionRtn, testItemRevisionFull);
    }

    @Property(
            priority = Priority.BVT,
            features = "Post v1/item-revisions",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Post Item Revisions",
            steps = {
                    "1. Post test item revisions with unicode characters",
                    "2. Use unicode characters to search the item"
            }
    )
    @Test
    public void testPostItemRevisionWithUnicode() throws Exception {
        this.prepareTestData();

        //Post an item revision only with required fields
        ItemRevision testItemRevisionFull = itemRevisionService.prepareItemRevisionEntity(fullItemRevisionFileName);

        testItemRevisionFull.setItemId(item1.getItemId());
        testItemRevisionFull.setOwnerId(organizationId);
        testItemRevisionFull.setPackageName("packageName_" + RandomFactory.getRandomStringOfAlphabetOrNumeric(10));

        Map<String, ItemRevisionLocaleProperties> locales = new HashMap<>();
        ItemRevisionLocaleProperties itemRevisionLocaleProperties = testItemRevisionFull.getLocales().get(defaultLocale);

        String itemRevisionName = "testItemRevision_" + RandomFactory.getRandomStringOfAlphabetOrNumeric(10) + "??????";
        itemRevisionLocaleProperties.setName(itemRevisionName);
        locales.put(defaultLocale, itemRevisionLocaleProperties);
        testItemRevisionFull.setLocales(locales);
        List<String> hostItemIds = new ArrayList<>();
        hostItemIds.add(item2.getItemId());
        testItemRevisionFull.setIapHostItemIds(hostItemIds);

        ItemRevision itemRevisionRtn = itemRevisionService.postItemRevision(testItemRevisionFull);
        Assert.assertTrue(itemRevisionName.equalsIgnoreCase(itemRevisionRtn.getLocales().get(defaultLocale).getName()));

        if (item1.getType().equalsIgnoreCase(CatalogItemType.APP.name()) ||
                item1.getType().equalsIgnoreCase(CatalogItemType.DOWNLOADED_ADDITION.name())) {
            itemRevisionRtn.setDownloadName("download name");

            Map<String, Binary> binaries = new HashMap<>();
            Binary binary = new Binary();
            binary.setVersion("1");
            binary.setSize(1024L);
            binary.setMd5("abcdabcdabcdabcdabcdabcdabcdabcd");
            binary.setHref("http://www.google.com/downlaod/angrybird1_0.exe");
            binaries.put("PC", binary);

            itemRevisionRtn.setBinaries(binaries);
        }

        releaseItemRevision(itemRevisionRtn);

        HashMap<String, List<String>> paraMap = new HashMap<>();
        List<String> query = new ArrayList<>();

        query.add("%22" + itemRevisionName + "%22");
        paraMap.put("q", query);

        Results<Item> items = itemService.getItems(paraMap);
        Assert.assertEquals(items.getItems().size(), 1);
        Assert.assertTrue(isContain(items, item1));
    }

    @Property(
            priority = Priority.Dailies,
            features = "Post v1/item-revisions",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Post Item Revisions",
            steps = {
                    "1. Prepare an item revision",
                    "2. test invalid values(like null, not null and some invalid enum values)",
                    "3. Try to post it and verify the expected error"
            }
    )
    @Test
    public void testPostItemRevisionInvalidScenarios() throws Exception {
        this.prepareTestData();

        //Set rev not null
        ItemRevision testItemRevision = itemRevisionService.prepareItemRevisionEntity(fullItemRevisionFileName);
        testItemRevision.setPackageName("packageName_" + RandomFactory.getRandomStringOfAlphabetOrNumeric(10));
        testItemRevision.setItemId(item1.getItemId());
        testItemRevision.setOwnerId(organizationId);
        testItemRevision.setRev("1");
        verifyExpectedError(testItemRevision);

        //Set status to invalid value
        testItemRevision.setRev(null);
        testItemRevision.setStatus("invalidValue");
        verifyExpectedError(testItemRevision);

        //set status to not allowed Enum value
        testItemRevision.setStatus(CatalogEntityStatus.REJECTED.name());
        verifyExpectedError(testItemRevision);

        //set OwnerId to null
        testItemRevision.setStatus(CatalogEntityStatus.DRAFT.name());
        testItemRevision.setOwnerId(null);
        verifyExpectedError(testItemRevision);

        //Test invalid value for itemId
        testItemRevision.setOwnerId(organizationId);
        testItemRevision.setItemId(null);
        verifyExpectedError(testItemRevision);

        testItemRevision.setItemId("11111");
        try {
            itemRevisionService.postItemRevision(testItemRevision, 404);
            Assert.fail("Post item revision should fail");
        }
        catch (Exception ex) {
            logger.logInfo("Expected exception: " + ex);
        }

        testItemRevision.setItemId(item1.getItemId());

        //locales: name
        Map<String, ItemRevisionLocaleProperties> locales = new HashMap<>();
        ItemRevisionLocaleProperties itemRevisionLocaleProperties = new ItemRevisionLocaleProperties();
        itemRevisionLocaleProperties.setName(null);
        locales.put(defaultLocale, itemRevisionLocaleProperties);
        testItemRevision.setLocales(locales);
        verifyExpectedError(testItemRevision);

        itemRevisionLocaleProperties.setName("testItemRevision_" + RandomFactory.getRandomStringOfAlphabetOrNumeric(10));
        locales.put(defaultLocale, itemRevisionLocaleProperties);
        testItemRevision.setLocales(locales);

        //set binaries for not APP and DOWNLOADED_ADDITION type
        Item item = itemService.postDefaultItem(CatalogItemType.PHYSICAL, organizationId);
        testItemRevision.setItemId(item.getItemId());
        Map<String, Binary> binaries = new HashMap<>();
        Binary binary = new Binary();
        binary.setVersion("1");
        binary.setSize(1024L);
        binary.setMd5("abcdabcdabcdabcdabcdabcdabcdabcd");
        binary.setHref("http://www.google.com/downlaod/angrybird1_0.exe");
        binaries.put("PC", binary);
        testItemRevision.setBinaries(binaries);

        testItemRevision = itemRevisionService.postItemRevision(testItemRevision);

        //duplicate packageName
        Item item3 = itemService.postDefaultItem(CatalogItemType.APP, organizationId);

        String packageName = "packageName_" + RandomFactory.getRandomStringOfAlphabetOrNumeric(10);
        ItemRevision tmpItemRevision = itemRevisionService.prepareItemRevisionEntity(fullItemRevisionFileName);
        tmpItemRevision.setItemId(item2.getItemId());
        tmpItemRevision.setOwnerId(organizationId);
        tmpItemRevision.setPackageName(packageName);
        List<String> hostItem = new ArrayList<>();
        hostItem.add(item3.getItemId());
        tmpItemRevision.setIapHostItemIds(hostItem);
        if (item2.getType().equalsIgnoreCase(CatalogItemType.APP.name()) ||
                item2.getType().equalsIgnoreCase(CatalogItemType.DOWNLOADED_ADDITION.name())) {
            tmpItemRevision.setDownloadName("download name");
            tmpItemRevision.setBinaries(binaries);
        }

        ItemRevision itemRevisionRtn = itemRevisionService.postItemRevision(tmpItemRevision);
        releaseItemRevision(itemRevisionRtn);

        if (item1.getType().equalsIgnoreCase(CatalogItemType.APP.name()) ||
                item1.getType().equalsIgnoreCase(CatalogItemType.DOWNLOADED_ADDITION.name())) {
            testItemRevision.setDownloadName("download name");
            testItemRevision.setBinaries(binaries);
        }
        else {
            testItemRevision.setDownloadName(null);
            testItemRevision.setBinaries(null);
        }

        testItemRevision.setItemId(item1.getItemId());
        testItemRevision.setPackageName(packageName);

        verifyExpectedError(testItemRevision);
    }

    private void prepareTestData() throws Exception {
        OrganizationService organizationService = OrganizationServiceImpl.instance();
        organizationId = organizationService.postDefaultOrganization().getId();

        item1 = itemService.postDefaultItem(CatalogItemType.getRandom(), organizationId);
        item2 = itemService.postDefaultItem(CatalogItemType.DOWNLOADED_ADDITION, organizationId);
    }

    private void checkItemRevisionRequiredFields(ItemRevision itemRevisionActual, ItemRevision itemRevisionExpected) {
        Assert.assertEquals(itemRevisionActual.getItemId(), itemRevisionExpected.getItemId());
        Assert.assertEquals(itemRevisionActual.getOwnerId(), itemRevisionExpected.getOwnerId());
        //Compare name
        ItemRevisionLocaleProperties localePropertiesActual = itemRevisionActual.getLocales().get(defaultLocale);
        ItemRevisionLocaleProperties localePropertiesExpected = itemRevisionExpected.getLocales().get(defaultLocale);
        Assert.assertEquals(localePropertiesActual.getName(), localePropertiesExpected.getName());
    }

    private void checkItemRevisionOptionalFields(ItemRevision itemRevisionActual, ItemRevision itemRevisionExpected) {
        Assert.assertEquals(itemRevisionActual.getStatus(), itemRevisionExpected.getStatus());
        // Assert.assertEquals(itemRevisionActual.getRollupPackageName(), itemRevisionExpected.getRollupPackageName());
        Assert.assertEquals(itemRevisionActual.getPackageName(), itemRevisionExpected.getPackageName());
        Assert.assertEquals(itemRevisionActual.getSku(), itemRevisionExpected.getSku());

        //Compare msrp
        Price actual = itemRevisionActual.getMsrp();
        Price expected = itemRevisionExpected.getMsrp();
        Assert.assertTrue((actual.getPriceTier() == null) ? expected.getPriceTier() == null : actual.getPriceTier().equalsIgnoreCase(expected.getPriceTier()));
        Assert.assertTrue(actual.getPriceType().equalsIgnoreCase(expected.getPriceType()));
        Assert.assertTrue(actual.getPrices().equals(expected.getPrices()));

        //Compare UserInteractionModes
        Assert.assertTrue((itemRevisionActual.getUserInteractionModes() == null) ? itemRevisionExpected.getUserInteractionModes() == null :
                itemRevisionActual.getUserInteractionModes().equals(itemRevisionExpected.getUserInteractionModes()));
        //Compare supportedInputDevices
        Assert.assertTrue((itemRevisionActual.getSupportedInputDevices() == null)? itemRevisionExpected.getSupportedInputDevices()== null :
                itemRevisionActual.getSupportedInputDevices().equals(itemRevisionExpected.getSupportedInputDevices()));
        //Compare platforms
        Assert.assertTrue((itemRevisionActual.getPlatforms() == null)? itemRevisionExpected.getPlatforms()== null :
                itemRevisionActual.getPlatforms().equals(itemRevisionExpected.getPlatforms()));
        //Compare iapHostItems
        Assert.assertTrue((itemRevisionActual.getIapHostItemIds() == null)? itemRevisionExpected.getIapHostItemIds()== null :
                itemRevisionActual.getIapHostItemIds().equals(itemRevisionExpected.getIapHostItemIds()));
    }

    private void verifyExpectedError(ItemRevision itemRevision) {
        try {
            //Error code 400 means "Missing Input field", "Unnecessary field found" or "invalid value"
            itemRevisionService.postItemRevision(itemRevision, 400);
            Assert.fail("Post item revision should fail");
        }
        catch (Exception ex) {
            logger.logInfo("Expected exception: " + ex);
        }
    }

}
