/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.restriction.clientproxy.impl

import com.junbo.restriction.spec.error.AppErrors
import com.junbo.catalog.spec.model.offer.OffersGetOptions
import com.junbo.common.id.OfferId
import com.junbo.common.model.Results
import com.junbo.langur.core.promise.Promise
import com.junbo.restriction.clientproxy.CatalogFacade
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.annotation.Resource
import com.junbo.catalog.spec.resource.OfferResource
import com.junbo.catalog.spec.model.offer.Offer
/**
 * Impl of Catalog Facade.
 */
@CompileStatic
class CatalogFacadeImpl implements CatalogFacade {

    private final static Logger LOGGER = LoggerFactory.getLogger(CatalogFacadeImpl)

    @Resource(name = 'restrictionOfferClient')
    private OfferResource offerResource

    void setOfferResource(OfferResource offerResource) {
        this.offerResource = offerResource
    }

    Promise<List<Offer>> getOffers(List<OfferId> offerIds) {
        OffersGetOptions options = new OffersGetOptions()
        options.setSize(Integer.MAX_VALUE)
        options.setOfferIds(new HashSet(offerIds))
        return offerResource.getOffers(options).then { Results<Offer> offers ->
            if (offers == null || offers?.items == null) {
                LOGGER.error('Offer not found')
                throw AppErrors.INSTANCE.offerNotFound().exception()
            }
            return offers.items
        }
    }
}
