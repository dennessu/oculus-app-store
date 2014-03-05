/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo;

import com.junbo.catalog.common.util.Utils;
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
public class OfferDraftRepository {
    @Autowired
    private OfferDraftDao offerDraftDao;

    public Long create(Offer offer) {
        OfferDraftEntity entity = OfferConverter.toDraftEntity(offer);

        return offerDraftDao.create(entity);
    }

    public Offer get(Long offerId) {
        OfferDraftEntity entity = offerDraftDao.get(offerId);
        return OfferConverter.toModel(entity);
    }

    public List<Offer> getOffers(int start, int size) {
        List<OfferDraftEntity> entities = offerDraftDao.getOffers(start, size);
        List<Offer> result = new ArrayList<>();
        for (OfferDraftEntity entity : entities) {
            result.add(OfferConverter.toModel(entity));
        }

        return result;
    }

    public Long update(Offer offer) {
        OfferDraftEntity entity = offerDraftDao.get(offer.getId());
        // TODO: validations
        entity.setName(offer.getName());
        entity.setRevision(offer.getRevision());
        entity.setStatus(offer.getStatus());
        entity.setOwnerId(offer.getOwnerId());
        entity.setPayload(Utils.toJson(offer));

        return offerDraftDao.update(entity);
    }
}
