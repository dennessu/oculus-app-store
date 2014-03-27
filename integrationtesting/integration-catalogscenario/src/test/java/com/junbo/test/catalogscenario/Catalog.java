/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalogscenario;

import com.junbo.catalog.spec.model.attribute.Attribute;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.test.catalogscenario.util.BaseTestClass;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.test.common.apihelper.catalog.AttributeService;
import com.junbo.test.common.apihelper.catalog.ItemService;
import com.junbo.test.common.apihelper.catalog.OfferService;
import com.junbo.test.common.apihelper.catalog.impl.AttributeServiceImpl;
import com.junbo.test.common.apihelper.catalog.impl.ItemServiceImpl;
import com.junbo.test.common.apihelper.catalog.impl.OfferServiceImpl;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.EnumHelper;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.libs.RandomFactory;
import com.junbo.test.common.property.*;

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
            status = Status.Enable,
            description = "Test Attribute Post/Get",
            steps = {
                    "1. Post an attribute",
                    "2. Get the attribute by attribute ID",
                    "3. Get attributes by some search conditions",
                    "4. Get all attributes without any search condition"
            }
    )
    @Test
    public void testAttributeManagement() throws Exception {

        HashMap<String, String> paraMap = new HashMap();
        AttributeService attributeServiceAPI = AttributeServiceImpl.instance();

        ///Post an attribute and verify it got posted
        Attribute attribute = new Attribute();
        attribute.setName("testAttribute_" + RandomFactory.getRandomStringOfAlphabetOrNumeric(10));
        attribute.setType(EnumHelper.CatalogAttributeType.getRandom());
        String attributeId = attributeServiceAPI.postAttribute(attribute);
        Attribute attributeRtn = Master.getInstance().getAttribute(attributeId);
        Assert.assertNotNull(attributeRtn);

        //Get the attribute by its id and assert the return value is not null
        String attributeGetId = attributeServiceAPI.getAttribute(attributeId);
        Assert.assertNotNull(Master.getInstance().getAttribute(attributeGetId));

        //Get attributes by some get conditions, like type and id.
        paraMap.put("type", attributeRtn.getType());
        paraMap.put("id", attributeId);
        List<String> attributeResultList = attributeServiceAPI.getAttribute(paraMap);
        Assert.assertNotNull(attributeResultList);

        //Get all attributes without any search condition
        paraMap.clear();
        attributeResultList.clear();
        attributeResultList = attributeServiceAPI.getAttribute(paraMap);
        Assert.assertNotNull(attributeResultList);
    }

    @Property(
            priority = Priority.BVT,
            features = "CatalogScenarios",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Item Post/Get/Put",
            steps = {
                    "1. Post an item",
                    "2. Get the item by its id",
                    "3. Get items by some search conditions, like status and id",
                    "4. Get all items without any search condition",
                    "5. Update the item"
            }
    )
    @Test
    public void testItemManagement() throws Exception {

        HashMap<String, String> paraMap = new HashMap();
        ItemService itemServiceAPI = ItemServiceImpl.instance();

        //Post a Physical item
        String itemId = itemServiceAPI.postDefaultItem(EnumHelper.CatalogItemType.PHYSICAL);
        Assert.assertNotNull(Master.getInstance().getItem(itemId));

        //Post a Digital item
        itemId = itemServiceAPI.postDefaultItem(EnumHelper.CatalogItemType.APP);
        Assert.assertNotNull(Master.getInstance().getItem(itemId));

        //Get the item by its id(other conditions in paraMap are empty)
        String itemGetId = itemServiceAPI.getItem(itemId, paraMap);
        Assert.assertNotNull(Master.getInstance().getItem(itemGetId));

        //Get the item(s) by some conditions: by status firstly
        paraMap.put("status", EnumHelper.CatalogEntityStatus.DESIGN.getEntityStatus());
        List<String> itemResultList = itemServiceAPI.getItem(paraMap);
        Assert.assertNotNull(itemResultList);

        //Get item by id and status
        paraMap.put("id", itemGetId);
        itemResultList.clear();
        itemResultList = itemServiceAPI.getItem(paraMap);
        Assert.assertNotNull(itemResultList);

        //Get all items without any search condition
        paraMap.clear();
        itemResultList.clear();
        itemResultList = itemServiceAPI.getItem(paraMap);
        Assert.assertNotNull(itemResultList);

        //Update item to released
        Item item = Master.getInstance().getItem(itemId);
        item.setStatus(EnumHelper.CatalogEntityStatus.RELEASED.getEntityStatus());
        itemId = itemServiceAPI.updateItem(item);
        Assert.assertEquals(Master.getInstance().getItem(itemId).getStatus(), "Released");
}

    @Property(
            priority = Priority.BVT,
            features = "CatalogScenarios",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Offer Post/Get/Put",
            steps = {
                    "1. Post an offer",
                    "2. Get the offer by its id",
                    "3. Get offers by some search conditions, like status and id",
                    "4. Get all offers without any search condition",
                    "5. Update the offer"
            }
    )
    @Test
    public void testOfferManagement() throws Exception {

        HashMap<String, String> paraMap = new HashMap();
        OfferService offerServiceAPI = OfferServiceImpl.instance();

        //Post a Physical offer
        String offerId = offerServiceAPI.postDefaultOffer(EnumHelper.CatalogItemType.PHYSICAL);
        Assert.assertNotNull(Master.getInstance().getOffer(offerId));

        ////Post a Digital offer
        offerId = offerServiceAPI.postDefaultOffer(EnumHelper.CatalogItemType.APP);
        Assert.assertNotNull(Master.getInstance().getOffer(offerId));

        //Get the offer by its id(other conditions in paraMap are empty)
        String offerGetId = offerServiceAPI.getOffer(offerId, paraMap);
        Assert.assertNotNull(Master.getInstance().getOffer(offerGetId));

        //Get the offer(s) by some conditions: by status firstly
        paraMap.put("status", EnumHelper.CatalogEntityStatus.DESIGN.getEntityStatus());
        List<String> offerResultList = offerServiceAPI.getOffer(paraMap);
        Assert.assertNotNull(offerResultList);

        //Get offer by id and status
        paraMap.put("id", offerGetId);
        offerResultList.clear();
        offerResultList = offerServiceAPI.getOffer(paraMap);
        Assert.assertNotNull(offerResultList);

        //Get all offers without any search condition
        paraMap.clear();
        offerResultList.clear();
        offerResultList = offerServiceAPI.getOffer(paraMap);
        Assert.assertNotNull(offerResultList);

        //Update offer to released
        Offer offerGet = Master.getInstance().getOffer(offerId);
        offerGet.setStatus(EnumHelper.CatalogEntityStatus.RELEASED.getEntityStatus());
        offerId = offerServiceAPI.updateOffer(offerGet);
        Assert.assertEquals(Master.getInstance().getOffer(offerId).getStatus(), "Released");
    }

    @Property(
            priority = Priority.BVT,
            features = "CatalogScenarios",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Disable,
            description = "Test predefined offers",
            steps = {
            }
    )
    @Test
    public void testGetPredefinedOffers() throws Exception {
        String offer1 = "testOffer_CartCheckout_Digital1";
        String offer2 = "testOffer_CartCheckout_Digital2";
        String offer3 = "testOffer_CartCheckout_Physical1";
        String offer4 = "testOffer_CartCheckout_Physical2";

        OfferService offerServiceAPI = OfferServiceImpl.instance();

        offerServiceAPI.getOfferIdByName(offer1);
        offerServiceAPI.getOfferIdByName(offer2);
        offerServiceAPI.getOfferIdByName(offer3);
        offerServiceAPI.getOfferIdByName(offer4);
    }
}
