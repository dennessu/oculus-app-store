/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.catalog.rest;

import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.catalog.spec.model.offer.OfferRevisionsGetOptions;
import com.junbo.catalog.spec.resource.OfferResource;
import com.junbo.catalog.spec.resource.OfferRevisionResource;
import com.junbo.common.model.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.annotations.Test;

/**
 * CatalogGatewayTest.
 */
public class CatalogGatewayTest extends BaseTest {
    @Autowired
    @Qualifier("offerClient")
    private OfferResource offerResource;
    @Autowired
    @Qualifier("offerRevisionClient")
    private OfferRevisionResource offerRevisionResource;

    @Test(enabled = false)
    public void testCreate() throws Exception {
        OfferRevisionsGetOptions options = new OfferRevisionsGetOptions();
        //options.setOfferIds(Arrays.asList(new OfferId(503332864L)));
        //options.setTimestamp(Utils.currentTimestamp());
        Results<OfferRevision> revisionResults = offerRevisionResource.getOfferRevisions(options).syncGet();
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println(revisionResults.getItems().size());
        for (OfferRevision revision : revisionResults.getItems()) {
            System.out.println(revision.getOfferId());
            System.out.println(revision.getRevisionId());
        }
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
    }
}
