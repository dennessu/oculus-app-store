/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalogscenario;

import com.junbo.catalog.spec.model.attribute.Attribute;
import com.junbo.catalog.spec.model.common.LocalizableProperty;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.common.id.ItemId;
import com.junbo.common.id.ItemRevisionId;
import com.junbo.common.id.OfferId;
import com.junbo.test.common.Utility.TestClass;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.test.common.apihelper.catalog.*;
import com.junbo.test.common.apihelper.catalog.impl.*;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.EnumHelper;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.libs.RandomFactory;
import com.junbo.test.common.property.*;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;

/**
  * @author Jason
  * Time: 3/14/2014
  * For testing catalog scenarios
*/
public class Catalog extends TestClass {

    private LogHelper logger = new LogHelper(Catalog.class);
    private final String defaultItemFileName = "defaultItem";
    private final String defaultItemRevisionFileName = "defaultItemRevision";
    private final String defaultOfferFileName = "defaultOffer";
    private final String defaultOfferRevisionFileName = "defaultOfferRevision";

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
        LocalizableProperty attributeName = new LocalizableProperty();
        attributeName.set("en_US", RandomFactory.getRandomStringOfAlphabet(10));
        attribute.setName(attributeName);
        attribute.setType(EnumHelper.CatalogAttributeType.getRandom());
        logger.LogSample("Post an attribute");
        String attributeId = attributeServiceAPI.postAttribute(attribute);
        Attribute attributeRtn = Master.getInstance().getAttribute(attributeId);
        Assert.assertNotNull(attributeRtn);

        //Get the attribute by its id and assert the return value is not null
        logger.LogSample("Get the attribute by its id");
        String attributeGetId = attributeServiceAPI.getAttribute(attributeId);
        Assert.assertNotNull(Master.getInstance().getAttribute(attributeGetId));

        //Get attributes by some get conditions, like type and id.
        logger.LogSample("Get attributes by its id and type");
        paraMap.put("type", attributeRtn.getType());
        paraMap.put("id", attributeId);
        List<String> attributeResultList = attributeServiceAPI.getAttribute(paraMap);
        Assert.assertNotNull(attributeResultList);

        //Get all attributes without any search condition
        logger.LogSample("Get all attributes(without any search condition)");
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
        ItemRevisionService itemRevisionService = ItemRevisionServiceImpl.instance();

        //Post a Physical item
        Item item = itemServiceAPI.prepareItemEntity(defaultItemFileName);
        item.setType(EnumHelper.CatalogItemType.PHYSICAL.getItemType());
        logger.LogSample("Post a physical item");
        String itemId = itemServiceAPI.postItem(item);
        Assert.assertNotNull(Master.getInstance().getItem(itemId));

        //Post a Digital item
        item = itemServiceAPI.prepareItemEntity(defaultItemFileName);
        item.setType(EnumHelper.CatalogItemType.DIGITAL.getItemType());
        logger.LogSample("Post a digital(app) item");
        itemId = itemServiceAPI.postItem(item);
        Assert.assertNotNull(Master.getInstance().getItem(itemId));

        //Get the item by its id
        logger.LogSample("Get the item by its Id");
        String itemGetId = itemServiceAPI.getItem(itemId);
        Assert.assertNotNull(Master.getInstance().getItem(itemGetId));

        //Get item by id and status
        logger.LogSample("Get item(s) by id");
        paraMap.put("id", itemGetId);
        List<String> itemResultList = itemServiceAPI.getItem(paraMap);
        Assert.assertNotNull(itemResultList);

        //Get all items without any search condition
        logger.LogSample("Get all items(without any search condition)");
        itemResultList.clear();
        paraMap.clear();
        itemResultList = itemServiceAPI.getItem(paraMap);
        Assert.assertNotNull(itemResultList);

        //Attach item revision to the item
        ItemRevision itemRevision = itemRevisionService.prepareItemRevisionEntity(defaultItemRevisionFileName, EnumHelper.CatalogItemType.DIGITAL);
        itemRevision.setItemId(IdConverter.hexStringToId(ItemId.class, itemId));
        itemRevision.setType(item.getType());
        itemRevision.setOwnerId(item.getOwnerId());
        String itemRevisionId = itemRevisionService.postItemRevision(itemRevision);

        //Approve the item revision
        itemRevision = Master.getInstance().getItemRevision(itemRevisionId);
        itemRevision.setStatus(EnumHelper.CatalogEntityStatus.APPROVED.getEntityStatus());
        itemRevisionService.updateItemRevision(itemRevision);

        //The item curated should be true now
        itemServiceAPI.getItem(itemId);
        Assert.assertEquals(Master.getInstance().getItem(itemId).getCurated(), Boolean.TRUE);
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
        OfferRevisionService offerRevisionServiceAPI = OfferRevisionServiceImpl.instance();

        //Post a Physical offer
        Offer offer = offerServiceAPI.prepareOfferEntity(defaultOfferFileName, EnumHelper.CatalogItemType.PHYSICAL);
        logger.LogSample("Post a physical offer");
        String offerId = offerServiceAPI.postOffer(offer);
        Assert.assertNotNull(Master.getInstance().getOffer(offerId));

        //Post a Digital offer
        offer = offerServiceAPI.prepareOfferEntity(defaultOfferFileName, EnumHelper.CatalogItemType.DIGITAL);
        logger.LogSample("Post a digital(app) offer");
        offerId = offerServiceAPI.postOffer(offer);
        Assert.assertNotNull(Master.getInstance().getOffer(offerId));

        //Get the offer by its id
        logger.LogSample("Get the offer by its id");
        String offerGetId = offerServiceAPI.getOffer(offerId);
        Assert.assertNotNull(Master.getInstance().getOffer(offerGetId));

        //Get the offer(s) by some conditions: by curated firstly
        logger.LogSample("Get the offer just by curated");
        paraMap.put("curated", EnumHelper.CatalogEntityStatus.DRAFT.getEntityStatus());
        List<String> offerResultList = offerServiceAPI.getOffer(paraMap);
        Assert.assertNotNull(offerResultList);

        //Get offer by id and status
        logger.LogSample("Get offers by id and status");
        paraMap.put("id", offerGetId);
        offerResultList.clear();
        offerResultList = offerServiceAPI.getOffer(paraMap);
        Assert.assertNotNull(offerResultList);

        //Get all offers without any search condition
        logger.LogSample("Get all offers without any search conditions");
        offerResultList.clear();
        paraMap.clear();
        offerResultList = offerServiceAPI.getOffer(paraMap);
        Assert.assertNotNull(offerResultList);

        //Attach offer revision to the offer
        OfferRevision offerRevision = offerRevisionServiceAPI.prepareOfferRevisionEntity(defaultOfferRevisionFileName, EnumHelper.CatalogItemType.DIGITAL);
        offerRevision.setOfferId(IdConverter.hexStringToId(OfferId.class, offerId));
        offerRevision.setOwnerId(offer.getOwnerId());
        String offerRevisionId = offerRevisionServiceAPI.postOfferRevision(offerRevision);

        //Approve the offer revision
        offerRevision = Master.getInstance().getOfferRevision(offerRevisionId);
        offerRevision.setStatus(EnumHelper.CatalogEntityStatus.APPROVED.getEntityStatus());
        offerRevisionServiceAPI.updateOfferRevision(offerRevision);

        //The offer curated should be true now
        offerServiceAPI.getOffer(offerId);
        Assert.assertEquals(Master.getInstance().getOffer(offerId).getCurated(), Boolean.TRUE);

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
