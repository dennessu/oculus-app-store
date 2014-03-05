/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo;

import com.junbo.catalog.db.BaseTest;
import com.junbo.catalog.spec.model.offer.Offer;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by baojing on 2/18/14.
 */
public class OfferDraftRepositoryTest extends BaseTest {
    @Autowired
    private OfferDraftRepository offerDraftRepository;

    @Test
    public void testCreate() {
        Offer offer = buildOffer();
        Long offerId = offerDraftRepository.create(offer);
        Assert.assertNotNull(offerId, "Entity id should not be null.");
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
