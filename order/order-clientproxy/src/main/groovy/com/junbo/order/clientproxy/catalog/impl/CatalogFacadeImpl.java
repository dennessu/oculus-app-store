/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.clientproxy.catalog.impl;

import com.junbo.catalog.spec.model.common.EntityGetOptions;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.resource.OfferResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.order.clientproxy.catalog.CatalogFacade;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Catalog facade implementation.
 */
@Component("catalogFacade")
public class CatalogFacadeImpl implements CatalogFacade {

    @Resource(name="offerClient")
    private OfferResource offerResource;

    public void setOfferResource(OfferResource offerResource) {
        this.offerResource = offerResource;
    }

    @Override
    public Promise<Offer> getOffer(Long offerId) {
        Promise<Offer> offerPromise = null;

        try {
            offerPromise = offerResource.getOffer(offerId, EntityGetOptions.getDefault());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return offerPromise;
    }

}
