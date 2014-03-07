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

import java.util.List;

/**
 * Offer resource implementation.
 */
public class OfferResourceImpl implements OfferResource{
    @Autowired
    private OfferService offerService;

    @Override
    public Promise<ResultList<Offer>> getOffers(EntitiesGetOptions options) {
        List<Offer> offers = offerService.getEntities(options);
        ResultList<Offer> resultList = new ResultList<>();
        resultList.setResults(offers);
        resultList.setHref("href TODO");
        resultList.setNext("next TODO");
        return Promise.pure(resultList);
    }

    @Override
    public Promise<Offer> getOffer(Long offerId, EntityGetOptions options) {
        Offer offer = offerService.get(offerId, options);
        return Promise.pure(offer);
    }

    @Override
    public Promise<Offer> createOffer(Offer offer) {
        Offer result = offerService.create(offer);
        return Promise.pure(result);
    }

    @Override
    public Promise<Offer> createReview(Long offerId) {
        Offer offer = offerService.review(offerId);
        return Promise.pure(offer);
    }

    @Override
    public Promise<Offer> releaseOffer(Long offerId) {
        Offer offer = offerService.release(offerId);
        return Promise.pure(offer);
    }

    @Override
    public Promise<Offer> rejectOffer(Long offerId) {
        Offer updatedOffer = offerService.reject(offerId);
        return Promise.pure(updatedOffer);
    }

    @Override
    public Promise<Offer> updateOffer(Offer offer) {
        Offer updatedOffer = offerService.update(offer);
        return Promise.pure(updatedOffer);
    }

    @Override
    public Promise<Long> removeOffer(Long offerId) {
        Long removedOfferId = offerService.remove(offerId);
        return Promise.pure(removedOfferId);
    }

    @Override
    public Promise<Long> deleteOffer(Long offerId) {
        Long deletedOfferId = offerService.delete(offerId);
        return Promise.pure(deletedOfferId);
    }
}
