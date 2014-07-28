/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.casesForBugs;

import com.junbo.catalog.spec.model.attribute.ItemAttribute;
import com.junbo.catalog.spec.model.attribute.OfferAttribute;
import com.junbo.catalog.spec.model.common.SimpleLocaleProperties;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.catalog.spec.model.pricetier.PriceTier;
import com.junbo.common.id.OrganizationId;
import com.junbo.test.catalog.*;
import com.junbo.test.catalog.enums.CatalogEntityStatus;
import com.junbo.test.catalog.enums.CatalogItemAttributeType;
import com.junbo.test.catalog.enums.CatalogItemType;
import com.junbo.test.catalog.enums.CatalogOfferAttributeType;
import com.junbo.test.catalog.impl.*;
import com.junbo.test.catalog.util.BaseTestClass;
import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.Entities.enums.Currency;
import com.junbo.test.common.apihelper.identity.OrganizationService;
import com.junbo.test.common.apihelper.identity.impl.OrganizationServiceImpl;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.libs.RandomFactory;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
  * @author Jason
  * Time: 4/1/2014
  * test cases for bugs
*/
public class casesForBugs extends BaseTestClass {

    private LogHelper logger = new LogHelper(casesForBugs.class);

    private ItemService itemService = ItemServiceImpl.instance();
    private ItemRevisionService itemRevisionService = ItemRevisionServiceImpl.instance();

    private OfferService offerService = OfferServiceImpl.instance();
    private OfferRevisionService offerRevisionService = OfferRevisionServiceImpl.instance();

    private OrganizationService organizationService = OrganizationServiceImpl.instance();

    private final String defaultDigitalItemRevisionFileName = "defaultDigitalItemRevision";
    private final String defaultOfferRevisionFileName = "defaultOfferRevision";

    @Property(
            priority = Priority.Dailies,
            features = "Bug 393",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Verify posting should fail if 'self' field is specified",
            steps = {
                    "1. Post an item with specified 'self'",
                    "2. Post an item revision with specified 'self'",
                    "3. Post an item attribute with specified 'self'",
                    "4. Post an offer with specified 'self'",
                    "5. Post an offer revision with specified 'self'",
                    "6. Post an offer attribute with specified 'self'",
                    "7. Post a price tier with specified 'self'"
            }
    )
    @Test
    public void testImmutableSelfField() throws Exception {
        ItemAttributeService itemAttributeService = ItemAttributeServiceImpl.instance();
        OfferAttributeService offerAttributeService = OfferAttributeServiceImpl.instance();
        PriceTierService priceTierService = PriceTierServiceImpl.instance();

        OrganizationId organizationId = organizationService.postDefaultOrganization().getId();

        String defaultItemFileName = "defaultItem";
        String defaultOfferFileName = "defaultOffer";
        String defaultOfferRevisionFileName = "defaultOfferRevision";
        String specifiedId = "AAAAAAAAAAAAAA";
        String defaultLocale = "en_US";

        //item
        Item item = itemService.prepareItemEntity(defaultItemFileName, organizationId);
        item.setItemId(specifiedId);

        try {
            itemService.postItem(item, 400);
        } catch (Exception ex) {
            logger.logInfo("Expected exception");
        }

        //item revision
        Item itemPrepared = itemService.postDefaultItem(CatalogItemType.APP, organizationId);
        ItemRevision itemRevision = itemRevisionService.prepareItemRevisionEntity(defaultDigitalItemRevisionFileName);
        itemRevision.setItemId(itemPrepared.getItemId());
        itemRevision.setOwnerId(organizationId);
        itemRevision.setRevisionId(specifiedId);

        try {
            itemRevisionService.postItemRevision(itemRevision, 400);
        } catch (Exception ex) {
            logger.logInfo("Expected exception");
        }

        //item attribute
        ItemAttribute itemAttribute = new ItemAttribute();
        HashMap<String, SimpleLocaleProperties> itemAttributeLocales = new HashMap<>();
        SimpleLocaleProperties itemAttributeProperties = new SimpleLocaleProperties();

        itemAttributeProperties.setName("testItemAttribute_" + RandomFactory.getRandomStringOfAlphabet(10));
        itemAttributeProperties.setDescription(RandomFactory.getRandomStringOfAlphabetOrNumeric(30));
        itemAttributeLocales.put(defaultLocale, itemAttributeProperties);

        itemAttribute.setType(CatalogItemAttributeType.GENRE.name());
        itemAttribute.setLocales(itemAttributeLocales);
        itemAttribute.setId(specifiedId);

        try {
            itemAttributeService.postItemAttribute(itemAttribute, 400);
        } catch (Exception ex) {
            logger.logInfo("Expected exception");
        }

        //offer
        Offer offer = offerService.prepareOfferEntity(defaultOfferFileName, organizationId);
        offer.setOfferId(specifiedId);

        try {
            offerService.postOffer(offer, 400);
        } catch (Exception ex) {
            logger.logInfo("Expected exception");
        }

        //offer revision
        Offer offerPrepared = offerService.postDefaultOffer(organizationId);
        OfferRevision offerRevision = offerRevisionService.prepareOfferRevisionEntity(defaultOfferRevisionFileName, organizationId);
        offerRevision.setOfferId(offerPrepared.getOfferId());
        offerRevision.setRevisionId(specifiedId);

        try {
            offerRevisionService.postOfferRevision(offerRevision, 400);
        } catch (Exception ex) {
            logger.logInfo("Expected exception");
        }

        //offer attribute
        OfferAttribute offerAttribute = new OfferAttribute();
        HashMap<String, SimpleLocaleProperties> offerAttributeLocales = new HashMap<>();
        SimpleLocaleProperties offerAttributeProperties = new SimpleLocaleProperties();

        offerAttributeProperties.setName("testOfferAttribute_" + RandomFactory.getRandomStringOfAlphabet(10));
        offerAttributeProperties.setDescription(RandomFactory.getRandomStringOfAlphabetOrNumeric(30));
        offerAttributeLocales.put(defaultLocale, offerAttributeProperties);

        offerAttribute.setType(CatalogOfferAttributeType.CATEGORY.name());
        offerAttribute.setLocales(offerAttributeLocales);
        offerAttribute.setId(specifiedId);

        try {
            offerAttributeService.postOfferAttribute(offerAttribute, 400);
        } catch (Exception ex) {
            logger.logInfo("Expected exception");
        }

        //price tier
        PriceTier priceTier = new PriceTier();

        Map<String, SimpleLocaleProperties> locales = new HashMap<>();
        SimpleLocaleProperties simpleLocaleProperties = new SimpleLocaleProperties();
        Map<String, Map<String, BigDecimal>> prices = new HashMap<>();
        Map<String, BigDecimal> price1 = new HashMap<>();
        Map<String, BigDecimal> price2 = new HashMap<>();

        simpleLocaleProperties.setName("testPriceTier_" + RandomFactory.getRandomStringOfAlphabet(10));
        simpleLocaleProperties.setDescription(RandomFactory.getRandomStringOfAlphabetOrNumeric(30));
        locales.put(defaultLocale, simpleLocaleProperties);

        price1.put(Currency.USD.name(), new BigDecimal(9.83));
        price1.put(Currency.JPY.name(), new BigDecimal(660));

        price2.put(Currency.CNY.name(), new BigDecimal(50.00));
        price2.put(Currency.USD.name(), new BigDecimal(9.55));

        prices.put(Country.US.toString(), price1);
        prices.put(Country.CN.toString(), price2);

        priceTier.setLocales(locales);
        priceTier.setPrices(prices);
        priceTier.setId(specifiedId);

        try {
            priceTierService.postPriceTier(priceTier, 400);
        } catch (Exception ex) {
            logger.logInfo("Expected exception");
        }

    }

    @Property(
            priority = Priority.Dailies,
            features = "Bug 402",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Verify posting should fail if ownerIDs are different for item, item revision, offer, offer revision",
            steps = {
                    "1. Prepare two organization ids",
                    "2. Post an item with one organization Id",
                    "3. Post an item revision with the other organization Id, verify the failure",
                    "4. Do same with offer and offer revision"
            }
    )
    @Test
    public void testSameOwnerId() throws Exception {

        //item and item revision
        OrganizationId organizationId1 = organizationService.postDefaultOrganization().getId();
        OrganizationId organizationId2 = organizationService.postDefaultOrganization().getId();

        Item item = itemService.postDefaultItem(CatalogItemType.APP, organizationId2);
        ItemRevision itemRevisionPrepared = itemRevisionService.prepareItemRevisionEntity(defaultDigitalItemRevisionFileName);

        itemRevisionPrepared.setItemId(item.getItemId());
        itemRevisionPrepared.setOwnerId(organizationId1);

        ItemRevision itemRevision = itemRevisionService.postItemRevision(itemRevisionPrepared);

        itemRevision.setStatus(CatalogEntityStatus.APPROVED.name());

        try {
            itemRevisionService.updateItemRevision(itemRevision.getRevisionId(), itemRevision, 400);
        } catch (Exception ex) {
            logger.logInfo("Expected exception");
        }

        //offer and offer revision
        Offer offer = offerService.postDefaultOffer(organizationId2);
        OfferRevision offerRevisionPrepared = offerRevisionService.prepareOfferRevisionEntity(defaultOfferRevisionFileName, organizationId1, false);
        offerRevisionPrepared.setOfferId(offer.getOfferId());

        OfferRevision offerRevision = offerRevisionService.postOfferRevision(offerRevisionPrepared);

        offerRevision.setStatus(CatalogEntityStatus.PENDING_REVIEW.name());

        try {
            offerRevisionService.updateOfferRevision(offerRevision.getRevisionId(), offerRevision, 400);
        } catch (Exception ex) {
            logger.logInfo("Expected exception");
        }
    }

}