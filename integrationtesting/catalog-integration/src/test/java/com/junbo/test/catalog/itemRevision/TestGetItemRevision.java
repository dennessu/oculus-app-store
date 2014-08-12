/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.itemRevision;

import com.junbo.test.common.apihelper.identity.impl.OrganizationServiceImpl;
import com.junbo.catalog.spec.model.item.ItemRevisionLocaleProperties;
import com.junbo.test.common.apihelper.identity.OrganizationService;
import com.junbo.test.catalog.impl.ItemRevisionServiceImpl;
import com.junbo.test.catalog.enums.CatalogEntityStatus;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.test.catalog.enums.CatalogItemType;
import com.junbo.test.catalog.impl.ItemServiceImpl;
import com.junbo.test.catalog.enums.LocaleAccuracy;
import com.junbo.test.catalog.ItemRevisionService;
import com.junbo.test.catalog.util.BaseTestClass;
import com.junbo.test.common.libs.RandomFactory;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.catalog.ItemService;
import com.junbo.common.id.OrganizationId;
import com.junbo.test.common.property.*;
import com.junbo.common.model.Results;

import org.testng.annotations.Test;
import org.testng.Assert;

import java.util.*;

/**
 * @author Jason
 * Time: 5/22/2014
 * For testing catalog get item revision(s) API
 */
public class TestGetItemRevision extends BaseTestClass {

    private ItemService itemService = ItemServiceImpl.instance();
    private LogHelper logger = new LogHelper(TestGetItemRevision.class);
    private ItemRevisionService itemRevisionService = ItemRevisionServiceImpl.instance();

    private Item testItem;
    private OrganizationId organizationId;
    private final String defaultLocale = "en_US";

    private void prepareTestData() throws Exception {
        OrganizationService organizationService = OrganizationServiceImpl.instance();
        organizationId = organizationService.postDefaultOrganization().getId();
        testItem = itemService.postDefaultItem(CatalogItemType.getRandom(), organizationId);
    }

    @Property(
            priority = Priority.Dailies,
            features = "Get v1/item-revisions/{itemRevisionId}",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Get an item revision by itemRevisionId(valid, invalid scenarios)",
            steps = {
                    "1. Prepare an item revision",
                    "2. Get it by its Id",
                    "3. Verify not able to get the item revision by invalid Id",
                    "4. Release the item revision",
                    "5. Get the item revision by Id again, verify the behavior is successful"
            }
    )
    @Test
    public void testGetAnItemRevisionById() throws Exception {
        this.prepareTestData();

        //Prepare an item revision
        ItemRevision itemRevision = itemRevisionService.postDefaultItemRevision(testItem);

        //get the item revision by Id, assert not null
        ItemRevision itemRevisionRtn = itemRevisionService.getItemRevision(itemRevision.getRevisionId());
        Assert.assertNotNull(itemRevisionRtn, "Couldn't get the item revision by its id");

        //verify the invalid Id scenario
        String invalidId = "0L";
        verifyInvalidScenarios(invalidId);

        //Release the item revision and then try to get the item revision
        releaseItemRevision(itemRevision);
        itemRevisionRtn = itemRevisionService.getItemRevision(itemRevision.getRevisionId());
        Assert.assertNotNull(itemRevisionRtn, "Can't get the item revision by its id");

        //verify the invalid Id scenario
        verifyInvalidScenarios(invalidId);

    }

    @Property(
            priority = Priority.Dailies,
            features = "Get v1/item-revisions/{itemRevisionId}?locale={locale}",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Get an item revision by itemRevisionId and locale",
            steps = {
                    "1. Prepare an item revision",
                    "2. Get it id and locale",
                    "3. Verify localeAccuracy and itemRevision locale property"
            }
    )
    @Test
    public void testGetAnItemRevisionByIdLocale() throws Exception {
        this.prepareTestData();

        HashMap<String, List<String>> httpPara = new HashMap<>();
        List<String> locale = new ArrayList<>();

        //Prepare an item revision
        ItemRevision itemRevision = itemRevisionService.postDefaultItemRevision(testItem);
        ItemRevisionLocaleProperties itemRevisionLocaleProperties = itemRevision.getLocales().get(defaultLocale);

        //Get without locale
        ItemRevision itemRevisionRtn = itemRevisionService.getItemRevision(itemRevision.getRevisionId());
        Assert.assertTrue(LocaleAccuracy.HIGH.is(itemRevisionRtn.getLocaleAccuracy()));

        //Get default locale
        locale.add(defaultLocale);
        httpPara.put("locale", locale);
        itemRevisionRtn = itemRevisionService.getItemRevision(itemRevision.getRevisionId(), httpPara);
        Assert.assertTrue(LocaleAccuracy.HIGH.is(itemRevisionRtn.getLocaleAccuracy()));
        Assert.assertEquals(itemRevisionRtn.getLocales().get(defaultLocale).getName(), itemRevisionLocaleProperties.getName());

        //Get fr_FR
        locale.clear();
        locale.add("fr_FR");
        httpPara.put("locale", locale);
        itemRevisionRtn = itemRevisionService.getItemRevision(itemRevision.getRevisionId(), httpPara);
        Assert.assertTrue(LocaleAccuracy.LOW.is(itemRevisionRtn.getLocaleAccuracy()));
        Assert.assertEquals(itemRevisionRtn.getLocales().get("fr_FR").getName(), itemRevisionLocaleProperties.getName());

        //get zh_CN
        locale.clear();
        locale.add("zh_CN");
        httpPara.put("locale", locale);
        itemRevisionRtn = itemRevisionService.getItemRevision(itemRevision.getRevisionId(), httpPara);
        Assert.assertTrue(LocaleAccuracy.LOW.is(itemRevisionRtn.getLocaleAccuracy()));
        Assert.assertEquals(itemRevisionRtn.getLocales().get("zh_CN").getName(), itemRevisionLocaleProperties.getName());

        //Add fr_FR locale to the item revision
        Map<String, ItemRevisionLocaleProperties> locales = itemRevision.getLocales();
        ItemRevisionLocaleProperties itemRevisionLocalePropertiesFR = new ItemRevisionLocaleProperties();
        itemRevisionLocalePropertiesFR.setName("testItemRevision_fr_FR_" + RandomFactory.getRandomStringOfAlphabet(10));
        locales.put("fr_FR", itemRevisionLocalePropertiesFR);

        itemRevision.setLocales(locales);
        itemRevisionService.updateItemRevision(itemRevision.getRevisionId(), itemRevision);

        //try get default locale
        locale.clear();
        locale.add(defaultLocale);
        httpPara.put("locale", locale);
        itemRevisionRtn = itemRevisionService.getItemRevision(itemRevision.getRevisionId(), httpPara);
        Assert.assertTrue(LocaleAccuracy.HIGH.is(itemRevisionRtn.getLocaleAccuracy()));
        Assert.assertEquals(itemRevisionRtn.getLocales().get(defaultLocale).getName(), itemRevisionLocaleProperties.getName());

        //Get fr_FR
        locale.clear();
        locale.add("fr_FR");
        httpPara.put("locale", locale);
        itemRevisionRtn = itemRevisionService.getItemRevision(itemRevision.getRevisionId(), httpPara);
        Assert.assertTrue(LocaleAccuracy.MEDIUM.is(itemRevisionRtn.getLocaleAccuracy()));
        Assert.assertEquals(itemRevisionRtn.getLocales().get("fr_FR").getName(), itemRevisionLocalePropertiesFR.getName());

        //Get fr_CA
        locale.clear();
        locale.add("fr_CA");
        httpPara.put("locale", locale);
        itemRevisionRtn = itemRevisionService.getItemRevision(itemRevision.getRevisionId(), httpPara);
        Assert.assertTrue(LocaleAccuracy.LOW.is(itemRevisionRtn.getLocaleAccuracy()));
        Assert.assertEquals(itemRevisionRtn.getLocales().get("fr_CA").getName(), itemRevisionLocalePropertiesFR.getName());

        //get zh_CN
        locale.clear();
        locale.add("zh_CN");
        httpPara.put("locale", locale);
        itemRevisionRtn = itemRevisionService.getItemRevision(itemRevision.getRevisionId(), httpPara);
        Assert.assertTrue(LocaleAccuracy.LOW.is(itemRevisionRtn.getLocaleAccuracy()));
        Assert.assertEquals(itemRevisionRtn.getLocales().get("zh_CN").getName(), itemRevisionLocaleProperties.getName());
    }

    @Property(
            priority = Priority.Dailies,
            features = "Get v1/item-revisions?itemId=&revisionId=",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Get item revisions by itemId and revisionId(valid, invalid scenarios)",
            steps = {
                    "1. Prepare some items and item revisions",
                    "2. Get revisions by item Id",
                    "3. Get revisions by revision Id",
                    "4. Get revisions by item Id and revision Id"
            }
    )
    @Test
    public void testGetItemRevisionsByItemIdRevisionId() throws Exception {
        this.prepareTestData();

        HashMap<String, List<String>> getOptions = new HashMap<>();
        List<String> itemIds = new ArrayList<>();
        List<String> revisionIds = new ArrayList<>();

        //Prepare two items
        Item item1 = itemService.postDefaultItem(CatalogItemType.getRandom(), organizationId);
        Item item2 = itemService.postDefaultItem(CatalogItemType.getRandom(), organizationId);
        String itemId1 = item1.getItemId();
        String itemId2 = item2.getItemId();

        //Prepare some item revisions
        ItemRevision itemRevision1 = itemRevisionService.postDefaultItemRevision(item1);
        ItemRevision itemRevision2 = itemRevisionService.postDefaultItemRevision(item1);
        ItemRevision itemRevision3 = itemRevisionService.postDefaultItemRevision(item2);
        ItemRevision itemRevision4 = itemRevisionService.postDefaultItemRevision(item2);

        //release the item revisions as if status is not specified, it will be set to APPROVED
        releaseItemRevision(itemRevision1);
        releaseItemRevision(itemRevision2);
        releaseItemRevision(itemRevision3);
        releaseItemRevision(itemRevision4);

        //get revisions by itemId
        itemIds.add(itemId1);
        getOptions.put("itemId", itemIds);
        verifyGetResults(getOptions, 2, itemRevision1, itemRevision2);
        itemIds.add(itemId2);
        getOptions.put("itemId", itemIds);
        verifyGetResults(getOptions, 4, itemRevision1, itemRevision2, itemRevision3, itemRevision4);

        itemIds.clear();
        itemIds.add(itemId2);
        itemIds.add("000000");
        getOptions.put("itemId", itemIds);
        verifyGetResults(getOptions, 2, itemRevision3, itemRevision4);

        //get revisions by itemId and revisionId, only revisionId works
        revisionIds.add(itemRevision4.getRevisionId());
        getOptions.put("revisionId", revisionIds);
        verifyGetResults(getOptions, 1, itemRevision4);

        revisionIds.add("0000000");
        getOptions.put("revisionId", revisionIds);
        verifyGetResults(getOptions, 1, itemRevision4);

        revisionIds.add(itemRevision1.getRevisionId());
        revisionIds.add(itemRevision2.getRevisionId());
        getOptions.put("revisionId", revisionIds);
        verifyGetResults(getOptions, 3, itemRevision1, itemRevision2, itemRevision4);

        itemIds.clear();
        itemIds.add(itemId2);
        itemIds.add(itemId1);
        getOptions.put("itemId", itemIds);
        verifyGetResults(getOptions, 3, itemRevision1, itemRevision2, itemRevision4);

        itemIds.clear();
        getOptions.put("itemId", itemIds);
        verifyGetResults(getOptions, 3, itemRevision1, itemRevision2, itemRevision4);

        itemIds.add("000000");
        getOptions.put("itemId", itemIds);
        verifyGetResults(getOptions, 3, itemRevision1, itemRevision2, itemRevision4);
    }

    @Property(
            priority = Priority.Dailies,
            features = "Get v1/item-revisions?revisionId=&status=",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Get item revisions by revision status(valid, invalid scenarios)",
            steps = {
                    "1. Prepare some item revisions",
                    "2. Get revisions by status(valid and invalid scenarios)",
                    "3. Approved the item revisions",
                    "4. Get revisions by status(valid and invalid scenarios)",
                    "5. Deleted the item revisions",
                    "6. Get revisions by status(valid and invalid scenarios)"
            }
    )
    @Test
    public void testGetItemRevisionsByRevisionStatus() throws Exception {
        this.prepareTestData();

        HashMap<String, List<String>> getOptions = new HashMap<>();
        List<String> revisionIds = new ArrayList<>();

        //Prepare two items
        Item item1 = itemService.postDefaultItem(CatalogItemType.getRandom(), organizationId);
        Item item2 = itemService.postDefaultItem(CatalogItemType.getRandom(), organizationId);

        //Prepare some item revisions
        ItemRevision itemRevision1 = itemRevisionService.postDefaultItemRevision(item1);
        ItemRevision itemRevision2 = itemRevisionService.postDefaultItemRevision(item2);
        String itemRevisionId1 = itemRevision1.getRevisionId();
        String itemRevisionId2 = itemRevision2.getRevisionId();

        revisionIds.add(itemRevisionId1);
        revisionIds.add(itemRevisionId2);
        getOptions.put("revisionId", revisionIds);

        //For the status specified is not approved, you must specify the developerId as well.
        String developer = IdConverter.idToHexString(organizationId);
        setSearchStatus(CatalogEntityStatus.DRAFT.getEntityStatus(), developer, getOptions, 2, itemRevision1, itemRevision2);
        setSearchStatus(CatalogEntityStatus.APPROVED.getEntityStatus(), null, getOptions, 0);
        setSearchStatus("invalidStatus", developer, getOptions, 0);

        //release one item revision
        releaseItemRevision(itemRevision1);

        setSearchStatus(CatalogEntityStatus.DRAFT.getEntityStatus(), developer, getOptions, 1, itemRevision2);
        setSearchStatus(CatalogEntityStatus.APPROVED.getEntityStatus(), null, getOptions, 1, itemRevision1);
        setSearchStatus(CatalogEntityStatus.PENDING_REVIEW.getEntityStatus(), developer, getOptions, 0);
        setSearchStatus("invalidStatus", developer, getOptions, 0);

        //release the both two item revisions
        releaseItemRevision(itemRevision2);

        setSearchStatus(CatalogEntityStatus.DRAFT.getEntityStatus(), developer, getOptions, 0);
        setSearchStatus(CatalogEntityStatus.APPROVED.getEntityStatus(), null, getOptions, 2, itemRevision1, itemRevision2);
        setSearchStatus(CatalogEntityStatus.PENDING_REVIEW.getEntityStatus(), developer, getOptions, 0);
        setSearchStatus("invalidStatus", developer, getOptions, 0);
    }

    @Property(
            priority = Priority.Dailies,
            features = "Get v1/item-revisions?itemID=&timeInMillis=",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Get item revisions by itemId and timestamp(valid, invalid scenarios)",
            steps = {
                    "1. Prepare some item revisions",
                    "2. Get item revisions by itemId and timestamp"
            }
    )
    @Test
    public void testGetItemRevisionsByItemIdTimestamp() throws Exception {
        this.prepareTestData();

        HashMap<String, List<String>> getOptions = new HashMap<>();
        List<String> itemIds = new ArrayList<>();
        List<String> timeStamp = new ArrayList<>();
        List<String> revisionIds = new ArrayList<>();

        //Prepare two items
        Item item1 = itemService.postDefaultItem(CatalogItemType.getRandom(), organizationId);
        Item item2 = itemService.postDefaultItem(CatalogItemType.getRandom(), organizationId);
        String itemId1 = item1.getItemId();
        String itemId2 = item2.getItemId();
        //Prepare some item revisions
        ItemRevision itemRevision1 = itemRevisionService.postDefaultItemRevision(item1);
        ItemRevision itemRevision2 = itemRevisionService.postDefaultItemRevision(item1);
        ItemRevision itemRevision3 = itemRevisionService.postDefaultItemRevision(item2);
        ItemRevision itemRevision4 = itemRevisionService.postDefaultItemRevision(item2);

        itemIds.add(itemId1);
        itemIds.add(itemId2);
        getOptions.put("itemId", itemIds);

        Calendar current = Calendar.getInstance();

        //not released, will get 0 results
        timeStamp.add(Long.toString(current.getTimeInMillis()));
        getOptions.put("timeInMillis", timeStamp);
        verifyGetResults(getOptions, 0);

        //release item revision1
        releaseItemRevision(itemRevision1);
        current = Calendar.getInstance();
        timeStamp.clear();
        timeStamp.add(Long.toString(current.getTimeInMillis()));
        getOptions.put("timeInMillis", timeStamp);
        verifyGetResults(getOptions, 1, itemRevision1);

        //release itemRevision2
        releaseItemRevision(itemRevision2);
        current = Calendar.getInstance();
        timeStamp.clear();
        timeStamp.add(Long.toString(current.getTimeInMillis()));
        getOptions.put("timeInMillis", timeStamp);
        verifyGetResults(getOptions, 1, itemRevision2);

        //set revisionId, but revisionId should not work, still get itemRevision2
        revisionIds.add(itemRevision3.getRevisionId());
        getOptions.put("revisionId", revisionIds);
        verifyGetResults(getOptions, 1, itemRevision2);

        //release the other 2 revisions
        releaseItemRevision(itemRevision3);
        releaseItemRevision(itemRevision4);

        current = Calendar.getInstance();
        timeStamp.clear();
        timeStamp.add(Long.toString(current.getTimeInMillis()));
        getOptions.put("timeInMillis", timeStamp);
        verifyGetResults(getOptions, 2, itemRevision2, itemRevision4);

    }

    @Property(
            priority = Priority.Dailies,
            features = "Get v1/item-revisions?revisionId=&locale=",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Get item revisions by revisionId and locale",
            steps = {
                    "1. Prepare some item revision",
                    "2. Get them by id and locale",
                    "3. Verify localeAccuracy and itemRevision locale property"
            }
    )
    @Test
    public void testGetItemRevisionsByRevisionIdLocale() throws Exception {
        this.prepareTestData();

        HashMap<String, List<String>> getOptions = new HashMap<>();
        List<String> revisionIds = new ArrayList<>();
        List<String> locales = new ArrayList<>();

        //Prepare two items
        Item item1 = itemService.postDefaultItem(CatalogItemType.getRandom(), organizationId);
        Item item2 = itemService.postDefaultItem(CatalogItemType.getRandom(), organizationId);

        //Prepare some item revisions
        ItemRevision itemRevision1 = itemRevisionService.postDefaultItemRevision(item1);
        ItemRevision itemRevision2 = itemRevisionService.postDefaultItemRevision(item1);
        ItemRevision itemRevision3 = itemRevisionService.postDefaultItemRevision(item2);
        ItemRevision itemRevision4 = itemRevisionService.postDefaultItemRevision(item2);

        //release the item revisions as if status is not specified, it will be set to APPROVED
        releaseItemRevision(itemRevision1);
        releaseItemRevision(itemRevision2);
        releaseItemRevision(itemRevision3);
        releaseItemRevision(itemRevision4);

        //get revisions by revisionId
        revisionIds.add(itemRevision4.getRevisionId());
        getOptions.put("revisionId", revisionIds);

        //Get without locale
        verifyGetResultsInLocale(getOptions, LocaleAccuracy.HIGH, itemRevision4);

        //set locale
        locales.add(defaultLocale);
        getOptions.put("locale", locales);

        verifyGetResultsInLocale(getOptions, LocaleAccuracy.HIGH, itemRevision4);

        //get revisions by revisionId
        revisionIds.add(itemRevision2.getRevisionId());
        revisionIds.add(itemRevision3.getRevisionId());
        getOptions.put("revisionId", revisionIds);

        verifyGetResultsInLocale(getOptions, LocaleAccuracy.HIGH, itemRevision2, itemRevision3, itemRevision4);

        //set locale
        locales.clear();
        locales.add("fr_FR");
        getOptions.put("locale", locales);

        verifyGetResultsInLocale(getOptions, LocaleAccuracy.LOW, itemRevision2, itemRevision3, itemRevision4);
    }

    private void verifyInvalidScenarios(String itemRevisionId) throws Exception {
        try {
            itemRevisionService.getItemRevision(itemRevisionId, null, 404);
            Assert.fail("Shouldn't get item revision with wrong id");
        }
        catch (Exception e) {
            logger.logInfo("Expected exception: couldn't get item revision with wrong id");
        }
    }

    private void setSearchStatus(String status, String developer, HashMap<String, List<String>> getOptions, int expectedRtnSize, ItemRevision... itemRevisions)
            throws Exception {
        List<String> searchStatus = new ArrayList<>();
        searchStatus.add(status);
        getOptions.put("status", searchStatus);
        if (developer != null && developer.length() > 0) {
            List<String> developerIds = new ArrayList<>();
            developerIds.add(developer);
            getOptions.put("developerId", developerIds);
        }
        else {
            getOptions.remove("developerId");
        }

        verifyGetResults(getOptions, expectedRtnSize, itemRevisions);
    }

    private void verifyGetResults(HashMap<String, List<String>> getOptions, int expectedRtnSize, ItemRevision... itemRevisions) throws Exception {
        Results<ItemRevision> itemRevisionsRtn = itemRevisionService.getItemRevisions(getOptions);
        Assert.assertEquals(itemRevisionsRtn.getItems().size(), expectedRtnSize);
        for (ItemRevision temp : itemRevisions) {
            Assert.assertTrue(isContain(itemRevisionsRtn, temp));
        }
    }

    private void verifyGetResultsInLocale(HashMap<String, List<String>> getOptions, LocaleAccuracy expectedLocaleAccuracy, ItemRevision... itemRevisions) throws Exception {
        String locale;

        if (getOptions.get("locale") != null) {
            locale = getOptions.get("locale").get(0);
        } else {
            locale = defaultLocale;
        }


        Results<ItemRevision> itemRevisionsRtn = itemRevisionService.getItemRevisions(getOptions);

        for (ItemRevision temp : itemRevisions) {
            Assert.assertTrue(isContain(itemRevisionsRtn, temp));
        }

        for (ItemRevision temp : itemRevisionsRtn.getItems()) {
            Assert.assertNotNull(temp.getLocales().get(locale));
            Assert.assertEquals(temp.getLocaleAccuracy(), expectedLocaleAccuracy.name());
        }
    }

}

