/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.entitlement.clientproxy.catalog.impl;

import com.junbo.catalog.spec.model.common.EntityGetOptions;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.resource.proxy.OfferResourceClientProxy;
import com.junbo.entitlement.clientproxy.catalog.CatalogFacade;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ExecutionException;

/**
 * Impl of CatalogFacade.
 */
public class CatalogFacadeImpl implements CatalogFacade {
    @Autowired
    private OfferResourceClientProxy catalogOfferProxy;

    @Override
    public boolean exists(Long offerId) {
        Offer offer = null;
        try {
            offer = catalogOfferProxy.getOffer(offerId, EntityGetOptions.getDefault()).wrapped().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (offer == null) {
            return false;
        }
        return true;
    }
}
