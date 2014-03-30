/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo;

import com.junbo.catalog.db.dao.OfferDao;
import com.junbo.catalog.db.entity.OfferEntity;
import com.junbo.catalog.db.mapper.OfferMapper;
import com.junbo.catalog.spec.model.common.Status;
import com.junbo.catalog.spec.model.offer.Offer;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Offer repository.
 */
public class OfferRepository implements BaseEntityRepository<Offer> {
    @Autowired
    private OfferDao offerDao;

    public Long create(Offer offer) {
        return offerDao.create(OfferMapper.toDBEntity(offer));
    }

    public Offer get(Long offerId) {
        OfferEntity offerEntity = offerDao.get(offerId);
        if (Status.DELETED.equalsIgnoreCase(offerEntity.getStatus())) {
            return null;
        }
        return OfferMapper.toModel(offerEntity);
    }

    @Override
    public Long update(Offer offer) {
        OfferEntity dbEntity = offerDao.get(offer.getOfferId());
        OfferMapper.fillDBEntity(offer, dbEntity);
        return offerDao.update(dbEntity);
    }
}
