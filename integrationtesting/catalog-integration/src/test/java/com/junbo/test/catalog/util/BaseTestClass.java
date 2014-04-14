/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.util;

import com.junbo.catalog.spec.model.item.Item;
import com.junbo.test.common.Utility.TestClass;
import com.junbo.test.common.apihelper.catalog.ItemService;
import com.junbo.test.common.apihelper.catalog.impl.ItemServiceImpl;
import com.junbo.test.common.libs.EnumHelper;

/**
 @author Jason
  * Time: 4/9/2014
  * Base test class for catalog-integration, holds some common functions
 */
public class BaseTestClass extends TestClass {

    protected String releaseItem(Item item) throws Exception {
        ItemService itemService = ItemServiceImpl.instance();
        item.setStatus(EnumHelper.CatalogEntityStatus.RELEASED.getEntityStatus());
        return itemService.updateItem(item);
    }
}