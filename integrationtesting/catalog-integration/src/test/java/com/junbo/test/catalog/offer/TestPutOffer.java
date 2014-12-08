/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.offer;

import com.junbo.test.common.Entities.enums.ComponentType;
import com.junbo.test.common.apihelper.identity.impl.OrganizationServiceImpl;
import com.junbo.test.common.apihelper.identity.OrganizationService;
import com.junbo.catalog.spec.model.attribute.OfferAttribute;
import com.junbo.test.catalog.impl.OfferAttributeServiceImpl;
import com.junbo.catalog.spec.model.attribute.ItemAttribute;
import com.junbo.test.catalog.impl.ItemAttributeServiceImpl;
import com.junbo.test.catalog.OfferAttributeService;
import com.junbo.test.catalog.impl.OfferServiceImpl;
import com.junbo.test.catalog.ItemAttributeService;
import com.junbo.test.catalog.util.BaseTestClass;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.test.common.apihelper.oauth.OAuthService;
import com.junbo.test.common.apihelper.oauth.enums.GrantType;
import com.junbo.test.common.apihelper.oauth.impl.OAuthServiceImpl;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.catalog.OfferService;
import com.junbo.common.id.OrganizationId;
import com.junbo.test.common.property.*;

import org.testng.annotations.Test;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jason
 * Time: 4/10/2014
 * For testing catalog put offer(s) API
 */
public class TestPutOffer extends BaseTestClass {

    private LogHelper logger = new LogHelper(TestPutOffer.class);
    private OfferService offerService = OfferServiceImpl.instance();
    private OrganizationId organizationId;

    private void prepareTestData() throws Exception {
        OrganizationService organizationService = OrganizationServiceImpl.instance();
        organizationId = organizationService.postDefaultOrganization().getId();
    }

    @Property(
            priority = Priority.BVT,
            features = "Put v1/offers/{offerId}",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test put offer successfully",
            steps = {
                    "1. Prepare a default offer",
                    "2. Put the offer with corrected fields values",
                    "3. Verify the action could be successful"
            }
    )
    @Test
    public void testPutOffer() throws Exception {
        this.prepareTestData();

        //Prepare an offer
        Offer offer = offerService.postDefaultOffer(organizationId);

        OAuthService oAuthTokenService = OAuthServiceImpl.getInstance();
        oAuthTokenService.postAccessToken(GrantType.CLIENT_CREDENTIALS, ComponentType.CATALOGADMIN);

        //put offer attribute
        OfferAttributeService offerAttributeService = OfferAttributeServiceImpl.instance();
        OfferAttribute offerAttribute1 = offerAttributeService.postDefaultOfferAttribute();
        OfferAttribute offerAttribute2 = offerAttributeService.postDefaultOfferAttribute();
        List<String> category = new ArrayList<>();
        category.add(offerAttribute1.getId());
        category.add(offerAttribute2.getId());

        offer.setCategories(category);
        offer.setEnvironment("PROD");

        Offer offerPut = offerService.updateOffer(offer.getOfferId(), offer);

        //Verification
        Assert.assertEquals(offerPut.getCategories(), category);
        Assert.assertEquals(offerPut.getEnvironment(), "PROD");
    }

    @Property(
            priority = Priority.Comprehensive,
            features = "Put v1/offers/{offerId}",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test put offer successfully",
            steps = {
                    "1. Prepare a default offer",
                    "2. test invalid values(like null, not null and some invalid enum values)",
                    "3. Verify the expected error"
            }
    )
    @Test
    public void testPutOfferInvalidScenarios() throws Exception {
        this.prepareTestData();

        List<String> category = new ArrayList<>();
        List<String> categoryInvalid = new ArrayList<>();
        categoryInvalid.add("0L");
        categoryInvalid.add("1L");

        //Prepare an offer
        Offer offer = offerService.postDefaultOffer(organizationId);
        String offerId = offer.getOfferId();

        //update itself id
        offer.setOfferId("1L");
        verifyExpectedError(offerId, offer);

        //test rev
        offer = offerService.postDefaultOffer(organizationId);
        offer.setRev("0");
        verifyExpectedError(offer.getOfferId(), offer);

        //can't update current revision id
        offer = offerService.postDefaultOffer(organizationId);
        offer.setCurrentRevisionId("0L");
        offer = offerService.updateOffer(offer.getOfferId(), offer);
        Assert.assertNotEquals(offer.getCurrentRevisionId(), "0");

        //test category is not existed
        offer = offerService.postDefaultOffer(organizationId);
        offer.setCategories(categoryInvalid);
        verifyExpectedError(offer.getOfferId(), offer);

        //test category type is genre
        OAuthService oAuthTokenService = OAuthServiceImpl.getInstance();
        oAuthTokenService.postAccessToken(GrantType.CLIENT_CREDENTIALS, ComponentType.CATALOGADMIN);

        ItemAttributeService itemAttributeService = ItemAttributeServiceImpl.instance();
        ItemAttribute itemAttribute = itemAttributeService.postDefaultItemAttribute();
        category.add(itemAttribute.getId());

        offer = offerService.postDefaultOffer(organizationId);
        offer.setCategories(category);
        verifyExpectedError(offer.getOfferId(), offer);

    }

    private void verifyExpectedError(String offerId, Offer offer) {
        try {
            //Error code 400 means "Missing Input field", "Unnecessary field found" or "invalid value"
            offerService.updateOffer(offerId, offer, 400);
            Assert.fail("Put offer should fail");
        }
        catch (Exception ex) {
        }
    }

}
