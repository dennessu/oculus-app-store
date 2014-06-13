/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.clientproxy;


import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OfferRevision;

/**
 * CatalogGateway.
 */
public interface CatalogGateway {
    Offer getOffer(String offerId, Long timestamp);

    Offer getOffer(String offerId);

    OfferRevision getOfferRev(String offerRevId);

    Item getItem(String itemID);

}
