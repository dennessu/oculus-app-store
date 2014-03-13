/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.rest.resource;

import com.junbo.catalog.core.OfferService;
import com.junbo.catalog.spec.model.common.EntityGetOptions;
import com.junbo.catalog.spec.model.common.ResultList;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OffersGetOptions;
import com.junbo.catalog.spec.resource.OfferResource;
import com.junbo.common.id.OfferId;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Offer resource implementation.
 */
public class OfferResourceImpl extends BaseResourceImpl<Offer> implements OfferResource {
    @Autowired
    private OfferService offerService;

    @Override
    public Promise<ResultList<Offer>> getOffers(OffersGetOptions options) {
        return getEntities(options);
    }

    @Override
    public Promise<Offer> getOffer(OfferId offerId, EntityGetOptions options) {
        return get(offerId, options);
    }

    @Override
    public Promise<Offer> update(OfferId offerId, Offer offer) {
        return super.update(offerId, offer);
    }

    @Override
    protected OfferService getEntityService() {
        return offerService;
    }
}
