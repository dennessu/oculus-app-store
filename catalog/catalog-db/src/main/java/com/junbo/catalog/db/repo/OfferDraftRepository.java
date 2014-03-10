/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo;

import com.junbo.catalog.db.convertor.OfferConverter;
import com.junbo.catalog.db.dao.OfferDraftDao;
import com.junbo.catalog.db.entity.OfferDraftEntity;
import com.junbo.catalog.spec.model.offer.Offer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Offer draft repository.
 */
public class OfferDraftRepository implements EntityDraftRepository<Offer> {
    @Autowired
    private OfferDraftDao offerDraftDao;

    @Override
    public Long create(Offer offer) {
        return offerDraftDao.create(OfferConverter.toDraftEntity(offer));
    }

    @Override
    public Offer get(Long offerId) {
        return OfferConverter.toModel(offerDraftDao.get(offerId));
    }

    @Override
    public List<Offer> getEntities(int start, int size) {
        return getOffers(start, size);
    }

    public List<Offer> getOffers(int start, int size) {
        List<OfferDraftEntity> entities = offerDraftDao.getOffers(start, size);
        List<Offer> result = new ArrayList<>();
        for (OfferDraftEntity entity : entities) {
            result.add(OfferConverter.toModel(entity));
        }

        return result;
    }

    @Override
    public Long update(Offer offer) {
        if (offer == null) {
            return null;
        }

        OfferDraftEntity entity = offerDraftDao.get(offer.getId());
        OfferConverter.fillDraftEntity(offer, entity);

        return offerDraftDao.update(entity);
    }
}
