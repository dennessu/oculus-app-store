/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.advancedContentSearch;

import com.junbo.test.common.apihelper.identity.impl.OrganizationServiceImpl;
import com.junbo.catalog.spec.model.offer.OfferRevisionLocaleProperties;
import com.junbo.test.common.apihelper.identity.OrganizationService;
import com.junbo.catalog.spec.model.attribute.OfferAttribute;
import com.junbo.catalog.spec.model.common.RevisionNotes;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.catalog.spec.model.offer.ItemEntry;
import com.junbo.test.catalog.enums.CatalogItemType;
import com.junbo.test.catalog.enums.EventActionType;
import com.junbo.test.catalog.util.BaseTestClass;
import com.junbo.catalog.spec.model.offer.Action;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.test.common.libs.RandomFactory;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.test.catalog.enums.EventType;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.common.id.OrganizationId;

import com.junbo.test.common.property.*;
import com.junbo.test.catalog.impl.*;
import com.junbo.test.catalog.*;

import org.testng.annotations.BeforeClass;
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

    private OfferService offerService = OfferServiceImpl.instance();
    private OfferRevisionService offerRevisionService = OfferRevisionServiceImpl.instance();

    @BeforeClass
    private void PrepareTestData() throws Exception {
        final String defaultOfferRevisionFileName = "defaultOfferRevision";
        final String defaultStoredValueOfferRevisionFileName = "defaultStoredValueOfferRevision";

        OrganizationService organizationService = OrganizationServiceImpl.instance();
        organizationId = organizationService.postDefaultOrganization().getId();

        ItemService itemService = ItemServiceImpl.instance();
        item = itemService.postDefaultItem(CatalogItemType.getRandom(), organizationId);

        offer1 = offerService.postDefaultOffer(organizationId);
        offer2 = offerService.postDefaultOffer(organizationId);
        offer3 = offerService.postDefaultOffer(organizationId);

        //put offer1 to add category
        OfferAttributeService offerAttributeService = OfferAttributeServiceImpl.instance();
        OfferAttribute offerAttribute = offerAttributeService.postDefaultOfferAttribute();

        List<String> categories = new ArrayList<>();
        categories.add(offerAttribute.getId());
        offer1.setCategories(categories);
        offer1 = offerService.updateOffer(offer1.getOfferId(), offer1);

        //release offer2 with customized properties
        OfferRevision offerRevisionPrepared;

        if (item.getType().equalsIgnoreCase(CatalogItemType.STORED_VALUE.getItemType())) {
            offerRevisionPrepared = offerRevisionService.prepareOfferRevisionEntity(defaultStoredValueOfferRevisionFileName, false);
        }
        else {
            offerRevisionPrepared = offerRevisionService.prepareOfferRevisionEntity(defaultOfferRevisionFileName, false);
        }

        if (item.getType().equalsIgnoreCase(CatalogItemType.IN_APP_CONSUMABLE.getItemType())) {
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

        //set sub offer
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
        RevisionNotes revisionNotes = new RevisionNotes();
        revisionNotes.setLongNotes("longOfferRevisionNotes_" + RandomFactory.getRandomStringOfAlphabetOrNumeric(10));
        revisionNotes.setShortNotes("shortOfferRevisionNotes_" + RandomFactory.getRandomStringOfAlphabetOrNumeric(10));
        offerRevisionLocaleProperties.setName("testOfferRevision_" + RandomFactory.getRandomStringOfAlphabetOrNumeric(10));
        offerRevisionLocaleProperties.setRevisionNotes(revisionNotes);
        offerRevisionLocaleProperties.setLongDescription("offerRevisionLongDescription_" + RandomFactory.getRandomStringOfAlphabetOrNumeric(10));
        offerRevisionLocaleProperties.setShortDescription("offerRevisionShortDescription_" + RandomFactory.getRandomStringOfAlphabetOrNumeric(10));

        locales.put("default", offerRevisionLocaleProperties);
        locales.put(defaultLocale, offerRevisionLocaleProperties);
        offerRevisionPrepared.setLocales(locales);

        //Post the offer revision and approve it
        OfferRevision offerRevisionPost = offerRevisionService.postOfferRevision(offerRevisionPrepared);
        offerRevisionPost.setStatus("APPROVED");
        offerRevisionService.updateOfferRevision(offerRevisionPost.getRevisionId(), offerRevisionPost);

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

        String offerId1 = offer1.getOfferId();
        String offerId2 = offer2.getOfferId();

        OfferRevision offerRevision = offerRevisionService.getOfferRevision(offer2.getCurrentRevisionId());
        OfferRevisionLocaleProperties offerRevisionLocaleProperties = offerRevision.getLocales().get(defaultLocale);

        //offerId
        buildSearchQuery("offerId:" + offerId1, 1, offerId1);

        //default -- offerId
        buildSearchQuery(offerId2, 1, offerId2);

        //published is not default index
        buildSearchQuery("false", 0);
        buildSearchQuery("true", 0);

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

        //longRevisionNotes
        String longRevisionNotes = offerRevisionLocaleProperties.getRevisionNotes().getLongNotes();
        buildSearchQuery("longNotes:" + longRevisionNotes, 1, offerId2);

        //default -- longRevisionNotes
        buildSearchQuery(longRevisionNotes, 1, offerId2);

        //shortRevisionNotes
        String shortRevisionNotes = offerRevisionLocaleProperties.getRevisionNotes().getShortNotes();
        buildSearchQuery("shortNotes:" + shortRevisionNotes, 1, offerId2);

        //default -- shortRevisionNotes
        buildSearchQuery(shortRevisionNotes, 1, offerId2);

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
        final String defaultEnvironment = "DEV";
        String offerId1 = offer1.getOfferId();
        String offerId2 = offer2.getOfferId();

        OfferRevision offerRevision = offerRevisionService.getOfferRevision(offer2.getCurrentRevisionId());
        OfferRevisionLocaleProperties offerRevisionLocaleProperties = offerRevision.getLocales().get(defaultLocale);

        String name = offerRevisionLocaleProperties.getName();
        String longRevisionNotes = offerRevisionLocaleProperties.getRevisionNotes().getLongNotes();
        String shortRevisionNotes = offerRevisionLocaleProperties.getRevisionNotes().getLongNotes();
        String longDescription = offerRevisionLocaleProperties.getLongDescription();
        String shortDescription = offerRevisionLocaleProperties.getShortDescription();

        //release offer1
        offer1 = releaseOffer(offer1);

        //test AND
        //%20: space
        buildSearchQuery("offerId:" + offer3.getOfferId() + "%20AND%20published:" + "false", 1, offer3.getOfferId());
        buildSearchQuery("offerId:" + offerId1 + "%20AND%20" + offer1.getCategories().get(0), 1, offerId1);
        buildSearchQuery(offerId1 + "%20AND%20" + offer1.getCategories().get(0), 1, offerId1);
        buildSearchQuery(offerId1 + "%20AND%20" + offer2.getCurrentRevisionId(), 0);
        buildSearchQuery(offerId1 + "%20AND%20" + offer2.getCurrentRevisionId() + "%20AND%20environment:" + defaultEnvironment, 0);
        buildSearchQuery(offerId1 + "%20AND%20" + defaultEnvironment, 1, offerId1);

        //test OR
        buildSearchQuery("offerId:" + offerId1 + "%20OR%20revisionId:" + offer2.getCurrentRevisionId(), 2, offerId1, offerId2);
        buildSearchQuery("offerId:" + offerId1 + "%20OR%20offerId:" + offerId2, 2, offerId1, offerId2);
        buildSearchQuery("offerId:" + offerId1 + "%20OR%20name:" + name, 2, offerId1, offerId2);
        buildSearchQuery("name:" + name + "%20OR%20longNotes:" + longRevisionNotes, 1, offerId2);
        buildSearchQuery(offerId1 + "%20OR%20name:" + name + "%20OR%20shortNotes:" + shortRevisionNotes, 2, offerId1, offerId2);
        buildSearchQuery(longDescription + "%20AND%20" + shortDescription, 1, offerId2);
        buildSearchQuery(longDescription + "%20OR%20" + shortDescription, 1, offerId2);
    }

    private void buildSearchQuery(String queryOption, int expectedRtnSize, String... offerId) throws Exception {
        HashMap<String, List<String>> paraMap = new HashMap<>();
        List<String> query = new ArrayList<>();

        query.add(queryOption);
        paraMap.put("q", query);
        verifyGetOffersScenarios(paraMap, expectedRtnSize, offerId);
    }

}
