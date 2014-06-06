/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.clientproxy.impl;

import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.catalog.spec.resource.ItemResource;
import com.junbo.catalog.spec.resource.ItemRevisionResource;
import com.junbo.catalog.spec.resource.OfferResource;
import com.junbo.catalog.spec.resource.OfferRevisionResource;
import com.junbo.common.id.EntitlementId;
import com.junbo.common.id.ItemRevisionId;
import com.junbo.common.id.OfferRevisionId;
import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.entitlement.spec.resource.EntitlementResource;
import com.junbo.fulfilment.clientproxy.MegaGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * MegaGatewayImpl.
 */
public class MegaGatewayImpl implements MegaGateway {
    @Autowired
    @Qualifier("offerClient")
    private OfferResource offerResource;

    @Autowired
    @Qualifier("offerRevisionClient")
    private OfferRevisionResource offerRevisionResource;

    @Autowired
    @Qualifier("itemClient")
    private ItemResource itemResource;

    @Autowired
    @Qualifier("itemRevisionClient")
    private ItemRevisionResource itemRevisionResource;

    @Autowired
    @Qualifier("entitlementClient")
    private EntitlementResource entitlementResource;

    @Override
    public Long createOffer(Offer offer) {
        try {
            return offerResource.create(offer).get().getOfferId();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred during calling [Catalog] component service.", e);
        }
    }

    @Override
    public Long createItem(Item item) {
        try {
            return itemResource.create(item).get().getItemId();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred during calling [Catalog] component service.", e);
        }
    }

    @Override
    public Long createOfferRevision(OfferRevision offerRevision) {
        try {
            return offerRevisionResource.createOfferRevision(offerRevision).get().getRevisionId();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred during calling [Catalog] component service.", e);
        }
    }

    @Override
    public Long createItemRevision(ItemRevision itemRevision) {
        try {
            return itemRevisionResource.createItemRevision(itemRevision).get().getRevisionId();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred during calling [Catalog] component service.", e);
        }
    }

    @Override
    public OfferRevision getOfferRevision(Long offerRevisionId) {
        try {
            return offerRevisionResource.getOfferRevision(new OfferRevisionId(offerRevisionId)).get();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred during calling [Catalog] component service.", e);
        }
    }

    @Override
    public ItemRevision getItemRevision(Long itemRevisionId) {
        try {
            return itemRevisionResource.getItemRevision(new ItemRevisionId(itemRevisionId)).get();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred during calling [Catalog] component service.", e);
        }
    }

    @Override
    public Long updateOfferRevision(OfferRevision offerRevision) {
        try {
            return offerRevisionResource.updateOfferRevision(
                    new OfferRevisionId(offerRevision.getRevisionId()),
                    offerRevision).get().getRevisionId();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred during calling [Catalog] component service.", e);
        }
    }

    @Override
    public Long updateItemRevision(ItemRevision itemRevision) {
        try {
            return itemRevisionResource.updateItemRevision(
                    new ItemRevisionId(itemRevision.getRevisionId()),
                    itemRevision).get().getRevisionId();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred during calling [Catalog] component service.", e);
        }
    }

    @Override
    public Entitlement getEntitlement(Long entitlementId) {
        try {
            return entitlementResource.getEntitlement(new EntitlementId(entitlementId)).get();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred during calling [Entitlement] component service.", e);
        }
    }
}
