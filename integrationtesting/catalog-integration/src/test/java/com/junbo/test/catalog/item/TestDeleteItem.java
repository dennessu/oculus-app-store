/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.item;

import com.junbo.test.catalog.enums.CatalogItemType;
import com.junbo.test.catalog.impl.ItemServiceImpl;
import com.junbo.test.catalog.util.BaseTestClass;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.catalog.ItemService;
import com.junbo.test.common.property.*;

import org.testng.annotations.Test;
import org.testng.Assert;

/**
 * @author Jason
 * Time: 4/21/2014
 * For testing catalog delet item(s) API
 */
public class TestDeleteItem extends BaseTestClass {

    private LogHelper logger = new LogHelper(TestDeleteItem.class);

    @Property(
            priority = Priority.Dailies,
            features = "Delete v1/items/{itemId}",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test delete an item by itemId",
            steps = {
                    "1. Prepare an item",
                    "2. delete it and verify can't search it"
            }
    )
    @Test
    public void testDeleteItem() throws Exception {
        ItemService itemService = ItemServiceImpl.instance();

        //Prepare an item
        Item item = itemService.postDefaultItem(CatalogItemType.getRandom());
        String invalidId = "0L";

        itemService.deleteItem(item.getItemId());

        //Try to get the item, expected status code is 404.
        try {
            itemService.getItem(item.getItemId(), 404);
            Assert.fail("Couldn't find the deleted item");
        }
        catch (Exception ex)
        {
        }

        //delete non-existing item
        item = itemService.postDefaultItem(CatalogItemType.getRandom());
        itemService.deleteItem(invalidId, 404);
        item = itemService.getItem(item.getItemId());
        Assert.assertNotNull(item);
    }

}
