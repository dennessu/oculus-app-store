/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.service;

import com.junbo.catalog.common.exception.NotFoundException;
import com.junbo.catalog.common.util.Constants;
import com.junbo.catalog.core.OfferService;
import com.junbo.catalog.db.repo.OfferDraftRepository;
import com.junbo.catalog.db.repo.OfferRepository;
import com.junbo.catalog.spec.model.common.EntityGetOptions;
import com.junbo.catalog.spec.model.common.Status;
import com.junbo.catalog.spec.model.offer.Offer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Offer service implementation.
 */
public class OfferServiceImpl implements OfferService {
    @Autowired
    private OfferDraftRepository offerDraftRepository;
    @Autowired
    private OfferRepository offerRepository;

    @Override
    public Offer getOffer(Long offerId, EntityGetOptions options) {
        Offer offer;
        if (options.getRevision() == null) {
            offer = offerRepository.get(offerId);
        } else {
            offer = offerRepository.get(offerId, options.getRevision());
        }

        checkOfferNotNull(offerId, offer);

        return offer;
    }

   /* @Override
    public Offer getDraftOffer(Long offerId) {
        Offer offer = offerDraftRepository.get(offerId);
        checkOfferNotNull(offerId, offer);

        return offer;
    }*/

    @Override
    public List<Offer> getOffers(int start, int size) {
        return offerDraftRepository.getOffers(start, size);
    }

    @Override
    public Offer createOffer(Offer offer) {
        // TODO: validations

        offer.setRevision(Constants.INITIAL_CREATION_REVISION);
        offer.setStatus(Status.DRAFT);

        Long offerId = offerDraftRepository.create(offer);
        //offer.setId(offerId);
        //offerRepository.create(offer);

        return offerDraftRepository.get(offerId);
    }

    @Override
    public Offer updateOffer(Offer offer) {
        // TODO: validations
        offerDraftRepository.update(offer);
        return offerDraftRepository.get(offer.getId());
    }

    @Override
    public Offer reviewOffer(Long offerId) {
        Offer offer = offerDraftRepository.get(offerId);
        checkOfferNotNull(offerId, offer);
        offer.setStatus(Status.PENDING_REVIEW);
        offerDraftRepository.update(offer);
        return offerRepository.get(offerId);
    }

    @Override
    public Offer publishOffer(Long offerId) {
        Offer offer = offerDraftRepository.get(offerId);
        checkOfferNotNull(offerId, offer);
        offer.setStatus(Status.ACTIVE);
        offerRepository.create(offer);
        offerDraftRepository.update(offer);
        return offerRepository.get(offerId);
    }

    @Override
    public Long removeOffer(Long offerId) {
        /*Offer offer = offerRepository.get(offerId);
        checkOfferNotNull(offerId, offer);
        offer.setStatus(Status.REMOVED);
        offerRepository.create(offer);
        return offer.getId();*/
        //TODO
        return null;
    }

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
