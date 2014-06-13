/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao;

import com.junbo.catalog.db.entity.OfferRevisionEntity;
import com.junbo.catalog.spec.model.offer.OfferRevisionsGetOptions;

import java.util.List;

/**
 * Offer DAO definition.
 */
public interface OfferRevisionDao extends BaseDao<OfferRevisionEntity> {
    List<OfferRevisionEntity> getRevisions(OfferRevisionsGetOptions options);
    OfferRevisionEntity getRevision(String offerId, Long timestamp);
    List<OfferRevisionEntity> getRevisionsBySubOfferId(String offerId);
    List<OfferRevisionEntity> getRevisionsByItemId(String itemId);
}
