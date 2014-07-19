/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.data.handler.catalog

import com.junbo.catalog.spec.model.offer.Offer
import com.junbo.catalog.spec.resource.OfferResource
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by jasonfu on 14-7-17.
 */
@CompileStatic
class OfferDataHandler extends BaseCatalogDataHandler {
    private OfferResource offerResource

    @Required
    void setOfferResource(OfferResource offerResource) {
        this.offerResource = offerResource
    }

    String handle(Offer offer) {
        logger.debug('Create new offer with this content')
        try {
            Offer result = offerResource.create(offer).get()
            return result.id
        } catch (Exception e) {
            logger.error("Error creating offer $offer.offerId.", e)
            return null
        }

    }
}
