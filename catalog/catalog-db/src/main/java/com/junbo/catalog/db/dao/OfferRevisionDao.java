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
    OfferRevisionEntity getRevision(Long offerId, Long timestamp);
    List<OfferRevisionEntity> getRevisionsBySubOfferId(Long offerId);
    List<OfferRevisionEntity> getRevisionsByItemId(Long itemId);
}
