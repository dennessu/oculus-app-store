/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.restriction.clientproxy.mock

import com.junbo.catalog.spec.model.offer.Offer
import com.junbo.common.id.OfferId
import com.junbo.langur.core.promise.Promise
import com.junbo.restriction.clientproxy.CatalogFacade

/**
 * Created by Wei on 4/20/14.
 */
class MockCatalogFacadeImpl implements CatalogFacade {
    Promise<List<Offer>> getOffers(List<OfferId> offerIds) {
        Offer offer = new Offer()
        return Promise.pure([offer] as List)
    }
}
