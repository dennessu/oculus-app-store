/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.core.mock;

import com.junbo.billing.clientproxy.CatalogFacade;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.common.id.OrganizationId;
import com.junbo.langur.core.promise.Promise;

/**
 * Created by linyi on 6/30/14.
 */
public class MockCatalogFacade implements CatalogFacade {
    @Override
    public Promise<OfferRevision> getOfferRevision(String offerId) {
        OfferRevision offerRevision = new OfferRevision();
        offerRevision.setOwnerId(new OrganizationId(123));
        return Promise.pure(offerRevision);
    }
}
