/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.clientproxy;

import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.entitlement.spec.model.Entitlement;

/**
 * MegaGateway.
 */
public interface MegaGateway {
    String createOffer(Offer offer);

    String createItem(Item item);

    String createOfferRevision(OfferRevision offerRevision);

    String createItemRevision(ItemRevision itemRevision);

    OfferRevision getOfferRevision(String offerRevisionId);

    ItemRevision getItemRevision(String itemRevisionId);

    String updateOfferRevision(OfferRevision offerRevision);

    String updateItemRevision(ItemRevision itemRevision);

    Entitlement getEntitlement(Long entitlementId);
}
