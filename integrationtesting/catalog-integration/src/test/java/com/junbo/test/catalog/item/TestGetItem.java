/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.item;

import com.junbo.test.common.apihelper.catalog.impl.ItemServiceImpl;
import com.junbo.test.common.apihelper.catalog.ItemService;
import com.junbo.test.catalog.util.BaseTestClass;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.EnumHelper;
import com.junbo.test.common.libs.LogHelper;
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

    @Property(
            priority = Priority.Dailies,
            features = "CatalogIntegration",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Get an Item by itemId(valid, invalid scenarios)",
            steps = {
                    "1. Prepare an item",
                    "2. Get the item by Id, check its curated status",
                    "3. Verify not able to get the item by invalid Id",
                    "4. Release the item",
                    "5. Get the item by Id again, verify the behavior is successful"
            }
    )
    @Test
    public void testGetAnItemById() throws Exception {

        //Prepare an item
        String itemId = itemService.postDefaultItem(EnumHelper.CatalogItemType.getRandom());
        String invalidId = "000000000";

        //get the item by Id, assert not null
        String itemRtnId = itemService.getItem(itemId);
        Assert.assertNotNull("Can't get items", itemRtnId);
        verifyInvalidScenarios(invalidId);

        //Release the item and then try to get the item
        Item item = Master.getInstance().getItem(itemId);
        releaseItem(item);

        itemRtnId = itemService.getItem(itemId);
        Assert.assertNotNull("Can't get items", itemRtnId);
        verifyInvalidScenarios(invalidId);
    }

    @Property(
            priority = Priority.Dailies,
            features = "CatalogIntegration",
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

        //prepare 5 items for later use
        String[] itemId = new String[5];
        for (int i = 0; i < itemId.length; i ++) {
            itemId[i] = itemService.postDefaultItem(EnumHelper.CatalogItemType.getByIndex(i));
        }

        HashMap<String, String> paraMap = new HashMap();

        //Set 1 item by its Id, verify 1 item could be gotten
        paraMap.put("itemId1", itemId[0]);
        verifyGetItemsScenarios(paraMap, 1, itemId[0]);

        //Set 2 items by their Ids, verify 2 items could be gotten
        paraMap.put("itemId2", itemId[1]);
        verifyGetItemsScenarios(paraMap, 2, itemId[0], itemId[1]);

        //Search the 5 items by their Ids, verify only return the 4 items
        paraMap.put("itemId3", itemId[2]);
        paraMap.put("itemId4", itemId[3]);
        paraMap.put("itemId5", itemId[4]);
        verifyGetItemsScenarios(paraMap, 5, itemId[0], itemId[1], itemId[2], itemId[3], itemId[4]);

        //Set 2 of 5 to invalid string
        paraMap.put("itemId1", "0000000000");
        paraMap.put("itemId2", "0000000001");
        verifyGetItemsScenarios(paraMap, 3, itemId[2], itemId[3], itemId[4]);

        //Release the 5 items
        for (int i = 0; i < itemId.length; i ++) {
            releaseItem(Master.getInstance().getItem(itemId[i]));
        }

        //Set 1 item by its Id, verify 1 item could be gotten
        paraMap.clear();
        paraMap.put("itemId1", itemId[0]);
        verifyGetItemsScenarios(paraMap, 1, itemId[0]);

        //Set 2 items by their Ids, verify 2 items could be gotten
        paraMap.put("itemId2", itemId[1]);
        verifyGetItemsScenarios(paraMap, 2, itemId[0], itemId[1]);

        //Search the 4 items by their Ids, verify only return the 4 items
        paraMap.put("itemId3", itemId[2]);
        paraMap.put("itemId4", itemId[3]);
        verifyGetItemsScenarios(paraMap, 4, itemId[0], itemId[1], itemId[2], itemId[3]);

        //Set all to invalid string
        paraMap.put("itemId1", "0000000000");
        paraMap.put("itemId2", "0000000001");
        paraMap.put("itemId3", "0000000002");
        paraMap.put("itemId4", "0000000003");
        paraMap.put("itemId5", "0000000004");
        verifyGetItemsScenarios(paraMap, 0);
    }

    @Property(
            priority = Priority.Comprehensive,
            features = "CatalogIntegration",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Get item(s) by Id(s), curated, type and genre(valid, invalid scenarios)",
            steps = {
                    "1. Prepare some items",
                    "2. Get the items by their ids, curated, type and genre(valid, invalid scenarios)",
                    "3. Release the items",
                    "4. Get the items by their ids, curated, type and genre(valid, invalid scenarios) again"
            }
    )
    @Test
    public void testGetItemsByIdTypeGenre() throws Exception {

        //prepare 4 items for later use
        String itemId1 = itemService.postDefaultItem(EnumHelper.CatalogItemType.DIGITAL);
        String itemId2 = itemService.postDefaultItem(EnumHelper.CatalogItemType.PHYSICAL);
        String itemId3 = itemService.postDefaultItem(EnumHelper.CatalogItemType.STORED_VALUE);
        String itemId4 = itemService.postDefaultItem(EnumHelper.CatalogItemType.SUBSCRIPTION);
        String itemId5 = itemService.postDefaultItem(EnumHelper.CatalogItemType.VIRTUAL);

        performVerification(itemId1, itemId2, itemId3, itemId4, itemId5);

        //Release the 4 items
        releaseItem(Master.getInstance().getItem(itemId1));
        releaseItem(Master.getInstance().getItem(itemId2));
        releaseItem(Master.getInstance().getItem(itemId3));
        releaseItem(Master.getInstance().getItem(itemId4));
        releaseItem(Master.getInstance().getItem(itemId5));

        performVerification(itemId1, itemId2, itemId3, itemId4, itemId5);
    }

    private void verifyInvalidScenarios(String itemId) throws Exception {
        try {
            itemService.getItem(itemId, 404);
            Assert.fail("Shouldn't get items with wrong id, status or timestamp");
        }
        catch (Exception e) {
            logger.logInfo("Expected exception: couldn't get items with wrong id, status or timestamp");
        }
    }

    private void performVerification(String itemId1, String itemId2, String itemId3, String itemId4, String itemId5)  throws Exception {

        HashMap<String, String> paraMap = new HashMap();

        //Set item ids
        paraMap.put("itemId1", itemId1);
        paraMap.put("itemId2", itemId2);
        paraMap.put("itemId3", itemId3);
        paraMap.put("itemId4", itemId4);
        paraMap.put("itemId5", itemId5);

        //set type Digital firstly
        paraMap.put("type", "DIGITAL");
        verifyGetItemsScenarios(paraMap, 1, itemId1);

        //set type to physical
        paraMap.put("type", "PHYSICAL");
        verifyGetItemsScenarios(paraMap, 1, itemId2);

        //set type to physical
        paraMap.put("type", "invalidType");
        verifyGetItemsScenarios(paraMap, 0);

        paraMap.remove("type");

        paraMap.put("type", "DIGITAL");
        verifyGetItemsScenarios(paraMap, 1, itemId1);
    }

    private void verifyGetItemsScenarios(HashMap<String, String> paraMap, int expectedRtnSize, String... itemId) throws Exception{
        List<String> itemRtnId = itemService.getItems(paraMap);

        Assert.assertEquals(itemRtnId.size(), expectedRtnSize);

        for (int i = 0; i < itemId.length; i++) {
            itemRtnId.contains(itemId[i]);
        }
    }
}
