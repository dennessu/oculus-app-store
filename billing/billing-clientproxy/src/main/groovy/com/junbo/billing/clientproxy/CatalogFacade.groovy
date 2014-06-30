/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy

import com.junbo.catalog.spec.model.offer.OfferRevision
import com.junbo.langur.core.promise.Promise

/**
 * Interface of catalog facade.
 */
public interface CatalogFacade {
    Promise<OfferRevision> getOfferRevision(String offerId)
}