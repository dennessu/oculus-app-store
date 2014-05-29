/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.offer;

import com.junbo.test.common.apihelper.identity.impl.UserServiceImpl;
import com.junbo.catalog.spec.model.attribute.OfferAttribute;
import com.junbo.test.catalog.impl.OfferAttributeServiceImpl;
import com.junbo.catalog.spec.model.attribute.ItemAttribute;
import com.junbo.test.catalog.impl.ItemAttributeServiceImpl;
import com.junbo.test.common.apihelper.identity.UserService;
import com.junbo.test.catalog.OfferAttributeService;
import com.junbo.test.catalog.impl.OfferServiceImpl;
import com.junbo.test.catalog.ItemAttributeService;
import com.junbo.test.catalog.util.BaseTestClass;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.catalog.OfferService;
import com.junbo.test.common.property.*;
import com.junbo.common.id.UserId;

import org.testng.annotations.BeforeClass;
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
    private String publisherId;

    @BeforeClass
    private void PrepareTestData() throws Exception {
        UserService userService = UserServiceImpl.instance();
        publisherId = userService.PostUser();
    }

    @Property(
            priority = Priority.Dailies,
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
        //Prepare an offer
        Offer offer = offerService.postDefaultOffer();

        //put offer attribute
        OfferAttributeService offerAttributeService = OfferAttributeServiceImpl.instance();
        OfferAttribute offerAttribute1 = offerAttributeService.postDefaultOfferAttribute();
        OfferAttribute offerAttribute2 = offerAttributeService.postDefaultOfferAttribute();
        List<Long> category = new ArrayList<>();
        category.add(offerAttribute1.getId());
        category.add(offerAttribute2.getId());

        Long userId = IdConverter.hexStringToId(UserId.class, publisherId);

        offer.setCategories(category);
        offer.setEnvironment("PROD");
        offer.setOwnerId(userId);

        Offer offerPut = offerService.updateOffer(offer.getOfferId(), offer);

        //Verification
        Assert.assertEquals(offerPut.getCategories(), category);
        Assert.assertEquals(offerPut.getEnvironment(), "PROD");
        Assert.assertEquals(offerPut.getOwnerId(), userId);
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
        List<Long> category = new ArrayList<>();
        List<Long> categoryInvalid = new ArrayList<>();
        categoryInvalid.add(0L);
        categoryInvalid.add(1L);

        //Prepare an offer
        Offer offer = offerService.postDefaultOffer();
        Long offerId = offer.getOfferId();

        //update itself id
        offer.setOfferId(1L);
        verifyExpectedError(offerId, offer);

        //test rev
        offer = offerService.postDefaultOffer();
        offer.setResourceAge(0);
        verifyExpectedError(offer.getOfferId(), offer);

        //test ownerId is null
        offer = offerService.postDefaultOffer();
        offer.setOwnerId(null);
        verifyExpectedError(offer.getOfferId(), offer);

        //can't update current revision id
        offer = offerService.postDefaultOffer();
        offer.setCurrentRevisionId(0L);
        verifyExpectedError(offer.getOfferId(), offer);

        //test category is not existed
        offer = offerService.postDefaultOffer();
        offer.setCategories(categoryInvalid);
        verifyExpectedError(offer.getOfferId(), offer);

        //test genres type is category
        ItemAttributeService itemAttributeService = ItemAttributeServiceImpl.instance();
        ItemAttribute itemAttribute = itemAttributeService.postDefaultItemAttribute();
        category.add(itemAttribute.getId());

        offer = offerService.postDefaultOffer();
        offer.setCategories(category);
        verifyExpectedError(offer.getOfferId(), offer);

    }

    private void verifyExpectedError(Long offerId, Offer offer) {
        try {
            //Error code 400 means "Missing Input field", "Unnecessary field found" or "invalid value"
            offerService.updateOffer(offerId, offer, 400);
            Assert.fail("Put offer should fail");
        }
        catch (Exception ex) {
        }
    }

}