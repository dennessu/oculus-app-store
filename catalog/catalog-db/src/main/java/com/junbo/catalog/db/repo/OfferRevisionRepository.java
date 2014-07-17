/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo;

import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.catalog.spec.model.offer.OfferRevisionsGetOptions;

import java.util.Collection;
import java.util.List;

/**
 * Offer revision repository.
 */
public interface OfferRevisionRepository extends BaseRevisionRepository<OfferRevision> {
    OfferRevision create(OfferRevision offerRevision);
    OfferRevision get(String revisionId);
    List<OfferRevision> getRevisions(OfferRevisionsGetOptions options);
    List<OfferRevision> getRevisions(Collection<String> offerIds, Long timestamp);
    List<OfferRevision> getRevisions(String itemId);
    List<OfferRevision> getRevisionsBySubOfferId(String offerId);
    OfferRevision update(OfferRevision revision, OfferRevision oldRevision);
    void delete(String revisionId);
}
