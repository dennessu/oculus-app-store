/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.catalogScenarios;

import com.junbo.test.common.apihelper.oauth.impl.OAuthTokenServiceImpl;
import com.junbo.test.common.apihelper.identity.impl.UserServiceImpl;
import com.junbo.catalog.spec.model.common.SimpleLocaleProperties;
import com.junbo.test.common.apihelper.oauth.OAuthTokenService;
import com.junbo.test.catalog.enums.CatalogOfferAttributeType;
import com.junbo.test.catalog.enums.CatalogItemAttributeType;
import com.junbo.test.common.apihelper.oauth.enums.GrantType;
import com.junbo.catalog.spec.model.attribute.OfferAttribute;
import com.junbo.catalog.spec.model.attribute.ItemAttribute;
import com.junbo.test.common.apihelper.identity.UserService;
import com.junbo.test.common.Entities.enums.ComponentType;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.test.catalog.enums.CatalogEntityStatus;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.test.catalog.enums.CatalogItemType;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.test.common.libs.RandomFactory;
import com.junbo.test.common.Utility.TestClass;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.common.id.OfferAttributeId;
import com.junbo.common.id.ItemAttributeId;
import com.junbo.test.common.property.*;
import com.junbo.common.model.Results;
import com.junbo.test.catalog.impl.*;
import com.junbo.common.id.OfferId;
import com.junbo.common.id.ItemId;
import com.junbo.test.catalog.*;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
  * @author Jason
  * Time: 3/14/2014
  * For testing catalog scenarios
*/
public class Catalog extends TestClass {

    private LogHelper logger = new LogHelper(Catalog.class);
    private final String defaultDigitalItemRevisionFileName = "defaultDigitalItemRevision";
    private final String defaultOfferRevisionFileName = "defaultOfferRevision";
    private final String defaultItemFileName = "defaultItem";

    @BeforeClass
    private void PrepareTestData() throws Exception {
        OAuthTokenService oAuthTokenService = OAuthTokenServiceImpl.getInstance();
        oAuthTokenService.postAccessToken(GrantType.CLIENT_CREDENTIALS, ComponentType.CATALOG);
    }

    @Property(
            priority = Priority.BVT,
            features = "catalogScenarios",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Item Attribute Post/Get",
            steps = {
                    "1. Post an attribute",
                    "2. Get the attribute by attribute ID",
                    "3. Get attributes by some search conditions",
                    "4. Get all attributes without any search condition"
            }
    )
    @Test
    public void testItemAttributeManagement() throws Exception {

        HashMap<String, List<String>> paraMap = new HashMap<>();
        HashMap<String, SimpleLocaleProperties> locales = new HashMap<>();
        ItemAttributeService itemAttributeAPI = ItemAttributeServiceImpl.instance();

        ///Post an attribute and verify it got posted
        ItemAttribute itemAttribute = new ItemAttribute();
        SimpleLocaleProperties attributeProperties = new SimpleLocaleProperties();
        attributeProperties.setName("testItemAttribute_" + RandomFactory.getRandomStringOfAlphabet(10));
        attributeProperties.setDescription(RandomFactory.getRandomStringOfAlphabetOrNumeric(30));
        locales.put("en_US", attributeProperties);
        itemAttribute.setLocales(locales);
        itemAttribute.setType(CatalogItemAttributeType.getRandom());

        logger.LogSample("Post an Item attribute");
        ItemAttribute attributeRtn = itemAttributeAPI.postItemAttribute(itemAttribute);
        Assert.assertNotNull(attributeRtn);

        //Get the attribute by its id and assert the return value is not null
        logger.LogSample("Get the item attribute by its id");
        ItemAttribute attributeGet = itemAttributeAPI.getItemAttribute(attributeRtn.getId());
        Assert.assertNotNull(attributeGet);

        //Get attributes by some get conditions, like type and id.
        logger.LogSample("Get item attributes by its id and type");
        List<String> listType = new ArrayList<>();
        listType.add(attributeRtn.getType());
        List<String> listAttributeId = new ArrayList<>();
        String attributeId = IdConverter.idToUrlString(ItemAttributeId.class, attributeGet.getId());
        listAttributeId.add(attributeId);

        paraMap.put("type", listType);
        paraMap.put("id", listAttributeId);
        Results<ItemAttribute> attributeResultList = itemAttributeAPI.getItemAttributes(paraMap);
        Assert.assertNotNull(attributeResultList);

        //Get all attributes without any search condition
        logger.LogSample("Get all item attributes(without any search condition)");
        paraMap.clear();
        attributeResultList = itemAttributeAPI.getItemAttributes(paraMap);
        Assert.assertNotNull(attributeResultList);
    }

    @Property(
            priority = Priority.BVT,
            features = "catalogScenarios",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Offer Attribute Post/Get",
            steps = {
                    "1. Post an offer attribute",
                    "2. Get the attribute by attribute ID",
                    "3. Get attributes by some search conditions",
                    "4. Get all attributes without any search condition"
            }
    )
    @Test
    public void testOfferAttributeManagement() throws Exception {

        HashMap<String, List<String>> paraMap = new HashMap<>();
        HashMap<String, SimpleLocaleProperties> locales = new HashMap<>();
        OfferAttributeService offerAttributeAPI = OfferAttributeServiceImpl.instance();

        ///Post an attribute and verify it got posted
        OfferAttribute offerAttribute = new OfferAttribute();
        SimpleLocaleProperties attributeProperties = new SimpleLocaleProperties();
        attributeProperties.setName("testOfferAttribute_" + RandomFactory.getRandomStringOfAlphabet(10));
        attributeProperties.setDescription(RandomFactory.getRandomStringOfAlphabetOrNumeric(30));
        locales.put("en_US", attributeProperties);
        offerAttribute.setLocales(locales);
        offerAttribute.setType(CatalogOfferAttributeType.getRandom());

        logger.LogSample("Post an offer attribute");
        OfferAttribute attributeRtn = offerAttributeAPI.postOfferAttribute(offerAttribute);
        Assert.assertNotNull(attributeRtn);

        //Get the attribute by its id and assert the return value is not null
        logger.LogSample("Get the offer attribute by its id");
        OfferAttribute attributeGet = offerAttributeAPI.getOfferAttribute(attributeRtn.getId());
        Assert.assertNotNull(attributeGet);

        //Get attributes by some get conditions, like type and id.
        logger.LogSample("Get offer attributes by its id and type");
        List<String> listType = new ArrayList<>();
        listType.add(attributeRtn.getType());
        List<String> listAttributeId = new ArrayList<>();
        String attributeId = IdConverter.idToUrlString(OfferAttributeId.class, attributeGet.getId());
        listAttributeId.add(attributeId);

        paraMap.put("type", listType);
        paraMap.put("id", listAttributeId);
        Results<OfferAttribute> attributeResultList = offerAttributeAPI.getOfferAttributes(paraMap);
        Assert.assertNotNull(attributeResultList);

        //Get all attributes without any search condition
        logger.LogSample("Get all offer attributes(without any search condition)");
        paraMap.clear();
        attributeResultList = offerAttributeAPI.getOfferAttributes(paraMap);
        Assert.assertNotNull(attributeResultList);
    }

    @Property(
            priority = Priority.BVT,
            features = "catalogScenarios",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Item Post/Get/Put",
            steps = {
                    "1. Post an item",
                    "2. Get the item by its id",
                    "3. Get items by some search conditions, like status and id",
                    "4. Get all items without any search condition",
                    "5. Post an item revision related with the item",
                    "6. Approve the item revision and check item currentRevisionId"
            }
    )
    @Test
    public void testItemManagement() throws Exception {

        HashMap<String, List<String>> paraMap = new HashMap<>();
        ItemService itemServiceAPI = ItemServiceImpl.instance();
        ItemRevisionService itemRevisionService = ItemRevisionServiceImpl.instance();

        //Post a Physical item
        Item physicalItem = itemServiceAPI.prepareItemEntity(defaultItemFileName);
        physicalItem.setType(CatalogItemType.PHYSICAL.getItemType());
        logger.LogSample("Post a physical item");
        Item physicalItemGet = itemServiceAPI.postItem(physicalItem);
        Assert.assertNotNull(physicalItemGet);

        //Post a Digital item
        Item digitalItem = itemServiceAPI.prepareItemEntity(defaultItemFileName);
        digitalItem.setType(CatalogItemType.APP.getItemType());
        logger.LogSample("Post a digital(app) item");
        Item digitalItemGet = itemServiceAPI.postItem(digitalItem);
        Assert.assertNotNull(digitalItemGet);

        //Get the item by its id
        logger.LogSample("Get the item by its Id");
        Item itemRtn = itemServiceAPI.getItem(digitalItemGet.getItemId());
        Assert.assertNotNull(itemRtn);

        //Get item by id and status
        logger.LogSample("Get item(s) by id");
        List<String> listItemId = new ArrayList<>();
        String itemId = IdConverter.idToUrlString(ItemId.class, itemRtn.getItemId());
        listItemId.add(itemId);

        paraMap.put("id", listItemId);
        Results<Item> itemResultList = itemServiceAPI.getItems(paraMap);
        Assert.assertNotNull(itemResultList);

        //Get all items without any search condition
        logger.LogSample("Get all items(without any search condition)");
        paraMap.clear();
        itemResultList = itemServiceAPI.getItems(paraMap);
        Assert.assertNotNull(itemResultList);

        //Attach item revision to the item
        ItemRevision itemRevision = itemRevisionService.prepareItemRevisionEntity(defaultDigitalItemRevisionFileName);
        itemRevision.setItemId(digitalItemGet.getItemId());
        itemRevision.setOwnerId(digitalItemGet.getOwnerId());
        logger.LogSample("Post an item Revision");
        ItemRevision itemRevisionRtn = itemRevisionService.postItemRevision(itemRevision);

        //Approve the item revision
        itemRevisionRtn.setStatus(CatalogEntityStatus.APPROVED.getEntityStatus());
        logger.LogSample("Update item Revision's status to APPROVED");
        itemRevisionService.updateItemRevision(itemRevisionRtn.getRevisionId(), itemRevisionRtn);

        //verify the item's currentRevisionId equals to item Revision ID
        itemRtn = itemServiceAPI.getItem(digitalItemGet.getItemId());
        Assert.assertEquals(itemRtn.getCurrentRevisionId(), itemRevisionRtn.getRevisionId());
}

    @Property(
            priority = Priority.BVT,
            features = "catalogScenarios",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Offer Post/Get/Put",
            steps = {
                    "1. Post a default offer",
                    "2. Get the offer by its id",
                    "3. Get offers by some search conditions, like status and id",
                    "4. Get all offers without any search condition",
                    "5. Post an offer revision related with the offer",
                    "6. Approve the offer revision and check currentOfferRevisionId and published status"
            }
    )
    @Test
    public void testOfferManagement() throws Exception {

        HashMap<String, List<String>> paraMap = new HashMap<>();
        OfferService offerServiceAPI = OfferServiceImpl.instance();
        OfferRevisionService offerRevisionServiceAPI = OfferRevisionServiceImpl.instance();

        //Post a default offer
        logger.LogSample("Post a default offer");
        Offer offer = offerServiceAPI.postDefaultOffer();
        Assert.assertNotNull(offer);

        //Get the offer by its id
        logger.LogSample("Get the offer by its id");
        Offer offerGet = offerServiceAPI.getOffer(offer.getOfferId());
        Assert.assertNotNull(offerGet);

        //Get the offer(s) by some conditions: by published property firstly
        logger.LogSample("Get the offer just by published property");
        List<String> listStatus = new ArrayList<>();
        listStatus.add("false");

        paraMap.put("published", listStatus);
        Results<Offer> offerResult = offerServiceAPI.getOffers(paraMap);
        Assert.assertNotNull(offerResult);

        //Get offer by id and status
        logger.LogSample("Get offers by id and published property");
        List<String> listOfferId = new ArrayList<>();
        String offerId = IdConverter.idToUrlString(OfferId.class, offerGet.getOfferId());
        listOfferId.add(offerId);

        paraMap.put("id", listOfferId);
        offerResult = offerServiceAPI.getOffers(paraMap);
        Assert.assertNotNull(offerResult);

        //Get all offers without any search condition
        logger.LogSample("Get all offers without any search conditions");
        paraMap.clear();
        offerResult = offerServiceAPI.getOffers(paraMap);
        Assert.assertNotNull(offerResult);

        //Attach offer revision to the offer
        OfferRevision offerRevision = offerRevisionServiceAPI.prepareOfferRevisionEntity(defaultOfferRevisionFileName);
        offerRevision.setOfferId(offerGet.getOfferId());
        offerRevision.setOwnerId(offerGet.getOwnerId());
        OfferRevision offerRevisionRtn = offerRevisionServiceAPI.postOfferRevision(offerRevision);

        //Approve the offer revision
        offerRevisionRtn.setStatus(CatalogEntityStatus.APPROVED.getEntityStatus());
        offerRevisionServiceAPI.updateOfferRevision(offerRevisionRtn.getRevisionId(), offerRevisionRtn);

        //verify the offer published status and currentOfferRevisionId
        offerGet = offerServiceAPI.getOffer(offer.getOfferId());
        Assert.assertEquals(offerGet.getPublished(), Boolean.TRUE);
        Assert.assertEquals(offerGet.getCurrentRevisionId(), offerRevisionRtn.getRevisionId());
    }

    @Property(
            priority = Priority.BVT,
            features = "catalogScenarios",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test uploading item and offer",
            steps = {
                    "1. View all previously submitted offers",
                    "2. Post an item",
                    "3. Post an item revision, approve it",
                    "4. Post an offer",
                    "5. Post an offer revision based on the item and offer above, approve it",
                    "6. Check the offer status is published"
            }
    )
    @Test
    public void testUploadingOfferToStore() throws Exception {
        UserService userService = UserServiceImpl.instance();
        ItemService itemService = ItemServiceImpl.instance();
        ItemRevisionService itemRevisionService = ItemRevisionServiceImpl.instance();
        OfferService offerService = OfferServiceImpl.instance();
        OfferRevisionService offerRevisionService = OfferRevisionServiceImpl.instance();

        //Prepare a super user
        String superUserId = userService.PostUser();

        //Show all previously submitted offers
        HashMap<String, List<String>> paraMap = new HashMap<>();
        offerService.getOffers(paraMap);

        //Simulate app submission process
        //1. Post an Item
        Item item = itemService.postDefaultItem(CatalogItemType.APP);

        //2. Post an item revision
        ItemRevision itemRevision = itemRevisionService.postDefaultItemRevision(item);

        //3. Approve the item revision
        itemRevision.setStatus(CatalogEntityStatus.APPROVED.getEntityStatus());
        itemRevisionService.updateItemRevision(itemRevision.getRevisionId(), itemRevision);

        //4. Post an offer
        Offer offer = offerService.postDefaultOffer();

        //5. Post an offer revision
        OfferRevision offerRevision = offerRevisionService.postDefaultOfferRevision(offer, item);

        //6. Approve the offer revision
        offerRevision.setStatus(CatalogEntityStatus.APPROVED.getEntityStatus());
        offerRevisionService.updateOfferRevision(offerRevision.getRevisionId(), offerRevision);

        //Check the offer status
        offer = offerService.getOffer(offer.getOfferId());
        Assert.assertEquals(offer.getPublished(), Boolean.TRUE);
    }

    @Property(
            priority = Priority.BVT,
            features = "catalogScenarios",
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
        String offer5 = "testOffer_CartCheckout_Stored_Value";
        String offer6 = "testOffer_PreOrder_Digital1";
        String offer7 = "testOffer_PreOrder_Digital1";
        String offer8 = "testOffer_InAppConsumable1";
        String offer9 = "testOffer_InAppConsumable2";
        String offer10 = "test";

        OfferService offerServiceAPI = OfferServiceImpl.instance();

        offerServiceAPI.getOfferIdByName(offer1);
        offerServiceAPI.getOfferIdByName(offer2);
        offerServiceAPI.getOfferIdByName(offer3);
        offerServiceAPI.getOfferIdByName(offer4);
        offerServiceAPI.getOfferIdByName(offer5);
        offerServiceAPI.getOfferIdByName(offer6);
        offerServiceAPI.getOfferIdByName(offer7);
        offerServiceAPI.getOfferIdByName(offer8);
        offerServiceAPI.getOfferIdByName(offer9);
        offerServiceAPI.getOfferIdByName(offer10);
    }

}
