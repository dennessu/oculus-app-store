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

    public Offer get(Long offerId, Integer revision) {
        OfferEntity entity = offerDao.getOffer(offerId, revision);
        return OfferConverter.toModel(entity);
    }

    public Offer get(Long offerId) {
        OfferEntity entity = offerDao.getOffer(offerId);
        return OfferConverter.toModel(entity);
    }

  /*  public Long update(Offer offer) {
        OfferEntity entity = offerDao.get(offer.getId());
        // TODO: validations
        entity.setName(offer.getName());
        entity.setRevision(offer.getRevision());
        entity.setStatus(offer.getStatus());
        entity.setOwnerId(offer.getOwnerId());
        entity.setPayload(Utils.toJson(offer));

        return offerDao.update(entity);
    }*/

}
