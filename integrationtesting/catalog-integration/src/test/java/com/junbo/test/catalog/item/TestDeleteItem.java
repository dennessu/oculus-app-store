/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.item;

import com.junbo.test.catalog.ItemResource;
import com.junbo.test.common.apihelper.catalog.ItemService;
import com.junbo.test.common.apihelper.catalog.impl.ItemServiceImpl;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.EnumHelper;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.property.*;

import junit.framework.Assert;
import org.testng.annotations.Test;

/**
 * @author Jason
 * Time: 4/21/2014
 * For testing catalog delet item(s) API
 */
public class TestDeleteItem {

    private LogHelper logger = new LogHelper(TestGetItem.class);

    @Property(
            priority = Priority.Dailies,
            features = "CatalogIntegration",
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
        String itemId = itemService.postDefaultItem(EnumHelper.CatalogItemType.getRandom());
        String invalidId = "000000000";
        String itemRtnId;

        ItemResource.instance().deleteItem(itemId);

        //Expected status code is 404.
        try {
            itemRtnId = itemService.getItem(itemId, 404);
            Assert.fail("Couldn't find the deleted item");
        }
        catch (Exception ex)
        {
        }

        //delete non-existing item
        itemId = itemService.postDefaultItem(EnumHelper.CatalogItemType.getRandom());
        ItemResource.instance().deleteItem(invalidId, 404);
        itemRtnId = itemService.getItem(itemId);
        Assert.assertNotNull(Master.getInstance().getItem(itemRtnId));
    }
}
