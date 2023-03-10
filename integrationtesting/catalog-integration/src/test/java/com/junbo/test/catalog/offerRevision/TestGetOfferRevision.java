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
import com.junbo.test.catalog.impl.ItemRevisionServiceImpl;
import com.junbo.test.catalog.enums.CatalogEntityStatus;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.test.catalog.impl.OfferServiceImpl;
import com.junbo.test.catalog.enums.CatalogItemType;
import com.junbo.test.catalog.impl.ItemServiceImpl;
import com.junbo.test.catalog.OfferRevisionService;
import com.junbo.test.catalog.enums.LocaleAccuracy;
import com.junbo.test.catalog.ItemRevisionService;
import com.junbo.test.catalog.util.BaseTestClass;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.RandomFactory;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.catalog.OfferService;
import com.junbo.common.id.OrganizationId;
import com.junbo.test.catalog.ItemService;

import com.junbo.test.common.property.*;
import com.junbo.common.model.Results;

import org.testng.annotations.Test;
import org.testng.Assert;

import java.util.*;

/**
 * @author Jason
 * Time: 5/22/2014
 * For testing catalog get offer revision(s) API
 */
public class TestGetOfferRevision extends BaseTestClass {

    private LogHelper logger = new LogHelper(TestGetOfferRevision.class);

    private OfferService offerService = OfferServiceImpl.instance();
    private OfferRevisionService offerRevisionService = OfferRevisionServiceImpl.instance();

    private Item item;
    private Offer testOffer;
    private OrganizationId organizationId;
    private final String defaultLocale = "en_US";

    private void prepareTestData() throws Exception {
        OrganizationService organizationService = OrganizationServiceImpl.instance();
        organizationId = organizationService.postDefaultOrganization().getId();
        testOffer = offerService.postDefaultOffer(organizationId);

        //Prepare an item
        ItemService itemService = ItemServiceImpl.instance();
        item = itemService.postDefaultItem(CatalogItemType.getRandom(), organizationId);
        item = releaseItem(item);
    }

    @Property(
            priority = Priority.Dailies,
            features = "Get v1/offer-revisions/{offerRevisionId}",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Get an offer revision by offerRevisionId(valid, invalid scenarios)",
            steps = {
                    "1. Prepare an offer revision",
                    "2. Get it by its Id",
                    "3. Verify not able to get the offer revision by invalid Id",
                    "4. Release the offer revision",
                    "5. Get the offer revision by Id again, verify the behavior is successful"
            }
    )
    @Test
    public void testGetAnOfferRevisionById() throws Exception {
        this.prepareTestData();

        //Prepare an offer revision
        OfferRevision offerRevision = offerRevisionService.postDefaultOfferRevision(testOffer);

        //get the offer revision by Id, assert not null
        OfferRevision offerRevisionRtn = offerRevisionService.getOfferRevision(offerRevision.getRevisionId());
        Assert.assertNotNull(offerRevisionRtn, "Couldn't get the offer revision by its id");

        //verify the invalid Id scenario
        String invalidId = "0L";
        verifyInvalidScenarios(invalidId);

        //Release the offer revision and then try to get the offer revision
        releaseOfferRevision(offerRevision);
        offerRevisionRtn = offerRevisionService.getOfferRevision(offerRevision.getRevisionId());
        Assert.assertNotNull(offerRevisionRtn, "Can't get the offer revision by its id");

        //verify the invalid Id scenario
        verifyInvalidScenarios(invalidId);
    }

    @Property(
            priority = Priority.Dailies,
            features = "Get v1/offer-revisions/{offerRevisionId}?locale={locale}",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Get an offer revision by offerRevisionId and locale",
            steps = {
                    "1. Prepare an offer revision",
                    "2. Get it id and locale",
                    "3. Verify localeAccuracy and offerRevision locale property"
            }
    )
    @Test
    public void testGetAnOfferRevisionByIdLocale() throws Exception {
        this.prepareTestData();

        HashMap<String, List<String>> httpPara = new HashMap<>();
        List<String> locale = new ArrayList<>();

        //Prepare an offer revision
        OfferRevision offerRevision = offerRevisionService.postDefaultOfferRevision(testOffer);
        OfferRevisionLocaleProperties offerRevisionLocaleProperties = offerRevision.getLocales().get(defaultLocale);

        //Get without locale
        OfferRevision offerRevisionRtn = offerRevisionService.getOfferRevision(offerRevision.getRevisionId());
        Assert.assertTrue(LocaleAccuracy.HIGH.is(offerRevisionRtn.getLocaleAccuracy()));

        //Get default locale 1. Medium
        locale.add(defaultLocale);
        httpPara.put("locale", locale);
        offerRevisionRtn = offerRevisionService.getOfferRevision(offerRevision.getRevisionId(), httpPara);
        Assert.assertTrue(LocaleAccuracy.MEDIUM.is(offerRevisionRtn.getLocaleAccuracy()));
        Assert.assertEquals(offerRevisionRtn.getLocales().get(defaultLocale).getName(), offerRevisionLocaleProperties.getName());

        //Get default locale 2. High
        ItemService itemService = ItemServiceImpl.instance();
        ItemRevisionService itemRevisionService = ItemRevisionServiceImpl.instance();

        Item item = itemService.getItem(offerRevision.getItems().get(0).getItemId());
        ItemRevision itemRevision = itemRevisionService.getItemRevision(item.getCurrentRevisionId());

        Map<String, OfferRevisionLocaleProperties> locales = offerRevision.getLocales();
        OfferRevisionLocaleProperties offerRevisionLocalePropertiesDefaultUS = locales.get(defaultLocale);
        offerRevisionLocalePropertiesDefaultUS.setItems(itemRevision.getLocales());

        locales.put(defaultLocale, offerRevisionLocalePropertiesDefaultUS);
        offerRevision.setLocales(locales);
        offerRevision = offerRevisionService.updateOfferRevision(offerRevision.getRevisionId(), offerRevision);

        offerRevisionRtn = offerRevisionService.getOfferRevision(offerRevision.getRevisionId(), httpPara);
        Assert.assertTrue(LocaleAccuracy.HIGH.is(offerRevisionRtn.getLocaleAccuracy()));
        Assert.assertEquals(offerRevisionRtn.getLocales().get(defaultLocale).getName(), offerRevisionLocaleProperties.getName());

        //Get fr_FR
        locale.clear();
        locale.add("fr_FR");
        httpPara.put("locale", locale);
        offerRevisionRtn = offerRevisionService.getOfferRevision(offerRevision.getRevisionId(), httpPara);
        Assert.assertTrue(LocaleAccuracy.LOW.is(offerRevisionRtn.getLocaleAccuracy()));
        Assert.assertEquals(offerRevisionRtn.getLocales().get("fr_FR").getName(), offerRevisionLocaleProperties.getName());

        //get zh_CN
        locale.clear();
        locale.add("zh_CN");
        httpPara.put("locale", locale);
        offerRevisionRtn = offerRevisionService.getOfferRevision(offerRevision.getRevisionId(), httpPara);
        Assert.assertTrue(LocaleAccuracy.LOW.is(offerRevisionRtn.getLocaleAccuracy()));
        Assert.assertEquals(offerRevisionRtn.getLocales().get("zh_CN").getName(), offerRevisionLocaleProperties.getName());

        //Add fr_FR locale to the offer revision
        locales = offerRevision.getLocales();
        OfferRevisionLocaleProperties offerRevisionLocalePropertiesFR = new OfferRevisionLocaleProperties();
        offerRevisionLocalePropertiesFR.setName("testOfferRevision_fr_FR_" + RandomFactory.getRandomStringOfAlphabet(10));
        locales.put("fr_FR", offerRevisionLocalePropertiesFR);

        offerRevision.setLocales(locales);
        offerRevisionService.updateOfferRevision(offerRevision.getRevisionId(), offerRevision);

        //try get default locale
        locale.clear();
        locale.add(defaultLocale);
        httpPara.put("locale", locale);
        offerRevisionRtn = offerRevisionService.getOfferRevision(offerRevision.getRevisionId(), httpPara);
        Assert.assertTrue(LocaleAccuracy.HIGH.is(offerRevisionRtn.getLocaleAccuracy()));
        Assert.assertEquals(offerRevisionRtn.getLocales().get(defaultLocale).getName(), offerRevisionLocaleProperties.getName());

        //Get fr_FR
        locale.clear();
        locale.add("fr_FR");
        httpPara.put("locale", locale);
        offerRevisionRtn = offerRevisionService.getOfferRevision(offerRevision.getRevisionId(), httpPara);
        Assert.assertTrue(LocaleAccuracy.MEDIUM.is(offerRevisionRtn.getLocaleAccuracy()));
        Assert.assertEquals(offerRevisionRtn.getLocales().get("fr_FR").getName(), offerRevisionLocalePropertiesFR.getName());

        //Get fr_CA
        locale.clear();
        locale.add("fr_CA");
        httpPara.put("locale", locale);
        offerRevisionRtn = offerRevisionService.getOfferRevision(offerRevision.getRevisionId(), httpPara);
        Assert.assertTrue(LocaleAccuracy.LOW.is(offerRevisionRtn.getLocaleAccuracy()));
        Assert.assertEquals(offerRevisionRtn.getLocales().get("fr_CA").getName(), offerRevisionLocalePropertiesFR.getName());

        //get zh_CN
        locale.clear();
        locale.add("zh_CN");
        httpPara.put("locale", locale);
        offerRevisionRtn = offerRevisionService.getOfferRevision(offerRevision.getRevisionId(), httpPara);
        Assert.assertTrue(LocaleAccuracy.LOW.is(offerRevisionRtn.getLocaleAccuracy()));
        Assert.assertEquals(offerRevisionRtn.getLocales().get("zh_CN").getName(), offerRevisionLocaleProperties.getName());
    }

    @Property(
            priority = Priority.Dailies,
            features = "Get v1/offer-revisions?offerId=&revisionId=",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Get offer revisions by offerId and revisionId(valid, invalid scenarios)",
            steps = {
                    "1. Prepare some offers and offer revisions",
                    "2. Get revisions by offer Id",
                    "3. Get revisions by revision Id",
                    "4. Get revisions by offer Id and revision Id"
            }
    )
    @Test
    public void testGetOfferRevisionsByOfferIdRevisionId() throws Exception {
        this.prepareTestData();

        HashMap<String, List<String>> getOptions = new HashMap<>();
        List<String> offerIds = new ArrayList<>();
        List<String> revisionIds = new ArrayList<>();
        List<String> status = new ArrayList<>();
        List<String> publisher = new ArrayList<>();

        //Prepare two offers
        Offer offer1 = offerService.postDefaultOffer(organizationId);
        Offer offer2 = offerService.postDefaultOffer(organizationId);
        String offerId1 = offer1.getOfferId();
        String offerId2 = offer2.getOfferId();

        //Prepare some offer revisions
        OfferRevision offerRevision1 = offerRevisionService.postDefaultOfferRevision(offer1, item);
        OfferRevision offerRevision2 = offerRevisionService.postDefaultOfferRevision(offer1, item);
        OfferRevision offerRevision3 = offerRevisionService.postDefaultOfferRevision(offer2, item);
        OfferRevision offerRevision4 = offerRevisionService.postDefaultOfferRevision(offer2, item);

        //set status
        status.add(CatalogEntityStatus.DRAFT.name());
        getOptions.put("status", status);

        publisher.add(IdConverter.idToHexString(organizationId));
        getOptions.put("publisherId", publisher);

        //get revisions by offerId
        offerIds.add(offerId1);
        getOptions.put("offerId", offerIds);
        verifyGetResults(getOptions, 2, offerRevision1, offerRevision2);
        offerIds.add(offerId2);
        getOptions.put("offerId", offerIds);
        verifyGetResults(getOptions, 4, offerRevision1, offerRevision2, offerRevision3, offerRevision4);

        offerIds.clear();
        offerIds.add(offerId2);
        offerIds.add("000000");
        getOptions.put("offerId", offerIds);
        verifyGetResults(getOptions, 2, offerRevision3, offerRevision4);

        //get revisions by offerId and revisionId, only revisionId works
        revisionIds.add(offerRevision4.getRevisionId());
        getOptions.put("revisionId", revisionIds);
        verifyGetResults(getOptions, 1, offerRevision4);

        revisionIds.add("0000000");
        getOptions.put("revisionId", revisionIds);
        verifyGetResults(getOptions, 1, offerRevision4);

        revisionIds.add(offerRevision1.getRevisionId());
        revisionIds.add(offerRevision2.getRevisionId());
        getOptions.put("revisionId", revisionIds);
        verifyGetResults(getOptions, 3, offerRevision1, offerRevision2, offerRevision4);

        offerIds.clear();
        offerIds.add(offerId2);
        offerIds.add(offerId1);
        getOptions.put("offerId", offerIds);
        verifyGetResults(getOptions, 3, offerRevision1, offerRevision2, offerRevision4);

        offerIds.clear();
        getOptions.put("offerId", offerIds);
        verifyGetResults(getOptions, 3, offerRevision1, offerRevision2, offerRevision4);

        offerIds.add("000000");
        getOptions.put("offerId", offerIds);
        verifyGetResults(getOptions, 3, offerRevision1, offerRevision2, offerRevision4);
    }

    @Property(
            priority = Priority.Dailies,
            features = "Get v1/offer-revisions?revisionId=&status=",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Get offer revisions by revision status(valid, invalid scenarios)",
            steps = {
                    "1. Prepare some offer revisions",
                    "2. Get revisions by status(valid and invalid scenarios)",
                    "3. Approved the offer revisions",
                    "4. Get revisions by status(valid and invalid scenarios)",
                    "5. Deleted the offer revisions",
                    "6. Get revisions by status(valid and invalid scenarios)"
            }
    )
    @Test
    public void testGetOfferRevisionsByRevisionStatus() throws Exception {
        this.prepareTestData();

        HashMap<String, List<String>> getOptions = new HashMap<>();
        List<String> revisionIds = new ArrayList<>();

        //Prepare two offers
        Offer offer1 = offerService.postDefaultOffer(organizationId);
        Offer offer2 = offerService.postDefaultOffer(organizationId);

        //Prepare some offer revisions
        OfferRevision offerRevision1 = offerRevisionService.postDefaultOfferRevision(offer1, item);
        OfferRevision offerRevision2 = offerRevisionService.postDefaultOfferRevision(offer2, item);
        String offerRevisionId1 = offerRevision1.getRevisionId();
        String offerRevisionId2 = offerRevision2.getRevisionId();

        revisionIds.add(offerRevisionId1);
        revisionIds.add(offerRevisionId2);
        getOptions.put("revisionId", revisionIds);

        setSearchStatus(CatalogEntityStatus.DRAFT.getEntityStatus(), getOptions, 2, offerRevision1, offerRevision2);
        setSearchStatus(CatalogEntityStatus.APPROVED.getEntityStatus(), getOptions, 0);
        setSearchStatus("invalidStatus", getOptions, 0);

        //release one offer revision
        releaseOfferRevision(offerRevision1);

        setSearchStatus(CatalogEntityStatus.DRAFT.getEntityStatus(), getOptions, 1, offerRevision2);
        setSearchStatus(CatalogEntityStatus.APPROVED.getEntityStatus(), getOptions, 1, offerRevision1);
        setSearchStatus(CatalogEntityStatus.PENDING_REVIEW.getEntityStatus(), getOptions, 0);
        setSearchStatus("invalidStatus", getOptions, 0);

        //release the both two offer revisions
        releaseOfferRevision(offerRevision2);

        setSearchStatus(CatalogEntityStatus.DRAFT.getEntityStatus(), getOptions, 0);
        setSearchStatus(CatalogEntityStatus.APPROVED.getEntityStatus(), getOptions, 2, offerRevision1, offerRevision2);
        setSearchStatus(CatalogEntityStatus.PENDING_REVIEW.getEntityStatus(), getOptions, 0);
        setSearchStatus("invalidStatus", getOptions, 0);
    }

    @Property(
            priority = Priority.Dailies,
            features = "Get v1/offer-revisions?offerID=&timeInMillis=",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Get offer revisions by offerId and timestamp(valid, invalid scenarios)",
            steps = {
                    "1. Prepare some offer revisions",
                    "2. Get offer revisions by offerId and timestamp"
            }
    )
    @Test
    public void testGetOfferRevisionsByOfferIdTimestamp() throws Exception {
        this.prepareTestData();

        HashMap<String, List<String>> getOptions = new HashMap<>();
        List<String> offerIds = new ArrayList<>();
        List<String> timeStamp = new ArrayList<>();
        List<String> revisionIds = new ArrayList<>();

        //Prepare two offers
        Offer offer1 = offerService.postDefaultOffer(organizationId);
        Offer offer2 = offerService.postDefaultOffer(organizationId);
        String offerId1 = offer1.getOfferId();
        String offerId2 = offer2.getOfferId();
        //Prepare some offer revisions
        OfferRevision offerRevision1 = offerRevisionService.postDefaultOfferRevision(offer1, item);
        OfferRevision offerRevision2 = offerRevisionService.postDefaultOfferRevision(offer1, item);
        OfferRevision offerRevision3 = offerRevisionService.postDefaultOfferRevision(offer2, item);
        OfferRevision offerRevision4 = offerRevisionService.postDefaultOfferRevision(offer2, item);

        offerIds.add(offerId1);
        offerIds.add(offerId2);
        getOptions.put("offerId", offerIds);

        Calendar current = Calendar.getInstance();

        //not released, will get 0 results
        timeStamp.add(Long.toString(current.getTimeInMillis()));
        getOptions.put("timeInMillis", timeStamp);
        verifyGetResults(getOptions, 0);

        //release offer revision1
        releaseOfferRevision(offerRevision1);
        current = Calendar.getInstance();
        timeStamp.clear();
        timeStamp.add(Long.toString(current.getTimeInMillis()));
        getOptions.put("timeInMillis", timeStamp);
        verifyGetResults(getOptions, 1, offerRevision1);

        //release offerRevision2
        releaseOfferRevision(offerRevision2);
        current = Calendar.getInstance();
        timeStamp.clear();
        timeStamp.add(Long.toString(current.getTimeInMillis()));
        getOptions.put("timeInMillis", timeStamp);
        verifyGetResults(getOptions, 1, offerRevision2);

        //set revisionId, but revisionId should not work, still get offerRevision2
        revisionIds.add(offerRevision3.getRevisionId());
        getOptions.put("revisionId", revisionIds);
        verifyGetResults(getOptions, 1, offerRevision2);

        //release the other 2 revisions
        releaseOfferRevision(offerRevision3);
        releaseOfferRevision(offerRevision4);

        current = Calendar.getInstance();
        timeStamp.clear();
        timeStamp.add(Long.toString(current.getTimeInMillis()));
        getOptions.put("timeInMillis", timeStamp);
        verifyGetResults(getOptions, 2, offerRevision2, offerRevision4);

    }

    @Property(
            priority = Priority.Dailies,
            features = "Get v1/offer-revisions?revisionId=&locale=",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Get offer revisions by revisionId and locale",
            steps = {
                    "1. Prepare some offer revision",
                    "2. Get them by id and locale",
                    "3. Verify localeAccuracy and offerRevision locale property"
            }
    )
    @Test
    public void testGetOfferRevisionsByRevisionIdLocale() throws Exception {
        this.prepareTestData();

        HashMap<String, List<String>> getOptions = new HashMap<>();
        List<String> revisionIds = new ArrayList<>();
        List<String> locales = new ArrayList<>();
        List<String> status = new ArrayList<>();
        List<String> publisher = new ArrayList<>();

        //Prepare two offers
        Offer offer1 = offerService.postDefaultOffer(organizationId);
        Offer offer2 = offerService.postDefaultOffer(organizationId);

        //Prepare some offer revisions
        OfferRevision offerRevision1 = offerRevisionService.postDefaultOfferRevision(offer1, item);
        OfferRevision offerRevision2 = offerRevisionService.postDefaultOfferRevision(offer1, item);
        OfferRevision offerRevision3 = offerRevisionService.postDefaultOfferRevision(offer2, item);
        OfferRevision offerRevision4 = offerRevisionService.postDefaultOfferRevision(offer2, item);

        //set status to draft as if status is not specified, it will be set to APPROVED
        status.add(CatalogEntityStatus.DRAFT.name());
        getOptions.put("status", status);

        publisher.add(IdConverter.idToHexString(organizationId));
        getOptions.put("publisherId", publisher);

        //get revisions by revisionId, without locale
        revisionIds.add(offerRevision4.getRevisionId());
        getOptions.put("revisionId", revisionIds);

        verifyGetResultsInLocale(getOptions, LocaleAccuracy.HIGH, offerRevision4);

        //set locale
        locales.add(defaultLocale);
        getOptions.put("locale", locales);

        verifyGetResultsInLocale(getOptions, LocaleAccuracy.MEDIUM, offerRevision4);

        //get revisions by revisionId
        revisionIds.add(offerRevision2.getRevisionId());
        revisionIds.add(offerRevision3.getRevisionId());
        getOptions.put("revisionId", revisionIds);

        verifyGetResultsInLocale(getOptions, LocaleAccuracy.MEDIUM, offerRevision2, offerRevision3, offerRevision4);

        //set locale
        locales.clear();
        locales.add("fr_FR");
        getOptions.put("locale", locales);

        verifyGetResultsInLocale(getOptions, LocaleAccuracy.LOW, offerRevision2, offerRevision3, offerRevision4);

        //set another locale
        locales.clear();
        locales.add("zh_CN");
        getOptions.put("locale", locales);

        revisionIds.clear();
        revisionIds.add(offerRevision1.getRevisionId());
        getOptions.put("revisionId", revisionIds);

        verifyGetResultsInLocale(getOptions, LocaleAccuracy.LOW, offerRevision1);

    }

    private void verifyInvalidScenarios(String offerRevisionId) throws Exception {
        try {
            offerRevisionService.getOfferRevision(offerRevisionId, null, 404);
            Assert.fail("Shouldn't get offer revision with wrong id");
        }
        catch (Exception e) {
            logger.logInfo("Expected exception: couldn't get offer revision with wrong id");
        }
    }

    private void setSearchStatus(String status, HashMap<String, List<String>> getOptions, int expectedRtnSize, OfferRevision... offerRevisions)
            throws Exception {
        List<String> searchStatus = new ArrayList<>();
        List<String> publisher = new ArrayList<>();

        publisher.add(IdConverter.idToHexString(organizationId));
        getOptions.put("publisherId", publisher);

        searchStatus.add(status);
        getOptions.put("status", searchStatus);

        verifyGetResults(getOptions, expectedRtnSize, offerRevisions);
    }

    private void verifyGetResults(HashMap<String, List<String>> getOptions, int expectedRtnSize, OfferRevision... offerRevisions) throws Exception {
        Results<OfferRevision> offerRevisionsRtn = offerRevisionService.getOfferRevisions(getOptions);
        Assert.assertEquals(offerRevisionsRtn.getItems().size(), expectedRtnSize);
        for (OfferRevision temp : offerRevisions) {
            Assert.assertTrue(isContain(offerRevisionsRtn, temp));
        }
    }

    private void verifyGetResultsInLocale(HashMap<String, List<String>> getOptions, LocaleAccuracy expectedLocaleAccuracy,
                                          OfferRevision... offerRevisions) throws Exception {
        String locale;

        if (getOptions.get("locale") != null) {
            locale = getOptions.get("locale").get(0);
        } else {
            locale = defaultLocale;
        }

        Results<OfferRevision> offerRevisionsRtn = offerRevisionService.getOfferRevisions(getOptions);

        for (OfferRevision temp : offerRevisions) {
            Assert.assertTrue(isContain(offerRevisionsRtn, temp));
        }

        for (OfferRevision temp : offerRevisionsRtn.getItems()) {
            Assert.assertNotNull(temp.getLocales().get(locale));
            Assert.assertEquals(temp.getLocaleAccuracy(), expectedLocaleAccuracy.name());
        }
    }
}

