/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.clientproxy.impl;

import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.catalog.spec.model.offer.OfferRevisionGetOptions;
import com.junbo.catalog.spec.resource.ItemResource;
import com.junbo.catalog.spec.resource.OfferResource;
import com.junbo.catalog.spec.resource.OfferRevisionResource;
import com.junbo.langur.core.promise.SyncModeScope;
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
    public Offer getOffer(String offerId, Long timestamp) {
        return retrieveOffer(offerId, timestamp);
    }

    @Override
    public Offer getOffer(String offerId) {
        return retrieveOffer(offerId, OFFER_TIMESTAMP_NOT_SPECIFIED);
    }

    @Override
    public OfferRevision getOfferRev(String offerRevId) {
        return retrieveOfferRev(offerRevId);
    }

    @Override
    public Item getItem(String itemId) {
        return retrieveItem(itemId);
    }

    protected Offer retrieveOffer(String offerId, Long timestamp) {
        try (SyncModeScope scope = new SyncModeScope()) {
            // TODO
            com.junbo.catalog.spec.model.offer.Offer offer =
                    offerResource.getOffer(offerId).syncGet();

            if (offer == null) {
                throw new ResourceNotFoundException(
                        "Offer [" + offerId + "] with timestamp [" + timestamp + "] does not exist");
            }

            return offer;
        } catch (Exception e) {
            throw new ResourceNotFoundException("Error occurred during calling [Catalog] component service.", e);
        }
    }

    protected OfferRevision retrieveOfferRev(String offerRevId) {
        try (SyncModeScope scope = new SyncModeScope()) {
            // TODO
            OfferRevision offerRev =
                    offerRevResource.getOfferRevision(offerRevId, new OfferRevisionGetOptions()).syncGet();

            if (offerRev == null) {
                throw new ResourceNotFoundException(
                        "Offer Rev [" + offerRevId + "]  does not exist");
            }

            return offerRev;
        } catch (Exception e) {
            throw new ResourceNotFoundException("Error occurred during calling [Catalog] component service.", e);
        }
    }

    protected Item retrieveItem(String itemId) {
        try (SyncModeScope scope = new SyncModeScope()) {
            // TODO
            Item item =
                    itemResource.getItem(itemId).syncGet();

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
