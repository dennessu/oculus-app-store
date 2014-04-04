/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.item;

import com.junbo.catalog.spec.model.item.Item;
import com.junbo.test.common.Utility.TestClass;
import com.junbo.test.common.apihelper.catalog.ItemService;
import com.junbo.test.common.apihelper.catalog.impl.ItemServiceImpl;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.EnumHelper;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.property.*;

import junit.framework.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Calendar;
import java.sql.Timestamp;

/**
 @author Jason
  * Time: 4/1/2014
  * For testing catalog get item(s) API
 */
public class TestGetItem extends TestClass {

    private LogHelper logger = new LogHelper(TestGetItem.class);

    private ItemService itemService = ItemServiceImpl.instance();

    @Property(
            priority = Priority.BVT,
            features = "CatalogIntegration",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Incomplete,
            description = "Test Get an Item by itemId and other parameters(timestamp & status)",
            steps = {
                    "1. Prepare an item"
            }
    )
    @Test
    public void testGetAnItem() throws Exception {

        HashMap<String, String> paraMap = new HashMap();

        String itemId = itemService.postDefaultItem(EnumHelper.CatalogItemType.APP);
        Item itemPrepared = Master.getInstance().getItem(itemId);

        //Just get the item by Id
        String itemRtnId = itemService.getItem(itemId, paraMap);
        Assert.assertNotNull("Can't get item by its id", itemRtnId);

        //Get the item by Id and status, the status is design currently
        paraMap.put("status", EnumHelper.CatalogEntityStatus.DESIGN.getEntityStatus());
        itemRtnId = itemService.getItem(itemId, paraMap);
        Assert.assertNotNull("Can't get item by its id and status", itemRtnId);

        //Release the item
        this.releaseItem(Master.getInstance().getItem(itemRtnId));

        //Get the item by Id and status, the status is released now
        paraMap.clear();
        paraMap.put("status", EnumHelper.CatalogEntityStatus.RELEASED.getEntityStatus());
        itemRtnId = itemService.getItem(itemId, paraMap);
        Assert.assertNotNull("Can't get item by its id and status", itemRtnId);

        //Get the item by Id, status and timestamp
        Long currentTime = Calendar.getInstance().getTimeInMillis();

        paraMap.put("timestamp", EnumHelper.CatalogEntityStatus.RELEASED.getEntityStatus());

    }

    private String releaseItem(Item item) throws Exception {
        item.setStatus(EnumHelper.CatalogEntityStatus.RELEASED.getEntityStatus());
        return itemService.updateItem(item);
    }

}
