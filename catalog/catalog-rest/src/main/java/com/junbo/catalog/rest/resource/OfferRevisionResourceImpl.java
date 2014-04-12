/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.rest.resource;

import com.junbo.catalog.core.OfferService;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.catalog.spec.model.offer.OfferRevisionsGetOptions;
import com.junbo.catalog.spec.resource.OfferRevisionResource;
import com.junbo.common.id.OfferRevisionId;
import com.junbo.common.model.Results;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Offer revision resource implementation.
 */
public class OfferRevisionResourceImpl implements OfferRevisionResource {
    @Autowired
    private OfferService offerService;

    @Override
    public Promise<Results<OfferRevision>> getOfferRevisions(OfferRevisionsGetOptions options) {
        List<OfferRevision> revisions = offerService.getRevisions(options);
        Results<OfferRevision> results = new Results<>();
        results.setItems(revisions);
        return Promise.pure(results);
    }

    @Override
    public Promise<OfferRevision> getOfferRevision(OfferRevisionId revisionId) {
        return Promise.pure(offerService.getRevision(revisionId.getValue()));
    }

    @Override
    public Promise<OfferRevision> createOfferRevision(OfferRevision offerRevision) {
        return Promise.pure(offerService.createRevision(offerRevision));
    }

    @Override
    public Promise<OfferRevision> updateOfferRevision(OfferRevisionId revisionId, OfferRevision offerRevision) {
        return Promise.pure(offerService.updateRevision(revisionId.getValue(), offerRevision));
    }
}
