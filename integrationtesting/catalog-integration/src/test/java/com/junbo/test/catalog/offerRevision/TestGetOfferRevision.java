/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.offerRevision;

import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.common.id.OfferId;
import com.junbo.common.id.OfferRevisionId;
import com.junbo.common.id.OrganizationId;
import com.junbo.common.model.Results;
import com.junbo.test.catalog.OfferRevisionService;
import com.junbo.test.catalog.OfferService;
import com.junbo.test.catalog.enums.CatalogEntityStatus;
import com.junbo.test.catalog.impl.OfferRevisionServiceImpl;
import com.junbo.test.catalog.impl.OfferServiceImpl;
import com.junbo.test.catalog.util.BaseTestClass;
import com.junbo.test.common.apihelper.identity.OrganizationService;
import com.junbo.test.common.apihelper.identity.impl.OrganizationServiceImpl;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * @author Jason
 * Time: 5/22/2014
 * For testing catalog get offer revision(s) API
 */
public class TestGetOfferRevision extends BaseTestClass {

    private LogHelper logger = new LogHelper(TestGetOfferRevision.class);
    private OfferRevisionService offerRevisionService = OfferRevisionServiceImpl.instance();
    private OfferService offerService = OfferServiceImpl.instance();
    private OrganizationId organizationId;
    private Offer testOffer;

    @BeforeClass
    private void PrepareTestData() throws Exception {
        OrganizationService organizationService = OrganizationServiceImpl.instance();
        organizationId = organizationService.postDefaultOrganization().getId();
        testOffer = offerService.postDefaultOffer(organizationId);
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
        HashMap<String, List<String>> getOptions = new HashMap<>();
        List<String> offerIds = new ArrayList<>();
        List<String> revisionIds = new ArrayList<>();

        //Prepare two offers
        Offer offer1 = offerService.postDefaultOffer(organizationId);
        Offer offer2 = offerService.postDefaultOffer(organizationId);
        String offerId1 = offer1.getOfferId();
        String offerId2 = offer2.getOfferId();

        //Prepare some offer revisions
        OfferRevision offerRevision1 = offerRevisionService.postDefaultOfferRevision(offer1);
        OfferRevision offerRevision2 = offerRevisionService.postDefaultOfferRevision(offer1);
        OfferRevision offerRevision3 = offerRevisionService.postDefaultOfferRevision(offer2);
        OfferRevision offerRevision4 = offerRevisionService.postDefaultOfferRevision(offer2);

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
        revisionIds.add(IdConverter.idToUrlString(OfferRevisionId.class, offerRevision4.getRevisionId()));
        getOptions.put("revisionId", revisionIds);
        verifyGetResults(getOptions, 1, offerRevision4);

        revisionIds.add("0000000");
        getOptions.put("revisionId", revisionIds);
        verifyGetResults(getOptions, 1, offerRevision4);

        revisionIds.add(IdConverter.idToUrlString(OfferRevisionId.class, offerRevision1.getRevisionId()));
        revisionIds.add(IdConverter.idToUrlString(OfferRevisionId.class, offerRevision2.getRevisionId()));
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
        HashMap<String, List<String>> getOptions = new HashMap<>();
        List<String> revisionIds = new ArrayList<>();

        //Prepare two offers
        Offer offer1 = offerService.postDefaultOffer(organizationId);
        Offer offer2 = offerService.postDefaultOffer(organizationId);

        //Prepare some offer revisions
        OfferRevision offerRevision1 = offerRevisionService.postDefaultOfferRevision(offer1);
        OfferRevision offerRevision2 = offerRevisionService.postDefaultOfferRevision(offer2);
        String offerRevisionId1 = IdConverter.idToUrlString(OfferRevisionId.class, offerRevision1.getRevisionId());
        String offerRevisionId2 = IdConverter.idToUrlString(OfferRevisionId.class, offerRevision2.getRevisionId());

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

        HashMap<String, List<String>> getOptions = new HashMap<>();
        List<String> offerIds = new ArrayList<>();
        List<String> timeStamp = new ArrayList<>();
        List<String> revisionIds = new ArrayList<>();

        //Prepare two offers
        Offer offer1 = offerService.postDefaultOffer(organizationId);
        Offer offer2 = offerService.postDefaultOffer(organizationId);
        String offerId1 = IdConverter.idToUrlString(OfferId.class, offer1.getOfferId());
        String offerId2 = IdConverter.idToUrlString(OfferId.class, offer2.getOfferId());
        //Prepare some offer revisions
        OfferRevision offerRevision1 = offerRevisionService.postDefaultOfferRevision(offer1);
        OfferRevision offerRevision2 = offerRevisionService.postDefaultOfferRevision(offer1);
        OfferRevision offerRevision3 = offerRevisionService.postDefaultOfferRevision(offer2);
        OfferRevision offerRevision4 = offerRevisionService.postDefaultOfferRevision(offer2);

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
        revisionIds.add(IdConverter.idToUrlString(OfferRevisionId.class, offerRevision3.getRevisionId()));
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

    private void verifyInvalidScenarios(String offerRevisionId) throws Exception {
        try {
            offerRevisionService.getOfferRevision(offerRevisionId, 404);
            Assert.fail("Shouldn't get offer revision with wrong id");
        }
        catch (Exception e) {
            logger.logInfo("Expected exception: couldn't get offer revision with wrong id");
        }
    }

    private void setSearchStatus(String status, HashMap<String, List<String>> getOptions, int expectedRtnSize, OfferRevision... offerRevisions)
            throws Exception {
        List<String> searchStatus = new ArrayList<>();
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

}

