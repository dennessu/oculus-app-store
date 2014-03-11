/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo;

import com.junbo.catalog.db.convertor.OfferConverter;
import com.junbo.catalog.db.dao.OfferDao;
import com.junbo.catalog.spec.model.offer.Offer;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Offer repository.
 */
public class OfferRepository implements EntityRepository<Offer> {
    @Autowired
    private OfferDao offerDao;

    @Override
    public Long create(Offer offer) {
        return offerDao.create(OfferConverter.toEntity(offer));
    }

    @Override
    public Offer get(Long offerId, Long timestamp) {
        return OfferConverter.toModel(offerDao.getOffer(offerId, timestamp));
    }
}
