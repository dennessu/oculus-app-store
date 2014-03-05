/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.offer;

import com.junbo.catalog.core.BaseTest;
import com.junbo.catalog.core.OfferService;
import com.junbo.catalog.spec.model.offer.Offer;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

public class OfferServiceTest extends BaseTest {
    @Autowired
    private OfferService offerService;

    @Test
    public void testCreate() {
        Offer offer = buildOffer();
        Offer result = offerService.createOffer(offer);
        Assert.assertNotNull(result.getId(), "Entity id should not be null.");
    }

    private Offer buildOffer() {
        Offer offer = new Offer();
        offer.setName("testName");
        offer.setStatus("testStatus");
        offer.setRevision(1);
        offer.setOwnerId(generateId());
        return offer;
    }
}
