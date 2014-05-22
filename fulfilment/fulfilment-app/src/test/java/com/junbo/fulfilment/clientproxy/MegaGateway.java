/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.clientproxy;

import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.entitlement.spec.model.Entitlement;

/**
 * MegaGateway.
 */
public interface MegaGateway {
    Long createOffer(Offer offer);

    Long createItem(Item item);

    Long createOfferRevision(OfferRevision offerRevision);

    Long createItemRevision(ItemRevision itemRevision);

    OfferRevision getOfferRevision(Long offerRevisionId);

    ItemRevision getItemRevision(Long itemRevisionId);

    Long updateOfferRevision(OfferRevision offerRevision);

    Long updateItemRevision(ItemRevision itemRevision);

    Entitlement getEntitlement(Long entitlementId);
}
