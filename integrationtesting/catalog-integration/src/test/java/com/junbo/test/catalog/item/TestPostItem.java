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
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.property.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
  * @author Jason
  * Time: 4/1/2014
  * For testing catalog post item(s) API
 */
public class TestPostItem extends BaseTestClass {

    private LogHelper logger = new LogHelper(TestGetItem.class);
    private ItemService itemService = ItemServiceImpl.instance();
    private final String itemRequiredPara = "itemWithRequiredPara";
    private final String defaultItem = "defaultItem";

    @Property(
            priority = Priority.Dailies,
            features = "CatalogIntegration",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Get an Item by itemId(valid, invalid scenarios)",
            steps = {
                    "1. Post test items only with required parameters",
                    "2. Verify the parameters",
                    "3. Post test item with optional params",
                    "4. Verify the parameters"
            }
    )
    @Test
    public void testPostItem() throws Exception {

        //Post test items only with required parameters
        Item testItemRequired = itemService.prepareItemEntity(itemRequiredPara);
        String itemRtnId1 = itemService.postItem(testItemRequired);
        Item returnedItem1 = Master.getInstance().getItem(itemRtnId1);

        checkItemRequiredParams(returnedItem1, testItemRequired);

        //Post test item with optional params
        Item testItemFull = itemService.prepareItemEntity(defaultItem);
        String itemRtnId2 = itemService.postItem(testItemFull);
        Item returnedItem2 = Master.getInstance().getItem(itemRtnId2);

        checkItemRequiredParams(returnedItem2, testItemFull);
        checkItemOptionalParams(returnedItem2, testItemFull);
    }

    @Property(
            priority = Priority.Comprehensive,
            features = "CatalogIntegration",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Get an Item by itemId(valid, invalid scenarios)",
            steps = {
                    "1. Prepare an item",
                    "2. Set the required parameters to null",
                    "3. Try to post it and verify the expected error"
            }
    )
    @Test
    public void testPostItemInvalidScenarios() throws Exception {

        Item testItem = itemService.prepareItemEntity(defaultItem);
        testItem.setName(null);
        try {
            //Error code 400 means "Missing Input field"
            itemService.postItem(testItem, 400);
            Assert.fail("Post item should fail");
        }
        catch (Exception ex) {
        }

        testItem = itemService.prepareItemEntity(defaultItem);
        testItem.setType(null);
        try {
            //Error code 400 means "Missing Input field"
            itemService.postItem(testItem, 400);
            Assert.fail("Post item should fail");
        }
        catch (Exception ex) {
        }

        testItem = itemService.prepareItemEntity(defaultItem);
        testItem.setOwnerId(null);
        try {
            //Error code 400 means "Missing Input field"
            itemService.postItem(testItem, 400);
            Assert.fail("Post item should fail");
        }
        catch (Exception ex) {
        }

        testItem = itemService.prepareItemEntity(defaultItem);
        testItem.setType("invalidItemType");
        try {
            //Error code 400 means "Missing Input field"
            itemService.postItem(testItem, 400);
            Assert.fail("Post item should fail");
        }
        catch (Exception ex) {
        }
    }

    private void checkItemRequiredParams(Item item1, Item item2) {
        Assert.assertEquals(item1.getType(), item2.getType());
        Assert.assertEquals(item1.getName(), item2.getName());
        Assert.assertEquals(item1.getOwnerId(), item2.getOwnerId());
    }

    private void checkItemOptionalParams(Item item1, Item item2) {
        Assert.assertEquals(item1.getSkus(), item2.getSkus());
        Assert.assertEquals(item1.getProperties(), item2.getProperties());
    }

}
