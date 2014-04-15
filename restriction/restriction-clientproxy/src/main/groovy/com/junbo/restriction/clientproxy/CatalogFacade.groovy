/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.restriction.clientproxy

import com.junbo.catalog.spec.model.offer.Offer
import com.junbo.langur.core.promise.Promise
/**
 * Interface of Catalog Facade.
 */
interface CatalogFacade {
    Promise<List<Offer>> getOffers(List<Long> offerIds)

}