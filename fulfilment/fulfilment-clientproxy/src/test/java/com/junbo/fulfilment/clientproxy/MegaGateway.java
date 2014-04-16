/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.clientproxy;

import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.entitlement.spec.model.Entitlement;

/**
 * MegaGateway.
 */
public interface MegaGateway {
    Long createOffer(Offer offer);

    Long createOfferRevision(OfferRevision offerRevision);

    OfferRevision getOfferRevision(Long offerRevisionId);

    Long updateOfferRevision(OfferRevision offer);

    Entitlement getEntitlement(Long entitlementId);

    Long createEntitlementDef(EntitlementDefinition def);
}
