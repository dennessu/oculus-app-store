/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo;

import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OffersGetOptions;

import java.util.List;

/**
 * Offer repository.
 */
public interface OfferRepository extends BaseEntityRepository<Offer> {
    Offer create(Offer offer);
    Offer get(String offerId);
    List<Offer> getOffers(OffersGetOptions options);
    Offer update(Offer offer, Offer oldOffer);
    void delete(String offerId);
}
