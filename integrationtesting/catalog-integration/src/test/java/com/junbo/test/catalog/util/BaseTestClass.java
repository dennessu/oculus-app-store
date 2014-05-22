/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.util;

import com.junbo.test.catalog.impl.ItemRevisionServiceImpl;
import com.junbo.test.catalog.enums.CatalogEntityStatus;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.test.catalog.ItemRevisionService;
import com.junbo.test.common.Utility.TestClass;
import com.junbo.catalog.spec.model.item.Item;

/**
 @author Jason
  * Time: 4/9/2014
  * Base test class for catalog-integration, holds some common functions
 */
public class BaseTestClass extends TestClass {

    protected final String defaultItemFileName = "defaultItem";
    protected final String defaultItemRevisionFileName = "defaultItemRevision";
    protected final String defaultOfferFileName = "defaultOffer";
    protected final String defaultOfferRevisionFileName = "defaultOfferRevision";

    protected void releaseItem(Item item) throws Exception {
        ItemRevisionService itemRevisionService = ItemRevisionServiceImpl.instance();

        //Attach item revision to the item
        ItemRevision itemRevision = itemRevisionService.postDefaultItemRevision(item);

        //Approve the item revision
        itemRevision.setStatus(CatalogEntityStatus.APPROVED.getEntityStatus());
        itemRevisionService.updateItemRevision(itemRevision.getRevisionId(), itemRevision);
    }

}