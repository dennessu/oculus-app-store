/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo;

import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.catalog.spec.model.offer.OfferRevisionsGetOptions;
import com.junbo.common.id.OfferId;

import java.util.Collection;
import java.util.List;

/**
 * Offer revision repository.
 */
public interface OfferRevisionRepository extends BaseRevisionRepository<OfferRevision> {
    Long create(OfferRevision offerRevision);
    OfferRevision get(Long revisionId);
    List<OfferRevision> getRevisions(OfferRevisionsGetOptions options);
    List<OfferRevision> getRevisions(Collection<OfferId> offerIds, Long timestamp);
    Long update(OfferRevision revision);
    void delete(Long revisionId);
}
