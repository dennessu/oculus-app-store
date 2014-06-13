/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.offer;

import com.junbo.test.catalog.impl.OfferServiceImpl;
import com.junbo.test.catalog.util.BaseTestClass;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.catalog.OfferService;
import com.junbo.test.common.property.*;
import com.junbo.common.model.Results;
import com.junbo.common.id.OfferId;

import org.testng.annotations.Test;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
  * @author Jason
  * Time: 4/1/2014
  * For testing catalog get offer(s) API
*/
public class TestGetOffer extends BaseTestClass {

    private LogHelper logger = new LogHelper(TestGetOffer.class);

    private OfferService offerService = OfferServiceImpl.instance();

    @Property(
            priority = Priority.Dailies,
            features = "Get v1/offers/{offerId}",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Get an Offer by offerId(valid, invalid scenarios)",
            steps = {
                    "1. Prepare an offer",
                    "2. Get the offer by Id",
                    "3. Verify not able to get the offer by invalid Id",
                    "4. Release the offer",
                    "5. Get the offer by Id again, verify the behavior is successful"
            }
    )
    @Test
    public void testGetAnOfferById() throws Exception {

        //Prepare an offer
        Offer offer = offerService.postDefaultOffer();
        String offerId = offer.getOfferId();
        String invalidId = "0L";

        //get the offer by Id, assert not null
        Offer offerRtn = offerService.getOffer(offerId);
        Assert.assertNotNull(offerRtn, "Can't get offers");

        //verify the invalid Id scenario
        verifyInvalidScenarios(invalidId);

        //Release the offer and then try to get the offer
        releaseOffer(offer);

        offerRtn = offerService.getOffer(offerId);
        Assert.assertNotNull(offerRtn, "Can't get offers");

        //verify the invalid Id scenario
        verifyInvalidScenarios(invalidId);
    }

    @Property(
            priority = Priority.Dailies,
            features = "Get v1/offers?offerId=&offerId=",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Get offer(s) by Id(s)(valid, invalid scenarios)",
            steps = {
                    "1. Prepare some offers",
                    "2. Get the offers by their ids",
                    "3. Release the offers",
                    "4. Get the offers by their ids again"
            }
    )
    @Test
    public void testGetOffersByIds() throws Exception {

        //prepare 3 offers for later use
        Offer offer1 = offerService.postDefaultOffer();
        Offer offer2 = offerService.postDefaultOffer();
        Offer offer3 = offerService.postDefaultOffer();
        String offerId1 = IdConverter.idToUrlString(OfferId.class, offer1.getOfferId());
        String offerId2 = IdConverter.idToUrlString(OfferId.class, offer2.getOfferId());
        String offerId3 = IdConverter.idToUrlString(OfferId.class, offer3.getOfferId());

        HashMap<String, List<String>> paraMap = new HashMap<>();
        List<String> listOfferId = new ArrayList<>();

        //Set 1 offer by its Id, verify 1 offer could be gotten
        listOfferId.add(offerId1);
        paraMap.put("offerId", listOfferId);
        verifyGetOffersScenarios(paraMap, 1, offerId1);

        //Set 2 offers by their Ids, verify 2 offers could be gotten
        listOfferId.add(offerId2);
        paraMap.put("offerId", listOfferId);
        verifyGetOffersScenarios(paraMap, 2, offerId1, offerId2);

        //Search all the 3 offers by their Ids
        listOfferId.add(offerId3);
        paraMap.put("offerId", listOfferId);
        verifyGetOffersScenarios(paraMap, 3, offerId1, offerId2, offerId3);

        //Set 2 of 5 to invalid string
        listOfferId.clear();
        listOfferId.add(offerId1);
        listOfferId.add(offerId2);
        listOfferId.add("0000000000");
        listOfferId.add("0000000001");
        paraMap.put("offerId", listOfferId);
        verifyGetOffersScenarios(paraMap, 2, offerId1, offerId2);

        //Release the 3 offers
        releaseOffer(offer1);
        releaseOffer(offer2);
        releaseOffer(offer3);

        //Set 2 offers by their Ids, verify 2 offers could be gotten
        listOfferId.clear();
        listOfferId.add(offerId1);
        listOfferId.add(offerId2);
        paraMap.put("offerId", listOfferId);
        verifyGetOffersScenarios(paraMap, 2, offerId1, offerId2);

        //Search the 4 offers by their Ids, verify only return the 4 offers
        listOfferId.add(offerId3);
        paraMap.put("offerId", listOfferId);
        verifyGetOffersScenarios(paraMap, 3, offerId1, offerId2, offerId3);

        //Set all to invalid string
        listOfferId.clear();
        listOfferId.add("0000000000");
        listOfferId.add("0000000001");
        listOfferId.add("0000000002");
        paraMap.put("offerId", listOfferId);
        verifyGetOffersScenarios(paraMap, 0);
    }

    @Property(
            priority = Priority.Comprehensive,
            features = "Get v1/offers?offerId=&published=",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Get offer(s) by Id(s), published(valid, invalid scenarios)",
            steps = {
                    "1. Prepare some offers",
                    "2. Get the offers by Id(s), published(valid, invalid scenarios)",
                    "3. Release the offers",
                    "4. Get the offers by Id(s), published(valid, invalid scenarios) again"
            }
    )
    @Test
    public void testGetOffersByIdPublished() throws Exception {

        //prepare 3 offers for later use
        Offer offer1 = offerService.postDefaultOffer();
        Offer offer2 = offerService.postDefaultOffer();
        Offer offer3 = offerService.postDefaultOffer();

        performVerification(offer1, offer2, offer3, false);

        //Release the 5 offers
        releaseOffer(offer1);
        releaseOffer(offer2);
        releaseOffer(offer3);

        performVerification(offer1, offer2, offer3, true);
    }

    private void verifyInvalidScenarios(String offerId) throws Exception {
        try {
            offerService.getOffer(offerId, 404);
            Assert.fail("Shouldn't get offers with wrong id");
        }
        catch (Exception e) {
            logger.logInfo("Expected exception: couldn't get offers with wrong id");
        }
    }

    private void performVerification(Offer offer1, Offer offer2, Offer offer3, Boolean isPublished)  throws Exception {

        HashMap<String, List<String>> paraMap = new HashMap<>();
        List<String> listOfferId = new ArrayList<>();
        List<String> listStatus = new ArrayList<>();
        String offerId1 = IdConverter.idToUrlString(OfferId.class, offer1.getOfferId());
        String offerId2 = IdConverter.idToUrlString(OfferId.class, offer2.getOfferId());
        String offerId3 = IdConverter.idToUrlString(OfferId.class, offer3.getOfferId());
        Boolean oppositeValue = !isPublished;

        //Set offer ids
        listOfferId.add(offerId1);
        listOfferId.add(offerId2);
        listOfferId.add(offerId3);

        //set published
        listStatus.add(isPublished.toString());
        paraMap.put("offerId", listOfferId);
        paraMap.put("published", listStatus);
        verifyGetOffersScenarios(paraMap, 3, offerId1, offerId2, offerId3);

        //set published to opposite value
        listStatus.clear();
        listStatus.add(oppositeValue.toString());
        paraMap.put("published", listStatus);
        verifyGetOffersScenarios(paraMap, 0);


        listOfferId.clear();
        listOfferId.add("00000000");
        listStatus.clear();
        listStatus.add(isPublished.toString());
        paraMap.put("offerId", listOfferId);
        paraMap.put("published", listStatus);
        verifyGetOffersScenarios(paraMap, 0);

        listStatus.clear();
        listStatus.add(oppositeValue.toString());
        paraMap.put("published", listStatus);
        verifyGetOffersScenarios(paraMap, 0);
    }

    private void verifyGetOffersScenarios(HashMap<String, List<String>> paraMap, int expectedRtnSize, String... offerId) throws Exception{
        Results<Offer> offerRtn = offerService.getOffers(paraMap);

        Assert.assertEquals(offerRtn.getItems().size(), expectedRtnSize);

        for (String offerGetId : offerId) {
            Offer offer = offerService.getOffer(offerGetId);
            Assert.assertTrue(isContain(offerRtn, offer));
        }
    }

}
