/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.service;

import com.junbo.catalog.common.exception.CatalogException;
import com.junbo.catalog.common.exception.NotFoundException;
import com.junbo.catalog.common.util.Constants;
import com.junbo.catalog.core.OfferService;
import com.junbo.catalog.db.repo.OfferDraftRepository;
import com.junbo.catalog.db.repo.OfferRepository;
import com.junbo.catalog.spec.model.common.EntitiesGetOptions;
import com.junbo.catalog.spec.model.common.EntityGetOptions;
import com.junbo.catalog.spec.model.common.Status;
import com.junbo.catalog.spec.model.offer.Offer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Offer service implementation.
 */
public class OfferServiceImpl extends BaseServiceImpl<Offer> implements OfferService {
    @Autowired
    private OfferDraftRepository offerDraftRepository;
    @Autowired
    private OfferRepository offerRepository;

    @Override
    public Offer getOffer(Long offerId, EntityGetOptions options) {
        Offer offer;
        if (options.getStatus() != null && !Status.RELEASED.equalsIgnoreCase(options.getStatus())) {
            offer = offerDraftRepository.get(offerId);
            if (!options.getStatus().equalsIgnoreCase(offer.getStatus())) {
                throw new NotFoundException("offer", offerId);
            }
        } else {
            offer = offerRepository.get(offerId, options.getTimestamp());
        }

        checkOfferNotNull(offerId, offer);

        return offer;
    }

    @Override
    public List<Offer> getOffers(EntitiesGetOptions options) {
        return getEntities(options, offerRepository, offerDraftRepository);
    }

    @Override
    public Offer createOffer(Offer offer) {
        if (offer == null) {
            throw new CatalogException("TODO");
        }

        offer.setRevision(Constants.INITIAL_CREATION_REVISION);
        offer.setStatus(Status.DESIGN);

        Long offerId = offerDraftRepository.create(offer);

        return offerDraftRepository.get(offerId);
    }

    @Override
    public Offer updateOffer(Offer offer) {
        if (offer == null) {
            throw new CatalogException("TODO");
        }

        offerDraftRepository.update(offer);
        return offerDraftRepository.get(offer.getId());
    }

    @Override
    public Offer reviewOffer(Long offerId) {
        Offer offer = offerDraftRepository.get(offerId);
        checkOfferNotNull(offerId, offer);
        offer.setStatus(Status.PENDING_REVIEW);
        offerDraftRepository.update(offer);
        return offerRepository.get(offerId, null);
    }

    @Override
    public Offer releaseOffer(Long offerId) {
        Offer offer = offerDraftRepository.get(offerId);
        checkOfferNotNull(offerId, offer);
        offer.setStatus(Status.RELEASED);
        offerRepository.create(offer);
        offerDraftRepository.update(offer);
        return offerRepository.get(offerId, null);
    }

    @Override
    public Offer rejectOffer(Long offerId) {
        Offer offer = offerDraftRepository.get(offerId);
        checkOfferNotNull(offerId, offer);
        offer.setStatus(Status.REJECTED);
        offerDraftRepository.update(offer);
        return offerRepository.get(offerId, null);
    }

    /**
     * Remove offer from listing. The draft offer is still kept.
     *
     * @param offerId the id of offer to be removed.
     * @return the id of removed offer.
     */
    @Override
    public Long removeOffer(Long offerId) {
        Offer offer = offerRepository.get(offerId, null);
        checkOfferNotNull(offerId, offer);
        offer.setStatus(Status.DELETED);
        offerRepository.create(offer);
        return offer.getId();
    }

    /**
     * Delete both draft and published offer.
     *
     * @param offerId the id of offer to be deleted.
     * @return the id of deleted offer.
     */
    @Override
    public Long deleteOffer(Long offerId) {
        Offer offer = offerDraftRepository.get(offerId);
        checkOfferNotNull(offerId, offer);
        offer.setStatus(Status.DELETED);
        offerRepository.create(offer);
        offerDraftRepository.update(offer);
        return offer.getId();
    }

    private void checkOfferNotNull(Long offerId, Offer offer) {
        if (offer == null) {
            throw new NotFoundException("offer", offerId);
        }
    }
}
