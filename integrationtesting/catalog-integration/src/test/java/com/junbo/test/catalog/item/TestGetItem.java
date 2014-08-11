/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.item;

import com.junbo.test.common.apihelper.identity.impl.OrganizationServiceImpl;
import com.junbo.test.common.apihelper.identity.OrganizationService;
import com.junbo.test.catalog.enums.CatalogItemType;
import com.junbo.test.catalog.impl.ItemServiceImpl;
import com.junbo.test.catalog.util.BaseTestClass;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.catalog.ItemService;
import com.junbo.common.id.OrganizationId;
import com.junbo.test.common.property.*;

import org.testng.annotations.Test;
import org.testng.Assert;

import java.util.*;

/**
  * @author Jason
  * Time: 4/1/2014
  * For testing catalog get item(s) API
*/
public class TestGetItem extends BaseTestClass {

    private LogHelper logger = new LogHelper(TestGetItem.class);
    private ItemService itemService = ItemServiceImpl.instance();
    private OrganizationId organizationId;

    private void prepareTestData() throws Exception {
        OrganizationService organizationService = OrganizationServiceImpl.instance();
        organizationId = organizationService.postDefaultOrganization().getId();
    }

    @Property(
            priority = Priority.Dailies,
            features = "Get v1/items/{itemId}",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Get an Item by itemId(valid, invalid scenarios)",
            steps = {
                    "1. Prepare an item",
                    "2. Get the item by Id",
                    "3. Verify not able to get the item by invalid Id",
                    "4. Release the item",
                    "5. Get the item by Id again, verify the behavior is successful"
            }
    )
    @Test
    public void testGetAnItemById() throws Exception {
        this.prepareTestData();

        //Prepare an item
        Item item = itemService.postDefaultItem(CatalogItemType.getRandom(), organizationId);
        String itemId = item.getItemId();
        String invalidId = "0L";

        //get the item by Id, assert not null
        Item itemRtn = itemService.getItem(itemId);
        Assert.assertNotNull(itemRtn, "Can't get items");

        //verify the invalid Id scenario
        verifyInvalidScenarios(invalidId);

        //Release the item and then try to get the item
        releaseItem(item);

        itemRtn = itemService.getItem(itemId);
        Assert.assertNotNull(itemRtn, "Can't get items");

        //verify the invalid Id scenario
        verifyInvalidScenarios(invalidId);
    }

    @Property(
            priority = Priority.Dailies,
            features = "Get v1/items?itemId=&itemId=",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Get item(s) by Id(s)(valid, invalid scenarios)",
            steps = {
                    "1. Prepare some items",
                    "2. Get the items by their ids",
                    "3. Release the items",
                    "4. Get the items by their ids again"
            }
    )
    @Test
    public void testGetItemsByIds() throws Exception {
        this.prepareTestData();

        //prepare 5 items for later use
        Item[] items = new Item[5];
        String[] itemId = new String[5];
        for (int i = 0; i < items.length; i ++) {
            items[i] = itemService.postDefaultItem(CatalogItemType.getByIndex(i), organizationId);
            itemId[i] = items[i].getItemId();
        }

        HashMap<String, List<String>> paraMap = new HashMap<>();
        List<String> listItemId = new ArrayList<>();

        //Set 1 item by its Id, verify 1 item could be gotten
        listItemId.add(itemId[0]);
        paraMap.put("itemId", listItemId);
        verifyGetItemsScenarios(paraMap, 1, itemId[0]);

        //Set 2 items by their Ids, verify 2 items could be gotten
        listItemId.add(itemId[1]);
        paraMap.put("itemId", listItemId);
        verifyGetItemsScenarios(paraMap, 2, itemId[0], itemId[1]);

        //Search the 5 items by their Ids, verify all could be got
        listItemId.add(itemId[2]);
        listItemId.add(itemId[3]);
        listItemId.add(itemId[4]);
        paraMap.put("itemId", listItemId);
        verifyGetItemsScenarios(paraMap, 5, itemId[0], itemId[1], itemId[2], itemId[3], itemId[4]);

        //Set 2 of 5 to invalid string
        listItemId.clear();
        listItemId.add(itemId[2]);
        listItemId.add(itemId[3]);
        listItemId.add(itemId[4]);
        listItemId.add("0000000000");
        listItemId.add("0000000001");
        paraMap.put("itemId", listItemId);
        verifyGetItemsScenarios(paraMap, 3, itemId[2], itemId[3], itemId[4]);

        //Release the 5 items
        for (Item item : items) {
            releaseItem(item);
        }

        //Set 1 item by its Id, verify 1 item could be gotten
        listItemId.clear();
        listItemId.add(itemId[0]);
        paraMap.put("itemId", listItemId);
        verifyGetItemsScenarios(paraMap, 1, itemId[0]);

        //Set 2 items by their Ids, verify 2 items could be gotten
        listItemId.add(itemId[1]);
        paraMap.put("itemId", listItemId);
        verifyGetItemsScenarios(paraMap, 2, itemId[0], itemId[1]);

        //Search the 4 items by their Ids, verify only return the 4 items
        listItemId.add(itemId[2]);
        listItemId.add(itemId[3]);
        paraMap.put("itemId", listItemId);
        verifyGetItemsScenarios(paraMap, 4, itemId[0], itemId[1], itemId[2], itemId[3]);

        //Set all to invalid string
        listItemId.clear();
        listItemId.add("0000000000");
        listItemId.add("0000000001");
        listItemId.add("0000000002");
        listItemId.add("0000000003");
        listItemId.add("0000000004");
        paraMap.put("itemId", listItemId);
        verifyGetItemsScenarios(paraMap, 0);
    }

    @Property(
            priority = Priority.Comprehensive,
            features = "Get v1/items?itemId=&type=",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Get item(s) by Id(s), type(valid, invalid scenarios)",
            steps = {
                    "1. Prepare some items",
                    "2. Get the items by their ids, type(valid, invalid scenarios)",
                    "3. Release the items",
                    "4. Get the items by their ids, type(valid, invalid scenarios) again"
            }
    )
    @Test
    public void testGetItemsByIdType() throws Exception {
        this.prepareTestData();

        //prepare 5 items for later use
        Item[] items = new Item[5];
        String[] itemId = new String[5];
        for (int i = 0; i < items.length; i ++) {
            items[i] = itemService.postDefaultItem(CatalogItemType.getByIndex(i), organizationId);
            itemId[i] = items[i].getItemId();
        }

        performVerification(itemId[0], itemId[1], itemId[2], itemId[3], itemId[4]);

        //Release the 5 items
        for (Item item : items) {
            releaseItem(item);
        }

        performVerification(itemId[0], itemId[1], itemId[2], itemId[3], itemId[4]);
    }

    private void verifyInvalidScenarios(String itemId) throws Exception {
        try {
            itemService.getItem(itemId, 404);
            Assert.fail("Shouldn't get items with wrong id");
        }
        catch (Exception e) {
            logger.logInfo("Expected exception: couldn't get items with wrong id");
        }
    }

    private void performVerification(String itemId1, String itemId2, String itemId3, String itemId4, String itemId5)  throws Exception {

        HashMap<String, List<String>> paraMap = new HashMap<>();
        List<String> listItemId = new ArrayList<>();
        List<String> listType = new ArrayList<>();

        //Set item ids
        listItemId.add(itemId1);
        listItemId.add(itemId2);
        listItemId.add(itemId3);
        listItemId.add(itemId4);
        listItemId.add(itemId5);

        //set type PHYSICAL firstly
        listType.add(CatalogItemType.PHYSICAL.getItemType());
        paraMap.put("itemId", listItemId);
        paraMap.put("type", listType);
        verifyGetItemsScenarios(paraMap, 1, itemId1);

        //set type to DIGITAL
        listType.clear();
        listType.add(CatalogItemType.APP.getItemType());
        paraMap.put("type", listType);
        verifyGetItemsScenarios(paraMap, 1, itemId2);

        //set type to invalid type
        listType.clear();
        listType.add("invalidType");
        paraMap.put("type", listType);
        verifyGetItemsScenarios(paraMap, 0);
    }

}
