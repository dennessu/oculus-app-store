/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.item;

import com.junbo.catalog.spec.model.item.Item;
import com.junbo.test.common.apihelper.catalog.ItemService;
import com.junbo.test.common.apihelper.catalog.impl.ItemServiceImpl;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.EnumHelper;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.property.*;
import org.testng.annotations.Test;

/**
 * @author Jason
 * Time: 4/10/2014
 * For testing catalog put item(s) API
 */
public class TestPutItem {

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
                    "1. Post an default item",
                    "2. Verify the parameters",
                    "3. Post test item with optional params",
                    "4. Verify the parameters"
            }
    )

    @Test
    public void testPutItem() throws Exception {

        String defaultItemId = itemService.postDefaultItem(EnumHelper.CatalogItemType.DIGITAL);
        Item defaultItem = Master.getInstance().getItem(defaultItemId);

    }
}
