/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.clientproxy.impl;

import com.junbo.catalog.spec.model.common.EntityGetOptions;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.catalog.spec.resource.ItemResource;
import com.junbo.catalog.spec.resource.OfferResource;
import com.junbo.catalog.spec.resource.OfferRevisionResource;
import com.junbo.common.id.ItemId;
import com.junbo.common.id.OfferId;
import com.junbo.common.id.OfferRevisionId;
import com.junbo.subscription.clientproxy.CatalogGateway;

import com.junbo.subscription.common.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * CatalogGatewayImpl.
 */
public class CatalogGatewayImpl implements CatalogGateway {
    private static final Long OFFER_TIMESTAMP_NOT_SPECIFIED = -1L;

    @Autowired
    @Qualifier("offerClient")
    private OfferResource offerResource;

    @Autowired
    @Qualifier("offerRevClient")
    private OfferRevisionResource offerRevResource;

    @Autowired
    @Qualifier("itemClient")
    private ItemResource itemResource;

    @Override
    public Offer getOffer(Long offerId, Long timestamp) {
        return retrieveOffer(offerId, timestamp);
    }

    @Override
    public Offer getOffer(Long offerId) {
        return retrieveOffer(offerId, OFFER_TIMESTAMP_NOT_SPECIFIED);
    }

    @Override
    public OfferRevision getOfferRev(Long offerRevId) {
        return retrieveOfferRev(offerRevId);
    }

    @Override
    public Item getItem(Long itemId) {
        return retrieveItem(itemId);
    }

    protected Offer retrieveOffer(Long offerId, Long timestamp) {
        try {
            // TODO
            com.junbo.catalog.spec.model.offer.Offer offer =
                    offerResource.getOffer(new OfferId(offerId)).wrapped().get();

            if (offer == null) {
                throw new ResourceNotFoundException(
                        "Offer [" + offerId + "] with timestamp [" + timestamp + "] does not exist");
            }

            return offer;
        } catch (Exception e) {
            throw new ResourceNotFoundException("Error occurred during calling [Catalog] component service.", e);
        }
    }

    protected OfferRevision retrieveOfferRev(Long offerRevId) {
        try {
            // TODO
            OfferRevision offerRev =
                    offerRevResource.getOfferRevision(new OfferRevisionId(offerRevId)).wrapped().get();

            if (offerRev == null) {
                throw new ResourceNotFoundException(
                        "Offer Rev [" + offerRevId + "]  does not exist");
            }

            return offerRev;
        } catch (Exception e) {
            throw new ResourceNotFoundException("Error occurred during calling [Catalog] component service.", e);
        }
    }

    protected Item retrieveItem(Long itemId) {
        try {
            // TODO
            Item item =
                    itemResource.getItem(new ItemId(itemId), EntityGetOptions.getDefault()).wrapped().get();

            if (item == null) {
                throw new ResourceNotFoundException(
                        "Item [" + itemId + "] does not exist");
            }

            return item;
        } catch (Exception e) {
            throw new ResourceNotFoundException("Error occurred during calling [Catalog] component service.", e);
        }
    }
}
