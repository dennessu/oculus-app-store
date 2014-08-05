/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.offerRevision;

import com.junbo.test.common.apihelper.identity.impl.OrganizationServiceImpl;
import com.junbo.catalog.spec.model.offer.OfferRevisionLocaleProperties;
import com.junbo.test.common.apihelper.identity.OrganizationService;
import com.junbo.test.catalog.impl.OfferRevisionServiceImpl;
import com.junbo.test.catalog.enums.CatalogEntityStatus;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.catalog.spec.model.offer.ItemEntry;
import com.junbo.test.catalog.enums.CatalogItemType;
import com.junbo.test.catalog.impl.OfferServiceImpl;
import com.junbo.test.catalog.impl.ItemServiceImpl;
import com.junbo.test.catalog.OfferRevisionService;
import com.junbo.test.catalog.util.BaseTestClass;
import com.junbo.catalog.spec.model.common.Price;
import com.junbo.test.common.libs.RandomFactory;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.test.catalog.enums.PriceType;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.catalog.OfferService;
import com.junbo.common.id.OrganizationId;
import com.junbo.test.catalog.ItemService;
import com.junbo.test.common.property.*;

import org.testng.annotations.Test;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jason
 * Time: 7/4/2014
 * For testing catalog post  revision API
 */
public class TestPostOfferRevision extends BaseTestClass {

    private LogHelper logger = new LogHelper(TestPostOfferRevision.class);

    private Item item;
    private Offer offer;
    private OrganizationId organizationId;
    private final String defaultLocale = "en_US";
    private final String defaultOfferRevisionFileName = "defaultOfferRevision";

    private ItemService itemService = ItemServiceImpl.instance();
    private OfferService offerService = OfferServiceImpl.instance();
    private OfferRevisionService offerRevisionService = OfferRevisionServiceImpl.instance();

    private void prepareTestData() throws Exception {
        OrganizationService organizationService = OrganizationServiceImpl.instance();
        organizationId = organizationService.postDefaultOrganization().getId();

        item = itemService.postDefaultItem(CatalogItemType.getRandom(), organizationId);
        offer = offerService.postDefaultOffer(organizationId);
    }

    @Property(
            priority = Priority.BVT,
            features = "Post v1/offer-revisions",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Post Offer Revisions",
            steps = {
                    "1. Post test offer revisions only with required fields",
                    "2. Verify the returned values are the same with prepared",
                    "3. Post test offer revisions with optional fields",
                    "4. Verify the returned values are the same with prepared"
            }
    )
    @Test
    public void testPostOfferRevision() throws Exception {
        this.prepareTestData();

        //Post an offer revision only with required fields
        OfferRevision offerRevisionPrepared = new OfferRevision();
        Map<String, OfferRevisionLocaleProperties> locales = new HashMap<>();
        OfferRevisionLocaleProperties offerRevisionLocaleProperties = new OfferRevisionLocaleProperties();

        offerRevisionPrepared.setOfferId(offer.getOfferId());
        offerRevisionPrepared.setOwnerId(organizationId);
        offerRevisionPrepared.setStatus(CatalogEntityStatus.DRAFT.name());

        offerRevisionLocaleProperties.setName("testOfferRevision_" + RandomFactory.getRandomStringOfAlphabetOrNumeric(10));
        locales.put(defaultLocale, offerRevisionLocaleProperties);
        offerRevisionPrepared.setLocales(locales);

        List<String> distributionChannels = new ArrayList<>();
        distributionChannels.add("INAPP");
        distributionChannels.add("STORE");
        offerRevisionPrepared.setDistributionChannels(distributionChannels);

        //Set price
        Price price = new Price();
        price.setPriceType(PriceType.FREE.name());

        offerRevisionPrepared.setPrice(price);
        OfferRevision offerRevisionRtn = offerRevisionService.postOfferRevision(offerRevisionPrepared);

        checkOfferRevisionRequiredFields(offerRevisionRtn, offerRevisionPrepared);

        //Post an offer revision with optional fields
        OfferRevision testOfferRevisionFull = offerRevisionService.prepareOfferRevisionEntity(defaultOfferRevisionFileName,
                organizationId, false);

        testOfferRevisionFull.setOfferId(offer.getOfferId());
        testOfferRevisionFull.setOwnerId(organizationId);

        //set item info
        ItemEntry itemEntry = new ItemEntry();
        List<ItemEntry> itemEntities = new ArrayList<>();
        itemEntry.setItemId(item.getItemId());
        itemEntry.setQuantity(1);
        itemEntities.add(itemEntry);
        testOfferRevisionFull.setItems(itemEntities);
        OfferRevision testOfferRevisionRtn = offerRevisionService.postOfferRevision(testOfferRevisionFull);

        checkOfferRevisionRequiredFields(testOfferRevisionRtn, testOfferRevisionFull);
        checkOfferRevisionOptionalFields(testOfferRevisionRtn, testOfferRevisionFull);
    }

    @Property(
            priority = Priority.Dailies,
            features = "Post v1/offer-revisions",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Post Offer Revisions",
            steps = {
                    "1. Prepare an offer revision",
                    "2. test invalid values(like null, not null and some invalid enum values)",
                    "3. Try to post it and verify the expected error"
            }
    )
    @Test
    public void testPostOfferRevisionInvalidScenarios() throws Exception {
        this.prepareTestData();

        //Set rev not null
        OfferRevision testOfferRevision = offerRevisionService.prepareOfferRevisionEntity(defaultOfferRevisionFileName, organizationId);
        testOfferRevision.setOfferId(offer.getOfferId());
        testOfferRevision.setOwnerId(organizationId);
        testOfferRevision.setRev("1");
        verifyExpectedError(testOfferRevision);

        //Set status to invalid value
        testOfferRevision.setRev(null);
        testOfferRevision.setStatus("invalidValue");
        verifyExpectedError(testOfferRevision);

        testOfferRevision.setStatus(CatalogEntityStatus.REJECTED.name());
        verifyExpectedError(testOfferRevision);

        //set OwnerId to null
        testOfferRevision.setStatus(CatalogEntityStatus.DRAFT.name());
        testOfferRevision.setOwnerId(null);
        verifyExpectedError(testOfferRevision);

        //Test invalid value for offerId
        testOfferRevision.setOwnerId(organizationId);
        testOfferRevision.setOfferId(null);
        verifyExpectedError(testOfferRevision);

        testOfferRevision.setOfferId("11111");
        try {
            offerRevisionService.postOfferRevision(testOfferRevision, 404);
            Assert.fail("Post offer revision should fail");
        }
        catch (Exception ex) {
            logger.logInfo("Expected exception: " + ex);
        }

        //locales: name
        testOfferRevision.setOfferId(offer.getOfferId());
        Map<String, OfferRevisionLocaleProperties> locales = new HashMap<>();
        OfferRevisionLocaleProperties offerRevisionLocaleProperties = new OfferRevisionLocaleProperties();
        offerRevisionLocaleProperties.setName(null);
        locales.put(defaultLocale, offerRevisionLocaleProperties);
        testOfferRevision.setLocales(locales);
        verifyExpectedError(testOfferRevision);

    }

    private void checkOfferRevisionRequiredFields(OfferRevision offerRevisionActual, OfferRevision offerRevisionExpected) {
        Assert.assertEquals(offerRevisionActual.getOfferId(), offerRevisionExpected.getOfferId());
        Assert.assertEquals(offerRevisionActual.getOwnerId(), offerRevisionExpected.getOwnerId());
        //Compare distributionChannel
        Assert.assertTrue((offerRevisionActual.getDistributionChannels() == null)? offerRevisionExpected.getDistributionChannels()== null :
                offerRevisionActual.getDistributionChannels().equals(offerRevisionExpected.getDistributionChannels()));
        //Compare name
        OfferRevisionLocaleProperties localePropertiesActual = offerRevisionActual.getLocales().get(defaultLocale);
        OfferRevisionLocaleProperties localePropertiesExpected = offerRevisionExpected.getLocales().get(defaultLocale);
        Assert.assertEquals(localePropertiesActual.getName(), localePropertiesExpected.getName());
    }

    private void checkOfferRevisionOptionalFields(OfferRevision offerRevisionActual, OfferRevision offerRevisionExpected) {
        Assert.assertEquals(offerRevisionActual.getStatus(), offerRevisionExpected.getStatus());

        //Compare price
        Price actual = offerRevisionActual.getPrice();
        Price expected = offerRevisionExpected.getPrice();
        Assert.assertTrue((actual.getPriceTier() == null) ? expected.getPriceTier() == null : actual.getPriceTier().equalsIgnoreCase(expected.getPriceTier()));
        Assert.assertTrue(actual.getPriceType().equalsIgnoreCase(expected.getPriceType()));
        Assert.assertTrue(actual.getPrices().equals(expected.getPrices()));

    }

    private void verifyExpectedError(OfferRevision offerRevision) {
        try {
            //Error code 400 means "Missing Input field", "Unnecessary field found" or "invalid value"
            offerRevisionService.postOfferRevision(offerRevision, 400);
            Assert.fail("Post offer revision should fail");
        }
        catch (Exception ex) {
            logger.logInfo("Expected exception: " + ex);
        }
    }

}
