/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.clientproxy.catalog;

import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.common.id.OfferId;
import com.junbo.langur.core.promise.Promise;

import java.util.List;

/**
 * Interface for catalog facade.
 */
public interface CatalogFacade {

    Promise<Offer> getOffer(Long offerId);

    Promise<List<Offer>> getOffers(List<OfferId> offerIds);
}
