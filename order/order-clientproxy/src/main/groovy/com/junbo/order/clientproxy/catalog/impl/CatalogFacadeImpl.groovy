/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.clientproxy.catalog.impl

import com.junbo.catalog.spec.model.common.EntityGetOptions
import com.junbo.catalog.spec.model.common.ResultList
import com.junbo.catalog.spec.model.offer.Offer
import com.junbo.catalog.spec.model.offer.OffersGetOptions
import com.junbo.catalog.spec.resource.OfferResource
import com.junbo.common.id.OfferId
import com.junbo.langur.core.promise.Promise
import com.junbo.order.clientproxy.catalog.CatalogFacade
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

import javax.annotation.Resource

/**
 * Catalog facade implementation.
 */
@Component('catalogFacade')
@CompileStatic
class CatalogFacadeImpl implements CatalogFacade {

    @Resource(name='offerClient')
    OfferResource offerResource

    void setOfferResource(OfferResource offerResource) {
        this.offerResource = offerResource
    }

    @Override
    Promise<Offer> getOffer(Long offerId) {
        return offerResource.getOffer(new OfferId(offerId), EntityGetOptions.default)
    }

    @Override
    Promise<ResultList<Offer>> getOffers(List<OfferId> offerIds) {
        OffersGetOptions options = new OffersGetOptions()
        options.offerIds = offerIds
        options.status = 'Released'
        return offerResource.getOffers(options)
    }


}
