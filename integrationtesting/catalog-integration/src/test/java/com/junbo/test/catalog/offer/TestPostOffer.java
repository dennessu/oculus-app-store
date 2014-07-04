/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.offer;

import com.junbo.test.common.apihelper.identity.impl.OrganizationServiceImpl;
import com.junbo.test.common.apihelper.identity.OrganizationService;
import com.junbo.test.catalog.impl.OfferAttributeServiceImpl;
import com.junbo.catalog.spec.model.attribute.OfferAttribute;
import com.junbo.catalog.spec.model.attribute.ItemAttribute;
import com.junbo.test.catalog.impl.ItemAttributeServiceImpl;
import com.junbo.test.catalog.impl.OfferServiceImpl;
import com.junbo.test.catalog.OfferAttributeService;
import com.junbo.test.catalog.ItemAttributeService;
import com.junbo.test.catalog.util.BaseTestClass;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.catalog.OfferService;
import com.junbo.common.id.OrganizationId;
import com.junbo.test.common.property.*;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;

/**
  * @author Jason
  * Time: 4/1/2014
  * For testing catalog post offer(s) API
 */
public class TestPostOffer extends BaseTestClass {

    private LogHelper logger = new LogHelper(TestPostOffer.class);
    private OfferService offerService = OfferServiceImpl.instance();
    private final String defaultOffer = "defaultOffer";
    private final String initRevValue = "1";
    private OrganizationId organizationId;

    @BeforeClass
    private void PrepareTestData() throws Exception {
        OrganizationService organizationService = OrganizationServiceImpl.instance();
        organizationId = organizationService.postDefaultOrganization().getId();
    }

    @Property(
            priority = Priority.Dailies,
            features = "Post v1/offers",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Post Offers only with required parameters and all parameters",
            steps = {
                    "1. Post test offers only with required parameters",
                    "2. Verify the parameters",
                    "3. Post test offer with optional parameters",
                    "4. Verify the parameters"
            }
    )
    @Test
    public void testPostOffer() throws Exception {
        //Post test offers only with required parameters
        Offer testOfferRequired = new Offer();
        testOfferRequired.setOwnerId(organizationId);
        Offer offerRtn1 = offerService.postOffer(testOfferRequired);

        checkOfferRequiredParams(offerRtn1, testOfferRequired);

        //Post test offer with optional params
        Offer testOfferFull = offerService.prepareOfferEntity(defaultOffer, organizationId);
        Offer offerRtn2 = offerService.postOffer(testOfferFull);

        checkOfferRequiredParams(offerRtn2, testOfferFull);
        checkOfferOptionalParams(offerRtn2, testOfferFull);
    }

    @Property(
            priority = Priority.Comprehensive,
            features = "Post v1/offers",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test post offers with existed values(category)",
            steps = {
                    "1. Prepare an offer",
                    "2. Prepare two categories for the offer use",
                    "3. Post the offer with the prepared two categories"
            }
    )
    @Test
    public void testPostOfferWithExistedValues() throws Exception {

        OfferService offerService = OfferServiceImpl.instance();
        Offer offer = offerService.prepareOfferEntity(defaultOffer, organizationId);

        OfferAttributeService offerAttributeService = OfferAttributeServiceImpl.instance();
        OfferAttribute offerAttribute1 = offerAttributeService.postDefaultOfferAttribute();
        OfferAttribute offerAttribute2 = offerAttributeService.postDefaultOfferAttribute();
        List<String> categories = new ArrayList<>();
        categories.add(offerAttribute1.getId());
        categories.add(offerAttribute2.getId());

        offer.setCategories(categories);

        Offer offerPosted = offerService.postOffer(offer);
        Assert.assertEquals(offerPosted.getCategories(), categories);
    }

    @Property(
            priority = Priority.Comprehensive,
            features = "Post v1/offers",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test post offers with invalid values",
            steps = {
                    "1. Prepare an offer",
                    "2. test invalid values(like null, not null and some invalid enum values)",
                    "3. Try to post it and verify the expected error"
            }
    )
    @Test
    public void testPostOfferInvalidScenarios() throws Exception {
        List<String> genres = new ArrayList<>();
        List<String> categoryInvalid = new ArrayList<>();
        categoryInvalid.add("0L");
        categoryInvalid.add("1L");

        //test ownerId is null
        Offer testOffer = offerService.prepareOfferEntity(defaultOffer, organizationId);
        testOffer.setOwnerId(null);
        verifyExpectedError(testOffer);

        //test currentRevision is not null
        testOffer = offerService.prepareOfferEntity(defaultOffer, organizationId);
        testOffer.setCurrentRevisionId("0L");
        verifyExpectedError(testOffer);

        //test rev
        testOffer = offerService.prepareOfferEntity(defaultOffer, organizationId);
        testOffer.setRev(initRevValue);
        verifyExpectedError(testOffer);

        //test isPublished is true
        testOffer = offerService.prepareOfferEntity(defaultOffer, organizationId);
        testOffer.setPublished(true);
        verifyExpectedError(testOffer);

        //test category is not existed
        testOffer = offerService.prepareOfferEntity(defaultOffer, organizationId);
        testOffer.setCategories(categoryInvalid);
        verifyExpectedError(testOffer);

        //test category type is genres
        ItemAttributeService itemAttributeService = ItemAttributeServiceImpl.instance();
        ItemAttribute itemAttribute = itemAttributeService.postDefaultItemAttribute();
        genres.add(itemAttribute.getId());

        testOffer = offerService.prepareOfferEntity(defaultOffer, organizationId);
        testOffer.setCategories(genres);
        verifyExpectedError(testOffer);

        //put all invalid scenarios together
        testOffer = offerService.prepareOfferEntity(defaultOffer, organizationId);
        testOffer.setOwnerId(null);
        testOffer.setCurrentRevisionId("0L");
        testOffer.setRev(initRevValue);
        testOffer.setPublished(true);
        testOffer.setCategories(genres);
        verifyExpectedError(testOffer);

    }

    private void checkOfferRequiredParams(Offer offerActual, Offer offerExpected) {
        Assert.assertEquals(offerActual.getOwnerId(), offerExpected.getOwnerId());
    }

    private void checkOfferOptionalParams(Offer offerActual, Offer offerExpected) {
        Assert.assertEquals(offerActual.getAdminInfo(), offerExpected.getAdminInfo());
        Assert.assertEquals(offerActual.getFutureExpansion(), offerExpected.getFutureExpansion());
        Assert.assertEquals(offerActual.getCategories(), offerExpected.getCategories());
        Assert.assertEquals(offerActual.getEnvironment(), offerExpected.getEnvironment());
        Assert.assertEquals(offerActual.getCurrentRevisionId(), offerExpected.getCurrentRevisionId());
        Assert.assertEquals(offerActual.getRevisions(), offerExpected.getRevisions());
    }

    private void verifyExpectedError(Offer offer) {
        try {
            //Error code 400 means "Missing Input field", "Unnecessary field found" or "invalid value"
            offerService.postOffer(offer, 400);
            Assert.fail("Post offer should fail");
        }
        catch (Exception ex) {
        }
    }

}
