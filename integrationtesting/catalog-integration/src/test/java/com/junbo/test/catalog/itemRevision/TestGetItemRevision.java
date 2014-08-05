/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.itemRevision;

import com.junbo.test.common.apihelper.identity.impl.OrganizationServiceImpl;
import com.junbo.test.common.apihelper.identity.OrganizationService;
import com.junbo.test.catalog.impl.ItemRevisionServiceImpl;
import com.junbo.test.catalog.enums.CatalogEntityStatus;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.test.catalog.enums.CatalogItemType;
import com.junbo.test.catalog.impl.ItemServiceImpl;
import com.junbo.test.catalog.ItemRevisionService;
import com.junbo.test.catalog.util.BaseTestClass;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.catalog.ItemService;
import com.junbo.common.id.ItemRevisionId;
import com.junbo.common.id.OrganizationId;
import com.junbo.test.common.property.*;
import com.junbo.common.model.Results;
import com.junbo.common.id.ItemId;

import org.testng.annotations.Test;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * @author Jason
 * Time: 5/22/2014
 * For testing catalog get item revision(s) API
 */
public class TestGetItemRevision extends BaseTestClass {

    private LogHelper logger = new LogHelper(TestGetItemRevision.class);
    private ItemRevisionService itemRevisionService = ItemRevisionServiceImpl.instance();
    private ItemService itemService = ItemServiceImpl.instance();
    private OrganizationId organizationId;
    private Item testItem;

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
        revisionIds.add(IdConverter.idToUrlString(ItemRevisionId.class, itemRevision4.getRevisionId()));
        getOptions.put("revisionId", revisionIds);
        verifyGetResults(getOptions, 1, itemRevision4);

        revisionIds.add("0000000");
        getOptions.put("revisionId", revisionIds);
        verifyGetResults(getOptions, 1, itemRevision4);

        revisionIds.add(IdConverter.idToUrlString(ItemRevisionId.class, itemRevision1.getRevisionId()));
        revisionIds.add(IdConverter.idToUrlString(ItemRevisionId.class, itemRevision2.getRevisionId()));
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
        String itemRevisionId1 = IdConverter.idToUrlString(ItemRevisionId.class, itemRevision1.getRevisionId());
        String itemRevisionId2 = IdConverter.idToUrlString(ItemRevisionId.class, itemRevision2.getRevisionId());

        revisionIds.add(itemRevisionId1);
        revisionIds.add(itemRevisionId2);
        getOptions.put("revisionId", revisionIds);

        setSearchStatus(CatalogEntityStatus.DRAFT.getEntityStatus(), getOptions, 2, itemRevision1, itemRevision2);
        setSearchStatus(CatalogEntityStatus.APPROVED.getEntityStatus(), getOptions, 0);
        setSearchStatus("invalidStatus", getOptions, 0);

        //release one item revision
        releaseItemRevision(itemRevision1);

        setSearchStatus(CatalogEntityStatus.DRAFT.getEntityStatus(), getOptions, 1, itemRevision2);
        setSearchStatus(CatalogEntityStatus.APPROVED.getEntityStatus(), getOptions, 1, itemRevision1);
        setSearchStatus(CatalogEntityStatus.PENDING_REVIEW.getEntityStatus(), getOptions, 0);
        setSearchStatus("invalidStatus", getOptions, 0);

        //release the both two item revisions
        releaseItemRevision(itemRevision2);

        setSearchStatus(CatalogEntityStatus.DRAFT.getEntityStatus(), getOptions, 0);
        setSearchStatus(CatalogEntityStatus.APPROVED.getEntityStatus(), getOptions, 2, itemRevision1, itemRevision2);
        setSearchStatus(CatalogEntityStatus.PENDING_REVIEW.getEntityStatus(), getOptions, 0);
        setSearchStatus("invalidStatus", getOptions, 0);
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
        String itemId1 = IdConverter.idToUrlString(ItemId.class, item1.getItemId());
        String itemId2 = IdConverter.idToUrlString(ItemId.class, item2.getItemId());
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
        revisionIds.add(IdConverter.idToUrlString(ItemRevisionId.class, itemRevision3.getRevisionId()));
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

    private void verifyInvalidScenarios(String itemRevisionId) throws Exception {
        try {
            itemRevisionService.getItemRevision(itemRevisionId, 404);
            Assert.fail("Shouldn't get item revision with wrong id");
        }
        catch (Exception e) {
            logger.logInfo("Expected exception: couldn't get item revision with wrong id");
        }
    }

    private void setSearchStatus(String status, HashMap<String, List<String>> getOptions, int expectedRtnSize, ItemRevision... itemRevisions)
            throws Exception {
        List<String> searchStatus = new ArrayList<>();
        searchStatus.add(status);
        getOptions.put("status", searchStatus);

        verifyGetResults(getOptions, expectedRtnSize, itemRevisions);
    }

    private void verifyGetResults(HashMap<String, List<String>> getOptions, int expectedRtnSize, ItemRevision... itemRevisions) throws Exception {
        Results<ItemRevision> itemRevisionsRtn = itemRevisionService.getItemRevisions(getOptions);
        Assert.assertEquals(itemRevisionsRtn.getItems().size(), expectedRtnSize);
        for (ItemRevision temp : itemRevisions) {
            Assert.assertTrue(isContain(itemRevisionsRtn, temp));
        }
    }

}

