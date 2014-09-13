/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.advancedContentSearch;

import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.apihelper.identity.impl.OrganizationServiceImpl;
import com.junbo.catalog.spec.model.offer.OfferRevisionLocaleProperties;
import com.junbo.test.common.apihelper.identity.OrganizationService;
import com.junbo.test.common.apihelper.oauth.impl.OAuthServiceImpl;
import com.junbo.catalog.spec.model.attribute.OfferAttribute;
import com.junbo.test.common.apihelper.oauth.enums.GrantType;
import com.junbo.test.common.apihelper.oauth.OAuthService;
import com.junbo.test.common.Entities.enums.ComponentType;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.catalog.spec.model.offer.ItemEntry;
import com.junbo.test.catalog.enums.CatalogItemType;
import com.junbo.test.catalog.enums.EventActionType;
import com.junbo.test.catalog.util.BaseTestClass;
import com.junbo.catalog.spec.model.offer.Action;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.RandomFactory;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.test.catalog.enums.EventType;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.common.id.OrganizationId;
import com.junbo.test.common.property.*;
import com.junbo.common.model.Results;
import com.junbo.test.catalog.impl.*;
import com.junbo.test.catalog.*;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jason
 * Time: 6/27/2014
 * For testing catalog offer advanced search
 */
public class OfferSearch extends BaseTestClass {

    private LogHelper logger = new LogHelper(OfferSearch.class);

    private Item item;
    private Offer offer1;
    private Offer offer2;
    private Offer offer3;
    private OrganizationId organizationId;
    private final String defaultLocale = "en_US";

    private void prepareTestData() throws Exception {
        OfferService offerService = OfferServiceImpl.instance();
        OfferRevisionService offerRevisionService = OfferRevisionServiceImpl.instance();

        final String defaultOfferRevisionFileName = "defaultOfferRevision";
        final String defaultStoredValueOfferRevisionFileName = "defaultStoredValueOfferRevision";

        OrganizationService organizationService = OrganizationServiceImpl.instance();
        organizationId = organizationService.postDefaultOrganization().getId();

        ItemService itemService = ItemServiceImpl.instance();
        item = itemService.postDefaultItem(CatalogItemType.getRandom(), organizationId);
        item = releaseItem(item);

        offer1 = offerService.postDefaultOffer(organizationId);
        offer2 = offerService.postDefaultOffer(organizationId);
        offer3 = offerService.postDefaultOffer(organizationId);

        //put offer1 to add category
        OAuthService oAuthTokenService = OAuthServiceImpl.getInstance();
        oAuthTokenService.postAccessToken(GrantType.CLIENT_CREDENTIALS, ComponentType.CATALOGADMIN);

        OfferAttributeService offerAttributeService = OfferAttributeServiceImpl.instance();
        OfferAttribute offerAttribute = offerAttributeService.postDefaultOfferAttribute();

        List<String> categories = new ArrayList<>();
        categories.add(offerAttribute.getId());
        offer1.setCategories(categories);
        offer1 = offerService.updateOffer(offer1.getOfferId(), offer1);

        //release offer2 with customized properties
        OfferRevision offerRevisionPrepared;

        if (item.getType().equalsIgnoreCase(CatalogItemType.STORED_VALUE.getItemType())) {
            offerRevisionPrepared = offerRevisionService.prepareOfferRevisionEntity(
                    defaultStoredValueOfferRevisionFileName, organizationId, false);
            offerRevisionPrepared.getEventActions().get(EventType.PURCHASE.name()).get(0).setItemId(item.getItemId());
        }
        else {
            offerRevisionPrepared = offerRevisionService.prepareOfferRevisionEntity(
                    defaultOfferRevisionFileName, organizationId, false);
        }

        if (item.getType().equalsIgnoreCase(CatalogItemType.CONSUMABLE_UNLOCK.getItemType())) {
            List<Action> purchaseActions = new ArrayList<>();
            Map<String, List<Action>> consumableEvent = new HashMap<>();
            Action action = new Action();
            action.setType(EventActionType.GRANT_ENTITLEMENT.name());
            action.setItemId(item.getItemId());
            action.setUseCount(10);
            purchaseActions.add(action);
            consumableEvent.put(EventType.PURCHASE.name(), purchaseActions);
            offerRevisionPrepared.setEventActions(consumableEvent);
        }

        offerRevisionPrepared.setOfferId(offer2.getOfferId());
        offerRevisionPrepared.setOwnerId(organizationId);

        //set sub offer - release offer3 firstly as suboffers must be published.
        offer3 = releaseOffer(offer3);
        List<String> subOffers = new ArrayList<>();
        subOffers.add(offer3.getOfferId());
        offerRevisionPrepared.setSubOffers(subOffers);

        //add Item info
        ItemEntry itemEntry = new ItemEntry();
        List<ItemEntry> itemEntities = new ArrayList<>();
        itemEntry.setItemId(item.getItemId());
        itemEntry.setQuantity(1);
        itemEntities.add(itemEntry);
        offerRevisionPrepared.setItems(itemEntities);

        //set name, revisionNotes, long description and short description
        Map<String, OfferRevisionLocaleProperties> locales = new HashMap<>();
        OfferRevisionLocaleProperties offerRevisionLocaleProperties = new OfferRevisionLocaleProperties();
        offerRevisionLocaleProperties.setName("testOfferRevision_" + RandomFactory.getRandomStringOfAlphabetOrNumeric(10));
        offerRevisionLocaleProperties.setLongDescription("offerRevisionLongDescription_" +
                RandomFactory.getRandomStringOfAlphabetOrNumeric(10));
        offerRevisionLocaleProperties.setShortDescription("offerRevisionShortDescription_" +
                RandomFactory.getRandomStringOfAlphabetOrNumeric(10));

        locales.put("default", offerRevisionLocaleProperties);
        locales.put(defaultLocale, offerRevisionLocaleProperties);
        offerRevisionPrepared.setLocales(locales);

        //Post the offer revision and approve it
        OfferRevision offerRevisionPost = offerRevisionService.postOfferRevision(offerRevisionPrepared);
        releaseOfferRevision(offerRevisionPost);

        offer2 = offerService.getOffer(offer2.getOfferId());
    }

    @Property(
            priority = Priority.BVT,
            features = "Get v1/offers?q=",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Get offers by advanced query",
            steps = {
                    "1. Prepare some offers",
                    "2. Get the offers by advanced query",
                    "3. Release the offers",
                    "4. Get the offers by advanced query again"
            }
    )
    @Test
    public void testGetOffersByOnlyOneOption() throws Exception {
        OfferRevisionService offerRevisionService = OfferRevisionServiceImpl.instance();

        this.prepareTestData();

        String offerId1 = offer1.getOfferId();
        String offerId2 = offer2.getOfferId();

        OfferRevision offerRevision = offerRevisionService.getOfferRevision(offer2.getCurrentRevisionId());
        OfferRevisionLocaleProperties offerRevisionLocaleProperties = offerRevision.getLocales().get(defaultLocale);

        //when published is not specified, published is set to true; offer1 is not published, so won't get any offer
        buildSearchQuery("offerId:" + offerId1, 0);

        //default -- offerId
        buildSearchQuery(offerId2, 1, offerId2);

        //published is not default index
        buildSearchQuery("false", 0);
        buildSearchQuery("true", 0);

        //release offer1
        offer1 = releaseOffer(offer1);

        //categoryId
        buildSearchQuery("categoryId:" + offer1.getCategories().get(0), 1, offerId1);

        //default -- categoryId
        buildSearchQuery(offer1.getCategories().get(0), 1, offerId1);

        //ownerId
        buildSearchQuery("ownerId:'" + organizationId.toString() + "'", 3, offerId1, offerId2, offer3.getOfferId());

        //default -- ownerId
        buildSearchQuery("'" + organizationId.toString() + "'", 3, offerId1, offerId2, offer3.getOfferId());

        //revisionId
        buildSearchQuery("revisionId:" + offer2.getCurrentRevisionId(), 1, offerId2);

        //default -- revisionId
        buildSearchQuery(offer2.getCurrentRevisionId(), 1, offerId2);

        //itemId
        buildSearchQuery("itemId:" + item.getItemId(), 1, offerId2);

        //subOfferId
        buildSearchQuery("subOfferId:" + offer3.getOfferId(), 1, offerId2);

        //name
        String name = offerRevisionLocaleProperties.getName();
        buildSearchQuery("name:" + name, 1, offerId2);

        //default -- name
        buildSearchQuery(name, 1, offerId2);

        //longDescription
        String longDescription = offerRevisionLocaleProperties.getLongDescription();
        buildSearchQuery("longDescription:" + longDescription, 1, offerId2);

        //default -- longDescription
        buildSearchQuery(longDescription, 1, offerId2);

        //shortDescription
        String shortDescription = offerRevisionLocaleProperties.getShortDescription();
        buildSearchQuery("shortDescription:" + shortDescription, 1, offerId2);

        //default -- shortDescription
        buildSearchQuery(shortDescription, 1, offerId2);

    }

    @Property(
            priority = Priority.BVT,
            features = "Get v1/offers?q= AND/OR",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Get offers by advanced query",
            steps = {
                    "1. Prepare some offers",
                    "2. Get the offers by advanced query",
                    "3. Release the offers",
                    "4. Get the offers by advanced query again"
            }
    )
    @Test
    public void testGetOffersByCombinedOption() throws Exception {
        OfferRevisionService offerRevisionService = OfferRevisionServiceImpl.instance();

        this.prepareTestData();

        final String defaultEnvironment = "DEV";
        String offerId1 = offer1.getOfferId();
        String offerId2 = offer2.getOfferId();

        OfferRevision offerRevision = offerRevisionService.getOfferRevision(offer2.getCurrentRevisionId());
        OfferRevisionLocaleProperties offerRevisionLocaleProperties = offerRevision.getLocales().get(defaultLocale);

        String name = offerRevisionLocaleProperties.getName();

        String longDescription = offerRevisionLocaleProperties.getLongDescription();
        String shortDescription = offerRevisionLocaleProperties.getShortDescription();

        //release offer1
        offer1 = releaseOffer(offer1);

        //test AND
        //%20: space
        buildSearchQuery("offerId:" + offer3.getOfferId() + "%20AND%20published:" + "false", 0);
        buildSearchQuery("offerId:" + offer3.getOfferId() + "%20AND%20published:" + "true", 1, offer3.getOfferId());
        buildSearchQuery("offerId:" + offerId1 + "%20AND%20" + offer1.getCategories().get(0), 1, offerId1);
        buildSearchQuery(offerId1 + "%20AND%20" + offer1.getCategories().get(0), 1, offerId1);
        buildSearchQuery(offerId1 + "%20AND%20" + offer2.getCurrentRevisionId(), 0);
        buildSearchQuery(offerId1 + "%20AND%20" + offer2.getCurrentRevisionId() + "%20AND%20environment:" + defaultEnvironment, 0);
        buildSearchQuery(offerId1 + "%20AND%20" + defaultEnvironment, 1, offerId1);

        //test OR
        buildSearchQuery("offerId:" + offerId1 + "%20OR%20revisionId:" + offer2.getCurrentRevisionId(), 2, offerId1, offerId2);
        buildSearchQuery("offerId:" + offerId1 + "%20OR%20offerId:" + offerId2, 2, offerId1, offerId2);
        buildSearchQuery("offerId:" + offerId1 + "%20OR%20name:" + name, 2, offerId1, offerId2);
        buildSearchQuery(longDescription + "%20AND%20" + shortDescription, 1, offerId2);
        buildSearchQuery(longDescription + "%20OR%20" + shortDescription, 1, offerId2);
    }

    @Property(
            priority = Priority.BVT,
            features = "get /v1/offers",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            environment = "release",
            description = "Test get predefined offers",
            steps = {
            }
    )
    @Test
    public void testGetOfferItemById() throws Exception {
        ItemService itemService = ItemServiceImpl.instance();
        OfferService offerService = OfferServiceImpl.instance();
        ItemRevisionService itemRevisionService = ItemRevisionServiceImpl.instance();
        OfferRevisionService offerRevisionService = OfferRevisionServiceImpl.instance();

        String offerId1 = ConfigHelper.getSetting("testdata.offer.digital.free");
        String offerId2 = ConfigHelper.getSetting("testdata.offer.iap.free");

        Offer offer1 = offerService.getOffer(offerId1);
        Offer offer2 = offerService.getOffer(offerId2);
        Assert.assertNotNull(offer1);
        Assert.assertNotNull(offer2);

        //get offer revision
        OfferRevision offerRevision1 = offerRevisionService.getOfferRevision(offer1.getCurrentRevisionId());
        OfferRevision offerRevision2 = offerRevisionService.getOfferRevision(offer2.getCurrentRevisionId());
        Assert.assertNotNull(offerRevision1);
        Assert.assertNotNull(offerRevision2);

        Item item1 = itemService.getItem(offerRevision1.getItems().get(0).getItemId());
        Item item2 = itemService.getItem(offerRevision2.getItems().get(0).getItemId());
        Assert.assertNotNull(item1);
        Assert.assertNotNull(item2);

        ItemRevision itemRevision1 = itemRevisionService.getItemRevision(item1.getCurrentRevisionId());
        ItemRevision itemRevision2 = itemRevisionService.getItemRevision(item2.getCurrentRevisionId());
        Assert.assertNotNull(itemRevision1);
        Assert.assertNotNull(itemRevision2);
    }

    @Property(
            priority = Priority.BVT,
            features = "get /v1/offers",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            environment = "release",
            description = "Test get predefined offers",
            steps = {
            }
    )
    @Test
    public void testGetOffersItemsBySearchOptions() throws Exception {
        OfferService offerService = OfferServiceImpl.instance();

        HashMap<String, List<String>> paraMap = new HashMap<>();
        List<String> offerIds = new ArrayList<>();
        List<String> publisherIds = new ArrayList<>();
        List<String> packageName = new ArrayList<>();
        List<String> itemType = new ArrayList<>();
        List<String> itemIds = new ArrayList<>();
        List<String> itemRevisionIds = new ArrayList<>();

        String offerId1 = ConfigHelper.getSetting("testdata.offer.digital.free");
        String offerId2 = ConfigHelper.getSetting("testdata.offer.iap.free");

        Offer offer1 = offerService.getOffer(offerId1);
        Offer offer2 = offerService.getOffer(offerId2);

        offerIds.add(offer1.getOfferId());
        offerIds.add(offer2.getOfferId());

        paraMap.clear();
        paraMap.put("offerId", offerIds);

        Results<Offer> offersRtn = offerService.getOffers(paraMap);
        Assert.assertEquals(offersRtn.getItems().size(), 2);

        publisherIds.add(IdConverter.idToHexString(offer1.getOwnerId()));
        paraMap.put("publisherId", publisherIds);

        offersRtn = offerService.getOffers(paraMap);
        Assert.assertTrue(isContain(offersRtn, offer1));

        //get offer revisions
        OfferRevisionService offerRevisionService = OfferRevisionServiceImpl.instance();
        OfferRevision offerRevision1 = offerRevisionService.getOfferRevision(offer1.getCurrentRevisionId());

        Results<OfferRevision> offerRevisionsRtn = offerRevisionService.getOfferRevisions(paraMap);
        Assert.assertTrue(isContain(offerRevisionsRtn, offerRevision1));

        ItemService itemService = ItemServiceImpl.instance();
        ItemRevisionService itemRevisionService = ItemRevisionServiceImpl.instance();
        Item item1 = itemService.getItem(offerRevisionsRtn.getItems().get(0).getItems().get(0).getItemId());

        ItemRevision itemRevision1 = itemRevisionService.getItemRevision(item1.getCurrentRevisionId());

        paraMap.clear();
        paraMap.put("developerId", publisherIds);

        itemIds.add(item1.getItemId());
        paraMap.put("itemId", itemIds);

        itemType.add(item1.getType());
        packageName.add(itemRevision1.getPackageName());
        paraMap.put("type", itemType);
        paraMap.put("packageName", packageName);

        Results<Item> itemsRtn = itemService.getItems(paraMap);
        Assert.assertEquals(itemsRtn.getItems().size(), 1);
        Assert.assertTrue(isContain(itemsRtn, item1));

        paraMap.clear();
        itemIds.clear();

        itemRevisionIds.add(itemRevision1.getRevisionId());

        itemIds.add(item1.getItemId());

        paraMap.put("itemId", itemIds);
        paraMap.put("developerId", publisherIds);
        paraMap.put("revisionId", itemRevisionIds);

        Results<ItemRevision> itemRevisionsRtn = itemRevisionService.getItemRevisions(paraMap);
        Assert.assertTrue(isContain(itemRevisionsRtn, itemRevision1));
    }

    private void buildSearchQuery(String queryOption, int expectedRtnSize, String... offerId) throws Exception {
        HashMap<String, List<String>> paraMap = new HashMap<>();
        List<String> query = new ArrayList<>();

        query.add(queryOption);
        paraMap.put("q", query);
        verifyGetOffersScenarios(paraMap, expectedRtnSize, offerId);
    }

}
