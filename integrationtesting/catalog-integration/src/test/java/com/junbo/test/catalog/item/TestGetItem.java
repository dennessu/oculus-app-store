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

        //get the item by Id, check its status
        verifyValidScenarios(itemId, false);
        verifyInvalidScenarios(invalidId);

        //Release the item and then try to get the item
        Item item = Master.getInstance().getItem(itemId);
        releaseItem(item);

        verifyValidScenarios(itemId, true);
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

        //prepare 4 items for later use
        String itemId1 = itemService.postDefaultItem(EnumHelper.CatalogItemType.DIGITAL);
        String itemId2 = itemService.postDefaultItem(EnumHelper.CatalogItemType.PHYSICAL);
        String itemId3 = itemService.postDefaultItem(EnumHelper.CatalogItemType.STORED_VALUE);
        String itemId4 = itemService.postDefaultItem(EnumHelper.CatalogItemType.SUBSCRIPTION);

        HashMap<String, String> paraMap = new HashMap();

        //Set 1 item by its Id, verify 1 item could be gotten
        paraMap.put("id1", itemId1);
        verifyGetItemsScenarios(paraMap, 1, itemId1);

        //Set 2 items by their Ids, verify 2 items could be gotten
        paraMap.put("id2", itemId2);
        verifyGetItemsScenarios(paraMap, 2, itemId1, itemId2);

        //Search the 4 items by their Ids, verify only return the 4 items
        paraMap.put("id3", itemId3);
        paraMap.put("id4", itemId4);
        verifyGetItemsScenarios(paraMap, 4, itemId1, itemId2, itemId3, itemId4);

        //Set 2 of 4 to invalid string
        paraMap.put("id1", "0000000000");
        paraMap.put("id2", "0000000001");
        verifyGetItemsScenarios(paraMap, 2, itemId3, itemId4);

        //Release the 4 items
        releaseItem(Master.getInstance().getItem(itemId1));
        releaseItem(Master.getInstance().getItem(itemId2));
        releaseItem(Master.getInstance().getItem(itemId3));
        releaseItem(Master.getInstance().getItem(itemId4));

        //Set 1 item by its Id, verify 1 item could be gotten
        paraMap.clear();
        paraMap.put("id1", itemId1);
        verifyGetItemsScenarios(paraMap, 1, itemId1);

        //Set 2 items by their Ids, verify 2 items could be gotten
        paraMap.put("id2", itemId2);
        verifyGetItemsScenarios(paraMap, 2, itemId1, itemId2);

        //Search the 4 items by their Ids, verify only return the 4 items
        paraMap.put("id3", itemId3);
        paraMap.put("id4", itemId4);
        verifyGetItemsScenarios(paraMap, 4, itemId1, itemId2, itemId3, itemId4);

        //Set all to invalid string
        paraMap.put("id1", "0000000000");
        paraMap.put("id2", "0000000001");
        paraMap.put("id3", "0000000002");
        paraMap.put("id4", "0000000003");
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

        performVerification(itemId1, itemId2, itemId3, itemId4);

        //Release the 4 items
        releaseItem(Master.getInstance().getItem(itemId1));
        releaseItem(Master.getInstance().getItem(itemId2));
        releaseItem(Master.getInstance().getItem(itemId3));
        releaseItem(Master.getInstance().getItem(itemId4));

        performVerification(itemId1, itemId2, itemId3, itemId4);
    }

    private void verifyValidScenarios(String itemId, boolean status) throws Exception {
        String itemRtnId = itemService.getItem(itemId);
        Assert.assertNotNull("Can't get items", itemRtnId);
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

    private void performVerification(String itemId1, String itemId2, String itemId3, String itemId4)  throws Exception {

        HashMap<String, String> paraMap = new HashMap();

        //Set item ids
        paraMap.put("id1", itemId1);
        paraMap.put("id2", itemId2);
        paraMap.put("id3", itemId3);
        paraMap.put("id4", itemId4);

        //set curated false
        paraMap.put("curated", "false");
        verifyGetItemsScenarios(paraMap, 4, itemId1, itemId2, itemId3, itemId4);

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

        //Set curated true
        paraMap.put("curated", "true");
        verifyGetItemsScenarios(paraMap, 0);

        paraMap.put("curated", "invalidStatus");
        verifyGetItemsScenarios(paraMap, 0);

        paraMap.put("type", "DIGITAL");
        paraMap.put("curated", "false");
        paraMap.put("genre", Master.getInstance().getItem(itemId1).getGenres().get(0).toString());
        verifyGetItemsScenarios(paraMap, 1, itemId1);

        paraMap.put("genre", "1111111");
        verifyGetItemsScenarios(paraMap, 0);
    }

    private void verifyGetItemsScenarios(HashMap<String, String> paraMap, int expectedRtnSize, String... itemId) throws Exception{
        List<String> itemRtnId = itemService.getItems(paraMap);

        Assert.assertEquals(itemRtnId.size(), expectedRtnSize);

        for (int i = 0; i < itemId.length; i++) {
            itemRtnId.contains(itemId[i]);
        }
    }
}
