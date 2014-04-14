/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.item;

import com.junbo.test.catalog.ItemResource;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.test.catalog.util.BaseTestClass;
import com.junbo.test.common.apihelper.catalog.ItemService;
import com.junbo.test.common.apihelper.catalog.impl.ItemServiceImpl;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.EnumHelper;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import junit.framework.Assert;
import org.testng.annotations.Test;

/**
  * @author Jason
  * Time: 4/3/2014
  * For testing catalog delete item(s) API
*/
public class TestDeleteItem extends BaseTestClass {

    @Property(
            priority = Priority.Dailies,
            features = "CatalogIntegration",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test delete an Item by itemId(valid, invalid scenarios)",
            steps = {
                    "1. Prepare an item(don't release it)",
                    "2. Delete the item, and verify it couldn't be found",
                    "3. Prepare an item and release it",
                    "4. Delete the item, and verify it couldn't be found",
                    "5. Try to delete a non-existing item, and expect to get a 404 error code"
            }
    )
    @Test
    public void testDeleteItemById() throws Exception {
        //prepare an item
        ItemService itemService = ItemServiceImpl.instance();
        String itemId = itemService.postDefaultItem(EnumHelper.CatalogItemType.APP);

        //Delete the item, and verify it couldn't be found
        ItemResource itemResource = ItemResource.instance();
        itemResource.deleteItem(itemId);

        try {
            itemService.getItem(itemId, null, 404);
            Assert.fail("Shouldn't get a deleted item");
        }
        catch (Exception ex) {
        }

        //Post and release another item, then delete the item again, verify it couldn't be found
        itemId = itemService.postDefaultItem(EnumHelper.CatalogItemType.APP);
        Item item = Master.getInstance().getItem(itemId);
        releaseItem(item);
        itemResource.deleteItem(itemId);

        try {
            itemService.getItem(itemId, null, 404);
            Assert.fail("Shouldn't get a deleted item");
        }
        catch (Exception ex) {
        }

        //Try to delete with non-existing Id and verify status code is 404
        String nonExistingId = "0000000000F";
        itemResource.deleteItem(nonExistingId, 404);
    }
}
