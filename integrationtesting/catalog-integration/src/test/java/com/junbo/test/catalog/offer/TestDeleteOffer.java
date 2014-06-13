/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.offer;

import com.junbo.test.catalog.impl.OfferServiceImpl;
import com.junbo.test.catalog.util.BaseTestClass;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.catalog.OfferService;
import com.junbo.test.common.property.*;

import org.testng.annotations.Test;
import org.testng.Assert;

/**
 * @author Jason
 * Time: 4/21/2014
 * For testing catalog delet offer(s) API
 */
public class TestDeleteOffer extends BaseTestClass {

    private LogHelper logger = new LogHelper(TestDeleteOffer.class);

    @Property(
            priority = Priority.Dailies,
            features = "Delete v1/offers/{offerId}",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test delete an offer by offerId",
            steps = {
                    "1. Prepare an offer",
                    "2. delete it and verify can't search it"
            }
    )
    @Test
    public void testDeleteOffer() throws Exception {
        OfferService offerService = OfferServiceImpl.instance();

        //Prepare an offer
        Offer offer = offerService.postDefaultOffer();
        String invalidId = "0L";

        offerService.deleteOffer(offer.getOfferId());

        //Try to get the offer, expected status code is 404.
        try {
            offerService.getOffer(offer.getOfferId(), 404);
            Assert.fail("Couldn't find the deleted offer");
        }
        catch (Exception ex)
        {
        }

        //Try to delete the offer again, expected status code is 404.
        offerService.deleteOffer(offer.getOfferId(), 404);

        //delete non-existing offer
        offer = offerService.postDefaultOffer();
        offerService.deleteOffer(invalidId, 404);
        offer = offerService.getOffer(offer.getOfferId());
        Assert.assertNotNull(offer);
    }

}
