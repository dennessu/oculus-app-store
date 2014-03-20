/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.entitlement.clientproxy.catalog.impl;

import com.junbo.catalog.spec.model.common.EntityGetOptions;
import com.junbo.catalog.spec.resource.proxy.OfferResourceClientProxy;
import com.junbo.common.id.OfferId;
import com.junbo.entitlement.clientproxy.catalog.CatalogFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Impl of CatalogFacade.
 */
public class CatalogFacadeImpl implements CatalogFacade {
    private static final Logger LOGGER = LoggerFactory.getLogger(CatalogFacade.class);
    @Autowired
    @Qualifier("catalogOfferClient")
    private OfferResourceClientProxy catalogOfferProxy;

    @Override
    public boolean exists(Long offerId) {
        try {
            LOGGER.info("Getting offer [{}] started.", offerId);
            catalogOfferProxy.getOffer(new OfferId(offerId), EntityGetOptions.getDefault()).wrapped().get();
            LOGGER.info("Getting offer [{}] finished.", offerId);
        } catch (Exception e) {
            LOGGER.error("Getting offer [{" + offerId + "}] failed.", e);
            return false;
        }
        return true;
    }
}
