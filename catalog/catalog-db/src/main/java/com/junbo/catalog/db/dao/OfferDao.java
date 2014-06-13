/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao;

import com.junbo.catalog.db.entity.OfferEntity;
import com.junbo.catalog.spec.model.offer.OffersGetOptions;

import java.util.Collection;
import java.util.List;

/**
 * Offer DAO definition.
 */
public interface OfferDao extends BaseDao<OfferEntity> {
    List<OfferEntity> getOffers(OffersGetOptions options);
    List<OfferEntity> getOffers(Collection<String> offerIds);
}
