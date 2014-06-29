/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl

import com.junbo.billing.clientproxy.CatalogFacade
import com.junbo.catalog.spec.model.offer.OfferRevision
import com.junbo.catalog.spec.resource.OfferRevisionResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import javax.annotation.Resource

/**
 * Catalog facade implementation.
 */
@CompileStatic
class CatalogFacadeImpl implements CatalogFacade {
    @Resource(name = 'billingOfferRevisionClient')
    OfferRevisionResource offerRevisionResource

    @Override
    Promise<OfferRevision> getOfferRevision(String offerId) {
        return offerRevisionResource.getOfferRevision(offerId)
    }
}
