/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.rest.resource;

import com.junbo.catalog.core.OfferService;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OffersGetOptions;
import com.junbo.catalog.spec.resource.OfferResource;
import com.junbo.common.id.OfferId;
import com.junbo.common.model.Results;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.BeanParam;

/**
 * Offer resource implementation.
 */
public class OfferResourceImpl implements OfferResource {
    @Autowired
    private OfferService offerService;

    @Override
    public Promise<Results<Offer>> getOffers(@BeanParam OffersGetOptions options) {
        return null;
    }

    @Override
    public Promise<Offer> getOffer(OfferId offerId) {
        return Promise.pure(offerService.getEntity(offerId.getValue()));
    }

    @Override
    public Promise<Offer> create(Offer offer) {
        return Promise.pure(offerService.createEntity(offer));
    }

    @Override
    public Promise<Offer> update(OfferId offerId, Offer offer) {
        return Promise.pure(offerService.updateEntity(offerId.getValue(), offer));
    }
}
