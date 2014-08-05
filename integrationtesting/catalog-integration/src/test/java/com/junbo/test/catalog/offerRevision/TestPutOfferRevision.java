/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.offerRevision;

import com.junbo.test.common.apihelper.identity.impl.OrganizationServiceImpl;
import com.junbo.test.common.apihelper.identity.OrganizationService;
import com.junbo.test.common.apihelper.oauth.impl.OAuthServiceImpl;
import com.junbo.test.common.apihelper.oauth.enums.GrantType;
import com.junbo.test.catalog.impl.OfferRevisionServiceImpl;
import com.junbo.test.common.apihelper.oauth.OAuthService;
import com.junbo.test.common.Entities.enums.ComponentType;
import com.junbo.catalog.spec.model.pricetier.PriceTier;
import com.junbo.test.catalog.enums.CatalogEntityStatus;
import com.junbo.test.catalog.impl.PriceTierServiceImpl;
import com.junbo.test.common.Entities.enums.Currency;
import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.catalog.enums.CatalogItemType;
import com.junbo.test.catalog.impl.OfferServiceImpl;
import com.junbo.test.catalog.OfferRevisionService;
import com.junbo.test.catalog.impl.ItemServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.junbo.catalog.spec.model.common.Price;
import com.junbo.test.catalog.util.BaseTestClass;
import com.junbo.test.common.libs.RandomFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.junbo.test.catalog.PriceTierService;
import com.junbo.test.catalog.enums.PriceType;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.catalog.spec.model.offer.*;
import com.junbo.test.catalog.OfferService;
import com.junbo.common.id.OrganizationId;
import com.junbo.test.catalog.ItemService;
import com.junbo.test.common.property.*;

import org.testng.annotations.Test;
import org.testng.Assert;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jason
 * Time: 7/4/2014
 * For testing catalog post offer revision API
 */
public class TestPutOfferRevision extends BaseTestClass {

    private LogHelper logger = new LogHelper(TestPutOfferRevision.class);

    private Offer offer1;
    private Offer offer2;
    private Item item1;
    private Item item2;
    private Item item3;
    private OrganizationId organizationId;
    private final String defaultLocale = "en_US";

    private OfferService offerService = OfferServiceImpl.instance();
    private OfferRevisionService offerRevisionService = OfferRevisionServiceImpl.instance();

    private void prepareTestData() throws Exception {
        OrganizationService organizationService = OrganizationServiceImpl.instance();
        ItemService itemService = ItemServiceImpl.instance();
        organizationId = organizationService.postDefaultOrganization().getId();

        item1 = itemService.postDefaultItem(CatalogItemType.getRandom(), organizationId);
        item2 = itemService.postDefaultItem(CatalogItemType.getRandom(), organizationId);
        item3 = itemService.postDefaultItem(CatalogItemType.getRandom(), organizationId);

        offer1 = offerService.postDefaultOffer(organizationId);
        offer2 = offerService.postDefaultOffer(organizationId);
    }

    @Property(
            priority = Priority.BVT,
            features = "Put v1/offer-revisions/{offerRevisionId}",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test put offer revision successfully",
            steps = {
                    "1. Prepare a default offer revision",
                    "2. Put the offer revision with corrected fields values",
                    "3. Verify the action could be successful"
            }
    )
    @Test
    public void testPutOfferRevision() throws Exception {
        this.prepareTestData();

        OfferRevision offerRevision = offerRevisionService.postDefaultOfferRevision(offer1);

        //update status
        offerRevision.setStatus(CatalogEntityStatus.REJECTED.name());
        offerRevision = offerRevisionService.updateOfferRevision(offerRevision.getRevisionId(), offerRevision);

        offerRevision.setStatus(CatalogEntityStatus.PENDING_REVIEW.name());
        offerRevision = offerRevisionService.updateOfferRevision(offerRevision.getRevisionId(), offerRevision);

        offerRevision.setStatus(CatalogEntityStatus.APPROVED.name());
        offerRevisionService.updateOfferRevision(offerRevision.getRevisionId(), offerRevision);

        //update distribution channel
        offerRevision = offerRevisionService.postDefaultOfferRevision(offer2);
        List<String> distributionChannel = new ArrayList<>();
        distributionChannel.add("INAPP");
        distributionChannel.add("STORE");

        offerRevision.setDistributionChannels(distributionChannel);
        offerRevision = offerRevisionService.updateOfferRevision(offerRevision.getRevisionId(), offerRevision);

        //update Price
        Price price = new Price();

        PriceTierService priceTierService = PriceTierServiceImpl.instance();
        PriceTier priceTier = priceTierService.postDefaultPriceTier();

        price.setPriceType(PriceType.TIERED.name());
        price.setPriceTier(priceTier.getId());

        offerRevision.setPrice(price);
        offerRevision = offerRevisionService.updateOfferRevision(offerRevision.getRevisionId(), offerRevision);

        //update restrictions
        Restriction restriction = new Restriction();
        List<String> exclusionItems = new ArrayList<>();
        List<String> preconditionItems  = new ArrayList<>();
        restriction.setLimitPerOrder(10);
        restriction.setLimitPerCustomer(10);

        exclusionItems.add(item1.getItemId());
        preconditionItems.add(item2.getItemId());

        restriction.setExclusionItems(exclusionItems);
        restriction.setPreconditionItems(preconditionItems);

        offerRevision.setRestrictions(restriction);
        offerRevision = offerRevisionService.updateOfferRevision(offerRevision.getRevisionId(), offerRevision);

        //update subOffers
        List<String> subOffers = new ArrayList<>();
        subOffers.add(offer1.getOfferId());

        offerRevision.setSubOffers(subOffers);
        offerRevision = offerRevisionService.updateOfferRevision(offerRevision.getRevisionId(), offerRevision);

        //Update items
        List<ItemEntry> items = offerRevision.getItems();

        if (items == null) {
            items = new ArrayList<>();
        }

        offerRevision.setItems(null);
        offerRevision = offerRevisionService.updateOfferRevision(offerRevision.getRevisionId(), offerRevision);

        ItemEntry itemEntry = new ItemEntry();
        itemEntry.setItemId(item3.getItemId());
        itemEntry.setQuantity(1);
        items.add(itemEntry);

        offerRevision.setItems(items);
        offerRevision = offerRevisionService.updateOfferRevision(offerRevision.getRevisionId(), offerRevision);

        //update locales
        Map<String, OfferRevisionLocaleProperties> locales = offerRevision.getLocales();
        OfferRevisionLocaleProperties offerRevisionLocaleProperties = new OfferRevisionLocaleProperties();

        offerRevisionLocaleProperties.setName("testOfferRevision" + RandomFactory.getRandomStringOfAlphabet(20));

        locales.put(defaultLocale, offerRevisionLocaleProperties);

        offerRevision.setLocales(locales);
        offerRevisionService.updateOfferRevision(offerRevision.getRevisionId(), offerRevision);
    }

    @Property(
            priority = Priority.Dailies,
            features = "Put v1/offer-revisions/{offerRevisionId}",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test put offer revision successfully",
            steps = {
                    "1. Prepare a default offer revision",
                    "2. test invalid values(like null, not null and some invalid enum values)",
                    "3. Verify the expected error"
            }
    )
    @Test
    public void testPutOfferRevisionInvalidScenarios() throws Exception {
        this.prepareTestData();

        ItemService itemService = ItemServiceImpl.instance();

        OfferRevision offerRevision = offerRevisionService.postDefaultOfferRevision(offer1);
        OfferRevision offerRevisionTmp = offerRevisionService.postDefaultOfferRevision(offer1);
        String offerRevisionId = offerRevision.getRevisionId();
        String offerRevisionRev = offerRevision.getRev();

        //not existed id
        String invalidRevisionId = "AAAA";
        verifyExpectedFailure(invalidRevisionId, offerRevision, 404);

        //existed but not its revision id
        verifyExpectedFailure(offerRevisionTmp.getRevisionId(), offerRevision);

        //update revision id
        offerRevision.setRevisionId(invalidRevisionId);
        verifyExpectedFailure(offerRevisionId, offerRevision);

        offerRevision.setRevisionId(offerRevisionTmp.getRevisionId());
        verifyExpectedFailure(offerRevisionId, offerRevision);

        offerRevision.setRevisionId(offerRevisionId);

        //update rev
        offerRevision.setRev(RandomFactory.getRandomStringOfAlphabet(10));
        verifyExpectedFailure(offerRevisionId, offerRevision);

        offerRevision.setRev(offerRevisionRev);

        //set status to null or invalid string(not enum value)
        offerRevision.setStatus(null);
        verifyExpectedFailure(offerRevisionId, offerRevision);

        offerRevision.setStatus("invalidEnumValue");
        verifyExpectedFailure(offerRevisionId, offerRevision);

        offerRevision.setStatus(CatalogEntityStatus.DRAFT.name());

        //OfferId to null or not existed value or to another offer
        offerRevision.setOfferId(null);
        verifyExpectedFailure(offerRevisionId, offerRevision);

        offerRevision.setOfferId(invalidRevisionId);
        verifyExpectedFailure(offerRevisionId, offerRevision, 404);

        offerRevision.setOfferId(offer2.getOfferId());
        offerRevision = offerRevisionService.updateOfferRevision(offerRevisionId, offerRevision);

        //update locale's name to null
        offerRevision.getLocales().get(defaultLocale).setName(null);
        verifyExpectedFailure(offerRevisionId, offerRevision);

        offerRevision.getLocales().get(defaultLocale).setName(RandomFactory.getRandomStringOfAlphabet(10));

        //set publisher to null
        offerRevision.setOwnerId(null);
        verifyExpectedFailure(offerRevisionId, offerRevision);

        offerRevision.setOwnerId(organizationId);

        //check distribution channel
        offerRevision.setDistributionChannels(null);

        offerRevision.setStatus(CatalogEntityStatus.DRAFT.name());
        offerRevision = offerRevisionService.updateOfferRevision(offerRevisionId, offerRevision);

        offerRevision.setStatus(CatalogEntityStatus.PENDING_REVIEW.name());
        verifyExpectedFailure(offerRevisionId, offerRevision);

        List<String> channels = new ArrayList<>();
        channels.add("INAPP");
        channels.add("STORE");
        channels.add("INVALIDCHANNEL");

        offerRevision.setDistributionChannels(channels);

        offerRevision.setStatus(CatalogEntityStatus.REJECTED.name());
        offerRevision = offerRevisionService.updateOfferRevision(offerRevisionId, offerRevision);

        offerRevision.setStatus(CatalogEntityStatus.APPROVED.name());
        verifyExpectedFailure(offerRevisionId, offerRevision);

        channels.clear();
        channels.add("INAPP");
        channels.add("STORE");
        offerRevision.setDistributionChannels(channels);

        //check item
        Item item1 = itemService.postDefaultItem(CatalogItemType.DOWNLOADED_ADDITION, organizationId);
        Item item2 = itemService.postDefaultItem(CatalogItemType.DOWNLOADED_ADDITION, organizationId);
        Item item3 = itemService.postDefaultItem(CatalogItemType.PHYSICAL, organizationId);

        List<ItemEntry> items = new ArrayList<>();
        ItemEntry itemEntry1 = new ItemEntry();
        ItemEntry itemEntry2 = new ItemEntry();
        ItemEntry itemEntry3 = new ItemEntry();

        itemEntry1.setItemId(null);
        itemEntry1.setQuantity(-2);

        items.add(itemEntry1);
        offerRevision.setItems(items);

        offerRevision.setStatus(CatalogEntityStatus.DRAFT.name());
        offerRevision = offerRevisionService.updateOfferRevision(offerRevisionId, offerRevision);

        offerRevision.setStatus(CatalogEntityStatus.APPROVED.name());
        verifyExpectedFailure(offerRevisionId, offerRevision);

        itemEntry1.setItemId(item1.getItemId());
        itemEntry1.setQuantity(0);

        itemEntry2.setItemId(item2.getItemId());
        itemEntry2.setQuantity(2);

        itemEntry3.setItemId(item3.getItemId());
        itemEntry3.setQuantity(5);

        items.clear();
        items.add(itemEntry1);
        items.add(itemEntry2);
        items.add(itemEntry3);
        offerRevision.setItems(items);

        offerRevision.setStatus(CatalogEntityStatus.REJECTED.name());
        offerRevision = offerRevisionService.updateOfferRevision(offerRevisionId, offerRevision);

        offerRevision.setStatus(CatalogEntityStatus.PENDING_REVIEW.name());
        verifyExpectedFailure(offerRevisionId, offerRevision);

        items.clear();
        items.add(itemEntry1);
        offerRevision.setItems(items);

        //check futureExpansion
        Map<String, JsonNode> futureExpansion = new HashMap<>();

        String str = "{\"subCountry\":\"TX\",\"street1\":\"800 West Campbell Road\"," +
                "\"city\":\"Richardson\",\"postalCode\":\"75080\"," +
                "\"country\":{\"id\":\"US\"}}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode value = mapper.readTree(str);

        futureExpansion.put("Address", value);
        offerRevision.setFutureExpansion(futureExpansion);

        offerRevision.setStatus(CatalogEntityStatus.DRAFT.name());
        verifyExpectedFailure(offerRevisionId, offerRevision);

    }

    @Property(
            priority = Priority.Dailies,
            features = "Put v1/offer-revisions/{offerRevisionId}",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test put offer revision successfully",
            steps = {
                    "1. Prepare a default offer revision",
                    "2. test invalid values for price",
                    "3. Verify the expected error"
            }
    )
    @Test
    public void testPutOfferRevisionWithWrongPrice() throws Exception {
        this.prepareTestData();

        //Data preparation
        OAuthService oAuthService = OAuthServiceImpl.getInstance();
        PriceTierService priceTierService = PriceTierServiceImpl.instance();

        oAuthService.postAccessToken(GrantType.CLIENT_CREDENTIALS, ComponentType.CATALOGADMIN);
        PriceTier priceTier = priceTierService.postDefaultPriceTier();

        Map<String, Map<String, BigDecimal>> prices = new HashMap<>();
        Map<String, BigDecimal> price1 = new HashMap<>();
        Map<String, BigDecimal> price2 = new HashMap<>();

        price1.put(Currency.USD.name(), new BigDecimal(9.83));
        price1.put(Currency.JPY.name(), new BigDecimal(660));

        price2.put(Currency.CNY.name(), new BigDecimal(50.00));
        price2.put(Currency.USD.name(), new BigDecimal(9.55));

        prices.put(Country.US.toString(), price1);
        prices.put(Country.CN.toString(), price2);

        OfferRevision offerRevision = offerRevisionService.postDefaultOfferRevision(offer1);
        String offerRevisionId = offerRevision.getRevisionId();

        Price originalPrice = offerRevision.getPrice();

        offerRevision.setPrice(null);

        offerRevision.setStatus(CatalogEntityStatus.DRAFT.name());
        offerRevision = offerRevisionService.updateOfferRevision(offerRevisionId, offerRevision);

        offerRevision.setStatus(CatalogEntityStatus.PENDING_REVIEW.name());
        verifyExpectedFailure(offerRevisionId, offerRevision);

        Price price = originalPrice;
        price.setPriceType("invalid type");
        offerRevision.setPrice(price);

        offerRevision.setStatus(CatalogEntityStatus.REJECTED.name());
        offerRevision = offerRevisionService.updateOfferRevision(offerRevisionId, offerRevision);

        offerRevision.setStatus(CatalogEntityStatus.APPROVED.name());
        verifyExpectedFailure(offerRevisionId, offerRevision);

        price.setPriceType(PriceType.CUSTOM.name());
        price.setPrices(null);
        price.setPriceTier(priceTier.getId());
        offerRevision.setPrice(price);

        offerRevision.setStatus(CatalogEntityStatus.DRAFT.name());
        offerRevision = offerRevisionService.updateOfferRevision(offerRevisionId, offerRevision);

        offerRevision.setStatus(CatalogEntityStatus.PENDING_REVIEW.name());
        verifyExpectedFailure(offerRevisionId, offerRevision);

        price.setPriceType(PriceType.FREE.name());
        price.setPrices(prices);
        price.setPriceTier(priceTier.getId());
        offerRevision.setPrice(price);

        offerRevision.setStatus(CatalogEntityStatus.REJECTED.name());
        offerRevision = offerRevisionService.updateOfferRevision(offerRevisionId, offerRevision);

        offerRevision.setStatus(CatalogEntityStatus.APPROVED.name());
        verifyExpectedFailure(offerRevisionId, offerRevision);

        price.setPriceType(PriceType.TIERED.name());
        price.setPrices(prices);
        price.setPriceTier(null);
        offerRevision.setPrice(price);

        offerRevision.setStatus(CatalogEntityStatus.DRAFT.name());
        offerRevision = offerRevisionService.updateOfferRevision(offerRevisionId, offerRevision);

        offerRevision.setStatus(CatalogEntityStatus.PENDING_REVIEW.name());
        verifyExpectedFailure(offerRevisionId, offerRevision);
    }

    private void verifyExpectedFailure(String offerRevisionId, OfferRevision offerRevision) throws Exception {
        verifyExpectedFailure(offerRevisionId, offerRevision, 400);
    }

    private void verifyExpectedFailure(String offerRevisionId, OfferRevision offerRevision, int expectedResponseCode) throws Exception {
        try {
            offerRevisionService.updateOfferRevision(offerRevisionId, offerRevision, expectedResponseCode);
            Assert.fail("should return expected exception");
        }
        catch (Exception ex) {
            logger.logInfo("Expected exception: " + ex);
        }
    }

}
