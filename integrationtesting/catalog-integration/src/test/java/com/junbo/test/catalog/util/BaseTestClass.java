/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.util;

import com.esotericsoftware.kryo.util.IdentityObjectIntMap;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.common.id.ItemId;
import com.junbo.test.common.Utility.TestClass;
import com.junbo.test.common.apihelper.catalog.ItemRevisionService;
import com.junbo.test.common.apihelper.catalog.ItemService;
import com.junbo.test.common.apihelper.catalog.impl.ItemRevisionServiceImpl;
import com.junbo.test.common.apihelper.catalog.impl.ItemServiceImpl;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.EnumHelper;
import com.junbo.test.common.libs.IdConverter;

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
        String itemId = IdConverter.idLongToHexString(ItemId.class, item.getItemId());
        String itemRevisionId = itemRevisionService.postDefaultItemRevision(itemId);

        //Approve the item revision
        ItemRevision itemRevision = Master.getInstance().getItemRevision(itemRevisionId);
        itemRevision.setStatus(EnumHelper.CatalogEntityStatus.APPROVED.getEntityStatus());
        itemRevisionService.updateItemRevision(itemRevision);
    }
}