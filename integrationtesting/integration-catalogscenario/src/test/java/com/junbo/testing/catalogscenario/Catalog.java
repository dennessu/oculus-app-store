/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.testing.catalogscenario;

import com.junbo.catalog.spec.model.attribute.Attribute;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.testing.catalogscenario.util.BaseTestClass;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.testing.common.apihelper.catalog.AttributeService;
import com.junbo.testing.common.apihelper.catalog.ItemService;
import com.junbo.testing.common.apihelper.catalog.OfferService;
import com.junbo.testing.common.apihelper.catalog.impl.AttributeServiceImpl;
import com.junbo.testing.common.apihelper.catalog.impl.ItemServiceImpl;
import com.junbo.testing.common.apihelper.catalog.impl.OfferServiceImpl;
import com.junbo.testing.common.blueprint.Master;
import com.junbo.testing.common.libs.EnumHelper;
import com.junbo.testing.common.libs.LogHelper;
import com.junbo.testing.common.libs.RandomFactory;
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
            description = "Test Attribute scenarios",
            steps = {
                    ""
            }
    )
    @Test
    public void testAttributeManagement() throws Exception {

        HashMap<String, String> paraMap = new HashMap();
        paraMap.put(EnumHelper.CatalogAttributeType.TYPE.getType(), "Digital");

        AttributeService attributeServiceAPI = AttributeServiceImpl.instance();

        Attribute attribute = new Attribute();
        attribute.setName("testAttribute_" + RandomFactory.getRandomStringOfAlphabetOrNumeric(5));
        attribute.setType("Digital");

        String attributeId = attributeServiceAPI.postAttribute(attribute);
        Assert.assertNotNull(attributeId);

        String attributeGetId = attributeServiceAPI.getAttribute(attributeId);
        Assert.assertNotNull(Master.getInstance().getAttribute(attributeGetId));

        List<String> attributeResultList = attributeServiceAPI.getAttribute(paraMap);
        Assert.assertNotNull(attributeResultList);
    }

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
        paraMap.put("status", EnumHelper.CatalogEntityStatus.DESIGN.getEntityStatus());

        ItemService itemServiceAPI = ItemServiceImpl.instance();

        String itemId = itemServiceAPI.postDefaultItem(true);
        Assert.assertNotNull(itemId);

        itemId = itemServiceAPI.postDefaultItem(false);
        Assert.assertNotNull(itemId);

        Item itemGet = Master.getInstance().getItem(itemServiceAPI.getItem(itemId, paraMap));
        Assert.assertNotNull(itemGet);

        List<String> itemResultList = itemServiceAPI.getItem(paraMap);
        Assert.assertNotNull(itemResultList);

        itemGet.setStatus(EnumHelper.CatalogEntityStatus.RELEASED.getEntityStatus());
        itemId = itemServiceAPI.updateItem(itemGet);

        Assert.assertNotNull(Master.getInstance().getItem(itemId));
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
        paraMap.put("status", EnumHelper.CatalogEntityStatus.DESIGN.getEntityStatus());

        OfferService offerServiceAPI = OfferServiceImpl.instance();

        String offerId = offerServiceAPI.postDefaultOffer(true);
        Assert.assertNotNull(offerId);

        offerId = offerServiceAPI.postDefaultOffer(false);
        Assert.assertNotNull(offerId);

        Offer offerGet = Master.getInstance().getOffer(offerServiceAPI.getOffer(offerId, paraMap));
        Assert.assertNotNull(offerGet);

        List<String> offerResultList = offerServiceAPI.getOffer(paraMap);
        Assert.assertNotNull(offerResultList);

        offerGet.setStatus(EnumHelper.CatalogEntityStatus.RELEASED.getEntityStatus());
        offerId = offerServiceAPI.updateOffer(offerGet);

        Assert.assertNotNull(Master.getInstance().getOffer(offerId));

    }
}