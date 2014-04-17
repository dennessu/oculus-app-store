/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.item;

import com.junbo.catalog.spec.model.item.Item;
import com.junbo.test.catalog.util.BaseTestClass;
import com.junbo.test.common.apihelper.catalog.ItemService;
import com.junbo.test.common.apihelper.catalog.impl.ItemServiceImpl;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.EnumHelper;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.property.*;

import java.util.*;
import junit.framework.Assert;
import org.testng.annotations.Test;

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
                    "2. Get the item by Id, check its status",
                    "3. Verify not able to get the item by invalid Id",
                    "4. Release the item",
                    "5. Get the item by Id again, verify the behavior is successful"
            }
    )
    @Test
    public void testGetAnItemById() throws Exception {

        String itemId = itemService.postDefaultItem(EnumHelper.CatalogItemType.DIGITAL);

        //Just get the item by Id, check its status
        verifyValidScenarios(itemId, EnumHelper.CatalogEntityStatus.DRAFT);

        String invalidId = "000000000";
        verifyInvalidScenarios(invalidId);

        //Release the item and then try to get the item
        Item item = Master.getInstance().getItem(itemId);
        itemId = releaseItem(item);

        verifyValidScenarios(itemId, EnumHelper.CatalogEntityStatus.APPROVED);
        verifyInvalidScenarios(invalidId);
    }

    @Property(
            priority = Priority.Dailies,
            features = "CatalogIntegration",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Get item(s) by Id(s), status(valid, invalid scenarios)",
            steps = {
                    "1. Prepare some items",
                    "2. Get the items by their ids",
                    "3. Release the item",
                    "4. Get the items by their ids again"
            }
    )
    @Test
    public void testGetItemsByIds() throws Exception {

        //prepare 3 items for later use
        String itemId1 = itemService.postDefaultItem(EnumHelper.CatalogItemType.DIGITAL);
        String itemId2 = itemService.postDefaultItem(EnumHelper.CatalogItemType.PHYSICAL);
        String itemId3 = itemService.postDefaultItem(EnumHelper.CatalogItemType.EWALLET);

        //Search the 3 items by their Ids, verify only return the 3 items
        verifyItemsValidScenarios(null, null, itemId1, itemId2, itemId3);

        //Set 2 items by their Ids, verify 2 items could be gotten
        verifyItemsValidScenarios(null, null, itemId1, itemId2);

        //Set 1 item by its Id, verify 1 item could be gotten
        verifyItemsValidScenarios(null, null, itemId1);

        //Set all to invalid string
        HashMap<String, String> paraMap = new HashMap();

        paraMap.put("id1", "0000000000");
        paraMap.put("id2", "0000000001");
        paraMap.put("id3", "0000000002");
        verifyItemsInvalidScenarios(paraMap);

        //Release the 3 items
        releaseItem(Master.getInstance().getItem(itemId1));
        releaseItem(Master.getInstance().getItem(itemId2));
        releaseItem(Master.getInstance().getItem(itemId3));

        //Search the 3 items by their Ids, verify only return the 3 items
        verifyItemsValidScenarios(null, null, itemId1, itemId2, itemId3);

        //Set 2 items by their Ids, verify 2 items could be gotten
        verifyItemsValidScenarios(null, null, itemId1, itemId2);

        //Set 1 item by its Id, verify 1 item could be gotten
        verifyItemsValidScenarios(null, null, itemId1);

        //Set all to invalid string
        paraMap.put("id1", "0000000000");
        paraMap.put("id2", "0000000001");
        paraMap.put("id3", "0000000002");
        verifyItemsInvalidScenarios(paraMap);
    }

    @Property(
            priority = Priority.Dailies,
            features = "CatalogIntegration",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Get item(s) by Id(s), status(valid, invalid scenarios)",
            steps = {
                    "1. Prepare some items",
                    "2. Get the items by their ids",
                    "3. Release the item",
                    "4. Get the items by their ids again"
            }
    )
    @Test
    public void testGetItemsByIdsStatusTimeStamp() throws Exception {

        //prepare 3 items for later use
        String itemId1 = itemService.postDefaultItem(EnumHelper.CatalogItemType.DIGITAL);
        String itemId2 = itemService.postDefaultItem(EnumHelper.CatalogItemType.PHYSICAL);
        String itemId3 = itemService.postDefaultItem(EnumHelper.CatalogItemType.EWALLET);

        //Release the 3 items
        releaseItem(Master.getInstance().getItem(itemId1));
        releaseItem(Master.getInstance().getItem(itemId2));
        releaseItem(Master.getInstance().getItem(itemId3));

        //Verify the 3 items
        String status = EnumHelper.CatalogEntityStatus.APPROVED.getEntityStatus();
        Long currentTime = Calendar.getInstance().getTimeInMillis();
        verifyItemsValidScenarios(status, currentTime.toString(), itemId1, itemId2, itemId3);

        //Verify the invalid scenarios
        currentTime = 1L;

        HashMap<String, String> paraMap = new HashMap();
        paraMap.put("status", status);
        paraMap.put("timestamp", currentTime.toString());

        verifyItemsInvalidScenarios(paraMap);
    }

    private void verifyValidScenarios(String itemId, EnumHelper.CatalogEntityStatus expectedStatus) throws Exception {
        String itemRtnId = itemService.getItem(itemId);
        Assert.assertNotNull("Can't get items", itemRtnId);
        Assert.assertEquals(expectedStatus.getEntityStatus(), Master.getInstance().getItem(itemRtnId).getCurated());
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

    private void verifyItemsValidScenarios(String status, String timestamp, String... itemId) throws Exception{

        HashMap<String, String> paraMap = new HashMap();

        paraMap.put("id1", "0000000000");
        paraMap.put("id2", "0000000001");
        paraMap.put("id3", "0000000002");

        for (int i = 0; i < itemId.length; i++) {
            paraMap.put("id" + (i + 1), itemId[i]);
        }

        if (status != null && !status.isEmpty()){
            paraMap.put("status", status);
        }

        if (timestamp != null && !timestamp.isEmpty()){
            paraMap.put("timestamp", timestamp);
        }

        List<String> itemRtnId = itemService.getItem(paraMap);
        Assert.assertEquals(itemRtnId.size(), itemId.length);
        for (int i = 0; i < itemId.length; i++) {
            Assert.assertTrue(itemRtnId.contains(itemId[i]));
        }
    }

    private void verifyItemsInvalidScenarios(HashMap<String, String> paraMap) throws Exception{
        List<String> itemRtnId = itemService.getItem(paraMap);
        Assert.assertEquals(itemRtnId.size(), 0);
    }
}
