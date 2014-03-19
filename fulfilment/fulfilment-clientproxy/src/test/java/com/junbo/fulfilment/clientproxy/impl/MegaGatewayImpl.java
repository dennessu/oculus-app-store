/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.clientproxy.impl;

import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.resource.OfferResource;
import com.junbo.common.id.OfferId;
import com.junbo.fulfilment.clientproxy.MegaGateway;
import com.junbo.fulfilment.common.exception.CatalogGatewayException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * MegaGatewayImpl.
 */
public class MegaGatewayImpl implements MegaGateway {
    @Autowired
    private OfferResource offerResource;

    @Override
    public Long createOffer(Offer offer) {
        try {
            return offerResource.create(offer).wrapped().get().getId();
        } catch (Exception e) {
            throw new CatalogGatewayException("Error occurred during calling [Catalog] component service.", e);
        }
    }

    @Override
    public Long updateOffer(Offer offer) {
        try {
            return offerResource.update(new OfferId(offer.getId()), offer).wrapped().get().getId();
        } catch (Exception e) {
            throw new CatalogGatewayException("Error occurred during calling [Catalog] component service.", e);
        }
    }
}
