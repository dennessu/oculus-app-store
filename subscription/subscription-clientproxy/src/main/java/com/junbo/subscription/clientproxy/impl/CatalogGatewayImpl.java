/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.clientproxy.impl;

import com.junbo.catalog.spec.model.common.EntityGetOptions;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.resource.OfferResource;
import com.junbo.common.id.OfferId;
import com.junbo.subscription.clientproxy.CatalogGateway;

import com.junbo.subscription.common.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * CatalogGatewayImpl.
 */
public class CatalogGatewayImpl implements CatalogGateway {
    private static final Long OFFER_TIMESTAMP_NOT_SPECIFIED = -1L;

    @Autowired
    private OfferResource offerResource;

    @Override
    public Offer getOffer(Long offerId, Long timestamp) {
        return retrieve(offerId, timestamp);
    }

    @Override
    public Offer getOffer(Long offerId) {
        return retrieve(offerId, OFFER_TIMESTAMP_NOT_SPECIFIED);
    }


    protected Offer retrieve(Long offerId, Long timestamp) {
        try {
            // TODO
            com.junbo.catalog.spec.model.offer.Offer offer =
                    offerResource.getOffer(new OfferId(offerId), EntityGetOptions.getDefault()).wrapped().get();

            if (offer == null) {
                throw new ResourceNotFoundException(
                        "Offer [" + offerId + "] with timestamp [" + timestamp + "] does not exist");
            }

            return offer;
        } catch (Exception e) {
            throw new ResourceNotFoundException("Error occurred during calling [Catalog] component service.", e);
        }
    }


}
