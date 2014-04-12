/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo;

import com.junbo.catalog.db.dao.OfferDao;
import com.junbo.catalog.db.entity.OfferEntity;
import com.junbo.catalog.db.mapper.OfferMapper;
import com.junbo.catalog.spec.error.AppErrors;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OffersGetOptions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

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
        return OfferMapper.toModel(offerDao.get(offerId));
    }

    public List<Offer> getOffers(OffersGetOptions options) {
        List<OfferEntity> offerEntities = offerDao.getOffers(options);
        List<Offer> offers = new ArrayList<>();
        for (OfferEntity offerEntity : offerEntities) {
            Offer offer = OfferMapper.toModel(offerEntity);
            offers.add(offer);
        }

        return offers;
    }

    @Override
    public Long update(Offer offer) {
        OfferEntity dbEntity = offerDao.get(offer.getOfferId());
        if (dbEntity == null) {
            throw AppErrors.INSTANCE.notFound("offer", offer.getOfferId()).exception();
        }
        OfferMapper.fillDBEntity(offer, dbEntity);
        return offerDao.update(dbEntity);
    }

    @Override
    public void delete(Long offerId) {
        OfferEntity dbEntity = offerDao.get(offerId);
        if (dbEntity == null) {
            throw AppErrors.INSTANCE.notFound("offer", offerId).exception();
        }
        dbEntity.setDeleted(true);
        offerDao.update(dbEntity);
    }
}
