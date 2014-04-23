/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.clientproxy.impl;

import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.catalog.spec.resource.*;
import com.junbo.common.id.EntitlementId;
import com.junbo.common.id.ItemRevisionId;
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
    private ItemResource itemResource;

    @Autowired
    private ItemRevisionResource itemRevisionResource;

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
    public Long createItem(Item item) {
        try {
            return itemResource.create(item).wrapped().get().getItemId();
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
    public Long createItemRevision(ItemRevision itemRevision) {
        try {
            return itemRevisionResource.createItemRevision(itemRevision).wrapped().get().getRevisionId();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred during calling [Catalog] component service.", e);
        }
    }

    @Override
    public OfferRevision getOfferRevision(Long offerRevisionId) {
        try {
            return offerRevisionResource.getOfferRevision(new OfferRevisionId(offerRevisionId)).wrapped().get();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred during calling [Catalog] component service.", e);
        }
    }

    @Override
    public ItemRevision getItemRevision(Long itemRevisionId) {
        try {
            return itemRevisionResource.getItemRevision(new ItemRevisionId(itemRevisionId)).wrapped().get();
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
    public Long updateItemRevision(ItemRevision itemRevision) {
        try {
            return itemRevisionResource.updateItemRevision(
                    new ItemRevisionId(itemRevision.getRevisionId()),
                    itemRevision).wrapped().get().getRevisionId();
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
