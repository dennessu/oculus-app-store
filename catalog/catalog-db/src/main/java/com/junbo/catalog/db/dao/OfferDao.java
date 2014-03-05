/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao;

import com.junbo.catalog.db.entity.OfferEntity;

import java.util.List;

/**
 * Offer DAO definition.
 */
public interface OfferDao extends BaseDao<OfferEntity>  {
    OfferEntity getOffer(long offerId);
    OfferEntity getOffer(long offerId, int revision);
    List<Integer> getRevisions(long offerId);
}
