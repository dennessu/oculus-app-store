/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.clientproxy.impl;

import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.catalog.spec.resource.EntitlementDefinitionResource;
import com.junbo.catalog.spec.resource.OfferResource;
import com.junbo.catalog.spec.resource.OfferRevisionResource;
import com.junbo.common.id.EntitlementId;
import com.junbo.common.id.OfferRevisionId;
import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.entitlement.spec.resource.EntitlementResource;
import com.junbo.fulfilment.clientproxy.MegaGateway;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * MegaGatewayImpl.
 */
public class MegaGatewayImpl implements MegaGateway {
    @Autowired
    private OfferResource offerResource;

    @Autowired
    private OfferRevisionResource offerRevisionResource;

    @Autowired
    private EntitlementResource entitlementResource;

    @Autowired
    private EntitlementDefinitionResource entitlementDefResource;

    @Override
    public Long createOffer(Offer offer) {
        try {
            return offerResource.create(offer).wrapped().get().getOfferId();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred during calling [Catalog] component service.", e);
        }
    }

    @Override
    public Long createOfferRevision(OfferRevision offerRevision) {
        try {
            return offerRevisionResource.createOfferRevision(offerRevision).wrapped().get().getRevisionId();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred during calling [Catalog] component service.", e);
        }
    }

    @Override
    public Long updateOfferRevision(OfferRevision offerRevision) {
        try {
            return offerRevisionResource.updateOfferRevision(
                    new OfferRevisionId(offerRevision.getRevisionId()),
                    offerRevision).wrapped().get().getRevisionId();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred during calling [Catalog] component service.", e);
        }
    }

    @Override
    public Entitlement getEntitlement(Long entitlementId) {
        try {
            return entitlementResource.getEntitlement(new EntitlementId(entitlementId)).wrapped().get();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred during calling [Entitlement] component service.", e);
        }
    }

    @Override
    public Long createEntitlementDef(EntitlementDefinition def) {
        try {
            return entitlementDefResource.postEntitlementDefinition(def).wrapped().get().getEntitlementDefId();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred during calling [Catalog] component service.", e);
        }
    }
}
