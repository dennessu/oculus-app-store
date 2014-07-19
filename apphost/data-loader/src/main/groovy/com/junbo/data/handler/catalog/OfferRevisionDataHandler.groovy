/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.data.handler.catalog

import com.junbo.catalog.spec.model.offer.OfferRevision
import com.junbo.catalog.spec.resource.OfferRevisionResource
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by jasonfu on 14-7-17.
 */
@CompileStatic
class OfferRevisionDataHandler extends BaseCatalogDataHandler {
    private OfferRevisionResource offerRevisionResource

    @Required
    void setOfferRevisionResource(OfferRevisionResource offerRevisionResource) {
        this.offerRevisionResource = offerRevisionResource
    }

    void handle(OfferRevision offerRevision) {
        logger.debug('Create new offer revision with this content')
        try {
            OfferRevision offerRevisionCreated = offerRevisionResource.createOfferRevision(offerRevision).get()

            logger.debug('put the offer revision to APPROVED')
            offerRevisionCreated.setStatus("APPROVED")
            offerRevisionResource.updateOfferRevision(offerRevisionCreated.revisionId, offerRevisionCreated).get()
        } catch (Exception e) {
            logger.error("Error creating offer revision $offerRevision.revisionId.", e)
        }
    }
}
