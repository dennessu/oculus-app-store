/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo;

import com.junbo.catalog.db.convertor.OfferConverter;
import com.junbo.catalog.db.dao.OfferDao;
import com.junbo.catalog.db.entity.OfferEntity;
import com.junbo.catalog.spec.model.offer.Offer;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Offer repository.
 */
public class OfferRepository {
    @Autowired
    private OfferDao offerDao;

    public Long create(Offer offer) {
        OfferEntity entity = OfferConverter.toEntity(offer);

        return offerDao.create(entity);
    }

    public Offer get(Long offerId, Long timestamp) {
        OfferEntity entity = offerDao.getOffer(offerId, timestamp);
        return OfferConverter.toModel(entity);
    }
}
