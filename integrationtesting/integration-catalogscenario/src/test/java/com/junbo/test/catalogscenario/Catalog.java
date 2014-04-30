/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalogscenario;

import com.junbo.catalog.spec.model.common.SimpleLocaleProperties;
import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition;
import com.junbo.test.common.apihelper.identity.impl.UserServiceImpl;
import com.junbo.catalog.spec.model.attribute.OfferAttribute;
import com.junbo.catalog.spec.model.attribute.ItemAttribute;
import com.junbo.test.common.apihelper.identity.UserService;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.test.common.apihelper.catalog.impl.*;
import com.junbo.test.common.apihelper.catalog.*;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.test.common.Utility.TestClass;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.EnumHelper;
import com.junbo.identity.spec.v1.model.User;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.libs.RandomFactory;
import com.junbo.test.common.property.*;

import org.testng.annotations.Test;
import org.testng.Assert;
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
    private final String defaultDigitalItemRevisionFileName = "defaultDigitalItemRevision";
    private final String defaultOfferRevisionFileName = "defaultOfferRevision";

    @Property(
            priority = Priority.BVT,
            features = "CatalogScenarios",
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

        HashMap<String, String> paraMap = new HashMap<>();
        HashMap<String, SimpleLocaleProperties> locales = new HashMap<>();
        ItemAttributeService itemAttributeAPI = ItemAttributeServiceImpl.instance();

        ///Post an attribute and verify it got posted
        ItemAttribute itemAttribute = new ItemAttribute();
        SimpleLocaleProperties attributeProperties = new SimpleLocaleProperties();
        attributeProperties.setName("testItemAttribute_" + RandomFactory.getRandomStringOfAlphabet(10));
        attributeProperties.setDescription(RandomFactory.getRandomStringOfAlphabetOrNumeric(30));
        locales.put("en_US", attributeProperties);
        itemAttribute.setLocales(locales);
        itemAttribute.setType(EnumHelper.CatalogItemAttributeType.getRandom());

        logger.LogSample("Post an Item attribute");
        String attributeId = itemAttributeAPI.postItemAttribute(itemAttribute);
        ItemAttribute attributeRtn = Master.getInstance().getItemAttribute(attributeId);
        Assert.assertNotNull(attributeRtn);

        //Get the attribute by its id and assert the return value is not null
        logger.LogSample("Get the item attribute by its id");
        String attributeGetId = itemAttributeAPI.getItemAttribute(attributeId);
        Assert.assertNotNull(Master.getInstance().getItemAttribute(attributeGetId));

        //Get attributes by some get conditions, like type and id.
        logger.LogSample("Get item attributes by its id and type");
        paraMap.put("type", attributeRtn.getType());
        paraMap.put("id", attributeId);
        List<String> attributeResultList = itemAttributeAPI.getItemAttributes(paraMap);
        Assert.assertNotNull(attributeResultList);

        //Get all attributes without any search condition
        logger.LogSample("Get all item attributes(without any search condition)");
        paraMap.clear();
        attributeResultList.clear();
        attributeResultList = itemAttributeAPI.getItemAttributes(paraMap);
        Assert.assertNotNull(attributeResultList);
    }

    @Property(
            priority = Priority.BVT,
            features = "CatalogScenarios",
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

        HashMap<String, String> paraMap = new HashMap();
        HashMap<String, SimpleLocaleProperties> locales = new HashMap<>();
        OfferAttributeService offerAttributeAPI = OfferAttributeServiceImpl.instance();

        ///Post an attribute and verify it got posted
        OfferAttribute offerAttribute = new OfferAttribute();
        SimpleLocaleProperties attributeProperties = new SimpleLocaleProperties();
        attributeProperties.setName("testOfferAttribute_" + RandomFactory.getRandomStringOfAlphabet(10));
        attributeProperties.setDescription(RandomFactory.getRandomStringOfAlphabetOrNumeric(30));
        locales.put("en_US", attributeProperties);
        offerAttribute.setLocales(locales);
        offerAttribute.setType(EnumHelper.CatalogOfferAttributeType.getRandom());

        logger.LogSample("Post an offer attribute");
        String attributeId = offerAttributeAPI.postOfferAttribute(offerAttribute);
        OfferAttribute attributeRtn = Master.getInstance().getOfferAttribute(attributeId);
        Assert.assertNotNull(attributeRtn);

        //Get the attribute by its id and assert the return value is not null
        logger.LogSample("Get the offer attribute by its id");
        String attributeGetId = offerAttributeAPI.getOfferAttribute(attributeId);
        Assert.assertNotNull(Master.getInstance().getOfferAttribute(attributeGetId));

        //Get attributes by some get conditions, like type and id.
        logger.LogSample("Get offer attributes by its id and type");
        paraMap.put("type", attributeRtn.getType());
        paraMap.put("id", attributeId);
        List<String> attributeResultList = offerAttributeAPI.getOfferAttributes(paraMap);
        Assert.assertNotNull(attributeResultList);

        //Get all attributes without any search condition
        logger.LogSample("Get all offer attributes(without any search condition)");
        paraMap.clear();
        attributeResultList.clear();
        attributeResultList = offerAttributeAPI.getOfferAttributes(paraMap);
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
                    "5. Post an item revision related with the item",
                    "6. Approve the item revision and check item currentRevisionId"
            }
    )
    @Test
    public void testItemManagement() throws Exception {

        HashMap<String, String> paraMap = new HashMap();
        ItemService itemServiceAPI = ItemServiceImpl.instance();
        ItemRevisionService itemRevisionService = ItemRevisionServiceImpl.instance();

        //Post a Physical item
        Item physicalItem = itemServiceAPI.prepareItemEntity(defaultItemFileName);
        physicalItem.setType(EnumHelper.CatalogItemType.PHYSICAL.getItemType());
        logger.LogSample("Post a physical item");
        String physicalItemId = itemServiceAPI.postItem(physicalItem);
        Assert.assertNotNull(Master.getInstance().getItem(physicalItemId));

        //Post a Digital item
        Item digitalItem = itemServiceAPI.prepareItemEntity(defaultItemFileName);
        digitalItem.setType(EnumHelper.CatalogItemType.DIGITAL.getItemType());
        logger.LogSample("Post a digital(app) item");
        String digitalItemId = itemServiceAPI.postItem(digitalItem);
        Assert.assertNotNull(Master.getInstance().getItem(digitalItemId));

        //Get the item by its id
        logger.LogSample("Get the item by its Id");
        String itemGetId = itemServiceAPI.getItem(digitalItemId);
        Assert.assertNotNull(Master.getInstance().getItem(itemGetId));

        //Get item by id and status
        logger.LogSample("Get item(s) by id");
        paraMap.put("id", itemGetId);
        List<String> itemResultList = itemServiceAPI.getItems(paraMap);
        Assert.assertNotNull(itemResultList);

        //Get all items without any search condition
        logger.LogSample("Get all items(without any search condition)");
        itemResultList.clear();
        paraMap.clear();
        itemResultList = itemServiceAPI.getItems(paraMap);
        Assert.assertNotNull(itemResultList);

        //Attach item revision to the item
        digitalItem = Master.getInstance().getItem(digitalItemId);
        ItemRevision itemRevision = itemRevisionService.prepareItemRevisionEntity(defaultDigitalItemRevisionFileName);
        itemRevision.setItemId(digitalItem.getItemId());
        itemRevision.setOwnerId(digitalItem.getOwnerId());
        logger.LogSample("Post an item Revision");
        String itemRevisionId = itemRevisionService.postItemRevision(itemRevision);

        //Approve the item revision
        itemRevision = Master.getInstance().getItemRevision(itemRevisionId);
        itemRevision.setStatus(EnumHelper.CatalogEntityStatus.APPROVED.getEntityStatus());
        logger.LogSample("Update item Revision's status to APPROVED");
        itemRevisionService.updateItemRevision(itemRevision);

        //verify the item's currentRevisionId equals to item Revision ID
        itemServiceAPI.getItem(digitalItemId);
        Assert.assertEquals(Master.getInstance().getItem(digitalItemId).getCurrentRevisionId(), itemRevision.getRevisionId());
}

    @Property(
            priority = Priority.BVT,
            features = "CatalogScenarios",
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

        HashMap<String, String> paraMap = new HashMap();
        OfferService offerServiceAPI = OfferServiceImpl.instance();
        OfferRevisionService offerRevisionServiceAPI = OfferRevisionServiceImpl.instance();

        //Post a default offer
        logger.LogSample("Post a default offer");
        String offerId = offerServiceAPI.postDefaultOffer();
        Assert.assertNotNull(Master.getInstance().getOffer(offerId));

        //Get the offer by its id
        logger.LogSample("Get the offer by its id");
        String offerGetId = offerServiceAPI.getOffer(offerId);
        Assert.assertNotNull(Master.getInstance().getOffer(offerGetId));

        //Get the offer(s) by some conditions: by published property firstly
        logger.LogSample("Get the offer just by published property");
        paraMap.put("published", "false");
        List<String> offerResultList = offerServiceAPI.getOffers(paraMap);
        Assert.assertNotNull(offerResultList);

        //Get offer by id and status
        logger.LogSample("Get offers by id and published property");
        paraMap.put("id", offerGetId);
        offerResultList.clear();
        offerResultList = offerServiceAPI.getOffers(paraMap);
        Assert.assertNotNull(offerResultList);

        //Get all offers without any search condition
        logger.LogSample("Get all offers without any search conditions");
        offerResultList.clear();
        paraMap.clear();
        offerResultList = offerServiceAPI.getOffers(paraMap);
        Assert.assertNotNull(offerResultList);

        //Attach offer revision to the offer
        Offer offer = Master.getInstance().getOffer(offerId);
        OfferRevision offerRevision = offerRevisionServiceAPI.prepareOfferRevisionEntity(defaultOfferRevisionFileName);
        offerRevision.setOfferId(offer.getOfferId());
        offerRevision.setOwnerId(offer.getOwnerId());
        String offerRevisionId = offerRevisionServiceAPI.postOfferRevision(offerRevision);

        //Approve the offer revision
        offerRevision = Master.getInstance().getOfferRevision(offerRevisionId);
        offerRevision.setStatus(EnumHelper.CatalogEntityStatus.APPROVED.getEntityStatus());
        offerRevisionServiceAPI.updateOfferRevision(offerRevision);

        //verify the offer published status and currentOfferRevisionId
        offerServiceAPI.getOffer(offerId);
        offer = Master.getInstance().getOffer(offerId);
        Assert.assertEquals(offer.getPublished(), Boolean.TRUE);
        Assert.assertEquals(offer.getCurrentRevisionId(), offerRevision.getRevisionId());
    }

    @Property(
            priority = Priority.BVT,
            features = "CatalogScenarios",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test EntitlementDefinition Post/Get",
            steps = {
                    "1. Post an EntitlementDefinition",
                    "2. Get the EntitlementDefinition by EntitlementDefinition ID",
                    "3. Get EntitlementDefinition by some search conditions",
                    "4. Get all EntitlementDefinition without any search condition",
                    "5. Update the EntitlementDefinition",
                    "6. Delete the EntitlementDefinition"
            }
    )
    @Test
    public void testEntitlementDefinitionManagement() throws Exception {

        HashMap<String, String> paraMap = new HashMap();
        EntitlementDefinitionService entitlementDefinitionService = EntitlementDefinitionServiceImpl.instance();

        ///Post an entitlement definition and verify it got posted
        logger.LogSample("Post an entitlement definition");
        String edId = entitlementDefinitionService.postDefaultEntitlementDefinition(EnumHelper.EntitlementType.getRandomType());
        EntitlementDefinition edRtn = Master.getInstance().getEntitlementDefinition(edId);
        Assert.assertNotNull(edRtn);

        //Get the entitlement definition by its id and assert the return value is not null
        logger.LogSample("Get the entitlement definition by its id");
        String edGetId = entitlementDefinitionService.getEntitlementDefinition(edId);
        Assert.assertNotNull(Master.getInstance().getEntitlementDefinition(edGetId));

        //Get entitlement definitions by some get conditions, like type and id.
        logger.LogSample("Get entitlement definitions by its id and type");
        paraMap.put("type", edRtn.getType());
        paraMap.put("id", edId);
        List<String> edResultList = entitlementDefinitionService.getEntitlementDefinitions(paraMap);
        Assert.assertNotNull(edResultList);

        //Get all entitlement definitions without any search condition
        logger.LogSample("Get all entitlement definitions(without any search condition)");
        paraMap.clear();
        edResultList.clear();
        edResultList = entitlementDefinitionService.getEntitlementDefinitions(paraMap);
        Assert.assertNotNull(edResultList);

        //update the entitlement definition
        edRtn = Master.getInstance().getEntitlementDefinition(edId);
        String edGroup = RandomFactory.getRandomStringOfAlphabet(5);
        String edTag = RandomFactory.getRandomStringOfAlphabet(5);
        edRtn.setGroup(edGroup);
        edRtn.setTag(edTag);
        edRtn.setConsumable(Boolean.TRUE);

        logger.LogSample("Update entitlement definition");
        entitlementDefinitionService.updateEntitlementDefinition(edRtn);
        edRtn = Master.getInstance().getEntitlementDefinition(edId);
        Assert.assertEquals(edRtn.getGroup(), edGroup);
        Assert.assertEquals(edRtn.getTag(), edTag);
        Assert.assertEquals(edRtn.getConsumable(), Boolean.TRUE);

        //Delete the entitlement definition
        logger.LogSample("Delete entitlement definition");
        entitlementDefinitionService.deleteEntitlementDefinition(edId);

        //search the entitlement definition again, and verify we could not found it.
        try {
            entitlementDefinitionService.getEntitlementDefinition(edId, 404);
            Assert.fail("couldn't find an entitlement definition which has been deleted");
        }
        catch (Exception ex) {
            Assert.assertTrue(ex.getMessage().contains(String.format("entitlementDefinition [%s] not found", edId)));
        }
    }

    @Property(
            priority = Priority.BVT,
            features = "CatalogScenarios",
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
        User userSuper = Master.getInstance().getUser(superUserId);

        //Show all previously submitted offers
        HashMap<String, String> paraMap = new HashMap();
        offerService.getOffers(paraMap);

        //Simulate app submission process
        //Post an Item
        String itemId = itemService.postDefaultItem(EnumHelper.CatalogItemType.DIGITAL);

        //Post an item revision
        String itemRevisionId = itemRevisionService.postDefaultItemRevision(itemId);

        //Approve the item revision
        ItemRevision itemRevision = Master.getInstance().getItemRevision(itemRevisionId);
        itemRevision.setStatus(EnumHelper.CatalogEntityStatus.APPROVED.getEntityStatus());
        itemRevisionService.updateItemRevision(itemRevision);

        //Post an offer
        String offerId = offerService.postDefaultOffer();

        //Post an offer revision
        String offerRevisionId = offerRevisionService.postDefaultOfferRevision(offerId, itemId);

        //Approve the offer revision
        OfferRevision offerRevision = Master.getInstance().getOfferRevision(offerRevisionId);
        offerRevision.setStatus(EnumHelper.CatalogEntityStatus.APPROVED.getEntityStatus());
        offerRevisionService.updateOfferRevision(offerRevision);

        //Check the offer status
        offerService.getOffer(offerId);
        Offer offer = Master.getInstance().getOffer(offerId);
        Assert.assertEquals(offer.getPublished(), Boolean.TRUE);
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
