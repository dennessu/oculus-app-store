/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.rest.resource;

import com.junbo.catalog.core.OfferService;
import com.junbo.catalog.spec.model.common.EntitiesGetOptions;
import com.junbo.catalog.spec.model.common.EntityGetOptions;
import com.junbo.catalog.spec.model.common.ResultList;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.resource.OfferResource;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Valid;
import java.util.List;

/**
 * Offer resource implementation.
 */
public class OfferResourceImpl implements OfferResource{
    @Autowired
    private OfferService offerService;

    @Override
    public Promise<ResultList<Offer>> getOffers(EntitiesGetOptions options) {
        List<Offer> offers = offerService.getOffers(options);
        ResultList<Offer> resultList = new ResultList<>();
        resultList.setResults(offers);
        resultList.setHref("href TODO");
        resultList.setNext("next TODO");
        return Promise.pure(resultList);
    }

    @Override
    public Promise<Offer> getOffer(Long offerId, EntityGetOptions options) {
        Offer offer = offerService.getOffer(offerId, options);
        return Promise.pure(offer);
    }

    @Override
    public Promise<Offer> createOffer(Offer offer) {
        Offer result = offerService.createOffer(offer);
        return Promise.pure(result);
    }

    @Override
    public Promise<Offer> createReview(Long offerId) {
        Offer offer = offerService.reviewOffer(offerId);
        return Promise.pure(offer);
    }

    @Override
    public Promise<Offer> releaseOffer(Long offerId) {
        Offer offer = offerService.releaseOffer(offerId);
        return Promise.pure(offer);
    }

    @Override
    public Promise<Offer> rejectOffer(Long offerId) {
        Offer updatedOffer = offerService.rejectOffer(offerId);
        return Promise.pure(updatedOffer);
    }

    @Override
    public Promise<Offer> updateOffer(@Valid Offer offer) {
        Offer updatedOffer = offerService.updateOffer(offer);
        return Promise.pure(updatedOffer);
    }

    @Override
    public Promise<Long> removeOffer(Long offerId) {
        Long removedOfferId = offerService.removeOffer(offerId);
        return Promise.pure(removedOfferId);
    }

    @Override
    public Promise<Long> deleteOffer(Long offerId) {
        Long deletedOfferId = offerService.deleteOffer(offerId);
        return Promise.pure(deletedOfferId);
    }
}
