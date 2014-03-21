/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.testing.catalogscenario;

import com.junbo.testing.catalogscenario.util.BaseTestClass;
import com.junbo.catalog.spec.model.common.ResultList;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.testing.common.apihelper.catalog.ItemService;
import com.junbo.testing.common.apihelper.catalog.OfferService;
import com.junbo.testing.common.apihelper.catalog.impl.ItemServiceImpl;
import com.junbo.testing.common.apihelper.catalog.impl.OfferServiceImpl;
import com.junbo.testing.common.blueprint.Master;
import com.junbo.testing.common.libs.LogHelper;
import com.junbo.testing.common.property.*;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;

/**
 @author Jason
  * Time: 3/14/2014
  * For testing catalog scenarios
 */
public class Catalog extends BaseTestClass {

    private LogHelper logger = new LogHelper(Catalog.class);

    @Property(
            priority = Priority.BVT,
            features = "CatalogScenarios",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Incomplete,
            description = "Test Item scenarios",
            steps = {
                    ""
            }
    )
    @Test
    public void testItemManagement() throws Exception {

        HashMap<String, String> paraMap = new HashMap();
        paraMap.put("status", "Design");

        ItemService itemServiceAPI = ItemServiceImpl.instance();

        String itemId = itemServiceAPI.postDefaultItem();
        Assert.assertNotNull(itemId);

        List<String> itemResultList = itemServiceAPI.getItem(paraMap);
        Assert.assertNotNull(itemResultList);

        Item itemGet = Master.getInstance().getItem(itemServiceAPI.getItem(itemId, paraMap));
        Assert.assertNotNull(itemGet);
    }

    @Property(
            priority = Priority.BVT,
            features = "CatalogScenarios",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Incomplete,
            description = "Test Offer scenarios",
            steps = {
                    ""
            }
    )
    @Test
    public void testOfferManagement() throws Exception {

        HashMap<String, String> paraMap = new HashMap();
        paraMap.put("status", "Design");

        OfferService offerServiceAPI = OfferServiceImpl.instance();

        String offerId = offerServiceAPI.postDefaultOffer();
        Assert.assertNotNull(offerId);

    }
}